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

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

/**
 * {@link JsEngine} backed by GraalJS via the polyglot API. Built with the
 * {@link LoboGraalHostAccess} policy so that DOM bridge classes appear in JS
 * the way they do under Rhino (JavaBean reflection, list/iterable interop,
 * functional-interface lambdas).
 */
public class GraalJsEngine implements JsEngine {

    private static final String LANG = "js";

    private final Context context;

    public GraalJsEngine() {
        this(LoboGraalHostAccess.newContextBuilder().build());
    }

    public GraalJsEngine(final Context context) {
        this.context = context;
    }

    @Override
    public Object eval(final String source, final String sourceName) {
        final Source src = Source.newBuilder(LANG, source, sourceName == null ? "<anonymous>" : sourceName)
                .buildLiteral();
        final Value result = context.eval(src);
        return result;
    }

    @Override
    public void putGlobal(final String name, final Object value) {
        context.getBindings(LANG).putMember(name, value);
    }

    @Override
    public Object call(final Object callback, final Object... args) {
        if (callback == null) {
            return null;
        }
        final Value fn = (callback instanceof Value v) ? v : context.asValue(callback);
        if (!fn.canExecute()) {
            return null;
        }
        return fn.execute(args == null ? new Object[0] : args);
    }

    @Override
    public void close() {
        context.close();
    }

    /** Exposed for migration code that still needs the underlying polyglot context. */
    public Context getContext() {
        return context;
    }
}
