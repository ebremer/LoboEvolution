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
 * Guards that {@code window} members are callable as bare globals (in a browser
 * {@code window} IS the global object), plus the invariant that {@code typeof}
 * of an undeclared identifier stays {@code "undefined"}.
 *
 * <p>The bare-global support comes from a {@code __noSuchProperty__} hook that
 * falls through to {@code window[name]}. That hook must return {@code undefined}
 * for truly-unknown names, NOT throw: it is invoked during identifier resolution
 * including inside {@code typeof}, so throwing would make {@code typeof
 * undeclaredVar} throw instead of yielding {@code "undefined"} — breaking every
 * {@code typeof x !== 'undefined'} feature check. This test pins that down.
 */
class GraalGlobalFunctionsTest extends LoboWebDriver {

    private GraalJsEngine engine;

    @BeforeEach
    void setUp() throws Exception {
        final HTMLDocumentImpl doc;
        try (final WritableLineReader wis =
                     new WritableLineReader(new StringReader("<html><body><div id='root'></div></body></html>"))) {
            final HtmlRendererConfig config = new LocalHtmlRendererConfig();
            final UserAgentContext ucontext = new UserAgentContext(config, true);
            final HtmlPanel panel = new HtmlPanel();
            panel.setPreferredSize(new Dimension(800, 400));
            final HtmlRendererContext rcontext = new LocalHtmlRendererContext(panel, ucontext);
            ucontext.setUserAgentEnabled(true);
            doc = new HTMLDocumentImpl(ucontext, rcontext, config, wis, "about:blank");
            doc.load();
        }
        final WindowImpl window = (WindowImpl) doc.getDefaultView();
        engine = new GraalJsEngine();
        JsEngineFactory.bindWindowGlobals(engine, window);
    }

    private String eval(final String src) {
        return ((Value) engine.eval(src, "<globals>")).toString();
    }

    @Test
    void bareWindowFunctionsAreCallable() {
        assertEquals("function", eval("typeof getComputedStyle"));
        assertEquals("hi", eval("atob('aGk=')"));
        assertEquals("aGk=", eval("btoa('hi')"));
    }

    @Test
    void bareGetComputedStyleWorks() {
        assertEquals("block", eval("'' + getComputedStyle(document.getElementById('root')).display"));
    }

    @Test
    void typeofUndeclaredIdentifierIsUndefined() {
        // Must not throw: the __noSuchProperty__ fall-through returns undefined
        // for unknown names rather than raising a ReferenceError.
        assertEquals("undefined", eval("typeof someNameThatDoesNotExist"));
    }
}
