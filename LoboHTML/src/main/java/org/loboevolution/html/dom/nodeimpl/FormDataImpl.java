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

package org.loboevolution.html.dom.nodeimpl;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.loboevolution.html.dom.domimpl.HTMLDocumentImpl;
import org.loboevolution.html.node.FormData;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.loboevolution.html.node.AbstractList;

/**
 * <p>FormDataImpl class.</p>
 */
@AllArgsConstructor
public class FormDataImpl extends AbstractList<FormDataImpl.Field> implements FormData {

    private final HTMLDocumentImpl doc;

    @Override
    public void append(String name, Object value) {
        append(name, value, null);
    }

    @Override
    public void append(String name, Object value, String fileName) {
        add(new Field(name, value, fileName));
    }

    @Override
    public void delete(String name) {
        removeIf(f -> !name.equals(f.getKey()));
    }

    @Override
    public boolean has(String name) {
        return stream().anyMatch(f -> name.equals(f.getKey()));
    }

    @Override
    public Iterator<Object[]> entries() {
        return stream().map(f -> new Object[] { f.getKey(), f.getValue() }).iterator();
    }

    @Override
    public Iterator<String> keys() {
        return stream().map(Field::getKey).iterator();
    }

    @Override
    public Iterator<Object> values() {
        return stream().map(Field::getValue).iterator();
    }

    @Override
    public Object get(Object name) {
        Optional<Field> optional = stream().filter(f -> name.equals(f.getKey())).findFirst();
        return optional.map(Field::getValue).orElse(null);
    }

    @Override
    public String getAll(String name) {
        StringBuilder builder = new StringBuilder();
        stream().filter(f -> name.equals(f.getKey())).forEach(d -> builder.append(d.getValue()).append(","));
        String result = builder.toString();
        return !result.isEmpty() ? result.substring(0, result.length() - 1) : "";
    }

    @Override
    public void set(String name, Object value) {
        set(name, value, null);
    }

    @Override
    public void set(String name, Object value, String fileName) {
        AtomicInteger index = new AtomicInteger();
        forEach(f -> {
            if (name.equals(f.getKey())) {
                set(index.getAndIncrement(), new Field(name, value, fileName));
            } else {
                index.incrementAndGet();
            }
        });
    }

    @Data
    public static class Field {
        private final String key;
        private final Object value;
        private final String fileName;
    }
}
