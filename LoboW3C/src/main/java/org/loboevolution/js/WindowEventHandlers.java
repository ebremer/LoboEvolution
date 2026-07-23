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

package org.loboevolution.js;

import org.loboevolution.events.EventTarget;

/**
 * <p>WindowEventHandlers interface.</p>
 */
public interface WindowEventHandlers extends EventTarget {

	/**
	 * <p>getOnafterprint.</p>
	 *
	 * @return a {@link Object} object.
	 */
	Object getOnafterprint();

	/**
	 * <p>getOnbeforeprint.</p>
	 *
	 * @return a {@link Object} object.
	 */
	Object getOnbeforeprint();

	/**
	 * <p>getOnlanguagechange.</p>
	 *
	 * @return a {@link Object} object.
	 */
	Object getOnlanguagechange();

	/**
	 * <p>getOnoffline.</p>
	 *
	 * @return a {@link Object} object.
	 */
	Object getOnoffline();

	/**
	 * <p>getOnonline.</p>
	 *
	 * @return a {@link Object} object.
	 */
	Object getOnonline();

	/**
	 * <p>getOnunload.</p>
	 *
	 * @return a {@link Object} object.
	 */
	Object getOnunload();

	/**
	 * <p>setOnunload.</p>
	 *
	 * @param onunload a {@link Object} object.
	 */
	void setOnunload(Object onunload);

	/**
	 * <p>setOnonline.</p>
	 *
	 * @param ononline a {@link Object} object.
	 */
	void setOnonline(Object ononline);

	/**
	 * <p>setOnoffline.</p>
	 *
	 * @param onoffline a {@link Object} object.
	 */
	void setOnoffline(Object onoffline);

	/**
	 * <p>setOnlanguagechange.</p>
	 *
	 * @param onlanguagechange a {@link Object} object.
	 */
	void setOnlanguagechange(Object onlanguagechange);

	/**
	 * <p>setOnbeforeprint.</p>
	 *
	 * @param onbeforeprint a {@link Object} object.
	 */
	void setOnbeforeprint(Object onbeforeprint);

	/**
	 * <p>setOnafterprint.</p>
	 *
	 * @param onafterprint a {@link Object} object.
	 */
	void setOnafterprint(Object onafterprint);

	/**
	 * <p>addAfterPrintEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void addAfterPrintEventListener(Object listener, boolean options) {
		addEventListener("afterprint", listener, options);
	}

	/**
	 * <p>addAfterPrintEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void addAfterPrintEventListener(Object listener) {
		addEventListener("afterprint", listener);
	}

	/**
	 * <p>removeAfterPrintEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void removeAfterPrintEventListener(Object listener, boolean options) {
		removeEventListener("afterprint", listener, options);
	}

	/**
	 * <p>removeAfterPrintEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void removeAfterPrintEventListener(Object listener) {
		removeEventListener("afterprint", listener);
	}

	/**
	 * <p>addBeforePrintEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void addBeforePrintEventListener(Object listener, boolean options) {
		addEventListener("beforeprint", listener, options);
	}

	/**
	 * <p>addBeforePrintEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void addBeforePrintEventListener(Object listener) {
		addEventListener("beforeprint", listener);
	}

	/**
	 * <p>removeBeforePrintEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void removeBeforePrintEventListener(Object listener, boolean options) {
		removeEventListener("beforeprint", listener, options);
	}

	/**
	 * <p>removeBeforePrintEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void removeBeforePrintEventListener(Object listener) {
		removeEventListener("beforeprint", listener);
	}

	/**
	 * <p>addLanguageChangeEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void addLanguageChangeEventListener(Object listener, boolean options) {
		addEventListener("languagechange", listener, options);
	}

	/**
	 * <p>addLanguageChangeEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void addLanguageChangeEventListener(Object listener) {
		addEventListener("languagechange", listener);
	}

	/**
	 * <p>removeLanguageChangeEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void removeLanguageChangeEventListener(Object listener, boolean options) {
		removeEventListener("languagechange", listener, options);
	}

	/**
	 * <p>removeLanguageChangeEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void removeLanguageChangeEventListener(Object listener) {
		removeEventListener("languagechange", listener);
	}

	/**
	 * <p>addOfflineEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void addOfflineEventListener(Object listener, boolean options) {
		addEventListener("offline", listener, options);
	}

	/**
	 * <p>addOfflineEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void addOfflineEventListener(Object listener) {
		addEventListener("offline", listener);
	}

	/**
	 * <p>removeOfflineEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void removeOfflineEventListener(Object listener, boolean options) {
		removeEventListener("offline", listener, options);
	}

	/**
	 * <p>removeOfflineEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void removeOfflineEventListener(Object listener) {
		removeEventListener("offline", listener);
	}

	/**
	 * <p>addOnlineEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void addOnlineEventListener(Object listener, boolean options) {
		addEventListener("online", listener, options);
	}

	/**
	 * <p>addOnlineEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void addOnlineEventListener(Object listener) {
		addEventListener("online", listener);
	}

	/**
	 * <p>removeOnlineEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void removeOnlineEventListener(Object listener, boolean options) {
		removeEventListener("online", listener, options);
	}

	/**
	 * <p>removeOnlineEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void removeOnlineEventListener(Object listener) {
		removeEventListener("online", listener);
	}

	/**
	 * <p>addRejectionHandledEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void addRejectionHandledEventListener(Object listener, boolean options) {
		addEventListener("rejectionhandled", listener, options);
	}

	/**
	 * <p>addRejectionHandledEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void addRejectionHandledEventListener(Object listener) {
		addEventListener("rejectionhandled", listener);
	}

	/**
	 * <p>removeRejectionHandledEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void removeRejectionHandledEventListener(Object listener, boolean options) {
		removeEventListener("rejectionhandled", listener, options);
	}

	/**
	 * <p>removeRejectionHandledEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void removeRejectionHandledEventListener(Object listener) {
		removeEventListener("rejectionhandled", listener);
	}

	/**
	 * <p>addUnloadEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void addUnloadEventListener(Object listener, boolean options) {
		addEventListener("unload", listener, options);
	}

	/**
	 * <p>addUnloadEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void addUnloadEventListener(Object listener) {
		addEventListener("unload", listener);
	}

	/**
	 * <p>removeUnloadEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 * @param options a boolean.
	 */
	default void removeUnloadEventListener(Object listener, boolean options) {
		removeEventListener("unload", listener, options);
	}

	/**
	 * <p>removeUnloadEventListener.</p>
	 *
	 * @param listener a {@link Object} object.
	 */
	default void removeUnloadEventListener(Object listener) {
		removeEventListener("unload", listener);
	}
}
