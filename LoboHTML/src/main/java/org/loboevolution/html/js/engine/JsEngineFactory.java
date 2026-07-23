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

import org.loboevolution.html.js.WindowImpl;
import org.loboevolution.html.node.Document;

/**
 * Builds {@link JsEngine} instances bound to a Lobo {@link Document}. Always
 * GraalJS — Phase 12 of the migration removed the Rhino runtime engine, so
 * this is now a thin factory rather than a true selector. No Rhino classes
 * remain on the classpath and nothing here reads a JS-engine selector; the
 * migration is complete.
 */
public final class JsEngineFactory {

    /** UserData key under which a document's cached {@link JsEngine} lives. */
    static final String DOCUMENT_ENGINE_KEY = "lobo.js.engine";

    /** Builds a fresh, document-less {@link JsEngine}. */
    public static JsEngine create() {
        return new GraalJsEngine();
    }

    /**
     * Returns the {@link JsEngine} bound to the given document, creating and
     * caching one on first use. Multiple {@code <script>} elements in the same
     * document share the engine so they see each other's globals.
     *
     * <p>{@link #bindWindowGlobals(JsEngine, WindowImpl)} populates the core
     * Window members ({@code window}, {@code document}, {@code navigator},
     * {@code location}, {@code console}, {@code performance}, {@code history},
     * {@code screen}, {@code localStorage}, {@code sessionStorage}).
     *
     * <p>Callers must <strong>not</strong> {@code close()} the returned engine;
     * its lifecycle is owned by the document.
     */
    public static JsEngine forDocument(final Document doc, final WindowImpl window) {
        final Object cached = doc.getUserData(DOCUMENT_ENGINE_KEY);
        if (cached instanceof JsEngine eng) {
            return eng;
        }
        final GraalJsEngine engine = new GraalJsEngine();
        bindWindowGlobals(engine, window);
        doc.setUserData(DOCUMENT_ENGINE_KEY, engine, null);
        return engine;
    }

    /**
     * Populates the core Window globals on {@code engine}. Each binding is the
     * Java-side getter result; GraalJS exposes them via the
     * {@link LoboGraalHostAccess} reflection rules so {@code navigator.userAgent},
     * {@code window.location.href}, {@code performance.now()} etc. resolve to
     * the underlying Java methods.
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
        bindDomTypes(engine, window);
        // In a real browser these are callable both as window.foo() and bare
        // foo() because window IS the global. Re-export them as globals so
        // legacy scripts work without the window. prefix.
        engine.eval(WINDOW_FUNCTION_BRIDGE, "<window-fn-bridge>");
    }

    /**
     * Binds DOM constructor/type identifiers as JS globals so scripts can do
     * {@code instanceof Element} or {@code new XMLHttpRequest()}. GraalJS
     * treats a bound {@link Class} as both a host type ({@code instanceof})
     * and, where the class has a no-arg constructor, a callable
     * ({@code new}). For types whose constructor needs context that lives on
     * the {@link WindowImpl} (e.g. XHR's owning document), a
     * {@code ProxyInstantiable} closes over the right reference.
     */
    private static void bindDomTypes(final JsEngine engine, final WindowImpl window) {
        // No-arg / pure-instanceof types: bind the Class directly so JS can
        // both `instanceof X` and (where there's a no-arg ctor) `new X()`.
        engine.putGlobal("Element", org.loboevolution.html.dom.nodeimpl.ElementImpl.class);
        engine.putGlobal("Node", org.loboevolution.html.dom.nodeimpl.NodeImpl.class);
        engine.putGlobal("HTMLElement", org.loboevolution.html.dom.domimpl.HTMLElementImpl.class);
        engine.putGlobal("HTMLDivElement", org.loboevolution.html.dom.domimpl.HTMLDivElementImpl.class);
        engine.putGlobal("HTMLAnchorElement", org.loboevolution.html.dom.domimpl.HTMLAnchorElementImpl.class);
        engine.putGlobal("HTMLBodyElement", org.loboevolution.html.dom.domimpl.HTMLBodyElementImpl.class);
        engine.putGlobal("HTMLButtonElement", org.loboevolution.html.dom.domimpl.HTMLButtonElementImpl.class);
        engine.putGlobal("HTMLFormElement", org.loboevolution.html.dom.domimpl.HTMLFormElementImpl.class);
        engine.putGlobal("HTMLImageElement", org.loboevolution.html.dom.domimpl.HTMLImageElementImpl.class);
        engine.putGlobal("HTMLInputElement", org.loboevolution.html.dom.domimpl.HTMLInputElementImpl.class);
        engine.putGlobal("HTMLLinkElement", org.loboevolution.html.dom.domimpl.HTMLLinkElementImpl.class);
        engine.putGlobal("HTMLScriptElement", org.loboevolution.html.dom.domimpl.HTMLScriptElementImpl.class);
        engine.putGlobal("HTMLSpanElement", org.loboevolution.html.dom.domimpl.HTMLSpanElementImpl.class);
        engine.putGlobal("HTMLTableElement", org.loboevolution.html.dom.domimpl.HTMLTableElementImpl.class);
        engine.putGlobal("Text", org.loboevolution.html.dom.nodeimpl.TextImpl.class);
        engine.putGlobal("Event", org.loboevolution.html.js.events.EventImpl.class);
        engine.putGlobal("MouseEvent", org.loboevolution.html.js.events.MouseEventImpl.class);
        engine.putGlobal("KeyboardEvent", org.loboevolution.html.js.events.KeyboardEventImpl.class);
        engine.putGlobal("CustomEvent", org.loboevolution.html.js.events.CustomEventImpl.class);
        engine.putGlobal("MutationObserver", org.loboevolution.html.js.observer.MutationObserverImpl.class);
        engine.putGlobal("IntersectionObserver", org.loboevolution.html.js.observer.IntersectionObserverImpl.class);
        engine.putGlobal("ResizeObserver", org.loboevolution.html.js.observer.ResizeObserverImpl.class);

        final org.loboevolution.html.dom.domimpl.HTMLDocumentImpl doc =
                (org.loboevolution.html.dom.domimpl.HTMLDocumentImpl) window.getDocumentNode();
        engine.putGlobal("DOMParser", (org.graalvm.polyglot.proxy.ProxyInstantiable) args ->
                new org.loboevolution.html.js.DOMParserImpl(doc));
        engine.putGlobal("FormData", (org.graalvm.polyglot.proxy.ProxyInstantiable) args ->
                new org.loboevolution.html.dom.nodeimpl.FormDataImpl(doc));
        engine.putGlobal("XMLHttpRequest", (org.graalvm.polyglot.proxy.ProxyInstantiable) args ->
                new org.loboevolution.html.js.xml.XMLHttpRequestImpl(doc, window));
    }

    private static final String WINDOW_FUNCTION_BRIDGE = String.join("\n",
            "var setTimeout = function(fn, ms) {",
            "  return arguments.length < 2 ? window.setTimeout(fn) : window.setTimeout(fn, ms);",
            "};",
            "var setInterval = function(fn, ms) { return window.setInterval(fn, ms); };",
            "var clearTimeout = function(id) { window.clearTimeout(id); };",
            "var clearInterval = function(id) { window.clearInterval(id); };",
            "var alert = function(msg) { window.alert(msg); };",
            // Element-id auto-globals: real browsers expose any element with
            // id='X' as window.X. Nashorn-compat lets us hook the global
            // object's __noSuchProperty__ to fall back to getElementById when
            // a bare identifier is unbound.
            "Object.defineProperty(this, '__noSuchProperty__', {",
            "  configurable: true,",
            "  value: function(name) {",
            "    var el = document.getElementById(name);",
            "    return el == null ? undefined : el;",
            "  }",
            "});",
            "");

    private JsEngineFactory() {
    }
}
