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

package org.loboevolution.html.js.geolocation;

import lombok.extern.slf4j.Slf4j;
import org.loboevolution.html.dom.domimpl.HTMLDocumentImpl;
import org.loboevolution.html.js.WindowImpl;
import org.loboevolution.html.js.engine.JsEngine;
import org.loboevolution.html.js.engine.JsEngineFactory;
import org.loboevolution.js.AbstractScriptableDelegate;
import org.loboevolution.js.Window;

import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

/**
 * <p>
 * The Geolocation class provides Java implementation of the
 * "Geolocation Interface" as detailed out in the W3C Specifications (
 * <a href="http://www.w3.org/TR/geolocation-API/#geolocation_interface">
 * http://www.w3.org/TR/geolocation-API/#geolocation_interface</a>).
 * </p>
 *
 * <p>
 * <b>Note: This class must not have any sub-classes to ensure W3C
 * Specifications are being strictly followed by the system or application that
 * uses this geolocation package.</b>
 * </p>
 */
@Slf4j
public class Geolocation extends AbstractScriptableDelegate {

	private final WindowImpl window;

	/**
	 * <p>Constructor for Geolocation.</p>
	 *
	 * @param window a {@link Window} object.
	 */
	public Geolocation(final WindowImpl window) {
		this.window = window;
	}

	public void getCurrentPosition(final Object success) throws Exception {
		final IPAddressBasedGeoAcquirer ip = new IPAddressBasedGeoAcquirer();
		final Position acquireLocation = ip.acquireLocation();
		invokeCallback(success, acquireLocation);
	}

	public void getCurrentPosition(final Object success, final Object error) {
		try {
			getCurrentPosition(success);
		} catch (final Exception e) {
			geoError(error, e);
		}
	}

	public long watchPosition(final Object success) {
		final long watchId = System.currentTimeMillis();
		final Thread t = new Thread(() -> {
			while (true) {
				try {
					getCurrentPosition(success);
					Thread.sleep(500);
				} catch (final Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		});
		t.start();
		return watchId;
	}

	public long watchPosition(final Object success, final Object error) {
		final long watchId = System.currentTimeMillis();
		final Thread t = new Thread(() -> {
			while (true) {
				try {
					getCurrentPosition(success, error);
					Thread.sleep(500);
				} catch (final Exception e) {
					geoError(error, e);
					break;
				}
			}
		});
		t.start();
		return watchId;
	}

	private void geoError(final Object error, final Exception e) {
		PositionError pError = null;
		if (e instanceof UnknownHostException) {
			pError = new PositionError(PositionError.POSITION_UNAVAILABLE);
		} else if (e instanceof TimeoutException) {
			pError = new PositionError(PositionError.TIMEOUT);
		}
		invokeCallback(error, pError);
	}

	private void invokeCallback(final Object callback, final Object arg) {
		if (callback == null) return;
		final HTMLDocumentImpl doc = (HTMLDocumentImpl) window.getDocument();
		if (doc == null) return;
		final JsEngine engine = JsEngineFactory.forDocument(doc, window);
		engine.call(callback, arg);
	}
}
