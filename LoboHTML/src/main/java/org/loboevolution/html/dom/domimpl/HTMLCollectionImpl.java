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

package org.loboevolution.html.dom.domimpl;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyIterable;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.loboevolution.html.dom.HTMLCollection;
import org.loboevolution.html.dom.nodeimpl.NodeImpl;
import org.loboevolution.html.dom.nodeimpl.NodeListImpl;
import org.loboevolution.html.js.engine.MemberReflector;
import org.loboevolution.html.node.*;
import org.loboevolution.traversal.NodeFilter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * <p>HTMLCollectionImpl class.</p>
 *
 * <p>Implements {@link ProxyObject} + {@link ProxyIterable} so the GraalJS bridge
 * exposes an {@code HTMLCollection} the way a browser does: numeric index
 * ({@code coll[0]}), {@code length} (read <em>and</em> the option collection's
 * writable {@code length} truncation), <em>named</em> access
 * ({@code document.forms.myForm}, {@code select.options.opt}) and the collection
 * methods ({@code item}, {@code namedItem}, and the option collection's
 * {@code add}/{@code remove}/{@code selectedIndex}) all route through
 * {@link ProxyObject}; {@code for..of}/{@code Array.from}/spread come from
 * {@link ProxyIterable}. (A {@code ProxyArray} was rejected: it makes
 * {@code length} read-only, silently dropping {@code options.length = 0}.)
 * Without the named half, {@code coll.name} fell through GraalJS bean reflection
 * to {@code undefined}. Rhino is unaware of the polyglot proxy interfaces and
 * keeps using its own reflection unchanged.</p>
 */
public class HTMLCollectionImpl extends AbstractList<Node> implements HTMLCollection, ProxyObject, ProxyIterable {
	
	private final NodeImpl rootNode;

	private final NodeFilter filter;

	/**
	 * <p>
	 * Constructor for HTMLCollectionImpl.
	 * </p>
	 * @param rootNode a {@link org.loboevolution.html.dom.nodeimpl.NodeImpl} object.
	 * @param list a {@link NodeList} object.
	 */
	public HTMLCollectionImpl(final NodeImpl rootNode, final NodeList list) {
		setList((NodeListImpl) list);
		this.rootNode = rootNode;
		this.filter = null;
	}

	 /**
	 * <p>
	 * Constructor for HTMLCollectionImpl.
	 * </p>
	 * @param rootNode a {@link org.loboevolution.html.dom.nodeimpl.NodeImpl} object.
	 * @param filter a {@link NodeFilter} object.
	 */
	public HTMLCollectionImpl(final NodeImpl rootNode, final NodeFilter filter) {
		setList((NodeListImpl) rootNode.getNodeList(filter));
		this.rootNode = rootNode;
		this.filter = filter;
	}

	/** {@inheritDoc} */
	@Override
	public int getLength() {
		if (filter != null) {
			setList(Arrays.asList(rootNode.getNodeList(filter).toArray()));
		}
		return this.size();
	}

	/** {@inheritDoc} */
	@Override
	public Node item(final Object index) {
		try {
			final double idx = Double.parseDouble(index.toString());
			if (idx >= getLength() || idx == -1) return null;
			return this.get((int) idx);
		} catch (final NumberFormatException e) {
			return this.get(0);
		}
	}

	@Override
	public void setItem(final Integer index, final Node node) {
		if (index > -1) {
			if (getLength() == 0 || getLength() == index || getLength() < index) {
				add(index, node);
			} else {
				set(index, node);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public Element namedItem(final String name) {
		final Document doc = this.rootNode.getOwnerDocument();
		if (doc == null) {
			return null;
		}
		final HTMLCollectionImpl nodeList = (HTMLCollectionImpl) doc.getElementsByName(name);
		if (nodeList.size() > 0) {
			final Optional<Node> node = nodeList.stream().findFirst();
			return (Element) node.orElse(null);
		} else {
			return doc.getElementById(name);
		}
	}
	
	// --- GraalJS ProxyIterable: for..of, Array.from, spread ---

	/** {@inheritDoc} */
	@Override
	public Object getIterator() {
		// A LIVE iterator: an HTMLCollection is live, so for..of must observe
		// elements added/removed mid-iteration (re-reads getLength()/item(i) each
		// step) rather than a snapshot taken up front.
		return new Iterator<Node>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < getLength();
			}

			@Override
			public Node next() {
				if (index >= getLength()) {
					throw new NoSuchElementException();
				}
				return item(index++);
			}
		};
	}

	// --- GraalJS ProxyObject: index, length, named access, methods ---

	/** {@inheritDoc} */
	@Override
	public Object getMember(final String key) {
		final int idx = asIndex(key);
		if (idx != Integer.MIN_VALUE) {
			return (idx >= 0 && idx < getLength()) ? item(idx) : null;
		}
		final Method getter = MemberReflector.findGetter(this, key);
		if (getter != null) {
			try {
				return getter.invoke(this);
			} catch (final ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}
		final Method[] methods = MemberReflector.findMethods(this, key);
		if (methods.length > 0) {
			return MemberReflector.makeExecutable(this, methods);
		}
		// Named-property access: document.forms.myForm, select.options.opt, ...
		return namedItemInCollection(key);
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasMember(final String key) {
		final int idx = asIndex(key);
		if (idx != Integer.MIN_VALUE) {
			return idx >= 0 && idx < getLength();
		}
		if (MemberReflector.findGetter(this, key) != null) return true;
		if (MemberReflector.findSetter(this, key) != null) return true;
		if (MemberReflector.findMethods(this, key).length > 0) return true;
		return namedItemInCollection(key) != null;
	}

	/** {@inheritDoc} */
	@Override
	public void putMember(final String key, final Value value) {
		final int idx = asIndex(key);
		if (idx != Integer.MIN_VALUE) {
			final Object node = MemberReflector.unwrap(value);
			if (node instanceof Node) {
				setItem(idx, (Node) node);
			}
			return;
		}
		final Method setter = MemberReflector.findSetter(this, key);
		if (setter != null) {
			try {
				setter.invoke(this, MemberReflector.coerce(value, setter.getParameterTypes()[0]));
			} catch (final ReflectiveOperationException ignored) {
				// read-only member; a strict-mode write must not surface as a TypeError
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public Object getMemberKeys() {
		return MemberReflector.reflectiveMemberNames(this).toArray(new String[0]);
	}

	/**
	 * Parses a JS array-style key ({@code "0"}, {@code "-1"}) to an int, or
	 * {@link Integer#MIN_VALUE} when {@code key} is a named member, not an index.
	 */
	private static int asIndex(final String key) {
		if (key == null || key.isEmpty()) return Integer.MIN_VALUE;
		final int start = key.charAt(0) == '-' ? 1 : 0;
		if (start == key.length()) return Integer.MIN_VALUE;
		for (int i = start; i < key.length(); i++) {
			if (!Character.isDigit(key.charAt(i))) return Integer.MIN_VALUE;
		}
		try {
			return Integer.parseInt(key);
		} catch (final NumberFormatException overflow) {
			return Integer.MIN_VALUE;
		}
	}

	/**
	 * HTML named-property access, scoped to this collection (not the whole
	 * document): the first contained element whose {@code id} or {@code name}
	 * attribute equals {@code key}, or null. This is what makes
	 * {@code document.forms.myForm} and {@code select.options.myOption} resolve.
	 */
	private Node namedItemInCollection(final String key) {
		if (key == null || key.isEmpty()) {
			return null;
		}
		final int len = getLength();
		for (int i = 0; i < len; i++) {
			final Node n = get(i);
			if (n instanceof Element el
					&& (key.equals(el.getAttribute("id")) || key.equals(el.getAttribute("name")))) {
				return n;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "[object HTMLCollection]";
	}
}
