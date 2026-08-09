/* Copyright (c) 2024 */
package io.github.easy4j.doc.jsp.engine;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** Unit tests for {@link JspEngine}. @author <a href="https://github.com/loong10k">Loong Wan</a> */
class JspEngine_Test {
    @Test void test_isAbstract() { assertThat(java.lang.reflect.Modifier.isAbstract(JspEngine.class.getModifiers())).isTrue(); }
}
