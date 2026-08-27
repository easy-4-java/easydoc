package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class DocxDocumentTest {

	private static String hashes(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append('#');
		}
		return sb.toString();
	}

	@Test
	void headingLevelZeroRendersDocumentTitle() {
		assertEquals("# 文档标题", new DocxHeading(0, "文档标题").toMarkdown());
	}

	@Test
	void headingLevelsOneToSixRenderMatchingHashes() {
		for (int level = 1; level <= 6; level++) {
			assertEquals(hashes(level) + " 层级" + level, new DocxHeading(level, "层级" + level).toMarkdown(),
					"heading level " + level + " must render " + level + " hashes");
		}
	}

	@Test
	void headingOutOfRangeLevelsAreClampedToValidHashes() {
		assertEquals("# 负层级", new DocxHeading(-3, "负层级").toMarkdown());
		// CommonMark ATX 标题仅支持 1-6 级，Heading 7-9 等更深层级钳为 6 个井号
		assertEquals(hashes(6) + " 七级样式", new DocxHeading(7, "七级样式").toMarkdown());
		assertEquals(hashes(6) + " 九级样式", new DocxHeading(9, "九级样式").toMarkdown());
		assertEquals(hashes(6) + " 深层级", new DocxHeading(12, "深层级").toMarkdown());
	}

	@Test
	void headingWithHyperlinkWrapsTextAsLink() {
		assertEquals("## [标题](https://example.com)",
				new DocxHeading(2, "标题", "https://example.com").toMarkdown());
	}

	@Test
	void headingWithBlankHyperlinkRendersPlainText() {
		assertEquals("# 标题", new DocxHeading(1, "标题", "").toMarkdown());
	}

	@Test
	void headingGettersAndToStringExposeState() {
		DocxHeading heading = new DocxHeading(3, "标题");
		assertEquals("heading", heading.getElementType());
		assertEquals(3, heading.getLevel());
		assertEquals("标题", heading.getText());
		assertNull(heading.getHyperlinkUrl(), "convenience constructor leaves hyperlink null");
		assertTrue(heading.toString().contains("text='标题'"), "toString must expose fields");
		assertTrue(heading.toString().contains("hyperlinkUrl=null"), "toString must expose null hyperlink");
	}

	@Test
	void inlineSpanAppliesItalicBoldUnderlineInOrder() {
		assertEquals("普通", new InlineSpan("普通").toMarkdown());
		assertEquals("*斜体*", new InlineSpan("斜体", false, true, false).toMarkdown());
		assertEquals("**加粗**", new InlineSpan("加粗", true, false, false).toMarkdown());
		assertEquals("<u>下划线</u>", new InlineSpan("下划线", false, false, true).toMarkdown());
		assertEquals("<u>***全***</u>", new InlineSpan("全", true, true, true).toMarkdown());
	}

	@Test
	void inlineSpanWithHyperlinkRendersLinkSyntax() {
		assertEquals("[文本](https://example.com)", new InlineSpan("文本", "https://example.com").toMarkdown());
		assertEquals("[**加粗**](https://example.com)",
				new InlineSpan("加粗", true, false, false, "https://example.com").toMarkdown());
	}

	@Test
	void inlineSpanNullAndEmptyTextRenderEmptyString() {
		assertEquals("", new InlineSpan(null, true, false, false, "https://example.com").toMarkdown());
		assertEquals("", new InlineSpan("", "https://example.com").toMarkdown());
	}

	@Test
	void inlineSpanGettersAndToStringExposeState() {
		InlineSpan span = new InlineSpan("文本", true, true, true, "https://example.com");
		assertEquals("文本", span.getText());
		assertTrue(span.isBold());
		assertTrue(span.isItalic());
		assertTrue(span.isUnderline());
		assertEquals("https://example.com", span.getHyperlinkUrl());
		assertTrue(span.toString().contains("hyperlinkUrl=https://example.com"), "toString must expose link");

		InlineSpan plain = new InlineSpan("纯文本");
		assertNull(plain.getHyperlinkUrl());
		assertFalse(plain.isBold(), "single-arg constructor disables styling");
		assertFalse(plain.isItalic(), "single-arg constructor disables styling");
		assertFalse(plain.isUnderline(), "single-arg constructor disables styling");
	}

	@Test
	void paragraphConcatenatesSpansInOrder() {
		DocxParagraph paragraph = new DocxParagraph(Arrays.asList(
				new InlineSpan("前缀 "),
				new InlineSpan("加粗", true, false, false),
				new InlineSpan(" 与 "),
				new InlineSpan("链接", "https://example.com")));
		assertEquals("前缀 **加粗** 与 [链接](https://example.com)", paragraph.toMarkdown());
	}

	@Test
	void paragraphSkipsNullSpansAndHandlesNullList() {
		List<InlineSpan> spans = new ArrayList<InlineSpan>();
		spans.add(null);
		spans.add(new InlineSpan("内容"));
		spans.add(null);
		assertEquals("内容", new DocxParagraph(spans).toMarkdown());
		assertEquals("", new DocxParagraph((List<InlineSpan>) null).toMarkdown());
	}

	@Test
	void paragraphEmptyAndBlankContentReturnEmptyString() {
		assertEquals("", new DocxParagraph(Collections.<InlineSpan>emptyList()).toMarkdown());
	}

	@Test
	void paragraphGetterToStringAndElementType() {
		DocxParagraph paragraph = new DocxParagraph(Collections.singletonList(new InlineSpan("甲")));
		assertEquals("paragraph", paragraph.getElementType());
		assertEquals(1, paragraph.getSpans().size());
		assertTrue(paragraph.toString().contains("spans="), "toString must expose spans");
	}

	@Test
	void unorderedListRendersDashItems() {
		assertEquals("- 首项\n- 次项",
				new DocxList(false, 0, Arrays.asList("首项", "次项")).toMarkdown());
	}

	@Test
	void orderedListNumbersItemsFromOnePerCall() {
		DocxList list = new DocxList(true, 0, Arrays.asList("甲", "乙"));
		assertEquals("1. 甲\n2. 乙", list.toMarkdown());
		assertEquals("1. 甲\n2. 乙", list.toMarkdown(), "numbering must restart at 1 on every render");
	}

	@Test
	void nestedIndentAddsTwoSpacesPerLevel() {
		assertEquals("  - 一级嵌套", new DocxList(false, 1, Collections.singletonList("一级嵌套")).toMarkdown());
		assertEquals("    1. 二级嵌套", new DocxList(true, 2, Collections.singletonList("二级嵌套")).toMarkdown());
	}

	@Test
	void richItemsTakePrecedenceOverPlainTextItems() {
		List<List<InlineSpan>> rich = Collections.singletonList(
				Collections.<InlineSpan>singletonList(new InlineSpan("富文本", true, false, false)));
		assertEquals("- **富文本**",
				new DocxList(false, 0, Collections.singletonList("纯文本"), rich).toMarkdown());

		// 富文本项为空列表时回退到同下标纯文本项
		List<List<InlineSpan>> emptyRich = Collections.<List<InlineSpan>>singletonList(
				Collections.<InlineSpan>emptyList());
		assertEquals("- 纯文本",
				new DocxList(false, 0, Collections.singletonList("纯文本"), emptyRich).toMarkdown());
	}

	@Test
	void listMergesItemAndRichItemIndexesByMaxLength() {
		List<String> items = Arrays.asList("双来源", "只有纯文本");
		List<List<InlineSpan>> rich = new ArrayList<List<InlineSpan>>();
		rich.add(Collections.<InlineSpan>singletonList(new InlineSpan("富文本优先")));
		rich.add(null); // 第二项富文本缺失，回退同下标纯文本
		assertEquals("- 富文本优先\n- 只有纯文本", new DocxList(false, 0, items, rich).toMarkdown());
	}

	@Test
	void listWithoutAnyItemsReturnsEmptyString() {
		assertEquals("", new DocxList(true, 0, null).toMarkdown());
		assertEquals("", new DocxList(false, 0, Collections.<String>emptyList()).toMarkdown());
		assertEquals("", new DocxList(false, 0, null,
				Collections.<List<InlineSpan>>emptyList()).toMarkdown());
	}

	@Test
	void listGettersToStringAndElementType() {
		DocxList list = new DocxList(true, 1, Collections.singletonList("项"),
				Collections.<List<InlineSpan>>emptyList());
		assertEquals("list", list.getElementType());
		assertTrue(list.isOrdered());
		assertEquals(1, list.getIndent());
		assertEquals(Collections.singletonList("项"), list.getItems());
		assertNotNull(list.getRichItems());
		assertTrue(list.toString().contains("ordered=true"), "toString must expose ordered flag");
	}

	@Test
	void tableRendersStandardGfmFormat() {
		DocxTable table = DocxTable.ofStrings(Arrays.asList("列一", "列二"),
				Arrays.asList(Arrays.asList("甲", "乙"), Arrays.asList("丙", "丁")));
		assertEquals("| 列一 | 列二 |\n|---|---|\n| 甲 | 乙 |\n| 丙 | 丁 |", table.toMarkdown());
	}

	@Test
	void tableWithoutHeadersSynthesizesHeaderFromFirstRowKeepingItAsDataRow() {
		DocxTable table = DocxTable.ofStrings(Collections.<String>emptyList(),
				Arrays.asList(Arrays.asList("首行甲", "首行乙"), Arrays.asList("次行", "补充")));
		assertEquals("| 首行甲 | 首行乙 |\n|---|---|\n| 首行甲 | 首行乙 |\n| 次行 | 补充 |",
				table.toMarkdown());
	}

	@Test
	void tableEscapesLiteralPipeInCellText() {
		DocxTable table = DocxTable.ofStrings(Collections.singletonList("表头"),
				Collections.singletonList(Collections.singletonList("含|竖线")));
		assertEquals("| 表头 |\n|---|\n| 含\\|竖线 |", table.toMarkdown());
	}

	@Test
	void tableWithOnlyHeadersStillEmitsSeparatorLine() {
		assertEquals("| 表头 |\n|---|",
				DocxTable.ofStrings(Collections.singletonList("表头"), Collections.<List<String>>emptyList()).toMarkdown());
	}

	@Test
	void tableTreatsNullRowsAndCellsGracefully() {
		assertEquals("", DocxTable.ofStrings(null, null).toMarkdown());
		assertEquals("", DocxTable.ofStrings(Collections.<String>emptyList(),
				Collections.<List<String>>emptyList()).toMarkdown());
		// null 数据行与 null 单元格不抛异常，按空单元格渲染
		List<List<String>> rows = new ArrayList<List<String>>();
		rows.add(null);
		rows.add(Arrays.asList((String) null));
		String md = DocxTable.ofStrings(Collections.singletonList("表头"), rows).toMarkdown();
		assertNotNull(md);
		assertTrue(md.startsWith("| 表头 |\n|---|\n"), "header block must stay intact");
	}

	@Test
	void tableGettersToStringAndElementType() {
		DocxTable table = DocxTable.ofStrings(Arrays.asList("列一", "列二"),
				Collections.singletonList(Arrays.asList("甲", "乙")));
		assertEquals("table", table.getElementType());
		assertEquals(2, table.getHeaders().size());
		assertEquals(1, table.getRows().size());
		assertTrue(table.toString().contains("headers="), "toString must expose headers");
	}

	@Test
	void imageRendersAltAndSrc() {
		assertEquals("![架构图](data:image/png;base64,iVBORw0KGgo=)",
				new DocxImage("data:image/png;base64,iVBORw0KGgo=", "架构图", "image/png").toMarkdown());
	}

	@Test
	void imageNullFieldsRenderEmptyBrackets() {
		assertEquals("![]()", new DocxImage(null, null, null).toMarkdown());
	}

	@Test
	void imageGettersToStringAndElementType() {
		DocxImage image = new DocxImage("data:image/jpeg;base64,/9j/", "照片", "image/jpeg");
		assertEquals("image", image.getElementType());
		assertEquals("data:image/jpeg;base64,/9j/", image.getSrc());
		assertEquals("照片", image.getAlt());
		assertEquals("image/jpeg", image.getMime());
		assertTrue(image.toString().contains("mime='image/jpeg'"), "toString must expose mime");
	}

	@Test
	void elementSubclassesExposeTypeTagsForLogging() {
		assertEquals("heading", new DocxHeading(1, "t").getElementType());
		assertEquals("paragraph", new DocxParagraph(null).getElementType());
		assertEquals("list", new DocxList(false, 0, null).getElementType());
		assertEquals("table", DocxTable.ofStrings(null, null).getElementType());
		assertEquals("image", new DocxImage(null, null, null).getElementType());
	}

	@Test
	void fullMarkdownJoinsBlocksWithBlankLinesInDocumentOrder() {
		DocxDocument document = new DocxDocument("年度报告", "张三", Instant.parse("2026-08-26T00:00:00Z"),
				Arrays.<DocxElement>asList(
						new DocxHeading(1, "总览"),
						new DocxParagraph(Arrays.asList(new InlineSpan("这是 "), new InlineSpan("正文", true, false, false))),
						new DocxParagraph(Collections.<InlineSpan>emptyList()),
						new DocxList(false, 0, Arrays.asList("要点一", "要点二")),
						DocxTable.ofStrings(Arrays.asList("指标", "数值"), Collections.singletonList(Arrays.asList("营收", "100"))),
						new DocxImage("data:image/png;base64,AAAA", "配图", "image/png")));
		assertEquals("# 总览\n\n"
				+ "这是 **正文**\n\n"
				+ "- 要点一\n- 要点二\n\n"
				+ "| 指标 | 数值 |\n|---|---|\n| 营收 | 100 |\n\n"
				+ "![配图](data:image/png;base64,AAAA)", document.fullMarkdown());
	}

	@Test
	void fullMarkdownSkipsNullElementsAndBlankBlocks() {
		List<DocxElement> elements = new ArrayList<DocxElement>();
		elements.add(new DocxHeading(2, "章节"));
		elements.add(null);
		elements.add(new DocxParagraph(null));
		elements.add(new DocxParagraph(Collections.<InlineSpan>emptyList()));
		elements.add(new DocxHeading(3, "小节"));
		assertEquals("## 章节\n\n### 小节",
				new DocxDocument("报告", null, null, elements).fullMarkdown());
	}

	@Test
	void fullMarkdownOnEmptyOrNullElementsReturnsEmptyString() {
		assertEquals("", new DocxDocument("空文档", null, null,
				Collections.<DocxElement>emptyList()).fullMarkdown());
		assertEquals("", new DocxDocument("空文档", null, null, null).fullMarkdown());
	}

	@Test
	void documentMetadataGettersAndToStringRoundTrip() {
		Instant modified = Instant.parse("2026-08-26T10:30:00Z");
		List<DocxElement> elements = Collections.<DocxElement>singletonList(new DocxHeading(1, "标题"));
		DocxDocument document = new DocxDocument("年度报告", "李四", modified, elements);
		assertEquals("年度报告", document.getTitle());
		assertEquals("李四", document.getAuthor());
		assertEquals(modified, document.getModified());
		assertEquals(elements, document.getElements());
		assertTrue(document.toString().contains("title='年度报告'"), "toString must expose title");
		assertTrue(document.toString().contains("author='李四'"), "toString must expose author");
		assertTrue(document.toString().contains("modified=" + modified), "toString must expose modified instant");
	}
}
