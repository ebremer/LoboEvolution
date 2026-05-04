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
import org.loboevolution.html.js.engine.CssMembers;
import org.loboevolution.html.js.engine.GraalJsEngine;
import org.loboevolution.html.js.engine.JsEngineFactory;
import org.loboevolution.http.UserAgentContext;

import java.awt.Dimension;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the {@link CssMembers}/{@code ProxyObject} cascade on
 * {@code CSSStyleDeclarationImpl}: bean reflection wins for properties that
 * have explicit setters (so {@code BackgroundImageSetter} etc. still fire),
 * and unknown camelCase property names fall through to
 * {@code setProperty(camelToKebab(name), ...)} so modern CSS works without
 * per-property setters being added.
 */
class GraalCssStyleProxyTest extends LoboWebDriver {

    private HTMLDocumentImpl doc;
    private WindowImpl window;
    private GraalJsEngine engine;

    @BeforeEach
    void setUp() throws Exception {
        final String html = "<html><body><div id='t'></div></body></html>";
        try (WritableLineReader wis = new WritableLineReader(new StringReader(html))) {
            HtmlRendererConfig config = new LocalHtmlRendererConfig();
            UserAgentContext ucontext = new UserAgentContext(config, true);
            HtmlPanel panel = new HtmlPanel();
            panel.setPreferredSize(new Dimension(800, 400));
            HtmlRendererContext rctx = new LocalHtmlRendererContext(panel, ucontext);
            ucontext.setUserAgentEnabled(true);
            doc = new HTMLDocumentImpl(ucontext, rctx, config, wis, "about:blank");
            doc.load();
        }
        window = (WindowImpl) doc.getDefaultView();
        engine = new GraalJsEngine();
        JsEngineFactory.bindWindowGlobals(engine, window);
    }

    private Value eval(final String src) {
        return (Value) engine.eval(src, "<css>");
    }

    @Test
    void unknownCamelCasePropertyRoundTripsViaCssStore() {
        // backgroundClip has no explicit setter — must route through
        // setProperty('background-clip', ...) and read back via the same.
        eval("document.getElementById('t').style.backgroundClip = 'border-box';");
        final Value back = eval("document.getElementById('t').style.backgroundClip");
        assertEquals("border-box", back.asString());
    }

    @Test
    void anotherUnknownCssPropertyRoundTrips() {
        // boxShadow: another property script-set in real-world pages.
        // Lobo's CSS parser canonicalises the value (adds spaces after commas);
        // we just need the assignment to round-trip without throwing, with the
        // original tokens preserved.
        eval("document.getElementById('t').style.boxShadow = '5px 10px blue';");
        final Value back = eval("document.getElementById('t').style.boxShadow");
        assertTrue(back.asString().contains("5px"));
        assertTrue(back.asString().contains("10px"));
        assertTrue(back.asString().contains("blue"));
    }

    @Test
    void existingExplicitSetterStillFires() {
        // backgroundColor HAS setBackgroundColor — reflection should win
        // (so any side effects like informLookInvalid happen).
        eval("document.getElementById('t').style.backgroundColor = 'red';");
        final Value back = eval("document.getElementById('t').style.backgroundColor");
        assertEquals("red", back.asString());
    }

    @Test
    void strictModeWriteToUnknownPropertyDoesNotThrow() {
        // The exact pattern that was crashing in jQuery / Drupal pages.
        // Lobo's CSS parser may normalise case; assert only that the write
        // succeeds without strict-mode TypeError and the value round-trips
        // (case-insensitively).
        eval("'use strict';" +
             "document.getElementById('t').style.transform = 'translatex(10px)';");
        final Value back = eval("document.getElementById('t').style.transform");
        assertEquals("translatex(10px)", back.asString().toLowerCase());
    }

    @Test
    void existingMethodCallableViaProxy() {
        // setProperty is a real method on CSSStyleDeclarationImpl — must
        // remain callable through the ProxyExecutable path.
        eval("document.getElementById('t').style.setProperty('color', 'blue');");
        final Value back = eval("document.getElementById('t').style.color");
        assertEquals("blue", back.asString());
    }

    @Test
    void getPropertyValueCallableViaProxy() {
        eval("document.getElementById('t').style.setProperty('font-weight', 'bold');");
        final Value back = eval("document.getElementById('t').style.getPropertyValue('font-weight')");
        assertEquals("bold", back.asString());
    }

    @Test
    void unknownPropertyReadReturnsEmptyString() {
        // Real browsers return "" (not undefined) for unset CSS properties
        final Value back = eval("document.getElementById('t').style.gridTemplateColumns");
        assertNotNull(back);
        assertTrue(back.isString() || back.isNull());
        if (back.isString()) {
            assertEquals("", back.asString());
        }
    }

    @Test
    void camelToKebabConversionMatchesSpec() {
        // White-box check on the conversion since it underlies everything
        assertEquals("background-clip", CssMembers.camelToKebab("backgroundClip"));
        assertEquals("box-shadow", CssMembers.camelToKebab("boxShadow"));
        assertEquals("color", CssMembers.camelToKebab("color"));
        assertEquals("border-top-left-radius", CssMembers.camelToKebab("borderTopLeftRadius"));
    }
}
