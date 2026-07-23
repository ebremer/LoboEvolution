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
/*
 * Created on Sep 3, 2005
 */
package org.loboevolution.html.dom.domimpl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.htmlunit.cssparser.dom.CSSStyleSheetImpl;
import org.htmlunit.cssparser.dom.CSSValueImpl;
import org.htmlunit.cssparser.dom.Property;
import org.htmlunit.cssparser.parser.CSSOMParser;
import org.htmlunit.cssparser.parser.javacc.CSS3Parser;
import org.loboevolution.common.Strings;
import org.loboevolution.css.ComputedCSSStyleDeclaration;
import org.loboevolution.html.CSSValues;
import org.loboevolution.html.dom.HTMLBodyElement;
import org.loboevolution.html.dom.HTMLElement;
import org.loboevolution.html.dom.HTMLTableCellElement;
import org.loboevolution.html.dom.HTMLTableElement;
import org.loboevolution.html.dom.input.FormInput;
import org.loboevolution.html.dom.nodeimpl.ElementImpl;
import org.loboevolution.html.dom.nodeimpl.NodeListImpl;
import org.loboevolution.html.js.css.CSSStyleDeclarationImpl;
import org.loboevolution.html.node.Attr;
import org.loboevolution.html.node.Element;
import org.loboevolution.html.node.Node;
import org.loboevolution.css.CSSStyleDeclaration;
import org.loboevolution.events.GlobalEventHandlers;
import org.loboevolution.html.renderer.HtmlController;
import org.loboevolution.html.renderstate.RenderState;
import org.loboevolution.html.renderstate.StyleSheetRenderState;
import org.loboevolution.html.style.*;
import org.loboevolution.info.PropertyCssInfo;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>HTMLElementImpl class.</p>
 */
@Slf4j
public class HTMLElementImpl extends ElementImpl implements HTMLElement, GlobalEventHandlers, CSSPropertiesContext {

	private ComputedCSSStyleDeclaration computedStyles;

	private CSSStyleDeclaration currentStyleDeclarationState;

	private CSSStyleDeclaration mouseOverStyleDeclarationState = null;

	private CSSStyleDeclarationImpl localStyleDeclarationState = null;

	@Getter
	private boolean hasMouseOver;
	
	/**
	 * <p>Constructor for HTMLElementImpl.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public HTMLElementImpl(final String name) {
		super(name);
	}

	/** {@inheritDoc} */
	@Override
	public void assignAttributeField(final String normalName, final String value) {
		if (!this.notificationsSuspended) {
			informInvalidAttibute(normalName);
		} else {
			if ("style".equals(normalName)) {
				forgetLocalStyle();
			}
		}
		super.assignAttributeField(normalName, value);
	}


	/** {@inheritDoc} */
	@Override
	protected RenderState createRenderState(final RenderState prevRenderState) {
		return new StyleSheetRenderState(prevRenderState, this);
	}

	/**
	 * <p>forgetLocalStyle.</p>
	 */
	protected final void forgetLocalStyle() {
		synchronized (this) {
			this.currentStyleDeclarationState = null;
			this.localStyleDeclarationState = null;
			this.computedStyles = null;
		}
	}

	/**
	 * <p>forgetStyle.</p>
	 *
	 * @param deep a boolean.
	 */
	protected final void forgetStyle(final boolean deep) {
		synchronized (this) {
			this.currentStyleDeclarationState = null;
			this.computedStyles = null;
			if (deep) {

				nodeList.forEach(node -> {
					if (node instanceof HTMLElementImpl) {
						((HTMLElementImpl) node).forgetStyle(deep);
					}
				});
			}
		}
	}

	/**
	 * <p>getAncestorForJavaClass.</p>
	 *
	 * @param javaClass a {@link java.lang.Class} object.
	 * @return a {@link java.lang.Object} object.
	 */
	protected Object getAncestorForJavaClass(final Class<?> javaClass) {
		final Object nodeObj = getParentNode();
		if (nodeObj == null || javaClass.isInstance(nodeObj)) {
			return nodeObj;
		} else if (nodeObj instanceof HTMLElementImpl) {
			return ((HTMLElementImpl) nodeObj).getAncestorForJavaClass(javaClass);
		} else {
			return null;
		}
	}

	/**
	 * <p>getAttributeAsBoolean.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public boolean getAttributeAsBoolean(final String name) {
		return getAttribute(name) != null;
	}

	/**
	 * <p>getAttributeAsInt.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param defaultValue a {@link java.lang.Integer} object.
	 * @return a {@link java.lang.Integer} object.
	 */
	protected int getAttributeAsInt(final String name, final int defaultValue) {
		final String value = getAttribute(name);
		final HTMLDocumentImpl doc =  (HTMLDocumentImpl)this.document;
		return HtmlValues.getPixelSize(value, null, doc.getDefaultView(), defaultValue);
	}

	/**
	 * <p>getCharset.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCharset() {
		return getAttribute("charset");
	}


	public ComputedCSSStyleDeclaration getComputedStyle() {
		return getComputedStyle(getTagName());
	}

	/**
	 * <p>getComputedStyle.</p>
	 *
	 * @param pseudoElem a {@link java.lang.String} object.
	 * @return a {@link ComputedCSSStyleDeclaration} object.
	 */
	public ComputedCSSStyleDeclaration getComputedStyle(final String pseudoElem) {

		String pseudoElement = pseudoElem;

		if (pseudoElement == null) {
			pseudoElement = "";
		}

		final CSSStyleDeclarationImpl style = (CSSStyleDeclarationImpl) addStyleSheetDeclarations(false, pseudoElement);
		final CSSStyleDeclarationImpl localStyle = (CSSStyleDeclarationImpl) getStyle();
		localStyle.merge(style);
		this.computedStyles = new ComputedCSSStyleDeclarationImpl(this, localStyle);
		return this.computedStyles;
	}

	/**
	 * Gets the style object associated with the element.
	 * It may return null only if the type of element does not handle stylesheets.
	 *
	 * @return a {@link CSSStyleDeclaration} object.
	 */
	@Override
	public CSSStyleDeclaration getCurrentStyle() {
		CSSStyleDeclarationImpl style = (CSSStyleDeclarationImpl) this.currentStyleDeclarationState;
		if (style != null) {
			return style;
		}
		style = (CSSStyleDeclarationImpl) addStyleSheetDeclarations(false, getTagName());
		final CSSStyleDeclarationImpl localStyle = (CSSStyleDeclarationImpl) getStyle();
		localStyle.merge(style);
		this.currentStyleDeclarationState = localStyle;
		return this.currentStyleDeclarationState;
	}


	/** {@inheritDoc} */
	@Override
	public String getDocumentBaseURI() {
		final HTMLDocumentImpl doc = (HTMLDocumentImpl) this.document;
		if (doc != null) {
			return doc.getBaseURI();
		} else {
			return null;
		}
	}

	/**
	 * Gets form input due to the current element. It should return
	 * null except when the element is a form input element.
	 *
	 * @return an array of {@link org.loboevolution.html.dom.input.FormInput} objects.
	 */
	protected FormInput[] getFormInputs() {
		// Override in input elements
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public Integer getOffsetHeight() {
		return calculateHeight(true, true, false);
	}

	/** {@inheritDoc} */
	@Override
	public double getOffsetLeft() {

		StyleSheetRenderState renderState = (StyleSheetRenderState) getRenderState();
		final String left = renderState.getLeft();
		if (this instanceof HTMLBodyElement || (left != null && RenderState.POSITION_STATIC == renderState.getPosition())) {
			return 0;
		}

		final HTMLDocumentImpl doc = (HTMLDocumentImpl) this.document;
        if(CSSValues.AUTO.isEqual(left) && RenderState.POSITION_FIXED == renderState.getPosition()){
			return HtmlValues.getPixelSize(renderState.getPreviousRenderState().getLeft(), getRenderState(), doc.getDefaultView(), 0);
		}

		int offseLeft = HtmlValues.getPixelSize(left, getRenderState(), doc.getDefaultView(), 0);
		int borderLeftWidth = 0;
		int marginLeft = renderState.getMarginInsets() != null ? renderState.getMarginInsets().getLeft() : 0;
		int paddingLeft = RenderState.POSITION_STATIC != renderState.getPosition() ? renderState.getPaddingInsets() != null ? renderState.getPaddingInsets().getLeft() : 0 : 0;

		Node currentElement = this;
		while (currentElement != null) {
			final Node parentNode = currentElement.getParentNode();
            if (parentNode instanceof HTMLBodyElement && RenderState.POSITION_STATIC == renderState.getPosition() &&
					RenderState.POSITION_RELATIVE == renderState.getPosition()) {
                offseLeft += 8;
            } else if (parentNode instanceof HTMLElement) {
				final HTMLElementImpl parentElement = (HTMLElementImpl) parentNode;
				final CSSStyleDeclaration css = parentElement.getCurrentStyle();
				borderLeftWidth += HtmlValues.getPixelSize(css.getBorderLeftWidth(), getRenderState(), doc.getDefaultView(), 0);
				marginLeft += HtmlValues.getPixelSize(css.getMarginLeft(), getRenderState(), doc.getDefaultView(), 0);
				paddingLeft += HtmlValues.getPixelSize(css.getPaddingLeft(), getRenderState(), doc.getDefaultView(), 0);
			}
			currentElement = currentElement.getParentNode();
		}

		offseLeft += marginLeft;
		offseLeft += borderLeftWidth;
		offseLeft += paddingLeft;

		if (offseLeft == 0 && getParentNode() instanceof HTMLBodyElement) {
			return 8;
		}

		return offseLeft;
	}

	/** {@inheritDoc} */
	@Override
	public int getOffsetTop() {

		StyleSheetRenderState renderState = (StyleSheetRenderState) getRenderState();
		final String top = renderState.getTop();
		if (this instanceof HTMLBodyElement || (top != null && RenderState.POSITION_STATIC == renderState.getPosition())) {
			return 0;
		}

		final HTMLDocumentImpl doc = (HTMLDocumentImpl) this.document;
		if(CSSValues.AUTO.isEqual(top) && RenderState.POSITION_FIXED == renderState.getPosition()){
			return HtmlValues.getPixelSize(renderState.getPreviousRenderState().getTop(), getRenderState(), doc.getDefaultView(), 0);
		}

		int offsetTop = HtmlValues.getPixelSize(top, getRenderState(), doc.getDefaultView(), 0);
		int borderTopWidth = 0;
		int marginTop = renderState.getMarginInsets() != null ? renderState.getMarginInsets().getTop() : 0;
		int paddingTop = RenderState.POSITION_STATIC != renderState.getPosition() ? renderState.getPaddingInsets() != null ? renderState.getPaddingInsets().getTop() : 0 : 0;

		Node currentElement = this;
		while (currentElement != null) {
			final Node parentNode = currentElement.getParentNode();
			if (parentNode instanceof HTMLElement) {
				final HTMLElementImpl parentElement = (HTMLElementImpl) parentNode;
				final CSSStyleDeclaration css = parentElement.getCurrentStyle();
				borderTopWidth += HtmlValues.getPixelSize(css.getBorderTopWidth(), getRenderState(), doc.getDefaultView(), 0);
				if(RenderState.POSITION_STATIC != renderState.getPosition()){
					marginTop += HtmlValues.getPixelSize(css.getMarginTop(), getRenderState(), doc.getDefaultView(), 0);
				}
				paddingTop += HtmlValues.getPixelSize(css.getPaddingTop(), getRenderState(), doc.getDefaultView(), 0);
			}
			currentElement = currentElement.getParentNode();
		}

		offsetTop += marginTop;
		offsetTop += borderTopWidth;
		offsetTop += paddingTop;

		if (offsetTop == 0 && getParentNode() instanceof HTMLBodyElement) {
			return 8;
		}

		return offsetTop;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getOffsetWidth() {
		return calculateWidth(true, true, false);
	}

	/**
	 * <p>getParent.</p>
	 *
	 * @param elementTL a {@link java.lang.String} object.
	 * @return a {@link org.loboevolution.html.dom.domimpl.HTMLElementImpl} object.
	 */
	public HTMLElementImpl getParent(final String elementTL) {
		final Object nodeObj = getParentNode();
		if (nodeObj instanceof HTMLElementImpl parentElement) {
            if ("*".equals(elementTL)) {
				return parentElement;
			}
			final String pelementTL = parentElement.getTagName().toLowerCase();
			if (elementTL.equals(pelementTL)) {
				return parentElement;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public CSSStyleDeclaration getParentStyle() {
		final Object parent = this.parentNode;
		if (parent instanceof HTMLElementImpl elementParent) {
            if (elementParent.currentStyleDeclarationState != null) {
				return elementParent.currentStyleDeclarationState;
			}

			return elementParent.getCurrentStyle();
		}
		return null;
	}

	public CSSStyleDeclaration getStyle() {
		CSSStyleDeclarationImpl styleDeclaration = new CSSStyleDeclarationImpl(this, false);
		if (localStyleDeclarationState == null || localStyleDeclarationState.getLength() == 0) {
			final Attr style = getAttributeNode("style");
			if (style != null && Strings.isNotBlank(style.getValue())) {
				final CSSOMParser parser = new CSSOMParser(new CSS3Parser());
				try {
					styleDeclaration = new CSSStyleDeclarationImpl(this, parser.parseStyleDeclaration(style.getValue()));
				} catch (final Exception err) {
					final String id = getId();
					final String withId = Strings.isBlank(id) ? "" : " with ID '" + id + "'";
					this.warn("Unable to parse style attribute value for element " + getTagName() + withId + " in " + getDocumentURL() + ".", err);
				}
			}
			this.localStyleDeclarationState = propertyValueProcessed(styleDeclaration);
		}

		return this.localStyleDeclarationState;
	}

	/** {@inheritDoc} */
	@Override
	public void informInvalid() {
		// This is called when an attribute or child changes.
		forgetStyle(false);
		super.informInvalid();
	}

	/**
	 * <p>informInvalidAttibute.</p>
	 *
	 * @param normalName a {@link java.lang.String} object.
	 */
	public void informInvalidAttibute(final String normalName) {
		if ("style".equals(normalName)) {
			this.forgetLocalStyle();
		}
		forgetStyle(true);
		informInvalidRecursive();

	}

	private void informInvalidRecursive() {
		super.informInvalid();
		final NodeListImpl nodeList = this.getNodeList();
		if (nodeList != null) {
			nodeList.forEach(n -> {
				if (n instanceof HTMLElementImpl htmlElementImpl) {
                    htmlElementImpl.informInvalidRecursive();
				}
			});
		}
	}

	/**
	 * <p>setCharset.</p>
	 *
	 * @param charset a {@link java.lang.String} object.
	 */
	public void setCharset(final String charset) {
		setAttribute("charset", charset);
	}
	
	/**
	 * <p>setMouseOver.</p>
	 *
	 * @param mouseOver a boolean.
	 */
	public void setMouseOver(final boolean mouseOver) {
		if (hasMouseOver) {
			if (mouseOver) {
				if (mouseOverStyleDeclarationState != null) {
					currentStyleDeclarationState = mouseOverStyleDeclarationState;
				} else {
					currentStyleDeclarationState = addStyleSheetDeclarations(true, getTagName());
					mouseOverStyleDeclarationState = currentStyleDeclarationState;
				}

				informInvalidRecursive();
			} else {
				forgetStyle(true);
				informInvalidRecursive();
			}
		}
	}

	/**
	 * <p>setStyle.</p>
	 *
	 * @param value a {@link java.lang.String} object.
	 *
	 */
	public void setStyle(final String value) {
		this.setAttribute("style", value);
	}
	
    /** {@inheritDoc} */
    @Override
    public String getContentEditable() {
        final String contenteditable = this.getAttribute("contenteditable");
        return Strings.isBlank(contenteditable) ? "true" : contenteditable;
    }

    /** {@inheritDoc} */
    @Override
    public void setContentEditable(final String contenteditable) {
        this.setAttribute("contenteditable", contenteditable);
    }
 
	/** {@inheritDoc} */
	@Override
	public void warn(final String message) {
		log.warn(message);
	}

	/** {@inheritDoc} */
	@Override
	public void warn(final String message, final Throwable err) {
		log.warn(message, err);
	}

	/** {@inheritDoc} */
	@Override
	public String getAccessKey() {
		return getAttribute("accessKey");
	}

	/** {@inheritDoc} */
	@Override
	public String getAccessKeyLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public String getAutocapitalize() {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
    public Element getOffsetParent() {
        final Node parent = getParentNode();
        final RenderState rs = getRenderState();
        if (parent == null || rs == null) {
            return null;
        }

        Node currentElement = this;

        if (RenderState.POSITION_FIXED == rs.getPosition()) {
            return null;
        }

        final boolean staticPos = RenderState.POSITION_STATIC == rs.getPosition();
        while (currentElement != null) {
            final Node parentNode = currentElement.getParentNode();
            if (parentNode instanceof HTMLBodyElement
                    || (staticPos && parentNode instanceof HTMLTableElement)
                    || (staticPos && parentNode instanceof HTMLTableCellElement)) {
                return (HTMLElementImpl) parentNode;
            }

            if (parentNode instanceof HTMLElement) {
                final HTMLElementImpl parentElement = (HTMLElementImpl) parentNode;
                final RenderState parentRs = parentElement.getRenderState();
                if (parentRs != null && RenderState.POSITION_STATIC != parentRs.getPosition()) {
                    return (HTMLElementImpl) parentNode;
                }
            }

            currentElement = currentElement.getParentNode();
        }

        return null;
    }

	/** {@inheritDoc} */
	@Override
	public boolean isSpellcheck() {
		// TODO Auto-generated method stub
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDraggable() {
		// TODO Auto-generated method stub
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isHidden() {
		final String hidden = getAttribute("hidden");
		return hidden != null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isTranslate() {
		// TODO Auto-generated method stub
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public void setAccessKey(final String accessKey) {
		setAttribute("accessKey", accessKey);
	}

	/** {@inheritDoc} */
	@Override
	public void setAutocapitalize(final String autocapitalize) {
		// TODO Auto-generated method stub
		
	}

	/** {@inheritDoc} */
	@Override
	public void setDraggable(final boolean draggable) {
		// TODO Auto-generated method stub
		
	}

	/** {@inheritDoc} */
	@Override
	public void setHidden(final boolean hidden) {
		setAttribute("hidden", String.valueOf(hidden));
	}

	/** {@inheritDoc} */
	@Override
	public void setSpellcheck(final boolean spellcheck) {
		// TODO Auto-generated method stub
		
	}

	/** {@inheritDoc} */
	@Override
	public void setTranslate(final boolean translate) {
		// TODO Auto-generated method stub
		
	}

	/** {@inheritDoc} */
	@Override
	public void click() {
		MouseEvent event = new MouseEvent(
				new JPanel(), MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
				0, 0, 0, 1, false, 1);
		HtmlController.getInstance().onMouseClick(this, event, 0, 0);
	}

	/**
	 * No-op {@code focus()} for elements that don't have a backing widget.
	 * Per the HTMLOrSVGElement mixin every HTML element exposes {@code focus()};
	 * input/textarea override with their real implementations. The headless
	 * renderer doesn't have a focus concept for non-form elements so this is a
	 * silent stub — {@code focus} events still fire when explicitly dispatched.
	 */
	public void focus() {
	}

	/** No-op {@code blur()}; see {@link #focus()}. */
	public void blur() {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field onblur.</p>
	 */
	@Override
	public Object getOnblur() {
		return getCallable(this, "blur");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field onclick.</p>
	 */
	@Override
	public Object getOnclick() {
		return getCallable(this, "click");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field oncontextmenu.</p>
	 */
	@Override
	public Object getOncontextmenu() {
		return getCallable(this, "contextmenu");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field ondblclick.</p>
	 */
	@Override
	public Object getOndblclick() {
		return getCallable(this, "dblclick");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field onfocus.</p>
	 */
	@Override
	public Object getOnfocus() {
		return getCallable(this, "focus");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field onfocus.</p>
	 */
	@Override
	public Object getOnfullscreenchange() {
		return getCallable(this, "focus");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field onkeydown.</p>
	 */
	@Override
	public Object getOnkeydown() {
		return getCallable(this, "keydown");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field onkeypress.</p>
	 */
	@Override
	public Object getOnkeypress() {
		return getCallable(this, "keypress");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field onkeyup.</p>
	 */
	@Override
	public Object getOnkeyup() {
		return getCallable(this, "keyup");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field onmousedown.</p>
	 */
	@Override
	public Object getOnmousedown() {
		return getCallable(this, "mousedown");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field onmousemove.</p>
	 */
	@Override
	public Object getOnmousemove() {
		return getCallable(this, "mousemove");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field onmouseout.</p>
	 */
	@Override
	public Object getOnmouseout() {
		return getCallable(this, "mouseout");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field onmouseover.</p>
	 */
	@Override
	public Object getOnmouseover() {
		return getCallable(this, "mouseover");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field onmouseup.</p>
	 */
	@Override
	public Object getOnmouseup() {
		return getCallable(this, "mouseup");
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Getter for the field onchange.</p>
	 */
	@Override
	public Object getOnchange() {
		return getCallable(this, "change");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnabort() {
		return getCallable(this, "abort");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnanimationcancel() {
		return getCallable(this, "animationcancel");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnanimationend() {
		return getCallable(this, "animationend");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnanimationiteration() {
		return getCallable(this, "animationiteration");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnanimationstart() {
		return getCallable(this, "animationstart");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnauxclick() {
		return getCallable(this, "auxclick");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOncancel() {
		return getCallable(this, "cancel");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOncanplay() {

		return getCallable(this, "canplay");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOncanplaythrough() {
		return getCallable(this, "canplaythrough");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnclose() {
		return getCallable(this, "close");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOncuechange() {
		return getCallable(this, "cuechange");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOndrag() {
		return getCallable(this, "drag");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOndragend() {
		return getCallable(this, "dragend");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOndragenter() {
		return getCallable(this, "dragenter");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOndragexit() {
		return getCallable(this, "dragexit");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOndragleave() {
		return getCallable(this, "dragleave");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOndragover() {
		return getCallable(this, "dragover");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOndragstart() {
		return getCallable(this, "dragstart");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOndrop() {
		return getCallable(this, "drop");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOndurationchange() {
		return getCallable(this, "durationchange");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnemptied() {
		return getCallable(this, "emptied");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnended() {
		return getCallable(this, "ended");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnerror() {
		return getCallable(this, "error");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnfocusin() {
		return getCallable(this, "focusin");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnfocusout() {
		return getCallable(this, "focusout");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOngotpointercapture() {
		return getCallable(this, "gotpointercapture");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOninput() {
		return getCallable(this, "input");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOninvalid() {
		return getCallable(this, "invalid");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnload() {
		return getCallable(this, "load");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnloadeddata() {
		return getCallable(this, "loadeddata");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnloadedmetadata() {
		return getCallable(this, "loadedmetadata");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnloadend() {
		return getCallable(this, "loadend");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnloadstart() {
		return getCallable(this, "loadstart");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnlostpointercapture() {
		return getCallable(this, "lostpointercapture");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnmouseenter() {
		return getCallable(this, "mouseenter");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnmouseleave() {
		return getCallable(this, "mouseleave");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnpause() {
		return getCallable(this, "pause");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnplay() {
		return getCallable(this, "play");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnplaying() {
		return getCallable(this, "playing");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnpointercancel() {
		return getCallable(this, "pointercancel");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnpointerdown() {
		return getCallable(this, "pointerdown");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnpointerenter() {
		return getCallable(this, "pointerenter");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnpointerleave() {
		return getCallable(this, "pointerleave");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnpointermove() {
		return getCallable(this, "pointermove");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnpointerout() {
		return getCallable(this, "pointerout");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnpointerover() {
		return getCallable(this, "pointerover");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnpointerup() {
		return getCallable(this, "pointerup");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnprogress() {
		return getCallable(this, "progress");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnratechange() {
		return getCallable(this, "ratechange");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnreset() {
		return getCallable(this, "reset");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnresize() {
		return getCallable(this, "resize");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnscroll() {
		return getCallable(this, "scroll");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnsecuritypolicyviolation() {
		return getCallable(this, "securitypolicyviolation");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnseeked() {
		return getCallable(this, "seeked");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnseeking() {
		return getCallable(this, "seeking");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnselect() {
		return getCallable(this, "select");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnselectionchange() {
		return getCallable(this, "selectionchange");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnselectstart() {
		return getCallable(this, "selectstart");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnstalled() {
		return getCallable(this, "stalled");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnsubmit() {
		return getCallable(this, "submit");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnsuspend() {
		return getCallable(this, "suspend");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOntimeupdate() {
		return getCallable(this, "timeupdate");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOntoggle() {
		return getCallable(this, "toggle");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOntouchcancel() {
		return getCallable(this, "touchcancel");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOntouchend() {
		return getCallable(this, "touchend");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOntouchmove() {
		return getCallable(this, "touchmove");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOntouchstart() {
		return getCallable(this, "touchstart");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOntransitioncancel() {
		return getCallable(this, "transitioncancel");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOntransitionend() {
		return getCallable(this, "transitionend");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOntransitionrun() {
		return getCallable(this, "transitionrun");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOntransitionstart() {
		return getCallable(this, "transitionstart");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnvolumechange() {
		return getCallable(this, "volumechange");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnwaiting() {
		return getCallable(this, "waiting");
	}

	/** {@inheritDoc} */
	@Override
	public Object getOnwheel() {
		return getCallable(this, "wheel");
	}

	/** {@inheritDoc} */
	@Override
	public void setOnfocus(final Object onfocus) {
		addEventListener("focus", onfocus);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnblur(final Object onblur) {
		addEventListener("blur", onblur);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnclick(final Object onclick) {
		addEventListener("click", onclick);
	}

	/** {@inheritDoc} */
	@Override
	public void setOndblclick(final Object ondblclick) {
		addEventListener("dblclick", ondblclick);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnmousedown(final Object onmousedown) {
		addEventListener("mousedown", onmousedown);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnmouseup(final Object onmouseup) {
		addEventListener("mouseup", onmouseup);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnmouseover(final Object onmouseover) {
		addEventListener("mouseover", onmouseover);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnmousemove(final Object onmousemove) {
		addEventListener("mousemove", onmousemove);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnmouseout(final Object onmouseout) {
		addEventListener("mouseout", onmouseout);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnkeypress(final Object onkeypress) {
		addEventListener("keypress", onkeypress);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnkeydown(final Object onkeydown) {
		addEventListener("keydown", onkeydown);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnkeyup(final Object onkeyup) {
		addEventListener("keyup", onkeyup);
	}

	/** {@inheritDoc} */
	@Override
	public void setOncontextmenu(final Object oncontextmenu) {
		addEventListener("contextmenu", oncontextmenu);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnchange(final Object onchange) {
		addEventListener("change", onchange);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnabort(final Object onabort) {
		addEventListener("abort", onabort);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnwaiting(final Object onwaiting) {
		addEventListener("waiting", onwaiting);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnvolumechange(final Object onvolumechange) {
		addEventListener("volumechange", onvolumechange);
	}

	/** {@inheritDoc} */
	@Override
	public void setOntransitionstart(final Object ontransitionstart) {
		addEventListener("transitionstart", ontransitionstart);
	}

	/** {@inheritDoc} */
	@Override
	public void setOntransitionrun(final Object ontransitionrun) {
		addEventListener("transitionrun", ontransitionrun);
	}

	/** {@inheritDoc} */
	@Override
	public void setOntransitionend(final Object ontransitionend) {
		addEventListener("transitionend", ontransitionend);
	}

	/** {@inheritDoc} */
	@Override
	public void setOntransitioncancel(final Object ontransitioncancel) {
		addEventListener("transitioncancel", ontransitioncancel);
	}

	/** {@inheritDoc} */
	@Override
	public void setOntouchstart(final Object ontouchstart) {
		addEventListener("touchstart", ontouchstart);
	}

	/** {@inheritDoc} */
	@Override
	public void setOntoggle(final Object ontoggle) {
		addEventListener("toggle", ontoggle);
	}

	/** {@inheritDoc} */
	@Override
	public void setOntouchmove(final Object ontouchmove) {
		addEventListener("touchmove", ontouchmove);
	}

	/** {@inheritDoc} */
	@Override
	public void setOntouchend(final Object ontouchend) {
		addEventListener("touchend", ontouchend);
	}

	/** {@inheritDoc} */
	@Override
	public void setOntouchcancel(final Object ontouchcancel) {
		addEventListener("touchcancel", ontouchcancel);
	}

	/** {@inheritDoc} */
	@Override
	public void setOntimeupdate(final Object ontimeupdate) {
		addEventListener("timeupdate", ontimeupdate);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnsubmit(final Object onsubmit) {
		addEventListener("submit", onsubmit);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnstalled(final Object onstalled) {
		addEventListener("stalled", onstalled);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnselectstart(final Object onselectstart) {
		addEventListener("selectstart", onselectstart);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnselectionchange(final Object onselectionchange) {
		addEventListener("selectionchange", onselectionchange);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnselect(final Object onselect) {
		addEventListener("select", onselect);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnseeking(final Object onseeking) {
		addEventListener("seeking", onseeking);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnseeked(final Object onseeked) {
		addEventListener("seeked", onseeked);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnsecuritypolicyviolation(final Object onsecuritypolicyviolation) {
		addEventListener("securitypolicyviolation", onsecuritypolicyviolation);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnscroll(final Object onscroll) {
		addEventListener("scroll", onscroll);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnresize(final Object onresize) {
		addEventListener("resize", onresize);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnreset(final Object onreset) {
		addEventListener("reset", onreset);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnratechange(final Object onratechange) {
		addEventListener("ratechange", onratechange);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnprogress(final Object onprogress) {
		addEventListener("progress", onprogress);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnpointerup(final Object onpointerup) {
		addEventListener("pointerup", onpointerup);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnpointerover(final Object onpointerover) {
		addEventListener("pointerover", onpointerover);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnpointerout(final Object onpointerout) {
		addEventListener("pointerout", onpointerout);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnwheel(final Object onwheel) {
		addEventListener("wheel", onwheel);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnsuspend(final Object onsuspend) {
		addEventListener("suspend", onsuspend);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnpointermove(final Object onpointermove) {
		addEventListener("pointermove", onpointermove);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnpointerleave(final Object onpointerleave) {
		addEventListener("pointerleave", onpointerleave);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnpointerenter(final Object onpointerenter) {
		addEventListener("pointerenter", onpointerenter);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnpointerdown(final Object onpointerdown) {
		addEventListener("pointerdown", onpointerdown);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnpointercancel(final Object onpointercancel) {
		addEventListener("pointercancel", onpointercancel);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnplaying(final Object onplaying) {
		addEventListener("playing", onplaying);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnplay(final Object onplay) {
		addEventListener("play", onplay);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnpause(final Object onpause) {
		addEventListener("pause", onpause);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnmouseleave(final Object onmouseleave) {
		addEventListener("mouseleave", onmouseleave);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnmouseenter(final Object onmouseenter) {
		addEventListener("mouseenter", onmouseenter);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnauxclick(final Object onauxclick) {
		addEventListener("auxclick", onauxclick);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnlostpointercapture(final Object onlostpointercapture) {
		addEventListener("lostpointercapture", onlostpointercapture);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnloadstart(final Object onloadstart) {
		addEventListener("loadstart", onloadstart);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnloadend(final Object onloadend) {
		addEventListener("loadend", onloadend);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnloadedmetadata(final Object onloadedmetadata) {
		addEventListener("loadedmetadata", onloadedmetadata);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnloadeddata(final Object onloadeddata) {
		addEventListener("loadeddata", onloadeddata);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnload(final Object onload) {
		addEventListener("load", onload);
	}

	/** {@inheritDoc} */
	@Override
	public void setOninvalid(final Object oninvalid) {
		addEventListener("invalid", oninvalid);
	}

	/** {@inheritDoc} */
	@Override
	public void setOninput(final Object oninput) {
		addEventListener("input", oninput);
	}

	/** {@inheritDoc} */
	@Override
	public void setOngotpointercapture(final Object ongotpointercapture) {
		addEventListener("gotpointercapture", ongotpointercapture);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnfocusout(final Object onfocusout) {
		addEventListener("focusout", onfocusout);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnfocusin(final Object onfocusin) {
		addEventListener("focusin", onfocusin);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnerror(final Object onerror) {
		addEventListener("error", onerror);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnended(final Object onended) {
		addEventListener("ended", onended);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnemptied(final Object onemptied) {
		addEventListener("emptied", onemptied);
	}

	/** {@inheritDoc} */
	@Override
	public void setOndurationchange(final Object ondurationchange) {
		addEventListener("durationchange", ondurationchange);
	}

	/** {@inheritDoc} */
	@Override
	public void setOndrop(final Object ondrop) {
		addEventListener("drop", ondrop);
	}

	/** {@inheritDoc} */
	@Override
	public void setOndragstart(final Object ondragstart) {
		addEventListener("dragstart", ondragstart);
	}

	/** {@inheritDoc} */
	@Override
	public void setOndragover(final Object ondragover) {
		addEventListener("dragover", ondragover);
	}

	/** {@inheritDoc} */
	@Override
	public void setOndragleave(final Object ondragleave) {
		addEventListener("dragleave", ondragleave);
	}

	/** {@inheritDoc} */
	@Override
	public void setOndragexit(final Object ondragexit) {
		addEventListener("dragexit", ondragexit);
	}

	/** {@inheritDoc} */
	@Override
	public void setOndragenter(final Object ondragenter) {
		addEventListener("dragenter", ondragenter);
	}

	/** {@inheritDoc} */
	@Override
	public void setOndragend(final Object ondragend) {
		addEventListener("dragend", ondragend);
	}

	/** {@inheritDoc} */
	@Override
	public void setOndrag(final Object ondrag) {
		addEventListener("drag", ondrag);
	}

	/** {@inheritDoc} */
	@Override
	public void setOncuechange(final Object oncuechange) {
		addEventListener("cuechange", oncuechange);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnclose(final Object onclose) {
		addEventListener("close", onclose);
	}

	/** {@inheritDoc} */
	@Override
	public void setOncanplaythrough(final Object oncanplaythrough) {
		addEventListener("canplaythrough", oncanplaythrough);
	}

	/** {@inheritDoc} */
	@Override
	public void setOncanplay(final Object oncanplay) {
		addEventListener("canplay", oncanplay);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnanimationstart(final Object onanimationstart) {
		addEventListener("animationstart", onanimationstart);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnanimationiteration(final Object onanimationiteration) {
		addEventListener("animationiteration", onanimationiteration);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnanimationend(final Object onanimationend) {
		addEventListener("animationend", onanimationend);
	}

	/** {@inheritDoc} */
	@Override
	public void setOnanimationcancel(final Object onanimationcancel) {
		addEventListener("animationcancel", onanimationcancel);
	}

	/** {@inheritDoc} */
	@Override
	public void setOncancel(final Object oncancel) {
		addEventListener("cancel", oncancel);
	}

	/**
	 * <p>setOnoffline.</p>
	 *
	 * @param onoffline a {@link Object} object.
	 */
	public void setOnoffline(final Object onoffline) {
		// TODO Auto-generated method stub
	}

	/**
	 * <p>findStyleDeclarations.</p>
	 *
	 * @param elementName a {@link java.lang.String} object.
	 * @param classes an array of {@link java.lang.String} objects.
	 * @param mouseOver a {@link java.lang.Boolean } object.
	 * @return a {@link java.util.List} object.
	 */
	public final List<CSSStyleSheetImpl.SelectorEntry> findStyleDeclarations(final String elementName, final String[] classes, final boolean mouseOver) {
		final HTMLDocumentImpl doc = (HTMLDocumentImpl) this.document;

		if (doc == null) {
			return new ArrayList<>();
		}
		final StyleSheetAggregator ssa = doc.getStyleSheetAggregator();
		ssa.setDoc(doc);
		final List<CSSStyleSheetImpl.SelectorEntry> list = ssa.getActiveStyleDeclarations(this, elementName, classes, mouseOver);
		hasMouseOver = ssa.isMouseOver();
		return list;
	}

	/**
	 * Adds style sheet declarations applicable to this element. A properties object
	 * is created if necessary when the one passed is null.
	 * @param mouseOver a {@link java.lang.Boolean } object.
	 * @return a {@link CSSStyleDeclaration} object.
	 */
	private CSSStyleDeclaration addStyleSheetDeclarations(final boolean mouseOver, final String elementName) {

		final CSSStyleDeclarationImpl localStyleDeclarationState = new CSSStyleDeclarationImpl(this, true);
		final String classNames = getClassName();
		final String[] classNameArray = Strings.isNotBlank(classNames) ? Strings.split(classNames) : null;

		final List<CSSStyleSheetImpl.SelectorEntry> matchingRules = findStyleDeclarations(elementName, classNameArray, mouseOver);
		for (final CSSStyleSheetImpl.SelectorEntry entry : matchingRules) {
			localStyleDeclarationState.getProperties().addAll(entry.getRule().getStyle().getProperties());
		}
		return propertyValueProcessed(localStyleDeclarationState);
	}

	private CSSStyleDeclarationImpl propertyValueProcessed(final CSSStyleDeclarationImpl localStyleDeclarationState) {
		try {
			final List<PropertyCssInfo> properties3 = new ArrayList<>();
			final List<Property> properties = localStyleDeclarationState.getProperties();
			properties.forEach(prop -> {
				final CSSValueImpl propertyValue = prop.getValue();
				properties3.add(new PropertyCssInfo(prop.getName(), propertyValue != null ? propertyValue.getCssText() : null, prop.isImportant()));
			});

			properties3.forEach(prop -> localStyleDeclarationState.setPropertyValueProcessed(prop.getName(), prop.getValue(), prop.isImportant()));

			return localStyleDeclarationState;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "[object HTMLElement]";
	}
}
