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
     * Wraps overloaded methods as a {@link ProxyExecutable}. On invoke it keeps
     * the arity-matching overloads and picks the one whose parameter types best
     * fit the actual argument types (see {@link #overloadScore}) instead of just
     * the first arity match — so, e.g., a host DOM-object argument selects the
     * overload declaring that type rather than a same-arity {@code String}
     * overload. If the best candidate fails to coerce or invoke, the next
     * best-scoring one is tried before giving up.
     */
    static ProxyExecutable makeExecutable(final Object target, final Method[] methods) {
        return args -> {
            final List<Method> candidates = new ArrayList<>();
            for (final Method m : methods) {
                if (m.getParameterCount() == args.length) {
                    candidates.add(m);
                }
            }
            candidates.sort((a, b) -> {
                final int byType = Integer.compare(overloadScore(b, args), overloadScore(a, args));
                if (byType != 0) return byType;
                // Tie-break: most-derived declaring class first (previous behaviour).
                if (a.getDeclaringClass().isAssignableFrom(b.getDeclaringClass())) return 1;
                if (b.getDeclaringClass().isAssignableFrom(a.getDeclaringClass())) return -1;
                return 0;
            });
            RuntimeException lastError = null;
            for (final Method m : candidates) {
                try {
                    final Class<?>[] paramTypes = m.getParameterTypes();
                    final Object[] javaArgs = new Object[args.length];
                    for (int i = 0; i < args.length; i++) {
                        javaArgs[i] = coerce(args[i], paramTypes[i]);
                    }
                    return m.invoke(target, javaArgs);
                } catch (final ReflectiveOperationException e) {
                    lastError = new RuntimeException(e);
                } catch (final RuntimeException e) {
                    // coerce() can reject an argument that doesn't fit this
                    // overload; fall through to the next best-scoring one.
                    lastError = e;
                }
            }
            if (lastError != null) {
                throw lastError;
            }
            throw new RuntimeException("No overload of " + methods[0].getName()
                    + " accepts " + args.length + " arguments");
        };
    }

    /** Sums {@link #argScore} across all parameters; higher means a better fit. */
    private static int overloadScore(final Method m, final Value[] args) {
        final Class<?>[] paramTypes = m.getParameterTypes();
        int score = 0;
        for (int i = 0; i < paramTypes.length; i++) {
            score += argScore(args[i], paramTypes[i]);
        }
        return score;
    }

    /**
     * Compatibility score for passing a single polyglot {@code value} to a Java
     * parameter of type {@code t}. Exact matches score highest, assignable or
     * coercible matches lower, clearly wrong matches negative — so the summed
     * {@link #overloadScore} prefers the most type-appropriate overload.
     */
    private static int argScore(final Value value, final Class<?> t) {
        if (value == null || value.isNull()) {
            return t.isPrimitive() ? -100 : 1;
        }
        if (value.isString()) {
            if (t == String.class) return 10;
            if (t == char.class || t == Character.class) return 3;
            if (t == CharSequence.class || t == Object.class) return 2;
            return -50;
        }
        if (value.isBoolean()) {
            if (t == boolean.class || t == Boolean.class) return 10;
            if (t == Object.class) return 2;
            return -50;
        }
        if (value.isNumber()) {
            if (t == int.class || t == Integer.class) return value.fitsInInt() ? 10 : 4;
            if (t == long.class || t == Long.class) return value.fitsInLong() ? 9 : 4;
            if (t == double.class || t == Double.class) return 8;
            if (t == float.class || t == Float.class) return 7;
            if (t == short.class || t == Short.class) return value.fitsInShort() ? 6 : 2;
            if (t == byte.class || t == Byte.class) return value.fitsInByte() ? 6 : 2;
            if (t == Number.class || t == Object.class) return 3;
            if (t == String.class) return -10;
            return -50;
        }
        if (value.isHostObject()) {
            final Object host = value.asHostObject();
            if (host == null) return t.isPrimitive() ? -100 : 1;
            if (t == host.getClass()) return 14;
            if (t.isInstance(host)) return 10;
            if (t == Object.class) return 2;
            return -50;
        }
        if (value.canExecute()) {
            return (t.isInterface() || t == Object.class) ? 5 : -20;
        }
        return t == Object.class ? 1 : 0;
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
