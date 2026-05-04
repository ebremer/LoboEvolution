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

package org.loboevolution.html.node;

import java.util.Iterator;

/**
 * NodeList objects are collections of nodes, usually returned by properties
 * such as Node.childNodes and methods such as document.querySelectorAll().
 *
 * <p>After Phase 12 the iterator/callback types use plain Java instead of
 * Rhino's {@code ES6Iterator}/{@code Function}: GraalJS handles the JS-side
 * iteration protocol automatically when given any {@link Iterable}.
 */
public interface NodeList {

    /** Number of nodes in the collection. */
    int getLength();

    /** Returns the node at {@code index} in tree order, or {@code null}. */
    Node item(int index);

    /** Iterator over [index, node] pairs. */
    Iterator<Object> entries();

    /** Iterator over the indices. */
    Iterator<Integer> keys();

    /** Iterator over the contained nodes. */
    Iterator<Node> values();

    /**
     * Executes the provided callback once per element. Callback type is
     * opaque so any engine's invocable representation fits.
     */
    void forEach(Object function);

    Node[] toArray();
}
