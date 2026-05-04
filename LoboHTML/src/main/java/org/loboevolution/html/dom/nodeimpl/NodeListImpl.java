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
package org.loboevolution.html.dom.nodeimpl;

import org.loboevolution.html.dom.domimpl.HTMLElementImpl;
import org.loboevolution.html.js.WindowImpl;
import org.loboevolution.html.js.engine.JsEngineFactory;
import org.loboevolution.html.node.AbstractList;
import org.loboevolution.html.node.Node;
import org.loboevolution.html.node.NodeList;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/** Live and snapshot NodeList implementation backed by a Java List. */
public class NodeListImpl extends AbstractList<Node> implements NodeList {

	public NodeListImpl() {
	}

	public NodeListImpl(final List<Node> collection) {
		super(collection);
	}

	@Override
	public int getLength() {
		return this.size();
	}

	@Override
	public Node item(final int index) {
		final int size = this.size();
		if (size > index && index > -1) {
			return this.get(index);
		} else {
			return null;
		}
	}

	@Override
	public Iterator<Object> entries() {
		return new Iterator<>() {
			private int i = 0;
			@Override public boolean hasNext() { return i < size(); }
			@Override public Object next() {
				if (!hasNext()) throw new NoSuchElementException();
				final int idx = i++;
				return new Object[]{ idx, get(idx) };
			}
		};
	}

	@Override
	public Iterator<Integer> keys() {
		return new Iterator<>() {
			private int i = 0;
			@Override public boolean hasNext() { return i < size(); }
			@Override public Integer next() {
				if (!hasNext()) throw new NoSuchElementException();
				return i++;
			}
		};
	}

	@Override
	public Iterator<Node> values() {
		return iterator();
	}

	@Override
	public void forEach(final Object function) {
		if (function == null) return;
		HTMLElementImpl probe = null;
		for (final Node n : this) {
			if (n instanceof HTMLElementImpl elem) { probe = elem; break; }
		}
		if (probe == null) return;
		final WindowImpl win = (WindowImpl) probe.getDocumentNode().getDefaultView();
		final var engine = JsEngineFactory.forDocument(probe.getDocumentNode(), win);
		int i = 0;
		for (final Node n : this) {
			engine.call(function, n, i, this);
			i++;
		}
	}

	@Override
	public Node[] toArray() {
		return this.toArray(new Node[0]);
	}

	@Override
	public String toString() {
		return "[object NodeList]";
	}
}
