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
package org.loboevolution.net;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression guard for Subresource Integrity (SRI). {@link IOUtil#readFully}
 * once silently returned an empty array, which made {@link AlgorithmDigest}
 * hash nothing and reject every integrity-protected script/stylesheet. These
 * tests exercise the exact {@code readFully} → {@code validate} combination the
 * resource loaders use, so a future regression of that kind fails the build.
 */
class SubresourceIntegrityTest {

    private static final String SCRIPT = "console.log('lobo');\n";

    /** readFully must return the whole stream — the core of the SRI regression. */
    @Test
    void readFullyReadsEntireStream() throws IOException {
        final byte[] bytes = SCRIPT.getBytes(StandardCharsets.UTF_8);
        final byte[] read = IOUtil.readFully(new ByteArrayInputStream(bytes));
        assertArrayEquals(bytes, read);
    }

    @Test
    void readFullyNullStreamReturnsEmpty() throws IOException {
        assertEquals(0, IOUtil.readFully(null).length);
    }

    /** A resource whose bytes match its declared sha256 digest passes validation. */
    @Test
    void matchingSha256Validates() throws Exception {
        final byte[] data = IOUtil.readFully(streamOf(SCRIPT));
        assertTrue(AlgorithmDigest.validate(data, integrity("SHA-256", "sha256", SCRIPT)));
    }

    @Test
    void matchingSha512Validates() throws Exception {
        final byte[] data = IOUtil.readFully(streamOf(SCRIPT));
        assertTrue(AlgorithmDigest.validate(data, integrity("SHA-512", "sha512", SCRIPT)));
    }

    /** Tampered content must be rejected against a good digest. */
    @Test
    void mismatchedContentIsRejected() throws Exception {
        final byte[] tampered = IOUtil.readFully(streamOf(SCRIPT + "/* evil */"));
        assertFalse(AlgorithmDigest.validate(tampered, integrity("SHA-256", "sha256", SCRIPT)));
    }

    /** No integrity attribute means no constraint — validation passes. */
    @Test
    void blankIntegrityValidates() throws IOException {
        final byte[] data = IOUtil.readFully(streamOf(SCRIPT));
        assertTrue(AlgorithmDigest.validate(data, null));
        assertTrue(AlgorithmDigest.validate(data, ""));
    }

    private static ByteArrayInputStream streamOf(final String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }

    /** Builds an {@code <alg>-<base64digest>} integrity token for the given content. */
    private static String integrity(final String jdkAlg, final String sriAlg, final String content)
            throws NoSuchAlgorithmException {
        final byte[] digest = MessageDigest.getInstance(jdkAlg)
                .digest(content.getBytes(StandardCharsets.UTF_8));
        return sriAlg + "-" + Base64.getEncoder().encodeToString(digest);
    }
}
