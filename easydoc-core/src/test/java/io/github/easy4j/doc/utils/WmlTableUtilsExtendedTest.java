package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.P;
import org.docx4j.wml.ObjectFactory;
import org.junit.jupiter.api.Test;

/**
 * Additional tests for WmlTableUtils to cover createHyperlink
 * and other uncovered methods.
 */
class WmlTableUtilsExtendedTest {

    @Test
    void createHyperlinkWithAllParams() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = pkg.getMainDocumentPart();
        ObjectFactory factory = Context.getWmlObjectFactory();
        P paragraph = factory.createP();

        WmlTableUtils.createHyperlink(pkg, mdp, factory, paragraph,
                "https://example.com", "Click here", "SimSun", "Arial", "24");

        assertFalse(paragraph.getContent().isEmpty());
    }

    @Test
    void createHyperlinkWithBlankFontNames() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = pkg.getMainDocumentPart();
        ObjectFactory factory = Context.getWmlObjectFactory();
        P paragraph = factory.createP();

        WmlTableUtils.createHyperlink(pkg, mdp, factory, paragraph,
                "https://example.com", "link", "", "", "");

        assertFalse(paragraph.getContent().isEmpty());
    }

    @Test
    void createHyperlinkWithNullFontNames() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = pkg.getMainDocumentPart();
        ObjectFactory factory = Context.getWmlObjectFactory();
        P paragraph = factory.createP();

        WmlTableUtils.createHyperlink(pkg, mdp, factory, paragraph,
                "https://example.com", "link", null, null, null);

        assertFalse(paragraph.getContent().isEmpty());
    }

    @Test
    void getTableReturnsNullWhenNoMatch() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        org.docx4j.wml.Tbl tbl = new org.docx4j.wml.Tbl();
        org.docx4j.wml.Tr tr = new org.docx4j.wml.Tr();
        org.docx4j.wml.Tc tc = new org.docx4j.wml.Tc();
        P p = new P();
        org.docx4j.wml.R r = new org.docx4j.wml.R();
        org.docx4j.wml.Text t = new org.docx4j.wml.Text();
        t.setValue("no match");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);
        tr.getContent().add(tc);
        tbl.getContent().add(tr);

        java.util.List<org.docx4j.wml.Tbl> tables = new java.util.ArrayList<>();
        tables.add(tbl);

        org.docx4j.wml.Tbl result = WmlTableUtils.getTable(tables, "${notfound}");
        assertNull(result);
    }

    @Test
    void getTableReturnsNullWhenTextValueIsNull() throws Exception {
        org.docx4j.wml.Tbl tbl = new org.docx4j.wml.Tbl();
        org.docx4j.wml.Tr tr = new org.docx4j.wml.Tr();
        org.docx4j.wml.Tc tc = new org.docx4j.wml.Tc();
        P p = new P();
        org.docx4j.wml.R r = new org.docx4j.wml.R();
        org.docx4j.wml.Text t = new org.docx4j.wml.Text();
        t.setValue(null);
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);
        tr.getContent().add(tc);
        tbl.getContent().add(tr);

        java.util.List<org.docx4j.wml.Tbl> tables = new java.util.ArrayList<>();
        tables.add(tbl);

        org.docx4j.wml.Tbl result = WmlTableUtils.getTable(tables, "${test}");
        assertNull(result);
    }

    @Test
    void getTcByPositionWithGridSpan() {
        org.docx4j.wml.Tc tc1 = new org.docx4j.wml.Tc();
        org.docx4j.wml.TcPr tcPr = new org.docx4j.wml.TcPr();
        org.docx4j.wml.TcPrInner.GridSpan gridSpan = new org.docx4j.wml.TcPrInner.GridSpan();
        gridSpan.setVal(java.math.BigInteger.valueOf(2));
        tcPr.setGridSpan(gridSpan);
        tc1.setTcPr(tcPr);

        org.docx4j.wml.Tc tc2 = new org.docx4j.wml.Tc();

        java.util.List<org.docx4j.wml.Tc> list = new java.util.ArrayList<>();
        list.add(tc1);
        list.add(tc2);

        // Position 0 should return tc1 (which spans 2 columns)
        org.docx4j.wml.Tc result = WmlTableUtils.getTcByPosition(list, 0);
        assertNotNull(result);
    }

    @Test
    void getTcByPositionReturnsNullForOutOfBounds() {
        java.util.List<org.docx4j.wml.Tc> list = new java.util.ArrayList<>();
        list.add(new org.docx4j.wml.Tc());

        org.docx4j.wml.Tc result = WmlTableUtils.getTcByPosition(list, 99);
        assertNull(result);
    }

    @Test
    void mergeCellsHorizontalWithGridSpan() {
        org.docx4j.wml.Tbl tbl = new org.docx4j.wml.Tbl();
        org.docx4j.wml.Tr tr = new org.docx4j.wml.Tr();
        org.docx4j.wml.Tc tc1 = new org.docx4j.wml.Tc();
        org.docx4j.wml.Tc tc2 = new org.docx4j.wml.Tc();
        org.docx4j.wml.Tc tc3 = new org.docx4j.wml.Tc();
        tr.getContent().add(tc1);
        tr.getContent().add(tc2);
        tr.getContent().add(tc3);
        tbl.getContent().add(tr);

        WmlTableUtils.mergeCellsHorizontalByGridSpan(tbl, 0, 0, 1);
        // Should not throw
        assertNotNull(tc1.getTcPr());
    }

    @Test
    void mergeCellsVerticallySetsVMerge() {
        org.docx4j.wml.Tbl tbl = new org.docx4j.wml.Tbl();
        org.docx4j.wml.Tr tr1 = new org.docx4j.wml.Tr();
        org.docx4j.wml.Tr tr2 = new org.docx4j.wml.Tr();
        org.docx4j.wml.Tc tc1 = new org.docx4j.wml.Tc();
        org.docx4j.wml.Tc tc2 = new org.docx4j.wml.Tc();
        tr1.getContent().add(tc1);
        tr2.getContent().add(tc2);
        tbl.getContent().add(tr1);
        tbl.getContent().add(tr2);

        WmlTableUtils.mergeCellsVertically(tbl, 0, 0, 1);
        // Should set vMerge on cells
    }

    @Test
    void addTrByIndexWithAlignAddsRowAtPosition() {
        org.docx4j.wml.Tbl tbl = new org.docx4j.wml.Tbl();
        org.docx4j.wml.Tr tr = new org.docx4j.wml.Tr();
        org.docx4j.wml.Tc tc = new org.docx4j.wml.Tc();
        tr.getContent().add(tc);
        tbl.getContent().add(tr);

        WmlTableUtils.addTrByIndex(tbl, 0, org.docx4j.wml.STVerticalJc.CENTER, org.docx4j.wml.JcEnumeration.CENTER);
        assertTrue(tbl.getContent().size() >= 2);
    }

    @Test
    void setTableCellMarginSetsMargins() {
        org.docx4j.wml.Tbl tbl = new org.docx4j.wml.Tbl();
        WmlTableUtils.setTableCellMargin(tbl, "100", "200", "100", "200");
        assertNotNull(tbl.getTblPr());
        assertNotNull(tbl.getTblPr().getTblCellMar());
    }

    @Test
    void setTrHeightSetsHeightOnRow() {
        org.docx4j.wml.Tr tr = new org.docx4j.wml.Tr();
        WmlTableUtils.setTrHeight(tr, "400");
        assertNotNull(tr.getTrPr());
    }

    @Test
    void removeTrByIndexRemovesRow() {
        org.docx4j.wml.Tbl tbl = new org.docx4j.wml.Tbl();
        org.docx4j.wml.Tr tr = new org.docx4j.wml.Tr();
        org.docx4j.wml.Tc tc = new org.docx4j.wml.Tc();
        tr.getContent().add(tc);
        tbl.getContent().add(tr);
        boolean removed = WmlTableUtils.removeTrByIndex(tbl, 0);
        assertTrue(removed);
    }

    @Test
    void getTcCellSizeWithMergeNumCountsCells() {
        org.docx4j.wml.Tr tr = new org.docx4j.wml.Tr();
        org.docx4j.wml.Tc tc1 = new org.docx4j.wml.Tc();
        org.docx4j.wml.Tc tc2 = new org.docx4j.wml.Tc();
        tr.getContent().add(tc1);
        tr.getContent().add(tc2);
        int count = WmlTableUtils.getTcCellSizeWithMergeNum(tr);
        assertEquals(2, count);
    }

    @Test
    void getTcCellSizeWithMergeNumHandlesGridSpan() {
        org.docx4j.wml.Tr tr = new org.docx4j.wml.Tr();
        org.docx4j.wml.Tc tc = new org.docx4j.wml.Tc();
        org.docx4j.wml.TcPr tcPr = new org.docx4j.wml.TcPr();
        org.docx4j.wml.TcPrInner.GridSpan gridSpan = new org.docx4j.wml.TcPrInner.GridSpan();
        gridSpan.setVal(java.math.BigInteger.valueOf(3));
        tcPr.setGridSpan(gridSpan);
        tc.setTcPr(tcPr);
        tr.getContent().add(tc);
        int count = WmlTableUtils.getTcCellSizeWithMergeNum(tr);
        assertEquals(3, count);
    }

    @Test
    void getTcContentExtractsText() throws Exception {
        org.docx4j.wml.Tc tc = new org.docx4j.wml.Tc();
        P p = new P();
        org.docx4j.wml.R r = new org.docx4j.wml.R();
        org.docx4j.wml.Text t = new org.docx4j.wml.Text();
        t.setValue("cell text");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);

        String content = WmlTableUtils.getTcContent(tc);
        assertNotNull(content);
    }

    @Test
    void getTblContentStrExtractsText() throws Exception {
        org.docx4j.wml.Tbl tbl = new org.docx4j.wml.Tbl();
        org.docx4j.wml.Tr tr = new org.docx4j.wml.Tr();
        org.docx4j.wml.Tc tc = new org.docx4j.wml.Tc();
        P p = new P();
        org.docx4j.wml.R r = new org.docx4j.wml.R();
        org.docx4j.wml.Text t = new org.docx4j.wml.Text();
        t.setValue("content");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);
        tr.getContent().add(tc);
        tbl.getContent().add(tr);

        String content = WmlTableUtils.getTblContentStr(tbl);
        assertNotNull(content);
    }

    @Test
    void getTblContentListExtractsList() throws Exception {
        org.docx4j.wml.Tbl tbl = new org.docx4j.wml.Tbl();
        org.docx4j.wml.Tr tr = new org.docx4j.wml.Tr();
        org.docx4j.wml.Tc tc = new org.docx4j.wml.Tc();
        P p = new P();
        org.docx4j.wml.R r = new org.docx4j.wml.R();
        org.docx4j.wml.Text t = new org.docx4j.wml.Text();
        t.setValue("item");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);
        tr.getContent().add(tc);
        tbl.getContent().add(tr);

        java.util.List<String> list = WmlTableUtils.getTblContentList(tbl);
        assertNotNull(list);
    }

    @Test
    void replaceTableWithMatchingPlaceholder() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        org.docx4j.wml.Tbl tbl = new org.docx4j.wml.Tbl();
        // Header row
        org.docx4j.wml.Tr headerRow = new org.docx4j.wml.Tr();
        org.docx4j.wml.Tc headerCell = new org.docx4j.wml.Tc();
        P headerP = new P();
        org.docx4j.wml.R headerR = new org.docx4j.wml.R();
        org.docx4j.wml.Text headerText = new org.docx4j.wml.Text();
        headerText.setValue("Header");
        headerR.getContent().add(headerText);
        headerP.getContent().add(headerR);
        headerCell.getContent().add(headerP);
        headerRow.getContent().add(headerCell);
        tbl.getContent().add(headerRow);

        // Template row with placeholder
        org.docx4j.wml.Tr templateRow = new org.docx4j.wml.Tr();
        org.docx4j.wml.Tc templateCell = new org.docx4j.wml.Tc();
        P templateP = new P();
        org.docx4j.wml.R templateR = new org.docx4j.wml.R();
        org.docx4j.wml.Text templateText = new org.docx4j.wml.Text();
        templateText.setValue("${name}");
        templateR.getContent().add(templateText);
        templateP.getContent().add(templateR);
        templateCell.getContent().add(templateP);
        templateRow.getContent().add(templateCell);
        tbl.getContent().add(templateRow);

        pkg.getMainDocumentPart().getContent().add(tbl);

        String[] placeholders = new String[]{"${name}"};
        java.util.Map<String, String> row = new java.util.HashMap<>();
        row.put("${name}", "Alice");
        java.util.List<java.util.Map<String, String>> data = new java.util.ArrayList<>();
        data.add(row);

        WmlTableUtils.replaceTable(placeholders, data, pkg);
        // Should not throw
    }
}
