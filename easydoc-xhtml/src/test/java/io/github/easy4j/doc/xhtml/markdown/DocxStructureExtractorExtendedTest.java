package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Color;
import org.docx4j.wml.CTShd;
import org.docx4j.wml.Lvl;
import org.docx4j.wml.NumberFormat;
import org.docx4j.wml.NumFmt;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.docx4j.wml.Tbl;
import org.junit.jupiter.api.Test;

/**
 * {@link DocxStructureExtractor} 扩展测试：单元格颜色抽取（fontColorHex / backgroundColorHex）
 * 以及 hex6FromString 边界场景。
 */
class DocxStructureExtractorExtendedTest {

	private static final ObjectFactory F = new ObjectFactory();

	// ==================== 构造工具 ====================

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

	private static P paragraph(String value) {
		P p = F.createP();
		p.getContent().add(run(value));
		return p;
	}

	private static Tc cellWithText(String value) {
		Tc tc = F.createTc();
		tc.getContent().add(paragraph(value));
		return tc;
	}

	/** 构造一行辅助单元格（纯文本，无样式）。 */
	private static Tr plainRow(String... texts) {
		Tr tr = F.createTr();
		for (String t : texts) {
			tr.getContent().add(cellWithText(t));
		}
		return tr;
	}

	// ==================== 单元格颜色抽取 ====================

	@Test
	void cellFontColorExtractedFromFirstRunRPr() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		Tbl tbl = F.createTbl();
		// 表头行
		tbl.getContent().add(plainRow("列甲", "列乙"));
		// 数据行：第一个单元格带 w:color w:val="FF0000"
		Tr dataRow = F.createTr();
		Tc coloredCell = F.createTc();
		R r = F.createR();
		RPr rPr = F.createRPr();
		Color color = new Color();
		color.setVal("FF0000");
		rPr.setColor(color);
		r.setRPr(rPr);
		r.getContent().add(text("红色文字"));
		P p = F.createP();
		p.getContent().add(r);
		coloredCell.getContent().add(p);
		dataRow.getContent().add(coloredCell);
		dataRow.getContent().add(cellWithText("普通"));
		tbl.getContent().add(dataRow);
		pkg.getMainDocumentPart().getContent().add(tbl);

		DocxTable table = (DocxTable) DocxStructureExtractor.extract(pkg).getElements().get(0);
		DocxCell dc = table.getRows().get(0).get(0);
		assertEquals("红色文字", dc.text());
		assertEquals("FF0000", dc.fontColorHex(),
				"explicit 6-char hex font color must be extracted and uppercased");
		assertNull(dc.backgroundColorHex());
	}

	@Test
	void cellBackgroundColorExtractedFromTcPrShd() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		Tbl tbl = F.createTbl();
		// 表头行
		tbl.getContent().add(plainRow("列甲", "列乙"));
		// 数据行：第一个单元格带 tcPr/shd/w:fill="FFFF00"
		Tr dataRow = F.createTr();
		Tc shadedCell = cellWithText("黄色底");
		TcPr tcPr = F.createTcPr();
		CTShd shd = new CTShd();
		shd.setFill("FFFF00");
		tcPr.setShd(shd);
		shadedCell.setTcPr(tcPr);
		dataRow.getContent().add(shadedCell);
		dataRow.getContent().add(cellWithText("普通"));
		tbl.getContent().add(dataRow);
		pkg.getMainDocumentPart().getContent().add(tbl);

		DocxTable table = (DocxTable) DocxStructureExtractor.extract(pkg).getElements().get(0);
		DocxCell dc = table.getRows().get(0).get(0);
		assertEquals("黄色底", dc.text());
		assertEquals("FFFF00", dc.backgroundColorHex(),
				"explicit 6-char hex background color must be extracted");
		assertNull(dc.fontColorHex());
	}

	@Test
	void themeColorReturnsNullAndNotRendered() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		Tbl tbl = F.createTbl();
		// 表头行
		tbl.getContent().add(plainRow("列甲", "列乙"));
		// 数据行：第一个单元格 run 带 theme 色（无显式 hex val）
		Tr dataRow = F.createTr();
		Tc themedCell = F.createTc();
		P p = F.createP();
		R r = F.createR();
		RPr rPr = F.createRPr();
		Color color = new Color();
		color.setThemeColor(org.docx4j.wml.STThemeColor.ACCENT_1);
		// 不设 setVal —— theme 色无显式 hex
		rPr.setColor(color);
		r.setRPr(rPr);
		r.getContent().add(text("主题色文字"));
		p.getContent().add(r);
		themedCell.getContent().add(p);
		dataRow.getContent().add(themedCell);
		dataRow.getContent().add(cellWithText("普通"));
		tbl.getContent().add(dataRow);
		pkg.getMainDocumentPart().getContent().add(tbl);

		DocxTable table = (DocxTable) DocxStructureExtractor.extract(pkg).getElements().get(0);
		DocxCell dc = table.getRows().get(0).get(0);
		assertEquals("主题色文字", dc.text());
		assertNull(dc.fontColorHex(),
				"theme-only color (no explicit hex val) must return null");
	}

	@Test
	void hexColorWithHashPrefixIsAccepted() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		Tbl tbl = F.createTbl();
		tbl.getContent().add(plainRow("列甲"));
		Tr dataRow = F.createTr();
		Tc cell = F.createTc();
		P p = F.createP();
		R r = F.createR();
		RPr rPr = F.createRPr();
		Color color = new Color();
		color.setVal("#FF0000"); // 带 # 前缀
		rPr.setColor(color);
		r.setRPr(rPr);
		r.getContent().add(text("带前缀"));
		p.getContent().add(r);
		cell.getContent().add(p);
		dataRow.getContent().add(cell);
		tbl.getContent().add(dataRow);
		pkg.getMainDocumentPart().getContent().add(tbl);

		DocxTable table = (DocxTable) DocxStructureExtractor.extract(pkg).getElements().get(0);
		DocxCell dc = table.getRows().get(0).get(0);
		assertEquals("FF0000", dc.fontColorHex(),
				"# prefix must be stripped and result uppercased");
	}

	@Test
	void nonHexColorValueReturnsNull() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		Tbl tbl = F.createTbl();
		tbl.getContent().add(plainRow("列甲"));
		Tr dataRow = F.createTr();
		Tc cell = F.createTc();
		P p = F.createP();
		R r = F.createR();
		RPr rPr = F.createRPr();
		Color color = new Color();
		color.setVal("ZZZZZZ"); // 非法 hex
		rPr.setColor(color);
		r.setRPr(rPr);
		r.getContent().add(text("非法色"));
		p.getContent().add(r);
		cell.getContent().add(p);
		dataRow.getContent().add(cell);
		tbl.getContent().add(dataRow);
		pkg.getMainDocumentPart().getContent().add(tbl);

		DocxTable table = (DocxTable) DocxStructureExtractor.extract(pkg).getElements().get(0);
		DocxCell dc = table.getRows().get(0).get(0);
		assertNull(dc.fontColorHex(), "non-hex value must return null");
	}

	@Test
	void shortHexColorValueReturnsNull() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		Tbl tbl = F.createTbl();
		tbl.getContent().add(plainRow("列甲"));
		Tr dataRow = F.createTr();
		Tc cell = F.createTc();
		P p = F.createP();
		R r = F.createR();
		RPr rPr = F.createRPr();
		Color color = new Color();
		color.setVal("FFF"); // 3 位短 hex
		rPr.setColor(color);
		r.setRPr(rPr);
		r.getContent().add(text("短色"));
		p.getContent().add(r);
		cell.getContent().add(p);
		dataRow.getContent().add(cell);
		tbl.getContent().add(dataRow);
		pkg.getMainDocumentPart().getContent().add(tbl);

		DocxTable table = (DocxTable) DocxStructureExtractor.extract(pkg).getElements().get(0);
		DocxCell dc = table.getRows().get(0).get(0);
		assertNull(dc.fontColorHex(), "3-char hex must return null (requires exactly 6)");
	}
}
