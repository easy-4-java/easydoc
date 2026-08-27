package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.Body;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.Lvl;
import org.docx4j.wml.NumberFormat;
import org.docx4j.wml.NumFmt;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.SdtBlock;
import org.docx4j.wml.SdtContentBlock;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DocxStructureExtractorTest {

	private static final ObjectFactory F = new ObjectFactory();

	/** 1x1 透明 PNG。 */
	private static final byte[] PNG_1X1 = java.util.Base64.getDecoder().decode(
			"iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");

	// ==================== 构造工具 ====================

	private static Text text(String value) {
		Text t = F.createText();
		t.setValue(value);
		return t;
	}

	private static P paragraph(String value) {
		P p = F.createP();
		p.getContent().add(run(value));
		return p;
	}

	private static R run(String value) {
		R r = F.createR();
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

	private static R styledRun(String value, boolean bold, boolean italic,
			org.docx4j.wml.UnderlineEnumeration underline) {
		R r = F.createR();
		RPr rPr = F.createRPr();
		if (bold) {
			rPr.setB(new BooleanDefaultTrue());
			rPr.getB().setVal(true);
		}
		if (italic) {
			rPr.setI(new BooleanDefaultTrue());
			rPr.getI().setVal(true);
		}
		if (underline != null) {
			org.docx4j.wml.U u = new org.docx4j.wml.U();
			u.setVal(underline);
			rPr.setU(u);
		}
		r.setRPr(rPr);
		r.getContent().add(text(value));
		return r;
	}

	private static void add(Body body, Object block) throws Exception {
		body.getContent().add(block);
	}

	private static Body body(WordprocessingMLPackage pkg) throws Exception {
		return pkg.getMainDocumentPart().getContents().getBody();
	}

	/** 构造 decimal 编号定义：abstractNum=1 → num=numIdParam；lvl0/lvl1 均为 DECIMAL。 */
	private static NumberingDefinitionsPart decimalNumbering(int numId) throws Exception {
		NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
		org.docx4j.wml.Numbering numbering = new org.docx4j.wml.Numbering();

		org.docx4j.wml.Numbering.AbstractNum abs = new org.docx4j.wml.Numbering.AbstractNum();
		abs.setAbstractNumId(BigInteger.ONE);
		for (int ilvl = 0; ilvl <= 1; ilvl++) {
			Lvl lvl = new Lvl();
			lvl.setIlvl(BigInteger.valueOf(ilvl));
			NumFmt fmt = new NumFmt();
			fmt.setVal(NumberFormat.DECIMAL);
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

	/** 指定某 ilvl 的 numFmt 为 BULLET 的编号定义（abstractNum=2 → num=给定 id）。 */
	private static NumberingDefinitionsPart bulletNumbering(int numId) throws Exception {
		NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
		org.docx4j.wml.Numbering numbering = new org.docx4j.wml.Numbering();
		org.docx4j.wml.Numbering.AbstractNum abs = new org.docx4j.wml.Numbering.AbstractNum();
		abs.setAbstractNumId(BigInteger.valueOf(2));
		Lvl lvl = new Lvl();
		lvl.setIlvl(BigInteger.ZERO);
		NumFmt fmt = new NumFmt();
		fmt.setVal(NumberFormat.BULLET);
		lvl.setNumFmt(fmt);
		abs.getLvl().add(lvl);
		numbering.getAbstractNum().add(abs);

		org.docx4j.wml.Numbering.Num num = new org.docx4j.wml.Numbering.Num();
		num.setNumId(BigInteger.valueOf(numId));
		org.docx4j.wml.Numbering.Num.AbstractNumId absId =
				new org.docx4j.wml.Numbering.Num.AbstractNumId();
		absId.setVal(BigInteger.valueOf(2));
		num.setAbstractNumId(absId);
		numbering.getNum().add(num);

		ndp.setJaxbElement(numbering);
		return ndp;
	}

	/** 指定单层 numFmt 的编号定义（abstractNum=3 → num=给定 id），用于非 BULLET/DECIMAL 样式。 */
	private static NumberingDefinitionsPart singleLvlNumbering(int numId, NumberFormat fmt)
			throws Exception {
		NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
		org.docx4j.wml.Numbering numbering = new org.docx4j.wml.Numbering();

		org.docx4j.wml.Numbering.AbstractNum abs = new org.docx4j.wml.Numbering.AbstractNum();
		abs.setAbstractNumId(BigInteger.valueOf(3));
		Lvl lvl = new Lvl();
		lvl.setIlvl(BigInteger.ZERO);
		NumFmt numFmt = new NumFmt();
		numFmt.setVal(fmt);
		lvl.setNumFmt(numFmt);
		abs.getLvl().add(lvl);
		numbering.getAbstractNum().add(abs);

		org.docx4j.wml.Numbering.Num num = new org.docx4j.wml.Numbering.Num();
		num.setNumId(BigInteger.valueOf(numId));
		org.docx4j.wml.Numbering.Num.AbstractNumId absId =
				new org.docx4j.wml.Numbering.Num.AbstractNumId();
		absId.setVal(BigInteger.valueOf(3));
		num.setAbstractNumId(absId);
		numbering.getNum().add(num);

		ndp.setJaxbElement(numbering);
		return ndp;
	}

	private static void linkExternalRelationshipsPartIfMissing(WordprocessingMLPackage pkg) throws Exception {
		if (pkg.getMainDocumentPart().getRelationshipsPart() == null) {
			pkg.getMainDocumentPart().setRelationships(
					new org.docx4j.openpackaging.parts.relationships.RelationshipsPart(
							pkg.getMainDocumentPart()));
		}
	}

	private static DocxList listAt(DocxDocument doc, int index) {
		return (DocxList) doc.getElements().get(index);
	}

	// ==================== 文件 / 流入口 ====================

	@Test
	void extractFromFileHeadingParagraphAndStyledRuns(@TempDir Path tempDir) throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		P heading = F.createP();
		style(heading, "Heading2");
		heading.getContent().add(styledRun("章节标题", false, false, null));
		add(body(pkg), heading);

		P para = F.createP();
		para.getContent().add(styledRun("普通", false, false, null));
		para.getContent().add(styledRun("加粗", true, false, null));
		para.getContent().add(styledRun("斜体", false, true, null));
		para.getContent().add(styledRun("下划线", false, false, org.docx4j.wml.UnderlineEnumeration.SINGLE));
		add(body(pkg), para);

		File file = tempDir.resolve("styled.docx").toFile();
		pkg.save(file);
		DocxDocument doc = DocxStructureExtractor.extract(file);

		assertEquals(2, doc.getElements().size());
		DocxHeading h = (DocxHeading) doc.getElements().get(0);
		assertEquals(2, h.getLevel());
		assertEquals("章节标题", h.getText());
		assertNull(h.getHyperlinkUrl());
		assertEquals("## 章节标题", h.toMarkdown());

		DocxParagraph p = (DocxParagraph) doc.getElements().get(1);
		assertEquals(4, p.getSpans().size());
		assertFalse(p.getSpans().get(0).isBold());
		assertTrue(p.getSpans().get(1).isBold());
		assertFalse(p.getSpans().get(1).isItalic());
		assertTrue(p.getSpans().get(2).isItalic());
		assertTrue(p.getSpans().get(3).isUnderline());
		assertTrue(doc.fullMarkdown().contains("**加粗**"));
		assertTrue(doc.fullMarkdown().contains("*斜体*"));
		assertTrue(doc.fullMarkdown().contains("<u>下划线</u>"));
	}

	@Test
	void extractFromInputStreamAndNullGuards(@TempDir Path tempDir) throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		add(body(pkg), paragraph("流式内容"));
		File file = tempDir.resolve("stream.docx").toFile();
		pkg.save(file);

		try (InputStream in = new FileInputStream(file)) {
			DocxDocument doc = DocxStructureExtractor.extract(in);
			assertEquals(1, doc.getElements().size());
			assertEquals("流式内容", ((DocxParagraph) doc.getElements().get(0))
					.getSpans().get(0).getText());
		}

		assertThrows(NullPointerException.class, () -> DocxStructureExtractor.extract((File) null));
		assertThrows(NullPointerException.class, () -> DocxStructureExtractor.extract((InputStream) null));

		IOException missing = assertThrows(IOException.class,
				() -> DocxStructureExtractor.extract(tempDir.resolve("nope.docx").toFile()));
		assertTrue(missing.getMessage().contains("DOCX not found"));

		File garbage = tempDir.resolve("garbage.docx").toFile();
		Files.write(garbage.toPath(), "这不是一个 zip 包".getBytes("UTF-8"));
		assertThrows(IOException.class, () -> DocxStructureExtractor.extract(garbage));
	}

	// ==================== 标题样式 ====================

	@Test
	void titleStyleBecomesLevelZero() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		P title = F.createP();
		style(title, "Title");
		title.getContent().add(run("文档名"));
		add(body(pkg), title);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		DocxHeading h = (DocxHeading) doc.getElements().get(0);
		assertEquals(0, h.getLevel());
		assertEquals("# 文档名", h.toMarkdown());
	}

	@Test
	void nonHeadingOrMalformedStyleFallsBackToParagraph() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		P weirdA = F.createP();
		style(weirdA, "heading");
		weirdA.getContent().add(run("无层级"));
		P weirdB = F.createP();
		style(weirdB, "Heading12");
		weirdB.getContent().add(run("越界"));
		add(body(pkg), weirdA);
		add(body(pkg), weirdB);
		add(body(pkg), paragraph("正文"));

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		for (int i = 0; i < 3; i++) {
			String type = doc.getElements().get(i).getElementType();
			assertEquals("paragraph", type, "malformed styles must stay paragraphs: " + i);
		}
	}

	@Test
	void headingWithHyperlinkCarriesUrl() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		linkExternalRelationshipsPartIfMissing(pkg);
		Relationship ext = new Relationship();
		ext.setId("rIdHead");
		ext.setTarget("https://example.com/target");
		ext.setTargetMode("External");
		pkg.getMainDocumentPart().getRelationshipsPart().addRelationship(ext);

		P heading = F.createP();
		style(heading, "Heading3");
		P.Hyperlink hl = new P.Hyperlink();
		hl.setId("rIdHead");
		R r = F.createR();
		r.getContent().add(text("跳转标题"));
		hl.getContent().add(r);
		heading.getContent().add(hl);
		add(body(pkg), heading);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		DocxHeading h = (DocxHeading) doc.getElements().get(0);
		assertEquals(3, h.getLevel());
		assertEquals("https://example.com/target", h.getHyperlinkUrl());
		assertEquals("### [跳转标题](https://example.com/target)", h.toMarkdown());
	}

	// ==================== 超链接段落 ====================

	@Test
	void hyperlinkResolvesExternalTarget() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		linkExternalRelationshipsPartIfMissing(pkg);
		Relationship ext = new Relationship();
		ext.setId("rIdHl");
		ext.setTarget("https://example.com/page");
		ext.setTargetMode("External");
		pkg.getMainDocumentPart().getRelationshipsPart().addRelationship(ext);

		P p = F.createP();
		P.Hyperlink hl = new P.Hyperlink();
		hl.setId("rIdHl");
		R r = F.createR();
		r.getContent().add(text("链接文字"));
		hl.getContent().add(r);
		p.getContent().add(hl);
		add(body(pkg), p);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		InlineSpan span = ((DocxParagraph) doc.getElements().get(0)).getSpans().get(0);
		assertEquals("https://example.com/page", span.getHyperlinkUrl());
		assertEquals("[链接文字](https://example.com/page)", doc.fullMarkdown());
	}

	@Test
	void hyperlinkWithUnknownRelStillYieldsText() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		linkExternalRelationshipsPartIfMissing(pkg);
		P p = F.createP();
		P.Hyperlink hl = new P.Hyperlink();
		hl.setId("rId404");
		R r = F.createR();
		r.getContent().add(text("孤立"));
		hl.getContent().add(r);
		p.getContent().add(hl);
		add(body(pkg), p);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		InlineSpan span = ((DocxParagraph) doc.getElements().get(0)).getSpans().get(0);
		assertEquals("孤立", span.getText());
		assertNull(span.getHyperlinkUrl());
	}

	// ==================== 列表 ====================

	@Test
	void orderedListViaNumberingDefinitionsPart() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		pkg.getMainDocumentPart().addTargetPart(decimalNumbering(5));
		P a = paragraph("甲");
		numPr(a, 5, 0);
		P b = paragraph("乙");
		numPr(b, 5, 0);
		add(body(pkg), a);
		add(body(pkg), b);
		add(body(pkg), paragraph("收尾"));

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertEquals(2, doc.getElements().size());
		DocxList list = listAt(doc, 0);
		assertTrue(list.isOrdered());
		assertEquals(0, list.getIndent());
		assertEquals(java.util.Arrays.asList("甲", "乙"), list.getItems());
		assertEquals(2, list.getRichItems().size());
		assertEquals("1. 甲\n2. 乙", list.toMarkdown());
		assertEquals("paragraph", doc.getElements().get(1).getElementType(),
				"plain paragraph must close the list run");
	}

	@Test
	void bulletNumberingIsUnordered() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		pkg.getMainDocumentPart().addTargetPart(bulletNumbering(7));
		P a = paragraph("项目一");
		numPr(a, 7, 0);
		P b = paragraph("项目二");
		numPr(b, 7, 0);
		add(body(pkg), a);
		add(body(pkg), b);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		DocxList list = listAt(doc, 0);
		assertFalse(list.isOrdered());
		assertEquals("- 项目一\n- 项目二", list.toMarkdown());
	}

	@Test
	void lowerRomanNumFmtRendersOrdered() throws Exception {
		// 控制裁定：Markdown 只有一种有序列表语法，可解析的非 BULLET numFmt
		// （LOWER_ROMAN/i-ii-iii 等）一律渲染有序，不降级为无序。
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		pkg.getMainDocumentPart().addTargetPart(singleLvlNumbering(12, NumberFormat.LOWER_ROMAN));
		P a = paragraph("壹");
		numPr(a, 12, 0);
		P b = paragraph("贰");
		numPr(b, 12, 0);
		add(body(pkg), a);
		add(body(pkg), b);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertTrue(listAt(doc, 0).isOrdered(),
				"resolvable non-BULLET numFmt (LOWER_ROMAN) renders ordered");
	}

	@Test
	void missingNumberingPartDegradesToBullet() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		P a = paragraph("降级一");
		numPr(a, 9, 0);
		P b = paragraph("降级二");
		numPr(b, 9, 0);
		add(body(pkg), a);
		add(body(pkg), b);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		DocxList list = listAt(doc, 0);
		assertFalse(list.isOrdered(), "no numbering part -> degrade to bullets");
		assertEquals("- 降级一\n- 降级二", list.toMarkdown());
	}

	@Test
	void danglingNumIdAndMissingFmtDegradeToBullet() throws Exception {
		// numbering part 存在，但引用的 numId 未定义（悬空）；且另一 numId 缺少匹配 lvl 的 numFmt。
		org.docx4j.wml.Numbering numbering = new org.docx4j.wml.Numbering();
		org.docx4j.wml.Numbering.AbstractNum abs = new org.docx4j.wml.Numbering.AbstractNum();
		abs.setAbstractNumId(BigInteger.valueOf(10));
		Lvl wrongLvl = new Lvl(); // 只有 ilvl=3，与请求的 ilvl=0 不匹配且无 numFmt
		wrongLvl.setIlvl(BigInteger.valueOf(3));
		abs.getLvl().add(wrongLvl);
		numbering.getAbstractNum().add(abs);
		org.docx4j.wml.Numbering.Num num = new org.docx4j.wml.Numbering.Num();
		num.setNumId(BigInteger.valueOf(11));
		org.docx4j.wml.Numbering.Num.AbstractNumId absId =
				new org.docx4j.wml.Numbering.Num.AbstractNumId();
		absId.setVal(BigInteger.valueOf(10));
		num.setAbstractNumId(absId);
		numbering.getNum().add(num);
		NumberingDefinitionsPart part = new NumberingDefinitionsPart();
		part.setJaxbElement(numbering);

		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		pkg.getMainDocumentPart().addTargetPart(part);

		P dangling = paragraph("悬空");
		numPr(dangling, 99, 0); // numToAbstract 无此映射
		P dangling2 = paragraph("悬空二");
		numPr(dangling2, 99, 0);
		add(body(pkg), dangling);
		add(body(pkg), dangling2);
		add(body(pkg), paragraph("过渡"));

		P nofmt = paragraph("无格式");
		numPr(nofmt, 11, 0); // 映射存在但 ilvl/numFmt 断链
		P nofmt2 = paragraph("无格式二");
		numPr(nofmt2, 11, 0);
		add(body(pkg), nofmt);
		add(body(pkg), nofmt2);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertEquals(3, doc.getElements().size());
		assertFalse(listAt(doc, 0).isOrdered());
		assertEquals(java.util.Arrays.asList("悬空", "悬空二"), listAt(doc, 0).getItems());
		assertEquals("paragraph", doc.getElements().get(1).getElementType(),
				"interlude paragraph emits in document order between the two list runs");
		assertEquals("过渡", ((DocxParagraph) doc.getElements().get(1)).getSpans().get(0).getText());
		assertFalse(listAt(doc, 2).isOrdered(), "lvl mismatch + missing numFmt -> bullet");
		assertEquals(java.util.Arrays.asList("无格式", "无格式二"), listAt(doc, 2).getItems());
	}

	/**
	 * 扁平合并规则钉死：同一 numId 内按 ilvl 分桶，run 结束时按 ilvl 升序各输出一个
	 * DocxList（level 0 在前，indent=ilvl），深浅交替时同桶项仍合并；
	 * 不同 numId 则开启新 run。
	 */
	@Test
	void flatMergingRuleAcrossLevelsAndRuns() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		pkg.getMainDocumentPart().addTargetPart(decimalNumbering(21));
		P a = paragraph("一层");
		numPr(a, 21, 0);
		P b = paragraph("二层");
		numPr(b, 21, 1);
		P c = paragraph("又一层");
		numPr(c, 21, 0);
		P other = paragraph("另一个列表");
		numPr(other, 22, 0); // 未在 numbering 中定义 → 同为无序，但独立 run
		add(body(pkg), a);
		add(body(pkg), b);
		add(body(pkg), c);
		add(body(pkg), other);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertEquals(3, doc.getElements().size());

		DocxList level0 = listAt(doc, 0);
		assertTrue(level0.isOrdered());
		assertEquals(0, level0.getIndent());
		assertEquals(java.util.Arrays.asList("一层", "又一层"), level0.getItems(),
				"same-bucket items merge despite interleaving");

		DocxList level1 = listAt(doc, 1);
		assertTrue(level1.isOrdered());
		assertEquals(1, level1.getIndent());
		assertEquals(java.util.Collections.singletonList("二层"), level1.getItems());

		DocxList secondRun = listAt(doc, 2);
		assertEquals(0, secondRun.getIndent());
		assertEquals(java.util.Collections.singletonList("另一个列表"), secondRun.getItems());
	}

	@Test
	void listItemKeepsRichSpansWithFormatting() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		pkg.getMainDocumentPart().addTargetPart(decimalNumbering(31));
		P item = F.createP();
		numPr(item, 31, 0);
		item.getContent().add(styledRun("重", true, false, null));
		item.getContent().add(run("点项"));
		add(body(pkg), item);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		DocxList list = listAt(doc, 0);
		assertTrue(list.getItems().get(0).contains("重点项"));
		assertTrue(list.getRichItems().get(0).get(0).isBold());
		assertEquals("1. **重**点项", list.toMarkdown());
	}

	// ==================== 表格 ====================

	@Test
	void tableFirstRowAsHeaderAndNestedCellFlattened() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();

		Tbl nested = F.createTbl();
		Tr nestedRow = F.createTr();
		Tc nestedCell = F.createTc();
		nestedCell.getContent().add(paragraph("内嵌格"));
		nestedRow.getContent().add(nestedCell);
		nested.getContent().add(nestedRow);

		Tr header = row("列甲", "列乙");
		Tr data = F.createTr();
		data.getContent().add(cellWithText("a1"));
		Tc nestHolder = cellWithText("b1");
		nestHolder.getContent().add(nested);
		data.getContent().add(nestHolder);

		Tbl tbl = F.createTbl();
		tbl.getContent().add(header);
		tbl.getContent().add(data);

		add(body(pkg), tbl);
		add(body(pkg), paragraph("表格之后"));

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertEquals(2, doc.getElements().size());
		DocxTable table = (DocxTable) doc.getElements().get(0);
			assertEquals(java.util.Arrays.asList("列甲", "列乙"), table.getHeadersAsText());
			assertEquals(1, table.getRows().size());
			assertEquals(java.util.Arrays.asList("a1", "b1 内嵌格"), table.getRowsAsText().get(0),
					"nested table text flattens into its holder cell");
		assertTrue(table.toMarkdown().startsWith("| 列甲 | 列乙 |"));
		assertTrue(doc.fullMarkdown().contains("| a1 | b1 内嵌格 |"));
	}

	@Test
	void tableWithoutRowsIsSkipped() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		Tbl empty = F.createTbl(); // 无任何 Tr
		add(body(pkg), empty);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertTrue(doc.getElements().isEmpty());
		assertEquals("", doc.fullMarkdown());
	}

	private static Tr row(String... cells) {
		Tr tr = F.createTr();
		for (String value : cells) {
			tr.getContent().add(cellWithText(value));
		}
		return tr;
	}

	private static Tc cellWithText(String value) {
		Tc tc = F.createTc();
		tc.getContent().add(paragraph(value));
		return tc;
	}

	// ==================== 图片 ====================

	@Test
	void imageExtractedAsDataUri(@TempDir Path tempDir) throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(pkg, PNG_1X1);
		Inline inline = imagePart.createImageInline("pic.png", "示意图片", 1L, 2, false);
		Drawing drawing = F.createDrawing();
		drawing.getAnchorOrInline().add(inline);
		P p = F.createP();
		R r = F.createR();
		r.getContent().add(drawing);
		p.getContent().add(r);
		add(body(pkg), p);

		File file = tempDir.resolve("image.docx").toFile();
		pkg.save(file);
		DocxDocument doc = DocxStructureExtractor.extract(file);

		assertEquals(1, doc.getElements().size());
		DocxImage img = (DocxImage) doc.getElements().get(0);
		assertEquals("image/png", img.getMime());
		assertEquals("示意图片", img.getAlt());
		assertTrue(img.getSrc().startsWith("data:image/png;base64,"),
				"src must be an inline data URI");
		byte[] decoded = java.util.Base64.getDecoder().decode(
				img.getSrc().substring(img.getSrc().indexOf(",") + 1));
		assertTrue(java.util.Arrays.equals(PNG_1X1, decoded), "round trip keeps bytes");
		assertEquals("![示意图片](data:image/png;base64," , img.toMarkdown()
				.substring(0, "![示意图片](data:image/png;base64,".length()));
	}

	@Test
	void drawingWithoutBlipIsSkippedSilently() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		Drawing drawing = F.createDrawing();
		// Inline 的 graphic 存在但 graphicData 为空 => blip 无法解析
		Inline inline = new Inline();
		inline.setGraphic(new org.docx4j.dml.Graphic());
		drawing.getAnchorOrInline().add(inline);
		P p = F.createP();
		R r = F.createR();
		r.getContent().add(drawing);
		p.getContent().add(r);
		add(body(pkg), p);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertTrue(doc.getElements().isEmpty(), "unresolvable blip => image skipped");
	}

	// ==================== SdtBlock / 空段落 / 异常降级 ====================

	@Test
	void sdtBlockChildrenAreProcessedInOrder() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		SdtBlock sdt = F.createSdtBlock();
		SdtContentBlock content = F.createSdtContentBlock();
		P inside = F.createP();
		style(inside, "Heading1");
		inside.getContent().add(run("控件内标题"));
		content.getContent().add(inside);
		content.getContent().add(rowlessTableForSdt());
		sdt.setSdtContent(content);
		add(body(pkg), sdt);
		add(body(pkg), paragraph("控件外"));

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertEquals(3, doc.getElements().size());
		assertEquals("heading", doc.getElements().get(0).getElementType());
		assertEquals("# 控件内标题",
				((DocxHeading) doc.getElements().get(0)).toMarkdown());
		assertEquals("table", doc.getElements().get(1).getElementType());
		assertEquals("paragraph", doc.getElements().get(2).getElementType());
	}

	private static Tbl rowlessTableForSdt() {
		Tr tr = F.createTr();
		tr.getContent().add(cellWithText("表头甲"));
		tr.getContent().add(cellWithText("表头乙"));
		Tbl tbl = F.createTbl();
		tbl.getContent().add(tr);
		return tbl;
	}

	@Test
	void whitespaceOnlyParagraphIsSkippedButBreakParagraphEmitsNewlines() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		P tabsOnly = F.createP();
		R tabRun = F.createR();
		tabRun.getContent().add(new R.Tab());
		tabsOnly.getContent().add(tabRun);
		add(body(pkg), tabsOnly);

		P breakPara = F.createP();
		breakPara.getContent().add(styledRun("前", false, false, null));
		Br pageBreak = F.createBr();
		pageBreak.setType(STBrType.PAGE);
		breakPara.getContent().add(pageBreak);
		breakPara.getContent().add(new R.Tab());
		breakPara.getContent().add(run("后"));
		add(body(pkg), breakPara);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertEquals(1, doc.getElements().size(), "tab-only paragraph skipped");
		DocxParagraph spans = (DocxParagraph) doc.getElements().get(0);
		assertEquals(4, spans.getSpans().size());
		assertEquals("\n", spans.getSpans().get(1).getText(), "page break becomes newline");
		assertEquals("\t", spans.getSpans().get(2).getText());
		assertEquals("前\n\t后", spans.toMarkdown());
	}

	/** getContent 抛异常的坏元素：验证顶层 try/catch 跳过后继续解析。 */
	static final class ExplodingTbl extends Tbl {

		boolean touched;

		@Override
		public java.util.List<Object> getContent() {
			touched = true;
			throw new IllegalStateException("boom");
		}
	}

	@Test
	void malformedElementSkippedAndParsingContinues() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		ExplodingTbl bad = new ExplodingTbl();
		add(body(pkg), bad);
		add(body(pkg), paragraph("幸存者"));

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertTrue(bad.touched, "bad element was visited");
		assertEquals(1, doc.getElements().size());
		assertEquals("paragraph", doc.getElements().get(0).getElementType());
		assertEquals("幸存者", doc.fullMarkdown());
	}

	// ==================== 空 body 与元数据 ====================

	@Test
	void emptyBodyYieldsEmptyDocument() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertNotNull(doc);
		assertTrue(doc.getElements().isEmpty());
		assertEquals("", doc.fullMarkdown());
	}

	@Test
	void corePropertiesFeedTitleAuthorModified() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		pkg.addDocPropsCorePart();
		org.docx4j.docProps.core.CoreProperties props =
				new org.docx4j.docProps.core.CoreProperties();

		org.docx4j.docProps.core.dc.elements.SimpleLiteral creator =
				new org.docx4j.docProps.core.dc.elements.SimpleLiteral();
		creator.getContent().add("张三");
		props.setCreator(creator);

		javax.xml.namespace.QName dcTitle = new javax.xml.namespace.QName(
				"http://purl.org/dc/elements/1.1/", "title");
		org.docx4j.docProps.core.dc.elements.SimpleLiteral titleLit =
				new org.docx4j.docProps.core.dc.elements.SimpleLiteral();
		titleLit.getContent().add("设计手册");
		props.setTitle(new javax.xml.bind.JAXBElement<>(dcTitle,
				org.docx4j.docProps.core.dc.elements.SimpleLiteral.class, titleLit));

		org.docx4j.docProps.core.dc.elements.SimpleLiteral modified =
				new org.docx4j.docProps.core.dc.elements.SimpleLiteral();
		modified.getContent().add("2026-08-26T08:30:00Z");
		props.setModified(modified);

		pkg.getDocPropsCorePart().setJaxbElement(props);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertEquals("设计手册", doc.getTitle());
		assertEquals("张三", doc.getAuthor());
		assertEquals(java.time.Instant.parse("2026-08-26T08:30:00Z"), doc.getModified());
	}

	@Test
	void badModifiedTimestampFallsBackToNull() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		pkg.addDocPropsCorePart();
		org.docx4j.docProps.core.CoreProperties props =
				new org.docx4j.docProps.core.CoreProperties();
		org.docx4j.docProps.core.dc.elements.SimpleLiteral modified =
				new org.docx4j.docProps.core.dc.elements.SimpleLiteral();
		modified.getContent().add("上周三");
		props.setModified(modified);
		pkg.getDocPropsCorePart().setJaxbElement(props);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertNull(doc.getModified());
	}

	// ==================== 集成：EasyMarkdown 产包兜底场景 ====================

	/**
	 * 经验校验结论：ImportXHTML 导入 "# 标题" 不产出 Heading1 样式，
	 * 故此处仅断言段落文本可被抽取器还原（不假设标题语义）。
	 */
	@Test
	void integrationEasyMarkdownPackageRoundTrip() throws Exception {
		WordprocessingMLPackage pkg = EasyMarkdown.markdownToDocx("# 标题\n\n- 甲\n- 乙");
		File file = File.createTempFile("easydoc-md", ".docx");
		try {
			pkg.save(file);
			DocxDocument doc = DocxStructureExtractor.extract(file);
			assertTrue(doc.fullMarkdown().contains("标题"), "paragraph text survives");
			long lists = doc.getElements().stream().filter(e -> e instanceof DocxList).count();
			assertEquals(1, lists, "consecutive same-numId paragraphs merge into one list");
			assertTrue(doc.fullMarkdown().contains("甲") && doc.fullMarkdown().contains("乙"));
		} finally {
			file.delete();
		}
	}
}
