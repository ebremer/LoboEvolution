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

package org.loboevolution.html.js;

import lombok.extern.slf4j.Slf4j;
import org.loboevolution.html.dom.domimpl.HTMLDocumentImpl;
import org.loboevolution.html.js.engine.JsEngine;
import org.loboevolution.html.js.engine.JsEngineFactory;

import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;

/**
 * Engine-agnostic counterpart to {@link FunctionTimerTask}. Holds the callback
 * as an opaque {@link Object} and dispatches it through whichever
 * {@link JsEngine} owns the associated document.
 *
 * <p>Used by {@code WindowImpl.setTimeout/setInterval} when the supplied
 * callback is neither a Rhino {@link org.mozilla.javascript.Function} nor a
 * string expression — i.e., the typical case under GraalJS where a JS
 * function arrives as a polyglot {@code Value}.
 */
@Slf4j
class CallableTimerTask extends WeakWindowTask {

    private final WeakReference<Object> callbackRef;
    private final boolean removeTask;
    private final Integer timeIDInt;

    public CallableTimerTask(final WindowImpl window, final Integer timeIDInt, final Object callback, final boolean removeTask) {
        super(window);
        this.timeIDInt = timeIDInt;
        this.callbackRef = new WeakReference<>(callback);
        this.removeTask = removeTask;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        try {
            final WindowImpl window = this.getWindow();
            if (window == null) {
                log.info("actionPerformed(): WindowImpl is no longer available.");
                return;
            }
            if (this.removeTask) {
                window.forgetTask(this.timeIDInt, false);
            }
            final HTMLDocumentImpl doc = (HTMLDocumentImpl) window.getDocument();
            if (doc == null) {
                throw new IllegalStateException("Cannot perform operation when document is unset.");
            }
            final Object callback = this.callbackRef.get();
            if (callback != null) {
                final JsEngine engine = JsEngineFactory.forDocument(doc, window);
                engine.call(callback);
            }
        } catch (final Throwable err) {
            log.error("actionPerformed()", err);
        }
    }
}
