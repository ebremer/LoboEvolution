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
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * Wraps a GraalJS callable {@link Value} in something that satisfies Rhino's
 * {@link org.mozilla.javascript.Function} contract. Necessary because plenty
 * of Lobo APIs declare {@code Function}-typed parameters (the W3C event
 * handlers, {@code setOnload}, {@code setOnclick}, …) and GraalJS's standard
 * host interop has no built-in conversion from a JS function to Rhino's
 * specific {@code Function} interface — calls fail with {@code "Cannot
 * convert ... to Java type 'org.mozilla.javascript.Function': Unsupported
 * target type"} and the script aborts.
 *
 * <p>Registered as a target-type mapping in {@link LoboGraalHostAccess}, so
 * conversion is automatic everywhere a host method takes {@code Function}.
 * {@link BaseFunction} provides the {@link Scriptable} boilerplate; we only
 * need to override invocation to forward into the polyglot value.
 */
public final class GraalRhinoFunctionAdapter extends BaseFunction {

    private final Value graalCallable;

    GraalRhinoFunctionAdapter(final Value graalCallable) {
        this.graalCallable = graalCallable;
    }

    /**
     * Exposes the underlying polyglot value so {@code GraalJsEngine.call}
     * can dispatch through it directly — when an event listener was
     * registered through a Function-typed parameter (so the adapter wrapped
     * the JS callable), later dispatch paths like
     * {@code EventTargetImpl.invokeCallback} need the original Value to
     * execute, not the adapter wrapper which {@code Value.canExecute}
     * doesn't recognise.
     */
    public Value getGraalCallable() {
        return graalCallable;
    }

    @Override
    public Object call(final Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
        final Value result = graalCallable.execute(args == null ? new Object[0] : args);
        if (result == null || result.isNull()) return null;
        if (result.isHostObject()) return result.asHostObject();
        return result;
    }

    @Override
    public Scriptable construct(final Context cx, final Scriptable scope, final Object[] args) {
        // Rhino occasionally invokes construct() for `new fn()`. Forward to
        // newInstance — guest-language behaviour matches enough for the
        // legacy paths Lobo actually exercises.
        final Value newInstance = graalCallable.newInstance(args == null ? new Object[0] : args);
        return newInstance.isHostObject() && newInstance.asHostObject() instanceof Scriptable s ? s : null;
    }
}
