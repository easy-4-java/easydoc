/* Copyright (c) 2024 */
package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** Unit tests for {@link Assert}. @author <a href="https://github.com/loong10k">Loong Wan</a> */
class Assert_Test {
    @Test void test_isAbstract() { assertThat(java.lang.reflect.Modifier.isAbstract(Assert.class.getModifiers())).isTrue(); }
}
