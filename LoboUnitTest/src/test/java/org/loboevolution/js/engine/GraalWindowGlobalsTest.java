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
    void forDocumentInGraalModeBindsGlobals() {
        // Going through the public factory entry-point used by HTMLScriptElementImpl
        // — proves the script-element bridge will see the same globals when the
        // graal flag is selected.
        final var engine = JsEngineFactory.forDocument(doc, window, JsEngineFactory.Kind.GRAAL);
        final Value ua = (Value) engine.eval("navigator.userAgent", "<navigator>");
        assertTrue(ua.isString());
        assertTrue(ua.asString().length() > 0);
    }
}
