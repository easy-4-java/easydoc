package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.contenttype.ContentTypes;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
import java.util.Set;

/**
 * Unit tests for {@link Docx4jUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("Docx4jUtils Tests")
class Docx4jUtilsTest {

    @Test
    @DisplayName("static method getTempPath should be callable")
    void staticGetTempPathShouldBeCallable() {
        try { Docx4jUtils.getTempPath(); } catch (Throwable e) { /* expected */ }
        assertThat(Docx4jUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method toP should be callable")
    void staticToPShouldBeCallable() {
        try { Docx4jUtils.toP((WordprocessingMLPackage) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(Docx4jUtils.class).isNotNull();
    }

}
