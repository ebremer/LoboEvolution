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

package org.loboevolution.js.engine;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Test;
import org.loboevolution.html.js.engine.LoboGraalHostAccess;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 1 of the GraalJS migration: prove the {@link LoboGraalHostAccess}
 * policy reflects ordinary JavaBean-style Java objects to JavaScript using
 * the same conventions Rhino uses.
 */
class LoboGraalHostAccessTest {

    /** Plain JavaBean used as the host object. No GraalJS-specific annotations. */
    public static class Bean {
        private String name = "initial";
        private int count = 0;
        private final List<String> items = Arrays.asList("a", "b", "c");

        public String getName() { return name; }
        public void setName(final String name) { this.name = name; }

        public int getCount() { return count; }
        public void setCount(final int count) { this.count = count; }

        public List<String> getItems() { return items; }

        public String greet(final String who) { return "hello " + who; }
    }

    private Context newContext() {
        return LoboGraalHostAccess.newContextBuilder().build();
    }

    @Test
    void beanGetterReadsAsJsProperty() {
        try (Context ctx = newContext()) {
            ctx.getBindings("js").putMember("bean", new Bean());
            final Value name = ctx.eval("js", "bean.name");
            assertEquals("initial", name.asString());
        }
    }

    @Test
    void beanSetterWritesAsJsProperty() {
        try (Context ctx = newContext()) {
            final Bean bean = new Bean();
            ctx.getBindings("js").putMember("bean", bean);
            ctx.eval("js", "bean.name = 'changed'; bean.count = 42;");
            assertEquals("changed", bean.getName());
            assertEquals(42, bean.getCount());
        }
    }

    @Test
    void publicMethodInvokes() {
        try (Context ctx = newContext()) {
            ctx.getBindings("js").putMember("bean", new Bean());
            final Value greeting = ctx.eval("js", "bean.greet('world')");
            assertEquals("hello world", greeting.asString());
        }
    }

    @Test
    void listIsIterableAndIndexable() {
        try (Context ctx = newContext()) {
            ctx.getBindings("js").putMember("bean", new Bean());
            final Value first = ctx.eval("js", "bean.items[0]");
            assertEquals("a", first.asString());
            final Value joined = ctx.eval("js",
                    "let s=''; for (const x of bean.items) s += x; s");
            assertEquals("abc", joined.asString());
        }
    }

    @Test
    void rhinoFunctionAdapterBridgesJsCallable() {
        // Lobo APIs declaring org.mozilla.javascript.Function parameters
        // (setOnload, setOnclick, …) must accept JS callables transparently.
        // The targetTypeMapping in LoboGraalHostAccess produces a
        // GraalRhinoFunctionAdapter on demand. Verify a host method taking
        // Function actually receives one.
        try (Context ctx = newContext()) {
            ctx.getBindings("js").putMember("sink", new RhinoFunctionSink());
            ctx.eval("js", "sink.set(function() { return 42; })");
            final org.mozilla.javascript.Function f = RhinoFunctionSink.last;
            assertNotNull(f);
            // Invoke the wrapped JS function via Rhino's Function.call(ctx, ...)
            final org.mozilla.javascript.Context rhinoCtx =
                    org.mozilla.javascript.Context.enter();
            try {
                final Object result = f.call(rhinoCtx, null, null, new Object[0]);
                assertNotNull(result);
                // Result is a polyglot Value or Number wrapping 42
                if (result instanceof org.graalvm.polyglot.Value v) {
                    assertEquals(42, v.asInt());
                } else if (result instanceof Number n) {
                    assertEquals(42, n.intValue());
                }
            } finally {
                org.mozilla.javascript.Context.exit();
            }
        }
    }

    /** Test sink with a Function-typed setter so the type mapping fires. */
    public static class RhinoFunctionSink {
        static org.mozilla.javascript.Function last;
        public void set(final org.mozilla.javascript.Function f) { last = f; }
    }

    @Test
    void functionalInterfaceTakesJsLambda() {
        try (Context ctx = newContext()) {
            ctx.getBindings("js").putMember(
                    "apply",
                    (java.util.function.Function<String, String>) s -> s + "!");
            final Value result = ctx.eval("js", "apply('hi')");
            assertTrue(result.isString());
            assertEquals("hi!", result.asString());
        }
    }
}
