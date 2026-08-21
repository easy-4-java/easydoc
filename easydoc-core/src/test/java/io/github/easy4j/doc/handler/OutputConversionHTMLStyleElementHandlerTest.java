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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.xml.parsers.DocumentBuilderFactory;

import org.docx4j.Docx4jProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import io.github.easy4j.doc.Docx4jConstants;

class OutputConversionHTMLStyleElementHandlerTest {

	private Document doc;

	@BeforeEach
	void setUp() throws Exception {
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	}

	@AfterEach
	void tearDown() {
		// Reset global docx4j properties so test ordering does not bleed.
		// setProperty(key, null) throws NPE on a Hashtable-backed Properties store, so use remove() instead.
		Docx4jProperties.getProperties().remove(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_CSSINCLUDEURI);
		Docx4jProperties.getProperties().remove(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_CSSINCLUDEPATH);
	}

	@Test
	void nullStyleReturnsNull() {
		OutputConversionHTMLStyleElementHandler handler = OutputConversionHTMLStyleElementHandler.getStyleElementHandler();
		assertNull(handler.createStyleElement(null, doc, null));
	}

	@Test
	void emptyStyleReturnsNull() {
		OutputConversionHTMLStyleElementHandler handler = OutputConversionHTMLStyleElementHandler.getStyleElementHandler();
		assertNull(handler.createStyleElement(null, doc, ""));
	}

	@Test
	void plainCssProducesStyleElementWithComment() {
		OutputConversionHTMLStyleElementHandler handler = OutputConversionHTMLStyleElementHandler.getStyleElementHandler();
		Element el = handler.createStyleElement(null, doc, "body{color:#000;}");
		assertNotNull(el);
		assertEquals("style", el.getTagName());
		assertEquals("text/css", el.getAttribute("type"));
		assertEquals(1, el.getChildNodes().getLength());
		Node child = el.getFirstChild();
		assertNotNull(child);
		assertTrue(child instanceof org.w3c.dom.Comment, "first child must be a Comment");
		assertEquals("body{color:#000;}", ((org.w3c.dom.Comment) child).getData());
	}

	@Test
	void disallowedSchemeIsRejected_C3Regression() {
		Docx4jProperties.setProperty(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_CSSINCLUDEURI,
				"http://evil.example.com/x.css");
		OutputConversionHTMLStyleElementHandler handler = OutputConversionHTMLStyleElementHandler.getStyleElementHandler();
		Element el = handler.createStyleElement(null, doc, null);
		if (el != null) {
			// If a <style> was produced, its comment body must not contain attacker-controlled URI content.
			assertEquals(0, el.getChildNodes().getLength(),
					"disallowed-scheme URI must not contribute comment content");
		}
	}

	@Test
	void allowedHttpsSchemeIsAcceptedButFileReadFailsSilently() {
		Docx4jProperties.setProperty(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_CSSINCLUDEURI,
				"https://nonexistent.invalid/x.css");
		OutputConversionHTMLStyleElementHandler handler = OutputConversionHTMLStyleElementHandler.getStyleElementHandler();
		// The point is no exception bubbles out. Result may be null (network unavailable) or a <style>.
		Element el = handler.createStyleElement(null, doc, null);
		if (el != null) {
			assertEquals("style", el.getTagName());
		}
	}

	@Test
	void pathSchemeWithClasspathFixtureIsLoaded() {
		Docx4jProperties.setProperty(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_CSSINCLUDEPATH,
				"src/test/resources/tpl/inline.css");
		OutputConversionHTMLStyleElementHandler handler = OutputConversionHTMLStyleElementHandler.getStyleElementHandler();
		Element el = handler.createStyleElement(null, doc, null);
		assertNotNull(el);
		assertEquals("style", el.getTagName());
		assertEquals("text/css", el.getAttribute("type"));
	}

	@Test
	void singletonIsStable() {
		OutputConversionHTMLStyleElementHandler a = OutputConversionHTMLStyleElementHandler.getStyleElementHandler();
		OutputConversionHTMLStyleElementHandler b = OutputConversionHTMLStyleElementHandler.getStyleElementHandler();
		assertSame(a, b);
	}
}