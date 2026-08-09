/* Copyright (c) 2024 */
package io.github.easy4j.doc.jsp.engine;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** Unit tests for {@link JspConfig}. @author <a href="https://github.com/loong10k">Loong Wan</a> */
class JspConfig_Test {
    @Test void test_DEFAULT_CONFIG_FILE() { assertThat(JspConfig.DEFAULT_CONFIG_FILE).isNotNull(); }
    @Test void test_IMPORT_CLASSES() { assertThat(JspConfig.IMPORT_CLASSES).isNotNull(); }
    @Test void test_IMPORT_METHODS() { assertThat(JspConfig.IMPORT_METHODS).isNotNull(); }
    @Test void test_IMPORT_FUNCTIONS() { assertThat(JspConfig.IMPORT_FUNCTIONS).isNotNull(); }
    @Test void test_IMPORT_TAGS() { assertThat(JspConfig.IMPORT_TAGS).isNotNull(); }
    @Test void test_IMPORT_MACROS() { assertThat(JspConfig.IMPORT_MACROS).isNotNull(); }
    @Test void test_IMPORT_DEFINES() { assertThat(JspConfig.IMPORT_DEFINES).isNotNull(); }
    @Test void test_TEMPLATE_SUFFIX() { assertThat(JspConfig.TEMPLATE_SUFFIX).isNotNull(); }
    @Test void test_INPUT_ENCODING() { assertThat(JspConfig.INPUT_ENCODING).isNotNull(); }
    @Test void test_OUTPUT_ENCODING() { assertThat(JspConfig.OUTPUT_ENCODING).isNotNull(); }
    @Test void test_TRIM_LEADING_WHITESPACES() { assertThat(JspConfig.TRIM_LEADING_WHITESPACES).isNotNull(); }
    @Test void test_TRIM_DIRECTIVE_WHITESPACES() { assertThat(JspConfig.TRIM_DIRECTIVE_WHITESPACES).isNotNull(); }
    @Test void test_TRIM_DIRECTIVE_COMMENTS() { assertThat(JspConfig.TRIM_DIRECTIVE_COMMENTS).isNotNull(); }
    @Test void test_TRIM_DIRECTIVE_COMMENTS_PREFIX() { assertThat(JspConfig.TRIM_DIRECTIVE_COMMENTS_PREFIX).isNotNull(); }
    @Test void test_TRIM_DIRECTIVE_COMMENTS_SUFFIX() { assertThat(JspConfig.TRIM_DIRECTIVE_COMMENTS_SUFFIX).isNotNull(); }
    @Test void test_IO_SKIPERRORS() { assertThat(JspConfig.IO_SKIPERRORS).isNotNull(); }
}
