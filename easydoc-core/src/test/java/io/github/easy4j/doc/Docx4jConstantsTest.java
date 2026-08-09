package io.github.easy4j.doc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;

/**
 * Unit tests for {@link Docx4jConstants}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("Docx4jConstants Tests")
class Docx4jConstantsTest {

    @Test
    @DisplayName("constant DEFAULT_CHARSETNAME should not be null or empty")
    void constantDEFAULT_CHARSETNAMEShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DEFAULT_CHARSETNAME).isNotNull();
        assertThat(Docx4jConstants.DEFAULT_CHARSETNAME).isNotEmpty();
    }

    @Test
    @DisplayName("constant DEFAULT_TIMEOUTMILLIS should be defined")
    void constantDEFAULT_TIMEOUTMILLISShouldBeDefined() {
        assertThat(Docx4jConstants.DEFAULT_TIMEOUTMILLIS).isNotNull();
    }

    @Test
    @DisplayName("constant DOCX4J_PARAM_01 should not be null or empty")
    void constantDOCX4J_PARAM_01ShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DOCX4J_PARAM_01).isNotNull();
        assertThat(Docx4jConstants.DOCX4J_PARAM_01).isNotEmpty();
    }

    @Test
    @DisplayName("constant DOCX4J_PARAM_02 should not be null or empty")
    void constantDOCX4J_PARAM_02ShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DOCX4J_PARAM_02).isNotNull();
        assertThat(Docx4jConstants.DOCX4J_PARAM_02).isNotEmpty();
    }

    @Test
    @DisplayName("constant DOCX4J_PARAM_03 should not be null or empty")
    void constantDOCX4J_PARAM_03ShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DOCX4J_PARAM_03).isNotNull();
        assertThat(Docx4jConstants.DOCX4J_PARAM_03).isNotEmpty();
    }

    @Test
    @DisplayName("constant DOCX4J_JSOUP_PARSE_TIMEOUTMILLIS should not be null or empty")
    void constantDOCX4J_JSOUP_PARSE_TIMEOUTMILLISShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DOCX4J_JSOUP_PARSE_TIMEOUTMILLIS).isNotNull();
        assertThat(Docx4jConstants.DOCX4J_JSOUP_PARSE_TIMEOUTMILLIS).isNotEmpty();
    }

    @Test
    @DisplayName("constant DOCX4J_JSOUP_PARSE_BASEURI should not be null or empty")
    void constantDOCX4J_JSOUP_PARSE_BASEURIShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DOCX4J_JSOUP_PARSE_BASEURI).isNotNull();
        assertThat(Docx4jConstants.DOCX4J_JSOUP_PARSE_BASEURI).isNotEmpty();
    }

    @Test
    @DisplayName("constant DOCX4J_JSOUP_PARSE_CHARSETNAME should not be null or empty")
    void constantDOCX4J_JSOUP_PARSE_CHARSETNAMEShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DOCX4J_JSOUP_PARSE_CHARSETNAME).isNotNull();
        assertThat(Docx4jConstants.DOCX4J_JSOUP_PARSE_CHARSETNAME).isNotEmpty();
    }

    @Test
    @DisplayName("constant DOCX4J_PARAM_04 should not be null or empty")
    void constantDOCX4J_PARAM_04ShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DOCX4J_PARAM_04).isNotNull();
        assertThat(Docx4jConstants.DOCX4J_PARAM_04).isNotEmpty();
    }

    @Test
    @DisplayName("constant DOCX4J_CONVERT_OUT_HTML_IMAGETARGETURI should not be null or empty")
    void constantDOCX4J_CONVERT_OUT_HTML_IMAGETARGETURIShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_IMAGETARGETURI).isNotNull();
        assertThat(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_IMAGETARGETURI).isNotEmpty();
    }

    @Test
    @DisplayName("constant DOCX4J_CONVERT_OUT_HTML_CSSINCLUDEURI should not be null or empty")
    void constantDOCX4J_CONVERT_OUT_HTML_CSSINCLUDEURIShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_CSSINCLUDEURI).isNotNull();
        assertThat(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_CSSINCLUDEURI).isNotEmpty();
    }

    @Test
    @DisplayName("constant DOCX4J_CONVERT_OUT_HTML_CSSINCLUDEPATH should not be null or empty")
    void constantDOCX4J_CONVERT_OUT_HTML_CSSINCLUDEPATHShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_CSSINCLUDEPATH).isNotNull();
        assertThat(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_CSSINCLUDEPATH).isNotEmpty();
    }

    @Test
    @DisplayName("constant DOCX4J_CONVERT_OUT_WMLTEMPLATE_CHARSETNAME should not be null or empty")
    void constantDOCX4J_CONVERT_OUT_WMLTEMPLATE_CHARSETNAMEShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DOCX4J_CONVERT_OUT_WMLTEMPLATE_CHARSETNAME).isNotNull();
        assertThat(Docx4jConstants.DOCX4J_CONVERT_OUT_WMLTEMPLATE_CHARSETNAME).isNotEmpty();
    }

    @Test
    @DisplayName("constant DOCX4J_PARAM_05 should not be null or empty")
    void constantDOCX4J_PARAM_05ShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DOCX4J_PARAM_05).isNotNull();
        assertThat(Docx4jConstants.DOCX4J_PARAM_05).isNotEmpty();
    }

    @Test
    @DisplayName("constant DOCX4J_PARAM_06 should not be null or empty")
    void constantDOCX4J_PARAM_06ShouldNotBeNullOrEmpty() {
        assertThat(Docx4jConstants.DOCX4J_PARAM_06).isNotNull();
        assertThat(Docx4jConstants.DOCX4J_PARAM_06).isNotEmpty();
    }

}
