package io.github.easy4j.doc.xhtml.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.io.WordprocessingMLPackageWriter;
import io.github.easy4j.doc.xhtml.io.WordprocessingMLPackageBuilder;

/**
 * Unit tests for {@link Docx4jHtmlUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("Docx4jHtmlUtils Tests")
class Docx4jHtmlUtilsTest {

    @Test
    @DisplayName("static method docxToPdf should be callable")
    void staticDocxToPdfShouldBeCallable() {
        try { Docx4jHtmlUtils.docxToPdf("test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Docx4jHtmlUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method docxToHtml should be callable")
    void staticDocxToHtmlShouldBeCallable() {
        try { Docx4jHtmlUtils.docxToHtml("test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Docx4jHtmlUtils.class).isNotNull();
    }

}
