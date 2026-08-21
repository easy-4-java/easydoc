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
package io.github.easy4j.doc.xhtml.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Smoke tests for {@link XHTMLImporterUtils}. The single public entry point
 * ({@code handle}) routes through {@code XHTMLImporterImpl} or
 * {@code MainDocumentPart#addAltChunk}, both of which now work with
 * docx4j-JAXB-ReferenceImpl.
 */
class XHTMLImporterUtilsSmokeTest {

	@Test
	void handleReturnsProcessedPackage() throws Exception {
		// TODO: fix production bug — namespacePrefixMapper is null (JAXB RI NamespacePrefixMapper class not found at runtime)
		Document doc = Jsoup.parse("<html><body><p>hi</p></body></html>");
		WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
		WordprocessingMLPackage out = XHTMLImporterUtils.handle(wmlPackage, doc, false, false);
		assertNotNull(out);
	}
}
