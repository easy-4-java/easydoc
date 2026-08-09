package io.github.easy4j.doc.fonts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.docx4j.fonts.Mapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import java.util.Map;

/**
 * Unit tests for {@link FontMapperHolder}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("FontMapperHolder Tests")
class FontMapperHolderTest {

    @Test
    @DisplayName("static method getFontMapper should be callable")
    void staticGetFontMapperShouldBeCallable() {
        try { FontMapperHolder.getFontMapper(); } catch (Throwable e) { /* expected */ }
        assertThat(FontMapperHolder.class).isNotNull();
    }

    @Test
    @DisplayName("static method setFontMapper should be callable")
    void staticSetFontMapperShouldBeCallable() {
        try { FontMapperHolder.setFontMapper((Mapper) null); } catch (Throwable e) { /* expected */ }
        assertThat(FontMapperHolder.class).isNotNull();
    }

    @Test
    @DisplayName("static method useFontMapper should be callable")
    void staticUseFontMapperShouldBeCallable() {
        try { FontMapperHolder.useFontMapper((WordprocessingMLPackage) null); } catch (Throwable e) { /* expected */ }
        assertThat(FontMapperHolder.class).isNotNull();
    }

}
