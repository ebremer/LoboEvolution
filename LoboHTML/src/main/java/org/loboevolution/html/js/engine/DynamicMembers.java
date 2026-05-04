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

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Window-style dynamic-member helper. Reads check the dynamic bag first
 * (so {@code window.foo = x; window.foo} round-trips to {@code x}), then
 * fall through to JavaBean reflection. Writes try a setter first, then
 * stash unknown names in the bag — strict-mode JS sees no failure either
 * way.
 *
 * <p>The CSS object has different semantics — see {@link CssMembers}.
 */
public final class DynamicMembers {

    private final Object target;
    private final Map<String, Object> dynamic = new ConcurrentHashMap<>();

    public DynamicMembers(final Object target) {
        this.target = target;
    }

    public Object getMember(final String key) {
        if (dynamic.containsKey(key)) {
            return dynamic.get(key);
        }
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
        return null;
    }

    public void putMember(final String key, final Value value) {
        final Method setter = MemberReflector.findSetter(target, key);
        if (setter != null) {
            try {
                final Object javaArg = MemberReflector.coerce(value, setter.getParameterTypes()[0]);
                setter.invoke(target, javaArg);
                return;
            } catch (final ReflectiveOperationException e) {
                // Fall through to dynamic storage so the write doesn't surface
                // as a strict-mode TypeError.
            }
        }
        dynamic.put(key, MemberReflector.unwrap(value));
    }

    public boolean hasMember(final String key) {
        if (dynamic.containsKey(key)) return true;
        return MemberReflector.findGetter(target, key) != null
                || MemberReflector.findSetter(target, key) != null
                || MemberReflector.findMethods(target, key).length > 0;
    }

    public Object getMemberKeys() {
        final Set<String> keys = new LinkedHashSet<>(dynamic.keySet());
        keys.addAll(MemberReflector.reflectiveMemberNames(target));
        return keys.toArray(new String[0]);
    }
}
