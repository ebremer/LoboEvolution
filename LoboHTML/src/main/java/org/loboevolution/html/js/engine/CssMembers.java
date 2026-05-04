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
import org.loboevolution.css.CSSStyleDeclaration;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * CSS-specific {@link DynamicMembers} variant. Real browsers expose a
 * {@code CSSStyleDeclaration} as a dynamic property bag where every camelCase
 * JS property name maps to the corresponding kebab-case CSS property, e.g.
 * {@code style.backgroundClip} ⇄ {@code background-clip}. The default
 * {@link DynamicMembers} stashes unknown writes in a HashMap; for CSS we
 * route them through {@link CSSStyleDeclaration#setProperty} /
 * {@link CSSStyleDeclaration#getPropertyValue} so the value actually applies
 * to the element and round-trips through the underlying CSS store rather
 * than a side bag.
 *
 * <p>Reflection takes precedence on both reads and writes: bean accessors
 * like {@code setBackgroundImage} have side effects ({@code BackgroundImageSetter},
 * {@code informLookInvalid}) that the renderer needs. Only when reflection
 * misses do we fall through to the property-store path.
 */
public final class CssMembers {

    private final CSSStyleDeclaration target;

    public CssMembers(final CSSStyleDeclaration target) {
        this.target = target;
    }

    public Object getMember(final String key) {
        // Reflection-first so bean getters with derived/computed values win.
        final Method getter = MemberReflector.findGetter(target, key);
        if (getter != null) {
            try {
                return getter.invoke(target);
            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        final Method[] methods = MemberReflector.findMethods(target, key);
        if (methods.length > 0) {
            return MemberReflector.makeExecutable(target, methods);
        }
        // Fall through to the CSS property store. getPropertyValue returns ""
        // for unset properties, mirroring real browsers' style.unknownProp.
        return target.getPropertyValue(camelToKebab(key));
    }

    public void putMember(final String key, final Value value) {
        final Method setter = MemberReflector.findSetter(target, key);
        if (setter != null) {
            try {
                final Object javaArg = MemberReflector.coerce(value, setter.getParameterTypes()[0]);
                setter.invoke(target, javaArg);
                return;
            } catch (final ReflectiveOperationException e) {
                // Fall through.
            }
        }
        // Unknown name: route to setProperty with kebab conversion so the
        // CSS property store holds it. Subsequent reads via getMember will
        // see it through getPropertyValue.
        target.setProperty(camelToKebab(key), value.isNull() ? "" : value.asString());
    }

    public boolean hasMember(final String key) {
        if (MemberReflector.findGetter(target, key) != null) return true;
        if (MemberReflector.findSetter(target, key) != null) return true;
        if (MemberReflector.findMethods(target, key).length > 0) return true;
        // Anything is fair game in a CSS style bag — claim true so writes
        // via putMember always take the success path under strict mode.
        return true;
    }

    public Object getMemberKeys() {
        final Set<String> keys = new LinkedHashSet<>(MemberReflector.reflectiveMemberNames(target));
        // Could also enumerate the target's set CSS properties here, but the
        // reflection-derived names already cover all the common cases real
        // scripts iterate.
        return keys.toArray(new String[0]);
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
}
