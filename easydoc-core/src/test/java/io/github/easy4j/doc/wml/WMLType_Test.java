/* Copyright (c) 2024 */
package io.github.easy4j.doc.wml;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** Unit tests for {@link WMLType}. @author <a href="https://github.com/loong10k">Loong Wan</a> */
class WMLType_Test {
    @Test void test_values() { assertThat(WMLType.values()).isNotEmpty(); }
    @Test void test_valueOf() { assertThat(WMLType.valueOf(WMLType.values()[0].name())).isNotNull(); }
}
