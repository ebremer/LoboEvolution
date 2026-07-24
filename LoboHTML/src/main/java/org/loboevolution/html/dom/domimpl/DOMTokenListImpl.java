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
import org.htmlunit.cssparser.dom.DOMException;
import org.loboevolution.common.Strings;
import org.loboevolution.html.dom.DOMTokenList;
import org.loboevolution.html.dom.nodeimpl.ElementImpl;
import org.loboevolution.html.js.engine.MemberReflector;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * <p>DOMTokenListImpl class.</p>
 *
 * <p>Implements {@link ProxyObject} + {@link ProxyIterable} so a {@code classList}
 * is exposed to GraalJS as the browser array-like it is: {@code classList[0]},
 * {@code classList.length}, {@code for..of}/{@code Array.from}, plus its methods
 * ({@code add}/{@code remove}/{@code toggle}/{@code contains}/{@code item}/…).
 * Previously only the methods and {@code length} were reachable — {@code
 * classList[i]} was {@code undefined} and iterating threw "not iterable", which
 * aborted the many token-list tests that loop over the list. Rhino is unaware of
 * the polyglot proxy interfaces and keeps using its own reflection unchanged.</p>
 */
public class DOMTokenListImpl implements DOMTokenList, ProxyObject, ProxyIterable {

	private final ElementImpl element;

	private final List<String> tokenset;

	/**
	 * <p>Constructor for DOMTokenListImpl.</p>
	 * @param element a {@link ElementImpl} object.
	 */
	public DOMTokenListImpl(final ElementImpl element) {
		this.element = element;
		this.tokenset = new LinkedList<>();
	}

	/** {@inheritDoc} */
	@Override
	public int getLength() {
		return tokenset.size();
	}

	/** {@inheritDoc} */
	@Override
	public String item(final int index) {
		if (index < 0 || index >= tokenset.size()) {
			return null;
		}
		return tokenset.get(index);
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(final String token) {
		return tokenset.contains(token);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final String token) throws DOMException {

		if(token == null) {
			throw new DOMException(DOMException.SYNTAX_ERR, "Token cannot contain spaces");
		}

		if (!tokenset.contains(token)) {
			final String tok = token.trim();
			if (Strings.isBlank(tok)) {
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Token cannot contain spaces");
			}

			if (tok.contains(" ") || tok.contains("\t")) {
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Token cannot contain spaces");
			}
			tokenset.add(tok);
			element.setAttribute("class", getValue());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void remove(final String token) {
		if (token != null) {
			final String tokTrim = token.trim();

			if (Strings.isBlank(tokTrim)) {
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Token cannot contain spaces");
			}

			if (tokTrim.contains(" ") || tokTrim.contains("\t")) {
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Token cannot contain spaces");
			}

			tokenset.remove(tokTrim);
			final StringBuilder sb = new StringBuilder();
			tokenset.forEach(tok -> sb.append(tok).append(' '));
			element.setAttribute("class",sb.toString().trim());
		}
	}

	public void remove() {
		tokenset.clear();
	}

	/** {@inheritDoc} */
	@Override
	public boolean toggle(final String token) throws DOMException {
		if (token != null) {
			final String tok = token.trim();

			if (Strings.isBlank(tok)) {
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Token cannot contain spaces");
			}

			if (tok.contains(" ")) {
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Token cannot contain spaces");
			}

			if (!tokenset.remove(tok)) {
				final boolean result = tokenset.add(tok);
				if (result) {
					final StringBuilder sb = new StringBuilder();
					tokenset.forEach(tk -> sb.append(tk).append(' '));
					element.setAttribute("class",sb.toString().trim());
				}
				return result;
			} else {
				final StringBuilder sb = new StringBuilder();
				tokenset.forEach(tk -> sb.append(tk).append(' '));
				element.setAttribute("class",sb.toString().trim());
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean toggle(final String token, final boolean force) {
		if (force) {
			add(token);
		} else {
			remove(token);
		}
		return force;
	}

	/** {@inheritDoc} */
	@Override
	public String getValue() {
		final int sz = tokenset.size();
		if (sz == 0) {
			return "";
		}
		if (sz == 1) {
			return tokenset.getFirst();
		}
		final StringBuilder buf = new StringBuilder(32 + 12 * tokenset.size());
		final Iterator<String> it = tokenset.iterator();
		buf.append(it.next());
		while (it.hasNext()) {
			buf.append(' ').append(it.next());
		}
		return buf.toString();
	}

	/** {@inheritDoc} */
	@Override
	public void setValue(final String value) throws DOMException {
		if (value == null) {
			throw new DOMException(DOMException.SYNTAX_ERR, "Token cannot be null");
		}
		tokenset.clear();
		final StringTokenizer st = new StringTokenizer(value);
		while (st.hasMoreTokens()) {
			tokenset.add(st.nextToken());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean replace(final String oldToken, final String newToken) {
		if (Strings.isNotBlank(oldToken) && Strings.isNotBlank(newToken)) {
			final int idx = tokenset.indexOf(oldToken);
			if (idx != -1) {
				final int idx2 = tokenset.indexOf(newToken);
				if (idx2 != -1) {
					tokenset.remove(idx);
				} else{
					tokenset.set(idx, newToken);
				}
				element.setAttribute("class",getValue());
				return true;
			}
		}

		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean supports(final String token) {
		// TODO Auto-generated method stub
		return false;
	}

	public void populate(final String token) {
		if (token != null && !tokenset.contains(token)) {
			final String tok = token.trim();
			if (Strings.isNotBlank(tok)) {
				if(Strings.countChars(tok, '\t') == 0) tokenset.add(tok);
				final String className = element.getClassName();
				if (!className.contains(tok))
					element.setAttribute("class",Strings.isNotBlank(className) ? className.trim() + " " + tok : tok);
			}
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		tokenset.forEach(tok -> sb.append(tok).append(' '));
		return sb.toString().trim();
	}

	// --- GraalJS ProxyIterable: for..of / Array.from over the tokens ---

	/** {@inheritDoc} */
	@Override
	public Object getIterator() {
		return new Iterator<Object>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < getLength();
			}

			@Override
			public Object next() {
				if (index >= getLength()) {
					throw new NoSuchElementException();
				}
				return item(index++);
			}
		};
	}

	// --- GraalJS ProxyObject: numeric index, length, token-list methods ---

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
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasMember(final String key) {
		final int idx = asIndex(key);
		if (idx != Integer.MIN_VALUE) {
			return idx >= 0 && idx < getLength();
		}
		return MemberReflector.findGetter(this, key) != null
				|| MemberReflector.findSetter(this, key) != null
				|| MemberReflector.findMethods(this, key).length > 0;
	}

	/** {@inheritDoc} */
	@Override
	public void putMember(final String key, final Value value) {
		final Method setter = MemberReflector.findSetter(this, key);
		if (setter != null) {
			try {
				setter.invoke(this, MemberReflector.coerce(value, setter.getParameterTypes()[0]));
			} catch (final ReflectiveOperationException ignored) {
				// read-only member
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public Object getMemberKeys() {
		return MemberReflector.reflectiveMemberNames(this).toArray(new String[0]);
	}

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
}