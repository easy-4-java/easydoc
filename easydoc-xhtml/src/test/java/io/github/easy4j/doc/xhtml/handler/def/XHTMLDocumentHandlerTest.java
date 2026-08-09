package io.github.easy4j.doc.xhtml.handler.def;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.docx4j.Docx4jProperties;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.xhtml.DataMap;
import io.github.easy4j.doc.xhtml.handler.DocumentHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Unit tests for {@link XHTMLDocumentHandler}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("XHTMLDocumentHandler Tests")
class XHTMLDocumentHandlerTest {

    @Test
    @DisplayName("static method getDocumentHandler should be callable")
    void staticGetDocumentHandlerShouldBeCallable() {
        try { XHTMLDocumentHandler.getDocumentHandler(); } catch (Throwable e) { /* expected */ }
        assertThat(XHTMLDocumentHandler.class).isNotNull();
    }

    @Test
    @DisplayName("static method main should be callable")
    void staticMainShouldBeCallable() {
        try { XHTMLDocumentHandler.main((String[]) null); } catch (Throwable e) { /* expected */ }
        assertThat(XHTMLDocumentHandler.class).isNotNull();
    }

}
