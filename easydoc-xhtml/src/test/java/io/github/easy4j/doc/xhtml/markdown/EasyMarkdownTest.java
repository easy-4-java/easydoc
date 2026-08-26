package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class EasyMarkdownTest {

	/**
	 * docx4j 8.3.15 的 HTML 导出会触发系统字体扫描（PhysicalFonts.discoverPhysicalFonts），
	 * 其内置 FOP 字体解析器在断言开启时对个别 macOS 系统字体的 GPOS 表抛 AssertionError
	 * （docx4j 17.x 已修复，3.0.x 不受影响）。导出 HTML 不依赖物理字体，
	 * 故按 docx4j 官方机制用 regex 白名单限制扫描范围，规避该环境性失败。
	 */
	@BeforeAll
	static void limitFontDiscovery() {
		PhysicalFonts.setRegex(".*(Courier New|Arial|Times New Roman|Georgia|Verdana|Tahoma|Trebuchet).*");
	}

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

	@Test
	void docxToMarkdownFromFile(@TempDir Path tempDir) throws Exception {
		WordprocessingMLPackage pkg = EasyMarkdown.markdownToDocx("# 标题\n\n**加粗** 内容");
		File file = tempDir.resolve("doc.docx").toFile();
		pkg.save(file);
		String md = EasyMarkdown.docxToMarkdown(file);
		assertTrue(md.contains("标题"), "heading text must appear in markdown output");
		assertTrue(md.contains("加粗"), "text must appear in markdown output");
	}

	@Test
	void docxToMarkdownFromInputStreamBytesAndPath(@TempDir Path tempDir) throws Exception {
		WordprocessingMLPackage pkg = EasyMarkdown.markdownToDocx("# 标题");
		File file = tempDir.resolve("doc2.docx").toFile();
		pkg.save(file);
		try (InputStream in = new FileInputStream(file)) {
			assertTrue(EasyMarkdown.docxToMarkdown(in).contains("标题"),
					"input stream input must produce markdown");
		}
		byte[] bytes = Files.readAllBytes(file.toPath());
		assertTrue(EasyMarkdown.docxToMarkdown(bytes).contains("标题"),
				"byte[] input must produce markdown");
		assertTrue(EasyMarkdown.docxToMarkdown(file.getAbsolutePath()).contains("标题"),
				"path input must produce markdown");
	}

	@Test
	void docxToMarkdownNullInputsReturnEmpty() throws Exception {
		assertTrue(EasyMarkdown.docxToMarkdown((File) null).isEmpty());
		assertTrue(EasyMarkdown.docxToMarkdown((InputStream) null).isEmpty());
		assertTrue(EasyMarkdown.docxToMarkdown((byte[]) null).isEmpty());
		assertTrue(EasyMarkdown.docxToMarkdown((String) null).isEmpty());
		assertTrue(EasyMarkdown.docxToMarkdown("   ").isEmpty());
	}
}
