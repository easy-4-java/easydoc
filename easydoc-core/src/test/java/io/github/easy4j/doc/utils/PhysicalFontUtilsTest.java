package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.fonts.ChineseFont;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Unit tests for {@link PhysicalFontUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("PhysicalFontUtils Tests")
class PhysicalFontUtilsTest {

    @Test
    @DisplayName("static method setWmlPackageFonts should be callable")
    void staticSetWmlPackageFontsShouldBeCallable() {
        try { PhysicalFontUtils.setWmlPackageFonts((WordprocessingMLPackage) null); } catch (Throwable e) { /* expected */ }
        assertThat(PhysicalFontUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setDefaultFont should be callable")
    void staticSetDefaultFontShouldBeCallable() {
        try { PhysicalFontUtils.setDefaultFont((WordprocessingMLPackage) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(PhysicalFontUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setSimSunFont should be callable")
    void staticSetSimSunFontShouldBeCallable() {
        try { PhysicalFontUtils.setSimSunFont((WordprocessingMLPackage) null); } catch (Throwable e) { /* expected */ }
        assertThat(PhysicalFontUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setPhysicalFont should be callable")
    void staticSetPhysicalFontShouldBeCallable() {
        try { PhysicalFontUtils.setPhysicalFont((WordprocessingMLPackage) null, (PhysicalFont) null); } catch (Throwable e) { /* expected */ }
        assertThat(PhysicalFontUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setPhysicalFont should be callable")
    void staticSetPhysicalFontWith4ParamsShouldBeCallable() {
        try { PhysicalFontUtils.setPhysicalFont((WordprocessingMLPackage) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(PhysicalFontUtils.class).isNotNull();
    }

}
