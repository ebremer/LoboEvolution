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

package org.loboevolution.html.js.xml;

import org.loboevolution.html.dom.nodeimpl.NodeImpl;
import org.loboevolution.html.dom.nodeimpl.event.EventTargetImpl;
import org.loboevolution.js.xml.XMLHttpRequestEventTarget;

/**
 * <p>XMLHttpRequestEventTargetImpl class.</p>
 */
public class XMLHttpRequestEventTargetImpl extends EventTargetImpl implements XMLHttpRequestEventTarget {

    public XMLHttpRequestEventTargetImpl(NodeImpl target) {
        setTarget(target);
    }

    @Override
    public Object getOnloadstart() {
        return getCallable(this, "loadstart");
    }

    @Override
    public void setOnloadstart(Object onloadstart) {
        addEventListener("loadstart", onloadstart);
    }

    @Override
    public Object getOnprogress() {
        return getCallable(this, "progress");
    }

    @Override
    public void setOnprogress(Object onprogress) {
        addEventListener("progress", onprogress);
    }

    @Override
    public Object getOnabort() {
        return getCallable(this, "abort");
    }

    @Override
    public void setOnabort(Object onabort) {
        addEventListener("abort", onabort);
    }

    @Override
    public Object getOnerror() {
        return getCallable(this, "error");
    }

    @Override
    public void setOnerror(Object onerror) {
        addEventListener("error", onerror);
    }

    @Override
    public Object getOnload() {
        return getCallable(this, "load");
    }

    @Override
    public void setOnload(Object onload) {
        addEventListener("load", onload);
    }

    @Override
    public Object getOntimeout() {
        return getCallable(this, "timeout");
    }

    @Override
    public void setOntimeout(Object ontimeout) {
        addEventListener("timeout", ontimeout);
    }

    @Override
    public Object getOnloadend() {
        return getCallable(this, "loadend");
    }

    @Override
    public void setOnloadend(Object onloadend) {
        addEventListener("loadend", onloadend);
    }
}
