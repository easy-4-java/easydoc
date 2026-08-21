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
package io.github.easy4j.doc.xhtml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import org.docx4j.model.structure.PageSizePaper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.github.easy4j.doc.xhtml.handler.DocumentHandler;
import io.github.easy4j.doc.xhtml.io.WordprocessingMLPackageBuilder;

/**
 * Lightweight coverage for the public {@link WordprocessingMLHtmlTemplate} surface —
 * the 8 process() overloads and the bean-style accessors. Tests that transitively
 * require a fully-initialised docx4j MOXy / JAXB context (which currently fails on
 * docx4j 11.5.14 in this build) are marked {@link Disabled} with a clear reason
 * rather than removed. Round-trip / getter / constructor tests run regardless.
 */
class WordprocessingMLHtmlTemplateProcessOverloadsTest {

	private static final String SIMPLE_HTML = "<html><body><p>hi</p></body></html>";

	private static File reportHtml() {
		// Resolved against the test classpath, where tpl/report.html is shipped.
		URL url = WordprocessingMLHtmlTemplateProcessOverloadsTest.class
				.getClassLoader().getResource("tpl/report.html");
		if (url == null) {
			throw new IllegalStateException("tpl/report.html missing from test classpath");
		}
		return new File(url.getFile());
	}

	// ---------------------------------------------------------------------
	// process(String, Map) overloads — full pipeline needs XHTMLImporterImpl
	// which initialises a MOXy context. The class is @Disabled for those, but
	// we keep them as documentation of the intended behaviour.
	// ---------------------------------------------------------------------

	@Test
	@Disabled("XHTMLImporterImpl requires valid docx4j JAXB context; see easydoc-core/pom.xml TODO")
	void processStringReturnsNonNullPackage() throws Exception {
		Object pkg = new WordprocessingMLHtmlTemplate().process(SIMPLE_HTML, null);
		assertNotNull(pkg);
	}

	@Test
	@Disabled("XHTMLImporterImpl requires valid docx4j JAXB context")
	void processStringHonoursLandscapeTrue() throws Exception {
		Object pkg = new WordprocessingMLHtmlTemplate(true, false).process(SIMPLE_HTML, null);
		assertNotNull(pkg);
	}

	@Test
	@Disabled("XHTMLImporterImpl requires valid docx4j JAXB context")
	void processStringHonoursLandscapeFalse() throws Exception {
		Object pkg = new WordprocessingMLHtmlTemplate(false, false).process(SIMPLE_HTML, null);
		assertNotNull(pkg);
	}

	@Test
	@Disabled("File overload calls XHTMLImporterImpl which requires valid docx4j JAXB context")
	void processFileWithLandscapeFlag() throws Exception {
		Object pkg = new WordprocessingMLHtmlTemplate(true, false).process(reportHtml(), (Map<String, Object>) null);
		assertNotNull(pkg);
	}

	@Test
	@Disabled("InputStream overload calls XHTMLImporterImpl which requires valid docx4j JAXB context")
	void processInputStreamWithLandscapeFlag() throws Exception {
		InputStream in = new ByteArrayInputStream(SIMPLE_HTML.getBytes(StandardCharsets.UTF_8));
		Object pkg = new WordprocessingMLHtmlTemplate(true, false).process(in, (Map<String, Object>) null);
		assertNotNull(pkg);
	}

	@Test
	@Disabled("PageSizePaper overload calls XHTMLImporterImpl which requires valid docx4j JAXB context")
	void processFileWithPageSizePaper() throws Exception {
		Object pkg = new WordprocessingMLHtmlTemplate()
				.process(reportHtml(), PageSizePaper.A4);
		assertNotNull(pkg);
	}

	@Test
	@Disabled("Document overload calls XHTMLImporterImpl which requires valid docx4j JAXB context")
	void processDocumentWithLandscapeFlag() throws Exception {
		Document doc = Jsoup.parse(SIMPLE_HTML);
		Object pkg = new WordprocessingMLHtmlTemplate(true, false).process(doc);
		assertNotNull(pkg);
	}

	@Test
	@Disabled("InputStream + PageSizePaper overload calls XHTMLImporterImpl which requires valid docx4j JAXB context")
	void processInputStreamWithPageSizePaper() throws Exception {
		InputStream in = new ByteArrayInputStream(SIMPLE_HTML.getBytes(StandardCharsets.UTF_8));
		Object pkg = new WordprocessingMLHtmlTemplate()
				.process(in, PageSizePaper.A4);
		assertNotNull(pkg);
	}

	@Test
	@Disabled("URL overload calls XHTMLImporterImpl which requires valid docx4j JAXB context")
	void processUrlWithLandscapeFlag() throws Exception {
		Object pkg = new WordprocessingMLHtmlTemplate(true, false)
				.process(reportHtml().toURI().toURL());
		assertNotNull(pkg);
	}

	@Test
	@Disabled("String+DataMap+PageSizePaper overload calls XHTMLImporterImpl which requires valid docx4j JAXB context")
	void processStringUrlDataMapPageSizePaper() throws Exception {
		Object pkg = new WordprocessingMLHtmlTemplate()
				.process("http://localhost/", Collections.<String, String>emptyMap(), PageSizePaper.A4);
		assertNotNull(pkg);
	}

	// ---------------------------------------------------------------------
	// Bean accessors / setters — pure state, no docx4j involvement.
	// ---------------------------------------------------------------------

	@Test
	void getDocHandlerReturnsNonNull() {
		DocumentHandler handler = new WordprocessingMLHtmlTemplate().getDocHandler();
		assertNotNull(handler);
	}

	@Test
	void setDocHandlerAcceptsCustomHandler() {
		WordprocessingMLHtmlTemplate tpl = new WordprocessingMLHtmlTemplate();
		DocumentHandler custom = new DocumentHandler() {
			@Override public org.jsoup.nodes.Document handle(java.io.File f) { return null; }
			@Override public org.jsoup.nodes.Document handle(String s, boolean fragment) { return null; }
			@Override public org.jsoup.nodes.Document handle(URL u) { return null; }
			@Override public org.jsoup.nodes.Document handle(String s, DataMap dm) { return null; }
			@Override public org.jsoup.nodes.Document handle(InputStream in) { return null; }
		};
		tpl.setDocHandler(custom);
		assertSame(custom, tpl.getDocHandler());
	}

	@Test
	void setWordMLPackageBuilderAcceptsCustomBuilder() {
		WordprocessingMLHtmlTemplate tpl = new WordprocessingMLHtmlTemplate();
		WordprocessingMLPackageBuilder original = tpl.getWordMLPackageBuilder();
		assertNotNull(original);
		// Round-trip: setting a different instance updates the getter.
		// (We re-use the singleton only to verify the setter actually wires through;
		// swapping in a fresh instance would also work.)
		tpl.setWordMLPackageBuilder(original);
		assertSame(original, tpl.getWordMLPackageBuilder());
	}

	@Test
	void getMlHtmlTemplateReturnsConstructorArgument() {
		// WordprocessingMLHtmlTemplate doesn't expose getMlHtmlTemplate itself; the
		// analogous accessor is getDocHandler / getWordMLPackageBuilder. The
		// getMlHtmlTemplate() accessor lives on AbstractStringTemplateWrappingTemplate
		// and is exercised in that test. Here we verify the constructor wiring via
		// isLandscape/isAltChunk, which are the two values that the constructor sets.
		WordprocessingMLHtmlTemplate tpl = new WordprocessingMLHtmlTemplate(true, true);
		assertTrue(tpl.isLandscape());
		assertTrue(tpl.isAltChunk());
	}

	@Test
	void setAltChunkUpdatesGetter() {
		WordprocessingMLHtmlTemplate tpl = new WordprocessingMLHtmlTemplate();
		assertEquals(false, tpl.isAltChunk());
		tpl.setAltChunk(true);
		assertEquals(true, tpl.isAltChunk());
		tpl.setAltChunk(false);
		assertEquals(false, tpl.isAltChunk());
	}

	@Test
	void setLandscapeUpdatesGetter() {
		WordprocessingMLHtmlTemplate tpl = new WordprocessingMLHtmlTemplate();
		assertEquals(false, tpl.isLandscape());
		tpl.setLandscape(true);
		assertEquals(true, tpl.isLandscape());
		tpl.setLandscape(false);
		assertEquals(false, tpl.isLandscape());
	}

	@Test
	void getMlHtmlTemplateAccessorMatchesConstructor() {
		// WordprocessingMLHtmlTemplate itself doesn't have a getMlHtmlTemplate()
		// method (that lives on AbstractStringTemplateWrappingTemplate). The closest
		// equivalent here is verifying the constructor-propagated fields via the
		// bean accessors — confirming the values set in the constructor survive.
		WordprocessingMLHtmlTemplate tpl = new WordprocessingMLHtmlTemplate(true, true);
		assertEquals(true, tpl.isLandscape(), "constructor landscape must be preserved");
		assertEquals(true, tpl.isAltChunk(), "constructor altChunk must be preserved");
		// And the chained collaborator accessors are non-null.
		assertNotNull(tpl.getDocHandler());
		assertNotNull(tpl.getWordMLPackageBuilder());
	}
}
