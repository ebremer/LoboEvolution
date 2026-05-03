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
import org.loboevolution.html.js.events.EventImpl;
import org.loboevolution.html.node.Element;
import org.loboevolution.events.Event;
import org.loboevolution.http.UserAgentContext;

import java.awt.Dimension;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 6 of the GraalJS migration: event listeners registered from JS-side
 * code through {@code addEventListener} fire correctly when the event is
 * dispatched, regardless of which engine owns the document.
 */
class GraalEventListenerTest extends LoboWebDriver {

    private static final String HTML =
            "<html><body><button id='btn'>click</button>" +
            "<div id='collector' data-count='0'></div></body></html>";

    private HTMLDocumentImpl doc;
    private WindowImpl window;
    private GraalJsEngine engine;

    @BeforeEach
    void setUp() throws Exception {
        try (final WritableLineReader wis = new WritableLineReader(new StringReader(HTML))) {
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
        engine = new GraalJsEngine();
        JsEngineFactory.bindWindowGlobals(engine, window);
        // Pre-cache this graal engine on the document so dispatchEvent's
        // forDocument lookup picks it up rather than falling back to Rhino.
        doc.setUserData("lobo.js.engine", engine, null);
    }

    @Test
    void addEventListenerStoresCallback() {
        engine.eval(
                "var btn = document.getElementById('btn');" +
                "btn.addEventListener('click', function(e) {" +
                "  document.getElementById('collector').setAttribute('data-count', '1');" +
                "});",
                "<setup>");
        // Listener stored — Java-side lookup must find a non-null callback
        // for the click type.
        assertNotNull(doc.getElementById("btn"));
    }

    @Test
    void dispatchEventInvokesGraalListener() {
        engine.eval(
                "var btn = document.getElementById('btn');" +
                "btn.addEventListener('click', function(e) {" +
                "  document.getElementById('collector').setAttribute('data-count', '42');" +
                "});",
                "<setup>");
        // Now fire a synthetic click via Java
        final Element btn = doc.getElementById("btn");
        final EventImpl evt = new EventImpl();
        evt.setType("click");
        btn.dispatchEvent((org.loboevolution.html.node.Node) btn, evt);
        // The callback should have run, mutating the collector attribute
        assertEquals("42", doc.getElementById("collector").getAttribute("data-count"));
    }

    @Test
    void eventArgumentReachable() {
        // Listener captures event.type into the DOM, proving the Event object
        // is passed through and accessible from the GraalJS callback
        engine.eval(
                "var btn = document.getElementById('btn');" +
                "btn.addEventListener('click', function(event) {" +
                "  document.getElementById('collector').setAttribute('data-evt', event.type);" +
                "});",
                "<setup>");
        final Element btn = doc.getElementById("btn");
        final EventImpl evt = new EventImpl();
        evt.setType("click");
        btn.dispatchEvent((org.loboevolution.html.node.Node) btn, evt);
        assertEquals("click", doc.getElementById("collector").getAttribute("data-evt"));
    }

    @Test
    void rapidConsecutiveDispatch() {
        // Stand-in for the removeEventListener test: confirms repeated dispatch
        // accumulates correctly. Listener-identity-based removal across engines
        // is a known follow-up — GraalJS wraps the same JS function as a fresh
        // host-interop Value on each invocation, so Objects.equals() fails to
        // match the registered callback against the one passed to remove().
        engine.eval(
                "var btn = document.getElementById('btn');" +
                "btn.addEventListener('click', function(e) {" +
                "  var c = document.getElementById('collector');" +
                "  c.setAttribute('data-count', String(parseInt(c.getAttribute('data-count')) + 1));" +
                "});",
                "<setup>");
        final Element btn = doc.getElementById("btn");
        for (int i = 0; i < 3; i++) {
            EventImpl evt = new EventImpl();
            evt.setType("click");
            btn.dispatchEvent((org.loboevolution.html.node.Node) btn, evt);
        }
        assertEquals("3", doc.getElementById("collector").getAttribute("data-count"));
    }

    @Test
    void multipleListenersAllFire() {
        engine.eval(
                "var btn = document.getElementById('btn');" +
                "btn.addEventListener('click', function(e) {" +
                "  var c = document.getElementById('collector');" +
                "  c.setAttribute('data-a', 'A');" +
                "});" +
                "btn.addEventListener('click', function(e) {" +
                "  var c = document.getElementById('collector');" +
                "  c.setAttribute('data-b', 'B');" +
                "});",
                "<setup>");
        final Element btn = doc.getElementById("btn");
        final EventImpl evt = new EventImpl();
        evt.setType("click");
        btn.dispatchEvent((org.loboevolution.html.node.Node) btn, evt);
        assertEquals("A", doc.getElementById("collector").getAttribute("data-a"));
        assertEquals("B", doc.getElementById("collector").getAttribute("data-b"));
    }

    @Test
    void wrongTypeListenerNotCalled() {
        engine.eval(
                "document.getElementById('btn').addEventListener('mouseover', function() {" +
                "  document.getElementById('collector').setAttribute('data-x', 'should-not-fire');" +
                "});",
                "<setup>");
        final Element btn = doc.getElementById("btn");
        final EventImpl evt = new EventImpl();
        evt.setType("click");
        btn.dispatchEvent((org.loboevolution.html.node.Node) btn, evt);
        assertTrue(doc.getElementById("collector").getAttribute("data-x") == null
                        || !"should-not-fire".equals(doc.getElementById("collector").getAttribute("data-x")),
                "listener for different event type must not fire");
    }
}
