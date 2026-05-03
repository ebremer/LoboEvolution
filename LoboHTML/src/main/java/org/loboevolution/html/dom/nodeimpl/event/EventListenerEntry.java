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

package org.loboevolution.html.dom.nodeimpl.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mozilla.javascript.Function;

/**
 * One registered event listener. The {@link #callback} is held as a generic
 * {@link Object} so the same entry type works for both engines: Rhino stores
 * a {@link org.mozilla.javascript.Function}; GraalJS stores a polyglot
 * {@code Value}. Dispatch routes through the JsEngine's
 * {@code call(callback, args)} method, which knows how to invoke either.
 *
 * <p>{@link #getFunction()} preserves the legacy Rhino-only accessor so
 * existing code that pulls the callback out as a Rhino {@code Function} (the
 * {@code on*} attribute path in {@code HTMLElementImpl}) keeps compiling.
 * It returns {@code null} for non-Rhino callbacks.
 */
@Data
@AllArgsConstructor
class EventListenerEntry {

    private String type;
    private Object callback;
    private boolean useCapture;

    /** Convenience constructor that defaults useCapture to false. */
    EventListenerEntry(final String type, final Object callback) {
        this(type, callback, false);
    }

    /**
     * Returns the callback as a Rhino {@link Function} when it is one, else
     * {@code null}. Kept for the legacy {@code getOn*()} accessors that go
     * back through Rhino's compiled-function machinery.
     */
    public Function getFunction() {
        return callback instanceof Function f ? f : null;
    }
}
