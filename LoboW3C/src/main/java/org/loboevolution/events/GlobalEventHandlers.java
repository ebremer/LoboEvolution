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

package org.loboevolution.events;


/**
 * The interface Global event handlers.
 */
public interface GlobalEventHandlers extends EventTarget {
    /**
     * Fires when the user aborts the download.
     *
     * @return the onabort
     */
    Object getOnabort();

    /**
     * Sets onabort.
     *
     * @param onabort the onabort
     */
    void setOnabort(Object onabort);

    /**
     * Add abort event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addAbortEventListener(Object listener, boolean options) {
        addEventListener("abort", listener, options);
    }

    /**
     * Add abort event listener.
     *
     * @param listener the listener
     */
    default void addAbortEventListener(Object listener) {
        addEventListener("abort", listener);
    }

    /**
     * Remove abort event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeAbortEventListener(Object listener, boolean options) {
        removeEventListener("abort", listener, options);
    }

    /**
     * Remove abort event listener.
     *
     * @param listener the listener
     */
    default void removeAbortEventListener(Object listener) {
        removeEventListener("abort", listener);
    }

    /**
     * Gets onanimationcancel.
     *
     * @return the onanimationcancel
     */
    Object getOnanimationcancel();

    /**
     * Sets onanimationcancel.
     *
     * @param onanimationcancel the onanimationcancel
     */
    void setOnanimationcancel(Object onanimationcancel);

    /**
     * Add animation cancel event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addAnimationCancelEventListener(Object listener, boolean options) {
        addEventListener("animationcancel", listener, options);
    }

    /**
     * Add animation cancel event listener.
     *
     * @param listener the listener
     */
    default void addAnimationCancelEventListener(Object listener) {
        addEventListener("animationcancel", listener);
    }

    /**
     * Remove animation cancel event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeAnimationCancelEventListener(Object listener, boolean options) {
        removeEventListener("animationcancel", listener, options);
    }

    /**
     * Remove animation cancel event listener.
     *
     * @param listener the listener
     */
    default void removeAnimationCancelEventListener(Object listener) {
        removeEventListener("animationcancel", listener);
    }

    /**
     * Gets onanimationend.
     *
     * @return the onanimationend
     */
    Object getOnanimationend();

    /**
     * Sets onanimationend.
     *
     * @param onanimationend the onanimationend
     */
    void setOnanimationend(Object onanimationend);

    /**
     * Add animation end event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addAnimationEndEventListener(Object listener, boolean options) {
        addEventListener("animationend", listener, options);
    }

    /**
     * Add animation end event listener.
     *
     * @param listener the listener
     */
    default void addAnimationEndEventListener(Object listener) {
        addEventListener("animationend", listener);
    }

    /**
     * Remove animation end event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeAnimationEndEventListener(Object listener, boolean options) {
        removeEventListener("animationend", listener, options);
    }

    /**
     * Remove animation end event listener.
     *
     * @param listener the listener
     */
    default void removeAnimationEndEventListener(Object listener) {
        removeEventListener("animationend", listener);
    }

    /**
     * Gets onanimationiteration.
     *
     * @return the onanimationiteration
     */
    Object getOnanimationiteration();

    /**
     * Sets onanimationiteration.
     *
     * @param onanimationiteration the onanimationiteration
     */
    void setOnanimationiteration(Object onanimationiteration);

    /**
     * Add animation iteration event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addAnimationIterationEventListener(Object listener, boolean options) {
        addEventListener("animationiteration", listener, options);
    }

    /**
     * Add animation iteration event listener.
     *
     * @param listener the listener
     */
    default void addAnimationIterationEventListener(Object listener) {
        addEventListener("animationiteration", listener);
    }

    /**
     * Remove animation iteration event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeAnimationIterationEventListener(Object listener, boolean options) {
        removeEventListener("animationiteration", listener, options);
    }

    /**
     * Remove animation iteration event listener.
     *
     * @param listener the listener
     */
    default void removeAnimationIterationEventListener(Object listener) {
        removeEventListener("animationiteration", listener);
    }

    /**
     * Gets onanimationstart.
     *
     * @return the onanimationstart
     */
    Object getOnanimationstart();

    /**
     * Sets onanimationstart.
     *
     * @param onanimationstart the onanimationstart
     */
    void setOnanimationstart(Object onanimationstart);

    /**
     * Add animation start event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addAnimationStartEventListener(Object listener, boolean options) {
        addEventListener("animationstart", listener, options);
    }

    /**
     * Add animation start event listener.
     *
     * @param listener the listener
     */
    default void addAnimationStartEventListener(Object listener) {
        addEventListener("animationstart", listener);
    }

    /**
     * Remove animation start event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeAnimationStartEventListener(Object listener, boolean options) {
        removeEventListener("animationstart", listener, options);
    }

    /**
     * Remove animation start event listener.
     *
     * @param listener the listener
     */
    default void removeAnimationStartEventListener(Object listener) {
        removeEventListener("animationstart", listener);
    }

    /**
     * Gets onauxclick.
     *
     * @return the onauxclick
     */
    Object getOnauxclick();

    /**
     * Sets onauxclick.
     *
     * @param onauxclick the onauxclick
     */
    void setOnauxclick(Object onauxclick);

    /**
     * Add aux click event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addAuxClickEventListener(Object listener, boolean options) {
        addEventListener("auxclick", listener, options);
    }

    /**
     * Add aux click event listener.
     *
     * @param listener the listener
     */
    default void addAuxClickEventListener(Object listener) {
        addEventListener("auxclick", listener);
    }

    /**
     * Remove aux click event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeAuxClickEventListener(Object listener, boolean options) {
        removeEventListener("auxclick", listener, options);
    }

    /**
     * Remove aux click event listener.
     *
     * @param listener the listener
     */
    default void removeAuxClickEventListener(Object listener) {
        removeEventListener("auxclick", listener);
    }

    /**
     * Fires when the object loses the input focus.
     *
     * @return the onblur
     */
    Object getOnblur();

    /**
     * Sets onblur.
     *
     * @param onblur the onblur
     */
    void setOnblur(Object onblur);

    /**
     * Add blur event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addBlurEventListener(Object listener, boolean options) {
        addEventListener("blur", listener, options);
    }

    /**
     * Add blur event listener.
     *
     * @param listener the listener
     */
    default void addBlurEventListener(Object listener) {
        addEventListener("blur", listener);
    }

    /**
     * Remove blur event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeBlurEventListener(Object listener, boolean options) {
        removeEventListener("blur", listener, options);
    }

    /**
     * Remove blur event listener.
     *
     * @param listener the listener
     */
    default void removeBlurEventListener(Object listener) {
        removeEventListener("blur", listener);
    }

    /**
     * Gets oncancel.
     *
     * @return the oncancel
     */
    Object getOncancel();

    /**
     * Sets oncancel.
     *
     * @param oncancel the oncancel
     */
    void setOncancel(Object oncancel);

    /**
     * Add cancel event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addCancelEventListener(Object listener, boolean options) {
        addEventListener("cancel", listener, options);
    }

    /**
     * Add cancel event listener.
     *
     * @param listener the listener
     */
    default void addCancelEventListener(Object listener) {
        addEventListener("cancel", listener);
    }

    /**
     * Remove cancel event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeCancelEventListener(Object listener, boolean options) {
        removeEventListener("cancel", listener, options);
    }

    /**
     * Remove cancel event listener.
     *
     * @param listener the listener
     */
    default void removeCancelEventListener(Object listener) {
        removeEventListener("cancel", listener);
    }

    /**
     * Occurs when playback is possible, but would require further buffering.
     *
     * @return the oncanplay
     */
    Object getOncanplay();

    /**
     * Sets oncanplay.
     *
     * @param oncanplay the oncanplay
     */
    void setOncanplay(Object oncanplay);

    /**
     * Add can play event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addCanPlayEventListener(Object listener, boolean options) {
        addEventListener("canplay", listener, options);
    }

    /**
     * Add can play event listener.
     *
     * @param listener the listener
     */
    default void addCanPlayEventListener(Object listener) {
        addEventListener("canplay", listener);
    }

    /**
     * Remove can play event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeCanPlayEventListener(Object listener, boolean options) {
        removeEventListener("canplay", listener, options);
    }

    /**
     * Remove can play event listener.
     *
     * @param listener the listener
     */
    default void removeCanPlayEventListener(Object listener) {
        removeEventListener("canplay", listener);
    }

    /**
     * Gets oncanplaythrough.
     *
     * @return the oncanplaythrough
     */
    Object getOncanplaythrough();

    /**
     * Sets oncanplaythrough.
     *
     * @param oncanplaythrough the oncanplaythrough
     */
    void setOncanplaythrough(Object oncanplaythrough);

    /**
     * Add can play through event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addCanPlayThroughEventListener(Object listener, boolean options) {
        addEventListener("canplaythrough", listener, options);
    }

    /**
     * Add can play through event listener.
     *
     * @param listener the listener
     */
    default void addCanPlayThroughEventListener(Object listener) {
        addEventListener("canplaythrough", listener);
    }

    /**
     * Remove can play through event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeCanPlayThroughEventListener(Object listener, boolean options) {
        removeEventListener("canplaythrough", listener, options);
    }

    /**
     * Remove can play through event listener.
     *
     * @param listener the listener
     */
    default void removeCanPlayThroughEventListener(Object listener) {
        removeEventListener("canplaythrough", listener);
    }

    /**
     * Fires when the contents of the object or selection have changed.
     *
     * @return the onchange
     */
    Object getOnchange();

    /**
     * Sets onchange.
     *
     * @param onchange the onchange
     */
    void setOnchange(Object onchange);

    /**
     * Add change event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addChangeEventListener(Object listener, boolean options) {
        addEventListener("change", listener, options);
    }

    /**
     * Add change event listener.
     *
     * @param listener the listener
     */
    default void addChangeEventListener(Object listener) {
        addEventListener("change", listener);
    }

    /**
     * Remove change event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeChangeEventListener(Object listener, boolean options) {
        removeEventListener("change", listener, options);
    }

    /**
     * Remove change event listener.
     *
     * @param listener the listener
     */
    default void removeChangeEventListener(Object listener) {
        removeEventListener("change", listener);
    }

    /**
     * Fires when the user clicks the left mouse button on the object
     *
     * @return the onclick
     */
    Object getOnclick();

    /**
     * Sets onclick.
     *
     * @param onclick the onclick
     */
    void setOnclick(Object onclick);

    /**
     * Add click event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addClickEventListener(Object listener, boolean options) {
        addEventListener("click", listener, options);
    }

    /**
     * Add click event listener.
     *
     * @param listener the listener
     */
    default void addClickEventListener(Object listener) {
        addEventListener("click", listener);
    }

    /**
     * Remove click event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeClickEventListener(Object listener, boolean options) {
        removeEventListener("click", listener, options);
    }

    /**
     * Remove click event listener.
     *
     * @param listener the listener
     */
    default void removeClickEventListener(Object listener) {
        removeEventListener("click", listener);
    }

    /**
     * Gets onclose.
     *
     * @return the onclose
     */
    Object getOnclose();

    /**
     * Sets onclose.
     *
     * @param onclose the onclose
     */
    void setOnclose(Object onclose);

    /**
     * Add close event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addCloseEventListener(Object listener, boolean options) {
        addEventListener("close", listener, options);
    }

    /**
     * Add close event listener.
     *
     * @param listener the listener
     */
    default void addCloseEventListener(Object listener) {
        addEventListener("close", listener);
    }

    /**
     * Remove close event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeCloseEventListener(Object listener, boolean options) {
        removeEventListener("close", listener, options);
    }

    /**
     * Remove close event listener.
     *
     * @param listener the listener
     */
    default void removeCloseEventListener(Object listener) {
        removeEventListener("close", listener);
    }

    /**
     * Fires when the user clicks the right mouse button in the client area, opening
     * the context menu.
     *
     * @return the oncontextmenu
     */
    Object getOncontextmenu();

    /**
     * Sets oncontextmenu.
     *
     * @param oncontextmenu the oncontextmenu
     */
    void setOncontextmenu(Object oncontextmenu);

    /**
     * Add context menu event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addContextMenuEventListener(Object listener, boolean options) {
        addEventListener("contextmenu", listener, options);
    }

    /**
     * Add context menu event listener.
     *
     * @param listener the listener
     */
    default void addContextMenuEventListener(Object listener) {
        addEventListener("contextmenu", listener);
    }

    /**
     * Remove context menu event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeContextMenuEventListener(Object listener, boolean options) {
        removeEventListener("contextmenu", listener, options);
    }

    /**
     * Remove context menu event listener.
     *
     * @param listener the listener
     */
    default void removeContextMenuEventListener(Object listener) {
        removeEventListener("contextmenu", listener);
    }

    /**
     * Gets oncuechange.
     *
     * @return the oncuechange
     */
    Object getOncuechange();

    /**
     * Sets oncuechange.
     *
     * @param oncuechange the oncuechange
     */
    void setOncuechange(Object oncuechange);

    /**
     * Add cue change event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addCueChangeEventListener(Object listener, boolean options) {
        addEventListener("cuechange", listener, options);
    }

    /**
     * Add cue change event listener.
     *
     * @param listener the listener
     */
    default void addCueChangeEventListener(Object listener) {
        addEventListener("cuechange", listener);
    }

    /**
     * Remove cue change event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeCueChangeEventListener(Object listener, boolean options) {
        removeEventListener("cuechange", listener, options);
    }

    /**
     * Remove cue change event listener.
     *
     * @param listener the listener
     */
    default void removeCueChangeEventListener(Object listener) {
        removeEventListener("cuechange", listener);
    }

    /**
     * Fires when the user double-clicks the object.
     *
     * @return the ondblclick
     */
    Object getOndblclick();

    /**
     * Sets ondblclick.
     *
     * @param ondblclick the ondblclick
     */
    void setOndblclick(Object ondblclick);

    /**
     * Add dbl click event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addDblClickEventListener(Object listener, boolean options) {
        addEventListener("dblclick", listener, options);
    }

    /**
     * Add dbl click event listener.
     *
     * @param listener the listener
     */
    default void addDblClickEventListener(Object listener) {
        addEventListener("dblclick", listener);
    }

    /**
     * Remove dbl click event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeDblClickEventListener(Object listener, boolean options) {
        removeEventListener("dblclick", listener, options);
    }

    /**
     * Remove dbl click event listener.
     *
     * @param listener the listener
     */
    default void removeDblClickEventListener(Object listener) {
        removeEventListener("dblclick", listener);
    }

    /**
     * Fires on the source object continuously during a drag operation.
     *
     * @return the ondrag
     */
    Object getOndrag();

    /**
     * Sets ondrag.
     *
     * @param ondrag the ondrag
     */
    void setOndrag(Object ondrag);

    /**
     * Add drag event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addDragEventListener(Object listener, boolean options) {
        addEventListener("drag", listener, options);
    }

    /**
     * Add drag event listener.
     *
     * @param listener the listener
     */
    default void addDragEventListener(Object listener) {
        addEventListener("drag", listener);
    }

    /**
     * Remove drag event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeDragEventListener(Object listener, boolean options) {
        removeEventListener("drag", listener, options);
    }

    /**
     * Remove drag event listener.
     *
     * @param listener the listener
     */
    default void removeDragEventListener(Object listener) {
        removeEventListener("drag", listener);
    }

    /**
     * Fires on the source object when the user releases the mouse at the close of a
     * drag operation.
     *
     * @return the ondragend
     */
    Object getOndragend();

    /**
     * Sets ondragend.
     *
     * @param ondragend the ondragend
     */
    void setOndragend(Object ondragend);

    /**
     * Add drag end event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addDragEndEventListener(Object listener, boolean options) {
        addEventListener("dragend", listener, options);
    }

    /**
     * Add drag end event listener.
     *
     * @param listener the listener
     */
    default void addDragEndEventListener(Object listener) {
        addEventListener("dragend", listener);
    }

    /**
     * Remove drag end event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeDragEndEventListener(Object listener, boolean options) {
        removeEventListener("dragend", listener, options);
    }

    /**
     * Remove drag end event listener.
     *
     * @param listener the listener
     */
    default void removeDragEndEventListener(Object listener) {
        removeEventListener("dragend", listener);
    }

    /**
     * Fires on the target element when the user drags the object to a valid drop
     * target.
     *
     * @return the ondragenter
     */
    Object getOndragenter();

    /**
     * Sets ondragenter.
     *
     * @param ondragenter the ondragenter
     */
    void setOndragenter(Object ondragenter);

    /**
     * Add drag enter event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addDragEnterEventListener(Object listener, boolean options) {
        addEventListener("dragenter", listener, options);
    }

    /**
     * Add drag enter event listener.
     *
     * @param listener the listener
     */
    default void addDragEnterEventListener(Object listener) {
        addEventListener("dragenter", listener);
    }

    /**
     * Remove drag enter event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeDragEnterEventListener(Object listener, boolean options) {
        removeEventListener("dragenter", listener, options);
    }

    /**
     * Remove drag enter event listener.
     *
     * @param listener the listener
     */
    default void removeDragEnterEventListener(Object listener) {
        removeEventListener("dragenter", listener);
    }

    /**
     * Gets ondragexit.
     *
     * @return the ondragexit
     */
    Object getOndragexit();

    /**
     * Sets ondragexit.
     *
     * @param ondragexit the ondragexit
     */
    void setOndragexit(Object ondragexit);

    /**
     * Add drag exit event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addDragExitEventListener(Object listener, boolean options) {
        addEventListener("dragexit", listener, options);
    }

    /**
     * Add drag exit event listener.
     *
     * @param listener the listener
     */
    default void addDragExitEventListener(Object listener) {
        addEventListener("dragexit", listener);
    }

    /**
     * Remove drag exit event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeDragExitEventListener(Object listener, boolean options) {
        removeEventListener("dragexit", listener, options);
    }

    /**
     * Remove drag exit event listener.
     *
     * @param listener the listener
     */
    default void removeDragExitEventListener(Object listener) {
        removeEventListener("dragexit", listener);
    }

    /**
     * Fires on the target object when the user moves the mouse out of a valid drop
     * target during a drag operation.
     *
     * @return the ondragleave
     */
    Object getOndragleave();

    /**
     * Sets ondragleave.
     *
     * @param ondragleave the ondragleave
     */
    void setOndragleave(Object ondragleave);

    /**
     * Add drag leave event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addDragLeaveEventListener(Object listener, boolean options) {
        addEventListener("dragleave", listener, options);
    }

    /**
     * Add drag leave event listener.
     *
     * @param listener the listener
     */
    default void addDragLeaveEventListener(Object listener) {
        addEventListener("dragleave", listener);
    }

    /**
     * Remove drag leave event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeDragLeaveEventListener(Object listener, boolean options) {
        removeEventListener("dragleave", listener, options);
    }

    /**
     * Remove drag leave event listener.
     *
     * @param listener the listener
     */
    default void removeDragLeaveEventListener(Object listener) {
        removeEventListener("dragleave", listener);
    }

    /**
     * Fires on the target element continuously while the user drags the object over
     * a valid drop target.
     *
     * @return the ondragover
     */
    Object getOndragover();

    /**
     * Sets ondragover.
     *
     * @param ondragover the ondragover
     */
    void setOndragover(Object ondragover);

    /**
     * Add drag over event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addDragOverEventListener(Object listener, boolean options) {
        addEventListener("dragover", listener, options);
    }

    /**
     * Add drag over event listener.
     *
     * @param listener the listener
     */
    default void addDragOverEventListener(Object listener) {
        addEventListener("dragover", listener);
    }

    /**
     * Remove drag over event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeDragOverEventListener(Object listener, boolean options) {
        removeEventListener("dragover", listener, options);
    }

    /**
     * Remove drag over event listener.
     *
     * @param listener the listener
     */
    default void removeDragOverEventListener(Object listener) {
        removeEventListener("dragover", listener);
    }

    /**
     * Fires on the source object when the user starts to drag a text selection or
     * selected object.
     *
     * @return the ondragstart
     */
    Object getOndragstart();

    /**
     * Sets ondragstart.
     *
     * @param ondragstart the ondragstart
     */
    void setOndragstart(Object ondragstart);

    /**
     * Add drag start event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addDragStartEventListener(Object listener, boolean options) {
        addEventListener("dragstart", listener, options);
    }

    /**
     * Add drag start event listener.
     *
     * @param listener the listener
     */
    default void addDragStartEventListener(Object listener) {
        addEventListener("dragstart", listener);
    }

    /**
     * Remove drag start event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeDragStartEventListener(Object listener, boolean options) {
        removeEventListener("dragstart", listener, options);
    }

    /**
     * Remove drag start event listener.
     *
     * @param listener the listener
     */
    default void removeDragStartEventListener(Object listener) {
        removeEventListener("dragstart", listener);
    }

    /**
     * Gets ondrop.
     *
     * @return the ondrop
     */
    Object getOndrop();

    /**
     * Sets ondrop.
     *
     * @param ondrop the ondrop
     */
    void setOndrop(Object ondrop);

    /**
     * Add drop event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addDropEventListener(Object listener, boolean options) {
        addEventListener("drop", listener, options);
    }

    /**
     * Add drop event listener.
     *
     * @param listener the listener
     */
    default void addDropEventListener(Object listener) {
        addEventListener("drop", listener);
    }

    /**
     * Remove drop event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeDropEventListener(Object listener, boolean options) {
        removeEventListener("drop", listener, options);
    }

    /**
     * Remove drop event listener.
     *
     * @param listener the listener
     */
    default void removeDropEventListener(Object listener) {
        removeEventListener("drop", listener);
    }

    /**
     * Occurs when the duration attribute is updated.
     *
     * @return the ondurationchange
     */
    Object getOndurationchange();

    /**
     * Sets ondurationchange.
     *
     * @param ondurationchange the ondurationchange
     */
    void setOndurationchange(Object ondurationchange);

    /**
     * Add duration change event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addDurationChangeEventListener(Object listener, boolean options) {
        addEventListener("durationchange", listener, options);
    }

    /**
     * Add duration change event listener.
     *
     * @param listener the listener
     */
    default void addDurationChangeEventListener(Object listener) {
        addEventListener("durationchange", listener);
    }

    /**
     * Remove duration change event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeDurationChangeEventListener(Object listener, boolean options) {
        removeEventListener("durationchange", listener, options);
    }

    /**
     * Remove duration change event listener.
     *
     * @param listener the listener
     */
    default void removeDurationChangeEventListener(Object listener) {
        removeEventListener("durationchange", listener);
    }

    /**
     * Occurs when the media element is reset to its initial state.
     *
     * @return the onemptied
     */
    Object getOnemptied();

    /**
     * Sets onemptied.
     *
     * @param onemptied the onemptied
     */
    void setOnemptied(Object onemptied);

    /**
     * Add emptied event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addEmptiedEventListener(Object listener, boolean options) {
        addEventListener("emptied", listener, options);
    }

    /**
     * Add emptied event listener.
     *
     * @param listener the listener
     */
    default void addEmptiedEventListener(Object listener) {
        addEventListener("emptied", listener);
    }

    /**
     * Remove emptied event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeEmptiedEventListener(Object listener, boolean options) {
        removeEventListener("emptied", listener, options);
    }

    /**
     * Remove emptied event listener.
     *
     * @param listener the listener
     */
    default void removeEmptiedEventListener(Object listener) {
        removeEventListener("emptied", listener);
    }

    /**
     * Occurs when the end of playback is reached.
     *
     * @return the onended
     */
    Object getOnended();

    /**
     * Sets onended.
     *
     * @param onended the onended
     */
    void setOnended(Object onended);

    /**
     * Add ended event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addEndedEventListener(Object listener, boolean options) {
        addEventListener("ended", listener, options);
    }

    /**
     * Add ended event listener.
     *
     * @param listener the listener
     */
    default void addEndedEventListener(Object listener) {
        addEventListener("ended", listener);
    }

    /**
     * Remove ended event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeEndedEventListener(Object listener, boolean options) {
        removeEventListener("ended", listener, options);
    }

    /**
     * Remove ended event listener.
     *
     * @param listener the listener
     */
    default void removeEndedEventListener(Object listener) {
        removeEventListener("ended", listener);
    }

    /**
     * Fires when an error occurs during object loading.
     *
     * @return the onerror
     */
    Object getOnerror();

    /**
     * Sets onerror.
     *
     * @param onerror the onerror
     */
    void setOnerror(Object onerror);

    /**
     * Add error event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addErrorEventListener(Object listener, boolean options) {
        addEventListener("error", listener, options);
    }

    /**
     * Add error event listener.
     *
     * @param listener the listener
     */
    default void addErrorEventListener(Object listener) {
        addEventListener("error", listener);
    }

    /**
     * Remove error event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeErrorEventListener(Object listener, boolean options) {
        removeEventListener("error", listener, options);
    }

    /**
     * Remove error event listener.
     *
     * @param listener the listener
     */
    default void removeErrorEventListener(Object listener) {
        removeEventListener("error", listener);
    }

    /**
     * Fires when the object receives focus.
     *
     * @return the onfocus
     */
    Object getOnfocus();

    /**
     * Sets onfocus.
     *
     * @param onfocus the onfocus
     */
    void setOnfocus(Object onfocus);

    /**
     * Add focus event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addFocusEventListener(Object listener, boolean options) {
        addEventListener("focus", listener, options);
    }

    /**
     * Add focus event listener.
     *
     * @param listener the listener
     */
    default void addFocusEventListener(Object listener) {
        addEventListener("focus", listener);
    }

    /**
     * Remove focus event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeFocusEventListener(Object listener, boolean options) {
        removeEventListener("focus", listener, options);
    }

    /**
     * Remove focus event listener.
     *
     * @param listener the listener
     */
    default void removeFocusEventListener(Object listener) {
        removeEventListener("focus", listener);
    }

    /**
     * Gets onfocusin.
     *
     * @return the onfocusin
     */
    Object getOnfocusin();

    /**
     * Sets onfocusin.
     *
     * @param onfocusin the onfocusin
     */
    void setOnfocusin(Object onfocusin);

    /**
     * Add focus in event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addFocusInEventListener(Object listener, boolean options) {
        addEventListener("focusin", listener, options);
    }

    /**
     * Add focus in event listener.
     *
     * @param listener the listener
     */
    default void addFocusInEventListener(Object listener) {
        addEventListener("focusin", listener);
    }

    /**
     * Remove focus in event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeFocusInEventListener(Object listener, boolean options) {
        removeEventListener("focusin", listener, options);
    }

    /**
     * Remove focus in event listener.
     *
     * @param listener the listener
     */
    default void removeFocusInEventListener(Object listener) {
        removeEventListener("focusin", listener);
    }

    /**
     * Gets onfocusout.
     *
     * @return the onfocusout
     */
    Object getOnfocusout();

    /**
     * Sets onfocusout.
     *
     * @param onfocusout the onfocusout
     */
    void setOnfocusout(Object onfocusout);

    /**
     * Add focus out event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addFocusOutEventListener(Object listener, boolean options) {
        addEventListener("focusout", listener, options);
    }

    /**
     * Add focus out event listener.
     *
     * @param listener the listener
     */
    default void addFocusOutEventListener(Object listener) {
        addEventListener("focusout", listener);
    }

    /**
     * Remove focus out event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeFocusOutEventListener(Object listener, boolean options) {
        removeEventListener("focusout", listener, options);
    }

    /**
     * Remove focus out event listener.
     *
     * @param listener the listener
     */
    default void removeFocusOutEventListener(Object listener) {
        removeEventListener("focusout", listener);
    }

    /**
     * Gets ongotpointercapture.
     *
     * @return the ongotpointercapture
     */
    Object getOngotpointercapture();

    /**
     * Sets ongotpointercapture.
     *
     * @param ongotpointercapture the ongotpointercapture
     */
    void setOngotpointercapture(Object ongotpointercapture);

    /**
     * Add got pointer capture event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addGotPointerCaptureEventListener(Object listener, boolean options) {
        addEventListener("gotpointercapture", listener, options);
    }

    /**
     * Add got pointer capture event listener.
     *
     * @param listener the listener
     */
    default void addGotPointerCaptureEventListener(Object listener) {
        addEventListener("gotpointercapture", listener);
    }

    /**
     * Remove got pointer capture event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeGotPointerCaptureEventListener(Object listener, boolean options) {
        removeEventListener("gotpointercapture", listener, options);
    }

    /**
     * Remove got pointer capture event listener.
     *
     * @param listener the listener
     */
    default void removeGotPointerCaptureEventListener(Object listener) {
        removeEventListener("gotpointercapture", listener);
    }

    /**
     * Gets oninput.
     *
     * @return the oninput
     */
    Object getOninput();

    /**
     * Sets oninput.
     *
     * @param oninput the oninput
     */
    void setOninput(Object oninput);

    /**
     * Add input event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addInputEventListener(Object listener, boolean options) {
        addEventListener("input", listener, options);
    }

    /**
     * Add input event listener.
     *
     * @param listener the listener
     */
    default void addInputEventListener(Object listener) {
        addEventListener("input", listener);
    }

    /**
     * Remove input event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeInputEventListener(Object listener, boolean options) {
        removeEventListener("input", listener, options);
    }

    /**
     * Remove input event listener.
     *
     * @param listener the listener
     */
    default void removeInputEventListener(Object listener) {
        removeEventListener("input", listener);
    }

    /**
     * Gets oninvalid.
     *
     * @return the oninvalid
     */
    Object getOninvalid();

    /**
     * Sets oninvalid.
     *
     * @param oninvalid the oninvalid
     */
    void setOninvalid(Object oninvalid);

    /**
     * Add invalid event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addInvalidEventListener(Object listener, boolean options) {
        addEventListener("invalid", listener, options);
    }

    /**
     * Add invalid event listener.
     *
     * @param listener the listener
     */
    default void addInvalidEventListener(Object listener) {
        addEventListener("invalid", listener);
    }

    /**
     * Remove invalid event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeInvalidEventListener(Object listener, boolean options) {
        removeEventListener("invalid", listener, options);
    }

    /**
     * Remove invalid event listener.
     *
     * @param listener the listener
     */
    default void removeInvalidEventListener(Object listener) {
        removeEventListener("invalid", listener);
    }

    /**
     * <p>getOnfullscreenchange.</p>
     *
     * @return a {@link Object} object.
     */
    Object getOnfullscreenchange();

    /**
     * Fires when the user presses a key.
     *
     * @return the onkeydown
     */
    Object getOnkeydown();

    /**
     * Sets onkeydown.
     *
     * @param onkeydown the onkeydown
     */
    void setOnkeydown(Object onkeydown);

    /**
     * Add key down event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addKeyDownEventListener(Object listener, boolean options) {
        addEventListener("keydown", listener, options);
    }

    /**
     * Add key down event listener.
     *
     * @param listener the listener
     */
    default void addKeyDownEventListener(Object listener) {
        addEventListener("keydown", listener);
    }

    /**
     * Remove key down event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeKeyDownEventListener(Object listener, boolean options) {
        removeEventListener("keydown", listener, options);
    }

    /**
     * Remove key down event listener.
     *
     * @param listener the listener
     */
    default void removeKeyDownEventListener(Object listener) {
        removeEventListener("keydown", listener);
    }

    /**
     * Fires when the user presses an alphanumeric key.
     *
     * @return the onkeypress
     */
    Object getOnkeypress();

    /**
     * Sets onkeypress.
     *
     * @param onkeypress the onkeypress
     */
    void setOnkeypress(Object onkeypress);

    /**
     * Add key press event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addKeyPressEventListener(Object listener, boolean options) {
        addEventListener("keypress", listener, options);
    }

    /**
     * Add key press event listener.
     *
     * @param listener the listener
     */
    default void addKeyPressEventListener(Object listener) {
        addEventListener("keypress", listener);
    }

    /**
     * Remove key press event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeKeyPressEventListener(Object listener, boolean options) {
        removeEventListener("keypress", listener, options);
    }

    /**
     * Remove key press event listener.
     *
     * @param listener the listener
     */
    default void removeKeyPressEventListener(Object listener) {
        removeEventListener("keypress", listener);
    }

    /**
     * Fires when the user releases a key.
     *
     * @return the onkeyup
     */
    Object getOnkeyup();

    /**
     * Sets onkeyup.
     *
     * @param onkeyup the onkeyup
     */
    void setOnkeyup(Object onkeyup);

    /**
     * Add key up event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addKeyUpEventListener(Object listener, boolean options) {
        addEventListener("keyup", listener, options);
    }

    /**
     * Add key up event listener.
     *
     * @param listener the listener
     */
    default void addKeyUpEventListener(Object listener) {
        addEventListener("keyup", listener);
    }

    /**
     * Remove key up event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeKeyUpEventListener(Object listener, boolean options) {
        removeEventListener("keyup", listener, options);
    }

    /**
     * Remove key up event listener.
     *
     * @param listener the listener
     */
    default void removeKeyUpEventListener(Object listener) {
        removeEventListener("keyup", listener);
    }

    /**
     * Fires immediately after the browser loads the object.
     *
     * @return the onload
     */
    Object getOnload();

    /**
     * Sets onload.
     *
     * @param onload the onload
     */
    void setOnload(Object onload);

    /**
     * Add load event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addLoadEventListener(Object listener, boolean options) {
        addEventListener("load", listener, options);
    }

    /**
     * Add load event listener.
     *
     * @param listener the listener
     */
    default void addLoadEventListener(Object listener) {
        addEventListener("load", listener);
    }

    /**
     * Remove load event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeLoadEventListener(Object listener, boolean options) {
        removeEventListener("load", listener, options);
    }

    /**
     * Remove load event listener.
     *
     * @param listener the listener
     */
    default void removeLoadEventListener(Object listener) {
        removeEventListener("load", listener);
    }

    /**
     * Occurs when media data is loaded at the current playback position.
     *
     * @return the onloadeddata
     */
    Object getOnloadeddata();

    /**
     * Sets onloadeddata.
     *
     * @param onloadeddata the onloadeddata
     */
    void setOnloadeddata(Object onloadeddata);

    /**
     * Add loaded data event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addLoadedDataEventListener(Object listener, boolean options) {
        addEventListener("loadeddata", listener, options);
    }

    /**
     * Add loaded data event listener.
     *
     * @param listener the listener
     */
    default void addLoadedDataEventListener(Object listener) {
        addEventListener("loadeddata", listener);
    }

    /**
     * Remove loaded data event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeLoadedDataEventListener(Object listener, boolean options) {
        removeEventListener("loadeddata", listener, options);
    }

    /**
     * Remove loaded data event listener.
     *
     * @param listener the listener
     */
    default void removeLoadedDataEventListener(Object listener) {
        removeEventListener("loadeddata", listener);
    }

    /**
     * Occurs when the duration and dimensions of the media have been determined.
     *
     * @return the onloadedmetadata
     */
    Object getOnloadedmetadata();

    /**
     * Sets onloadedmetadata.
     *
     * @param onloadedmetadata the onloadedmetadata
     */
    void setOnloadedmetadata(Object onloadedmetadata);

    /**
     * Add loaded meta data event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addLoadedMetaDataEventListener(Object listener, boolean options) {
        addEventListener("loadedmetadata", listener, options);
    }

    /**
     * Add loaded meta data event listener.
     *
     * @param listener the listener
     */
    default void addLoadedMetaDataEventListener(Object listener) {
        addEventListener("loadedmetadata", listener);
    }

    /**
     * Remove loaded meta data event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeLoadedMetaDataEventListener(Object listener, boolean options) {
        removeEventListener("loadedmetadata", listener, options);
    }

    /**
     * Remove loaded meta data event listener.
     *
     * @param listener the listener
     */
    default void removeLoadedMetaDataEventListener(Object listener) {
        removeEventListener("loadedmetadata", listener);
    }

    /**
     * Gets onloadend.
     *
     * @return the onloadend
     */
    Object getOnloadend();

    /**
     * Sets onloadend.
     *
     * @param onloadend the onloadend
     */
    void setOnloadend(Object onloadend);

    /**
     * Add load end event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addLoadEndEventListener(Object listener, boolean options) {
        addEventListener("loadend", listener, options);
    }

    /**
     * Add load end event listener.
     *
     * @param listener the listener
     */
    default void addLoadEndEventListener(Object listener) {
        addEventListener("loadend", listener);
    }

    /**
     * Remove load end event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeLoadEndEventListener(Object listener, boolean options) {
        removeEventListener("loadend", listener, options);
    }

    /**
     * Remove load end event listener.
     *
     * @param listener the listener
     */
    default void removeLoadEndEventListener(Object listener) {
        removeEventListener("loadend", listener);
    }

    /**
     * Occurs when Internet Explorer begins looking for media data.
     *
     * @return the onloadstart
     */
    Object getOnloadstart();

    /**
     * Sets onloadstart.
     *
     * @param onloadstart the onloadstart
     */
    void setOnloadstart(Object onloadstart);

    /**
     * Add load start event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addLoadStartEventListener(Object listener, boolean options) {
        addEventListener("loadstart", listener, options);
    }

    /**
     * Add load start event listener.
     *
     * @param listener the listener
     */
    default void addLoadStartEventListener(Object listener) {
        addEventListener("loadstart", listener);
    }

    /**
     * Remove load start event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeLoadStartEventListener(Object listener, boolean options) {
        removeEventListener("loadstart", listener, options);
    }

    /**
     * Remove load start event listener.
     *
     * @param listener the listener
     */
    default void removeLoadStartEventListener(Object listener) {
        removeEventListener("loadstart", listener);
    }

    /**
     * Gets onlostpointercapture.
     *
     * @return the onlostpointercapture
     */
    Object getOnlostpointercapture();

    /**
     * Sets onlostpointercapture.
     *
     * @param onlostpointercapture the onlostpointercapture
     */
    void setOnlostpointercapture(Object onlostpointercapture);

    /**
     * Add lost pointer capture event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addLostPointerCaptureEventListener(Object listener, boolean options) {
        addEventListener("lostpointercapture", listener, options);
    }

    /**
     * Add lost pointer capture event listener.
     *
     * @param listener the listener
     */
    default void addLostPointerCaptureEventListener(Object listener) {
        addEventListener("lostpointercapture", listener);
    }

    /**
     * Remove lost pointer capture event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeLostPointerCaptureEventListener(Object listener, boolean options) {
        removeEventListener("lostpointercapture", listener, options);
    }

    /**
     * Remove lost pointer capture event listener.
     *
     * @param listener the listener
     */
    default void removeLostPointerCaptureEventListener(Object listener) {
        removeEventListener("lostpointercapture", listener);
    }

    /**
     * Fires when the user clicks the object with either mouse button.
     *
     * @return the onmousedown
     */
    Object getOnmousedown();

    /**
     * Sets onmousedown.
     *
     * @param onmousedown the onmousedown
     */
    void setOnmousedown(Object onmousedown);

    /**
     * Add mouse down event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addMouseDownEventListener(Object listener, boolean options) {
        addEventListener("mousedown", listener, options);
    }

    /**
     * Add mouse down event listener.
     *
     * @param listener the listener
     */
    default void addMouseDownEventListener(Object listener) {
        addEventListener("mousedown", listener);
    }

    /**
     * Remove mouse down event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeMouseDownEventListener(Object listener, boolean options) {
        removeEventListener("mousedown", listener, options);
    }

    /**
     * Remove mouse down event listener.
     *
     * @param listener the listener
     */
    default void removeMouseDownEventListener(Object listener) {
        removeEventListener("mousedown", listener);
    }

    /**
     * Gets onmouseenter.
     *
     * @return the onmouseenter
     */
    Object getOnmouseenter();

    /**
     * Sets onmouseenter.
     *
     * @param onmouseenter the onmouseenter
     */
    void setOnmouseenter(Object onmouseenter);

    /**
     * Add mouse enter event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addMouseEnterEventListener(Object listener, boolean options) {
        addEventListener("mouseenter", listener, options);
    }

    /**
     * Add mouse enter event listener.
     *
     * @param listener the listener
     */
    default void addMouseEnterEventListener(Object listener) {
        addEventListener("mouseenter", listener);
    }

    /**
     * Remove mouse enter event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeMouseEnterEventListener(Object listener, boolean options) {
        removeEventListener("mouseenter", listener, options);
    }

    /**
     * Remove mouse enter event listener.
     *
     * @param listener the listener
     */
    default void removeMouseEnterEventListener(Object listener) {
        removeEventListener("mouseenter", listener);
    }

    /**
     * Gets onmouseleave.
     *
     * @return the onmouseleave
     */
    Object getOnmouseleave();

    /**
     * Sets onmouseleave.
     *
     * @param onmouseleave the onmouseleave
     */
    void setOnmouseleave(Object onmouseleave);

    /**
     * Add mouse leave event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addMouseLeaveEventListener(Object listener, boolean options) {
        addEventListener("mouseleave", listener, options);
    }

    /**
     * Add mouse leave event listener.
     *
     * @param listener the listener
     */
    default void addMouseLeaveEventListener(Object listener) {
        addEventListener("mouseleave", listener);
    }

    /**
     * Remove mouse leave event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeMouseLeaveEventListener(Object listener, boolean options) {
        removeEventListener("mouseleave", listener, options);
    }

    /**
     * Remove mouse leave event listener.
     *
     * @param listener the listener
     */
    default void removeMouseLeaveEventListener(Object listener) {
        removeEventListener("mouseleave", listener);
    }

    /**
     * Fires when the user moves the mouse over the object.
     *
     * @return the onmousemove
     */
    Object getOnmousemove();

    /**
     * Sets onmousemove.
     *
     * @param onmousemove the onmousemove
     */
    void setOnmousemove(Object onmousemove);

    /**
     * Add mouse move event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addMouseMoveEventListener(Object listener, boolean options) {
        addEventListener("mousemove", listener, options);
    }

    /**
     * Add mouse move event listener.
     *
     * @param listener the listener
     */
    default void addMouseMoveEventListener(Object listener) {
        addEventListener("mousemove", listener);
    }

    /**
     * Remove mouse move event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeMouseMoveEventListener(Object listener, boolean options) {
        removeEventListener("mousemove", listener, options);
    }

    /**
     * Remove mouse move event listener.
     *
     * @param listener the listener
     */
    default void removeMouseMoveEventListener(Object listener) {
        removeEventListener("mousemove", listener);
    }

    /**
     * Fires when the user moves the mouse pointer outside the boundaries of the
     * object.
     *
     * @return the onmouseout
     */
    Object getOnmouseout();

    /**
     * Sets onmouseout.
     *
     * @param onmouseout the onmouseout
     */
    void setOnmouseout(Object onmouseout);

    /**
     * Add mouse out event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addMouseOutEventListener(Object listener, boolean options) {
        addEventListener("mouseout", listener, options);
    }

    /**
     * Add mouse out event listener.
     *
     * @param listener the listener
     */
    default void addMouseOutEventListener(Object listener) {
        addEventListener("mouseout", listener);
    }

    /**
     * Remove mouse out event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeMouseOutEventListener(Object listener, boolean options) {
        removeEventListener("mouseout", listener, options);
    }

    /**
     * Remove mouse out event listener.
     *
     * @param listener the listener
     */
    default void removeMouseOutEventListener(Object listener) {
        removeEventListener("mouseout", listener);
    }

    /**
     * Fires when the user moves the mouse pointer into the object.
     *
     * @return the onmouseover
     */
    Object getOnmouseover();

    /**
     * Sets onmouseover.
     *
     * @param onmouseover the onmouseover
     */
    void setOnmouseover(Object onmouseover);

    /**
     * Add mouse over event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addMouseOverEventListener(Object listener, boolean options) {
        addEventListener("mouseover", listener, options);
    }

    /**
     * Add mouse over event listener.
     *
     * @param listener the listener
     */
    default void addMouseOverEventListener(Object listener) {
        addEventListener("mouseover", listener);
    }

    /**
     * Remove mouse over event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeMouseOverEventListener(Object listener, boolean options) {
        removeEventListener("mouseover", listener, options);
    }

    /**
     * Remove mouse over event listener.
     *
     * @param listener the listener
     */
    default void removeMouseOverEventListener(Object listener) {
        removeEventListener("mouseover", listener);
    }

    /**
     * Fires when the user releases a mouse button while the mouse is over the
     * object.
     *
     * @return the onmouseup
     */
    Object getOnmouseup();

    /**
     * Sets onmouseup.
     *
     * @param onmouseup the onmouseup
     */
    void setOnmouseup(Object onmouseup);

    /**
     * Add mouse up event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addMouseUpEventListener(Object listener, boolean options) {
        addEventListener("mouseup", listener, options);
    }

    /**
     * Add mouse up event listener.
     *
     * @param listener the listener
     */
    default void addMouseUpEventListener(Object listener) {
        addEventListener("mouseup", listener);
    }

    /**
     * Remove mouse up event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeMouseUpEventListener(Object listener, boolean options) {
        removeEventListener("mouseup", listener, options);
    }

    /**
     * Remove mouse up event listener.
     *
     * @param listener the listener
     */
    default void removeMouseUpEventListener(Object listener) {
        removeEventListener("mouseup", listener);
    }

    /**
     * Occurs when playback is paused.
     *
     * @return the onpause
     */
    Object getOnpause();

    /**
     * Sets onpause.
     *
     * @param onpause the onpause
     */
    void setOnpause(Object onpause);

    /**
     * Add pause event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addPauseEventListener(Object listener, boolean options) {
        addEventListener("pause", listener, options);
    }

    /**
     * Add pause event listener.
     *
     * @param listener the listener
     */
    default void addPauseEventListener(Object listener) {
        addEventListener("pause", listener);
    }

    /**
     * Remove pause event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removePauseEventListener(Object listener, boolean options) {
        removeEventListener("pause", listener, options);
    }

    /**
     * Remove pause event listener.
     *
     * @param listener the listener
     */
    default void removePauseEventListener(Object listener) {
        removeEventListener("pause", listener);
    }

    /**
     * Occurs when the play method is requested.
     *
     * @return the onplay
     */
    Object getOnplay();

    /**
     * Sets onplay.
     *
     * @param onplay the onplay
     */
    void setOnplay(Object onplay);

    /**
     * Add play event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addPlayEventListener(Object listener, boolean options) {
        addEventListener("play", listener, options);
    }

    /**
     * Add play event listener.
     *
     * @param listener the listener
     */
    default void addPlayEventListener(Object listener) {
        addEventListener("play", listener);
    }

    /**
     * Remove play event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removePlayEventListener(Object listener, boolean options) {
        removeEventListener("play", listener, options);
    }

    /**
     * Remove play event listener.
     *
     * @param listener the listener
     */
    default void removePlayEventListener(Object listener) {
        removeEventListener("play", listener);
    }

    /**
     * Occurs when the audio or video has started playing.
     *
     * @return the onplaying
     */
    Object getOnplaying();

    /**
     * Sets onplaying.
     *
     * @param onplaying the onplaying
     */
    void setOnplaying(Object onplaying);

    /**
     * Add playing event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addPlayingEventListener(Object listener, boolean options) {
        addEventListener("playing", listener, options);
    }

    /**
     * Add playing event listener.
     *
     * @param listener the listener
     */
    default void addPlayingEventListener(Object listener) {
        addEventListener("playing", listener);
    }

    /**
     * Remove playing event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removePlayingEventListener(Object listener, boolean options) {
        removeEventListener("playing", listener, options);
    }

    /**
     * Remove playing event listener.
     *
     * @param listener the listener
     */
    default void removePlayingEventListener(Object listener) {
        removeEventListener("playing", listener);
    }

    /**
     * Gets onpointercancel.
     *
     * @return the onpointercancel
     */
    Object getOnpointercancel();

    /**
     * Sets onpointercancel.
     *
     * @param onpointercancel the onpointercancel
     */
    void setOnpointercancel(Object onpointercancel);

    /**
     * Add pointer cancel event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addPointerCancelEventListener(Object listener, boolean options) {
        addEventListener("pointercancel", listener, options);
    }

    /**
     * Add pointer cancel event listener.
     *
     * @param listener the listener
     */
    default void addPointerCancelEventListener(Object listener) {
        addEventListener("pointercancel", listener);
    }

    /**
     * Remove pointer cancel event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removePointerCancelEventListener(Object listener, boolean options) {
        removeEventListener("pointercancel", listener, options);
    }

    /**
     * Remove pointer cancel event listener.
     *
     * @param listener the listener
     */
    default void removePointerCancelEventListener(Object listener) {
        removeEventListener("pointercancel", listener);
    }

    /**
     * Gets onpointerdown.
     *
     * @return the onpointerdown
     */
    Object getOnpointerdown();

    /**
     * Sets onpointerdown.
     *
     * @param onpointerdown the onpointerdown
     */
    void setOnpointerdown(Object onpointerdown);

    /**
     * Add pointer down event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addPointerDownEventListener(Object listener, boolean options) {
        addEventListener("pointerdown", listener, options);
    }

    /**
     * Add pointer down event listener.
     *
     * @param listener the listener
     */
    default void addPointerDownEventListener(Object listener) {
        addEventListener("pointerdown", listener);
    }

    /**
     * Remove pointer down event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removePointerDownEventListener(Object listener, boolean options) {
        removeEventListener("pointerdown", listener, options);
    }

    /**
     * Remove pointer down event listener.
     *
     * @param listener the listener
     */
    default void removePointerDownEventListener(Object listener) {
        removeEventListener("pointerdown", listener);
    }

    /**
     * Gets onpointerenter.
     *
     * @return the onpointerenter
     */
    Object getOnpointerenter();

    /**
     * Sets onpointerenter.
     *
     * @param onpointerenter the onpointerenter
     */
    void setOnpointerenter(Object onpointerenter);

    /**
     * Add pointer enter event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addPointerEnterEventListener(Object listener, boolean options) {
        addEventListener("pointerenter", listener, options);
    }

    /**
     * Add pointer enter event listener.
     *
     * @param listener the listener
     */
    default void addPointerEnterEventListener(Object listener) {
        addEventListener("pointerenter", listener);

    }

    /**
     * Remove pointer enter event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removePointerEnterEventListener(Object listener, boolean options) {
        removeEventListener("pointerenter", listener, options);
    }

    /**
     * Remove pointer enter event listener.
     *
     * @param listener the listener
     */
    default void removePointerEnterEventListener(Object listener) {
        removeEventListener("pointerenter", listener);
    }

    /**
     * Gets onpointerleave.
     *
     * @return the onpointerleave
     */
    Object getOnpointerleave();

    /**
     * Sets onpointerleave.
     *
     * @param onpointerleave the onpointerleave
     */
    void setOnpointerleave(Object onpointerleave);

    /**
     * Add pointer leave event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addPointerLeaveEventListener(Object listener, boolean options) {
        addEventListener("pointerleave", listener, options);
    }

    /**
     * Add pointer leave event listener.
     *
     * @param listener the listener
     */
    default void addPointerLeaveEventListener(Object listener) {
        addEventListener("pointerleave", listener);
    }

    /**
     * Remove pointer leave event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removePointerLeaveEventListener(Object listener, boolean options) {
        removeEventListener("pointerleave", listener, options);
    }

    /**
     * Remove pointer leave event listener.
     *
     * @param listener the listener
     */
    default void removePointerLeaveEventListener(Object listener) {
        removeEventListener("pointerleave", listener);
    }

    /**
     * Gets onpointermove.
     *
     * @return the onpointermove
     */
    Object getOnpointermove();

    /**
     * Sets onpointermove.
     *
     * @param onpointermove the onpointermove
     */
    void setOnpointermove(Object onpointermove);

    /**
     * Add pointer move event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addPointerMoveEventListener(Object listener, boolean options) {
        addEventListener("pointermove", listener, options);
    }

    /**
     * Add pointer move event listener.
     *
     * @param listener the listener
     */
    default void addPointerMoveEventListener(Object listener) {
        addEventListener("pointermove", listener);
    }

    /**
     * Remove pointer move event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removePointerMoveEventListener(Object listener, boolean options) {
        removeEventListener("pointermove", listener, options);
    }

    /**
     * Remove pointer move event listener.
     *
     * @param listener the listener
     */
    default void removePointerMoveEventListener(Object listener) {
        removeEventListener("pointermove", listener);
    }

    /**
     * Gets onpointerout.
     *
     * @return the onpointerout
     */
    Object getOnpointerout();

    /**
     * Sets onpointerout.
     *
     * @param onpointerout the onpointerout
     */
    void setOnpointerout(Object onpointerout);

    /**
     * Add pointer out event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addPointerOutEventListener(Object listener, boolean options) {
        addEventListener("pointerout", listener, options);
    }

    /**
     * Add pointer out event listener.
     *
     * @param listener the listener
     */
    default void addPointerOutEventListener(Object listener) {
        addEventListener("pointerout", listener);
    }

    /**
     * Remove pointer out event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removePointerOutEventListener(Object listener, boolean options) {
        removeEventListener("pointerout", listener, options);
    }

    /**
     * Remove pointer out event listener.
     *
     * @param listener the listener
     */
    default void removePointerOutEventListener(Object listener) {
        removeEventListener("pointerout", listener);
    }

    /**
     * Gets onpointerover.
     *
     * @return the onpointerover
     */
    Object getOnpointerover();

    /**
     * Sets onpointerover.
     *
     * @param onpointerover the onpointerover
     */
    void setOnpointerover(Object onpointerover);

    /**
     * Add pointer over event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addPointerOverEventListener(Object listener, boolean options) {
        addEventListener("pointerover", listener, options);
    }

    /**
     * Add pointer over event listener.
     *
     * @param listener the listener
     */
    default void addPointerOverEventListener(Object listener) {
        addEventListener("pointerover", listener);
    }

    /**
     * Remove pointer over event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removePointerOverEventListener(Object listener, boolean options) {
        removeEventListener("pointerover", listener, options);
    }

    /**
     * Remove pointer over event listener.
     *
     * @param listener the listener
     */
    default void removePointerOverEventListener(Object listener) {
        removeEventListener("pointerover", listener);
    }

    /**
     * Gets onpointerup.
     *
     * @return the onpointerup
     */
    Object getOnpointerup();

    /**
     * Sets onpointerup.
     *
     * @param onpointerup the onpointerup
     */
    void setOnpointerup(Object onpointerup);

    /**
     * Add pointer up event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addPointerUpEventListener(Object listener, boolean options) {
        addEventListener("pointerup", listener, options);
    }

    /**
     * Add pointer up event listener.
     *
     * @param listener the listener
     */
    default void addPointerUpEventListener(Object listener) {
        addEventListener("pointerup", listener);
    }

    /**
     * Remove pointer up event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removePointerUpEventListener(Object listener, boolean options) {
        removeEventListener("pointerup", listener, options);
    }

    /**
     * Remove pointer up event listener.
     *
     * @param listener the listener
     */
    default void removePointerUpEventListener(Object listener) {
        removeEventListener("pointerup", listener);
    }

    /**
     * Occurs to indicate progress while downloading media data.
     *
     * @return the onprogress
     */
    Object getOnprogress();

    /**
     * Sets onprogress.
     *
     * @param onprogress the onprogress
     */
    void setOnprogress(Object onprogress);

    /**
     * Add progress event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addProgressEventListener(Object listener, boolean options) {
        addEventListener("progress", listener, options);
    }

    /**
     * Add progress event listener.
     *
     * @param listener the listener
     */
    default void addProgressEventListener(Object listener) {
        addEventListener("progress", listener);
    }

    /**
     * Remove progress event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeProgressEventListener(Object listener, boolean options) {
        removeEventListener("progress", listener, options);
    }

    /**
     * Remove progress event listener.
     *
     * @param listener the listener
     */
    default void removeProgressEventListener(Object listener) {
        removeEventListener("progress", listener);
    }

    /**
     * Occurs when the playback rate is increased or decreased.
     *
     * @return the onratechange
     */
    Object getOnratechange();

    /**
     * Sets onratechange.
     *
     * @param onratechange the onratechange
     */
    void setOnratechange(Object onratechange);

    /**
     * Add rate change event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addRateChangeEventListener(Object listener, boolean options) {
        addEventListener("ratechange", listener, options);
    }

    /**
     * Add rate change event listener.
     *
     * @param listener the listener
     */
    default void addRateChangeEventListener(Object listener) {
        addEventListener("ratechange", listener);
    }

    /**
     * Remove rate change event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeRateChangeEventListener(Object listener, boolean options) {
        removeEventListener("ratechange", listener, options);
    }

    /**
     * Remove rate change event listener.
     *
     * @param listener the listener
     */
    default void removeRateChangeEventListener(Object listener) {
        removeEventListener("ratechange", listener);
    }

    /**
     * Fires when the user resets a form.
     *
     * @return the onreset
     */
    Object getOnreset();

    /**
     * Sets onreset.
     *
     * @param onreset the onreset
     */
    void setOnreset(Object onreset);

    /**
     * Add reset event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addResetEventListener(Object listener, boolean options) {
        addEventListener("reset", listener, options);
    }

    /**
     * Add reset event listener.
     *
     * @param listener the listener
     */
    default void addResetEventListener(Object listener) {
        addEventListener("reset", listener);
    }

    /**
     * Remove reset event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeResetEventListener(Object listener, boolean options) {
        removeEventListener("reset", listener, options);
    }

    /**
     * Remove reset event listener.
     *
     * @param listener the listener
     */
    default void removeResetEventListener(Object listener) {
        removeEventListener("reset", listener);
    }

    /**
     * Gets onresize.
     *
     * @return the onresize
     */
    Object getOnresize();

    /**
     * Sets onresize.
     *
     * @param onresize the onresize
     */
    void setOnresize(Object onresize);

    /**
     * Add resize event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addResizeEventListener(Object listener, boolean options) {
        addEventListener("resize", listener, options);
    }

    /**
     * Add resize event listener.
     *
     * @param listener the listener
     */
    default void addResizeEventListener(Object listener) {
        addEventListener("resize", listener);
    }

    /**
     * Remove resize event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeResizeEventListener(Object listener, boolean options) {
        removeEventListener("resize", listener, options);
    }

    /**
     * Remove resize event listener.
     *
     * @param listener the listener
     */
    default void removeResizeEventListener(Object listener) {
        removeEventListener("resize", listener);
    }

    /**
     * Fires when the user repositions the scroll box in the scroll bar on the
     * object.
     *
     * @return the onscroll
     */
    Object getOnscroll();

    /**
     * Sets onscroll.
     *
     * @param onscroll the onscroll
     */
    void setOnscroll(Object onscroll);

    /**
     * Add scroll event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addScrollEventListener(Object listener, boolean options) {
        addEventListener("scroll", listener, options);
    }

    /**
     * Add scroll event listener.
     *
     * @param listener the listener
     */
    default void addScrollEventListener(Object listener) {
        addEventListener("scroll", listener);
    }

    /**
     * Remove scroll event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeScrollEventListener(Object listener, boolean options) {
        removeEventListener("scroll", listener, options);
    }

    /**
     * Remove scroll event listener.
     *
     * @param listener the listener
     */
    default void removeScrollEventListener(Object listener) {
        removeEventListener("scroll", listener);
    }

    /**
     * Gets onsecuritypolicyviolation.
     *
     * @return the onsecuritypolicyviolation
     */
    Object getOnsecuritypolicyviolation();

    /**
     * Sets onsecuritypolicyviolation.
     *
     * @param onsecuritypolicyviolation the onsecuritypolicyviolation
     */
    void setOnsecuritypolicyviolation(Object onsecuritypolicyviolation);

    /**
     * Add security policy violation event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addSecurityPolicyViolationEventListener(Object listener, boolean options) {
        addEventListener("securitypolicyviolation", listener, options);
    }

    /**
     * Add security policy violation event listener.
     *
     * @param listener the listener
     */
    default void addSecurityPolicyViolationEventListener(Object listener) {
        addEventListener("securitypolicyviolation", listener);
    }

    /**
     * Remove security policy violation event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeSecurityPolicyViolationEventListener(Object listener, boolean options) {
        removeEventListener("securitypolicyviolation", listener, options);
    }

    /**
     * Remove security policy violation event listener.
     *
     * @param listener the listener
     */
    default void removeSecurityPolicyViolationEventListener(Object listener) {
        removeEventListener("securitypolicyviolation", listener);
    }

    /**
     * Occurs when the seek operation ends.
     *
     * @return the onseeked
     */
    Object getOnseeked();

    /**
     * Sets onseeked.
     *
     * @param onseeked the onseeked
     */
    void setOnseeked(Object onseeked);

    /**
     * Add seeked event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addSeekedEventListener(Object listener, boolean options) {
        addEventListener("seeked", listener, options);
    }

    /**
     * Add seeked event listener.
     *
     * @param listener the listener
     */
    default void addSeekedEventListener(Object listener) {
        addEventListener("seeked", listener);
    }

    /**
     * Remove seeked event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeSeekedEventListener(Object listener, boolean options) {
        removeEventListener("seeked", listener, options);
    }

    /**
     * Remove seeked event listener.
     *
     * @param listener the listener
     */
    default void removeSeekedEventListener(Object listener) {
        removeEventListener("seeked", listener);
    }

    /**
     * Occurs when the current playback position is moved.
     *
     * @return the onseeking
     */
    Object getOnseeking();

    /**
     * Sets onseeking.
     *
     * @param onseeking the onseeking
     */
    void setOnseeking(Object onseeking);

    /**
     * Add seeking event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addSeekingEventListener(Object listener, boolean options) {
        addEventListener("seeking", listener, options);
    }

    /**
     * Add seeking event listener.
     *
     * @param listener the listener
     */
    default void addSeekingEventListener(Object listener) {
        addEventListener("seeking", listener);
    }

    /**
     * Remove seeking event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeSeekingEventListener(Object listener, boolean options) {
        removeEventListener("seeking", listener, options);
    }

    /**
     * Remove seeking event listener.
     *
     * @param listener the listener
     */
    default void removeSeekingEventListener(Object listener) {
        removeEventListener("seeking", listener);
    }

    /**
     * Fires when the current selection changes.
     *
     * @return the onselect
     */
    Object getOnselect();

    /**
     * Sets onselect.
     *
     * @param onselect the onselect
     */
    void setOnselect(Object onselect);

    /**
     * Add select event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addSelectEventListener(Object listener, boolean options) {
        addEventListener("select", listener, options);
    }

    /**
     * Add select event listener.
     *
     * @param listener the listener
     */
    default void addSelectEventListener(Object listener) {
        addEventListener("select", listener);
    }

    /**
     * Remove select event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeSelectEventListener(Object listener, boolean options) {
        removeEventListener("select", listener, options);
    }

    /**
     * Remove select event listener.
     *
     * @param listener the listener
     */
    default void removeSelectEventListener(Object listener) {
        removeEventListener("select", listener);
    }

    /**
     * Gets onselectionchange.
     *
     * @return the onselectionchange
     */
    Object getOnselectionchange();

    /**
     * Sets onselectionchange.
     *
     * @param onselectionchange the onselectionchange
     */
    void setOnselectionchange(Object onselectionchange);

    /**
     * Add selection change event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addSelectionChangeEventListener(Object listener, boolean options) {
        addEventListener("selectionchange", listener, options);
    }

    /**
     * Add selection change event listener.
     *
     * @param listener the listener
     */
    default void addSelectionChangeEventListener(Object listener) {
        addEventListener("selectionchange", listener);
    }

    /**
     * Remove selection change event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeSelectionChangeEventListener(Object listener, boolean options) {
        removeEventListener("selectionchange", listener, options);
    }

    /**
     * Remove selection change event listener.
     *
     * @param listener the listener
     */
    default void removeSelectionChangeEventListener(Object listener) {
        removeEventListener("selectionchange", listener);
    }

    /**
     * Gets onselectstart.
     *
     * @return the onselectstart
     */
    Object getOnselectstart();

    /**
     * Sets onselectstart.
     *
     * @param onselectstart the onselectstart
     */
    void setOnselectstart(Object onselectstart);

    /**
     * Add select start event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addSelectStartEventListener(Object listener, boolean options) {
        addEventListener("selectstart", listener, options);
    }

    /**
     * Add select start event listener.
     *
     * @param listener the listener
     */
    default void addSelectStartEventListener(Object listener) {
        addEventListener("selectstart", listener);
    }

    /**
     * Remove select start event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeSelectStartEventListener(Object listener, boolean options) {
        removeEventListener("selectstart", listener, options);
    }

    /**
     * Remove select start event listener.
     *
     * @param listener the listener
     */
    default void removeSelectStartEventListener(Object listener) {
        removeEventListener("selectstart", listener);
    }

    /**
     * Occurs when the download has stopped.
     *
     * @return the onstalled
     */
    Object getOnstalled();

    /**
     * Sets onstalled.
     *
     * @param onstalled the onstalled
     */
    void setOnstalled(Object onstalled);

    /**
     * Add stalled event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addStalledEventListener(Object listener, boolean options) {
        addEventListener("stalled", listener, options);
    }

    /**
     * Add stalled event listener.
     *
     * @param listener the listener
     */
    default void addStalledEventListener(Object listener) {
        addEventListener("stalled", listener);
    }

    /**
     * Remove stalled event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeStalledEventListener(Object listener, boolean options) {
        removeEventListener("stalled", listener, options);
    }

    /**
     * Remove stalled event listener.
     *
     * @param listener the listener
     */
    default void removeStalledEventListener(Object listener) {
        removeEventListener("stalled", listener);
    }

    /**
     * Gets onsubmit.
     *
     * @return the onsubmit
     */
    Object getOnsubmit();

    /**
     * Sets onsubmit.
     *
     * @param onsubmit the onsubmit
     */
    void setOnsubmit(Object onsubmit);

    /**
     * Add submit event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addSubmitEventListener(Object listener, boolean options) {
        addEventListener("submit", listener, options);
    }

    /**
     * Add submit event listener.
     *
     * @param listener the listener
     */
    default void addSubmitEventListener(Object listener) {
        addEventListener("submit", listener);
    }

    /**
     * Remove submit event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeSubmitEventListener(Object listener, boolean options) {
        removeEventListener("submit", listener, options);
    }

    /**
     * Remove submit event listener.
     *
     * @param listener the listener
     */
    default void removeSubmitEventListener(Object listener) {
        removeEventListener("submit", listener);
    }

    /**
     * Occurs if the load operation has been intentionally halted.
     *
     * @return the onsuspend
     */
    Object getOnsuspend();

    /**
     * Sets onsuspend.
     *
     * @param onsuspend the onsuspend
     */
    void setOnsuspend(Object onsuspend);

    /**
     * Add suspend event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addSuspendEventListener(Object listener, boolean options) {
        addEventListener("suspend", listener, options);
    }

    /**
     * Add suspend event listener.
     *
     * @param listener the listener
     */
    default void addSuspendEventListener(Object listener) {
        addEventListener("suspend", listener);
    }

    /**
     * Remove suspend event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeSuspendEventListener(Object listener, boolean options) {
        removeEventListener("suspend", listener, options);
    }

    /**
     * Remove suspend event listener.
     *
     * @param listener the listener
     */
    default void removeSuspendEventListener(Object listener) {
        removeEventListener("suspend", listener);
    }

    /**
     * Occurs to indicate the current playback position.
     *
     * @return the ontimeupdate
     */
    Object getOntimeupdate();

    /**
     * Sets ontimeupdate.
     *
     * @param ontimeupdate the ontimeupdate
     */
    void setOntimeupdate(Object ontimeupdate);

    /**
     * Add time update event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addTimeUpdateEventListener(Object listener, boolean options) {
        addEventListener("timeupdate", listener, options);
    }

    /**
     * Add time update event listener.
     *
     * @param listener the listener
     */
    default void addTimeUpdateEventListener(Object listener) {
        addEventListener("timeupdate", listener);
    }

    /**
     * Remove time update event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeTimeUpdateEventListener(Object listener, boolean options) {
        removeEventListener("timeupdate", listener, options);
    }

    /**
     * Remove time update event listener.
     *
     * @param listener the listener
     */
    default void removeTimeUpdateEventListener(Object listener) {
        removeEventListener("timeupdate", listener);
    }

    /**
     * Gets ontoggle.
     *
     * @return the ontoggle
     */
    Object getOntoggle();

    /**
     * Sets ontoggle.
     *
     * @param ontoggle the ontoggle
     */
    void setOntoggle(Object ontoggle);

    /**
     * Add toggle event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addToggleEventListener(Object listener, boolean options) {
        addEventListener("toggle", listener, options);
    }

    /**
     * Add toggle event listener.
     *
     * @param listener the listener
     */
    default void addToggleEventListener(Object listener) {
        addEventListener("toggle", listener);
    }

    /**
     * Remove toggle event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeToggleEventListener(Object listener, boolean options) {
        removeEventListener("toggle", listener, options);
    }

    /**
     * Remove toggle event listener.
     *
     * @param listener the listener
     */
    default void removeToggleEventListener(Object listener) {
        removeEventListener("toggle", listener);
    }

    /**
     * Gets ontouchcancel.
     *
     * @return the ontouchcancel
     */
    Object getOntouchcancel();

    /**
     * Sets ontouchcancel.
     *
     * @param ontouchcancel the ontouchcancel
     */
    void setOntouchcancel(Object ontouchcancel);

    /**
     * Add touch cancel event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addTouchCancelEventListener(Object listener, boolean options) {
        addEventListener("touchcancel", listener, options);
    }

    /**
     * Add touch cancel event listener.
     *
     * @param listener the listener
     */
    default void addTouchCancelEventListener(Object listener) {
        addEventListener("touchcancel", listener);
    }

    /**
     * Remove touch cancel event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeTouchCancelEventListener(Object listener, boolean options) {
        removeEventListener("touchcancel", listener, options);
    }

    /**
     * Remove touch cancel event listener.
     *
     * @param listener the listener
     */
    default void removeTouchCancelEventListener(Object listener) {
        removeEventListener("touchcancel", listener);
    }

    /**
     * Gets ontouchend.
     *
     * @return the ontouchend
     */
    Object getOntouchend();

    /**
     * Sets ontouchend.
     *
     * @param ontouchend the ontouchend
     */
    void setOntouchend(Object ontouchend);

    /**
     * Add touch end event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addTouchEndEventListener(Object listener, boolean options) {
        addEventListener("touchend", listener, options);
    }

    /**
     * Add touch end event listener.
     *
     * @param listener the listener
     */
    default void addTouchEndEventListener(Object listener) {
        addEventListener("touchend", listener);
    }

    /**
     * Remove touch end event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeTouchEndEventListener(Object listener, boolean options) {
        removeEventListener("touchend", listener, options);
    }

    /**
     * Remove touch end event listener.
     *
     * @param listener the listener
     */
    default void removeTouchEndEventListener(Object listener) {
        removeEventListener("touchend", listener);
    }

    /**
     * Gets ontouchmove.
     *
     * @return the ontouchmove
     */
    Object getOntouchmove();

    /**
     * Sets ontouchmove.
     *
     * @param ontouchmove the ontouchmove
     */
    void setOntouchmove(Object ontouchmove);

    /**
     * Add touch move event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addTouchMoveEventListener(Object listener, boolean options) {
        addEventListener("touchmove", listener, options);
    }

    /**
     * Add touch move event listener.
     *
     * @param listener the listener
     */
    default void addTouchMoveEventListener(Object listener) {
        addEventListener("touchmove", listener);
    }

    /**
     * Remove touch move event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeTouchMoveEventListener(Object listener, boolean options) {
        removeEventListener("touchmove", listener, options);
    }

    /**
     * Remove touch move event listener.
     *
     * @param listener the listener
     */
    default void removeTouchMoveEventListener(Object listener) {
        removeEventListener("touchmove", listener);
    }

    /**
     * Gets ontouchstart.
     *
     * @return the ontouchstart
     */
    Object getOntouchstart();

    /**
     * Sets ontouchstart.
     *
     * @param ontouchstart the ontouchstart
     */
    void setOntouchstart(Object ontouchstart);

    /**
     * Add touch start event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addTouchStartEventListener(Object listener, boolean options) {
        addEventListener("touchstart", listener, options);
    }

    /**
     * Add touch start event listener.
     *
     * @param listener the listener
     */
    default void addTouchStartEventListener(Object listener) {
        addEventListener("touchstart", listener);
    }

    /**
     * Remove touch start event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeTouchStartEventListener(Object listener, boolean options) {
        removeEventListener("touchstart", listener, options);
    }

    /**
     * Remove touch start event listener.
     *
     * @param listener the listener
     */
    default void removeTouchStartEventListener(Object listener) {
        removeEventListener("touchstart", listener);
    }

    /**
     * Gets ontransitioncancel.
     *
     * @return the ontransitioncancel
     */
    Object getOntransitioncancel();

    /**
     * Sets ontransitioncancel.
     *
     * @param ontransitioncancel the ontransitioncancel
     */
    void setOntransitioncancel(Object ontransitioncancel);

    /**
     * Add transition cancel event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addTransitionCancelEventListener(Object listener, boolean options) {
        addEventListener("transitioncancel", listener, options);
    }

    /**
     * Add transition cancel event listener.
     *
     * @param listener the listener
     */
    default void addTransitionCancelEventListener(Object listener) {
        addEventListener("transitioncancel", listener);
    }

    /**
     * Remove transition cancel event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeTransitionCancelEventListener(Object listener, boolean options) {
        removeEventListener("transitioncancel", listener, options);
    }

    /**
     * Remove transition cancel event listener.
     *
     * @param listener the listener
     */
    default void removeTransitionCancelEventListener(Object listener) {
        removeEventListener("transitioncancel", listener);
    }

    /**
     * Gets ontransitionend.
     *
     * @return the ontransitionend
     */
    Object getOntransitionend();

    /**
     * Sets ontransitionend.
     *
     * @param ontransitionend the ontransitionend
     */
    void setOntransitionend(Object ontransitionend);

    /**
     * Add transition end event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addTransitionEndEventListener(Object listener, boolean options) {
        addEventListener("transitionend", listener, options);
    }

    /**
     * Add transition end event listener.
     *
     * @param listener the listener
     */
    default void addTransitionEndEventListener(Object listener) {
        addEventListener("transitionend", listener);
    }

    /**
     * Remove transition end event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeTransitionEndEventListener(Object listener, boolean options) {
        removeEventListener("transitionend", listener, options);
    }

    /**
     * Remove transition end event listener.
     *
     * @param listener the listener
     */
    default void removeTransitionEndEventListener(Object listener) {
        removeEventListener("transitionend", listener);
    }

    /**
     * Gets ontransitionrun.
     *
     * @return the ontransitionrun
     */
    Object getOntransitionrun();

    /**
     * Sets ontransitionrun.
     *
     * @param ontransitionrun the ontransitionrun
     */
    void setOntransitionrun(Object ontransitionrun);

    /**
     * Add transition run event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addTransitionRunEventListener(Object listener, boolean options) {
        addEventListener("transitionrun", listener, options);
    }

    /**
     * Add transition run event listener.
     *
     * @param listener the listener
     */
    default void addTransitionRunEventListener(Object listener) {
        addEventListener("transitionrun", listener);
    }

    /**
     * Remove transition run event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeTransitionRunEventListener(Object listener, boolean options) {
        removeEventListener("transitionrun", listener, options);
    }

    /**
     * Remove transition run event listener.
     *
     * @param listener the listener
     */
    default void removeTransitionRunEventListener(Object listener) {
        removeEventListener("transitionrun", listener);
    }

    /**
     * Gets ontransitionstart.
     *
     * @return the ontransitionstart
     */
    Object getOntransitionstart();

    /**
     * Sets ontransitionstart.
     *
     * @param ontransitionstart the ontransitionstart
     */
    void setOntransitionstart(Object ontransitionstart);

    /**
     * Add transition start event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addTransitionStartEventListener(Object listener, boolean options) {
        addEventListener("transitionstart", listener, options);
    }

    /**
     * Add transition start event listener.
     *
     * @param listener the listener
     */
    default void addTransitionStartEventListener(Object listener) {
        addEventListener("transitionstart", listener);
    }

    /**
     * Remove transition start event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeTransitionStartEventListener(Object listener, boolean options) {
        removeEventListener("transitionstart", listener, options);
    }

    /**
     * Remove transition start event listener.
     *
     * @param listener the listener
     */
    default void removeTransitionStartEventListener(Object listener) {
        removeEventListener("transitionstart", listener);
    }

    /**
     * Occurs when the volume is changed, or playback is muted or unmuted.
     *
     * @return the onvolumechange
     */
    Object getOnvolumechange();

    /**
     * Sets onvolumechange.
     *
     * @param onvolumechange the onvolumechange
     */
    void setOnvolumechange(Object onvolumechange);

    /**
     * Add volume change event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addVolumeChangeEventListener(Object listener, boolean options) {
        addEventListener("volumechange", listener, options);
    }

    /**
     * Add volume change event listener.
     *
     * @param listener the listener
     */
    default void addVolumeChangeEventListener(Object listener) {
        addEventListener("volumechange", listener);
    }

    /**
     * Remove volume change event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeVolumeChangeEventListener(Object listener, boolean options) {
        removeEventListener("volumechange", listener, options);
    }

    /**
     * Remove volume change event listener.
     *
     * @param listener the listener
     */
    default void removeVolumeChangeEventListener(Object listener) {
        removeEventListener("volumechange", listener);
    }

    /**
     * Occurs when playback stops because the next frame of a video resource is not
     * available.
     *
     * @return the onwaiting
     */
    Object getOnwaiting();

    /**
     * Sets onwaiting.
     *
     * @param onwaiting the onwaiting
     */
    void setOnwaiting(Object onwaiting);

    /**
     * Add waiting event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addWaitingEventListener(Object listener, boolean options) {
        addEventListener("waiting", listener, options);
    }

    /**
     * Add waiting event listener.
     *
     * @param listener the listener
     */
    default void addWaitingEventListener(Object listener) {
        addEventListener("waiting", listener);
    }

    /**
     * Remove waiting event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeWaitingEventListener(Object listener, boolean options) {
        removeEventListener("waiting", listener, options);
    }

    /**
     * Remove waiting event listener.
     *
     * @param listener the listener
     */
    default void removeWaitingEventListener(Object listener) {
        removeEventListener("waiting", listener);
    }

    /**
     * Gets onwheel.
     *
     * @return the onwheel
     */
    Object getOnwheel();

    /**
     * Sets onwheel.
     *
     * @param onwheel the onwheel
     */
    void setOnwheel(Object onwheel);

    /**
     * Add wheel event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void addWheelEventListener(Object listener, boolean options) {
        addEventListener("wheel", listener, options);
    }

    /**
     * Add wheel event listener.
     *
     * @param listener the listener
     */
    default void addWheelEventListener(Object listener) {
        addEventListener("wheel", listener);
    }

    /**
     * Remove wheel event listener.
     *
     * @param listener the listener
     * @param options  the options
     */
    default void removeWheelEventListener(Object listener, boolean options) {
        removeEventListener("wheel", listener, options);
    }

    /**
     * Remove wheel event listener.
     *
     * @param listener the listener
     */
    default void removeWheelEventListener(Object listener) {
        removeEventListener("wheel", listener);
    }
}
