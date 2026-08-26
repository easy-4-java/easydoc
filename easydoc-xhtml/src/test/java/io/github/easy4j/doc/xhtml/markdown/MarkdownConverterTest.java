package io.github.easy4j.doc.xhtml.markdown;

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
}
