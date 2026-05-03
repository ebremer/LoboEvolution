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

import org.junit.jupiter.api.Test;
import org.loboevolution.html.dom.domimpl.HTMLScriptElementImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Phase 9 follow-up: XHTML CDATA markers must be stripped from script bodies
 * before they reach the JS engine, otherwise GraalJS rejects them as syntax
 * errors (which is correct per ES spec). The stripping must preserve line and
 * column positions so error reports still point at the author's source.
 */
class GraalCdataStrippingTest {

    @Test
    void stripsBareCdataMarkers() {
        final String src = "<![CDATA[\nalert('hi');\n]]>";
        final String out = HTMLScriptElementImpl.stripCdataMarkers(src);
        assertEquals("         \nalert('hi');\n   ", out);
    }

    @Test
    void preservesScriptsWithoutMarkers() {
        final String src = "var x = 1; alert(x);";
        assertEquals(src, HTMLScriptElementImpl.stripCdataMarkers(src));
    }

    @Test
    void preservesAlreadyCommentedMarkers() {
        // The // and /* */ wrapping leaves the markers as-is — they're inside
        // comments so JS-side they're harmless. We still strip the literal
        // tokens, but the surrounding comment characters protect line shape.
        final String src = "/*<![CDATA[*/alert('hi');/*]]>*/";
        // Stripping removes the inner tokens; the comment delimiters become
        // empty /* */ blocks which are valid JS.
        final String out = HTMLScriptElementImpl.stripCdataMarkers(src);
        assertEquals("/*         */alert('hi');/*   */", out);
    }

    @Test
    void handlesNullAndEmpty() {
        assertNull(HTMLScriptElementImpl.stripCdataMarkers(null));
        assertEquals("", HTMLScriptElementImpl.stripCdataMarkers(""));
    }

    @Test
    void preservesLengthForErrorPositions() {
        final String src = "<![CDATA[alert('boom']]>";
        final String out = HTMLScriptElementImpl.stripCdataMarkers(src);
        // Same length so error column numbers match the original
        assertEquals(src.length(), out.length());
    }

    @Test
    void multipleCdataBlocksAllStripped() {
        final String src = "<![CDATA[a]]><![CDATA[b]]>";
        final String out = HTMLScriptElementImpl.stripCdataMarkers(src);
        assertEquals("         a            b   ", out);
    }
}
