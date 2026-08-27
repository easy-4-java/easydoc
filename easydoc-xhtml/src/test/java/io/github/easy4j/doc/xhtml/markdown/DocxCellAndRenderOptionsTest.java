package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * {@link DocxCell} 与 {@link MarkdownRenderOptions} 基础类型测试。
 */
class DocxCellAndRenderOptionsTest {

    // ==================== DocxCell ====================

    @Test
    void constructorAndGetters() {
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

	// ==================== DocxTable.ofStrings 兼容工厂 ====================

	@Test
	void ofStringsCreatesTableWithNullColors() {
		DocxTable table = DocxTable.ofStrings(
				Arrays.asList("H1", "H2"),
				Collections.singletonList(Arrays.asList("A", "B")));
		List<DocxCell> headers = table.getHeaders();
		assertEquals(2, headers.size());
		assertEquals("H1", headers.get(0).text());
		assertNull(headers.get(0).fontColorHex());
		assertNull(headers.get(0).backgroundColorHex());
		assertEquals("A", table.getRows().get(0).get(0).text());
	}

	@Test
	void ofStringsNullArgsHandled() {
		DocxTable table = DocxTable.ofStrings(null, null);
		assertEquals("", table.toMarkdown());
	}

	@Test
	void ofStringsEmptyListsHandled() {
		DocxTable table = DocxTable.ofStrings(
				Collections.<String>emptyList(),
				Collections.<List<String>>emptyList());
		assertEquals("", table.toMarkdown());
	}

	// ==================== getHeadersAsText / getRowsAsText ====================

	@Test
	void headersAsTextAndRowsAsTextReturnPureStrings() {
		DocxTable table = DocxTable.ofStrings(
				Arrays.asList("甲", "乙"),
				Arrays.asList(Arrays.asList("1", "2"), Arrays.asList("3", "4")));
		assertEquals(Arrays.asList("甲", "乙"), table.getHeadersAsText());
		assertEquals(2, table.getRowsAsText().size());
		assertEquals(Arrays.asList("1", "2"), table.getRowsAsText().get(0));
		assertEquals(Arrays.asList("3", "4"), table.getRowsAsText().get(1));
	}

	@Test
	void headersAsTextHandlesNullCells() {
		// 构造含 null 元素的 DocxCell 列表
		List<DocxCell> cells = Arrays.asList(
				new DocxCell("ok", null, null), null);
		DocxTable table = new DocxTable(cells, null);
		List<String> texts = table.getHeadersAsText();
		assertEquals("ok", texts.get(0));
		assertEquals("", texts.get(1));
	}

	@Test
	void rowsAsTextReturnsNullWhenRowsNull() {
		DocxTable table = new DocxTable(
				Collections.singletonList(new DocxCell("h", null, null)), null);
		assertNull(table.getRowsAsText());
	}

	// ==================== hex6FromString 边界 ====================

	@Test
	void docxCellStoresColorsAsPassed() {
		// DocxCell 是纯值对象，不做归一化（归一化由 extractor 的 hex6FromString 负责）
		assertNull(DocxCell.EMPTY.fontColorHex());
		DocxCell cell = new DocxCell("x", "ABCDEF", "123456");
		assertEquals("ABCDEF", cell.fontColorHex());
		assertEquals("123456", cell.backgroundColorHex());
		assertTrue(cell.hasStyle());
	}

	// ==================== toMarkdown(MarkdownRenderOptions) ====================

	@Test
	void toMarkdownWithExplicitOptsMatchesDefault() {
		DocxTable table = DocxTable.ofStrings(
				Collections.singletonList("H"),
				Collections.singletonList(Collections.singletonList("V")));
		String defaultMd = table.toMarkdown();
		String explicitOff = table.toMarkdown(MarkdownRenderOptions.of(false));
		assertEquals(defaultMd, explicitOff, "explicit OFF must match default");
	}

	@Test
	void toMarkdownNullOptsTreatedAsDefault() {
		DocxTable table = DocxTable.ofStrings(
				Collections.singletonList("H"),
				Collections.singletonList(Collections.singletonList("V")));
		assertEquals(table.toMarkdown(), table.toMarkdown(null));
	}

	// ==================== 颜色渲染（renderHtmlColor=true） ====================

	@Test
	void renderHtmlColorFontColorOnly() {
		List<DocxCell> headers = Collections.singletonList(new DocxCell("H", null, null));
		List<List<DocxCell>> rows = Collections.singletonList(
				Collections.singletonList(new DocxCell("bold", "FF0000", null)));
		DocxTable table = new DocxTable(headers, rows);
		String md = table.toMarkdown(MarkdownRenderOptions.of(true));
		assertTrue(md.contains("<span style=\"color:#FF0000;\">bold</span>"),
				"font color must render as span, got: " + md);
	}

	@Test
	void renderHtmlColorBackgroundColorOnly() {
		List<DocxCell> headers = Collections.singletonList(new DocxCell("H", null, null));
		List<List<DocxCell>> rows = Collections.singletonList(
				Collections.singletonList(new DocxCell("bg", null, "FFFF00")));
		DocxTable table = new DocxTable(headers, rows);
		String md = table.toMarkdown(MarkdownRenderOptions.of(true));
		assertTrue(md.contains("<span style=\"background-color:#FFFF00;\">bg</span>"),
				"background color must render as span, got: " + md);
	}

	@Test
	void renderHtmlColorBothColors() {
		List<DocxCell> headers = Collections.singletonList(new DocxCell("H", null, null));
		List<List<DocxCell>> rows = Collections.singletonList(
				Collections.singletonList(new DocxCell("both", "FF0000", "FFFF00")));
		DocxTable table = new DocxTable(headers, rows);
		String md = table.toMarkdown(MarkdownRenderOptions.of(true));
		assertTrue(md.contains("<span style=\"color:#FF0000;background-color:#FFFF00;\">both</span>"),
				"both colors must render in order, got: " + md);
	}

	@Test
	void renderHtmlColorOffDoesNotEmitSpan() {
		List<DocxCell> headers = Collections.singletonList(new DocxCell("H", null, null));
		List<List<DocxCell>> rows = Collections.singletonList(
				Collections.singletonList(new DocxCell("plain", "FF0000", null)));
		DocxTable table = new DocxTable(headers, rows);
		String md = table.toMarkdown(MarkdownRenderOptions.of(false));
		assertFalse(md.contains("<span"), "OFF must not emit span, got: " + md);
		assertTrue(md.contains("plain"));
	}

	@Test
	void renderHtmlColorEscapesSpecialCharsInsideSpan() {
		List<DocxCell> headers = Collections.singletonList(new DocxCell("H", null, null));
		List<List<DocxCell>> rows = Collections.singletonList(
				Collections.singletonList(new DocxCell("<script>", "FF0000", null)));
		DocxTable table = new DocxTable(headers, rows);
		String md = table.toMarkdown(MarkdownRenderOptions.of(true));
		assertTrue(md.contains("\\<script\\>"), "HTML chars must be backslash-escaped inside span, got: " + md);
		assertTrue(md.contains("<span"), "span must still wrap the escaped text");
	}

	@Test
	void renderHtmlColorNoStyleCellOmittedFromSpan() {
		List<DocxCell> headers = Collections.singletonList(new DocxCell("H", null, null));
		List<List<DocxCell>> rows = Collections.singletonList(
				Collections.singletonList(new DocxCell("no-style", null, null)));
		DocxTable table = new DocxTable(headers, rows);
		String md = table.toMarkdown(MarkdownRenderOptions.of(true));
		assertFalse(md.contains("<span"), "cell without style must not be wrapped, got: " + md);
		assertTrue(md.contains("no-style"));
	}

	@Test
	void defaultOffOutputMatchesPreviousBehavior() {
		// 用 ofStrings 构造的表，toMarkdown() 与旧版本行为一致
		DocxTable table = DocxTable.ofStrings(
				Arrays.asList("A", "B"),
				Arrays.asList(Arrays.asList("1", "2"), Arrays.asList("3", "4")));
		assertEquals("| A | B |\n|---|---|\n| 1 | 2 |\n| 3 | 4 |", table.toMarkdown());
		assertEquals(table.toMarkdown(), table.toMarkdown(MarkdownRenderOptions.DEFAULT));
	}

	// ==================== equals / hashCode / toString ====================

	@Test
	void docxCellEqualsHashCodeAndToString() {
		DocxCell a = new DocxCell("x", "FF0000", null);
		DocxCell b = new DocxCell("x", "FF0000", null);
		DocxCell c = new DocxCell("y", "FF0000", null);
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertFalse(a.equals(c));
		assertTrue(a.toString().contains("DocxCell"));
		assertTrue(a.toString().contains("FF0000"));
	}
}
