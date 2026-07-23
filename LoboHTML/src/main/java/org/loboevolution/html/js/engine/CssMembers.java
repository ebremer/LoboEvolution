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
package org.loboevolution.html.js.engine;

import org.graalvm.polyglot.Value;
import org.htmlunit.cssparser.util.CSSProperties;
import org.loboevolution.css.CSSStyleDeclaration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * CSS-specific member proxy implementing the {@code CSSStyleDeclaration} half of
 * the CSSOM object model, where the distinction between {@code undefined},
 * {@code ""} and {@code null} matters:
 *
 * <ul>
 *   <li>A <b>recognized</b> CSS property — a bean accessor exists, or the
 *       kebab-cased name is in the {@link CSSProperties} registry — always reads
 *       as its value or {@code ""}, never {@code null}. So {@code style.margin}
 *       with no margin set is {@code ""} ({@code typeof === "string"}), matching
 *       browsers, not the Java {@code null} the underlying store returns.</li>
 *   <li>A <b>numeric</b> key is an indexed accessor: {@code style[0]} returns the
 *       n-th set property's name via {@link CSSStyleDeclaration#item(int)}; an
 *       out-of-range index is not a member (reads as {@code undefined}).</li>
 *   <li>An <b>unrecognized</b> name is not a member — {@link #hasMember} returns
 *       {@code false}, so it reads as {@code undefined}, the way {@code style.foo}
 *       does in a browser. This holds regardless of whether a same-named entry
 *       exists in the underlying store (e.g. an inline {@code style="htmlunit:x"}
 *       parses into the store but is still not a camelCase member).</li>
 * </ul>
 *
 * <p>Reflection takes precedence on writes: bean setters like
 * {@code setBackgroundImage} have side effects ({@code BackgroundImageSetter},
 * {@code informLookInvalid}) the renderer needs. On reads reflection wins too,
 * but a {@code null} from a recognized property's getter is normalised to
 * {@code ""}. A write to a read-only CSSOM attribute (e.g. {@code length}) is
 * rejected, so {@code 'use strict'; style.length = 1} throws as it does in a
 * browser rather than silently minting a bogus {@code length} property.
 *
 * <p>Note: a genuinely unknown assignment ({@code style.fooBar = 'x'}) cannot be
 * modelled as a per-object JS expando here because the wrapper is re-created
 * while the declaration is empty (see {@code HTMLElementImpl.getStyle}); it is
 * routed to the property store like any other write, but — not being a
 * recognized property — it does not read back through the camelCase accessor.
 */
public final class CssMembers {

    /**
     * Kebab-cased names of every CSS property Lobo recognizes, harvested once
     * from the {@link CSSProperties} constant registry (the same source the
     * style impl draws its bean accessors from). Decides whether a getterless
     * camelCase name is a real property (reads {@code ""} when unset) or an
     * unknown name (reads {@code undefined}).
     */
    private static final Set<String> KNOWN_PROPERTIES = harvestKnownProperties();

    private final CSSStyleDeclaration target;

    public CssMembers(final CSSStyleDeclaration target) {
        this.target = target;
    }

    public Object getMember(final String key) {
        final int idx = asIndex(key);
        if (idx != Integer.MIN_VALUE) {
            return (idx >= 0 && idx < target.getLength()) ? target.item(idx) : null;
        }
        // Reflection-first so bean getters with derived/computed values win.
        final Method getter = MemberReflector.findGetter(target, key);
        if (getter != null) {
            try {
                final Object value = getter.invoke(target);
                // A recognized property never reads as null: browsers report ""
                // (typeof "string") for an unset property, not null (typeof "object").
                return value != null ? value : "";
            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        final Method[] methods = MemberReflector.findMethods(target, key);
        if (methods.length > 0) {
            return MemberReflector.makeExecutable(target, methods);
        }
        // Getterless but recognized (e.g. background-clip, transform): read via
        // the store, coercing the store's null to "".
        if (isKnownProperty(camelToKebab(key))) {
            final String value = target.getPropertyValue(camelToKebab(key));
            return value != null ? value : "";
        }
        // Unknown name: hasMember() reports false, so this is normally never
        // reached for a read; return null defensively.
        return null;
    }

    public void putMember(final String key, final Value value) {
        final Method setter = MemberReflector.findSetter(target, key);
        if (setter != null) {
            final Class<?> paramType = setter.getParameterTypes()[0];
            try {
                // CSSOM camelCase setters are [LegacyNullToEmptyString]: assigning
                // null clears the property ("") rather than storing the coerced
                // string "null".
                final Object javaArg = (value.isNull() && paramType == String.class)
                        ? "" : MemberReflector.coerce(value, paramType);
                setter.invoke(target, javaArg);
                return;
            } catch (final ReflectiveOperationException e) {
                // Fall through.
            }
        }
        if (isKnownProperty(camelToKebab(key))) {
            target.setProperty(camelToKebab(key), asCssString(value));
            return;
        }
        // A getter with no setter and no matching property is a read-only CSSOM
        // attribute (length, parentRule); assigning to it must throw so strict
        // mode reports a TypeError, exactly as browsers do — and so we never
        // fabricate a phantom "length" CSS property.
        if (MemberReflector.findGetter(target, key) != null) {
            throw new UnsupportedOperationException("Cannot assign to read-only CSSStyleDeclaration." + key);
        }
        // Genuinely unknown name: route to the store so the write does not surface
        // as a strict-mode error (real browsers accept it as an expando). It will
        // not read back through the camelCase accessor, matching CSSOM.
        target.setProperty(camelToKebab(key), asCssString(value));
    }

    public boolean hasMember(final String key) {
        final int idx = asIndex(key);
        if (idx != Integer.MIN_VALUE) {
            return idx >= 0 && idx < target.getLength();
        }
        if (MemberReflector.findGetter(target, key) != null) return true;
        if (MemberReflector.findSetter(target, key) != null) return true;
        if (MemberReflector.findMethods(target, key).length > 0) return true;
        // A recognized property is a member; any other name is not, so it reads
        // as undefined the way real browsers expose only supported properties.
        return isKnownProperty(camelToKebab(key));
    }

    public Object getMemberKeys() {
        final Set<String> keys = new LinkedHashSet<>(MemberReflector.reflectiveMemberNames(target));
        // Enumerating every set CSS property (for..in over shorthand longhands)
        // is a separate concern served by the property store.
        return keys.toArray(new String[0]);
    }

    private static boolean isKnownProperty(final String kebab) {
        return KNOWN_PROPERTIES.contains(kebab);
    }

    /**
     * Parses a JS array-style key ({@code "0"}, {@code "12"}, {@code "-1"}) to an
     * int, or returns {@link Integer#MIN_VALUE} when {@code key} is not a plain
     * integer (i.e. it is a named member, not an index). Only all-digit strings
     * (with an optional leading {@code -}) are treated as indices; no CSS
     * property name is numeric, so this never shadows a real property.
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
     * Converts an assigned JS value to the string the property store should hold,
     * mirroring JS {@code String(value)} for the common cases so
     * {@code style.zIndex = 2.0} stores {@code "2"} rather than throwing (the old
     * {@code value.asString()} rejected any non-string). {@code null}/{@code
     * undefined} clear the property ({@code ""}).
     */
    private static String asCssString(final Value value) {
        if (value == null || value.isNull()) return "";
        if (value.isString()) return value.asString();
        if (value.isBoolean()) return String.valueOf(value.asBoolean());
        if (value.isNumber()) {
            if (value.fitsInLong()) return Long.toString(value.asLong());
            return Double.toString(value.asDouble());
        }
        return value.toString();
    }

    /**
     * {@code backgroundClip} → {@code background-clip}. Standard CSS DOM
     * camelCase ↔ kebab-case mapping; vendor prefixes ({@code WebkitFoo} →
     * {@code -webkit-foo}) are out of scope here and would need a leading
     * dash.
     */
    public static String camelToKebab(final String camel) {
        final StringBuilder out = new StringBuilder(camel.length() + 4);
        for (int i = 0; i < camel.length(); i++) {
            final char c = camel.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) out.append('-');
                out.append(Character.toLowerCase(c));
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    /** Collect every String constant declared on {@link CSSProperties}. */
    private static Set<String> harvestKnownProperties() {
        final Set<String> names = new LinkedHashSet<>();
        for (final Field f : CSSProperties.class.getFields()) {
            if (f.getType() == String.class) {
                try {
                    final Object value = f.get(null);
                    if (value != null) names.add((String) value);
                } catch (final IllegalAccessException ignored) {
                    // interface constants are public; unreachable in practice
                }
            }
        }
        return names;
    }
}
