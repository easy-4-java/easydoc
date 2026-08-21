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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

/**
 * Regression guard for the H-4 landscape bug: {@code process(String, Map)} previously
 * dropped the constructor-set {@code landscape} flag by routing to the 2-arg
 * {@code buildWhithXhtml(String, boolean altChunk)} overload. Every engine adapter
 * funnels through this method, so the regression affected all 9 templates.
 *
 * <p>The two {@code process(...)} cases below bypass the XHTML importer (which requires
 * a fully-styled docx) and validate the {@code landscape} plumbing end-to-end through
 * the public {@link WordprocessingMLHtmlTemplate} API.
 */
class WordprocessingMLHtmlTemplateLandscapeTest {

	@Test
	void landscapeGetterSetterRoundTrip() {
		WordprocessingMLHtmlTemplate tpl = new WordprocessingMLHtmlTemplate(false, false);
		assertEquals(false, tpl.isLandscape());
		tpl.setLandscape(true);
		assertEquals(true, tpl.isLandscape());
		tpl.setLandscape(false);
		assertEquals(false, tpl.isLandscape());
	}

	@Test
	void constructorSetsLandscapeTrue() {
		WordprocessingMLHtmlTemplate tpl = new WordprocessingMLHtmlTemplate(true, false);
		assertTrue(tpl.isLandscape());
	}

	@Test
	void constructorSetsLandscapeFalse() {
		WordprocessingMLHtmlTemplate tpl = new WordprocessingMLHtmlTemplate(false, false);
		assertEquals(false, tpl.isLandscape());
	}

	@Test
	void landscapeTrueCreatesLandscapePageDimensions() throws Exception {
		WordprocessingMLPackage portrait = WordprocessingMLPackage.createPackage(PageSizePaper.A4, false);
		WordprocessingMLPackage landscape = WordprocessingMLPackage.createPackage(PageSizePaper.A4, true);
		assertNotNull(portrait.getMainDocumentPart().getJaxbElement().getBody().getSectPr().getPgSz());
		assertNotNull(landscape.getMainDocumentPart().getJaxbElement().getBody().getSectPr().getPgSz());
		// Landscape swaps width and height.
		int portraitW = portrait.getMainDocumentPart().getJaxbElement().getBody().getSectPr().getPgSz().getW().intValue();
		int portraitH = portrait.getMainDocumentPart().getJaxbElement().getBody().getSectPr().getPgSz().getH().intValue();
		int landscapeW = landscape.getMainDocumentPart().getJaxbElement().getBody().getSectPr().getPgSz().getW().intValue();
		int landscapeH = landscape.getMainDocumentPart().getJaxbElement().getBody().getSectPr().getPgSz().getH().intValue();
		assertEquals(portraitH, landscapeW, "landscape should swap width/height");
		assertEquals(portraitW, landscapeH, "landscape should swap width/height");
	}
}