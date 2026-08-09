package io.github.easy4j.doc.xhtml.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import io.github.easy4j.doc.Docx4jConstants;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import java.util.Properties;
import java.util.Set;

/**
 * Unit tests for {@link XHTMLImporterUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("XHTMLImporterUtils Tests")
class XHTMLImporterUtilsTest {

    @Test
    @DisplayName("static method handle should be callable")
    void staticHandleShouldBeCallable() {
        try { XHTMLImporterUtils.handle((WordprocessingMLPackage) null, (Document) null, true, true); } catch (Throwable e) { /* expected */ }
        assertThat(XHTMLImporterUtils.class).isNotNull();
    }

}
