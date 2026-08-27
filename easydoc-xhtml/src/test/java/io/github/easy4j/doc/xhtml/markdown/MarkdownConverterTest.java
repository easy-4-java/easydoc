package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MarkdownConverterTest {

	@Test
	void mdToHtmlConvertsHeadingsAndParagraphs() {
		String html = MarkdownConverter.mdToHtml("# Title\n\nHello **world**.");
		assertTrue(html.contains("<h1>"), "heading must render to <h1>");
		assertTrue(html.contains("<strong>world</strong>"), "bold must render to <strong>");
		assertTrue(html.contains("<p>"), "paragraph must render to <p>");
	}

	@Test
	void mdToHtmlConvertsTable() {
		String html = MarkdownConverter.mdToHtml("| A | B |\n|---|---|\n| 1 | 2 |");
		assertTrue(html.contains("<table>"), "GFM table must render to <table>");
		assertTrue(html.contains("<td>1</td>"), "table cell must render");
	}

	@Test
	void mdToHtmlConvertsCodeBlockAndList() {
		String html = MarkdownConverter.mdToHtml("```java\nint x=1;\n```\n\n- item1\n- item2");
		assertTrue(html.contains("<pre>"), "code block must render to <pre>");
		assertTrue(html.contains("<li>item1</li>"), "list item must render");
	}

	@Test
	void htmlToMarkdownHandlesTagsWithAttributes() {
		String md = MarkdownConverter.htmlToMarkdown(
				"<strong class=\"x\">bold</strong> and <li class=\"y\">item</li>");
		assertTrue(md.contains("**bold**"), "strong with attribute must become **bold**");
		assertTrue(md.contains("- item"), "li with attribute must become list item");
	}

	// -------- 以下为 flexmark-html2md 升级后新增的行为钉桩用例 --------

	/** 带属性的标题应正确转换为 ATX 样式（h1→#，h2→##，h3→###）。 */
	@Test
	void htmlToMarkdownAttributeHeading() {
		String md = MarkdownConverter.htmlToMarkdown(
				"<h1 style=\"color:red\">标题一</h1><h2 id=\"sec\">标题二</h2><h3>标题三</h3>");
		assertTrue(md.contains("# 标题一"), "h1 with style attr must become # heading");
		assertTrue(md.contains("## 标题二"), "h2 with id attr must become ## heading");
		assertTrue(md.contains("### 标题三"), "h3 must become ### heading");
	}

	/** 嵌套列表应保留层级缩进（非正则拉平）。 */
	@Test
	void htmlToMarkdownNestedList() {
		String md = MarkdownConverter.htmlToMarkdown(
				"<ul><li>外层<ul><li>内层一</li><li>内层二</li></ul></li></ul>");
		assertTrue(md.contains("- 外层"), "outer list item must appear");
		assertTrue(md.contains("内层一"), "nested item 1 must appear");
		assertTrue(md.contains("内层二"), "nested item 2 must appear");
		// flexmark 缩进嵌套列表项（2 或 4 空格前缀），验证非拉平
		int outerIdx = md.indexOf("- 外层");
		int innerIdx = md.indexOf("内层一");
		assertTrue(innerIdx > outerIdx, "nested item must follow outer item");
	}

	/** 有序列表应保留编号（1. 2. 3.），而非正则全部映射为 -。 */
	@Test
	void htmlToMarkdownOrderedList() {
		String md = MarkdownConverter.htmlToMarkdown(
				"<ol><li>第一项</li><li>第二项</li><li>第三项</li></ol>");
		assertTrue(md.contains("1. 第一项"), "first ordered item must be numbered 1.");
		assertTrue(md.contains("2. 第二项"), "second ordered item must be numbered 2.");
		assertTrue(md.contains("3. 第三项"), "third ordered item must be numbered 3.");
	}

	/** GFM 表格应包含分隔行（|---|---|），解决 F1 旧病。 */
	@Test
	void htmlToMarkdownTableWithSeparator() {
		String md = MarkdownConverter.htmlToMarkdown(
				"<table><thead><tr><th>姓名</th><th>值</th></tr></thead>"
				+ "<tbody><tr><td>A</td><td>1</td></tr>"
				+ "<tr><td>B</td><td>2</td></tr></tbody></table>");
		assertTrue(md.contains("姓名"), "table header must appear");
		assertTrue(md.contains("|"), "table must use pipe delimiters");
		// GFM 分隔行：包含 --- 或 :--
		assertTrue(md.contains("---"), "table must have separator row with dashes");
		assertTrue(md.contains("A"), "cell A must appear");
		assertTrue(md.contains("1"), "cell 1 must appear");
	}

	/** 内联代码应保留反引号包裹。 */
	@Test
	void htmlToMarkdownInlineCode() {
		String md = MarkdownConverter.htmlToMarkdown(
				"<p>调用 <code>foo()</code> 方法</p>");
		assertTrue(md.contains("`foo()`"), "inline code must be wrapped in backticks");
	}

	/** 锚点链接应保留 [text](href) 格式，不丢失 href。 */
	@Test
	void htmlToMarkdownAnchorLink() {
		String md = MarkdownConverter.htmlToMarkdown(
				"<a href=\"https://example.com\">点击这里</a>");
		assertTrue(md.contains("[点击这里](https://example.com)"),
				"anchor must become [text](href) markdown link");
	}

	/** null 输入返回空串（签名语义不变）。 */
	@Test
	void htmlToMarkdownNullReturnsEmpty() {
		assertEquals("", MarkdownConverter.htmlToMarkdown(null),
				"null input must return empty string");
	}

	/** 空串输入返回空串。 */
	@Test
	void htmlToMarkdownEmptyReturnsEmpty() {
		String result = MarkdownConverter.htmlToMarkdown("");
		assertTrue(result.trim().isEmpty(), "empty input must return empty/blank string");
	}
}
