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

import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Test;
import org.loboevolution.html.js.engine.GraalJsEngine;
import org.loboevolution.html.js.engine.JsEngine;
import org.loboevolution.html.js.engine.JsEngineFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * After Phase 12 Part A there is no longer an engine selector — every call
 * to {@link JsEngineFactory#create} produces a {@link GraalJsEngine}. These
 * tests prove the factory still compiles, evaluates JS, and shares globals
 * across consecutive evals on the same engine instance.
 */
class JsEngineFactoryTest {

    @Test
    void createReturnsGraalJsEngine() {
        try (JsEngine engine = JsEngineFactory.create()) {
            assertInstanceOf(GraalJsEngine.class, engine);
        }
    }

    @Test
    void engineEvaluatesArithmetic() {
        try (JsEngine engine = JsEngineFactory.create()) {
            assertEquals(6, asInt(engine.eval("2 * 3", "arith")));
        }
    }

    @Test
    void engineExposesGlobals() {
        try (JsEngine engine = JsEngineFactory.create()) {
            engine.putGlobal("greeting", "hi");
            assertEquals("hi from JS", asString(engine.eval("greeting + ' from JS'", "global")));
        }
    }

    /**
     * Two consecutive {@code eval} calls on the same engine instance must
     * share globals — the per-document caching invariant that
     * {@code HTMLScriptElementImpl} relies on so multiple {@code <script>}
     * tags on a page see each other's variables.
     */
    @Test
    void sequentialScriptsShareScope() {
        try (JsEngine engine = JsEngineFactory.create()) {
            engine.eval("var shared = 7;", "<script1>");
            assertEquals(14, asInt(engine.eval("shared * 2", "<script2>")));
        }
    }

    private static int asInt(final Object o) {
        if (o instanceof Value v) return v.asInt();
        if (o instanceof Number n) return n.intValue();
        throw new AssertionError("not a number: " + o);
    }

    private static String asString(final Object o) {
        if (o instanceof Value v) return v.asString();
        return String.valueOf(o);
    }
}
