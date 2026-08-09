package io.github.easy4j.doc.wml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link WMLType}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WMLType Tests")
class WMLTypeTest {

    @Test
    @DisplayName("should contain expected enum constants")
    void shouldContainExpectedConstants() {
        assertThat(WMLType.values()).isNotEmpty();
        assertThat(WMLType.valueOf("PDF_SUFFIX")).isEqualTo(WMLType.PDF_SUFFIX);
        assertThat(WMLType.valueOf("DOCX_SUFFIX")).isEqualTo(WMLType.DOCX_SUFFIX);
    }

    @Test
    @DisplayName("should have correct number of constants")
    void shouldHaveCorrectNumberOfConstants() {
        assertThat(WMLType.values().length).isEqualTo(2);
    }

}
