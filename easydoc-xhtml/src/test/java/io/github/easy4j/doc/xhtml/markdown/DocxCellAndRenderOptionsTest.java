package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * {@link DocxCell} 与 {@link MarkdownRenderOptions} 基础类型测试。
 */
class DocxCellAndRenderOptionsTest {

    // ==================== DocxCell ====================

    @Test
    void recordConstructorAndGetters() {
        DocxCell cell = new DocxCell("hello", "FF0000", "FFFF00");
        assertEquals("hello", cell.text());
        assertEquals("FF0000", cell.fontColorHex());
        assertEquals("FFFF00", cell.backgroundColorHex());
    }

    @Test
    void hasStyleReturnsTrueWhenFontColorPresent() {
        DocxCell cell = new DocxCell("x", "FF0000", null);
        assertTrue(cell.hasStyle());
    }

    @Test
    void hasStyleReturnsTrueWhenBackgroundColorPresent() {
        DocxCell cell = new DocxCell("x", null, "FFFF00");
        assertTrue(cell.hasStyle());
    }

    @Test
    void hasStyleReturnsTrueWhenBothColorsPresent() {
        DocxCell cell = new DocxCell("x", "FF0000", "FFFF00");
        assertTrue(cell.hasStyle());
    }

    @Test
    void hasStyleReturnsFalseWhenNoColors() {
        DocxCell cell = new DocxCell("plain", null, null);
        assertFalse(cell.hasStyle());
    }

    @Test
    void emptyConstantHasNoStyle() {
        assertNotNull(DocxCell.EMPTY);
        assertEquals("", DocxCell.EMPTY.text());
        assertNull(DocxCell.EMPTY.fontColorHex());
        assertNull(DocxCell.EMPTY.backgroundColorHex());
        assertFalse(DocxCell.EMPTY.hasStyle());
    }

    // ==================== MarkdownRenderOptions ====================

    @Test
    void defaultRenderHtmlColorIsFalse() {
        assertFalse(MarkdownRenderOptions.DEFAULT.renderHtmlColor(),
                "DEFAULT must have renderHtmlColor=false for pure GFM output");
    }

    @Test
    void ofTrueReturnsEnabledInstance() {
        MarkdownRenderOptions opts = MarkdownRenderOptions.of(true);
        assertTrue(opts.renderHtmlColor());
    }

    @Test
    void ofFalseReturnsDisabledInstance() {
        MarkdownRenderOptions opts = MarkdownRenderOptions.of(false);
        assertFalse(opts.renderHtmlColor());
    }
}
