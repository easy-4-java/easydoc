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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class OutputConversionHTMLScriptElementHandlerTest {

	@Test
	void nullScriptDefinitionReturnsNull() throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		OutputConversionHTMLScriptElementHandler handler = OutputConversionHTMLScriptElementHandler.getScriptElementHandler();
		assertNull(handler.createScriptElement(null, doc, null));
		assertNull(handler.createScriptElement(null, doc, ""));
	}

	@Test
	void plainScriptDefinitionProducesScriptElementWithComment() throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		OutputConversionHTMLScriptElementHandler handler = OutputConversionHTMLScriptElementHandler.getScriptElementHandler();
		Element el = handler.createScriptElement(null, doc, "alert('hi');");
		assertNotNull(el);
		assertEquals("script", el.getTagName());
		assertEquals("text/javascript", el.getAttribute("type"));
		assertEquals(1, el.getChildNodes().getLength());
		Node child = el.getFirstChild();
		assertNotNull(child);
		assertTrue(child instanceof Comment, "first child must be a Comment");
		assertEquals("alert('hi');", ((Comment) child).getData());
	}

	@Test
	void scriptDefinitionWithDoubleDashIsEscaped_C2Regression() throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		OutputConversionHTMLScriptElementHandler handler = OutputConversionHTMLScriptElementHandler.getScriptElementHandler();
		String payload = "x--><script>alert('xss')</script>";
		Element el = handler.createScriptElement(null, doc, payload);
		assertNotNull(el);
		Comment comment = (Comment) el.getFirstChild();
		String data = comment.getData();
		assertFalse(data.contains("-->"), "comment body must not contain comment-closing sequence");
	}

	@Test
	void singletonIsStable() {
		OutputConversionHTMLScriptElementHandler a = OutputConversionHTMLScriptElementHandler.getScriptElementHandler();
		OutputConversionHTMLScriptElementHandler b = OutputConversionHTMLScriptElementHandler.getScriptElementHandler();
		assertSame(a, b);
	}
}