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
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Shared reflection plumbing used by {@link DynamicMembers} and
 * {@link CssMembers} to expose JavaBean-style accessors and methods to
 * GraalJS without having to repeat the per-class boilerplate. Pure
 * {@code java.lang.reflect}; no polyglot recursion.
 */
final class MemberReflector {

    private MemberReflector() {}

    /** A bean getter ({@code getX}/{@code isX}) on {@code target} for {@code key}, or null. */
    static Method findGetter(final Object target, final String key) {
        if (key == null || key.isEmpty()) return null;
        final String cap = capitalize(key);
        for (final String prefix : new String[]{"get", "is"}) {
            try {
                final Method m = target.getClass().getMethod(prefix + cap);
                if (!m.getReturnType().equals(void.class) && m.getParameterCount() == 0) {
                    return m;
                }
            } catch (final NoSuchMethodException ignored) {
                // try next prefix
            }
        }
        return null;
    }

    /** A single-arg bean setter ({@code setX}) on {@code target} for {@code key}, or null. */
    static Method findSetter(final Object target, final String key) {
        if (key == null || key.isEmpty()) return null;
        final String setterName = "set" + capitalize(key);
        for (final Method m : target.getClass().getMethods()) {
            if (m.getName().equals(setterName) && m.getParameterCount() == 1) {
                return m;
            }
        }
        return null;
    }

    /** All public methods named {@code key} (any arity); empty array on miss. */
    static Method[] findMethods(final Object target, final String key) {
        return Arrays.stream(target.getClass().getMethods())
                .filter(m -> m.getName().equals(key))
                .toArray(Method[]::new);
    }

    /**
     * Wraps overloaded methods as a {@link ProxyExecutable}; on invoke picks
     * the first overload whose arity matches and converts each polyglot
     * {@link Value} argument with {@link Value#as}. Most-specific declarations
     * are tried first.
     */
    static ProxyExecutable makeExecutable(final Object target, final Method[] methods) {
        return args -> {
            final List<Method> sorted = new ArrayList<>(Arrays.asList(methods));
            sorted.sort((a, b) -> {
                if (a.getDeclaringClass().isAssignableFrom(b.getDeclaringClass())) return 1;
                if (b.getDeclaringClass().isAssignableFrom(a.getDeclaringClass())) return -1;
                return 0;
            });
            ReflectiveOperationException lastError = null;
            for (final Method m : sorted) {
                if (m.getParameterCount() != args.length) continue;
                try {
                    final Class<?>[] paramTypes = m.getParameterTypes();
                    final Object[] javaArgs = new Object[args.length];
                    for (int i = 0; i < args.length; i++) {
                        javaArgs[i] = coerce(args[i], paramTypes[i]);
                    }
                    return m.invoke(target, javaArgs);
                } catch (final ReflectiveOperationException e) {
                    lastError = e;
                }
            }
            if (lastError != null) {
                throw new RuntimeException(lastError);
            }
            throw new RuntimeException("No overload of " + methods[0].getName()
                    + " accepts " + args.length + " arguments");
        };
    }

    /** Bean-style member names plus method names declared on {@code target}'s class. */
    static Set<String> reflectiveMemberNames(final Object target) {
        final Set<String> keys = new LinkedHashSet<>();
        for (final Method m : target.getClass().getMethods()) {
            if (m.getDeclaringClass() == Object.class) continue;
            final String name = m.getName();
            final int paramCount = m.getParameterCount();
            if (paramCount == 0 && name.length() > 3 && name.startsWith("get")) {
                keys.add(decapitalize(name.substring(3)));
            } else if (paramCount == 0 && name.length() > 2 && name.startsWith("is")) {
                keys.add(decapitalize(name.substring(2)));
            } else {
                keys.add(name);
            }
        }
        return keys;
    }

    /** Convert a polyglot Value to the requested Java type. */
    static Object coerce(final Value value, final Class<?> targetType) {
        if (value == null) return null;
        if (targetType == Object.class) {
            return value.isHostObject() ? value.asHostObject() : value;
        }
        return value.as(targetType);
    }

    /** Unwrap a Value to its host representation; preserve Values for callables. */
    static Object unwrap(final Value value) {
        if (value == null) return null;
        if (value.isHostObject()) return value.asHostObject();
        if (value.isString()) return value.asString();
        if (value.isBoolean()) return value.asBoolean();
        if (value.isNumber()) {
            if (value.fitsInInt()) return value.asInt();
            if (value.fitsInLong()) return value.asLong();
            return value.asDouble();
        }
        if (value.isNull()) return null;
        return value;
    }

    private static String capitalize(final String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String decapitalize(final String s) {
        if (s.isEmpty()) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
