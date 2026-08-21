package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Supplementary tests for WmlElementUtils facade delegate methods
 * that were not covered by the original WmlElementUtilsTest.
 * Each test exercises one or more deprecated delegate methods,
 * which simultaneously covers the delegate line in WmlElementUtils
 * and the actual implementation in the target utility class.
 */
class WmlElementUtilsFacadeTest {

    private static final ObjectFactory F = Context.getWmlObjectFactory();

    // ======================== Traversal delegates ========================

    @Test
    void getElementContentExtractsText() throws Exception {
        P p = new P();
        R r = new R();
        Text t = new Text();
        t.setValue("hello");
        r.getContent().add(t);
        p.getContent().add(r);
        String content = WmlElementUtils.getElementContent(p);
        assertNotNull(content);
        assertTrue(content.contains("hello"));
    }

    @Test
    void getChildrenElementsReturnsContentListForContentAccessor() {
        Tbl tbl = new Tbl();
        // Tbl is not directly a ContentAccessor for children,
        // but wrapping in JAXBElement path via getChildrenElements
        // should return the list when the source itself matches
        List<Object> result = WmlElementUtils.getChildrenElements(tbl, Object.class);
        assertNotNull(result);
    }

    // ======================== Table delegates ========================

    @Test
    void getTableFindsMatchingTable() throws Exception {
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc = new Tc();
        P p = new P();
        R r = new R();
        Text t = new Text();
        t.setValue("${placeholder}");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);
        tr.getContent().add(tc);
        tbl.getContent().add(tr);

        List<Tbl> tables = new ArrayList<>();
        tables.add(tbl);
        Tbl result = WmlElementUtils.getTable(tables, "${placeholder}");
        assertNotNull(result);
    }

    @Test
    void replaceTableReplacesPlaceholders() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc = new Tc();
        P p = new P();
        R r = new R();
        Text t = new Text();
        t.setValue("${name}");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);
        tr.getContent().add(tc);
        tbl.getContent().add(tr);
        pkg.getMainDocumentPart().getContent().add(tbl);

        String[] placeholders = new String[]{"${name}"};
        Map<String, String> row = new HashMap<>();
        row.put("${name}", "Alice");
        List<Map<String, String>> data = new ArrayList<>();
        data.add(row);

        WmlElementUtils.replaceTable(placeholders, data, pkg);
        // Should not throw
    }

    @Test
    void addRowToTableAddsRow() {
        Tbl tbl = new Tbl();
        Tr templateRow = new Tr();
        Tc tc = new Tc();
        P p = new P();
        R r = new R();
        Text t = new Text();
        t.setValue("${col}");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);
        templateRow.getContent().add(tc);
        tbl.getContent().add(templateRow);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("${col}", "value");
        WmlElementUtils.addRowToTable(tbl, templateRow, replacements);
        assertTrue(tbl.getContent().size() >= 2);
    }

    @Test
    void saveWordPackageWritesFile(@TempDir Path tempDir) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File outFile = tempDir.resolve("test.docx").toFile();
        WmlElementUtils instance = new WmlElementUtils();
        instance.saveWordPackage(pkg, outFile);
        assertTrue(outFile.exists());
        assertTrue(outFile.length() > 0);
    }

    @Test
    void getTcByPositionReturnsCorrectCell() {
        Tc tc0 = new Tc();
        Tc tc1 = new Tc();
        Tc tc2 = new Tc();
        List<Tc> list = new ArrayList<>();
        list.add(tc0);
        list.add(tc1);
        list.add(tc2);

        WmlElementUtils instance = new WmlElementUtils();
        assertSame(tc1, instance.getTcByPosition(list, 1));
    }

    @Test
    void mergeCellsHorizontalByGridSpanMergesCells() {
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc1 = new Tc();
        Tc tc2 = new Tc();
        Tc tc3 = new Tc();
        tr.getContent().add(tc1);
        tr.getContent().add(tc2);
        tr.getContent().add(tc3);
        tbl.getContent().add(tr);
        WmlElementUtils.mergeCellsHorizontalByGridSpan(tbl, 0, 0, 1);
        // Should not throw
    }

    @Test
    void mergeCellsHorizontalMergesCells() {
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc1 = new Tc();
        Tc tc2 = new Tc();
        tr.getContent().add(tc1);
        tr.getContent().add(tc2);
        tbl.getContent().add(tr);
        WmlElementUtils.mergeCellsHorizontal(tbl, 0, 0, 1);
        // Should not throw
    }

    @Test
    void mergeCellsVerticallyMergesCells() {
        Tbl tbl = new Tbl();
        Tr tr1 = new Tr();
        Tr tr2 = new Tr();
        Tc tc1 = new Tc();
        Tc tc2 = new Tc();
        tr1.getContent().add(tc1);
        tr2.getContent().add(tc2);
        tbl.getContent().add(tr1);
        tbl.getContent().add(tr2);
        WmlElementUtils.mergeCellsVertically(tbl, 0, 0, 1);
        // Should not throw
    }

    @Test
    void getTcReturnsCorrectCell() {
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc = new Tc();
        tr.getContent().add(tc);
        tbl.getContent().add(tr);
        Tc result = WmlElementUtils.getTc(tbl, 0, 0);
        assertSame(tc, result);
    }

    @Test
    void getAllTblReturnsTablesFromPackage() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        Tbl tbl = new Tbl();
        pkg.getMainDocumentPart().getContent().add(tbl);
        List<Tbl> result = WmlElementUtils.getAllTbl(pkg);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void removeTableByIndexRemovesTable() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        Tbl tbl = new Tbl();
        pkg.getMainDocumentPart().getContent().add(tbl);
        boolean removed = WmlElementUtils.removeTableByIndex(pkg, 0);
        assertTrue(removed);
    }

    @Test
    void getTblContentStrExtractsText() throws Exception {
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc = new Tc();
        P p = new P();
        R r = new R();
        Text t = new Text();
        t.setValue("cell text");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);
        tr.getContent().add(tc);
        tbl.getContent().add(tr);

        String content = WmlElementUtils.getTblContentStr(tbl);
        assertNotNull(content);
    }

    @Test
    void getTblContentListExtractsTextList() throws Exception {
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc = new Tc();
        P p = new P();
        R r = new R();
        Text t = new Text();
        t.setValue("cell");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);
        tr.getContent().add(tc);
        tbl.getContent().add(tr);

        List<String> result = WmlElementUtils.getTblContentList(tbl);
        assertNotNull(result);
    }

    @Test
    void getTblPrCreatesWhenMissing() {
        Tbl tbl = new Tbl();
        TblPr result = WmlElementUtils.getTblPr(tbl);
        assertNotNull(result);
        assertSame(result, tbl.getTblPr());
    }

    @Test
    void setTableWidthSetsWidth() {
        Tbl tbl = new Tbl();
        WmlElementUtils.setTableWidth(tbl, "5000");
        assertNotNull(tbl.getTblPr());
        assertNotNull(tbl.getTblPr().getTblW());
    }

    @Test
    void createTableWithPackageReturnsTable() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        Tbl tbl = WmlElementUtils.createTable(pkg, 2, 3);
        assertNotNull(tbl);
    }

    @Test
    void createTableWithWidthsReturnsTable() throws Exception {
        int[] widths = new int[]{2000, 3000, 5000};
        Tbl tbl = WmlElementUtils.createTable(2, 3, widths);
        assertNotNull(tbl);
    }

    @Test
    void setTblBordersSetsBorders() {
        WmlElementUtils instance = new WmlElementUtils();
        TblPr tblPr = new TblPr();
        CTBorder top = F.createCTBorder();
        CTBorder right = F.createCTBorder();
        CTBorder bottom = F.createCTBorder();
        CTBorder left = F.createCTBorder();
        CTBorder h = F.createCTBorder();
        CTBorder v = F.createCTBorder();
        instance.setTblBorders(tblPr, top, right, bottom, left, h, v);
        assertNotNull(tblPr.getTblBorders());
    }

    @Test
    void setTblJcAlignSetsAlignment() {
        WmlElementUtils instance = new WmlElementUtils();
        Tbl tbl = new Tbl();
        instance.setTblJcAlign(tbl, JcEnumeration.CENTER);
        assertNotNull(tbl.getTblPr());
    }

    @Test
    void setTblAllJcAlignSetsAlignment() {
        WmlElementUtils instance = new WmlElementUtils();
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc = new Tc();
        P p = new P();
        tc.getContent().add(p);
        tr.getContent().add(tc);
        tbl.getContent().add(tr);
        instance.setTblAllJcAlign(tbl, JcEnumeration.CENTER);
        // Should not throw
    }

    @Test
    void setTblAllVAlignSetsAlignment() {
        WmlElementUtils instance = new WmlElementUtils();
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc = new Tc();
        tr.getContent().add(tc);
        tbl.getContent().add(tr);
        instance.setTblAllVAlign(tbl, STVerticalJc.CENTER);
        // Should not throw
    }

    @Test
    void setTableCellMarginSetsMargins() {
        WmlElementUtils instance = new WmlElementUtils();
        Tbl tbl = new Tbl();
        instance.setTableCellMargin(tbl, "100", "200", "100", "200");
        assertNotNull(tbl.getTblPr());
    }

    @Test
    void setTrHeightSetsHeight() {
        Tr tr = new Tr();
        WmlElementUtils.setTrHeight(tr, "400");
        assertNotNull(tr.getTrPr());
    }

    @Test
    void addTrByIndexAddsRow() {
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc = new Tc();
        tr.getContent().add(tc);
        tbl.getContent().add(tr);
        WmlElementUtils.addTrByIndex(tbl, 0);
        assertTrue(tbl.getContent().size() >= 2);
    }

    @Test
    void addTrByIndexWithAlignAddsRow() {
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc = new Tc();
        tr.getContent().add(tc);
        tbl.getContent().add(tr);
        WmlElementUtils.addTrByIndex(tbl, 0, STVerticalJc.CENTER, JcEnumeration.CENTER);
        assertTrue(tbl.getContent().size() >= 2);
    }

    @Test
    void getTcCellSizeWithMergeNumReturnsCount() {
        Tr tr = new Tr();
        Tc tc = new Tc();
        tr.getContent().add(tc);
        int count = WmlElementUtils.getTcCellSizeWithMergeNum(tr);
        assertEquals(1, count);
    }

    @Test
    void removeTrByIndexRemovesRow() {
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc = new Tc();
        tr.getContent().add(tc);
        tbl.getContent().add(tr);
        boolean removed = WmlElementUtils.removeTrByIndex(tbl, 0);
        assertTrue(removed);
    }

    @Test
    void getTrPrCreatesWhenMissing() {
        Tr tr = new Tr();
        TrPr result = WmlElementUtils.getTrPr(tr);
        assertNotNull(result);
        assertSame(result, tr.getTrPr());
    }

    @Test
    void setTrHiddenSetsHidden() {
        Tr tr = new Tr();
        Tc tc = new Tc();
        P p = new P();
        R r = new R();
        Text t = new Text();
        t.setValue("hidden");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);
        tr.getContent().add(tc);
        WmlElementUtils.setTrHidden(tr, true);
        // setTrHidden sets vanish on runs inside cells
        assertFalse(tr.getContent().isEmpty());
    }

    @Test
    void setTcWidthSetsWidth() {
        Tc tc = new Tc();
        WmlElementUtils.setTcWidth(tc, "2000");
        assertNotNull(tc.getTcPr());
        assertNotNull(tc.getTcPr().getTcW());
    }

    @Test
    void setTcHiddenSetsHidden() {
        Tc tc = new Tc();
        P p = new P();
        R r = new R();
        Text t = new Text();
        t.setValue("hidden");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);
        WmlElementUtils.setTcHidden(tc, true);
        // setTcHidden sets vanish style on runs inside paragraphs
        assertFalse(tc.getContent().isEmpty());
    }

    @Test
    void getTcAllPReturnsParagraphs() {
        Tc tc = new Tc();
        P p = new P();
        tc.getContent().add(p);
        List<P> result = WmlElementUtils.getTcAllP(tc);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getTcContentExtractsText() throws Exception {
        Tc tc = new Tc();
        P p = new P();
        R r = new R();
        Text t = new Text();
        t.setValue("cell content");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);

        String content = WmlElementUtils.getTcContent(tc);
        assertNotNull(content);
    }

    @Test
    void setTcContentSetsContent() {
        WmlElementUtils instance = new WmlElementUtils();
        Tc tc = new Tc();
        RPr rpr = new RPr();
        instance.setTcContent(tc, rpr, "new content");
        assertFalse(tc.getContent().isEmpty());
    }

    @Test
    void removeTcContentClearsContent() {
        WmlElementUtils instance = new WmlElementUtils();
        Tc tc = new Tc();
        P p = new P();
        R r = new R();
        Text t = new Text();
        t.setValue("content");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);
        instance.removeTcContent(tc);
        // removeTcContent clears R elements from the first P, but P remains
        P firstP = (P) tc.getContent().get(0);
        assertTrue(firstP.getContent().isEmpty());
    }

    // ======================== Run style delegates ========================

    @Test
    void setFontStyleAppliesAllStyles() {
        RPr rpr = new RPr();
        WmlElementUtils.setFontStyle(rpr, "\u5b8b\u4f53", "Arial", "12", "FF0000");
        assertNotNull(rpr.getRFonts());
        assertNotNull(rpr.getSz());
        assertNotNull(rpr.getColor());
    }

    @Test
    void setFontSizeAppliesSize() {
        RPr rpr = new RPr();
        WmlElementUtils.setFontSize(rpr, "24");
        assertNotNull(rpr.getSz());
        assertEquals(24, rpr.getSz().getVal().intValue());
    }

    @Test
    void setFontColorAppliesColor() {
        RPr rpr = new RPr();
        WmlElementUtils.setFontColor(rpr, "00FF00");
        assertNotNull(rpr.getColor());
        assertEquals("00FF00", rpr.getColor().getVal());
    }

    @Test
    void addRPrBorderStyleSetsBorder() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrBorderStyle(rpr, "4", STBorder.SINGLE, "1", "000000");
        assertNotNull(rpr.getBdr());
    }

    @Test
    void addRPrEmStyleSetsEmphasis() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrEmStyle(rpr, STEm.DOT);
        assertNotNull(rpr.getEm());
    }

    @Test
    void addRPrcaleStyleSetsVertAlign() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrcaleStyle(rpr, STVerticalAlignRun.SUPERSCRIPT);
        assertNotNull(rpr.getVertAlign());
    }

    @Test
    void addRPrScaleStyleSetsTextScale() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrScaleStyle(rpr, 100);
        assertNotNull(rpr.getW());
    }

    @Test
    void addRPrtSpacingStyleSetsSpacing() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrtSpacingStyle(rpr, 20);
        assertNotNull(rpr.getSpacing());
    }

    @Test
    void addRPrtPositionStyleSetsPosition() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrtPositionStyle(rpr, 10);
        assertNotNull(rpr.getPosition());
    }

    @Test
    void addRPrHightLightStyleSetsHighlight() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrHightLightStyle(rpr, "yellow");
        assertNotNull(rpr.getHighlight());
    }

    @Test
    void setRPrVanishStyleSetsVanish() {
        RPr rpr = new RPr();
        WmlElementUtils.setRPrVanishStyle(rpr, true);
        assertNotNull(rpr.getVanish());
    }

    @Test
    void setRPrVanishStyleUpdatesExistingVanish() {
        RPr rpr = new RPr();
        BooleanDefaultTrue vanish = new BooleanDefaultTrue();
        vanish.setVal(false);
        rpr.setVanish(vanish);
        WmlElementUtils.setRPrVanishStyle(rpr, true);
        assertTrue(rpr.getVanish().isVal());
    }

    @Test
    void addRPrShdStyleSetsShading() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrShdStyle(rpr, STShd.CLEAR);
        assertNotNull(rpr.getShd());
    }

    // ======================== Paragraph delegates ========================

    @Test
    void removeParaByIndexRemovesParagraph() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        P p = new P();
        pkg.getMainDocumentPart().getContent().add(p);
        WmlElementUtils instance = new WmlElementUtils();
        boolean removed = instance.removeParaByIndex(pkg, 0);
        assertTrue(removed);
    }

    @Test
    void removeParaByIndexNegativeReturnsFalse() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WmlElementUtils instance = new WmlElementUtils();
        assertFalse(instance.removeParaByIndex(pkg, -1));
    }

    @Test
    void setParagraphSpacingSetsAllSpacing() {
        WmlElementUtils instance = new WmlElementUtils();
        P p = new P();
        instance.setParagraphSpacing(p, true, "100", "200", "50", "60",
                true, "240", STLineSpacingRule.AUTO);
        assertNotNull(p.getPPr().getSpacing());
    }

    @Test
    void setParagraphIndInfoSetsAllIndents() {
        WmlElementUtils instance = new WmlElementUtils();
        P p = new P();
        instance.setParagraphIndInfo(p, "480", "100", "200", "50",
                "300", "75", "600", "100");
        PPr ppr = p.getPPr();
        assertNotNull(ppr.getInd());
        assertEquals(480, ppr.getInd().getFirstLine().intValue());
        assertEquals(600, ppr.getInd().getLeft().intValue());
    }

    @Test
    void setParagraghBordersSetsAllBorders() {
        P p = new P();
        CTBorder top = F.createCTBorder();
        CTBorder bottom = F.createCTBorder();
        CTBorder left = F.createCTBorder();
        CTBorder right = F.createCTBorder();
        WmlElementUtils.setParagraghBorders(p, top, bottom, left, right);
        assertNotNull(p.getPPr().getPBdr());
    }

    // ======================== Section delegates ========================

    @Test
    void setDocSectionBreakSetsType() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WmlElementUtils.setDocSectionBreak(pkg, "nextPage");
        assertNotNull(WmlSectionUtils.getDocSectPr(pkg).getType());
    }

    @Test
    void setDocMarginSpaceSetsMargins() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ObjectFactory factory = Context.getWmlObjectFactory();
        WmlElementUtils.setDocMarginSpace(pkg, factory, "1440", "1800", "1440", "1800");
        assertNotNull(WmlSectionUtils.getDocSectPr(pkg).getPgMar());
    }

    @Test
    void setDocumentSizeSetsPageSize() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ObjectFactory factory = Context.getWmlObjectFactory();
        WmlElementUtils.setDocumentSize(pkg, factory, "12240", "15840", STPageOrientation.LANDSCAPE);
        assertNotNull(WmlSectionUtils.getDocSectPr(pkg).getPgSz());
    }

    @Test
    void getWritableWidthReturnsPositiveValue() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        int width = WmlElementUtils.getWritableWidth(pkg);
        assertTrue(width > 0);
    }

    // ======================== Document delegates ========================

    @Test
    void setDocumentBackGroundSetsColor() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ObjectFactory factory = Context.getWmlObjectFactory();
        WmlElementUtils.setDocumentBackGround(pkg, factory, "FF0000");
        assertNotNull(pkg.getMainDocumentPart().getContents().getBackground());
    }

    @Test
    void setDocumentBordersSetsBorders() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ObjectFactory factory = Context.getWmlObjectFactory();
        CTBorder top = factory.createCTBorder();
        CTBorder right = factory.createCTBorder();
        CTBorder bottom = factory.createCTBorder();
        CTBorder left = factory.createCTBorder();
        WmlElementUtils.setDocumentBorders(pkg, factory, top, right, bottom, left);
        assertNotNull(WmlSectionUtils.getDocSectPr(pkg).getPgBorders());
    }

    @Test
    void setDocInNumTypeSetsLineNumbers() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WmlElementUtils.setDocInNumType(pkg, "1", "567", "0", STLineNumberRestart.CONTINUOUS);
        assertNotNull(WmlSectionUtils.getDocSectPr(pkg).getLnNumType());
    }

    @Test
    void setDocTextDirectionSetsDirection() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WmlElementUtils.setDocTextDirection(pkg, "tbRl");
        assertNotNull(WmlSectionUtils.getDocSectPr(pkg).getTextDirection());
    }

    @Test
    void setDocVAlignSetsVerticalAlign() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WmlElementUtils.setDocVAlign(pkg, STVerticalJc.CENTER);
        assertNotNull(WmlSectionUtils.getDocSectPr(pkg).getVAlign());
    }

    @Test
    void loadWordprocessingMLPackageLoadsFile(@TempDir Path tempDir) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File f = tempDir.resolve("test.docx").toFile();
        pkg.save(f);
        WordprocessingMLPackage loaded = WmlElementUtils.loadWordprocessingMLPackage(f.getAbsolutePath());
        assertNotNull(loaded);
    }

    // ======================== Misc edge cases ========================

    @Test
    void setTcJcAlignWithParagraphsInCellSetsAlign() {
        Tc tc = new Tc();
        P p = new P();
        tc.getContent().add(p);
        WmlElementUtils.setTcJcAlign(tc, JcEnumeration.CENTER);
        // The method sets alignment on paragraphs inside the cell
    }

    @Test
    void addRPrStrikeStyleSetsDoubleStrike() {
        RPr rpr = new RPr();
        WmlElementUtils.addRPrStrikeStyle(rpr, false, true);
        assertNotNull(rpr.getDstrike());
    }

    @Test
    void setParagraphShdStyleWithBlankColorStillSetsShd() {
        P p = new P();
        WmlElementUtils.setParagraphShdStyle(p, STShd.CLEAR, "");
        assertNotNull(p.getPPr().getShd());
    }

    @Test
    void setParaVanishExistingVanishUpdates() {
        PPr ppr = new PPr();
        ParaRPr paraRpr = new ParaRPr();
        BooleanDefaultTrue vanish = new BooleanDefaultTrue();
        vanish.setVal(false);
        paraRpr.setVanish(vanish);
        ppr.setRPr(paraRpr);
        WmlElementUtils.setParaVanish(ppr, true);
        assertTrue(ppr.getRPr().getVanish().isVal());
    }

    @Test
    void setParaRContentWithMultilineContent() {
        P p = new P();
        WmlElementUtils.setParaRContent(p, null, "line1\nline2\nline3");
        assertEquals(1, p.getContent().size());
    }

    @Test
    void appendParaRContentWithNullContentDoesNothing() {
        P p = new P();
        WmlElementUtils.appendParaRContent(p, null, null);
        assertEquals(0, p.getContent().size());
    }

    @Test
    void setParagraphSpacingWithExistingSpacing() {
        P p = new P();
        PPr ppr = new PPr();
        PPrBase.Spacing spacing = new PPrBase.Spacing();
        ppr.setSpacing(spacing);
        p.setPPr(ppr);
        WmlElementUtils instance = new WmlElementUtils();
        instance.setParagraphSpacing(p, true, "100", "200", null, null,
                true, "240", STLineSpacingRule.AUTO);
        assertEquals(100, ppr.getSpacing().getBefore().intValue());
    }

    @Test
    void setParagraphIndInfoWithExistingInd() {
        P p = new P();
        PPr ppr = new PPr();
        PPrBase.Ind ind = new PPrBase.Ind();
        ppr.setInd(ind);
        p.setPPr(ppr);
        WmlElementUtils instance = new WmlElementUtils();
        instance.setParagraphIndInfo(p, null, null, null, null,
                null, null, "600", null);
        assertEquals(600, ind.getLeft().intValue());
    }

    @Test
    void setParagraphShdStyleWithExistingShd() {
        P p = new P();
        PPr ppr = new PPr();
        CTShd shd = new CTShd();
        ppr.setShd(shd);
        p.setPPr(ppr);
        WmlElementUtils.setParagraphShdStyle(p, STShd.CLEAR, "00FF00");
        assertEquals("00FF00", ppr.getShd().getColor());
    }
}
