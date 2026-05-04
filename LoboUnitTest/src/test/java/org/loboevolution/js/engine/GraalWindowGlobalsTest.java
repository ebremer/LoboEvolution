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

package org.loboevolution.js.engine;

import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.loboevolution.config.HtmlRendererConfig;
import org.loboevolution.driver.LoboWebDriver;
import org.loboevolution.gui.HtmlPanel;
import org.loboevolution.gui.HtmlRendererContext;
import org.loboevolution.gui.LocalHtmlRendererConfig;
import org.loboevolution.gui.LocalHtmlRendererContext;
import org.loboevolution.html.dom.domimpl.HTMLDocumentImpl;
import org.loboevolution.html.io.WritableLineReader;
import org.loboevolution.html.js.WindowImpl;
import org.loboevolution.html.js.engine.GraalJsEngine;
import org.loboevolution.html.js.engine.JsEngineFactory;
import org.loboevolution.http.UserAgentContext;

import java.awt.Dimension;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 4 of the GraalJS migration: Window globals are reachable from a
 * GraalJS context populated by
 * {@link JsEngineFactory#bindWindowGlobals(org.loboevolution.html.js.engine.JsEngine,
 * WindowImpl)}.
 *
 * <p>The tests build a real {@link WindowImpl} via the same path the test
 * driver uses, then either bind globals onto a fresh {@link GraalJsEngine}
 * directly or go through {@link JsEngineFactory#forDocument} with the
 * {@code GRAAL} kind. Both routes must let JS reach {@code navigator},
 * {@code performance}, {@code location}, {@code window} and friends.
 */
class GraalWindowGlobalsTest extends LoboWebDriver {

    private HTMLDocumentImpl doc;
    private WindowImpl window;

    @BeforeEach
    void setUp() throws Exception {
        // Build a minimal document mirroring LoboWebDriver.loadHtml so the
        // window is fully wired (renderer context, user-agent context, config).
        final String html = "<html><head><title>graal-window-test</title></head><body></body></html>";
        try (final WritableLineReader wis = new WritableLineReader(new StringReader(html))) {
            final HtmlRendererConfig config = new LocalHtmlRendererConfig();
            final UserAgentContext ucontext = new UserAgentContext(config, true);
            final HtmlPanel panel = new HtmlPanel();
            panel.setPreferredSize(new Dimension(800, 400));
            final HtmlRendererContext rcontext = new LocalHtmlRendererContext(panel, ucontext);
            ucontext.setUserAgentEnabled(true);
            doc = new HTMLDocumentImpl(ucontext, rcontext, config, wis, "about:blank");
            doc.load();
        }
        window = (WindowImpl) doc.getDefaultView();
        assertNotNull(window, "window must exist after document load");
    }

    @Test
    void navigatorUserAgentReachable() {
        try (GraalJsEngine engine = new GraalJsEngine()) {
            JsEngineFactory.bindWindowGlobals(engine, window);
            final Value ua = (Value) engine.eval("navigator.userAgent", "<navigator>");
            assertTrue(ua.isString(), "navigator.userAgent must be a string, got " + ua);
            assertTrue(ua.asString().length() > 0, "navigator.userAgent must be non-empty");
        }
    }

    @Test
    void performanceNowReturnsNumber() {
        try (GraalJsEngine engine = new GraalJsEngine()) {
            JsEngineFactory.bindWindowGlobals(engine, window);
            final Value now = (Value) engine.eval("performance.now()", "<performance>");
            assertTrue(now.isNumber(), "performance.now() must be a number, got " + now);
            assertTrue(now.asDouble() >= 0, "performance.now() must be non-negative");
        }
    }

    @Test
    void windowSelfIdentity() {
        try (GraalJsEngine engine = new GraalJsEngine()) {
            JsEngineFactory.bindWindowGlobals(engine, window);
            // self === window per the WindowOrWorkerGlobalScope mixin
            final Value same = (Value) engine.eval("self === window", "<self>");
            assertTrue(same.asBoolean());
        }
    }

    @Test
    void documentReachable() {
        try (GraalJsEngine engine = new GraalJsEngine()) {
            JsEngineFactory.bindWindowGlobals(engine, window);
            final Value title = (Value) engine.eval("document.title", "<doc>");
            assertEquals("graal-window-test", title.asString());
        }
    }

    @Test
    void arbitraryWindowPropertyWriteSucceedsAndReadsBack() {
        // Phase 11 prep: WindowImpl is a ProxyObject so jQuery-style
        // `window.$ = jQuery` writes succeed AND read back the stored value.
        try (GraalJsEngine engine = new GraalJsEngine()) {
            JsEngineFactory.bindWindowGlobals(engine, window);
            engine.eval("window.$ = 'jquery-stub';", "<jquery-style>");
            final Value back = (Value) engine.eval("window.$", "<read-back>");
            assertEquals("jquery-stub", back.asString());
        }
    }

    @Test
    void strictModeWindowPropertyWriteDoesNotThrow() {
        // jQuery's setup IIFE has "use strict"; the strict-mode TypeError on
        // host-write failure is exactly what aborted scripts before this fix.
        try (GraalJsEngine engine = new GraalJsEngine()) {
            JsEngineFactory.bindWindowGlobals(engine, window);
            engine.eval("'use strict'; window.fooBar = 42;", "<strict>");
            final Value back = (Value) engine.eval("window.fooBar", "<read>");
            assertEquals(42, back.asInt());
        }
    }

    @Test
    void existingWindowGetterStillReachableThroughProxy() {
        // ProxyObject overrides reflection completely; the DynamicMembers
        // delegate must still surface the existing bean getters.
        try (GraalJsEngine engine = new GraalJsEngine()) {
            JsEngineFactory.bindWindowGlobals(engine, window);
            final Value ua = (Value) engine.eval("window.navigator.userAgent", "<reflection>");
            assertTrue(ua.isString() && ua.asString().length() > 0);
        }
    }

    @Test
    void existingWindowMethodStillCallableThroughProxy() {
        try (GraalJsEngine engine = new GraalJsEngine()) {
            JsEngineFactory.bindWindowGlobals(engine, window);
            // setTimeout returns a numeric id — proves the method routed
            // through the ProxyExecutable produced by DynamicMembers.
            final Value id = (Value) engine.eval("window.setTimeout(function(){}, 1000)", "<method>");
            assertTrue(id.isNumber());
        }
    }

    @Test
    void dynamicPropertyShadowsBeanGetter() {
        // If a script overrides a known bean accessor (`window.location = ...`),
        // subsequent reads should see the override rather than the original
        // host value — same behaviour real browsers have for non-built-in
        // identifiers. Built-in non-writable properties are out of scope here.
        try (GraalJsEngine engine = new GraalJsEngine()) {
            JsEngineFactory.bindWindowGlobals(engine, window);
            engine.eval("window.customField = 'first';", "<set1>");
            engine.eval("window.customField = 'second';", "<set2>");
            assertEquals("second", ((Value) engine.eval("window.customField", "<get>")).asString());
        }
    }

    @Test
    void forDocumentInGraalModeBindsGlobals() {
        // Going through the public factory entry-point used by HTMLScriptElementImpl
        // — proves the script-element bridge will see the same globals when the
        // graal flag is selected.
        final var engine = JsEngineFactory.forDocument(doc, window);
        final Value ua = (Value) engine.eval("navigator.userAgent", "<navigator>");
        assertTrue(ua.isString());
        assertTrue(ua.asString().length() > 0);
    }
}
