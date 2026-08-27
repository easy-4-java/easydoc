package io.github.easy4j.doc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.testutil.FontDiscoveryTestBase;

/**
 * Behavioral tests for {@link SampleDocument} that exercise the createContent
 * method and the addObject method with various templates.
 *
 * <p>Tests that trigger {@code PhysicalFonts.discoverPhysicalFonts()} extend
 * {@link FontDiscoveryTestBase} so they SKIP (via JUnit assumption) on machines
 * where font discovery throws — instead of swallowing the error and passing
 * vacuously.</p>
 */
@DisplayName("SampleDocument Behavioral Tests")
class SampleDocumentBehavioralTest extends FontDiscoveryTestBase {

    @Test
    @DisplayName("createContent adds paragraphs for discovered fonts")
    void createContentAddsParagraphs() throws Exception {
        assumeFontDiscoveryWorks();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = pkg.getMainDocumentPart();
        int sizeBefore = mdp.getContent().size();

        SampleDocument.createContent(mdp);

        // Once fonts are discovered, createContent must actually add paragraphs
        List<?> content = mdp.getContent();
        int sizeAfter = content.size();
        assertTrue(sizeAfter > sizeBefore,
                "createContent must add at least one paragraph when fonts are discovered");
    }

    @Test
    @DisplayName("addObject with sampleText adds a paragraph with font name")
    void addObjectSampleText() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = pkg.getMainDocumentPart();
        int sizeBefore = mdp.getContent().size();

        SampleDocument.addObject(mdp, SampleDocument.sampleText, "TestFont");
        assertEquals(sizeBefore + 1, mdp.getContent().size(),
                "addObject should add exactly one paragraph");
    }

    @Test
    @DisplayName("addObject with sampleTextBold adds bold paragraph")
    void addObjectSampleTextBold() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = pkg.getMainDocumentPart();
        int sizeBefore = mdp.getContent().size();

        SampleDocument.addObject(mdp, SampleDocument.sampleTextBold, "BoldFont");
        assertEquals(sizeBefore + 1, mdp.getContent().size());
    }

    @Test
    @DisplayName("addObject with sampleTextItalic adds italic paragraph")
    void addObjectSampleTextItalic() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = pkg.getMainDocumentPart();
        int sizeBefore = mdp.getContent().size();

        SampleDocument.addObject(mdp, SampleDocument.sampleTextItalic, "ItalicFont");
        assertEquals(sizeBefore + 1, mdp.getContent().size());
    }

    @Test
    @DisplayName("addObject with sampleTextBoldItalic adds bold-italic paragraph")
    void addObjectSampleTextBoldItalic() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = pkg.getMainDocumentPart();
        int sizeBefore = mdp.getContent().size();

        SampleDocument.addObject(mdp, SampleDocument.sampleTextBoldItalic, "BoldItalicFont");
        assertEquals(sizeBefore + 1, mdp.getContent().size());
    }

    @Test
    @DisplayName("addObject with all four templates adds 4 paragraphs")
    void addObjectAllTemplates() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = pkg.getMainDocumentPart();
        int sizeBefore = mdp.getContent().size();

        SampleDocument.addObject(mdp, SampleDocument.sampleText, "Font1");
        SampleDocument.addObject(mdp, SampleDocument.sampleTextBold, "Font2");
        SampleDocument.addObject(mdp, SampleDocument.sampleTextItalic, "Font3");
        SampleDocument.addObject(mdp, SampleDocument.sampleTextBoldItalic, "Font4");

        assertEquals(sizeBefore + 4, mdp.getContent().size());
    }

    @Test
    @DisplayName("sampleText template contains fontname placeholder")
    void sampleTextTemplateContainsPlaceholder() {
        assertTrue(SampleDocument.sampleText.contains("${fontname}"));
        assertTrue(SampleDocument.sampleText.contains("xmlns:w="));
    }

    @Test
    @DisplayName("sampleTextBold template contains bold element and placeholder")
    void sampleTextBoldTemplate() {
        assertTrue(SampleDocument.sampleTextBold.contains("<w:b />"));
        assertTrue(SampleDocument.sampleTextBold.contains("${fontname}"));
        assertTrue(SampleDocument.sampleTextBold.contains("bold;"));
    }

    @Test
    @DisplayName("sampleTextItalic template contains italic element and placeholder")
    void sampleTextItalicTemplate() {
        assertTrue(SampleDocument.sampleTextItalic.contains("<w:i />"));
        assertTrue(SampleDocument.sampleTextItalic.contains("${fontname}"));
        assertTrue(SampleDocument.sampleTextItalic.contains("italic"));
    }

    @Test
    @DisplayName("sampleTextBoldItalic template contains both elements")
    void sampleTextBoldItalicTemplate() {
        assertTrue(SampleDocument.sampleTextBoldItalic.contains("<w:b />"));
        assertTrue(SampleDocument.sampleTextBoldItalic.contains("<w:i />"));
        assertTrue(SampleDocument.sampleTextBoldItalic.contains("${fontname}"));
        assertTrue(SampleDocument.sampleTextBoldItalic.contains("bold italic"));
    }
}
