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
import org.loboevolution.common.Nodes;
import org.loboevolution.common.Strings;
import org.loboevolution.gui.HtmlRendererContext;
import org.loboevolution.html.dom.DOMTokenList;
import org.loboevolution.html.dom.HTMLFormControlsCollection;
import org.loboevolution.html.dom.HTMLFormElement;
import org.loboevolution.html.dom.filter.FormFilter;
import org.loboevolution.html.dom.input.FormInput;
import org.loboevolution.html.dom.nodeimpl.NodeImpl;
import org.loboevolution.html.dom.nodeimpl.NodeListImpl;
import org.loboevolution.html.js.engine.MemberReflector;
import org.loboevolution.html.node.NodeVisitor;
import org.loboevolution.html.js.WindowImpl;
import org.loboevolution.html.node.Element;
import org.loboevolution.html.node.NamedNodeMap;
import org.loboevolution.html.node.Node;
import org.loboevolution.html.renderstate.FormRenderState;
import org.loboevolution.html.renderstate.RenderState;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>HTMLFormElementImpl class.</p>
 *
 * <p>Implements {@link ProxyObject} + {@link ProxyIterable} so GraalJS exposes a
 * form's <em>named</em> and <em>indexed</em> controls the way a browser does:
 * {@code document.forms.myForm.myControl} / {@code form.myControl} resolve to the
 * descendant control whose name (or id) matches, and {@code form[i]} / {@code
 * for..of} enumerate the controls — reusing the form's own traversal-based
 * {@link #namedItem}/{@link #item} (which walk the descendants via {@code isInput},
 * not the collection returned by {@link #getElements}). All other members fall
 * back to the same bean/method reflection the Window and collection proxies use.
 * {@code instanceof HTMLFormElement}/{@code HTMLElement} still works because the
 * proxy keeps its host-type identity. The non-browser {@code item}/{@code
 * namedItem} methods are hidden so {@code form.item(1)} is {@code undefined} as in
 * a browser.</p>
 */
public class HTMLFormElementImpl extends HTMLElementImpl implements HTMLFormElement, ProxyObject, ProxyIterable {

	private HTMLFormControlsCollection elements;

	/**
	 * <p>Constructor for HTMLFormElementImpl.</p>
	 */
	public HTMLFormElementImpl() {
		super("FORM");
	}

	/**
	 * <p>Constructor for HTMLFormElementImpl.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public HTMLFormElementImpl(final String name) {
		super(name);
	}

	/** {@inheritDoc} */
	@Override
	protected RenderState createRenderState(final RenderState prevRenderState) {
		return new FormRenderState(prevRenderState, this);
	}

	/** {@inheritDoc} */
	@Override
	public String getAcceptCharset() {
		return getAttribute("acceptCharset");
	}

	/** {@inheritDoc} */
	@Override
	public String getAction() {
		return getAttribute("action");
	}

	/** {@inheritDoc} */
	@Override
	public HTMLFormControlsCollection getElements() {
		if (this.elements == null) {
			final HTMLFormControlsCollectionImpl elements = new HTMLFormControlsCollectionImpl((NodeImpl) this.getDocumentNode().getRootNode(), new FormFilter());
			final List<Node> list = new ArrayList<>();
			elements.forEach(node -> {
				if (node.hasAttributes()) {
					final NamedNodeMap attributes = node.getAttributes();
					if (attributes != null) {
						for (final Node attribute : Nodes.iterable(attributes)) {
							if (getName().equals(attribute.getNodeValue())) {
								list.add(node);
							}
						}
					}
				}

				if (node.hasChildNodes()) {
					findChild(node, list);
				}
			});
			elements.clear();
			elements.addAll(list);
			this.elements = elements;
		}
		return this.elements;
	}

	private void findChild(final Node node, final List<Node> list) {
		final NodeListImpl childNodes = (NodeListImpl) node.getChildNodes();
		childNodes.forEach(nde -> {
			if (nde.hasAttributes()) {
				final NamedNodeMap attributes = nde.getAttributes();
				for (final Node attribute : Nodes.iterable(attributes)) {
					if (getName().equals(attribute.getNodeValue())) {
						list.add(node);
					}
				}
			}

			if (nde.hasChildNodes()) {
				findChild(nde, list);
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public String getEnctype() {
		String enctype = getAttribute("enctype");
		return Strings.isNotBlank(enctype) && !"null".equals(enctype) ? enctype : "application/x-www-form-urlencoded";
	}

	/** {@inheritDoc} */
	@Override
	public int getLength() {
		return ((HTMLFormControlsCollectionImpl)getElements()).size();
	}

	/** {@inheritDoc} */
	@Override
	public String getMethod() {
		String method = getAttribute("method");
		if (method == null) {
			method = "GET";
		}
		return method;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		final String name = getAttribute("name");
		return name == null ? "" : name;
	}

	/** {@inheritDoc} */
	@Override
	public String getTarget() {
		return getAttribute("target");
	}

	/**
	 * <p>item.</p>
	 *
	 * @param index a {@link java.lang.Integer} object.
	 * @return a {@link java.lang.Object} object.
	 */
	public Object item(final int index) {
		try {
			visit(new NodeVisitor() {
				private int current = 0;

				@Override
				public void visit(final Node node) {
					if (HTMLFormElementImpl.isInput(node)) {
						if (this.current == index) {
							throw new StopVisitorException(node);
						}
						this.current++;
					}
				}
			});
		} catch (final StopVisitorException sve) {
			return sve.getTag();
		}
		return null;
	}

	/**
	 * <p>namedItem.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a {@link java.lang.Object} object.
	 */
	public Object namedItem(final String name) {
		try {
			// TODO: This could use document.namedItem.
			visit(node -> {
				if (HTMLFormElementImpl.isInput(node)) {
					if (name.equals(((Element) node).getAttribute("name"))
							|| name.equals(((Element) node).getAttribute("id"))) {
						throw new StopVisitorException(node);
					}
				}
			});
		} catch (final StopVisitorException sve) {
			return sve.getTag();
		}
		return null;
	}

	// --- GraalJS ProxyIterable: for..of / Array.from over the form controls ---

	/** {@inheritDoc} */
	@Override
	public Object getIterator() {
		return new Iterator<Object>() {
			private int index = 0;
			private Object lookahead = item(0);

			@Override
			public boolean hasNext() {
				return lookahead != null;
			}

			@Override
			public Object next() {
				if (lookahead == null) {
					throw new NoSuchElementException();
				}
				final Object current = lookahead;
				lookahead = item(++index);
				return current;
			}
		};
	}

	// --- GraalJS ProxyObject: numeric-index + named-control access ---

	/** {@inheritDoc} */
	@Override
	public Object getMember(final String key) {
		final int idx = asFormIndex(key);
		if (idx != Integer.MIN_VALUE) {
			return idx >= 0 ? item(idx) : null;
		}
		// item/namedItem are collection methods, not browser members of a form:
		// hiding them keeps form.item(1) === undefined (a browser TypeError).
		if (!"item".equals(key) && !"namedItem".equals(key)) {
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
		}
		return namedItem(key);
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasMember(final String key) {
		final int idx = asFormIndex(key);
		if (idx != Integer.MIN_VALUE) {
			return idx >= 0 && item(idx) != null;
		}
		if (!"item".equals(key) && !"namedItem".equals(key)) {
			if (MemberReflector.findGetter(this, key) != null) return true;
			if (MemberReflector.findSetter(this, key) != null) return true;
			if (MemberReflector.findMethods(this, key).length > 0) return true;
		}
		return namedItem(key) != null;
	}

	/** {@inheritDoc} */
	@Override
	public void putMember(final String key, final Value value) {
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

	/** Non-negative JS array index, or {@link Integer#MIN_VALUE} for a named member. */
	private static int asFormIndex(final String key) {
		if (key == null || key.isEmpty()) return Integer.MIN_VALUE;
		for (int i = 0; i < key.length(); i++) {
			if (!Character.isDigit(key.charAt(i))) return Integer.MIN_VALUE;
		}
		try {
			return Integer.parseInt(key);
		} catch (final NumberFormatException overflow) {
			return Integer.MIN_VALUE;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void reset() { 
		visit(node -> {
			if (node instanceof HTMLInputElementImpl hie) {
                hie.resetInput();
			}
			
			if (node instanceof HTMLButtonElementImpl btn) {
                btn.resetInput();
			}
			
			if (node instanceof HTMLSelectElementImpl slct) {
                slct.resetInput();
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public void setAcceptCharset(final String acceptCharset) {
		setAttribute("acceptCharset", acceptCharset);
	}

	/** {@inheritDoc} */
	@Override
	public void setAction(final String action) {
		setAttribute("action", action);
	}

	/** {@inheritDoc} */
	@Override
	public void setEnctype(final String enctype) {
		if (enctype.equalsIgnoreCase("application/x-www-form-urlencoded") ||
				enctype.equalsIgnoreCase("multipart/form-data")
				|| enctype.equalsIgnoreCase("text/plain"))
			setAttribute("enctype", enctype);
	}

	/** {@inheritDoc} */
	@Override
	public void setMethod(final String method) {
		setAttribute("method", method);
	}

	/** {@inheritDoc} */
	@Override
	public void setName(final String name) {
		setAttribute("name", name);
	}

	/** {@inheritDoc} */
	@Override
	public void setTarget(final String target) {
		setAttribute("target", target);
	}

	/** {@inheritDoc} */
	@Override
	public void submit() {
		this.submit(null);
	}

	/**
	 * This method should be called when form submission is done by a submit button.
	 *
	 * @param extraFormInputs Any additional form inputs that need to be submitted,
	 *                        e.g. the submit button parameter.
	 */
	public final void submit(final FormInput[] extraFormInputs) {
		final Object onsubmit = getOnsubmit();
		final WindowImpl window = (WindowImpl) this.getDocumentNode().getDefaultView();
		if (onsubmit != null) {
			final Object result = org.loboevolution.html.js.engine.JsEngineFactory
					.forDocument(this.getDocumentNode(), window)
					.call(onsubmit);
			if (Boolean.FALSE.equals(result)) {
				return;
			}
		}
		final HtmlRendererContext context = getHtmlRendererContext();
		if (context != null) {
			final ArrayList<FormInput> formInputs = new ArrayList<>();
			if (extraFormInputs != null) {
				Collections.addAll(formInputs, extraFormInputs);
			}
			visit(node -> {
				if (node instanceof HTMLElementImpl) {
					final FormInput[] fis = ((HTMLElementImpl) node).getFormInputs();
					if (fis != null) {
						for (final FormInput fi : fis) {
							if (fi.getName() == null) {
								throw new IllegalStateException("Form input does not have a name: " + node);
							}
							formInputs.add(fi);
						}
					}
				}
			});
			final FormInput[] fia = formInputs.toArray(FormInput.EMPTY_ARRAY);
			String href = getAction();
			if (href == null) {
				href = getBaseURI();
			}
			try {
				final URL url = getFullURL(href);
				context.submitForm(getMethod(), url, getTarget(), getEnctype(), fia);
			} catch (Exception mfu) {
				this.warn("submit()", mfu);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getAutocomplete() {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void setAutocomplete(final String autocomplete) {
		// TODO Auto-generated method stub
		
	}

	/** {@inheritDoc} */
	@Override
	public String getEncoding() {
		return getEnctype();
	}

	/** {@inheritDoc} */
	@Override
	public void setEncoding(final String encoding) {
		setEnctype(encoding);

	}

	/** {@inheritDoc} */
	@Override
	public boolean isNoValidate() {
		// TODO Auto-generated method stub
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public void setNoValidate(final boolean noValidate) {
		// TODO Auto-generated method stub
		
	}

	/** {@inheritDoc} */
	@Override
	public boolean checkValidity() {
		// TODO Auto-generated method stub
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean reportValidity() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getRel() {
		return getAttribute("rel");
	}

	@Override
	public void setRel(final String rel) {
		setAttribute("rel", rel);
	}

	@Override
	public DOMTokenList getRelList() {
		final DOMTokenListImpl tokList = new DOMTokenListImpl(this);
		final String rel = getRel();
		if(rel != null){
			final String[] listString = rel.split(" ");
			final List<String> names = Arrays.asList(listString);
			names.forEach(tokList::populate);
		}
		return tokList;
	}

	@Override
	public void setRelList(String rel) {
		setRel(rel == null ? "null": rel);
	}

	/**
	 * <p>isInput.</p>
	 *
	 * @param node a {@link org.loboevolution.html.node.Node} object.
	 * @return a boolean.
	 */
	public static boolean isInput(final Node node) {
		final String name = node.getNodeName().toLowerCase();
		return name.equals("input") || name.equals("textarea") || name.equals("select");
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "[object HTMLFormElement]";
	}
}
