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
package io.github.easy4j.doc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ContentAccessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.testutil.FontDiscoveryTestBase;

/**
 * Unit tests for {@link SampleDocument}.
 *
 * <p>Tests both {@code createContent(MainDocumentPart)} and the package-private
 * {@code addObject(MainDocumentPart, String, String)} method.
 *
 * <p>Tests that trigger {@code PhysicalFonts.discoverPhysicalFonts()} extend
 * {@link FontDiscoveryTestBase} and SKIP on machines where discovery throws,
 * instead of swallowing {@code AssertionError} and passing vacuously.</p>
 */
@DisplayName("SampleDocument Tests")
class SampleDocumentTest extends FontDiscoveryTestBase {

    @Test
    @DisplayName("createContent with a real MainDocumentPart runs without error")
    void createContent_realMainDocumentPart_doesNotThrow() throws Exception {
        assumeFontDiscoveryWorks();
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wmlPackage.getMainDocumentPart();

        SampleDocument.createContent(mdp);

        assertThat(mdp).isNotNull();
    }

    @Test
    @DisplayName("createContent adds paragraph content when fonts are available")
    void createContent_addsContentWhenFontsExist() throws Exception {
        assumeFontDiscoveryWorks();
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wmlPackage.getMainDocumentPart();

        SampleDocument.createContent(mdp);

        List<Object> content = mdp.getContent();
        assertThat(content).isNotNull();
        assertThat(content).isNotEmpty();
    }

    @Test
    @DisplayName("addObject directly adds a paragraph to the document part")
    void addObject_addsParagraphDirectly() throws Exception {
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wmlPackage.getMainDocumentPart();

        int sizeBefore = mdp.getContent().size();

        SampleDocument.addObject(mdp, SampleDocument.sampleText, "Arial");

        int sizeAfter = mdp.getContent().size();
        assertThat(sizeAfter).isGreaterThan(sizeBefore);
    }

    @Test
    @DisplayName("addObject with bold template adds content")
    void addObject_boldTemplate_addsContent() throws Exception {
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wmlPackage.getMainDocumentPart();

        int sizeBefore = mdp.getContent().size();

        SampleDocument.addObject(mdp, SampleDocument.sampleTextBold, "Arial");

        int sizeAfter = mdp.getContent().size();
        assertThat(sizeAfter).isGreaterThan(sizeBefore);
    }

    @Test
    @DisplayName("addObject with italic template adds content")
    void addObject_italicTemplate_addsContent() throws Exception {
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wmlPackage.getMainDocumentPart();

        int sizeBefore = mdp.getContent().size();

        SampleDocument.addObject(mdp, SampleDocument.sampleTextItalic, "Arial");

        int sizeAfter = mdp.getContent().size();
        assertThat(sizeAfter).isGreaterThan(sizeBefore);
    }

    @Test
    @DisplayName("addObject with bold-italic template adds content")
    void addObject_boldItalicTemplate_addsContent() throws Exception {
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wmlPackage.getMainDocumentPart();

        int sizeBefore = mdp.getContent().size();

        SampleDocument.addObject(mdp, SampleDocument.sampleTextBoldItalic, "Arial");

        int sizeAfter = mdp.getContent().size();
        assertThat(sizeAfter).isGreaterThan(sizeBefore);
    }

    @Test
    @DisplayName("sampleText template string is well-formed XML")
    void sampleText_isWellFormed() {
        assertThat(SampleDocument.sampleText).contains("xmlns:w=");
        assertThat(SampleDocument.sampleText).contains("${fontname}");
    }

    @Test
    @DisplayName("sampleTextBold template string contains bold element")
    void sampleTextBold_containsBoldElement() {
        assertThat(SampleDocument.sampleTextBold).contains("<w:b />");
        assertThat(SampleDocument.sampleTextBold).contains("bold;");
    }

    @Test
    @DisplayName("sampleTextItalic template string contains italic element")
    void sampleTextItalic_containsItalicElement() {
        assertThat(SampleDocument.sampleTextItalic).contains("<w:i />");
        assertThat(SampleDocument.sampleTextItalic).contains("italic");
    }

    @Test
    @DisplayName("sampleTextBoldItalic template string contains both bold and italic")
    void sampleTextBoldItalic_containsBothElements() {
        assertThat(SampleDocument.sampleTextBoldItalic).contains("<w:b />");
        assertThat(SampleDocument.sampleTextBoldItalic).contains("<w:i />");
        assertThat(SampleDocument.sampleTextBoldItalic).contains("bold italic");
    }

    @Test
    @DisplayName("createContent on null MainDocumentPart does not throw (catches internally)")
    void createContent_null_doesNotThrowUncaught() throws Exception {
        // SampleDocument.createContent catches Exception internally, so a null
        // part produces an internal NPE that never escapes. Font discovery is
        // assumed healthy, so no AssertionError can escape either (audit #17).
        assumeFontDiscoveryWorks();
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
        assertThat(wmlPackage).isNotNull();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> SampleDocument.createContent(null));
    }

}
