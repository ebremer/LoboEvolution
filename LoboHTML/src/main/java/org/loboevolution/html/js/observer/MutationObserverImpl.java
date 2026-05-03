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

package org.loboevolution.html.js.observer;

import org.loboevolution.js.AbstractScriptableDelegate;

/**
 * No-op stub of the DOM <a href="https://developer.mozilla.org/en-US/docs/Web/API/MutationObserver">MutationObserver</a>.
 *
 * <p>Modern frameworks (React, Vue, Bootstrap 5, etc.) probe for {@code MutationObserver}
 * during bootstrap and throw a {@code ReferenceError} if it is missing, aborting any
 * subsequent script execution. This stub satisfies the constructor + method surface so
 * those scripts can proceed; the observer never actually fires its callback. Consumers
 * that depend on real mutation tracking will see no events, but they will at least load.
 */
public class MutationObserverImpl extends AbstractScriptableDelegate {

    private final Object callback;

    public MutationObserverImpl(final Object[] args) {
        this.callback = (args != null && args.length > 0) ? args[0] : null;
    }

    public void observe(final Object target, final Object options) {
        // no-op: we do not track mutations
    }

    public void disconnect() {
        // no-op
    }

    public Object[] takeRecords() {
        return new Object[0];
    }

    public Object getCallback() {
        return callback;
    }
}
