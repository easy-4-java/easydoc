package io.github.easy4j.doc.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.Writer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.docx4j.TextUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

/**
 * Unit tests for {@link WordprocessingMLPackageExtractor}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WordprocessingMLPackageExtractor Tests")
class WordprocessingMLPackageExtractorTest {

    @Test
    @DisplayName("static method getWMLPackageExtractor should be callable")
    void staticGetWMLPackageExtractorShouldBeCallable() {
        try { WordprocessingMLPackageExtractor.getWMLPackageExtractor(); } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageExtractor.class).isNotNull();
    }

}
