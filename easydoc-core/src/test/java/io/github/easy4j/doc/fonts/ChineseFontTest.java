package io.github.easy4j.doc.fonts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.net.URL;
import java.io.File;

/**
 * Unit tests for {@link ChineseFont}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("ChineseFont Tests")
class ChineseFontTest {

    @Test
    @DisplayName("should contain expected enum constants")
    void shouldContainExpectedConstants() {
        assertThat(ChineseFont.values()).isNotEmpty();
        assertThat(ChineseFont.valueOf("SIMFANG")).isEqualTo(ChineseFont.SIMFANG);
        assertThat(ChineseFont.valueOf("SIMHEI")).isEqualTo(ChineseFont.SIMHEI);
        assertThat(ChineseFont.valueOf("SIMKAI")).isEqualTo(ChineseFont.SIMKAI);
        assertThat(ChineseFont.valueOf("SIMSUM")).isEqualTo(ChineseFont.SIMSUM);
        assertThat(ChineseFont.valueOf("STFANGSO")).isEqualTo(ChineseFont.STFANGSO);
    }

    @Test
    @DisplayName("should have correct number of constants")
    void shouldHaveCorrectNumberOfConstants() {
        assertThat(ChineseFont.values().length).isEqualTo(5);
    }

}
