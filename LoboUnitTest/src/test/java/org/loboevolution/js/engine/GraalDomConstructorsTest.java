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

/**
 * Guards the DOM constructors reachable from JavaScript. GraalJS cannot route a
 * single JS argument to a Java {@code Object[]} constructor parameter, so binding
 * the event impls as raw {@code Class} objects made {@code new Event('click')}
 * throw "no applicable overload found"; {@code Option}/{@code Image} were not
 * bound at all. {@link JsEngineFactory} now binds them as {@code ProxyInstantiable}
 * — these tests fail if that regresses.
 */
class GraalDomConstructorsTest extends LoboWebDriver {

    private WindowImpl window;
    private GraalJsEngine engine;

    @BeforeEach
    void setUp() throws Exception {
        final HTMLDocumentImpl doc;
        try (final WritableLineReader wis = new WritableLineReader(new StringReader("<html><body></body></html>"))) {
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

    private String eval(final String src) {
        return ((Value) engine.eval(src, "<ctor>")).asString();
    }

    @Test
    void eventConstructor() {
        assertEquals("click", eval("new Event('click').type"));
        assertEquals("[object Event]", eval("'' + new Event('click')"));
    }

    @Test
    void eventInitDictIsRead() {
        // The (type, init) dict must reach the impl (a JS object -> Map).
        assertEquals("true", eval("'' + new Event('e', {bubbles: true}).bubbles"));
    }

    @Test
    void mouseKeyboardCustomEventConstructors() {
        assertEquals("mousedown", eval("new MouseEvent('mousedown').type"));
        assertEquals("keydown", eval("new KeyboardEvent('keydown').type"));
        assertEquals("mycustom", eval("new CustomEvent('mycustom').type"));
    }

    @Test
    void optionConstructor() {
        assertEquals("txt", eval("new Option('txt', 'val').text"));
        assertEquals("val", eval("new Option('txt', 'val').value"));
    }

    @Test
    void imageConstructor() {
        assertEquals("[object HTMLImageElement]", eval("'' + new Image()"));
    }

    @Test
    void domParserParsesXml() {
        // parseFromString used to corrupt the source and NPE on a null doc.
        final String parse = "new DOMParser().parseFromString('<root><a>1</a></root>', 'text/xml')";
        assertEquals("root", eval(parse + ".documentElement.nodeName"));
        assertEquals("1", eval("'' + " + parse + ".getElementsByTagName('a').length"));
    }

    @Test
    void observerConstructorsAcceptCallback() {
        // The observers take a callback; their impls declare an Object[] ctor
        // that a raw-Class binding cannot reach from one JS arg, so
        // `new MutationObserver(fn)` threw and aborted the whole observer/
        // DOMTokenList test family. They must construct (and MutationObserver
        // observe/disconnect) without throwing.
        assertEquals("ok", eval(
                "(function(){var o = new MutationObserver(function(){});"
                        + "o.observe(document.body, {attributes:true}); o.disconnect(); return 'ok';})()"));
        assertEquals("ok", eval(
                "(function(){new ResizeObserver(function(){});"
                        + "new IntersectionObserver(function(){}); return 'ok';})()"));
    }
}
