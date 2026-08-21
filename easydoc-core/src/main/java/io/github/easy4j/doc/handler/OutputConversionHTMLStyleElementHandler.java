/*
 * Copyright (c) 2018, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.easy4j.doc.handler;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.ConversionHTMLStyleElementHandler;
import org.docx4j.openpackaging.packages.OpcPackage;
import io.github.easy4j.doc.Docx4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OutputConversionHTMLStyleElementHandler implements ConversionHTMLStyleElementHandler {

	private static final Logger LOG = LoggerFactory.getLogger(OutputConversionHTMLStyleElementHandler.class);

	/** Schemes allowed for {@code docx4j.Convert.Out.HTML.CssIncludeUri}. (C-3) */
	private static final Set<String> ALLOWED_URI_SCHEMES = Set.of("https", "file");

	private static final OutputConversionHTMLStyleElementHandler OUTPUT_CONVERSION_HTMLSTYLE_ELEMENT_HANDLER = new OutputConversionHTMLStyleElementHandler();

	/**
	 * Generate a OutputConversionHTMLStyleElementHandler.
	 * @return the OutputConversionHTMLStyleElementHandler
	 */
	public static OutputConversionHTMLStyleElementHandler getStyleElementHandler() {
		return OUTPUT_CONVERSION_HTMLSTYLE_ELEMENT_HANDLER;
	}

	protected OutputConversionHTMLStyleElementHandler() {

	}

	@Override
	public Element createStyleElement(OpcPackage opcPackage, Document document, String styleDefinition) {

		// See XsltHTMLFunctions, which typically generates the String styleDefinition.
		// In practice, the styles are coupled to the document content, so you're
		// less likely to override their content; just whether they are linked or inline.

		Element ret = null;
		if ((styleDefinition != null) && (styleDefinition.length() > 0)) {
			ret = document.createElement("style");
			ret.setAttribute("type", "text/css");
			ret.appendChild(document.createComment(styleDefinition));
		}

		/**Key = docx4j.Convert.Out.HTML.CssIncludeUri*/
		String cssIncludeUri = Docx4jProperties.getProperty(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_CSSINCLUDEURI);
		if ((cssIncludeUri != null) && (cssIncludeUri.length() > 0)) {
			try {
				URI uri = new URI(cssIncludeUri);
				String scheme = uri.getScheme();
				if (scheme == null || !ALLOWED_URI_SCHEMES.contains(scheme.toLowerCase())) {
					LOG.warn("Refusing CSS include URI with disallowed scheme: {}", scheme);
				} else {
					ret = document.createElement("style");
					ret.setAttribute("type", "text/css");
					ret.appendChild(document.createComment(
							IOUtils.toString(uri, Charset.forName(Docx4jConstants.DEFAULT_CHARSETNAME))));
				}
			} catch (IOException e) {
				LOG.warn("Failed to read CSS include URI {}", cssIncludeUri, e);
			} catch (URISyntaxException e) {
				LOG.warn("Malformed CSS include URI {}", cssIncludeUri, e);
			}
		}
		/**Key = docx4j.Convert.Out.HTML.CssIncludePath*/
		String cssIncludePath = Docx4jProperties.getProperty(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_CSSINCLUDEPATH);
		if ((cssIncludePath != null) && (cssIncludePath.length() > 0)) {
			File path = new File(cssIncludePath).toPath().normalize().toFile();
			try (InputStream input = new FileInputStream(path)) {
				ret = document.createElement("style");
				ret.setAttribute("type", "text/css");
				ret.appendChild(document.createComment(
						IOUtils.toString(input, Charset.forName(Docx4jConstants.DEFAULT_CHARSETNAME))));
			} catch (IOException e) {
				LOG.warn("Failed to read CSS include path {}", cssIncludePath, e);
			}
		}

		return ret;
	}

}
