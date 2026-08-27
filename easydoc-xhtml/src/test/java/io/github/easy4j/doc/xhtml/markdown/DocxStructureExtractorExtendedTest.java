package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.List;

import org.docx4j.dml.CTBlip;
import org.docx4j.dml.CTBlipFillProperties;
import org.docx4j.dml.CTNonVisualDrawingProps;
import org.docx4j.dml.Graphic;
import org.docx4j.dml.GraphicData;
import org.docx4j.dml.picture.Pic;
import org.docx4j.dml.wordprocessingDrawing.Anchor;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.Body;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.R.Tab;
import org.docx4j.wml.SdtBlock;
import org.docx4j.wml.SdtContentBlock;
import org.docx4j.wml.CTSdtContentRun;
import org.docx4j.wml.SdtRun;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.docx4j.wml.Tbl;
import org.junit.jupiter.api.Test;

/**
 * Coverage for {@link DocxStructureExtractor} guard/edge paths the primary
 * suite does not reach: SDT blocks (block + inline + degenerate), inline
 * container fallbacks, images across their blip/resolution variants
 * (happy path, unknown relationship, missing graphic data, anchored),
 * hyperlink without relationship id, and table-cell flattening of
 * nested blocks/tabs/breaks.
 */
class DocxStructureExtractorExtendedTest {

	private static final ObjectFactory F = new ObjectFactory();

	private static final byte[] PNG_1X1 = java.util.Base64.getDecoder().decode(
			"iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");

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

	private static Body body(WordprocessingMLPackage pkg) throws Exception {
		return pkg.getMainDocumentPart().getContents().getBody();
	}

	// ==================== 内容控件（SDT） ====================

	@Test
	void sdtBlockRecursesIntoInnerParagraphs() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		SdtBlock sdt = F.createSdtBlock();
		SdtContentBlock inner = F.createSdtContentBlock();
		inner.getContent().add(paragraph("控件内文本"));
		sdt.setSdtContent(inner);
		body(pkg).getContent().add(sdt);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertTrue(doc.fullMarkdown().contains("控件内文本"), "SDT-wrapped paragraphs must be extracted");
	}

	@Test
	void sdtWithoutContentIsIgnoredSafely() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		SdtBlock sdt = F.createSdtBlock();
		// sdtContent 本体存在但其内容列表为 null：命中 handleSdt 的防御分支
		sdt.setSdtContent(new SdtContentBlock() {
			@Override
			public java.util.List<Object> getContent() {
				return null;
			}
		});
		body(pkg).getContent().add(sdt);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertEquals("", doc.fullMarkdown());
	}

	@Test
	void sdtRunInsideParagraphContributesSpans() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		P p = F.createP();
		p.getContent().add(run("前缀 "));
		SdtRun sdtRun = F.createSdtRun();
		CTSdtContentRun inner = F.createCTSdtContentRun();
		inner.getContent().add(run("内嵌"));
		sdtRun.setSdtContent(inner);
		p.getContent().add(sdtRun);

		body(pkg).getContent().add(p);
		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertTrue(doc.fullMarkdown().contains("前缀 内嵌"),
				"inline SDT spans must merge into the paragraph, got: " + doc.fullMarkdown());
	}

	// ==================== 行内容器兜底与边界 ====================

	@Test
	void hyperlinkWithoutRelIdStillYieldsLinklessSpan() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		P p = F.createP();
		P.Hyperlink link = F.createPHyperlink();
		link.getContent().add(run("无链接ID"));
		p.getContent().add(link);

		body(pkg).getContent().add(p);
		List<DocxElement> els = DocxStructureExtractor.extract(pkg).getElements();
		assertEquals(1, els.size());
		assertFalse(DocxStructureExtractor.extract(pkg).fullMarkdown().contains("http"),
				"hyperlink without r:id must not fabricate a URL");
	}

	@Test
	void headingStyleWithNullValIsNotAHeading() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		P p = paragraph("正文样式无值");
		PPr pPr = F.createPPr();
		PPrBase.PStyle pStyle = new PPrBase.PStyle();
		pStyle.setVal(null);
		pPr.setPStyle(pStyle);
		p.setPPr(pPr);
		body(pkg).getContent().add(p);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertFalse(doc.getElements().get(0) instanceof DocxHeading,
				"a pStyle without val must degrade to a plain paragraph");
	}

	@Test
	void numPrWithNullIlvlValDefaultsToTopLevel() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		P p = paragraph("列表项缺级");
		PPr pPr = F.createPPr();
		PPrBase.NumPr np = new PPrBase.NumPr();
		PPrBase.NumPr.NumId nid = new PPrBase.NumPr.NumId();
		nid.setVal(BigInteger.valueOf(77));
		np.setNumId(nid);
		PPrBase.NumPr.Ilvl lvl = new PPrBase.NumPr.Ilvl();
		lvl.setVal(null);
		np.setIlvl(lvl);
		pPr.setNumPr(np);
		p.setPPr(pPr);
		body(pkg).getContent().add(p);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		long lists = doc.getElements().stream().filter(e -> e instanceof DocxList).count();
		assertEquals(1, lists, "null ilvl falls back to level 0 and still forms a list");
	}

	// ==================== 图片各形态 ====================

	/** 手工组装带 blip 的 inline drawing，r:embed 指向真实图片关系。 */
	private static Drawing inlineDrawingWithEmbed(String relId, String descr, String name)
			throws Exception {
		Drawing d = F.createDrawing();
		org.docx4j.dml.wordprocessingDrawing.ObjectFactory WPF =
				new org.docx4j.dml.wordprocessingDrawing.ObjectFactory();
		Inline inline = WPF.createInline();
		if (name != null || descr != null) {
			CTNonVisualDrawingProps docPr = new CTNonVisualDrawingProps();
			docPr.setName(name != null ? name : "img");
			docPr.setDescr(descr);
			inline.setDocPr(docPr);
		}
		Graphic graphic = new Graphic();
		GraphicData gd = new GraphicData();
		Pic pic = new Pic();
		CTBlipFillProperties fill = new CTBlipFillProperties();
		CTBlip blip = new CTBlip();
		blip.setEmbed(relId);
		fill.setBlip(blip);
		pic.setBlipFill(fill);
		gd.getAny().add(pic);
		graphic.setGraphicData(gd);
		inline.setGraphic(graphic);
		d.getAnchorOrInline().add(inline);
		return d;
	}

	@Test
	void realEmbeddedImageYieldsDataUriAndAltFromDescription() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		BinaryPartAbstractImage imagePart =
				BinaryPartAbstractImage.createImagePart(pkg, PNG_1X1);
		String relId = imagePart.getSourceRelationship().getId();

		P p = F.createP();
		p.getContent().add(inlineDrawingWithEmbed(relId, "截图说明", "图1"));
		p.getContent().add(run(" 图后文字"));
		body(pkg).getContent().add(p);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		boolean hasImage = doc.getElements().stream()
				.filter(e -> e instanceof DocxImage)
				.map(e -> (DocxImage) e)
				.anyMatch(i -> i.getSrc().startsWith("data:image/png;base64,")
						&& "截图说明".equals(i.getAlt()));
		assertTrue(hasImage, "embedded PNG must become a png data-URI image with its description as alt");
	}

	@Test
	void altFallsBackToNameWhenDescriptionBlank() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		BinaryPartAbstractImage imagePart =
				BinaryPartAbstractImage.createImagePart(pkg, PNG_1X1);
		String relId = imagePart.getSourceRelationship().getId();

		P p = F.createP();
		p.getContent().add(inlineDrawingWithEmbed(relId, "   ", "备选名"));
		body(pkg).getContent().add(p);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		String md = doc.fullMarkdown();
		assertTrue(md.contains("备选名"), "blank descr must fall back to docPr name, got: " + md);
	}

	@Test
	void imageWithUnknownRelationshipIsSkipped() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		P p = F.createP();
		p.getContent().add(run("占位 "));
		// blip 指向不存在的关系：bytes/mime 解析失败，drawing 被安全跳过
		Drawing ghost = inlineDrawingWithEmbed("rIdDoesNotExist", null, null);
		// 移除 docPr 以同时命中 altOf(null) 分支
		((Inline) ghost.getAnchorOrInline().get(0)).setDocPr(null);
		p.getContent().add(ghost);
		body(pkg).getContent().add(p);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		long images = doc.getElements().stream().filter(e -> e instanceof DocxImage).count();
		assertEquals(0, images, "unresolvable r:embed must be skipped silently");
	}

	@Test
	void drawingWithoutGraphicDataIsSkipped() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		Drawing empty = F.createDrawing();
		empty.getAnchorOrInline().clear(); // 无任何 inline/anchor 包装

		P withEmpty = F.createP();
		withEmpty.getContent().add(empty);
		body(pkg).getContent().add(withEmpty);

		// graphic 缺 graphicData 的 anchor 包装
		Drawing bareAnchor = F.createDrawing();
		org.docx4j.dml.wordprocessingDrawing.ObjectFactory WPF =
				new org.docx4j.dml.wordprocessingDrawing.ObjectFactory();
		Anchor anchor = WPF.createAnchor();
		bareAnchor.getAnchorOrInline().add(anchor);
		P withBare = F.createP();
		withBare.getContent().add(run("y"));
		withBare.getContent().add(bareAnchor);
		body(pkg).getContent().add(withBare);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		long images = doc.getElements().stream().filter(e -> e instanceof DocxImage).count();
		assertEquals(0, images, "drawings without resolvable graphic data must be skipped");
	}

	// ==================== 表格单元格扁平化 ====================

	@Test
	void tableCellFlattensNestedBlocksTabsAndBreaks() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();

		P nestedInCell = F.createP();
		nestedInCell.getContent().add(run("a"));
		R tabRun = F.createR();
		tabRun.getContent().add(F.createRTab());
		nestedInCell.getContent().add(tabRun);
		tabRun.getContent().add(text("b"));

		Tbl innerTbl = F.createTbl();
		Tr innerRow = F.createTr();
		Tc innerCell = F.createTc();
		P brP = F.createP();
		brP.getContent().add(run("c"));
		Br br = F.createBr();
		brP.getContent().add(br);
		brP.getContent().add(run("d"));
		innerCell.getContent().add(brP);
		innerRow.getContent().add(innerCell);
		innerTbl.getContent().add(innerRow);

		Tr row = F.createTr();
		Tc cell = F.createTc();
		cell.getContent().add(nestedInCell);
		cell.getContent().add(innerTbl);
		row.getContent().add(cell);

		Tbl outer = F.createTbl();
		outer.getContent().add(row);
		body(pkg).getContent().add(outer);

		String md = DocxStructureExtractor.extract(pkg).fullMarkdown();
		assertTrue(md.contains("a b c d"), "tabs/breaks collapse to spaces and blocks join, got: " + md);
	}

	// ==================== 下划线与空段落 ====================

	@Test
	void whitespaceOnlyParagraphIsDroppedEvenWithFormatting() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		P p = F.createP();
		R r = F.createR();
		RPr rPr = F.createRPr();
		rPr.setB(new BooleanDefaultTrue());
		r.setRPr(rPr);
		Text ws = F.createText();
		ws.setValue("   ");
		r.getContent().add(ws);
		p.getContent().add(r);
		body(pkg).getContent().add(p);

		DocxDocument doc = DocxStructureExtractor.extract(pkg);
		assertNotNull(doc);
		assertEquals("", doc.fullMarkdown());
	}
}
