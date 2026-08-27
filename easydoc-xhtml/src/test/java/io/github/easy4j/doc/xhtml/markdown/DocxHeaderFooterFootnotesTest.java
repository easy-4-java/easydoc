package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.junit.jupiter.api.Test;

/**
 * 页眉 / 页脚 / 脚注内容抽取回归（audit 22 选项 a）：辅助部件复用正文解析，
 * 按 页眉 → 页脚 → 脚注 顺序追加到文档模型末尾；分隔线脚注跳过；部件缺失不产出元素。
 */
class DocxHeaderFooterFootnotesTest {

	private static final ObjectFactory F = new ObjectFactory();

	// ==================== 构造工具 ====================

	private static P paragraph(String value) {
		P p = F.createP();
		R r = F.createR();
		Text t = F.createText();
		t.setValue(value);
		r.getContent().add(t);
		p.getContent().add(r);
		return p;
	}

	private static org.docx4j.wml.Tbl singleCellTable(String value) {
		org.docx4j.wml.Tc tc = F.createTc();
		tc.getContent().add(paragraph(value));
		org.docx4j.wml.Tr tr = F.createTr();
		tr.getContent().add(tc);
		org.docx4j.wml.Tbl tbl = F.createTbl();
		tbl.getContent().add(tr);
		return tbl;
	}

	private static FootnotesPart footnotesPartWith(String normalText, String separatorText)
			throws Exception {
		org.docx4j.wml.CTFootnotes footnotes = new org.docx4j.wml.CTFootnotes();

		org.docx4j.wml.CTFtnEdn separator = new org.docx4j.wml.CTFtnEdn();
		separator.setId(BigInteger.ZERO);
		separator.setType(org.docx4j.wml.STFtnEdn.SEPARATOR);
		separator.getContent().add(paragraph(separatorText));
		footnotes.getFootnote().add(separator);

		org.docx4j.wml.CTFtnEdn normalNote = new org.docx4j.wml.CTFtnEdn();
		normalNote.setId(BigInteger.ONE); // 无 type 属性 => normal
		normalNote.getContent().add(paragraph(normalText));
		footnotes.getFootnote().add(normalNote);

		FootnotesPart part = new FootnotesPart();
		part.setJaxbElement(footnotes);
		return part;
	}

	// ==================== 回归 ====================

	@Test
	void headerFooterTableAndFootnoteAppendAfterBody() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		pkg.getMainDocumentPart().getContent().add(paragraph("正文首段"));

		HeaderPart header = new HeaderPart();
		header.getContent().add(paragraph("机密文件 页眉"));
		pkg.getMainDocumentPart().addTargetPart(header);

		FooterPart footer = new FooterPart();
		footer.getContent().add(singleCellTable("公司署名"));
		pkg.getMainDocumentPart().addTargetPart(footer);

		pkg.getMainDocumentPart().addTargetPart(
				footnotesPartWith("参见 2026 年度白皮书", "───"));

		DocxDocument doc = DocxStructureExtractor.extract(pkg);

		assertEquals("paragraph", doc.getElements().get(0).getElementType(),
				"body content stays first");
		assertEquals("正文首段", ((DocxParagraph) doc.getElements().get(0))
				.getSpans().get(0).getText());

		String md = doc.fullMarkdown();
		assertTrue(md.contains("机密文件 页眉"), "header paragraph must be appended: " + md);
		assertTrue(md.contains("| 公司署名 |"), "footer table must render as GFM table: " + md);
		assertTrue(md.contains("参见 2026 年度白皮书"), "normal footnote must be included: " + md);
		assertFalse(md.contains("───"), "separator footnote must be skipped");

		int bodyIndex = elementIndexContaining(doc, "正文首段");
		int headerIndex = elementIndexContaining(doc, "机密文件 页眉");
		int footerIndex = elementIndexContaining(doc, "公司署名");
		int noteIndex = elementIndexContaining(doc, "参见 2026 年度白皮书");
		assertTrue(bodyIndex == 0, "body content stays first: " + doc.getElements());
		assertTrue(headerIndex > bodyIndex, "header after body");
		assertTrue(footerIndex > headerIndex, "footer after header");
		assertTrue(noteIndex > footerIndex, "footnote after footer");
	}

	private static int elementIndexContaining(DocxDocument doc, String needle) {
		for (int i = 0; i < doc.getElements().size(); i++) {
			if (doc.getElements().get(i).toMarkdown().contains(needle)) {
				return i;
			}
		}
		return -1;
	}

	@Test
	void packagesWithoutAncillaryPartsYieldBodyOnly() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		pkg.getMainDocumentPart().getContent().add(paragraph("纯净正文"));

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertEquals(1, doc.getElements().size());
		assertEquals("纯净正文", doc.fullMarkdown());
	}

	@Test
	void emptyHeaderContentAddsNothing() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		pkg.getMainDocumentPart().getContent().add(paragraph("唯一段落"));

		HeaderPart empty = new HeaderPart();
		pkg.getMainDocumentPart().addTargetPart(empty);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertEquals(1, doc.getElements().size(), "empty header must not add elements");
		assertEquals("唯一段落", doc.fullMarkdown());
	}

	@Test
	void multipleHeadersProcessInPartNameOrder() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();

		// 默认构造器固定为 /word/header.xml，多部件需显式命名避免相互覆盖
		HeaderPart first = new HeaderPart(
				new org.docx4j.openpackaging.parts.PartName("/word/header1.xml"));
		first.getContent().add(paragraph("页眉甲"));
		pkg.getMainDocumentPart().addTargetPart(first);
		HeaderPart second = new HeaderPart(
				new org.docx4j.openpackaging.parts.PartName("/word/header2.xml"));
		second.getContent().add(paragraph("页眉乙"));
		pkg.getMainDocumentPart().addTargetPart(second);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		StringBuilder joined = new StringBuilder();
		for (int i = 0; i < doc.getElements().size(); i++) {
			joined.append(doc.getElements().get(i).toMarkdown()).append('\n');
		}
		assertTrue(joined.indexOf("页眉甲") < joined.indexOf("页眉乙"),
				"headers append in stable part-name order: " + joined);
	}

	/** 正文列表 run 与页眉段落之间不得串桶：部件边界处 flush 列表状态。 */
	@Test
	void listRunDoesNotBleedAcrossPartBoundaries() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		P item = paragraph("正文列表项");
		org.docx4j.wml.PPr pPr = F.createPPr();
		org.docx4j.wml.PPrBase.NumPr numPr = new org.docx4j.wml.PPrBase.NumPr();
		org.docx4j.wml.PPrBase.NumPr.NumId numId =
				new org.docx4j.wml.PPrBase.NumPr.NumId();
		numId.setVal(BigInteger.valueOf(9));
		numPr.setNumId(numId);
		pPr.setNumPr(numPr);
		item.setPPr(pPr);
		pkg.getMainDocumentPart().getContent().add(item);

		HeaderPart header = new HeaderPart();
		header.getContent().add(paragraph("页尾独立文字"));
		pkg.getMainDocumentPart().addTargetPart(header);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertTrue(doc.getElements().get(0) instanceof DocxList,
				"body list still lands as list element");
		assertTrue(doc.getElements().get(1) instanceof DocxParagraph,
				"header paragraph is not swallowed into the body list run");
	}
}
