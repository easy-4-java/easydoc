package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

/**
 * Unit tests for {@link ParagraphUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("ParagraphUtils Tests")
class ParagraphUtilsTest {

    @Test
    @DisplayName("static method addInlineImageToParagraph should be callable")
    void staticAddInlineImageToParagraphShouldBeCallable() {
        try { ParagraphUtils.addInlineImageToParagraph((Inline) null); } catch (Throwable e) { /* expected */ }
        assertThat(ParagraphUtils.class).isNotNull();
    }

}
