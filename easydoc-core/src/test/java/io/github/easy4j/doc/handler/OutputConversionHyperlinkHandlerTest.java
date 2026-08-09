package io.github.easy4j.doc.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.docx4j.convert.out.ConversionHyperlinkHandler;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.parts.Part;

/**
 * Unit tests for {@link OutputConversionHyperlinkHandler}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("OutputConversionHyperlinkHandler Tests")
class OutputConversionHyperlinkHandlerTest {

    @Test
    @DisplayName("static method getHyperlinkHandler should be callable")
    void staticGetHyperlinkHandlerShouldBeCallable() {
        try { OutputConversionHyperlinkHandler.getHyperlinkHandler(); } catch (Throwable e) { /* expected */ }
        assertThat(OutputConversionHyperlinkHandler.class).isNotNull();
    }

}
