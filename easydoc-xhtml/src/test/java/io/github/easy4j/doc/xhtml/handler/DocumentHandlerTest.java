package io.github.easy4j.doc.xhtml.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import io.github.easy4j.doc.xhtml.DataMap;
import org.jsoup.nodes.Document;
import java.util.Map;

/**
 * Unit tests for {@link DocumentHandler}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("DocumentHandler Tests")
class DocumentHandlerTest {

    @Test
    @DisplayName("should be an interface")
    void shouldBeAnInterface() {
        assertThat(DocumentHandler.class).isInterface();
    }

    @Test
    @DisplayName("class should be loadable")
    void classShouldBeLoadable() {
        assertThat(DocumentHandler.class.getName()).isEqualTo("io.github.easy4j.doc.xhtml.handler.DocumentHandler");
    }

}
