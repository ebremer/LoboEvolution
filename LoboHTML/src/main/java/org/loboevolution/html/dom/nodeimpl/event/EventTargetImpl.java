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

package org.loboevolution.html.dom.nodeimpl.event;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.loboevolution.common.ArrayUtilities;
import org.loboevolution.common.Strings;
import org.loboevolution.html.dom.domimpl.HTMLElementImpl;
import org.loboevolution.html.dom.nodeimpl.ElementImpl;
import org.loboevolution.html.dom.nodeimpl.NodeImpl;
import org.loboevolution.html.js.Executor;
import org.loboevolution.html.js.WindowImpl;
import org.loboevolution.html.js.engine.JsEngine;
import org.loboevolution.html.js.engine.JsEngineFactory;
import org.loboevolution.html.js.events.EventImpl;
import org.loboevolution.html.node.Document;
import org.loboevolution.html.node.Node;
import org.loboevolution.events.Event;
import org.loboevolution.events.EventTarget;
import org.loboevolution.html.renderer.HtmlController;
import org.loboevolution.http.UserAgentContext;
import org.loboevolution.js.AbstractScriptableDelegate;
import org.loboevolution.js.JavaScript;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.events.EventException;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>EventTargetImpl class.</p>
 */
@Slf4j
public class EventTargetImpl extends AbstractScriptableDelegate implements EventTarget {

   @Setter
   private NodeImpl target;
   private List<EventListenerEntry> mListenerEntries;

    @Override
    public void addEventListener(final String type, final Function listener) {
        addEventListener(type, (Object) listener, false);
    }

    @Override
    public void addEventListener(final String type, final Function listener, final boolean useCapture) {
        addEventListener(type, (Object) listener, useCapture);
    }

    @Override
    public void addEventListener(final String type, final Object listener) {
        addEventListener(type, listener, false);
    }

    @Override
    public void addEventListener(final String type, final Object listener, final boolean useCapture) {
        if (Strings.isNotBlank(type) && listener != null) {
            if ("load".equals(type) || "DOMContentLoaded".equals(type)) {
                onloadEvent(listener);
            } else {
                removeEventListener(type, listener, useCapture);
                if (mListenerEntries == null) {
                    mListenerEntries = new ArrayList<>();
                }
                mListenerEntries.add(new EventListenerEntry(type, listener, useCapture));
            }
        }
    }

    @Override
    public void removeEventListener(final String type, final Function listener) {
        removeEventListener(type, (Object) listener, true);
    }

    @Override
    public void removeEventListener(final String type, final Function listener, final boolean useCapture) {
        removeEventListener(type, (Object) listener, useCapture);
    }

    @Override
    public void removeEventListener(final String type, final Object listener) {
        removeEventListener(type, listener, true);
    }

    @Override
    public void removeEventListener(final String type, final Object listener, final boolean useCapture) {
        if (ArrayUtilities.isNotBlank(mListenerEntries)) {
            mListenerEntries.removeIf(entry ->
                    Objects.equals(entry.isUseCapture(), useCapture)
                            && Objects.equals(entry.getCallback(), listener)
                            && Objects.equals(entry.getType(), type));
        }
    }

    @Override
    public boolean dispatchEvent(final Node element, final Event evt) {
        final EventImpl eventImpl = (EventImpl) evt;
        eventImpl.setEventPhase(Event.AT_TARGET);
        if (!eventImpl.isPropogationStopped() && mListenerEntries != null) {
            new ArrayList<>(mListenerEntries).forEach(listenerEntry -> {
                if (!listenerEntry.isUseCapture() && listenerEntry.getType().equals(eventImpl.getType())) {
                    try {
                        if (element instanceof HTMLElementImpl elem) {
                            eventImpl.setTarget(elem);
                            eventImpl.setCurrentTarget(elem);
                            invokeCallback((NodeImpl) element, listenerEntry.getCallback(), eventImpl);
                        }
                    } catch (Exception e) {
                        log.error("Caught EventListener exception", e);
                    }
                }
            });
        }
        return eventImpl.getDefaultPrevented();
    }

    @Override
    public boolean dispatchEvent(final Event evt) throws EventException {
        final EventImpl eventImpl = (EventImpl) evt;
        eventImpl.setEventPhase(Event.AT_TARGET);
        if (!eventImpl.getDefaultPrevented() && mListenerEntries != null) {
            new ArrayList<>(mListenerEntries).forEach(listenerEntry -> {
                if (!listenerEntry.isUseCapture() && listenerEntry.getType().equals(eventImpl.getType())) {
                    try {
                        if (target instanceof HTMLElementImpl elem) {
                            eventImpl.setTarget(elem);
                            eventImpl.setCurrentTarget(elem);
                            // Rhino-style attribute handlers go through HtmlController so the
                            // legacy global `event` property is set; engine-agnostic callbacks
                            // (e.g. graal Values from addEventListener) take the direct path.
                            final Object cb = listenerEntry.getCallback();
                            if (cb instanceof Function f) {
                                HtmlController.getInstance().execute(target, f, eventImpl);
                            } else {
                                invokeCallback(target, cb, eventImpl);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Caught EventListener exception", e);
                    }
                }
            });
        }
        return eventImpl.getDefaultPrevented();
    }

    /**
     * Routes a callback through the engine that owns the document this event
     * target belongs to. Works for both Rhino {@link Function}s and GraalJS
     * {@code Value}s without the call site having to know which engine is
     * active.
     */
    private static void invokeCallback(final NodeImpl node, final Object callback, final EventImpl evt) {
        final Document doc = node.getDocumentNode();
        if (doc == null) {
            return;
        }
        final WindowImpl window = (WindowImpl) doc.getDefaultView();
        if (window == null) {
            return;
        }
        final JsEngine engine = JsEngineFactory.forDocument(doc, window);
        if (evt == null) {
            engine.call(callback);
        } else {
            engine.call(callback, evt);
        }
    }

    public Function getFunction(final Object obj, final String type) {
        final Object callable = getCallable(obj, type);
        return callable instanceof Function f ? f : null;
    }

    /**
     * Engine-agnostic counterpart to {@link #getFunction(Object, String)}. Returns
     * the registered callback for {@code type} as an opaque {@link Object} —
     * a Rhino {@link Function} under Rhino, a GraalJS callable under GraalJS,
     * or whatever the active engine produces. Use this when the caller needs
     * to fire the handler regardless of engine; downcast only when a Rhino
     * {@code Function} is specifically required (e.g. legacy W3C accessors).
     */
    public Object getCallable(final Object obj, final String type) {
        final String subType = type.startsWith("on") ? type.substring(2) : type;
        if (obj instanceof WindowImpl window) {
            return window.getUserAgentContext().isScriptingEnabled() ? searchCallable(subType) : null;
        }

        if (obj instanceof ElementImpl elem) {
            final UserAgentContext uac = elem.getUserAgentContext();
            if (uac.isScriptingEnabled()) {
                final Object cb = searchCallable(subType);
                if (cb == null) {
                    return searchStringCallable(elem, subType);
                }
                return cb;
            }
        }

        return searchCallable(subType);
    }

    private Object searchCallable(final String type) {
        if (mListenerEntries != null) {
            for (final EventListenerEntry entry : mListenerEntries) {
                if (!entry.isUseCapture() && entry.getType().equals(type)) {
                    return entry.getCallback();
                }
            }
        }
        return null;
    }

    /**
     * Compiles an inline {@code on*} attribute handler string into a callable.
     * Dispatches based on which engine owns the document so the resulting
     * handler shares globals with the page's {@code <script>} content. Returns
     * a Rhino {@link Function} under Rhino mode and a GraalJS callable under
     * Graal mode.
     */
    private Object searchStringCallable(final ElementImpl elem, final String type) {
        final String normalAttributeName = "on" + type;
        final String attributeValue = elem.getAttribute(normalAttributeName);
        if (!Strings.isCssNotBlank(attributeValue)) {
            return null;
        }
        final Document doc = elem.getDocumentNode();
        if (doc == null) {
            throw new IllegalStateException("Element does not belong to a document.");
        }
        final WindowImpl window = (WindowImpl) doc.getDefaultView();
        final String sourceName = elem.getTagName() + "[" + elem.getId() + "]." + normalAttributeName;
        final JsEngine engine = JsEngineFactory.forDocument(doc, window);

        // Evaluate the attribute body as an expression returning an anonymous
        // function, so the result is a callable Value sharing the engine's
        // global scope (where page <script>s defined their functions).
        try {
            return engine.eval("(function(){\n" + attributeValue + "\n})", sourceName);
        } catch (final Throwable err) {
            log.warn("Failed to compile attribute handler {}: {}", sourceName, err.getMessage());
            return null;
        }
    }

    private void onloadEvent(final Object onloadHandler) {
         if(target instanceof HTMLElementImpl elem){
            // Rhino path keeps the legacy parent-scope behaviour; for any other
            // engine we just route through the abstraction.
            if (onloadHandler instanceof Function f) {
                final WindowImpl window = (WindowImpl) elem.getDocumentNode().getDefaultView();
                Executor.executeFunction(f.getParentScope(), f, window.getContextFactory());
            } else {
                invokeCallback(elem, onloadHandler, null);
            }
        }
    }
}