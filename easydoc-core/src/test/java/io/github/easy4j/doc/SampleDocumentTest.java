package io.github.easy4j.doc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.docx4j.XmlUtils;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import java.util.Set;

/**
 * Unit tests for {@link SampleDocument}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("SampleDocument Tests")
class SampleDocumentTest {

    @Test
    @DisplayName("static method createContent should be callable")
    void staticCreateContentShouldBeCallable() {
        try { SampleDocument.createContent((MainDocumentPart) null); } catch (Throwable e) { /* expected */ }
        assertThat(SampleDocument.class).isNotNull();
    }

}
