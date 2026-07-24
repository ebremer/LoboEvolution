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
 * Guards the browser-correct named/indexed access on {@code HTMLCollection}
 * ({@code HTMLCollectionImpl}) and {@code HTMLFormElement}
 * ({@code HTMLFormElementImpl}) — the polyglot-proxy bridge that lets
 * {@code document.forms.myForm.myControl} resolve. Easy to regress back to
 * {@code undefined} (bean reflection misses name keys), so it lives in the
 * blocking smoke gate.
 *
 * <p>Known limitation (see {@code HTMLFormElementImpl}): because a form is a
 * polyglot {@code ProxyObject}, it cannot be passed <em>as an argument</em> to a
 * host method (e.g. {@code document.body.appendChild(aForm)}) — GraalJS will not
 * unwrap a proxy back to its host type. Form-as-receiver
 * ({@code form.appendChild(child)}) is unaffected.
 */
class GraalFormMembersTest extends LoboWebDriver {

    private GraalJsEngine engine;

    @BeforeEach
    void setUp() throws Exception {
        final String html = "<html><body>"
                + "<form name='testForm' id='formId'>"
                + "<input name='field1' id='f1' value='v1'>"
                + "<select name='select1'>"
                + "<option value='o1'>One</option>"
                + "<option value='o2' selected>Two</option>"
                + "<option value='o3'>Three</option>"
                + "</select>"
                + "</form></body></html>";
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
        return (Value) engine.eval("(function(){" + src + "})()", "<form>");
    }

    @Test
    void collectionNamedAccessResolvesForm() {
        assertEquals("object", eval("return typeof document.forms.testForm;").asString());
        assertTrue(eval("return document.forms.testForm === document.forms[0];").asBoolean());
        // named by id too
        assertTrue(eval("return document.forms.formId === document.forms[0];").asBoolean());
        assertEquals(1, eval("return document.forms.length;").asInt());
    }

    @Test
    void formNamedControlResolvesByNameAndId() {
        assertEquals("v1", eval("return document.forms.testForm.field1.value;").asString());
        // control referenced by id attribute
        assertEquals("v1", eval("return document.forms.testForm.f1.value;").asString());
        // the select, reached through the form, has its real options
        assertEquals(3, eval("return document.forms.testForm.select1.options.length;").asInt());
        assertEquals("Two", eval("return document.forms.testForm.select1.options[1].text;").asString());
    }

    @Test
    void formIndexedAndIterableControls() {
        assertEquals("field1", eval("return document.forms[0][0].name;").asString());
        final Value names = eval(
                "var n=[]; for (var c of document.forms[0]) n.push(c.name); return n.join(',');");
        assertTrue(names.asString().contains("field1"), "for..of should enumerate controls: " + names.asString());
        assertTrue(names.asString().contains("select1"));
    }

    @Test
    void formHidesNonBrowserItemAndNamedItem() {
        // A browser HTMLFormElement has no item()/namedItem(); they must read undefined.
        assertEquals("undefined", eval("return typeof document.forms[0].item;").asString());
        assertEquals("undefined", eval("return typeof document.forms[0].namedItem;").asString());
    }

    @Test
    void formKeepsHostTypeIdentity() {
        // Making the form a ProxyObject must not break instanceof against the
        // bound DOM constructors.
        assertTrue(eval("return document.forms[0] instanceof HTMLFormElement;").asBoolean());
        assertTrue(eval("return document.forms[0] instanceof HTMLElement;").asBoolean());
    }

    @Test
    void unknownFormMemberIsUndefined() {
        assertEquals("undefined", eval("return typeof document.forms[0].noSuchControl;").asString());
        assertFalse(eval("return ('noSuchControl' in document.forms[0]);").asBoolean());
    }
}
