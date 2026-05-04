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
package org.loboevolution.apache.xml.dtm.ref;

import org.loboevolution.html.node.Node;
import org.loboevolution.html.node.NodeList;

import java.util.Collections;
import java.util.Iterator;

/**
 * Base implementation of DOM's NodeList wrapped around a DTM iterator. After
 * Phase 12 the iterator/forEach signatures use plain Java types instead of
 * Rhino's {@code ES6Iterator}/{@code Function}.
 */
public class DTMNodeListBase implements NodeList {
  public DTMNodeListBase() {}

  @Override
  public Node item(final int index) {
    return null;
  }

  @Override
  public Iterator<Object> entries() {
    return Collections.emptyIterator();
  }

  @Override
  public Iterator<Integer> keys() {
    return Collections.emptyIterator();
  }

  @Override
  public Iterator<Node> values() {
    return Collections.emptyIterator();
  }

  @Override
  public void forEach(final Object function) {
  }

  @Override
  public Node[] toArray() {
    return new Node[0];
  }

  @Override
  public int getLength() {
    return 0;
  }
}
