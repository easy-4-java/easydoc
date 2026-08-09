/* Copyright (c) 2024 */
package io.github.easy4j.doc.fonts;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** Unit tests for {@link ChineseFont}. @author <a href="https://github.com/loong10k">Loong Wan</a> */
class ChineseFont_Test {
    @Test void test_values() { assertThat(ChineseFont.values()).isNotEmpty(); }
    @Test void test_valueOf() { assertThat(ChineseFont.valueOf(ChineseFont.values()[0].name())).isNotNull(); }
}
