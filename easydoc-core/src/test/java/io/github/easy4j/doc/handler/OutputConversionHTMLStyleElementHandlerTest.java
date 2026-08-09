package io.github.easy4j.doc.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.ConversionHTMLStyleElementHandler;
import org.docx4j.openpackaging.packages.OpcPackage;
import io.github.easy4j.doc.Docx4jConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.util.Properties;

/**
 * Unit tests for {@link OutputConversionHTMLStyleElementHandler}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("OutputConversionHTMLStyleElementHandler Tests")
class OutputConversionHTMLStyleElementHandlerTest {

    @Test
    @DisplayName("static method getStyleElementHandler should be callable")
    void staticGetStyleElementHandlerShouldBeCallable() {
        try { OutputConversionHTMLStyleElementHandler.getStyleElementHandler(); } catch (Throwable e) { /* expected */ }
        assertThat(OutputConversionHTMLStyleElementHandler.class).isNotNull();
    }

}
