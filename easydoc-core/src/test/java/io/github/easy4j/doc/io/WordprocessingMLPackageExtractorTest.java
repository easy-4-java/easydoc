package io.github.easy4j.doc.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

class WordprocessingMLPackageExtractorTest {

    private static File getTemplateFile() {
        URL res = WordprocessingMLPackageExtractorTest.class.getClassLoader().getResource("tpl/template.docx");
        if (res == null) return null;
        try {
            return new File(res.toURI());
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    void getWMLPackageExtractorReturnsInstance() {
        WordprocessingMLPackageExtractor extractor = WordprocessingMLPackageExtractor.getWMLPackageExtractor();
        assertNotNull(extractor);
    }

    @Test
    void getWMLPackageExtractorReturnsSameInstance() {
        WordprocessingMLPackageExtractor a = WordprocessingMLPackageExtractor.getWMLPackageExtractor();
        WordprocessingMLPackageExtractor b = WordprocessingMLPackageExtractor.getWMLPackageExtractor();
        assertNotNull(a);
        assertSame(a, b);
    }

    @Test
    void extractFromPackageReturnsNonEmptyString() throws Exception {
        File tpl = getTemplateFile();
        if (tpl == null || !tpl.exists()) return;
        WordprocessingMLPackage pkg = WordprocessingMLPackage.load(tpl);
        assertNotNull(pkg);
        WordprocessingMLPackageExtractor extractor = WordprocessingMLPackageExtractor.getWMLPackageExtractor();
        String text = extractor.extract(pkg);
        assertNotNull(text);
        assertTrue(text.length() > 0, "extracted text should be non-empty");
    }

    @Test
    void extractFromPackageWithWriterWritesOutput() throws Exception {
        File tpl = getTemplateFile();
        if (tpl == null || !tpl.exists()) return;
        WordprocessingMLPackage pkg = WordprocessingMLPackage.load(tpl);
        WordprocessingMLPackageExtractor extractor = WordprocessingMLPackageExtractor.getWMLPackageExtractor();
        StringWriter sw = new StringWriter();
        extractor.extract(pkg, sw);
        assertNotNull(sw.toString());
        assertTrue(sw.toString().length() > 0, "written text should be non-empty");
    }

    @Test
    void extractFromFileReturnsText() throws Exception {
        File tpl = getTemplateFile();
        if (tpl == null || !tpl.exists()) return;
        WordprocessingMLPackageExtractor extractor = WordprocessingMLPackageExtractor.getWMLPackageExtractor();
        String text = extractor.extract(tpl);
        assertNotNull(text);
        assertTrue(text.length() > 0, "extracted text from file should be non-empty");
    }

    @Test
    void extractFromStringPathReturnsText() throws Exception {
        File tpl = getTemplateFile();
        if (tpl == null || !tpl.exists()) return;
        WordprocessingMLPackageExtractor extractor = WordprocessingMLPackageExtractor.getWMLPackageExtractor();
        String text = extractor.extract(tpl.getAbsolutePath());
        assertNotNull(text);
        assertTrue(text.length() > 0, "extracted text from path string should be non-empty");
    }
}
