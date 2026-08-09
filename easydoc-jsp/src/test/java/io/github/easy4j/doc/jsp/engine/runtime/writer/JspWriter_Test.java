/* Copyright (c) 2024 */
package io.github.easy4j.doc.jsp.engine.runtime.writer;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** Unit tests for {@link JspWriter}. @author <a href="https://github.com/loong10k">Loong Wan</a> */
class JspWriter_Test {
    @Test void test_isAbstract() { assertThat(java.lang.reflect.Modifier.isAbstract(JspWriter.class.getModifiers())).isTrue(); }
}
