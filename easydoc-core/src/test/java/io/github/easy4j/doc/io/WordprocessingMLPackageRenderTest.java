package io.github.easy4j.doc.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.junit.jupiter.api.Test;

class WordprocessingMLPackageRenderTest {

    @Test
    void defaultConstructorCreatesPackage() throws Exception {
        WordprocessingMLPackageRender render = new WordprocessingMLPackageRender();
        assertNotNull(render);
        assertNotNull(render.wmlPackage);
    }

    @Test
    void packageConstructorStoresPackage() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageRender render = new WordprocessingMLPackageRender(pkg);
        assertNotNull(render);
        assertEquals(pkg, render.wmlPackage);
    }

    @Test
    void addTitleAddsStyledParagraph() throws Exception {
        WordprocessingMLPackageRender render = new WordprocessingMLPackageRender();
        int before = render.wmlPackage.getMainDocumentPart().getContent().size();
        render.addTitle("My Title");
        int after = render.wmlPackage.getMainDocumentPart().getContent().size();
        assertEquals(before + 1, after);
    }

    @Test
    void addSubtitleAddsStyledParagraph() throws Exception {
        WordprocessingMLPackageRender render = new WordprocessingMLPackageRender();
        int before = render.wmlPackage.getMainDocumentPart().getContent().size();
        render.addSubtitle("My Subtitle");
        int after = render.wmlPackage.getMainDocumentPart().getContent().size();
        assertEquals(before + 1, after);
    }

    @Test
    void addTableAppendsRowToMainDocument() throws Exception {
        WordprocessingMLPackageRender render = new WordprocessingMLPackageRender();
        int before = render.wmlPackage.getMainDocumentPart().getContent().size();
        Tr tr = new Tr();
        render.addTable(tr);
        int after = render.wmlPackage.getMainDocumentPart().getContent().size();
        assertEquals(before + 1, after);
    }

    @Test
    void addTableRowAddsRowToTable() throws Exception {
        WordprocessingMLPackageRender render = new WordprocessingMLPackageRender();
        Tr table = new Tr();
        Tr row = new Tr();
        render.addTableRow(table, row);
        assertTrue(table.getContent().contains(row));
    }

    @Test
    void addTableCellAddsCellToRow() throws Exception {
        WordprocessingMLPackageRender render = new WordprocessingMLPackageRender();
        Tr row = new Tr();
        render.addTableCell(row, "cell content");
        assertEquals(1, row.getContent().size());
        Tc tc = (Tc) row.getContent().get(0);
        assertNotNull(tc);
    }

    @Test
    void addStyledTableCellAddsCellWithStyle() throws Exception {
        WordprocessingMLPackageRender render = new WordprocessingMLPackageRender();
        Tr row = new Tr();
        render.addStyledTableCell(row, "styled", true, "24");
        assertEquals(1, row.getContent().size());
        Tc tc = (Tc) row.getContent().get(0);
        assertNotNull(tc);
    }

    @Test
    void addStylingSetsContentAndBold() throws Exception {
        WordprocessingMLPackageRender render = new WordprocessingMLPackageRender();
        Tc cell = new Tc();
        render.addStyling(cell, "hello", true, "12");
        assertTrue(cell.getContent().size() > 0);
    }

    @Test
    void addStylingWithoutBoldOrFontSize() throws Exception {
        WordprocessingMLPackageRender render = new WordprocessingMLPackageRender();
        Tc cell = new Tc();
        render.addStyling(cell, "plain", false, null);
        assertTrue(cell.getContent().size() > 0);
    }

    @Test
    void addStylingWithEmptyFontSize() throws Exception {
        WordprocessingMLPackageRender render = new WordprocessingMLPackageRender();
        Tc cell = new Tc();
        render.addStyling(cell, "text", false, "");
        assertTrue(cell.getContent().size() > 0);
    }

    @Test
    void setFontSizeSetsSzAndSzCs() {
        WordprocessingMLPackageRender render = createRender();
        RPr rpr = new RPr();
        render.setFontSize(rpr, "24");
        assertNotNull(rpr.getSz());
        assertEquals(new BigInteger("24"), rpr.getSz().getVal());
        assertEquals(rpr.getSz(), rpr.getSzCs());
    }

    @Test
    void addBoldStyleSetsBTrue() {
        WordprocessingMLPackageRender render = createRender();
        RPr rpr = new RPr();
        render.addBoldStyle(rpr);
        assertNotNull(rpr.getB());
        assertEquals(Boolean.TRUE, rpr.getB().isVal());
    }

    @Test
    void addBordersSetsTblBorders() {
        WordprocessingMLPackageRender render = createRender();
        Tbl table = new Tbl();
        render.addBorders(table);
        TblPr tblPr = table.getTblPr();
        assertNotNull(tblPr);
        TblBorders borders = tblPr.getTblBorders();
        assertNotNull(borders);
        assertNotNull(borders.getBottom());
        assertNotNull(borders.getTop());
        assertNotNull(borders.getLeft());
        assertNotNull(borders.getRight());
        assertNotNull(borders.getInsideH());
        assertNotNull(borders.getInsideV());
    }

    @Test
    void addTableRowWithMergedCellsAddsRow() {
        WordprocessingMLPackageRender render = createRender();
        Tbl table = new Tbl();
        render.addTableRowWithMergedCells("merged", "f1", "f2", table);
        assertEquals(1, table.getContent().size());
    }

    @Test
    void addMergedColumnWithContent() {
        WordprocessingMLPackageRender render = createRender();
        Tr row = new Tr();
        render.addMergedColumn(row, "merged content");
        assertEquals(1, row.getContent().size());
    }

    @Test
    void addMergedColumnWithNullContent() {
        WordprocessingMLPackageRender render = createRender();
        Tr row = new Tr();
        render.addMergedColumn(row, null);
        assertEquals(1, row.getContent().size());
    }

    @Test
    void addMergedCellWithRestartVal() {
        WordprocessingMLPackageRender render = createRender();
        Tr row = new Tr();
        render.addMergedCell(row, "content", "restart");
        assertEquals(1, row.getContent().size());
        Tc tc = (Tc) row.getContent().get(0);
        assertNotNull(tc.getTcPr());
        assertNotNull(tc.getTcPr().getVMerge());
        assertEquals("restart", tc.getTcPr().getVMerge().getVal());
    }

    @Test
    void addMergedCellWithNullVal() {
        WordprocessingMLPackageRender render = createRender();
        Tr row = new Tr();
        render.addMergedCell(row, "content", null);
        assertEquals(1, row.getContent().size());
        Tc tc = (Tc) row.getContent().get(0);
        assertNotNull(tc.getTcPr());
        assertNotNull(tc.getTcPr().getVMerge());
    }

    @Test
    void addMergedCellWithNullContent() {
        WordprocessingMLPackageRender render = createRender();
        Tr row = new Tr();
        render.addMergedCell(row, null, "restart");
        assertEquals(1, row.getContent().size());
    }

    @Test
    void addTableCellWithWidthAddsCell() {
        WordprocessingMLPackageRender render = createRender();
        Tr row = new Tr();
        render.addTableCellWithWidth(row, "content", 2000);
        assertEquals(1, row.getContent().size());
    }

    @Test
    void addTableCellWithWidthZeroDoesNotSetWidth() {
        WordprocessingMLPackageRender render = createRender();
        Tr row = new Tr();
        render.addTableCellWithWidth(row, "content", 0);
        assertEquals(1, row.getContent().size());
        Tc tc = (Tc) row.getContent().get(0);
        // width 0 means setCellWidth is not called
        assertEquals(null, tc.getTcPr());
    }

    @Test
    void setCellWidthSetsWidthOnCell() {
        WordprocessingMLPackageRender render = createRender();
        Tc cell = new Tc();
        render.setCellWidth(cell, 5000);
        TcPr tcPr = cell.getTcPr();
        assertNotNull(tcPr);
        assertNotNull(tcPr.getTcW());
        assertEquals(BigInteger.valueOf(5000), tcPr.getTcW().getW());
    }

    @Test
    void addImageToPackageAddsParagraphWithImage() throws Exception {
        WordprocessingMLPackageRender render = new WordprocessingMLPackageRender();
        // 1x1 red PNG
        byte[] png = createMinimalPng();
        int before = render.wmlPackage.getMainDocumentPart().getContent().size();
        render.addImageToPackage(png);
        int after = render.wmlPackage.getMainDocumentPart().getContent().size();
        assertEquals(before + 1, after);
    }

    /**
     * Minimal 1x1 red PNG (67 bytes).
     */
    private static byte[] createMinimalPng() {
        // A minimal valid PNG: 1x1 pixel, red
        return new byte[] {
            (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, // IHDR chunk
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, // 1x1
            0x08, 0x02, 0x00, 0x00, 0x00, (byte)0x90, 0x77, 0x53, (byte)0xDE,
            0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41, 0x54, // IDAT chunk
            0x08, (byte)0xD7, 0x63, (byte)0xF8, (byte)0xCF, (byte)0xC0, 0x00, 0x00,
            0x00, 0x02, 0x00, 0x01, (byte)0xE2, 0x21, (byte)0xBC, 0x33,
            0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, // IEND chunk
            (byte)0xAE, 0x42, 0x60, (byte)0x82
        };
    }

    private static WordprocessingMLPackageRender createRender() {
        try {
            return new WordprocessingMLPackageRender();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
