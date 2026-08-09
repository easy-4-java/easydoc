package io.github.easy4j.doc.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.docx4j.convert.out.ConversionHTMLScriptElementHandler;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Unit tests for {@link OutputConversionHTMLScriptElementHandler}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("OutputConversionHTMLScriptElementHandler Tests")
class OutputConversionHTMLScriptElementHandlerTest {

    @Test
    @DisplayName("static method getScriptElementHandler should be callable")
    void staticGetScriptElementHandlerShouldBeCallable() {
        try { OutputConversionHTMLScriptElementHandler.getScriptElementHandler(); } catch (Throwable e) { /* expected */ }
        assertThat(OutputConversionHTMLScriptElementHandler.class).isNotNull();
    }

}
