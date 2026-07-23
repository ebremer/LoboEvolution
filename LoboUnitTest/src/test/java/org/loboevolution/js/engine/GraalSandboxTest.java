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
import org.junit.jupiter.api.Test;
import org.loboevolution.html.js.engine.LoboGraalHostAccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Locks in the JavaScript sandbox boundary. Because the host-access policy uses
 * {@code allowPublicAccess}, a bound object exposes {@code Object.getClass()};
 * without the reflection denials in {@link LoboGraalHostAccess} a script can
 * walk {@code getClass().getClassLoader().loadClass("java.lang.Runtime")} to
 * arbitrary code execution. These tests prove that escape is blocked while the
 * legitimate interop the DOM bridge relies on (method calls, {@code instanceof},
 * {@code new}) keeps working, and fail loudly if the policy is ever widened.
 */
class GraalSandboxTest {

    /** A stand-in for a bound DOM object. */
    public static class Host {
        public String hello() {
            return "hi";
        }
    }

    private static Context sandbox() {
        final Context c = LoboGraalHostAccess.newContextBuilder().build();
        c.getBindings("js").putMember("host", new Host());
        c.getBindings("js").putMember("HostType", Host.class);
        return c;
    }

    @Test
    void boundObjectInteropStillWorks() {
        try (Context c = sandbox()) {
            assertEquals("hi", c.eval("js", "host.hello()").asString());
            assertTrue(c.eval("js", "host instanceof HostType").asBoolean());
            assertEquals("hi", c.eval("js", "new HostType().hello()").asString());
        }
    }

    @Test
    void cannotReachRuntimeThroughReflection() {
        try (Context c = sandbox()) {
            final String result = c.eval("js",
                    "(function(){try{"
                    + "host.getClass().getClassLoader().loadClass('java.lang.Runtime')"
                    + ".getMethod('getRuntime').invoke(null);"
                    + "return 'ESCAPED';}catch(e){return 'BLOCKED';}})()").asString();
            assertEquals("BLOCKED", result,
                    "untrusted script reached java.lang.Runtime via bound-object reflection");
        }
    }

    @Test
    void classAndClassLoaderMethodsAreDenied() {
        try (Context c = sandbox()) {
            assertEquals("BLOCKED", c.eval("js",
                    "(function(){try{return ''+host.getClass().getClassLoader();}"
                    + "catch(e){return 'BLOCKED';}})()").asString());
            assertEquals("BLOCKED", c.eval("js",
                    "(function(){try{host.getClass().getMethods();return 'OK';}"
                    + "catch(e){return 'BLOCKED';}})()").asString());
        }
    }

    @Test
    void javaHostAccessGlobalsAreUnavailable() {
        try (Context c = sandbox()) {
            assertEquals("undefined", c.eval("js", "typeof Java").asString());
            assertEquals("undefined", c.eval("js", "typeof Packages").asString());
        }
    }
}
