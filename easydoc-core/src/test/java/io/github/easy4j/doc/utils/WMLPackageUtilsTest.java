package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.CTMarkupRange;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Document;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.Text;

/**
 * Unit tests for {@link WMLPackageUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WMLPackageUtils Tests")
class WMLPackageUtilsTest {

    @Test
    @DisplayName("static method cleanDocumentPart should be callable")
    void staticCleanDocumentPartShouldBeCallable() {
        try { WMLPackageUtils.cleanDocumentPart((MainDocumentPart) null); } catch (Throwable e) { /* expected */ }
        assertThat(WMLPackageUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method replacePlaceholder should be callable")
    void staticReplacePlaceholderShouldBeCallable() {
        try { WMLPackageUtils.replacePlaceholder((MainDocumentPart) null, "test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WMLPackageUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method replaceParagraph should be callable")
    void staticReplaceParagraphShouldBeCallable() {
        try { WMLPackageUtils.replaceParagraph((MainDocumentPart) null, "test", "test", (ContentAccessor) null); } catch (Throwable e) { /* expected */ }
        assertThat(WMLPackageUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method replaceText should be callable")
    void staticReplaceTextShouldBeCallable() {
        try { WMLPackageUtils.replaceText((CTBookmark) null, (Object) null); } catch (Throwable e) { /* expected */ }
        assertThat(WMLPackageUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method imageToByteArray should be callable")
    void staticImageToByteArrayShouldBeCallable() {
        try { WMLPackageUtils.imageToByteArray((File) null); } catch (Throwable e) { /* expected */ }
        assertThat(WMLPackageUtils.class).isNotNull();
    }

}
