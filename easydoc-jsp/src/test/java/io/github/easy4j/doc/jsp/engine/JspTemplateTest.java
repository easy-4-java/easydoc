package io.github.easy4j.doc.jsp.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import javax.servlet.ServletException;

/**
 * Unit tests for {@link JspTemplate}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("JspTemplate Tests")
class JspTemplateTest {

    @Test
    @DisplayName("should be an interface")
    void shouldBeAnInterface() {
        assertThat(JspTemplate.class).isInterface();
    }

    @Test
    @DisplayName("class should be loadable")
    void classShouldBeLoadable() {
        assertThat(JspTemplate.class.getName()).isEqualTo("io.github.easy4j.doc.jsp.engine.JspTemplate");
    }

}
