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
import org.graalvm.polyglot.HostAccess;

/**
 * GraalJS host-access policy chosen to match Rhino's reflective binding model
 * as closely as possible, so the existing DOM bridge classes (which expose
 * state via JavaBean getters and setters) continue to behave the same when
 * driven by GraalJS instead of Rhino.
 *
 * <p>{@link #INSTANCE} permits:
 * <ul>
 *   <li>access to all public methods and fields (Rhino defaults to the same);
 *   <li>indexed, list, map, iterable and iterator interop so {@code NodeList}
 *       and similar collection types behave naturally from JS;
 *   <li>passing JS functions to Java callbacks declared with
 *       {@link FunctionalInterface}, mirroring Rhino's automatic single-method
 *       coercion of functions.
 * </ul>
 *
 * <p>{@link #newContextBuilder()} additionally turns on
 * {@code js.nashorn-compat}, which is what makes {@code obj.name} resolve to
 * a Java {@code getName()} the way Rhino does. Always go through this helper
 * (or call {@link #applyTo(Context.Builder)}) when constructing a Lobo
 * GraalJS context — the host-access policy alone is not enough to give us
 * bean-property reflection.
 *
 * <p>This setup is intentionally permissive. The browser sandbox is enforced
 * elsewhere (the {@code ClassShutter} equivalent comes in a later phase);
 * this class is only about how an already-reachable Java object is reflected
 * into the JS world.
 */
public final class LoboGraalHostAccess {

    /** Shared, immutable {@link HostAccess} instance. Use this everywhere. */
    public static final HostAccess INSTANCE = HostAccess.newBuilder()
            .allowPublicAccess(true)
            .allowAccessInheritance(true)
            .allowArrayAccess(true)
            .allowListAccess(true)
            .allowMapAccess(true)
            .allowIterableAccess(true)
            .allowIteratorAccess(true)
            .allowBufferAccess(true)
            .allowImplementationsAnnotatedBy(FunctionalInterface.class)
            .build();

    /**
     * Returns a {@link Context.Builder} preconfigured for the Lobo browser:
     * the {@link #INSTANCE} host-access policy plus the Nashorn-compat option
     * that gives us JavaBean-style property reflection.
     */
    public static Context.Builder newContextBuilder() {
        return applyTo(Context.newBuilder("js"));
    }

    /**
     * Applies the Lobo browser host-access and JS-engine options to an existing
     * builder. Use this when callers need to add their own options on top.
     */
    public static Context.Builder applyTo(final Context.Builder builder) {
        return builder
                .allowHostAccess(INSTANCE)
                .allowExperimentalOptions(true)
                .option("js.nashorn-compat", "true")
                .option("js.ecmascript-version", "latest");
    }

    private LoboGraalHostAccess() {
    }
}
