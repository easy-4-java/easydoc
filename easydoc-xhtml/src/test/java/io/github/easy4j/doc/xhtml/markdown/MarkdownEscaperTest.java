package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

/**
 * Markdown 转义层回归（audit2 F2）：DOCX 正文中的 Markdown 结构字符与 URL 目标
 * 必须按站点逐一转义 —— 行内片段、标题、表格单元格、图片 alt 与 src。
 */
class MarkdownEscaperTest {

	// ==================== escapeText ====================

	@Test
	void escapesEmphasisLinkAndHeadingCharacters() {
		assertEquals("\\*重点\\*", MarkdownEscaper.escapeText("*重点*"));
		assertEquals("\\_下划\\_强调\\_", MarkdownEscaper.escapeText("_下划_强调_"));
		assertEquals("\\`code\\`", MarkdownEscaper.escapeText("`code`"));
		assertEquals("见 \\[附录\\] 一节", MarkdownEscaper.escapeText("见 [附录] 一节"));
		assertEquals("\\# 井号开头", MarkdownEscaper.escapeText("# 井号开头"));
		assertEquals("感叹 \\! 配图", MarkdownEscaper.escapeText("感叹 ! 配图"));
	}

	@Test
	void escapesGfmTablePipeStrikethroughAndAngleBrackets() {
		assertEquals("a \\| b", MarkdownEscaper.escapeText("a | b"), "pipe must not split GFM table cells");
		assertEquals("\\~删除线\\~ → \\~删除线\\~", MarkdownEscaper.escapeText("~删除线~ → ~删除线~"),
				"tilde must not trigger GFM strikethrough");
		assertEquals("\\<script\\>alert(1)\\</script\\>", MarkdownEscaper.escapeText("<script>alert(1)</script>"),
				"raw HTML must be neutralized");
	}

	@Test
	void backslashIsEscapedFirstAndStaysLiteral() {
		assertEquals("C:\\\\path\\\\\\*", MarkdownEscaper.escapeText("C:\\path\\*"));
	}

	@Test
	void disarmsBlockStartSequencesAtBeginningOnly() {
		assertEquals("\\- 不是列表项", MarkdownEscaper.escapeText("- 不是列表项"));
		assertEquals("\\+ 不是待办", MarkdownEscaper.escapeText("+ 不是待办"));
		assertEquals("\\> 不是引用", MarkdownEscaper.escapeText("> 不是引用"));
		assertEquals("1\\. 不是序号列表", MarkdownEscaper.escapeText("1. 不是序号列表"));
		assertEquals("12\\) 括号序号同样中和", MarkdownEscaper.escapeText("12) 括号序号同样中和"));
		assertEquals("中部 - 加粗", MarkdownEscaper.escapeText("中部 - 加粗"),
				"non-leading markers must stay unescaped for readability");
	}

	@Test
	void escapeTextPassesThroughNullEmptyAndPlainText() {
		assertNull(MarkdownEscaper.escapeText(null));
		assertEquals("", MarkdownEscaper.escapeText(""));
		assertEquals("普通中文文本 123", MarkdownEscaper.escapeText("普通中文文本 123"));
	}

	// ==================== collapseLineBreaks ====================

	@Test
	void collapsesLineBreaksTabsAndRunsToSingleSpaces() {
		assertNull(MarkdownEscaper.collapseLineBreaks(null), "null passes through untouched");
		assertEquals("", MarkdownEscaper.collapseLineBreaks(""));
		assertEquals("", MarkdownEscaper.collapseLineBreaks("\n\r\t "), "blank input collapses to empty");
		assertEquals("a b c", MarkdownEscaper.collapseLineBreaks("a\n\rb\t\tc"));
		assertEquals("首行 次行", MarkdownEscaper.collapseLineBreaks("首行\n次行"));
	}

	@Test
	void leadingLineBreakIsDroppedByCollapse() {
		assertEquals("标题正文", MarkdownEscaper.collapseLineBreaks("\n标题正文"),
				"line break as first child of a heading must vanish, not leave a stray space");
		assertEquals("尾随也清理", MarkdownEscaper.collapseLineBreaks("尾随也清理   "));
	}

	// ==================== escapeUrl ====================

	@Test
	void plainUrlsAndDataUrisStayUntouched() {
		assertEquals("https://example.com/page?x=1#锚", MarkdownEscaper.escapeUrl(
				"https://example.com/page?x=1#锚"),
				"clean URLs must pass through without encoding noise");
	}

	@Test
	void urlsWithWhitespaceOrParenthesesArePercentEncoded() {
		assertEquals("my%20file.pdf", MarkdownEscaper.escapeUrl("my file.pdf"));
		assertEquals("%28括号%29", MarkdownEscaper.escapeUrl("(括号)"));
		assertEquals("https://example.com/a%281%29.png", MarkdownEscaper.escapeUrl(
				"https://example.com/a(1).png"));
		assertEquals("行%0A尾", MarkdownEscaper.escapeUrl("行\n尾"));
		assertEquals("%3C自动链接%3E",
				MarkdownEscaper.escapeUrl("<自动链接>"), "angle brackets encoded too");
	}

	@Test
	void escapeUrlHandlesNullEmptyAndDataUriBase64() {
		assertNull(MarkdownEscaper.escapeUrl(null));
		assertEquals("", MarkdownEscaper.escapeUrl(""));
		String dataUri = "data:image/png;base64,iVBORw0KGgo+AA/8=";
		assertEquals(dataUri, MarkdownEscaper.escapeUrl(dataUri),
				"base64 chars (+ / =) must never be percent-encoded");
	}

	// ==================== 站点回归：行内片段 / 标题 / 单元格 / 图片 ====================

	@Test
	void inlineSpanEscapesStructuralCharsBeforeWrapping() {
		assertEquals("\\*星号\\*", new InlineSpan("*星号*").toMarkdown());
		assertEquals("**\\*加粗的星号\\***",
				new InlineSpan("*加粗的星号*", true, false, false).toMarkdown(),
				"escaping applies to raw text only, not to the emphasis markers themselves");
		assertEquals("[\\[标签\\]](https://example.com/x%20y.json)",
				new InlineSpan("[标签]", "https://example.com/x y.json").toMarkdown());
	}

	@Test
	void headingCollapsesBreaksAndEscapesMarkup() {
		assertEquals("# \\#\\# 标题陷阱", new DocxHeading(1, "## 标题陷阱").toMarkdown());
		assertEquals("## 折叠 成单行", new DocxHeading(2, "折叠\n成单行").toMarkdown(),
				"embedded line break becomes one space inside ATX output");
		assertEquals("### 开头换行被丢弃", new DocxHeading(3, "\n开头换行被丢弃").toMarkdown());
		assertEquals("### [带\\[框\\]](https://example.com)",
				new DocxHeading(3, "带[框]", "https://example.com").toMarkdown());
	}

	@Test
	void extractorNormalizesHeadingTextWithTrailingBreak() throws Exception {
		org.docx4j.wml.ObjectFactory F = new org.docx4j.wml.ObjectFactory();
		org.docx4j.openpackaging.packages.WordprocessingMLPackage pkg =
				org.docx4j.openpackaging.packages.WordprocessingMLPackage.createPackage();
		org.docx4j.wml.P p = F.createP();
		org.docx4j.wml.PPr pPr = F.createPPr();
		org.docx4j.wml.PPrBase.PStyle style = new org.docx4j.wml.PPrBase.PStyle();
		style.setVal("Heading1");
		pPr.setPStyle(style);
		p.setPPr(pPr);
		// 首个子元素即 Br（w:br），其后接文字：折叠后标题文本不含前导空格
		p.getContent().add(new org.docx4j.wml.Br());
		org.docx4j.wml.R r = F.createR();
		org.docx4j.wml.Text t = F.createText();
		t.setValue("真正的标题");
		r.getContent().add(t);
		p.getContent().add(r);
		pkg.getMainDocumentPart().getContent().add(p);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		DocxHeading heading = (DocxHeading) doc.getElements().get(0);
		assertEquals("真正的标题", heading.getText(), "leading w:br is stripped from stored heading text");
		assertEquals("# 真正的标题", doc.fullMarkdown());
	}

	@Test
	void tableCellEscapesPipesAndEmphasis() {
		DocxTable table = DocxTable.ofStrings(Collections.singletonList("表头"),
				Collections.singletonList(Collections.singletonList("**加粗**|正文")));
		assertEquals("| 表头 |\n|---|\n| \\*\\*加粗\\*\\*\\|正文 |", table.toMarkdown());
	}

	@Test
	void imageEscapesAltTextAndKeepsDataUriIntact() {
		DocxImage image = new DocxImage("data:image/png;base64,AAA=", "架构] 图 *v2*", "image/png");
		assertEquals("![架构\\] 图 \\*v2\\*](data:image/png;base64,AAA=)", image.toMarkdown());
		assertEquals("![](my%20pic.png)", new DocxImage("my pic.png", null, null).toMarkdown());
	}

	@Test
	void documentWithHostileTextStillRendersLegalCommonMark() {
		DocxDocument doc = new DocxDocument(null, null, null, Arrays.<DocxElement>asList(
				new DocxParagraph(Arrays.asList(new InlineSpan("值 <b> ", true, false, false))),
				new DocxList(false, 0, Collections.singletonList("- 假列表"))));
		String md = doc.fullMarkdown();
		assertEquals("**值 \\<b\\> **\n\n- \\- 假列表", md);
	}
}
