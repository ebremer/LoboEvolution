/*
 * MIT License
 *
 * Copyright (c) 2014 - 2025 LoboEvolution
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Contact info: ivan.difrancesco@yahoo.it
 */
package org.loboevolution.html.js.engine;

import org.loboevolution.html.js.Executor;
import org.loboevolution.html.js.WindowImpl;
import org.loboevolution.html.node.Document;
import org.mozilla.javascript.Scriptable;

/**
 * Selects the active {@link JsEngine} implementation. The choice is driven by
 * the system property {@value #PROPERTY}, with values {@code rhino} (default)
 * or {@code graal}. A bad value falls back to Rhino so that misconfiguration
 * cannot regress production behaviour.
 *
 * <p>Until Phase 4 wires DOM globals through this abstraction, the GraalJS
 * branch only evaluates standalone scripts; selecting it for a real page will
 * produce {@code ReferenceError}s for DOM lookups. That is by design.
 */
public final class JsEngineFactory {

    public static final String PROPERTY = "lobo.jsengine";

    /** UserData key under which a document's cached {@link JsEngine} lives. */
    static final String DOCUMENT_ENGINE_KEY = "lobo.js.engine";

    public enum Kind {
        RHINO,
        GRAAL;

        static Kind parse(final String raw) {
            if (raw == null) {
                return RHINO;
            }
            return switch (raw.trim().toLowerCase()) {
                case "graal", "graaljs", "graalvm" -> GRAAL;
                default -> RHINO;
            };
        }
    }

    /** Returns the engine selected by {@value #PROPERTY}, defaulting to Rhino. */
    public static Kind defaultKind() {
        return Kind.parse(System.getProperty(PROPERTY));
    }

    /** Builds a fresh {@link JsEngine} of the default kind. */
    public static JsEngine create() {
        return create(defaultKind());
    }

    /** Builds a fresh {@link JsEngine} of the requested kind. */
    public static JsEngine create(final Kind kind) {
        return switch (kind) {
            case GRAAL -> new GraalJsEngine();
            case RHINO -> new RhinoJsEngine();
        };
    }

    /**
     * Returns the {@link JsEngine} bound to the given document, creating and
     * caching one on first use. Multiple {@code <script>} elements in the same
     * document share the engine so they see each other's globals — exactly as
     * the legacy Rhino-only path did.
     *
     * <p>In Rhino mode the engine wraps the per-document scope already built
     * by {@code WindowImpl.initWindowScope}, so DOM access continues to work.
     * In GraalJS mode the engine is a fresh polyglot {@code Context} into
     * which {@link #bindWindowGlobals(JsEngine, WindowImpl)} populates the
     * core Window members ({@code window}, {@code document}, {@code navigator},
     * {@code location}, {@code console}, {@code performance}, {@code history},
     * {@code screen}, {@code localStorage}, {@code sessionStorage}).
     *
     * <p>Callers must <strong>not</strong> {@code close()} the returned engine;
     * its lifecycle is owned by the document.
     */
    public static JsEngine forDocument(final Document doc, final WindowImpl window) {
        return forDocument(doc, window, defaultKind());
    }

    /** {@link #forDocument(Document, WindowImpl)} with an explicit engine choice. */
    public static JsEngine forDocument(final Document doc, final WindowImpl window, final Kind kind) {
        final Object cached = doc.getUserData(DOCUMENT_ENGINE_KEY);
        if (cached instanceof JsEngine eng) {
            return eng;
        }
        final JsEngine engine;
        switch (kind) {
            case GRAAL -> {
                final GraalJsEngine ge = new GraalJsEngine();
                bindWindowGlobals(ge, window);
                engine = ge;
            }
            case RHINO -> {
                final Scriptable scope = (Scriptable) doc.getUserData(Executor.SCOPE_KEY);
                if (scope == null) {
                    throw new IllegalStateException(
                            "Document scope not initialised; expected UserData key " + Executor.SCOPE_KEY);
                }
                engine = new RhinoJsEngine(window.getContextFactory(), scope);
            }
            default -> throw new IllegalStateException("unknown engine kind");
        }
        doc.setUserData(DOCUMENT_ENGINE_KEY, engine, null);
        return engine;
    }

    /**
     * Populates the core Window globals on {@code engine}. Each binding is the
     * Java-side getter result; GraalJS exposes them via the
     * {@link LoboGraalHostAccess} reflection rules so {@code navigator.userAgent},
     * {@code window.location.href}, {@code performance.now()} etc. resolve to
     * the underlying Java methods.
     *
     * <p>This is intentionally separate from {@link #forDocument} so it can be
     * exercised in unit tests without the Rhino-side scope prerequisite.
     */
    public static void bindWindowGlobals(final JsEngine engine, final WindowImpl window) {
        engine.putGlobal("window", window);
        engine.putGlobal("self", window);
        engine.putGlobal("document", window.getDocumentNode());
        engine.putGlobal("navigator", window.getNavigator());
        engine.putGlobal("location", window.getLocation());
        engine.putGlobal("console", window.getConsole());
        engine.putGlobal("performance", window.getPerformance());
        engine.putGlobal("history", window.getHistory());
        engine.putGlobal("screen", window.getScreen());
        engine.putGlobal("localStorage", window.getLocalStorage());
        engine.putGlobal("sessionStorage", window.getSessionStorage());
        // In a real browser these are callable both as window.foo() and bare
        // foo() because window IS the global. Re-export them as globals so
        // legacy scripts work without the window. prefix.
        engine.eval(WINDOW_FUNCTION_BRIDGE, "<window-fn-bridge>");
    }

    private static final String WINDOW_FUNCTION_BRIDGE = String.join("\n",
            "var setTimeout = function(fn, ms) {",
            "  return arguments.length < 2 ? window.setTimeout(fn) : window.setTimeout(fn, ms);",
            "};",
            "var setInterval = function(fn, ms) { return window.setInterval(fn, ms); };",
            "var clearTimeout = function(id) { window.clearTimeout(id); };",
            "var clearInterval = function(id) { window.clearInterval(id); };",
            "var alert = function(msg) { window.alert(msg); };",
            "");

    private JsEngineFactory() {
    }
}
