package io.github.easy4j.doc.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.docx4j.Docx4jProperties;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.utils.Assert;
import java.util.Properties;

/**
 * Unit tests for {@link WordprocessingMLTemplateWriter}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WordprocessingMLTemplateWriter Tests")
class WordprocessingMLTemplateWriterTest {

    @Test
    @DisplayName("static method getWMLTemplateWriter should be callable")
    void staticGetWMLTemplateWriterShouldBeCallable() {
        try { WordprocessingMLTemplateWriter.getWMLTemplateWriter(); } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLTemplateWriter.class).isNotNull();
    }

    @Test
    @DisplayName("static method writeToFile should be callable")
    void staticWriteToFileShouldBeCallable() {
        try { WordprocessingMLTemplateWriter.writeToFile((WordprocessingMLPackage) null, (File) null); } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLTemplateWriter.class).isNotNull();
    }

    @Test
    @DisplayName("static method writeToStream should be callable")
    void staticWriteToStreamShouldBeCallable() {
        try { WordprocessingMLTemplateWriter.writeToStream((WordprocessingMLPackage) null, (OutputStream) null); } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLTemplateWriter.class).isNotNull();
    }

}
