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
/*
 * Created on Oct 8, 2005
 */
package org.loboevolution.html.dom.domimpl;

import lombok.extern.slf4j.Slf4j;
import org.loboevolution.common.Strings;
import org.loboevolution.common.Urls;
import org.loboevolution.gui.HtmlRendererContext;
import org.loboevolution.html.dom.HTMLScriptElement;
import org.loboevolution.gui.HtmlPanel;
import org.loboevolution.html.js.WindowImpl;
import org.loboevolution.html.js.engine.JsEngine;
import org.loboevolution.html.js.engine.JsEngineFactory;
import org.loboevolution.html.node.Document;
import org.loboevolution.html.parser.XHtmlParser;
import org.loboevolution.html.renderstate.DisplayRenderState;
import org.loboevolution.html.renderstate.RenderState;
import org.loboevolution.http.UserAgentContext;
import org.loboevolution.info.TimingInfo;
import org.loboevolution.net.AlgorithmDigest;
import org.loboevolution.net.HttpNetwork;
import org.loboevolution.net.IOUtil;
import org.loboevolution.net.UserAgent;
import org.loboevolution.html.dom.UserDataHandler;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

/**
 * <p>HTMLScriptElementImpl class.</p>
 */
@Slf4j
public class HTMLScriptElementImpl extends HTMLElementImpl implements HTMLScriptElement {
	private boolean defer;

	private String text;

	/** HTML5 "already started" flag. Set on scripts that arrive via innerHTML /
	 *  outerHTML / insertAdjacentHTML (parser-inserted=false in spec terms);
	 *  prevents execution when the parser later signals end-of-element. */
	@lombok.Setter
	private boolean alreadyStarted;

	/**
	 * <p>Constructor for HTMLScriptElementImpl.</p>
	 */
	public HTMLScriptElementImpl() {
		super("SCRIPT");
	}

	/**
	 * <p>Constructor for HTMLScriptElementImpl.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public HTMLScriptElementImpl(final String name) {
		super(name);
	}

	/** {@inheritDoc} */
	@Override
	protected void appendInnerTextImpl(final StringBuilder buffer) {
		// nop
	}

	@Override
	public Boolean isAsync() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAsync(Object async) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getCrossOrigin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCrossOrigin(final String crossOrigin) {
		// TODO Auto-generated method stub
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDefer() {
		return this.defer;
	}

	/** {@inheritDoc} */
	@Override
	public String getEvent() {
		return getAttribute("event");
	}

	/** {@inheritDoc} */
	@Override
	public String getHtmlFor() {
		return getAttribute("htmlFor");
	}

	/** {@inheritDoc} */
	@Override
	public String getSrc() {
		return getAttribute("src");
	}

	/** {@inheritDoc} */
	@Override
	public String getText() {
		final String t = this.text;
		if (t == null) {
			return getRawInnerText(true);
		} else {
			return t;
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getType() {
		return getAttribute("type");
	}

	/**
	 * Returns true if the given <code>type</code> attribute identifies the script
	 * as JavaScript (or no type attribute is present, which defaults to JavaScript).
	 * Per HTML spec, scripts with other types are "data blocks" and must not be
	 * executed.
	 */
	private static boolean isJavaScriptType(final String type) {
		if (Strings.isBlank(type)) {
			return true;
		}
		final String t = type.trim().toLowerCase();
		// Accept "module" plus the standard JavaScript MIME types and historical
		// aliases. Anything else (application/json, application/ld+json,
		// text/template, etc.) is data.
		if ("module".equals(t)) {
			return true;
		}
		final int semi = t.indexOf(';');
		final String mime = semi < 0 ? t : t.substring(0, semi).trim();
		switch (mime) {
			case "text/javascript":
			case "application/javascript":
			case "application/ecmascript":
			case "application/x-ecmascript":
			case "application/x-javascript":
			case "text/ecmascript":
			case "text/javascript1.0":
			case "text/javascript1.1":
			case "text/javascript1.2":
			case "text/javascript1.3":
			case "text/javascript1.4":
			case "text/javascript1.5":
			case "text/jscript":
			case "text/livescript":
			case "text/x-ecmascript":
			case "text/x-javascript":
				return true;
			default:
				return false;
		}
	}

	/**
	 * <p>processScript.</p>
	 */
	private void processScript() {
		if (!isJavaScriptType(getType())) {
			// Data blocks (application/json, application/ld+json, text/template,
			// etc.) must not be executed as scripts per HTML spec.
			return;
		}
		final UserAgentContext bcontext = getUserAgentContext();
		if (bcontext == null) {
			throw new IllegalStateException("No user agent context.");
		}

		final Document doc = this.document;

		if (bcontext.isScriptingEnabled()) {
			final WindowImpl window = (WindowImpl) doc.getDefaultView();
			final JsEngine engine = JsEngineFactory.forDocument(doc, window);
			final String src = getSrc();
			final Instant start = Instant.now();

			if (Strings.isNotBlank(src)) {
				final TimingInfo info = new TimingInfo();
				final URL scriptURL;
				final String scriptURI;
				try {
					scriptURL = ((HTMLDocumentImpl) doc).getFullURL(src);
					scriptURI = scriptURL == null ? src : scriptURL.toExternalForm();
					info.setName(scriptURL != null ? scriptURL.getFile() : new URI(scriptURI).toURL().getFile());
				} catch (final Exception e) {
					log.error("Failed to resolve script URL {}", src, e);
					return;
				}

				try (InputStream in = getStream(scriptURL, scriptURI, info)) {
					// Read the script body exactly once: readFully now consumes
					// the stream, so the SRI check and the decode must share one
					// buffer (reading the stream again would yield nothing and
					// the script would silently not run).
					final byte[] data = IOUtil.readFully(in);
					if (AlgorithmDigest.validate(data, getIntegrity())) {
						final String body = stripCdataMarkers(new String(data, StandardCharsets.UTF_8));
						evalAndLog(engine, body, scriptURI);
					}
				} catch (final SocketTimeoutException e) {
					info.setHttpResponse(400);
				} catch (final Exception e) {
					log.error("Failed to load script {}", scriptURI, e);
				} finally {
					final Instant finish = Instant.now();
					info.setTimeElapsed(Duration.between(start, finish).toMillis());
					info.setPath(scriptURI);
					info.setHttpResponse(200);

					final HtmlRendererContext htmlRendererContext = this.getHtmlRendererContext();
					final HtmlPanel htmlPanel = htmlRendererContext.getHtmlPanel();
					htmlPanel.getBrowserPanel().getTimingList.add(info);
				}
			} else {
				final String scriptURI = doc.getBaseURI();
				text = getText();
				evalAndLog(engine, stripCdataMarkers(text), scriptURI);
			}
		}
	}

	private static void evalAndLog(final JsEngine engine, final String source, final String sourceName) {
		try {
			engine.eval(source, sourceName);
		} catch (final Throwable err) {
			log.warn("Javascript error in {}: {}", sourceName, err.getMessage());
		}
	}

	/**
	 * Removes XML {@code <![CDATA[} and {@code ]]>} markers from a script body.
	 * Pages that were authored as XHTML often wrap script content in a CDATA
	 * section so the XML parser leaves it alone; when those pages are served
	 * (or processed) as HTML the markers reach the JS parser verbatim, which
	 * is a syntax error per ES spec. Real browsers' XHTML-as-HTML compat
	 * layers strip these markers, and Rhino tolerates them by quirk; GraalJS
	 * is strict and rejects them. Stripping here unifies the two engines'
	 * behaviour without changing any valid JS program (those tokens are never
	 * legal JS syntax).
	 *
	 * <p>Markers are replaced with spaces of the same length so error line
	 * and column numbers continue to refer to positions in the original
	 * source the author wrote.
	 */
	public static String stripCdataMarkers(final String src) {
		if (src == null) return null;
		if (src.indexOf('<') < 0 && src.indexOf(']') < 0) return src;
		return src
				.replace("<![CDATA[", "         ")  // 9 chars → 9 spaces
				.replace("]]>", "   ");              // 3 chars → 3 spaces
	}

	private InputStream getStream(URL scriptURL, String scriptURI, TimingInfo info) throws Exception {
		if (Urls.isLocalFile(scriptURL)) {
			return Files.newInputStream(Paths.get(scriptURI.replace("file://", "")));
		} else {
			final java.net.http.HttpResponse<InputStream> resp = HttpNetwork.fetch(scriptURL.toURI(), "GET", null, null);
			info.setType(resp.headers().firstValue("Content-Type").orElse(null));
			final String enc = resp.headers().firstValue("Content-Encoding").orElse("");
			final InputStream body = resp.body();
			return HttpNetwork.GZIP_ENCODING.equalsIgnoreCase(enc)
					? new java.util.zip.GZIPInputStream(body)
					: body;
		}
	}


	/** {@inheritDoc} */
	@Override
	public void setDefer(final boolean defer) {
		this.defer = defer;
	}

	/** {@inheritDoc} */
	@Override
	public void setEvent(final String event) {
		setAttribute("event", event);
	}

	/** {@inheritDoc} */
	@Override
	public void setHtmlFor(final String htmlFor) {
		setAttribute("htmlFor", htmlFor);
	}

	/** {@inheritDoc} */
	@Override
	public void setSrc(final String src) {
		setAttribute("src", src);
	}

	/** {@inheritDoc} */
	@Override
	public void setText(final String text) {
		this.text = text;
	}

	/** {@inheritDoc} */
	@Override
	public void setType(final String type) {
		setAttribute("type", type);
	}

	/** {@inheritDoc} */
	@Override
	public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
		if (XHtmlParser.MODIFYING_KEY.equals(key) && !Boolean.TRUE.equals(data) && !alreadyStarted) {
			processScript();
		}
		return super.setUserData(key, data, handler);
	}
	@Override
	public String getIntegrity() {
		return getAttribute("integrity");
	}

	@Override
	public void setIntegrity(final String integrity) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isNoModule() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setNoModule(final boolean noModule) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getReferrerPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReferrerPolicy(final String referrerPolicy) {
		// TODO Auto-generated method stub
	}

	/** {@inheritDoc} */
	@Override
	protected RenderState createRenderState(final RenderState prevRenderState) {
		return new DisplayRenderState(prevRenderState, this, RenderState.DISPLAY_NONE);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "[object HTMLScriptElement]";
	}
}
