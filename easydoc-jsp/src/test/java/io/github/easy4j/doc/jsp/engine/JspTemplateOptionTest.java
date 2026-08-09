package io.github.easy4j.doc.jsp.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link JspTemplateOption}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("JspTemplateOption Tests")
class JspTemplateOptionTest {

    @Test
    @DisplayName("class should be loadable")
    void classShouldBeLoadable() {
        assertThat(JspTemplateOption.class.getName()).isEqualTo("io.github.easy4j.doc.jsp.engine.JspTemplateOption");
    }

}
