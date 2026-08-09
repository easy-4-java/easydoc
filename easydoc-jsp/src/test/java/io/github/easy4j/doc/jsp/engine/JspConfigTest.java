package io.github.easy4j.doc.jsp.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import io.github.easy4j.doc.utils.StringUtils;

/**
 * Unit tests for {@link JspConfig}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("JspConfig Tests")
class JspConfigTest {

    @Test
    @DisplayName("constant DEFAULT_CONFIG_FILE should not be null or empty")
    void constantDEFAULT_CONFIG_FILEShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.DEFAULT_CONFIG_FILE).isNotNull();
        assertThat(JspConfig.DEFAULT_CONFIG_FILE).isNotEmpty();
    }

    @Test
    @DisplayName("constant IMPORT_CLASSES should not be null or empty")
    void constantIMPORT_CLASSESShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.IMPORT_CLASSES).isNotNull();
        assertThat(JspConfig.IMPORT_CLASSES).isNotEmpty();
    }

    @Test
    @DisplayName("constant IMPORT_METHODS should not be null or empty")
    void constantIMPORT_METHODSShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.IMPORT_METHODS).isNotNull();
        assertThat(JspConfig.IMPORT_METHODS).isNotEmpty();
    }

    @Test
    @DisplayName("constant IMPORT_FUNCTIONS should not be null or empty")
    void constantIMPORT_FUNCTIONSShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.IMPORT_FUNCTIONS).isNotNull();
        assertThat(JspConfig.IMPORT_FUNCTIONS).isNotEmpty();
    }

    @Test
    @DisplayName("constant IMPORT_TAGS should not be null or empty")
    void constantIMPORT_TAGSShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.IMPORT_TAGS).isNotNull();
        assertThat(JspConfig.IMPORT_TAGS).isNotEmpty();
    }

    @Test
    @DisplayName("constant IMPORT_MACROS should not be null or empty")
    void constantIMPORT_MACROSShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.IMPORT_MACROS).isNotNull();
        assertThat(JspConfig.IMPORT_MACROS).isNotEmpty();
    }

    @Test
    @DisplayName("constant IMPORT_DEFINES should not be null or empty")
    void constantIMPORT_DEFINESShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.IMPORT_DEFINES).isNotNull();
        assertThat(JspConfig.IMPORT_DEFINES).isNotEmpty();
    }

    @Test
    @DisplayName("constant TEMPLATE_SUFFIX should not be null or empty")
    void constantTEMPLATE_SUFFIXShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.TEMPLATE_SUFFIX).isNotNull();
        assertThat(JspConfig.TEMPLATE_SUFFIX).isNotEmpty();
    }

    @Test
    @DisplayName("constant INPUT_ENCODING should not be null or empty")
    void constantINPUT_ENCODINGShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.INPUT_ENCODING).isNotNull();
        assertThat(JspConfig.INPUT_ENCODING).isNotEmpty();
    }

    @Test
    @DisplayName("constant OUTPUT_ENCODING should not be null or empty")
    void constantOUTPUT_ENCODINGShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.OUTPUT_ENCODING).isNotNull();
        assertThat(JspConfig.OUTPUT_ENCODING).isNotEmpty();
    }

    @Test
    @DisplayName("constant TRIM_LEADING_WHITESPACES should not be null or empty")
    void constantTRIM_LEADING_WHITESPACESShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.TRIM_LEADING_WHITESPACES).isNotNull();
        assertThat(JspConfig.TRIM_LEADING_WHITESPACES).isNotEmpty();
    }

    @Test
    @DisplayName("constant TRIM_DIRECTIVE_WHITESPACES should not be null or empty")
    void constantTRIM_DIRECTIVE_WHITESPACESShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.TRIM_DIRECTIVE_WHITESPACES).isNotNull();
        assertThat(JspConfig.TRIM_DIRECTIVE_WHITESPACES).isNotEmpty();
    }

    @Test
    @DisplayName("constant TRIM_DIRECTIVE_COMMENTS should not be null or empty")
    void constantTRIM_DIRECTIVE_COMMENTSShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.TRIM_DIRECTIVE_COMMENTS).isNotNull();
        assertThat(JspConfig.TRIM_DIRECTIVE_COMMENTS).isNotEmpty();
    }

    @Test
    @DisplayName("constant TRIM_DIRECTIVE_COMMENTS_PREFIX should not be null or empty")
    void constantTRIM_DIRECTIVE_COMMENTS_PREFIXShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.TRIM_DIRECTIVE_COMMENTS_PREFIX).isNotNull();
        assertThat(JspConfig.TRIM_DIRECTIVE_COMMENTS_PREFIX).isNotEmpty();
    }

    @Test
    @DisplayName("constant TRIM_DIRECTIVE_COMMENTS_SUFFIX should not be null or empty")
    void constantTRIM_DIRECTIVE_COMMENTS_SUFFIXShouldNotBeNullOrEmpty() {
        assertThat(JspConfig.TRIM_DIRECTIVE_COMMENTS_SUFFIX).isNotNull();
        assertThat(JspConfig.TRIM_DIRECTIVE_COMMENTS_SUFFIX).isNotEmpty();
    }

}
