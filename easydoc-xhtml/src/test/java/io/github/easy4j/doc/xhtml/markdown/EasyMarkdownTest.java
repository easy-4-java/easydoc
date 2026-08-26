package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

class EasyMarkdownTest {

	@Test
	void markdownToDocxProducesPackage() throws Exception {
		WordprocessingMLPackage pkg = EasyMarkdown.markdownToDocx(
				"# 标题\n\n这是 **加粗** 内容。\n\n- 列表一\n- 列表二");
		assertNotNull(pkg);
		String xml = pkg.getMainDocumentPart().getXML();
		assertTrue(xml.contains("标题"), "heading text must appear in the docx");
		assertTrue(xml.contains("加粗"), "bold text must appear in the docx");
	}

	@Test
	void markdownToDocxHandlesNullAndEmpty() throws Exception {
		assertNotNull(EasyMarkdown.markdownToDocx(null), "null markdown yields a package");
		assertNotNull(EasyMarkdown.markdownToDocx(""), "empty markdown yields a package");
	}

	@Test
	void docxToMarkdownConvertsHeadingAndBold() throws Exception {
		WordprocessingMLPackage pkg = EasyMarkdown.markdownToDocx("# 标题\n\n**加粗** 内容");
		String md = EasyMarkdown.docxToMarkdown(pkg);
		assertTrue(md.contains("标题"), "heading text must appear in markdown output");
		assertTrue(md.contains("加粗"), "text must appear in markdown output");
	}
}
