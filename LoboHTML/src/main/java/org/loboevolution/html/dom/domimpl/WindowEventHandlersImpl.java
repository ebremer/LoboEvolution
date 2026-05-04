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
package org.loboevolution.html.dom.domimpl;

import org.loboevolution.html.dom.nodeimpl.event.EventTargetImpl;
import org.loboevolution.events.GlobalEventHandlers;
import org.loboevolution.js.WindowEventHandlers;

/**
 * <p>WindowEventHandlersImpl class.</p>
 */
public class WindowEventHandlersImpl extends EventTargetImpl implements WindowEventHandlers, GlobalEventHandlers {


    @Override
    public Object getOnafterprint() {
       return getCallable(this, "afterprint");
    }

    @Override
    public Object getOnbeforeprint() {
       return getCallable(this, "change");
    }

    @Override
    public Object getOnlanguagechange() {
       return getCallable(this, "change");
    }

    @Override
    public Object getOnoffline() {
       return getCallable(this, "change");
    }

    @Override
    public Object getOnonline() {
       return getCallable(this, "change");
    }

    @Override
    public Object getOnunload() {
       return getCallable(this, "change");
    }

    @Override
    public Object getOnfullscreenchange() {
        return getCallable(this, "fullscreenchange");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field onblur.</p>
     */
    @Override
    public Object getOnblur() {
        return getCallable(this, "blur");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field onclick.</p>
     */
    @Override
    public Object getOnclick() {
        return getCallable(this, "click");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field oncontextmenu.</p>
     */
    @Override
    public Object getOncontextmenu() {
        return getCallable(this, "contextmenu");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field ondblclick.</p>
     */
    @Override
    public Object getOndblclick() {
        return getCallable(this, "dblclick");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field onfocus.</p>
     */
    @Override
    public Object getOnfocus() {
        return getCallable(this, "focus");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field onkeydown.</p>
     */
    @Override
    public Object getOnkeydown() {
        return getCallable(this, "keydown");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field onkeypress.</p>
     */
    @Override
    public Object getOnkeypress() {
        return getCallable(this, "keypress");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field onkeyup.</p>
     */
    @Override
    public Object getOnkeyup() {
        return getCallable(this, "keyup");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field onmousedown.</p>
     */
    @Override
    public Object getOnmousedown() {
        return getCallable(this, "mousedown");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field onmousemove.</p>
     */
    @Override
    public Object getOnmousemove() {
        return getCallable(this, "mousemove");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field onmouseout.</p>
     */
    @Override
    public Object getOnmouseout() {
        return getCallable(this, "mouseout");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field onmouseover.</p>
     */
    @Override
    public Object getOnmouseover() {
        return getCallable(this, "mouseover");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field onmouseup.</p>
     */
    @Override
    public Object getOnmouseup() {
        return getCallable(this, "mouseup");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Getter for the field onchange.</p>
     */
    @Override
    public Object getOnchange() {
        return getCallable(this, "change");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnabort() {
        return getCallable(this, "abort");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnanimationcancel() {
        return getCallable(this, "animationcancel");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnanimationend() {
        return getCallable(this, "animationend");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnanimationiteration() {
        return getCallable(this, "animationiteration");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnanimationstart() {
        return getCallable(this, "animationstart");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnauxclick() {
        return getCallable(this, "auxclick");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOncancel() {
        return getCallable(this, "cancel");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOncanplay() {

        return getCallable(this, "canplay");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOncanplaythrough() {
        return getCallable(this, "canplaythrough");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnclose() {
        return getCallable(this, "close");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOncuechange() {
        return getCallable(this, "cuechange");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOndrag() {
        return getCallable(this, "drag");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOndragend() {
        return getCallable(this, "dragend");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOndragenter() {
        return getCallable(this, "dragenter");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOndragexit() {
        return getCallable(this, "dragexit");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOndragleave() {
        return getCallable(this, "dragleave");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOndragover() {
        return getCallable(this, "dragover");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOndragstart() {
        return getCallable(this, "dragstart");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOndrop() {
        return getCallable(this, "drop");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOndurationchange() {
        return getCallable(this, "durationchange");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnemptied() {
        return getCallable(this, "emptied");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnended() {
        return getCallable(this, "ended");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnerror() {
        return getCallable(this, "error");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnfocusin() {
        return getCallable(this, "focusin");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnfocusout() {
        return getCallable(this, "focusout");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOngotpointercapture() {
        return getCallable(this, "gotpointercapture");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOninput() {
        return getCallable(this, "input");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOninvalid() {
        return getCallable(this, "invalid");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnload() {
        return getCallable(this, "load");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnloadeddata() {
        return getCallable(this, "loadeddata");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnloadedmetadata() {
        return getCallable(this, "loadedmetadata");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnloadend() {
        return getCallable(this, "loadend");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnloadstart() {
        return getCallable(this, "loadstart");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnlostpointercapture() {
        return getCallable(this, "lostpointercapture");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnmouseenter() {
        return getCallable(this, "mouseenter");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnmouseleave() {
        return getCallable(this, "mouseleave");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnpause() {
        return getCallable(this, "pause");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnplay() {
        return getCallable(this, "play");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnplaying() {
        return getCallable(this, "playing");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnpointercancel() {
        return getCallable(this, "pointercancel");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnpointerdown() {
        return getCallable(this, "pointerdown");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnpointerenter() {
        return getCallable(this, "pointerenter");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnpointerleave() {
        return getCallable(this, "pointerleave");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnpointermove() {
        return getCallable(this, "pointermove");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnpointerout() {
        return getCallable(this, "pointerout");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnpointerover() {
        return getCallable(this, "pointerover");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnpointerup() {
        return getCallable(this, "pointerup");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnprogress() {
        return getCallable(this, "progress");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnratechange() {
        return getCallable(this, "ratechange");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnreset() {
        return getCallable(this, "reset");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnresize() {
        return getCallable(this, "resize");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnscroll() {
        return getCallable(this, "scroll");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnsecuritypolicyviolation() {
        return getCallable(this, "securitypolicyviolation");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnseeked() {
        return getCallable(this, "seeked");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnseeking() {
        return getCallable(this, "seeking");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnselect() {
        return getCallable(this, "select");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnselectionchange() {
        return getCallable(this, "selectionchange");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnselectstart() {
        return getCallable(this, "selectstart");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnstalled() {
        return getCallable(this, "stalled");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnsubmit() {
        return getCallable(this, "submit");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnsuspend() {
        return getCallable(this, "suspend");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOntimeupdate() {
        return getCallable(this, "timeupdate");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOntoggle() {
        return getCallable(this, "toggle");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOntouchcancel() {
        return getCallable(this, "touchcancel");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOntouchend() {
        return getCallable(this, "touchend");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOntouchmove() {
        return getCallable(this, "touchmove");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOntouchstart() {
        return getCallable(this, "touchstart");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOntransitioncancel() {
        return getCallable(this, "transitioncancel");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOntransitionend() {
        return getCallable(this, "transitionend");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOntransitionrun() {
        return getCallable(this, "transitionrun");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOntransitionstart() {
        return getCallable(this, "transitionstart");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnvolumechange() {
        return getCallable(this, "volumechange");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnwaiting() {
        return getCallable(this, "waiting");
    }

    /** {@inheritDoc} */
    @Override
    public Object getOnwheel() {
        return getCallable(this, "wheel");
    }

    /** {@inheritDoc} */
    @Override
    public void setOnfocus(final Object onfocus) {
        addEventListener("focus", onfocus);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnblur(final Object onblur) {
        addEventListener("blur", onblur);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnclick(final Object onclick) {
        addEventListener("click", onclick);
    }

    /** {@inheritDoc} */
    @Override
    public void setOndblclick(final Object ondblclick) {
        addEventListener("dblclick", ondblclick);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnmousedown(final Object onmousedown) {
        addEventListener("mousedown", onmousedown);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnmouseup(final Object onmouseup) {
        addEventListener("mouseup", onmouseup);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnmouseover(final Object onmouseover) {
        addEventListener("mouseover", onmouseover);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnmousemove(final Object onmousemove) {
        addEventListener("mousemove", onmousemove);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnmouseout(final Object onmouseout) {
        addEventListener("mouseout", onmouseout);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnkeypress(final Object onkeypress) {
        addEventListener("keypress", onkeypress);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnkeydown(final Object onkeydown) {
        addEventListener("keydown", onkeydown);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnkeyup(final Object onkeyup) {
        addEventListener("keyup", onkeyup);
    }

    /** {@inheritDoc} */
    @Override
    public void setOncontextmenu(final Object oncontextmenu) {
        addEventListener("contextmenu", oncontextmenu);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnchange(final Object onchange) {
        addEventListener("change", onchange);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnabort(final Object onabort) {
        addEventListener("abort", onabort);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnwaiting(final Object onwaiting) {
        addEventListener("waiting", onwaiting);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnvolumechange(final Object onvolumechange) {
        addEventListener("volumechange", onvolumechange);
    }

    /** {@inheritDoc} */
    @Override
    public void setOntransitionstart(final Object ontransitionstart) {
        addEventListener("transitionstart", ontransitionstart);
    }

    /** {@inheritDoc} */
    @Override
    public void setOntransitionrun(final Object ontransitionrun) {
        addEventListener("transitionrun", ontransitionrun);
    }

    /** {@inheritDoc} */
    @Override
    public void setOntransitionend(final Object ontransitionend) {
        addEventListener("transitionend", ontransitionend);
    }

    /** {@inheritDoc} */
    @Override
    public void setOntransitioncancel(final Object ontransitioncancel) {
        addEventListener("transitioncancel", ontransitioncancel);
    }

    /** {@inheritDoc} */
    @Override
    public void setOntouchstart(final Object ontouchstart) {
        addEventListener("touchstart", ontouchstart);
    }

    /** {@inheritDoc} */
    @Override
    public void setOntoggle(final Object ontoggle) {
        addEventListener("toggle", ontoggle);
    }

    /** {@inheritDoc} */
    @Override
    public void setOntouchmove(final Object ontouchmove) {
        addEventListener("touchmove", ontouchmove);
    }

    /** {@inheritDoc} */
    @Override
    public void setOntouchend(final Object ontouchend) {
        addEventListener("touchend", ontouchend);
    }

    /** {@inheritDoc} */
    @Override
    public void setOntouchcancel(final Object ontouchcancel) {
        addEventListener("touchcancel", ontouchcancel);
    }

    /** {@inheritDoc} */
    @Override
    public void setOntimeupdate(final Object ontimeupdate) {
        addEventListener("timeupdate", ontimeupdate);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnsubmit(final Object onsubmit) {
        addEventListener("submit", onsubmit);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnstalled(final Object onstalled) {
        addEventListener("stalled", onstalled);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnselectstart(final Object onselectstart) {
        addEventListener("selectstart", onselectstart);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnselectionchange(final Object onselectionchange) {
        addEventListener("selectionchange", onselectionchange);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnselect(final Object onselect) {
        addEventListener("select", onselect);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnseeking(final Object onseeking) {
        addEventListener("seeking", onseeking);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnseeked(final Object onseeked) {
        addEventListener("seeked", onseeked);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnsecuritypolicyviolation(final Object onsecuritypolicyviolation) {
        addEventListener("securitypolicyviolation", onsecuritypolicyviolation);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnscroll(final Object onscroll) {
        addEventListener("scroll", onscroll);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnresize(final Object onresize) {
        addEventListener("resize", onresize);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnreset(final Object onreset) {
        addEventListener("reset", onreset);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnratechange(final Object onratechange) {
        addEventListener("ratechange", onratechange);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnprogress(final Object onprogress) {
        addEventListener("progress", onprogress);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnpointerup(final Object onpointerup) {
        addEventListener("pointerup", onpointerup);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnpointerover(final Object onpointerover) {
        addEventListener("pointerover", onpointerover);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnpointerout(final Object onpointerout) {
        addEventListener("pointerout", onpointerout);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnwheel(final Object onwheel) {
        addEventListener("wheel", onwheel);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnsuspend(final Object onsuspend) {
        addEventListener("suspend", onsuspend);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnpointermove(final Object onpointermove) {
        addEventListener("pointermove", onpointermove);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnpointerleave(final Object onpointerleave) {
        addEventListener("pointerleave", onpointerleave);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnpointerenter(final Object onpointerenter) {
        addEventListener("pointerenter", onpointerenter);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnpointerdown(final Object onpointerdown) {
        addEventListener("pointerdown", onpointerdown);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnpointercancel(final Object onpointercancel) {
        addEventListener("pointercancel", onpointercancel);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnplaying(final Object onplaying) {
        addEventListener("playing", onplaying);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnplay(final Object onplay) {
        addEventListener("play", onplay);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnpause(final Object onpause) {
        addEventListener("pause", onpause);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnmouseleave(final Object onmouseleave) {
        addEventListener("mouseleave", onmouseleave);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnmouseenter(final Object onmouseenter) {
        addEventListener("mouseenter", onmouseenter);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnauxclick(final Object onauxclick) {
        addEventListener("auxclick", onauxclick);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnlostpointercapture(final Object onlostpointercapture) {
        addEventListener("lostpointercapture", onlostpointercapture);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnloadstart(final Object onloadstart) {
        addEventListener("loadstart", onloadstart);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnloadend(final Object onloadend) {
        addEventListener("loadend", onloadend);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnloadedmetadata(final Object onloadedmetadata) {
        addEventListener("loadedmetadata", onloadedmetadata);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnloadeddata(final Object onloadeddata) {
        addEventListener("loadeddata", onloadeddata);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnload(final Object onload) {
        addEventListener("load", onload);
    }

    /** {@inheritDoc} */
    @Override
    public void setOninvalid(final Object oninvalid) {
        addEventListener("invalid", oninvalid);
    }



    /** {@inheritDoc} */
    @Override
    public void setOninput(final Object oninput) {
        addEventListener("input", oninput);
    }

    /** {@inheritDoc} */
    @Override
    public void setOngotpointercapture(final Object ongotpointercapture) {
        addEventListener("gotpointercapture", ongotpointercapture);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnfocusout(final Object onfocusout) {
        addEventListener("focusout", onfocusout);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnfocusin(final Object onfocusin) {
        addEventListener("focusin", onfocusin);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnerror(final Object onerror) {
        addEventListener("error", onerror);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnended(final Object onended) {
        addEventListener("ended", onended);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnemptied(final Object onemptied) {
        addEventListener("emptied", onemptied);
    }

    /** {@inheritDoc} */
    @Override
    public void setOndurationchange(final Object ondurationchange) {
        addEventListener("durationchange", ondurationchange);
    }

    /** {@inheritDoc} */
    @Override
    public void setOndrop(final Object ondrop) {
        addEventListener("drop", ondrop);
    }

    /** {@inheritDoc} */
    @Override
    public void setOndragstart(final Object ondragstart) {
        addEventListener("dragstart", ondragstart);
    }

    /** {@inheritDoc} */
    @Override
    public void setOndragover(final Object ondragover) {
        addEventListener("dragover", ondragover);
    }

    /** {@inheritDoc} */
    @Override
    public void setOndragleave(final Object ondragleave) {
        addEventListener("dragleave", ondragleave);
    }

    /** {@inheritDoc} */
    @Override
    public void setOndragexit(final Object ondragexit) {
        addEventListener("dragexit", ondragexit);
    }

    /** {@inheritDoc} */
    @Override
    public void setOndragenter(final Object ondragenter) {
        addEventListener("dragenter", ondragenter);
    }

    /** {@inheritDoc} */
    @Override
    public void setOndragend(final Object ondragend) {
        addEventListener("dragend", ondragend);
    }

    /** {@inheritDoc} */
    @Override
    public void setOndrag(final Object ondrag) {
        addEventListener("drag", ondrag);
    }

    /** {@inheritDoc} */
    @Override
    public void setOncuechange(final Object oncuechange) {
        addEventListener("cuechange", oncuechange);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnclose(final Object onclose) {
        addEventListener("close", onclose);
    }

    /** {@inheritDoc} */
    @Override
    public void setOncanplaythrough(final Object oncanplaythrough) {
        addEventListener("canplaythrough", oncanplaythrough);
    }

    /** {@inheritDoc} */
    @Override
    public void setOncanplay(final Object oncanplay) {
        addEventListener("canplay", oncanplay);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnanimationstart(final Object onanimationstart) {
        addEventListener("animationstart", onanimationstart);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnanimationiteration(final Object onanimationiteration) {
        addEventListener("animationiteration", onanimationiteration);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnanimationend(final Object onanimationend) {
        addEventListener("animationend", onanimationend);
    }

    /** {@inheritDoc} */
    @Override
    public void setOnanimationcancel(final Object onanimationcancel) {
        addEventListener("animationcancel", onanimationcancel);
    }

    /** {@inheritDoc} */
    @Override
    public void setOncancel(final Object oncancel) {
        addEventListener("cancel", oncancel);
    }

    @Override
    public void setOnunload(final Object onunload) {
        addEventListener("unload", onunload, false);
    }

    @Override
    public void setOnonline(final Object ononline) {
        addEventListener("online", ononline, false);
    }

    @Override
    public void setOnoffline(final Object onoffline) {
        addEventListener("offline", onoffline, false);
    }

    @Override
    public void setOnlanguagechange(final Object onlanguagechange) {
        addEventListener("languagechange", onlanguagechange, false);
    }

    @Override
    public void setOnbeforeprint(final Object onbeforeprint) {
        addEventListener("beforeprint", onbeforeprint, false);
    }

    @Override
    public void setOnafterprint(final Object onafterprint) {
        addEventListener("afterprint", onafterprint, false);
    }
}
