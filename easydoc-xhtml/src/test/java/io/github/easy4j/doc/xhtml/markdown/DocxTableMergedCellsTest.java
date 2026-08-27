package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.junit.jupiter.api.Test;

/**
 * 合并单元格与锯齿行回归（audit2 F7）：gridSpan 横向展开为重复格；vMerge 延续行以
 * 空串占位；渲染端所有行按表头列数归一化（不足补空、超出截断），GFM 表格列数恒定。
 */
class DocxTableMergedCellsTest {

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

	private static Tc cellWithText(String value) {
		Tc tc = F.createTc();
		tc.getContent().add(paragraph(value));
		return tc;
	}

	private static Tc cellWithSpan(String value, int gridSpan) {
		Tc tc = cellWithText(value);
		TcPr pr = F.createTcPr();
		org.docx4j.wml.TcPrInner.GridSpan gs = F.createTcPrInnerGridSpan();
		gs.setVal(BigInteger.valueOf(gridSpan));
		pr.setGridSpan(gs);
		tc.setTcPr(pr);
		return tc;
	}

	private static Tc restartCell(String value) {
		Tc tc = cellWithText(value);
		TcPr pr = F.createTcPr();
		org.docx4j.wml.TcPrInner.VMerge vm = F.createTcPrInnerVMerge();
		vm.setVal("restart");
		pr.setVMerge(vm);
		tc.setTcPr(pr);
		return tc;
	}

	/** OOXML 常见形态：延续格省略 val（缺省即 continue）。 */
	private static Tc continueCellImplicit() {
		Tc tc = cellWithText("");
		TcPr pr = F.createTcPr();
		pr.setVMerge(F.createTcPrInnerVMerge());
		tc.setTcPr(pr);
		return tc;
	}

	/** val="continue" 显式延续。 */
	private static Tc continueCellExplicit(String staleText) {
		Tc tc = cellWithText(staleText);
		TcPr pr = F.createTcPr();
		org.docx4j.wml.TcPrInner.VMerge vm = F.createTcPrInnerVMerge();
		vm.setVal("continue");
		pr.setVMerge(vm);
		tc.setTcPr(pr);
		return tc;
	}

	private static Tr row(Tc... cells) {
		Tr tr = F.createTr();
		for (Tc tc : cells) {
			tr.getContent().add(tc);
		}
		return tr;
	}

	private static WordprocessingMLPackage pkgWithTable(Tbl tbl) {
		WordprocessingMLPackage pkg = new org.docx4j.openpackaging.packages.WordprocessingMLPackage();
		try {
			pkg = WordprocessingMLPackage.createPackage();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		pkg.getMainDocumentPart().getContent().add(tbl);
		return pkg;
	}

	private static DocxTable extractSingleTable(WordprocessingMLPackage pkg) {
		return (DocxTable) DocxStructureExtractor.extract(pkg).getElements().get(0);
	}

	// ==================== 抽取端：gridSpan / vMerge ====================

	@Test
	void gridSpanExpandsToRepeatedCellsKeepingColumnCount() {
		Tbl tbl = F.createTbl();
		tbl.getContent().add(row(cellWithText("列甲"), cellWithText("列乙")));
		tbl.getContent().add(row(cellWithSpan("横跨两列", 2), cellWithText("越界普通格")));

		DocxTable table = extractSingleTable(pkgWithTable(tbl));
		assertEquals(Arrays.asList("列甲", "列乙"), table.getHeaders());
		assertEquals(Arrays.asList("横跨两列", "横跨两列", "越界普通格"),
				table.getRows().get(0),
				"gridSpan=2 expands to two repeated grid cells so POJO keeps grid truth");
		// 渲染端以表头列数为基准：超过表头宽度的物理格截断（归一化契约）
		assertEquals("| 列甲 | 列乙 |\n|---|---|\n| 横跨两列 | 横跨两列 |",
				table.toMarkdown());
	}

	@Test
	void vMergeContinuationBecomesEmptyPlaceholder() {
		Tbl tbl = F.createTbl();
		tbl.getContent().add(row(cellWithText("类目"), cellWithText("明细")));
		tbl.getContent().add(row(restartCell("水果"), cellWithText("苹果")));
		tbl.getContent().add(row(continueCellImplicit(), cellWithText("香蕉")));

		DocxTable table = extractSingleTable(pkgWithTable(tbl));
		assertEquals(2, table.getHeaders().size());
		assertEquals(2, table.getRows().size());
		assertEquals(Arrays.asList("水果", "苹果"), table.getRows().get(0), "restart row keeps content");
		assertEquals(Arrays.asList("", "香蕉"), table.getRows().get(1),
				"vMerge continuation (val omitted => continue) collapses to empty placeholder");
		String md = table.toMarkdown();
		String[] lines = md.split("\n");
		int expectedPipes = lines[0].length() - lines[0].replace("|", "").length();
		for (String line : lines) {
			int pipes = line.length() - line.replace("|", "").length();
			assertEquals(expectedPipes, pipes,
					"every rendered line keeps the header column count: " + md);
		}
	}

	@Test
	void explicitContinueValAndOrphanContinuationHandledDefensively() {
		Tbl tbl = F.createTbl();
		tbl.getContent().add(row(cellWithText("甲"), cellWithText("乙")));
		tbl.getContent().add(row(continueCellExplicit("孤儿内容"), cellWithText("b1")));
		tbl.getContent().add(row(restartCell("重启"), cellWithText("b2")));
		tbl.getContent().add(row(continueCellExplicit("陈旧残留"), cellWithText("b3")));

		DocxTable table = extractSingleTable(pkgWithTable(tbl));
		// 孤立延续（无前置 restart）按普通内容保留，不误吞
		assertEquals(Arrays.asList("孤儿内容", "b1"), table.getRows().get(0));
		assertEquals(Arrays.asList("重启", "b2"), table.getRows().get(1));
		// 有前置 restart 后，显式 val="continue" 归并为空占位
		assertEquals(Arrays.asList("", "b3"), table.getRows().get(2));
	}

	@Test
	void plainCellClosesOpenVerticalMergeChain() {
		Tbl tbl = F.createTbl();
		tbl.getContent().add(row(cellWithText("甲"), cellWithText("乙")));
		tbl.getContent().add(row(restartCell("合并开始"), cellWithText("b1")));
		tbl.getContent().add(row(continueCellImplicit(), cellWithText("b2")));
		tbl.getContent().add(row(cellWithText("独立格"), cellWithText("b3")));

		DocxTable table = extractSingleTable(pkgWithTable(tbl));
		assertEquals(Arrays.asList("", "b2"), table.getRows().get(1));
		assertEquals(Arrays.asList("独立格", "b3"), table.getRows().get(2),
				"plain cell ends the merge chain; no phantom empties afterwards");
	}

	// ==================== 渲染端：行归一化 ====================

	@Test
	void rendererPadsShortRowsAndTruncatesLongRows() {
		DocxTable table = new DocxTable(
				Arrays.asList("一", "二", "三"),
				Arrays.asList(
						Arrays.asList("短"),
						Arrays.asList("刚", "好", "齐"),
						Arrays.asList("超", "出", "被", "截", "断")));
		assertEquals("| 一 | 二 | 三 |\n"
				+ "|---|---|---|\n"
				+ "| 短 |  |  |\n"
				+ "| 刚 | 好 | 齐 |\n"
				+ "| 超 | 出 | 被 |", table.toMarkdown());
	}

	@Test
	void rendererNormalizesNullRowAndPreservesEscapingDuringPadding() {
		java.util.List<java.util.List<String>> rows =
				new java.util.ArrayList<java.util.List<String>>();
		rows.add(null);
		rows.add(Collections.singletonList("含|竖线"));
		String md = new DocxTable(Collections.singletonList("表头"), rows).toMarkdown();
		assertEquals("| 表头 |\n|---|\n|  |\n| 含\\|竖线 |", md);
	}

	@Test
	void fullPipelineTableWithBothMergeKindsStaysRectangular() {
		Tbl tbl = F.createTbl();
		tbl.getContent().add(row(cellWithSpan("宽表头", 2), cellWithText("备注")));
		tbl.getContent().add(row(cellWithText("a1"), restartCell("纵合")));
		tbl.getContent().add(row(cellWithText("a2"), continueCellImplicit()));

		String md = extractSingleTable(pkgWithTable(tbl)).toMarkdown();
		String[] lines = md.split("\n");
		assertEquals(4, lines.length, "header + separator + two data rows");
		int headerCols = lines[0].split("\\|", -1).length;
		assertEquals(lines[1].split("\\|", -1).length, headerCols, "separator aligns with header");
		assertEquals(lines[2].split("\\|", -1).length, headerCols, "data row aligns with header");
		assertTrue(md.contains("| a2 | | |") || md.contains("| a2 |  |  |"),
				"continuation renders as empty placeholder inside rectangular grid: " + md);
	}
}
