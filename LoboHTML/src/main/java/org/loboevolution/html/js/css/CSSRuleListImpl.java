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

package org.loboevolution.html.js.css;

import lombok.Getter;
import lombok.Setter;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyIterable;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.htmlunit.cssparser.dom.AbstractCSSRuleImpl;
import org.loboevolution.css.CSSRuleList;
import org.loboevolution.css.CSSStyleRule;
import org.loboevolution.html.js.engine.MemberReflector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>CSSRuleListImpl class.</p>
 *
 * <p>Implements {@link ProxyObject} + {@link ProxyIterable} so a
 * {@code CSSRuleList} is the browser array-like it should be: {@code
 * sheet.cssRules[0]}, {@code cssRules.length}, {@code for..of} and its
 * {@code item()} method. Previously only {@code item(i)}/{@code length} were
 * reachable and {@code cssRules[i]} was {@code undefined}, so the common
 * {@code styleSheets[0].cssRules[0].style} pattern threw and aborted the whole
 * stylesheet-rule test family. Rhino ignores the polyglot proxy interfaces.</p>
 */
public class CSSRuleListImpl implements CSSRuleList, ProxyObject, ProxyIterable {

    @Getter
    @Setter
    private List<CSSStyleRule> styleRuleList = new ArrayList<>();

    private final org.htmlunit.cssparser.dom.CSSRuleListImpl cssRuleList;

    public CSSRuleListImpl(final org.htmlunit.cssparser.dom.CSSRuleListImpl cssRuleList) {
        this.cssRuleList = cssRuleList;
        addStyleRule(cssRuleList);
    }

    /** {@inheritDoc} */
    @Override
    public CSSStyleRule item(final int index) {
        return styleRuleList.get(index);
    }

    /** {@inheritDoc} */
    @Override
    public long getLength() {
        return styleRuleList.size();
    }

    /**
     * <p> addStyleRule. </p>
     * @param newList a {@link org.htmlunit.cssparser.dom.CSSRuleListImpl} object.
     */
    public void addStyleRule(final org.htmlunit.cssparser.dom.CSSRuleListImpl newList) {
        List<AbstractCSSRuleImpl> ruls;
        styleRuleList.clear();
        ruls = newList != null ? newList.getRules() : cssRuleList.getRules();
        ruls.forEach(rule -> {

            if (rule instanceof org.htmlunit.cssparser.dom.CSSStyleRuleImpl) {
                styleRuleList.add(new CSSStyleRuleImpl(rule));
            }
            if (rule instanceof org.htmlunit.cssparser.dom.CSSImportRuleImpl) {
                styleRuleList.add(new CSSImportRuleImpl((org.htmlunit.cssparser.dom.CSSImportRuleImpl) rule));
            }

            if (rule instanceof org.htmlunit.cssparser.dom.CSSFontFaceRuleImpl) {
                styleRuleList.add(new CSSFontFaceRuleImpl((org.htmlunit.cssparser.dom.CSSFontFaceRuleImpl) rule));
            }

            if (rule instanceof org.htmlunit.cssparser.dom.CSSPageRuleImpl) {
                styleRuleList.add(new CSSPageRuleImpl((org.htmlunit.cssparser.dom.CSSPageRuleImpl) rule));
            }

            if (rule instanceof org.htmlunit.cssparser.dom.CSSCharsetRuleImpl) {
                styleRuleList.add(new CSSCharsetRuleImpl(rule));
            }

            if (rule instanceof org.htmlunit.cssparser.dom.CSSUnknownRuleImpl unknownRule) {
                if (unknownRule.getCssText().startsWith("@keyframes")) {
                    styleRuleList.add(new CSSKeyFramesRuleImpl(this, rule));
                }
            }

            if (rule instanceof org.htmlunit.cssparser.dom.CSSMediaRuleImpl) {
                styleRuleList.add(new CSSMediaRuleImpl(rule));
            }
        });
    }

    // --- GraalJS ProxyIterable: for..of / Array.from over the rules ---

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

    // --- GraalJS ProxyObject: numeric index, length, item() ---

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

    @Override
    public String toString() {
        return "[object CSSRuleList]";
    }
}
