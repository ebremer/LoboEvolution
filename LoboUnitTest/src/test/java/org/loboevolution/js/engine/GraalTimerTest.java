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
import org.loboevolution.http.UserAgentContext;

import java.awt.Dimension;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 7 of the GraalJS migration: setTimeout/setInterval callbacks
 * registered from a GraalJS-side script fire correctly when the timer
 * elapses, dispatching through {@link JsEngineFactory#forDocument} via the
 * {@code CallableTimerTask}.
 */
class GraalTimerTest extends LoboWebDriver {

    private static final String HTML =
            "<html><body><div id='collector'></div></body></html>";

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
        doc.setUserData("lobo.js.engine", engine, null);
    }

    /**
     * The timers run on the AWT EDT. This helper polls the DOM until the
     * given attribute appears, with a timeout, so tests aren't flaky.
     */
    private void awaitAttribute(final String elementId, final String attr, final long timeoutMs) throws InterruptedException {
        final long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            final String value = doc.getElementById(elementId).getAttribute(attr);
            if (value != null && !value.isEmpty()) {
                return;
            }
            TimeUnit.MILLISECONDS.sleep(20);
        }
    }

    @Test
    void setTimeoutFiresCallback() throws InterruptedException {
        engine.eval(
                "setTimeout(function() {" +
                "  document.getElementById('collector').setAttribute('data-fired', 'yes');" +
                "}, 50);",
                "<timeout>");
        awaitAttribute("collector", "data-fired", 1500);
        assertEquals("yes", doc.getElementById("collector").getAttribute("data-fired"));
    }

    @Test
    void setTimeoutZeroDelay() throws InterruptedException {
        engine.eval(
                "setTimeout(function() {" +
                "  document.getElementById('collector').setAttribute('data-zero', 'ok');" +
                "}, 0);",
                "<timeout-zero>");
        awaitAttribute("collector", "data-zero", 1500);
        assertEquals("ok", doc.getElementById("collector").getAttribute("data-zero"));
    }

    @Test
    void setTimeoutNoDelayArg() throws InterruptedException {
        // Single-arg overload — should default delay to 0
        engine.eval(
                "setTimeout(function() {" +
                "  document.getElementById('collector').setAttribute('data-noarg', 'yep');" +
                "});",
                "<timeout-noarg>");
        awaitAttribute("collector", "data-noarg", 1500);
        assertEquals("yep", doc.getElementById("collector").getAttribute("data-noarg"));
    }

    @Test
    void setIntervalFiresMultipleTimes() throws InterruptedException {
        engine.eval(
                "var n = 0;" +
                "var id = setInterval(function() {" +
                "  n++;" +
                "  document.getElementById('collector').setAttribute('data-n', String(n));" +
                "  if (n >= 3) clearInterval(id);" +
                "}, 30);",
                "<interval>");
        // Wait for at least 3 ticks, with margin
        final long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline) {
            final String v = doc.getElementById("collector").getAttribute("data-n");
            if ("3".equals(v)) break;
            TimeUnit.MILLISECONDS.sleep(20);
        }
        assertEquals("3", doc.getElementById("collector").getAttribute("data-n"));
    }

    @Test
    void clearTimeoutCancels() throws InterruptedException {
        engine.eval(
                "var id = setTimeout(function() {" +
                "  document.getElementById('collector').setAttribute('data-clear', 'fired');" +
                "}, 100);" +
                "clearTimeout(id);",
                "<clear>");
        // Sleep past the timer — listener must NOT have run
        TimeUnit.MILLISECONDS.sleep(300);
        final String value = doc.getElementById("collector").getAttribute("data-clear");
        assertTrue(value == null || value.isEmpty(),
                "clearTimeout should cancel the pending callback; got " + value);
    }

    @Test
    void timerReturnsNumericId() {
        // setTimeout returns the timer id; useful from JS for later cancellation
        final var id = engine.eval(
                "setTimeout(function() {}, 1000)",
                "<id>");
        assertNotNull(id);
    }
}
