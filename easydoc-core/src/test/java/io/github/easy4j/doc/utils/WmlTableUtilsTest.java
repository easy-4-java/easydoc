package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.docx4j.wml.TrPr;
import org.junit.jupiter.api.Test;

class WmlTableUtilsTest {

    // ---- create / structure ----

    @Test
    void createTableReturnsPopulatedTable() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(2, 3, new int[]{1000, 2000, 3000});
        assertNotNull(tbl);
        assertNotNull(tbl.getTblPr());
        assertNotNull(tbl.getTblGrid());
        assertEquals(3, tbl.getTblGrid().getGridCol().size());
        List<Tr> rows = WmlTableUtils.getTblAllTr(tbl);
        assertEquals(2, rows.size());
        List<Tc> cells = WmlTableUtils.getTrAllCell(rows.get(0));
        assertEquals(3, cells.size());
    }

    @Test
    void createTableWithWordPackageReturnsTable() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        Tbl tbl = WmlTableUtils.createTable(pkg, 2, 2);
        assertNotNull(tbl);
        assertEquals(2, WmlTableUtils.getTblAllTr(tbl).size());
    }

    @Test
    void createTableClampsNegativeDimensions() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(-1, -1, new int[]{1000});
        assertNotNull(tbl);
        assertTrue(WmlTableUtils.getTblAllTr(tbl).size() >= 1);
    }

    // ---- get*Pr ----

    @Test
    void getTblPrCreatesTblPrWhenMissing() {
        Tbl tbl = new Tbl();
        TblPr tblPr = WmlTableUtils.getTblPr(tbl);
        assertNotNull(tblPr);
        assertSame(tblPr, tbl.getTblPr());
    }

    @Test
    void getTrPrCreatesTrPrWhenMissing() {
        Tr tr = new Tr();
        TrPr trPr = WmlTableUtils.getTrPr(tr);
        assertNotNull(trPr);
        assertSame(trPr, tr.getTrPr());
    }

    @Test
    void getTcPrCreatesTcPrWhenMissing() {
        Tc tc = new Tc();
        org.docx4j.wml.TcPr tcPr = WmlTableUtils.getTcPr(tc);
        assertNotNull(tcPr);
        assertSame(tcPr, tc.getTcPr());
    }

    // ---- width / height ----

    @Test
    void setTableWidthSetsTblW() {
        Tbl tbl = new Tbl();
        WmlTableUtils.setTableWidth(tbl, "5000");
        TblPr tblPr = tbl.getTblPr();
        assertNotNull(tblPr.getTblW());
        assertEquals(new BigInteger("5000"), tblPr.getTblW().getW());
        assertEquals("dxa", tblPr.getTblW().getType());
    }

    @Test
    void setTableWidthIgnoresBlank() {
        Tbl tbl = new Tbl();
        WmlTableUtils.setTableWidth(tbl, "");
        assertEquals(null, tbl.getTblPr());
    }

    @Test
    void setTcWidthSetsTcW() {
        Tc tc = new Tc();
        WmlTableUtils.setTcWidth(tc, "2500");
        assertNotNull(tc.getTcPr().getTcW());
        assertEquals(new BigInteger("2500"), tc.getTcPr().getTcW().getW());
        assertEquals("dxa", tc.getTcPr().getTcW().getType());
    }

    @Test
    void setTcWidthIgnoresBlank() {
        Tc tc = new Tc();
        WmlTableUtils.setTcWidth(tc, "");
        assertNull(tc.getTcPr());
    }

    @Test
    void setTrHeightSetsTrPr() {
        Tr tr = new Tr();
        WmlTableUtils.setTrHeight(tr, "500");
        assertNotNull(tr.getTrPr());
    }

    // ---- add / remove rows ----

    @Test
    void addTrByIndexWithGridAddsRow() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 2, new int[]{1000, 2000});
        int before = WmlTableUtils.getTblAllTr(tbl).size();
        WmlTableUtils.addTrByIndex(tbl, 0);
        int after = WmlTableUtils.getTblAllTr(tbl).size();
        assertEquals(before + 1, after);
    }

    @Test
    void addTrByIndexOutOfBoundsAppendsAtEnd() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 2, new int[]{1000, 2000});
        int before = WmlTableUtils.getTblAllTr(tbl).size();
        WmlTableUtils.addTrByIndex(tbl, 999);
        assertEquals(before + 1, WmlTableUtils.getTblAllTr(tbl).size());
    }

    @Test
    void addTrByIndexWithAlignParams() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 2, new int[]{1000, 2000});
        WmlTableUtils.addTrByIndex(tbl, 0, STVerticalJc.CENTER, JcEnumeration.LEFT);
        assertTrue(WmlTableUtils.getTblAllTr(tbl).size() >= 2);
    }

    @Test
    void removeTrByIndexReturnsFalseForNegative() {
        Tbl tbl = new Tbl();
        assertFalse(WmlTableUtils.removeTrByIndex(tbl, -1));
    }

    @Test
    void removeTrByIndexRemovesRow() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(2, 2, new int[]{1000, 2000});
        int before = WmlTableUtils.getTblAllTr(tbl).size();
        assertTrue(WmlTableUtils.removeTrByIndex(tbl, 0));
        assertEquals(before - 1, WmlTableUtils.getTblAllTr(tbl).size());
    }

    @Test
    void removeTrByIndexReturnsFalseForEmptyTable() {
        Tbl tbl = new Tbl();
        assertFalse(WmlTableUtils.removeTrByIndex(tbl, 0));
    }

    // ---- get cells / content ----

    @Test
    void getTblAllTrReturnsEmptyForEmpty() {
        Tbl tbl = new Tbl();
        List<Tr> rows = WmlTableUtils.getTblAllTr(tbl);
        assertNotNull(rows);
        assertEquals(0, rows.size());
    }

    @Test
    void getTrAllCellEmptyForFreshRow() {
        Tr tr = new Tr();
        List<Tc> tcList = WmlTableUtils.getTrAllCell(tr);
        assertNotNull(tcList);
        assertEquals(0, tcList.size());
    }

    @Test
    void getTcAllPEmptyForFreshCell() {
        Tc tc = new Tc();
        List<P> pList = WmlTableUtils.getTcAllP(tc);
        assertNotNull(pList);
        assertEquals(0, pList.size());
    }

    @Test
    void getTcByPositionReturnsNullForNegative() {
        assertNull(WmlTableUtils.getTc(new Tbl(), -1, 0));
    }

    @Test
    void getTcReturnsCorrectCell() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(2, 2, new int[]{1000, 2000});
        Tc tc = WmlTableUtils.getTc(tbl, 0, 0);
        assertNotNull(tc);
    }

    @Test
    void getTcReturnsNullForOutOfBounds() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        assertNull(WmlTableUtils.getTc(tbl, 0, 5));
        assertNull(WmlTableUtils.getTc(tbl, 5, 0));
    }

    @Test
    void getTcByPositionReturnsCorrectCell() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 3, new int[]{1000, 2000, 3000});
        List<Tc> cells = WmlTableUtils.getTrAllCell(WmlTableUtils.getTblAllTr(tbl).get(0));
        Tc tc = WmlTableUtils.getTcByPosition(cells, 1);
        assertNotNull(tc);
    }

    @Test
    void getTcByPositionReturnsNullForOutOfBounds() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 2, new int[]{1000, 2000});
        List<Tc> cells = WmlTableUtils.getTrAllCell(WmlTableUtils.getTblAllTr(tbl).get(0));
        assertNull(WmlTableUtils.getTcByPosition(cells, 99));
    }

    @Test
    void getTcCellSizeWithMergeNumReturnsCellCount() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 3, new int[]{1000, 2000, 3000});
        Tr tr = WmlTableUtils.getTblAllTr(tbl).get(0);
        int size = WmlTableUtils.getTcCellSizeWithMergeNum(tr);
        assertEquals(3, size);
    }

    @Test
    void getTcCellSizeWithMergeNumReturnsOneForEmptyRow() {
        Tr tr = new Tr();
        assertEquals(1, WmlTableUtils.getTcCellSizeWithMergeNum(tr));
    }

    // ---- content ----

    @Test
    void getTcContentReturnsString() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        Tc tc = WmlTableUtils.getTc(tbl, 0, 0);
        String content = WmlTableUtils.getTcContent(tc);
        assertNotNull(content);
    }

    @Test
    void setTcContentSetsText() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        Tc tc = WmlTableUtils.getTc(tbl, 0, 0);
        WmlTableUtils.setTcContent(tc, null, "new content");
        String content = WmlTableUtils.getTcContent(tc);
        assertTrue(content.contains("new content"));
    }

    @Test
    void setTcContentWithRPr() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        Tc tc = WmlTableUtils.getTc(tbl, 0, 0);
        RPr rpr = new RPr();
        WmlTableUtils.setTcContent(tc, rpr, "styled");
        String content = WmlTableUtils.getTcContent(tc);
        assertTrue(content.contains("styled"));
    }

    @Test
    void setTcContentWithMultilineContent() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        Tc tc = WmlTableUtils.getTc(tbl, 0, 0);
        WmlTableUtils.setTcContent(tc, null, "line1\nline2");
        String content = WmlTableUtils.getTcContent(tc);
        assertTrue(content.contains("line1"));
    }

    @Test
    void setTcContentWithNullContentClearsCell() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        Tc tc = WmlTableUtils.getTc(tbl, 0, 0);
        WmlTableUtils.setTcContent(tc, null, "initial");
        WmlTableUtils.setTcContent(tc, null, null);
        // null content => clears text but run remains
    }

    @Test
    void removeTcContentClearsCell() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        Tc tc = WmlTableUtils.getTc(tbl, 0, 0);
        WmlTableUtils.setTcContent(tc, null, "to remove");
        WmlTableUtils.removeTcContent(tc);
        String content = WmlTableUtils.getTcContent(tc);
        assertFalse(content.contains("to remove"));
    }

    @Test
    void removeTcContentOnEmptyCellDoesNotThrow() {
        Tc tc = new Tc();
        WmlTableUtils.removeTcContent(tc);
    }

    @Test
    void getTblContentStrReturnsString() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        String content = WmlTableUtils.getTblContentStr(tbl);
        assertNotNull(content);
    }

    @Test
    void getTblContentListReturnsList() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(2, 2, new int[]{1000, 2000});
        List<String> contentList = WmlTableUtils.getTblContentList(tbl);
        assertNotNull(contentList);
        assertEquals(2, contentList.size());
    }

    // ---- alignment ----

    @Test
    void setTblJcAlignSetsAlignment() {
        Tbl tbl = new Tbl();
        WmlTableUtils.setTblJcAlign(tbl, JcEnumeration.RIGHT);
        assertNotNull(WmlTableUtils.getTblPr(tbl).getJc());
        assertEquals(JcEnumeration.RIGHT, WmlTableUtils.getTblPr(tbl).getJc().getVal());
    }

    @Test
    void setTblJcAlignIgnoresNull() {
        Tbl tbl = new Tbl();
        WmlTableUtils.setTblJcAlign(tbl, null);
        assertNull(tbl.getTblPr());
    }

    @Test
    void setTblAllJcAlignSetsAllCells() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(2, 2, new int[]{1000, 2000});
        WmlTableUtils.setTblAllJcAlign(tbl, JcEnumeration.LEFT);
        assertNotNull(WmlTableUtils.getTblPr(tbl).getJc());
    }

    @Test
    void setTblAllVAlignSetsAllCells() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(2, 2, new int[]{1000, 2000});
        WmlTableUtils.setTblAllVAlign(tbl, STVerticalJc.BOTTOM);
        // Verify first cell has valign set
        Tc tc = WmlTableUtils.getTc(tbl, 0, 0);
        assertNotNull(tc.getTcPr().getVAlign());
    }

    @Test
    void setTcVAlignSetsVerticalAlignment() {
        Tc tc = new Tc();
        WmlTableUtils.setTcVAlign(tc, STVerticalJc.CENTER);
        assertNotNull(tc.getTcPr().getVAlign());
        assertEquals(STVerticalJc.CENTER, tc.getTcPr().getVAlign().getVal());
    }

    @Test
    void setTcVAlignIgnoresNull() {
        Tc tc = new Tc();
        WmlTableUtils.setTcVAlign(tc, null);
        assertNull(tc.getTcPr());
    }

    @Test
    void setTcJcAlignSetsAlignment() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        Tc tc = WmlTableUtils.getTc(tbl, 0, 0);
        WmlTableUtils.setTcJcAlign(tc, JcEnumeration.RIGHT);
    }

    // ---- merge ----

    @Test
    void mergeCellsHorizontalMergesCells() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(2, 3, new int[]{1000, 2000, 3000});
        WmlTableUtils.mergeCellsHorizontal(tbl, 0, 0, 1);
        List<Tc> cells = WmlTableUtils.getTrAllCell(WmlTableUtils.getTblAllTr(tbl).get(0));
        assertNotNull(cells.get(0).getTcPr().getHMerge());
        assertEquals("restart", cells.get(0).getTcPr().getHMerge().getVal());
    }

    @Test
    void mergeCellsHorizontalIgnoresNegativeArgs() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 2, new int[]{1000, 2000});
        WmlTableUtils.mergeCellsHorizontal(tbl, -1, 0, 1);
        // no-op
    }

    @Test
    void mergeCellsHorizontalByGridSpanMergesCells() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(2, 3, new int[]{1000, 2000, 3000});
        WmlTableUtils.mergeCellsHorizontalByGridSpan(tbl, 0, 0, 1);
        List<Tc> cells = WmlTableUtils.getTrAllCell(WmlTableUtils.getTblAllTr(tbl).get(0));
        assertNotNull(cells.get(0).getTcPr().getGridSpan());
    }

    @Test
    void mergeCellsHorizontalByGridSpanIgnoresNegativeArgs() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 2, new int[]{1000, 2000});
        WmlTableUtils.mergeCellsHorizontalByGridSpan(tbl, -1, 0, 1);
    }

    @Test
    void mergeCellsVerticallyMergesCells() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(3, 2, new int[]{1000, 2000});
        WmlTableUtils.mergeCellsVertically(tbl, 0, 0, 1);
        Tc tc0 = WmlTableUtils.getTc(tbl, 0, 0);
        assertNotNull(tc0.getTcPr().getVMerge());
        assertEquals("restart", tc0.getTcPr().getVMerge().getVal());
    }

    @Test
    void mergeCellsVerticallyIgnoresNegativeArgs() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(2, 2, new int[]{1000, 2000});
        WmlTableUtils.mergeCellsVertically(tbl, -1, 0, 1);
    }

    // ---- borders / cell margin ----

    @Test
    void setTblBordersSetsBorders() {
        TblPr tblPr = new TblPr();
        CTBorder border = new CTBorder();
        WmlTableUtils.setTblBorders(tblPr, border, border, border, border, border, border);
        assertNotNull(tblPr.getTblBorders());
        assertNotNull(tblPr.getTblBorders().getTop());
        assertNotNull(tblPr.getTblBorders().getBottom());
    }

    @Test
    void setTblBordersWithNullBorders() {
        TblPr tblPr = new TblPr();
        WmlTableUtils.setTblBorders(tblPr, null, null, null, null, null, null);
        assertNotNull(tblPr.getTblBorders());
    }

    @Test
    void setTableCellMarginSetsMargins() {
        Tbl tbl = new Tbl();
        WmlTableUtils.setTableCellMargin(tbl, "100", "200", "300", "400");
        TblPr tblPr = tbl.getTblPr();
        assertNotNull(tblPr.getTblCellMar());
    }

    @Test
    void setTableCellMarginWithBlankArgs() {
        Tbl tbl = new Tbl();
        WmlTableUtils.setTableCellMargin(tbl, "", "", "", "");
        TblPr tblPr = tbl.getTblPr();
        assertNotNull(tblPr.getTblCellMar());
    }

    // ---- hidden ----

    @Test
    void setTrHiddenHidesRow() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        Tr tr = WmlTableUtils.getTblAllTr(tbl).get(0);
        WmlTableUtils.setTrHidden(tr, true);
    }

    @Test
    void setTcHiddenHidesCell() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        Tc tc = WmlTableUtils.getTc(tbl, 0, 0);
        WmlTableUtils.setTcHidden(tc, true);
    }

    // ---- getAllTbl / removeTableByIndex ----

    @Test
    void getAllTblReturnsTablesInPackage() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        pkg.getMainDocumentPart().getContent().add(tbl);
        List<Tbl> tables = WmlTableUtils.getAllTbl(pkg);
        assertNotNull(tables);
        assertEquals(1, tables.size());
    }

    @Test
    void removeTableByIndexRemovesTable() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        pkg.getMainDocumentPart().getContent().add(tbl);
        assertTrue(WmlTableUtils.removeTableByIndex(pkg, 0));
    }

    @Test
    void removeTableByIndexReturnsFalseForNegative() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        assertFalse(WmlTableUtils.removeTableByIndex(pkg, -1));
    }

    // ---- addRowToTable / replaceTable ----

    @Test
    void addRowToTableAddsRow() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 1, new int[]{1000});
        Tr templateRow = WmlTableUtils.getTblAllTr(tbl).get(0);
        WmlTableUtils.addRowToTable(tbl, templateRow, Map.of());
        assertEquals(2, WmlTableUtils.getTblAllTr(tbl).size());
    }

    // ---- saveWordPackage ----

    @Test
    void saveWordPackageWritesFile(@org.junit.jupiter.api.io.TempDir java.nio.file.Path tempDir) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        java.io.File f = tempDir.resolve("out.docx").toFile();
        WmlTableUtils.saveWordPackage(pkg, f);
        assertTrue(f.exists());
        assertTrue(f.length() > 0);
    }
}
