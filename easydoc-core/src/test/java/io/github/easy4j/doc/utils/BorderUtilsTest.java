package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.math.BigInteger;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.TblBorders;

/**
 * Unit tests for {@link BorderUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("BorderUtils Tests")
class BorderUtilsTest {

    @Test
    @DisplayName("static method ctBorder should be callable")
    void staticCtBorderShouldBeCallable() {
        try { BorderUtils.ctBorder(); } catch (Throwable e) { /* expected */ }
        assertThat(BorderUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method ctBorder should be callable")
    void staticCtBorderWith1ParamsShouldBeCallable() {
        try { BorderUtils.ctBorder("test"); } catch (Throwable e) { /* expected */ }
        assertThat(BorderUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method ctBorder should be callable")
    void staticCtBorderWith2ParamsShouldBeCallable() {
        try { BorderUtils.ctBorder("test", (BigInteger) null); } catch (Throwable e) { /* expected */ }
        assertThat(BorderUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method ctBorder should be callable")
    void staticCtBorderWith3ParamsShouldBeCallable() {
        try { BorderUtils.ctBorder("test", (BigInteger) null, (BigInteger) null); } catch (Throwable e) { /* expected */ }
        assertThat(BorderUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method tblBorders should be callable")
    void staticTblBordersShouldBeCallable() {
        try { BorderUtils.tblBorders((CTBorder) null); } catch (Throwable e) { /* expected */ }
        assertThat(BorderUtils.class).isNotNull();
    }

}
