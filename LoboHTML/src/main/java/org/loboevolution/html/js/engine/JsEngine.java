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

/**
 * Engine-agnostic surface used by Lobo to evaluate JavaScript. After
 * Phase 12 Part A the only implementation is {@link GraalJsEngine}; the
 * abstraction is preserved so call sites stay decoupled from polyglot
 * specifics and to keep the door open for additional backends in the future.
 * Build a context-bound engine through {@link JsEngineFactory#forDocument}.
 */
public interface JsEngine extends AutoCloseable {

    /**
     * Evaluates a JavaScript source in this engine's top-level scope.
     *
     * @param source     the JavaScript source text
     * @param sourceName an arbitrary label used in stack traces
     * @return the script's last-expression result; the concrete return type is
     *         engine-specific (Rhino returns a Java boxed value or
     *         {@code Scriptable}; GraalJS returns a {@code Value}).
     */
    Object eval(String source, String sourceName);

    /**
     * Binds a Java object as a JS global. Property reads and method calls on
     * the object follow each engine's reflection rules — see
     * {@link LoboGraalHostAccess} for what GraalJS exposes.
     */
    void putGlobal(String name, Object value);

    /**
     * Invokes a JS callback that was previously stored as a host-side
     * {@code Object} — typically an event listener that came in through
     * {@code addEventListener}. The runtime type of {@code callback} is
     * engine-specific (Rhino's {@code Function}, Graal's
     * {@code org.graalvm.polyglot.Value}). Implementations downcast
     * accordingly.
     *
     * @return the call result, or {@code null} if the engine cannot invoke
     *         this kind of object.
     */
    Object call(Object callback, Object... args);

    /** Releases any engine-owned resources. Safe to call more than once. */
    @Override
    void close();
}
