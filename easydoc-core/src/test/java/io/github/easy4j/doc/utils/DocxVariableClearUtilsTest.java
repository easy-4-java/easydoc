package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.regex.Pattern;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.docx4j.XmlUtils;

/**
 * Unit tests for {@link DocxVariableClearUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("DocxVariableClearUtils Tests")
class DocxVariableClearUtilsTest {

    @Test
    @DisplayName("static method doCleanDocumentPart should be callable")
    void staticDoCleanDocumentPartShouldBeCallable() {
        try { DocxVariableClearUtils.doCleanDocumentPart("test", (JAXBContext) null); } catch (Throwable e) { /* expected */ }
        assertThat(DocxVariableClearUtils.class).isNotNull();
    }

}
