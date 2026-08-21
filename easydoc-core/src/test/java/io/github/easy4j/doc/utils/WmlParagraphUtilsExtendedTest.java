package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTShd;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STLineSpacingRule;
import org.docx4j.wml.STShd;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.docx4j.wml.Tc;
import org.junit.jupiter.api.Test;

/**
 * Additional tests for WmlParagraphUtils to cover removeParaByIndex,
 * setParagraphSpacing branches, setParagraphIndInfo branches, and
 * setParagraghBorders.
 */
class WmlParagraphUtilsExtendedTest {

    @Test
    void removeParaByIndexRemovesFirstParagraph() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        P p1 = new P();
        P p2 = new P();
        pkg.getMainDocumentPart().getContent().add(p1);
        pkg.getMainDocumentPart().getContent().add(p2);

        boolean removed = WmlParagraphUtils.removeParaByIndex(pkg, 0);
        assertTrue(removed);
    }

    @Test
    void removeParaByIndexNegativeReturnsFalse() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        assertFalse(WmlParagraphUtils.removeParaByIndex(pkg, -1));
    }

    @Test
    void removeParaByIndexOutOfBoundsReturnsFalse() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        assertFalse(WmlParagraphUtils.removeParaByIndex(pkg, 999));
    }

    @Test
    void removeParaByIndexSkipsNonParagraphContent() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        // Add a table (not a P) and then a paragraph
        Tbl tbl = new Tbl();
        P p = new P();
        pkg.getMainDocumentPart().getContent().add(tbl);
        pkg.getMainDocumentPart().getContent().add(p);

        // Index 0 should find the first P (skipping Tbl)
        boolean removed = WmlParagraphUtils.removeParaByIndex(pkg, 0);
        assertTrue(removed);
    }

    @Test
    void setParagraphSpacingWithAllFieldsSet() {
        P p = new P();
        WmlParagraphUtils.setParagraphSpacing(p, true, "100", "200", "50", "60",
                true, "240", STLineSpacingRule.AUTO);
        PPr ppr = p.getPPr();
        PPrBase.Spacing spacing = ppr.getSpacing();
        assertEquals(100, spacing.getBefore().intValue());
        assertEquals(200, spacing.getAfter().intValue());
        assertEquals(50, spacing.getBeforeLines().intValue());
        assertEquals(60, spacing.getAfterLines().intValue());
        assertEquals(240, spacing.getLine().intValue());
        assertEquals(STLineSpacingRule.AUTO, spacing.getLineRule());
    }

    @Test
    void setParagraphSpacingWithExistingSpacing() {
        P p = new P();
        PPr ppr = new PPr();
        PPrBase.Spacing existingSpacing = new PPrBase.Spacing();
        ppr.setSpacing(existingSpacing);
        p.setPPr(ppr);

        WmlParagraphUtils.setParagraphSpacing(p, true, "100", "200", null, null,
                false, null, null);
        assertEquals(100, existingSpacing.getBefore().intValue());
    }

    @Test
    void setParagraphSpacingLineOnly() {
        P p = new P();
        WmlParagraphUtils.setParagraphSpacing(p, false, "", "", "", "",
                true, "360", STLineSpacingRule.EXACT);
        PPrBase.Spacing spacing = p.getPPr().getSpacing();
        assertEquals(360, spacing.getLine().intValue());
        assertEquals(STLineSpacingRule.EXACT, spacing.getLineRule());
    }

    @Test
    void setParagraphIndInfoAllFields() {
        P p = new P();
        WmlParagraphUtils.setParagraphIndInfo(p, "480", "100", "200", "50",
                "300", "75", "600", "100");
        PPrBase.Ind ind = p.getPPr().getInd();
        assertEquals(480, ind.getFirstLine().intValue());
        assertEquals(100, ind.getFirstLineChars().intValue());
        assertEquals(200, ind.getHanging().intValue());
        assertEquals(50, ind.getHangingChars().intValue());
        assertEquals(300, ind.getRight().intValue());
        assertEquals(75, ind.getRightChars().intValue());
        assertEquals(600, ind.getLeft().intValue());
        assertEquals(100, ind.getLeftChars().intValue());
    }

    @Test
    void setParagraphIndInfoWithExistingInd() {
        P p = new P();
        PPr ppr = new PPr();
        PPrBase.Ind existingInd = new PPrBase.Ind();
        ppr.setInd(existingInd);
        p.setPPr(ppr);

        WmlParagraphUtils.setParagraphIndInfo(p, null, null, null, null,
                null, null, "600", null);
        assertEquals(600, existingInd.getLeft().intValue());
    }

    @Test
    void setParagraghBordersSetsAllBorders() {
        P p = new P();
        ObjectFactory f = Context.getWmlObjectFactory();
        CTBorder top = f.createCTBorder();
        CTBorder bottom = f.createCTBorder();
        CTBorder left = f.createCTBorder();
        CTBorder right = f.createCTBorder();

        WmlParagraphUtils.setParagraghBorders(p, top, bottom, left, right);
        PPrBase.PBdr pBdr = p.getPPr().getPBdr();
        assertNotNull(pBdr);
        assertSame(top, pBdr.getTop());
        assertSame(bottom, pBdr.getBottom());
        assertSame(left, pBdr.getLeft());
        assertSame(right, pBdr.getRight());
    }

    @Test
    void setParagraghBordersWithNullBorders() {
        P p = new P();
        WmlParagraphUtils.setParagraghBorders(p, null, null, null, null);
        PPrBase.PBdr pBdr = p.getPPr().getPBdr();
        assertNotNull(pBdr);
        assertNull(pBdr.getTop());
    }

    @Test
    void setParaVanishExistingVanishUpdates() {
        PPr ppr = new PPr();
        ParaRPr paraRpr = new ParaRPr();
        BooleanDefaultTrue vanish = new BooleanDefaultTrue();
        vanish.setVal(false);
        paraRpr.setVanish(vanish);
        ppr.setRPr(paraRpr);

        WmlParagraphUtils.setParaVanish(ppr, true);
        assertTrue(ppr.getRPr().getVanish().isVal());
    }

    @Test
    void setParaRContentWithExistingContent() {
        P p = new P();
        // Add existing content
        R existingR = new R();
        Text existingText = new Text();
        existingText.setValue("old");
        existingR.getContent().add(existingText);
        p.getContent().add(existingR);

        // Replace with new content
        WmlParagraphUtils.setParaRContent(p, null, "new content");
        assertEquals(1, p.getContent().size());
    }

    @Test
    void setParaRContentWithRPr() {
        P p = new P();
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrBoldStyle(rpr);
        WmlParagraphUtils.setParaRContent(p, rpr, "bold text");
        assertEquals(1, p.getContent().size());
    }

    @Test
    void appendParaRContentWithRPr() {
        P p = new P();
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrBoldStyle(rpr);
        WmlParagraphUtils.appendParaRContent(p, rpr, "bold text");
        assertEquals(1, p.getContent().size());
    }

    @Test
    void addInlineImageToParagraphCreatesParagraphWithDrawing() {
        org.docx4j.dml.wordprocessingDrawing.Inline inline =
                new org.docx4j.dml.wordprocessingDrawing.Inline();
        P p = WmlParagraphUtils.addInlineImageToParagraph(inline);
        assertNotNull(p);
        assertEquals(1, p.getContent().size());
        Object run = p.getContent().get(0);
        assertTrue(run instanceof R);
        assertFalse(((R) run).getContent().isEmpty());
    }

    @Test
    void setParagraphShdStyleWithNullShdType() {
        P p = new P();
        WmlParagraphUtils.setParagraphShdStyle(p, null, "FF0000");
        assertNotNull(p.getPPr().getShd());
        assertEquals("FF0000", p.getPPr().getShd().getColor());
    }

    @Test
    void setParagraphShdStyleWithNullColor() {
        P p = new P();
        WmlParagraphUtils.setParagraphShdStyle(p, STShd.CLEAR, null);
        assertNotNull(p.getPPr().getShd());
        assertEquals(STShd.CLEAR, p.getPPr().getShd().getVal());
    }

	@Test
	void addPageBreakAddsBrToParagraph() {
		P p = Context.getWmlObjectFactory().createP();
		WmlParagraphUtils.addPageBreak(p, STBrType.PAGE);
		assertEquals(1, p.getContent().size());
		assertTrue(p.getContent().get(0) instanceof Br);
	}

	@Test
	void getParaRPrReturnsExistingOrCreates() {
		PPr ppr = Context.getWmlObjectFactory().createPPr();
		ParaRPr rpr = WmlParagraphUtils.getParaRPr(ppr);
		assertNotNull(rpr);
		// second call returns same instance
		assertSame(rpr, WmlParagraphUtils.getParaRPr(ppr));
	}

	@Test
	void addImageToParaAddsTextAndDrawing() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		ObjectFactory factory = Context.getWmlObjectFactory();
		P paragraph = factory.createP();
		java.nio.file.Path tmp = java.nio.file.Files.createTempFile("wmlpara", ".png");
		// 1x1 PNG
		byte[] png = java.util.Base64.getDecoder().decode(
				"iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");
		java.nio.file.Files.write(tmp, png);
		WmlParagraphUtils.addImageToPara(pkg, factory, paragraph, tmp.toString(), "text", null, "alt", 1, 2);
		assertFalse(paragraph.getContent().isEmpty());
		java.nio.file.Files.deleteIfExists(tmp);
	}

}
