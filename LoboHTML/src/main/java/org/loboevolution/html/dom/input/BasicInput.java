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

package org.loboevolution.html.dom.input;

import lombok.Data;
import org.loboevolution.common.Strings;
import org.loboevolution.config.HtmlRendererConfig;
import org.loboevolution.html.dom.HTMLInputElement;
import org.loboevolution.html.dom.domimpl.HTMLBasicInputElement;
import org.loboevolution.html.dom.domimpl.HTMLDocumentImpl;
import org.loboevolution.html.dom.domimpl.HTMLInputElementImpl;
import org.loboevolution.html.js.WindowImpl;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>BasicInput class.</p>
 */
@Data
public class BasicInput implements FocusListener, KeyListener, CaretListener, MouseListener {

    private HTMLBasicInputElement element;

    private JTextComponent jComponent;

    @Override
    public void focusGained(final FocusEvent e) {
        if (element.getOnfocus() != null) {
            callHandler(element, element.getOnfocus());
        }

        if (element.getOnfocusin() != null) {
            callHandler(element, element.getOnfocusin());
        }
    }

    @Override
    public void focusLost(final FocusEvent event) {
        final boolean autocomplete = element.isAutocomplete();
        final String baseUrl = element.getBaseURI();
        final String type = element instanceof HTMLInputElement ? ((HTMLInputElement)element).getType() : "";

        final String selectedText = jComponent.getSelectedText();
        if (Strings.isNotBlank(selectedText) && Strings.isNotBlank(element.getValue())) {
            final Pattern word = Pattern.compile(selectedText);
            final Matcher match = word.matcher(element.getValue());

            while (match.find()) {
                element.setSelectionRange(match.start(), match.end() - 1);
            }
        }

        if (autocomplete || "password".equals(type)) {
            final HTMLInputElementImpl im =  (HTMLInputElementImpl)element;
            final WindowImpl win = (WindowImpl) im.getDocumentNode().getDefaultView();
            final HtmlRendererConfig config = win.getConfig();
            final String text = jComponent.getText();
            final boolean isNavigation = element.getUserAgentContext().isNavigationEnabled();
            config.deleteInput(text, baseUrl);
            config.insertLogin(type, text, baseUrl, isNavigation);
        }

        if (element.getOnblur() != null) {
            callHandler(element, element.getOnblur());
        }

        if (element.getOnfocusout() != null) {
            jComponent.setText(element.getValue());
            callHandler(element, element.getOnfocusout());
        }
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        if (element.getOnkeydown() != null) {
            callHandler(element, element.getOnkeydown());
        }

        if (element.getOnkeypress() != null) {
            callHandler(element, element.getOnkeypress());
        }

        if (element.getOninput() != null) {
            element.setValue(Strings.isBlank(element.getValue()) ? String.valueOf(e.getKeyChar()) : element.getValue() + e.getKeyChar());
            callHandler(element, element.getOninput());
        }
    }

    @Override
    public void keyPressed(final KeyEvent e) {

    }

    @Override
    public void keyReleased(final KeyEvent e) {
        if (element.getOnkeyup() != null) {
            callHandler(element, element.getOnkeyup());
        }
    }

    @Override
    public void caretUpdate(final CaretEvent e) {
        final int dot = e.getDot();
        final int mark = e.getMark();

        if (dot != mark && element.getOnselect() != null) {
            callHandler(element, element.getOnselect());
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        if (element.getOnmouseover() != null) {
            callHandler(element, element.getOnmouseover());
        }
    }

    public void mousePressed(final MouseEvent e) {
        if (element.getOnkeypress() != null) {
            callHandler(element, element.getOnkeypress());
        }

        if (element.getOnkeydown() != null) {
            callHandler(element, element.getOnkeydown());
        }
    }

    public void mouseReleased(final MouseEvent e) {
        if (element.getOnkeyup() != null) {
            callHandler(element, element.getOnkeyup());
        }
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /**
     * Routes a registered handler through the document's JS engine. Replaces
     * the legacy {@code Executor.executeFunction} call paths so dispatch is
     * engine-agnostic — the handler can be a polyglot {@code Value}, a Rhino
     * {@code Function}, or anything else the active engine produces.
     */
    static void callHandler(final org.loboevolution.html.node.Node node, final Object handler) {
        if (handler == null || node == null) return;
        final HTMLDocumentImpl doc = (HTMLDocumentImpl) node.getOwnerDocument();
        if (doc == null) return;
        final WindowImpl window = (WindowImpl) doc.getDefaultView();
        if (window == null) return;
        try {
            org.loboevolution.html.js.engine.JsEngineFactory
                    .forDocument(doc, window)
                    .call(handler);
        } catch (final Throwable ignored) {
            // Match the legacy behaviour of swallowing handler errors.
        }
    }
}
