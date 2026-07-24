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

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyInstantiable;
import org.loboevolution.html.dom.HTMLImageElement;
import org.loboevolution.html.dom.HTMLOptionElement;
import org.loboevolution.html.js.WindowImpl;
import org.loboevolution.html.js.events.*;
import org.loboevolution.html.node.Document;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

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

        // The observers take a callback function; their impls declare an Object[]
        // constructor, which — bound as a raw Class — GraalJS cannot reach from a
        // single JS argument ("no applicable overload found"), so `new
        // MutationObserver(fn)` threw and aborted every script using one (the
        // whole DOMTokenList/observer test family). Bind as ProxyInstantiable so
        // the JS args reach the Object[] constructor (same fix as the DOM events).
        // Tests do not use `instanceof <Observer>`, so binding the Class is not
        // needed.
        engine.putGlobal("MutationObserver", (ProxyInstantiable) args ->
                new org.loboevolution.html.js.observer.MutationObserverImpl(toHostArgs(args)));
        engine.putGlobal("IntersectionObserver", (ProxyInstantiable) args ->
                new org.loboevolution.html.js.observer.IntersectionObserverImpl(toHostArgs(args)));
        engine.putGlobal("ResizeObserver", (ProxyInstantiable) args ->
                new org.loboevolution.html.js.observer.ResizeObserverImpl(toHostArgs(args)));

        final org.loboevolution.html.dom.domimpl.HTMLDocumentImpl doc =
                (org.loboevolution.html.dom.domimpl.HTMLDocumentImpl) window.getDocumentNode();
        engine.putGlobal("DOMParser", (ProxyInstantiable) args ->
                new org.loboevolution.html.js.DOMParserImpl(doc));
        engine.putGlobal("FormData", (ProxyInstantiable) args ->
                new org.loboevolution.html.dom.nodeimpl.FormDataImpl(doc));
        engine.putGlobal("XMLHttpRequest", (ProxyInstantiable) args ->
                new org.loboevolution.html.js.xml.XMLHttpRequestImpl(doc, window));

        // DOM event constructors. Bound as ProxyInstantiable (not a raw Class)
        // so `new Event('click', {bubbles:true})` reaches the impl's Object[]
        // constructor: GraalJS cannot route a single JS argument to a Java
        // Object[] parameter, so binding the Class made `new X(...)` throw
        // "no applicable overload found". Tests never use `instanceof <Event>`,
        // so nothing is lost by not binding the Class.
        bindEvent(engine, "Event", EventImpl::new);
        bindEvent(engine, "UIEvent", UIEventImpl::new);
        bindEvent(engine, "MouseEvent", MouseEventImpl::new);
        bindEvent(engine, "KeyboardEvent", KeyboardEventImpl::new);
        bindEvent(engine, "CustomEvent", CustomEventImpl::new);
        bindEvent(engine, "FocusEvent", FocusEventImpl::new);
        bindEvent(engine, "PointerEvent", PointerEventImpl::new);
        bindEvent(engine, "WheelEvent", WheelEventImpl::new);
        bindEvent(engine, "InputEvent", InputEventImpl::new);
        bindEvent(engine, "CompositionEvent", CompositionEventImpl::new);
        bindEvent(engine, "DragEvent", DragEventImpl::new);
        bindEvent(engine, "TouchEvent", TouchEventImpl::new);
        bindEvent(engine, "MessageEvent", MessageEventImpl::new);
        bindEvent(engine, "StorageEvent", StorageEventImpl::new);
        bindEvent(engine, "ProgressEvent", ProgressEventImpl::new);
        bindEvent(engine, "SubmitEvent", SubmitEventImpl::new);
        bindEvent(engine, "HashChangeEvent", HashChangeEventImpl::new);
        bindEvent(engine, "PopStateEvent", PopStateEventImpl::new);
        bindEvent(engine, "PageTransitionEvent", PageTransitionEventImpl::new);
        bindEvent(engine, "CloseEvent", CloseEventImpl::new);
        bindEvent(engine, "AnimationEvent", AnimationEventImpl::new);
        bindEvent(engine, "TransitionEvent", TransitionEventImpl::new);
        bindEvent(engine, "ErrorEvent", ErrorEventImpl::new);
        bindEvent(engine, "MutationEvent", MutationEventImpl::new);
        bindEvent(engine, "TrackEvent", TrackEventImpl::new);
        bindEvent(engine, "BeforeUnloadEvent", BeforeUnloadEventImpl::new);
        bindEvent(engine, "BlobEvent", BlobEventImpl::new);
        bindEvent(engine, "DeviceMotionEvent", DeviceMotionEventImpl::new);
        bindEvent(engine, "DeviceOrientationEvent", DeviceOrientationEventImpl::new);

        // Element constructors that build through the document.
        engine.putGlobal("Option", (ProxyInstantiable) args -> {
            final HTMLOptionElement o = (HTMLOptionElement) doc.createElement("option");
            if (args.length > 0 && !args[0].isNull()) o.setText(stringOf(args[0]));
            if (args.length > 1 && !args[1].isNull()) o.setValue(stringOf(args[1]));
            if (args.length > 2 && !args[2].isNull()) o.setDefaultSelected(boolOf(args[2]));
            if (args.length > 3 && !args[3].isNull()) o.setSelected(boolOf(args[3]));
            return o;
        });
        engine.putGlobal("Image", (ProxyInstantiable) args -> {
            final HTMLImageElement img = (HTMLImageElement) doc.createElement("img");
            if (args.length > 0 && args[0].isNumber()) img.setWidth(args[0].asDouble());
            if (args.length > 1 && args[1].isNumber()) img.setHeight(args[1].asDouble());
            return img;
        });
    }

    /** A DOM event impl's {@code (Object[])} constructor; may throw checked exceptions. */
    @FunctionalInterface
    private interface EventCtor {
        Object make(Object[] params) throws Exception;
    }

    /**
     * Binds {@code name} as a JS constructor that builds the event impl from the
     * JS arguments. The polyglot values are converted to host values first
     * (a JS init dict becomes a {@link Map} so {@code EventImpl.setParams} reads
     * its properties).
     */
    private static void bindEvent(final JsEngine engine, final String name, final EventCtor factory) {
        engine.putGlobal(name, (ProxyInstantiable) args -> {
            try {
                return factory.make(toHostArgs(args));
            } catch (final RuntimeException re) {
                throw re;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static Object[] toHostArgs(final Value[] args) {
        final Object[] out = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            out[i] = toHost(args[i]);
        }
        return out;
    }

    /** Converts a polyglot value to a plain host value; JS objects -> Map, arrays -> List. */
    private static Object toHost(final Value v) {
        if (v == null || v.isNull()) return null;
        if (v.isHostObject()) return v.asHostObject();
        if (v.isString()) return v.asString();
        if (v.isBoolean()) return v.asBoolean();
        if (v.isNumber()) return v.asDouble();
        if (v.hasArrayElements()) {
            final int n = (int) v.getArraySize();
            final Object[] arr = new Object[n];
            for (int i = 0; i < n; i++) arr[i] = toHost(v.getArrayElement(i));
            return Arrays.asList(arr);
        }
        if (v.hasMembers()) {
            final Map<String, Object> m = new LinkedHashMap<>();
            for (final String k : v.getMemberKeys()) m.put(k, toHost(v.getMember(k)));
            return m;
        }
        return v;
    }

    private static String stringOf(final Value v) {
        return v.isString() ? v.asString() : v.toString();
    }

    private static boolean boolOf(final Value v) {
        if (v.isBoolean()) return v.asBoolean();
        if (v.isNumber()) return v.asDouble() != 0;
        return !v.isNull();
    }

    private static final String WINDOW_FUNCTION_BRIDGE = String.join("\n",
            "var setTimeout = function(fn, ms) {",
            "  return arguments.length < 2 ? window.setTimeout(fn) : window.setTimeout(fn, ms);",
            "};",
            "var setInterval = function(fn, ms) { return window.setInterval(fn, ms); };",
            "var clearTimeout = function(id) { window.clearTimeout(id); };",
            "var clearInterval = function(id) { window.clearInterval(id); };",
            "var alert = function(msg) { window.alert(msg); };",
            // Nashorn-compat calls the global object's __noSuchProperty__ when a
            // bare identifier is unbound. In a browser `window` IS the global,
            // so first fall through to window members: this makes bare calls
            // like getComputedStyle(el), matchMedia(q), getSelection() and
            // atob(s) work without the `window.` prefix. Then fall back to
            // element-id auto-globals (an element with id='X' is exposed as X).
            "Object.defineProperty(this, '__noSuchProperty__', {",
            "  configurable: true,",
            "  value: function(name) {",
            "    if (name in window) return window[name];",
            "    var el = document.getElementById(name);",
            "    return el == null ? undefined : el;",
            "  }",
            "});",
            "");

    private JsEngineFactory() {
    }
}
