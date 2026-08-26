package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Arrays;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Text;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * EasyMarkdown 结构化门面（docxToStructured / docxToStructuredMarkdown）端到端测试。
 *
 * <p>经验钉死（2026-08-27 探针验证）：{@code EasyMarkdown.markdownToDocx} 对
 * "# 标题" 产出 pStyle=Heading1 段落、对 "**加粗**" 产出 w:b run、对 "- 列表X"
 * 产出带 numPr 的段落（无 numbering part → 按约定降级无序），故结构化路径可
 * 逐字还原原 Markdown。（Task 2 集成用例中"不产出 Heading1"的旧结论已过时。）</p>
 */
class EasyMarkdownStructuredTest {

	private static final ObjectFactory F = new ObjectFactory();

	private static final String MARKDOWN = "# 标题\n\n**加粗** 内容\n\n- 列表一\n- 列表二";

	// ==================== 构造工具（同 DocxStructureExtractorTest 的模式） ====================

	private static Text text(String value) {
		Text t = F.createText();
		t.setValue(value);
		return t;
	}

	private static R run(String value) {
		R r = F.createR();
		r.getContent().add(text(value));
		return r;
	}

	private static R boldRun(String value) {
		R r = F.createR();
		RPr rPr = F.createRPr();
		rPr.setB(new org.docx4j.wml.BooleanDefaultTrue());
		rPr.getB().setVal(true);
		r.setRPr(rPr);
		r.getContent().add(text(value));
		return r;
	}

	private static void style(P p, String styleVal) {
		PPr pPr = p.getPPr() == null ? F.createPPr() : p.getPPr();
		PPrBase.PStyle pStyle = new PPrBase.PStyle();
		pStyle.setVal(styleVal);
		pPr.setPStyle(pStyle);
		p.setPPr(pPr);
	}

	private static void numPr(P p, int numId, int ilvl) {
		PPr pPr = p.getPPr() == null ? F.createPPr() : p.getPPr();
		PPrBase.NumPr np = new PPrBase.NumPr();
		PPrBase.NumPr.Ilvl lvl = new PPrBase.NumPr.Ilvl();
		lvl.setVal(BigInteger.valueOf(ilvl));
		np.setIlvl(lvl);
		PPrBase.NumPr.NumId nid = new PPrBase.NumPr.NumId();
		nid.setVal(BigInteger.valueOf(numId));
		np.setNumId(nid);
		pPr.setNumPr(np);
		p.setPPr(pPr);
	}

	/** markdownToDocx 产包 → 落盘临时文件。 */
	private static File saveTempDocx(Path tempDir) throws Exception {
		WordprocessingMLPackage pkg = EasyMarkdown.markdownToDocx(MARKDOWN);
		File file = tempDir.resolve("structured-roundtrip.docx").toFile();
		pkg.save(file);
		return file;
	}

	// ==================== 端到端：markdownToDocx → 结构化还原 ====================

	@Test
	void structuredMarkdownRoundTripFromFile(@TempDir Path tempDir) throws Exception {
		String md = EasyMarkdown.docxToStructuredMarkdown(saveTempDocx(tempDir));
		assertEquals(MARKDOWN, md, "structured path must round-trip heading/bold/list verbatim");
		assertTrue(md.contains("# 标题"));
		assertTrue(md.contains("**加粗**"));
		assertTrue(md.contains("- 列表一"));
	}

	@Test
	void structuredFromInputStreamOwnsStreamAndPkgOverloadMatches(@TempDir Path tempDir) throws Exception {
		File file = saveTempDocx(tempDir);

		InputStream in = new FileInputStream(file);
		String mdFromStream = EasyMarkdown.docxToStructuredMarkdown(in);
		assertEquals(MARKDOWN, mdFromStream);
		assertThrows(IOException.class, () -> in.read(),
				"facade must not double-wrap: extractor owns and closes the caller's stream");

		InputStream docStream = new FileInputStream(file);
		DocxDocument docFromStream = EasyMarkdown.docxToStructured(docStream);
		assertEquals("标题", ((DocxHeading) docFromStream.getElements().get(0)).getText(),
				"document-tree stream overload must parse the same content");
		assertThrows(IOException.class, () -> docStream.read(),
				"docxToStructured(InputStream) also delegates stream ownership to the extractor");

		String mdFromPkg = EasyMarkdown.docxToStructuredMarkdown(EasyMarkdown.markdownToDocx(MARKDOWN));
		assertEquals(MARKDOWN, mdFromPkg, "package overload must match file/stream fidelity");
	}

	@Test
	void structuredDocumentTreeExposesHeadingBoldAndList() throws Exception {
		WordprocessingMLPackage pkg = EasyMarkdown.markdownToDocx(MARKDOWN);
		DocxDocument doc = EasyMarkdown.docxToStructured(pkg);
		assertNotNull(doc);
		assertTrue(doc.getElements().size() >= 3, "heading + paragraph + list expected");

		DocxHeading heading = (DocxHeading) doc.getElements().get(0);
		assertEquals(1, heading.getLevel());
		assertEquals("标题", heading.getText());

		DocxParagraph paragraph = (DocxParagraph) doc.getElements().get(1);
		assertTrue(paragraph.getSpans().get(0).isBold(), "w:b run must surface as bold span");
		assertEquals("加粗", paragraph.getSpans().get(0).getText());

		DocxList list = (DocxList) doc.getElements().get(2);
		assertFalse(list.isOrdered(), "numPr without numbering part degrades to bullets");
		assertEquals(Arrays.asList("列表一", "列表二"), list.getItems());
	}

	// ==================== 构造式对照：与 HTML 导入器行为解耦 ====================

	/**
	 * 直接构造 OOXML（构造模式同 {@link DocxStructureExtractorTest}），钉死门面在语义
	 * 明确输入上的行为，不受 HTML 导入器实现漂移影响：三个来源（文件/流/包）结果一致，
	 * DocxDocument 树含 level-1 标题等 ≥3 个元素。
	 */
	@Test
	void structuredOverloadsOnProgrammaticallyBuiltPackage(@TempDir Path tempDir) throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		P heading = F.createP();
		style(heading, "Heading1");
		heading.getContent().add(run("标题"));

		P para = F.createP();
		para.getContent().add(boldRun("加粗"));
		para.getContent().add(run(" 内容"));

		P itemA = F.createP();
		itemA.getContent().add(run("列表一"));
		numPr(itemA, 9, 0);
		P itemB = F.createP();
		itemB.getContent().add(run("列表二"));
		numPr(itemB, 9, 0);

		pkg.getMainDocumentPart().getContents().getBody().getContent()
				.addAll(Arrays.asList(heading, para, itemA, itemB));

		File file = tempDir.resolve("programmatic.docx").toFile();
		pkg.save(file);

		assertEquals(MARKDOWN, EasyMarkdown.docxToStructuredMarkdown(file), "file overload");
		try (InputStream in = new FileInputStream(file)) {
			assertEquals(MARKDOWN, EasyMarkdown.docxToStructuredMarkdown(in), "stream overload");
		}
		assertEquals(MARKDOWN, EasyMarkdown.docxToStructuredMarkdown(pkg), "package overload");

		DocxDocument fromFile = EasyMarkdown.docxToStructured(file);
		assertTrue(fromFile.getElements().size() >= 3);
		DocxHeading first = (DocxHeading) fromFile.getElements().get(0);
		assertEquals(1, first.getLevel());
		assertEquals("标题", first.getText());
		assertTrue(fromFile.fullMarkdown().contains("**加粗**"));
	}

	// ==================== null 输入快速失败 ====================

	/**
	 * 结构化路径的 null 策略有意与宽松的旧 docxToMarkdown 系列（null 返回空串）不同：
	 * 统一 requireNonNull 快速失败（NPE），错误尽早暴露。
	 */
	@Test
	void nullInputsFailFastWithNpe() {
		assertThrows(NullPointerException.class,
				() -> EasyMarkdown.docxToStructuredMarkdown((File) null));
		assertThrows(NullPointerException.class,
				() -> EasyMarkdown.docxToStructuredMarkdown((InputStream) null));
		assertThrows(NullPointerException.class,
				() -> EasyMarkdown.docxToStructuredMarkdown((WordprocessingMLPackage) null));
		assertThrows(NullPointerException.class,
				() -> EasyMarkdown.docxToStructured((File) null));
		assertThrows(NullPointerException.class,
				() -> EasyMarkdown.docxToStructured((InputStream) null));
		assertThrows(NullPointerException.class,
				() -> EasyMarkdown.docxToStructured((WordprocessingMLPackage) null));
	}
}
