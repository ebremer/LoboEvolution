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

import org.loboevolution.js.LoboContextFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * {@link JsEngine} backed by the project's vendored Rhino fork. The semantics
 * match {@link org.loboevolution.html.js.Executor} so that selecting Rhino via
 * {@link JsEngineFactory} produces the same behaviour as the legacy code path.
 */
public class RhinoJsEngine implements JsEngine {

    private final ContextFactory factory;
    private final Scriptable scope;

    public RhinoJsEngine() {
        this(new LoboContextFactory());
    }

    public RhinoJsEngine(final ContextFactory factory) {
        this.factory = factory;
        final Context ctx = factory.enterContext();
        try {
            this.scope = ctx.initStandardObjects();
        } finally {
            Context.exit();
        }
    }

    /**
     * Wraps an externally-managed scope. Used by the script-element bridge so
     * a JsEngine can run scripts against the per-document scope built by
     * {@code WindowImpl} (which already has all the DOM globals defined on it).
     */
    public RhinoJsEngine(final ContextFactory factory, final Scriptable scope) {
        this.factory = factory;
        this.scope = scope;
    }

    @Override
    public Object eval(final String source, final String sourceName) {
        final Context ctx = factory.enterContext();
        try {
            return ctx.evaluateString(scope, source, sourceName, 1, null);
        } finally {
            Context.exit();
        }
    }

    @Override
    public void putGlobal(final String name, final Object value) {
        final Context ctx = factory.enterContext();
        try {
            ScriptableObject.putProperty(scope, name, Context.javaToJS(value, scope));
        } finally {
            Context.exit();
        }
    }

    @Override
    public Object call(final Object callback, final Object... args) {
        if (!(callback instanceof Function fn)) {
            return null;
        }
        final Context ctx = factory.enterContext();
        try {
            return fn.call(ctx, scope, scope, args == null ? new Object[0] : args);
        } finally {
            Context.exit();
        }
    }

    @Override
    public void close() {
        // Rhino contexts are scoped per call (enter/exit pattern), so there is
        // nothing engine-wide to release.
    }

    /** Exposed for migration code that still needs the underlying scope. */
    public Scriptable getScope() {
        return scope;
    }

    /** Exposed for migration code that still needs the underlying factory. */
    public ContextFactory getFactory() {
        return factory;
    }
}
