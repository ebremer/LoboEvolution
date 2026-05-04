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

package org.loboevolution.events;

import org.loboevolution.html.node.Node;
import org.w3c.dom.events.EventException;

/**
 * Engine-agnostic event target. Listener parameters are typed as plain
 * {@link Object} so any callable a JS engine can produce — a polyglot
 * {@code Value}, a {@code FunctionalInterface} lambda, etc. — fits.
 */
public interface EventTarget {

    /** Registers {@code listener} for {@code type} (no useCapture). */
    void addEventListener(String type, Object listener);

    /** {@link #addEventListener(String, Object)} with an explicit useCapture flag. */
    void addEventListener(String type, Object listener, boolean useCapture);

    /** Removes a previously-registered listener (no useCapture). */
    void removeEventListener(String type, Object listener);

    /** {@link #removeEventListener(String, Object)} with an explicit useCapture flag. */
    void removeEventListener(String type, Object listener, boolean useCapture);

    boolean dispatchEvent(Node element, Event evt);

    boolean dispatchEvent(Event evt) throws EventException;
}
