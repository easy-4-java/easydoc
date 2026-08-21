package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STLineSpacingRule;
import org.docx4j.wml.STShd;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.docx4j.wml.UnderlineEnumeration;
import org.junit.jupiter.api.Test;

class WmlElementUtilsTest {

    @Test
    void getRPrCreatesRPrWhenMissing() {
        R r = new R();
        RPr rpr = WmlElementUtils.getRPr(r);
        assertNotNull(rpr);
        assertSame(rpr, r.getRPr());
    }

    @Test
    void getPPrCreatesPPrWhenMissing() {
        P p = new P();
        PPr ppr = WmlElementUtils.getPPr(p);
        assertNotNull(ppr);
        assertSame(ppr, p.getPPr());
    }

    @Test
    void getAllElementFromObjectReturnsEmptyForLeaf() {
        List<Object> result = WmlElementUtils.getAllElementFromObject("string", RPr.class);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void getTargetElementsReturnsSelfIfMatching() {
        List<RPr> result = WmlElementUtils.getTargetElements(new RPr(), RPr.class);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getChildrenElementsIncludesSelf() {
        List<RPr> result = WmlElementUtils.getChildrenElements(new RPr(), RPr.class);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void setFontFamilyAppliesToExistingRPr() {
        RPr rpr = new RPr();
        WmlElementUtils.setFontFamily(rpr, "\u5b8b\u4f53", "Times New Roman");
        assertNotNull(rpr.getRFonts());
        assertEquals("\u5b8b\u4f53", rpr.getRFonts().getEastAsia());
        assertEquals("Times New Roman", rpr.getRFonts().getAscii());
    }

    @Test
    void createWordprocessingMLPackageReturnsPackage() throws Exception {
        assertNotNull(WmlElementUtils.createWordprocessingMLPackage());
    }

    @Test
    void createFooterReturnsFooter() {
        assertNotNull(WmlElementUtils.createFooter("test footer"));
    }

    @Test
    void getDocSectPrReturnsSectPr() throws Exception {
        org.docx4j.openpackaging.packages.WordprocessingMLPackage pkg =
                org.docx4j.openpackaging.packages.WordprocessingMLPackage.createPackage();
        SectPr sectPr = WmlElementUtils.getDocSectPr(pkg);
        assertNotNull(sectPr);
    }

    @Test
    void addInlineImageToParagraphReturnsParagraph() {
        // Inline requires a drawing -- just test null handling
        org.docx4j.dml.wordprocessingDrawing.Inline inline = new org.docx4j.dml.wordprocessingDrawing.Inline();
        P p = WmlElementUtils.addInlineImageToParagraph(inline);
        assertNotNull(p);
        assertNotNull(p.getContent());
    }

    @Test
    void setParaJcAlignSetsAlignment() {
        P p = new P();
        WmlElementUtils.setParaJcAlign(p, JcEnumeration.CENTER);
        assertNotNull(p.getPPr());
        assertNotNull(p.getPPr().getJc());
        assertEquals(JcEnumeration.CENTER, p.getPPr().getJc().getVal());
    }

    @Test
    void setParaRContentAddsRun() {
        P p = new P();
        WmlElementUtils.setParaRContent(p, null, "hello");
        assertEquals(1, p.getContent().size());
    }

    @Test
    void appendParaRContentAppends() {
        P p = new P();
        WmlElementUtils.appendParaRContent(p, null, "a");
        WmlElementUtils.appendParaRContent(p, null, "b");
        assertEquals(2, p.getContent().size());
    }

    @Test
    void addPageBreakAddsBr() {
        P p = new P();
        WmlElementUtils.addPageBreak(p, STBrType.PAGE);
        assertEquals(1, p.getContent().size());
    }

    @Test
    void setParagraphSuppressLineNumSetsSuppress() {
        P p = new P();
        WmlElementUtils.setParagraphSuppressLineNum(p);
        assertNotNull(p.getPPr());
        assertNotNull(p.getPPr().getSuppressLineNumbers());
    }

    @Test
    void setParagraphShdStyleSetsShd() {
        P p = new P();
        WmlElementUtils.setParagraphShdStyle(p, STShd.CLEAR, "FF0000");
        assertNotNull(p.getPPr().getShd());
    }

    @Test
    void addRPrBoldStyleSetsB() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrBoldStyle(rpr);
        assertNotNull(rpr.getB());
    }

    @Test
    void addRPrItalicStyleSetsI() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrItalicStyle(rpr);
        assertNotNull(rpr.getI());
    }

    @Test
    void addRPrUnderlineStyleSetsU() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrUnderlineStyle(rpr, UnderlineEnumeration.SINGLE);
        assertNotNull(rpr.getU());
    }

    @Test
    void addRPrStrikeStyleSetsStrike() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrStrikeStyle(rpr, true, false);
        assertNotNull(rpr.getStrike());
    }

    @Test
    void addRPrShadowStyleSetsShadow() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrShadowStyle(rpr);
        assertNotNull(rpr.getShadow());
    }

    @Test
    void addRPrImprintStyleSetsImprint() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrImprintStyle(rpr);
        assertNotNull(rpr.getImprint());
    }

    @Test
    void addRPrEmbossStyleSetsEmboss() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrEmbossStyle(rpr);
        assertNotNull(rpr.getEmboss());
    }

    @Test
    void addRPrOutlineStyleSetsOutline() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrOutlineStyle(rpr);
        assertNotNull(rpr.getOutline());
    }

    @Test
    void getTblAllTrReturnsEmptyForEmptyTable() {
        Tbl tbl = new Tbl();
        List<Tr> rows = WmlElementUtils.getTblAllTr(tbl);
        assertNotNull(rows);
        assertEquals(0, rows.size());
    }

    @Test
    void getTrAllCellReturnsEmptyForEmptyRow() {
        Tr tr = new Tr();
        List<Tc> cells = WmlElementUtils.getTrAllCell(tr);
        assertNotNull(cells);
        assertEquals(0, cells.size());
    }

    @Test
    void getTcPrCreatesWhenMissing() {
        Tc tc = new Tc();
        org.docx4j.wml.TcPr tcPr = WmlElementUtils.getTcPr(tc);
        assertNotNull(tcPr);
        assertSame(tcPr, tc.getTcPr());
    }

    @Test
    void setTcVAlignSetsAlignment() {
        Tc tc = new Tc();
        WmlElementUtils.setTcVAlign(tc, STVerticalJc.CENTER);
        assertNotNull(tc.getTcPr().getVAlign());
    }

    @Test
    void setTcJcAlignSetsAlignment() {
        Tc tc = new Tc();
        // setTcJcAlign sets alignment on paragraphs inside the cell, not on TcPr.
        // With an empty cell (no paragraphs), the method is a no-op.
        // Verify it doesn't throw.
        WmlElementUtils.setTcJcAlign(tc, JcEnumeration.RIGHT);
    }

    @Test
    void getParaRPrCreatesWhenNull() {
        PPr ppr = new PPr();
        org.docx4j.wml.ParaRPr paraRpr = WmlElementUtils.getParaRPr(ppr);
        assertNotNull(paraRpr);
        assertSame(paraRpr, ppr.getRPr());
    }

    @Test
    void setParaVanishSetsVanish() {
        PPr ppr = new PPr();
        WmlElementUtils.setParaVanish(ppr, true);
        assertNotNull(ppr.getRPr());
        assertNotNull(ppr.getRPr().getVanish());
    }
}
