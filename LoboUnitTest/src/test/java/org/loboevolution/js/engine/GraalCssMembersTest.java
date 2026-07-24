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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the CSSOM member semantics implemented by {@code CssMembers}: the
 * {@code undefined} / {@code ""} / numeric-index / read-only distinctions that a
 * real browser makes on an inline {@code element.style}. These are easy to
 * regress from a Java bridge (Java {@code null} → JS {@code null}; every name
 * being "present"), so this test lives in the blocking smoke gate.
 */
class GraalCssMembersTest extends LoboWebDriver {

    private GraalJsEngine engine;

    @BeforeEach
    void setUp() throws Exception {
        final String html = "<html><head>"
                + "<style>div { color: red; } p { margin-top: 1px; }</style>"
                + "</head><body>"
                + "<div id='t'></div>"
                + "<div id='w' style='width: 100%'></div>"
                + "</body></html>";
        HTMLDocumentImpl doc;
        try (WritableLineReader wis = new WritableLineReader(new StringReader(html))) {
            final HtmlRendererConfig config = new LocalHtmlRendererConfig();
            final UserAgentContext ucontext = new UserAgentContext(config, true);
            final HtmlPanel panel = new HtmlPanel();
            panel.setPreferredSize(new Dimension(800, 400));
            final HtmlRendererContext rctx = new LocalHtmlRendererContext(panel, ucontext);
            ucontext.setUserAgentEnabled(true);
            doc = new HTMLDocumentImpl(ucontext, rctx, config, wis, "about:blank");
            doc.load();
        }
        final WindowImpl window = (WindowImpl) doc.getDefaultView();
        engine = new GraalJsEngine();
        JsEngineFactory.bindWindowGlobals(engine, window);
    }

    private Value eval(final String src) {
        return (Value) engine.eval(src, "<css>");
    }

    private Value style(final String id, final String expr) {
        return eval("(function(){var s=document.getElementById('" + id + "').style; return " + expr + ";})()");
    }

    @Test
    void recognizedUnsetPropertyIsEmptyString() {
        // margin has a bean getter that returns null unset; color likewise.
        assertEquals("string", style("t", "typeof s.margin").asString());
        assertEquals("", style("t", "s.margin").asString());
        assertEquals("string", style("t", "typeof s.color").asString());
        assertEquals("", style("t", "s.color").asString());
    }

    @Test
    void getterlessRecognizedPropertyIsEmptyString() {
        // background-clip is in the registry but has no bean accessor here.
        assertEquals("string", style("t", "typeof s.backgroundClip").asString());
        assertEquals("", style("t", "s.backgroundClip").asString());
    }

    @Test
    void unrecognizedNameIsUndefined() {
        // Not a CSS property and not a method: reads as undefined, not null.
        assertEquals("undefined", style("t", "typeof s.foo").asString());
        assertTrue(style("t", "s.bar").isNull());
        assertFalse(eval("'foo' in document.getElementById('t').style").asBoolean());
    }

    @Test
    void recognizedPropertyRoundTripsViaCamelCase() {
        eval("document.getElementById('t').style.backgroundClip = 'border-box';");
        assertEquals("border-box", style("t", "s.backgroundClip").asString());
    }

    @Test
    void numericIndexIsPropertyNameOrUndefined() {
        // #w has exactly one declared property (width).
        assertEquals(1, style("w", "s.length").asInt());
        assertEquals("width", style("w", "s[0]").asString());
        assertTrue(style("w", "s[1]").isNull(), "out-of-range index is undefined");
        assertTrue(style("w", "s[-1]").isNull(), "negative index is undefined");
        assertTrue(eval("0 in document.getElementById('w').style").asBoolean());
        assertFalse(eval("1 in document.getElementById('w').style").asBoolean());
    }

    @Test
    void readOnlyLengthAssignmentThrowsInStrictMode() {
        final Value caught = eval("(function(){'use strict';"
                + "try { document.getElementById('t').style.length = 5; return 'no-throw'; }"
                + "catch (e) { return 'threw'; }})()");
        assertEquals("threw", caught.asString());
        // and no phantom 'length' property was minted
        assertEquals(0, style("t", "s.length").asInt());
    }

    @Test
    void cssRuleListIsIndexedAndIterable() {
        // styleSheets[0].cssRules[0].style is the pervasive stylesheet-rule
        // pattern; cssRules[i] must resolve (was undefined -> threw).
        assertEquals(2, eval("document.styleSheets[0].cssRules.length").asInt());
        assertEquals("object", eval("typeof document.styleSheets[0].cssRules[0]").asString());
        assertTrue(eval("document.styleSheets[0].cssRules[0] "
                + "=== document.styleSheets[0].cssRules.item(0)").asBoolean());
        assertEquals(2, eval(
                "(function(){var n=0;for(var r of document.styleSheets[0].cssRules)n++;return n;})()").asInt());
        assertEquals("red", eval("document.styleSheets[0].cssRules[0].style.color").asString());
    }

    @Test
    void nullAssignmentClearsToEmptyString() {
        // CSSOM [LegacyNullToEmptyString]: assigning null yields "", not "null".
        eval("document.getElementById('t').style.display = 'block';");
        eval("document.getElementById('t').style.display = null;");
        assertEquals("", style("t", "s.display").asString());
    }
}
