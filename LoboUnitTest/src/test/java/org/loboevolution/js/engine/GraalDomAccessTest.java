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
 * Phase 5 of the GraalJS migration: DOM tree access from a script running in
 * a GraalJS context. The fixture uses a small HTML document with a few
 * known-shaped elements; each test exercises a different DOM API and checks
 * the JS-side and Java-side views agree.
 */
class GraalDomAccessTest extends LoboWebDriver {

    private static final String HTML =
            "<html><head><title>dom-test</title></head>" +
            "<body>" +
            "  <div id='root' class='outer'>" +
            "    <p class='child'>first</p>" +
            "    <p class='child'>second</p>" +
            "    <p class='child'>third</p>" +
            "  </div>" +
            "  <span id='other'>x</span>" +
            "</body></html>";

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
    }

    private Value eval(final String src) {
        return (Value) engine.eval(src, "<dom>");
    }

    @Test
    void getElementByIdReturnsElement() {
        final Value el = eval("document.getElementById('root')");
        assertNotNull(el);
        assertTrue(!el.isNull(), "getElementById should not return null for #root");
        // tagName is reflected via getTagName()
        assertEquals("DIV", eval("document.getElementById('root').tagName").asString());
    }

    @Test
    void getElementByIdMissingReturnsNull() {
        final Value el = eval("document.getElementById('does-not-exist')");
        assertTrue(el.isNull(), "missing element must come back as JS null");
    }

    @Test
    void elementInnerHtmlReads() {
        final Value html = eval("document.getElementById('other').innerHTML");
        assertEquals("x", html.asString().trim());
    }

    @Test
    void elementInnerHtmlWrites() {
        eval("document.getElementById('other').innerHTML = 'changed'");
        // Read back via JS
        assertEquals("changed", eval("document.getElementById('other').innerHTML").asString().trim());
        // And via Java side directly
        assertEquals("changed", doc.getElementById("other").getInnerHTML().trim());
    }

    @Test
    void elementChildrenLength() {
        final Value n = eval("document.getElementById('root').children.length");
        assertTrue(n.isNumber(), "children.length must be a number");
        assertEquals(3, n.asInt());
    }

    @Test
    void elementChildrenIndexed() {
        final Value tag = eval("document.getElementById('root').children[0].tagName");
        assertEquals("P", tag.asString());
    }

    @Test
    void elementChildrenForOf() {
        final Value count = eval(
                "let n = 0; for (const c of document.getElementById('root').children) n++; n");
        assertEquals(3, count.asInt());
    }

    @Test
    void getElementsByTagName() {
        final Value n = eval("document.getElementsByTagName('p').length");
        assertEquals(3, n.asInt());
    }

    @Test
    void querySelectorById() {
        final Value tag = eval("document.querySelector('#root').tagName");
        assertEquals("DIV", tag.asString());
    }

    @Test
    void querySelectorAllByClass() {
        final Value n = eval("document.querySelectorAll('.child').length");
        assertEquals(3, n.asInt());
    }

    @Test
    void setAttributeAndGetAttribute() {
        eval("document.getElementById('other').setAttribute('data-x', 'value')");
        assertEquals("value", eval("document.getElementById('other').getAttribute('data-x')").asString());
        // Java side sees it too
        assertEquals("value", doc.getElementById("other").getAttribute("data-x"));
    }

    @Test
    void elementClassNameRoundTrip() {
        assertEquals("outer", eval("document.getElementById('root').className").asString());
        eval("document.getElementById('root').className = 'newclass'");
        assertEquals("newclass", doc.getElementById("root").getClassName());
    }

    @Test
    void createElementAndAppendChild() {
        eval("var d = document.createElement('div');" +
             "d.id = 'created';" +
             "d.innerHTML = 'hello';" +
             "document.body.appendChild(d);");
        assertNotNull(doc.getElementById("created"), "createElement+appendChild should be visible to Java");
        assertEquals("hello", doc.getElementById("created").getInnerHTML().trim());
    }

    @Test
    void removeChild() {
        eval("var p = document.getElementById('root');" +
             "var first = p.children[0];" +
             "p.removeChild(first);");
        assertEquals(2, doc.getElementById("root").getChildren().getLength());
    }

    @Test
    void textContentReadWrite() {
        // Use a freshly created element so we test the bridge, not Lobo's
        // pre-existing replace-vs-append textContent setter quirk.
        eval("var t = document.createElement('span');" +
             "t.id = 'fresh-text';" +
             "t.textContent = 'plain';" +
             "document.body.appendChild(t);");
        assertEquals("plain", doc.getElementById("fresh-text").getTextContent());
        assertEquals("plain", eval("document.getElementById('fresh-text').textContent").asString());
    }

    @Test
    void classListAddContainsRemove() {
        // classList interop: add('foo') should mutate; contains should reflect; remove should clear
        eval("var e = document.getElementById('root');" +
             "e.classList.add('foo');" +
             "e.classList.add('bar');");
        assertTrue(eval("document.getElementById('root').classList.contains('foo')").asBoolean());
        assertTrue(eval("document.getElementById('root').classList.contains('bar')").asBoolean());
        eval("document.getElementById('root').classList.remove('foo')");
        assertTrue(!eval("document.getElementById('root').classList.contains('foo')").asBoolean());
    }

    @Test
    void parentAndSiblingTraversal() {
        final Value tag = eval("document.getElementById('root').children[0].parentNode.tagName");
        assertEquals("DIV", tag.asString());
        final Value sibling = eval("document.getElementById('root').children[0].nextElementSibling.textContent");
        assertEquals("second", sibling.asString().trim());
    }

    @Test
    void documentBodyShortcut() {
        final Value tag = eval("document.body.tagName");
        assertEquals("BODY", tag.asString());
    }

    @Test
    void consoleLogSingleArg() {
        // Lobo's Console.log() takes exactly one arg today (varargs would
        // require an interface change in LoboW3C). Real-world multi-arg calls
        // are a known follow-up that affects both engines, not a Phase-5 issue.
        eval("console.log('hello from graal')");
    }

    @Test
    void createTextNodeAndAppend() {
        eval("var p = document.createElement('p');" +
             "p.id = 'tn-host';" +
             "p.appendChild(document.createTextNode('hi'));" +
             "document.body.appendChild(p);");
        assertEquals("hi", doc.getElementById("tn-host").getTextContent());
    }

    @Test
    void multiLevelChaining() {
        // document.body.children[0].id requires HostAccess to walk through three
        // separately-reflected returns: body (Element), children (HTMLCollection),
        // [0] (Element).
        final Value id = eval("document.body.children[0].id");
        assertEquals("root", id.asString());
    }

    @Test
    void chainedDomMutation() {
        // A moderately realistic mutation chain that combines several operations
        eval("var d = document.createElement('section');" +
             "d.id = 'sec';" +
             "for (let i = 0; i < 5; i++) {" +
             "  var item = document.createElement('span');" +
             "  item.textContent = 'item-' + i;" +
             "  d.appendChild(item);" +
             "}" +
             "document.body.appendChild(d);");
        assertEquals(5, doc.getElementById("sec").getChildren().getLength());
        assertEquals("item-0", doc.getElementById("sec").getChildren().item(0).getTextContent());
        assertEquals("item-4", doc.getElementById("sec").getChildren().item(4).getTextContent());
    }
}
