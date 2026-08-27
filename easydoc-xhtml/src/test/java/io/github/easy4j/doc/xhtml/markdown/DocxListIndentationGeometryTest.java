package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.Lvl;
import org.docx4j.wml.NumFmt;
import org.docx4j.wml.NumberFormat;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.junit.jupiter.api.Test;

/**
 * 列表嵌套缩进几何回归（audit2 F1）：CommonMark 要求子块缩进列数 ≥ 父项内容起始列，
 * 即“父级最宽标记宽度”（{@code "- "} 占 2 列、{@code "1. "} 占 3 列、{@code "10. "} 占 4 列）。
 *
 * <p>两级钉死：抽取产物逐字断言（含精确空格数）；另以 flexmark（真实 CommonMark+GFM 解析器，
 * Babelmark 等价物）把同几何的源码渲染为 HTML，验证确为嵌套列表而非懒续行。</p>
 */
class DocxListIndentationGeometryTest {

	private static final ObjectFactory F = new ObjectFactory();

	// ==================== 构造工具 ====================

	private static P numberedParagraph(String value, int numId, int ilvl) {
		P p = F.createP();
		R r = F.createR();
		Text t = F.createText();
		t.setValue(value);
		r.getContent().add(t);
		p.getContent().add(r);
		PPr pPr = F.createPPr();
		PPrBase.NumPr np = new PPrBase.NumPr();
		PPrBase.NumPr.Ilvl lvl = new PPrBase.NumPr.Ilvl();
		lvl.setVal(BigInteger.valueOf(ilvl));
		np.setIlvl(lvl);
		PPrBase.NumPr.NumId nid = new PPrBase.NumPr.NumId();
		nid.setVal(BigInteger.valueOf(numId));
		np.setNumId(nid);
		pPr.setNumPr(np);
		p.setPPr(pPr);
		return p;
	}

	/** 按 ilvl→numFmt 定义编号：abstractNum 固定 id=1，num 给定。 */
	private static NumberingDefinitionsPart numbering(int numId, Map<Integer, NumberFormat> levelFormats)
			throws Exception {
		NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
		org.docx4j.wml.Numbering numbering = new org.docx4j.wml.Numbering();

		org.docx4j.wml.Numbering.AbstractNum abs = new org.docx4j.wml.Numbering.AbstractNum();
		abs.setAbstractNumId(BigInteger.ONE);
		for (Map.Entry<Integer, NumberFormat> entry : levelFormats.entrySet()) {
			Lvl lvl = new Lvl();
			lvl.setIlvl(BigInteger.valueOf(entry.getKey()));
			NumFmt fmt = new NumFmt();
			fmt.setVal(entry.getValue());
			lvl.setNumFmt(fmt);
			abs.getLvl().add(lvl);
		}
		numbering.getAbstractNum().add(abs);

		org.docx4j.wml.Numbering.Num num = new org.docx4j.wml.Numbering.Num();
		num.setNumId(BigInteger.valueOf(numId));
		org.docx4j.wml.Numbering.Num.AbstractNumId absId =
				new org.docx4j.wml.Numbering.Num.AbstractNumId();
		absId.setVal(BigInteger.ONE);
		num.setAbstractNumId(absId);
		numbering.getNum().add(num);

		ndp.setJaxbElement(numbering);
		return ndp;
	}

	private static WordprocessingMLPackage pkgWith(NumberingDefinitionsPart ndp, P... paragraphs)
			throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		if (ndp != null) {
			pkg.getMainDocumentPart().addTargetPart(ndp);
		}
		for (P p : paragraphs) {
			pkg.getMainDocumentPart().getContent().add(p);
		}
		return pkg;
	}	private static List<String> markdownsOf(DocxDocument doc) {
		List<String> parts = new ArrayList<String>();
		for (DocxElement element : doc.getElements()) {
			parts.add(element.toMarkdown());
		}
		return parts;
	}

	private static String stripWhitespace(String html) {
		return html.replaceAll("\\s+", "");
	}

	private static void assertNestedHtml(String markdownSource, String expectedFragment) {
		String stripped = stripWhitespace(MarkdownConverter.mdToHtml(markdownSource));
		assertTrue(stripped.contains(expectedFragment),
				"flexmark must confirm nested-list geometry.\nsource: " + markdownSource
						+ "\nhtml: " + stripped);
	}

	// ==================== 金标样例 ====================

	@Test
	void threeLevelBulletsIndentTwoColumnsPerLevel() throws Exception {
		Map<Integer, NumberFormat> formats = new LinkedHashMap<Integer, NumberFormat>();
		formats.put(0, NumberFormat.BULLET);
		formats.put(1, NumberFormat.BULLET);
		formats.put(2, NumberFormat.BULLET);
		WordprocessingMLPackage pkg = pkgWith(numbering(30, formats),
				numberedParagraph("甲", 30, 0),
				numberedParagraph("乙", 30, 1),
				numberedParagraph("丙", 30, 2));

		List<String> parts = markdownsOf(DocxStructureExtractor.extract(pkg));
		assertEquals("- 甲", parts.get(0), "level 0 bullet has no indentation");
		assertEquals("  - 乙", parts.get(1), "level 1 bullet nests at parent marker column 2");
		assertEquals("    - 丙", parts.get(2), "level 2 bullet nests at cumulative column 4");

		assertNestedHtml("- 甲\n  - 乙\n    - 丙",
				"<li>甲<ul><li>乙<ul><li>丙</li>");
	}

	@Test
	void orderedUnderOrderedUsesParentMarkerWidth() throws Exception {
		Map<Integer, NumberFormat> formats = new LinkedHashMap<Integer, NumberFormat>();
		formats.put(0, NumberFormat.DECIMAL);
		formats.put(1, NumberFormat.LOWER_LETTER);
		formats.put(2, NumberFormat.LOWER_ROMAN);
		WordprocessingMLPackage pkg = pkgWith(numbering(31, formats),
				numberedParagraph("一层", 31, 0),
				numberedParagraph("二层", 31, 1),
				numberedParagraph("三层", 31, 2));

		List<String> parts = markdownsOf(DocxStructureExtractor.extract(pkg));
		assertEquals("1. 一层", parts.get(0));
		assertEquals("   1. 二层", parts.get(1), "ordered child needs >= 3 columns");
		assertEquals("      1. 三层", parts.get(2), "cumulative width of two 3-column ancestors");

		assertNestedHtml("1. 一层\n   1. 二层\n      1. 三层",
				"<li>一层<ol><li>二层<ol><li>三层</li>");
	}

	@Test
	void bulletUnderOrderedIndentsToOrderedMarkerColumn() throws Exception {
		Map<Integer, NumberFormat> formats = new LinkedHashMap<Integer, NumberFormat>();
		formats.put(0, NumberFormat.DECIMAL);
		formats.put(1, NumberFormat.BULLET);
		WordprocessingMLPackage pkg = pkgWith(numbering(32, formats),
				numberedParagraph("父项", 32, 0),
				numberedParagraph("子项", 32, 1));

		List<String> parts = markdownsOf(DocxStructureExtractor.extract(pkg));
		assertEquals("1. 父项", parts.get(0));
		assertEquals("   - 子项", parts.get(1), "bullet child indents by ordered parent width 3");

		assertNestedHtml("1. 父项\n   - 子项",
				"<li>父项<ul><li>子项</li>");
	}

	@Test
	void orderedUnderBulletIndentsToBulletMarkerColumn() throws Exception {
		Map<Integer, NumberFormat> formats = new LinkedHashMap<Integer, NumberFormat>();
		formats.put(0, NumberFormat.BULLET);
		formats.put(1, NumberFormat.DECIMAL);
		WordprocessingMLPackage pkg = pkgWith(numbering(33, formats),
				numberedParagraph("父项", 33, 0),
				numberedParagraph("子项", 33, 1));

		List<String> parts = markdownsOf(DocxStructureExtractor.extract(pkg));
		assertEquals("- 父项", parts.get(0));
		assertEquals("  1. 子项", parts.get(1), "ordered child indents by bullet parent width 2");

		assertNestedHtml("- 父项\n  1. 子项",
				"<li>父项<ol><li>子项</li>");
	}

	/**
	 * 10+ 项有序列表：两位编号标记 "10. " 起 “最宽标记” 变宽（4 列），
	 * 其后的子层级缩进必须跟着拓宽 —— 这是 {@code indent*2} 启发式做不到的场景。
	 */
	@Test
	void tenPlusItemOrderedListWidensChildIndent() throws Exception {
		Map<Integer, NumberFormat> formats = new LinkedHashMap<Integer, NumberFormat>();
		formats.put(0, NumberFormat.DECIMAL);
		formats.put(1, NumberFormat.BULLET);
		String[] labels = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二" };
		List<P> paragraphs = new ArrayList<P>();
		for (String label : labels) {
			paragraphs.add(numberedParagraph(label, 34, 0));
		}
		paragraphs.add(numberedParagraph("嵌套", 34, 1));
		P[] array = paragraphs.toArray(new P[0]);
		WordprocessingMLPackage pkg = pkgWith(numbering(34, formats), array);

		List<String> parts = markdownsOf(DocxStructureExtractor.extract(pkg));
		String parent = parts.get(0);
		for (int i = 0; i < labels.length; i++) {
			assertTrue(parent.contains((i + 1) + ". " + labels[i]),
					"item " + (i + 1) + " must render its own ordinal: missing " + (i + 1) + ". " + labels[i]);
		}
		assertEquals("    - 嵌套", parts.get(1),
				"child indent widens to two-digit marker width 4 (\"10. \")");
		assertEquals(12, parent.split("\n").length);

		assertNestedHtml(parent + "\n    - 嵌套",
				"<li>十二<ul><li>嵌套</li>");
	}

	/** 多个独立 run 并存时，第二个 run 的缩进几何从零重新累计（不受前一 run 影响）。 */
	@Test
	void secondListRunRestartsCumulativeIndent() throws Exception {
		Map<Integer, NumberFormat> formats = new LinkedHashMap<Integer, NumberFormat>();
		formats.put(0, NumberFormat.DECIMAL);
		formats.put(1, NumberFormat.DECIMAL);
		// 夹一个普通段落以关闭第一个列表 run（同 numId 连续段落会按扁平合并规则并入同一 run）
		P separator = F.createP();
		R sepRun = F.createR();
		Text sepText = F.createText();
		sepText.setValue("分隔");
		sepRun.getContent().add(sepText);
		separator.getContent().add(sepRun);
		WordprocessingMLPackage pkg = pkgWith(numbering(35, formats),
				numberedParagraph("首组一", 35, 0),
				numberedParagraph("首组二", 35, 1),
				separator,
				numberedParagraph("次组", 35, 0));

		List<String> parts = markdownsOf(DocxStructureExtractor.extract(pkg));
		assertEquals("1. 首组一", parts.get(0));
		assertEquals("   1. 首组二", parts.get(1));
		assertEquals("paragraph", ((DocxElement) DocxStructureExtractor.extract(pkg)
				.getElements().get(2)).getElementType(), "separator paragraph lands between runs");
		assertEquals("1. 次组", parts.get(3), "fresh run restarts column accounting");
	}
}
