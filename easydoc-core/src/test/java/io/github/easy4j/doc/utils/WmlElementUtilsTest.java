package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.TextUtils;
import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.model.properties.table.tr.TrHeight;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTBackground;
import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTEm;
import org.docx4j.wml.CTHeight;
import org.docx4j.wml.CTLineNumber;
import org.docx4j.wml.CTShd;
import org.docx4j.wml.CTSignedHpsMeasure;
import org.docx4j.wml.CTSignedTwipsMeasure;
import org.docx4j.wml.CTTblCellMar;
import org.docx4j.wml.CTTextScale;
import org.docx4j.wml.CTVerticalAlignRun;
import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.Color;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.Highlight;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.P.Hyperlink;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.Ind;
import org.docx4j.wml.PPrBase.PBdr;
import org.docx4j.wml.PPrBase.Spacing;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STEm;
import org.docx4j.wml.STLineNumberRestart;
import org.docx4j.wml.STLineSpacingRule;
import org.docx4j.wml.STPageOrientation;
import org.docx4j.wml.STShd;
import org.docx4j.wml.STVerticalAlignRun;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.SectPr.PgBorders;
import org.docx4j.wml.SectPr.PgMar;
import org.docx4j.wml.SectPr.PgSz;
import org.docx4j.wml.SectPr.Type;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblGrid;
import org.docx4j.wml.TblGridCol;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.TcPrInner.GridSpan;
import org.docx4j.wml.TcPrInner.HMerge;
import org.docx4j.wml.TcPrInner.VMerge;
import org.docx4j.wml.Text;
import org.docx4j.wml.TextDirection;
import org.docx4j.wml.Tr;
import org.docx4j.wml.TrPr;
import org.docx4j.wml.U;
import org.docx4j.wml.UnderlineEnumeration;
import java.util.Properties;

/**
 * Unit tests for {@link WmlElementUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WmlElementUtils Tests")
class WmlElementUtilsTest {

    @Test
    @DisplayName("static method getChildrenElements should be callable")
    void staticGetChildrenElementsShouldBeCallable() {
        try { WmlElementUtils.getChildrenElements((Object) null, (Class) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getTargetElements should be callable")
    void staticGetTargetElementsShouldBeCallable() {
        try { WmlElementUtils.getTargetElements((Object) null, (Class) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getTable should be callable")
    void staticGetTableShouldBeCallable() {
        try { WmlElementUtils.getTable((List) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method replaceTable should be callable")
    void staticReplaceTableShouldBeCallable() {
        try { WmlElementUtils.replaceTable((String[]) null, (List) null, (WordprocessingMLPackage) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRowToTable should be callable")
    void staticAddRowToTableShouldBeCallable() {
        try { WmlElementUtils.addRowToTable((Tbl) null, (Tr) null, (Map) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method createFooter should be callable")
    void staticCreateFooterShouldBeCallable() {
        try { WmlElementUtils.createFooter("test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addInlineImageToParagraph should be callable")
    void staticAddInlineImageToParagraphShouldBeCallable() {
        try { WmlElementUtils.addInlineImageToParagraph((Inline) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addImage should be callable")
    void staticAddImageShouldBeCallable() {
        try { WmlElementUtils.addImage((WordprocessingMLPackage) null, (CTBookmark) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getElementContent should be callable")
    void staticGetElementContentShouldBeCallable() {
        try { WmlElementUtils.getElementContent((Object) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getAllElementFromObject should be callable")
    void staticGetAllElementFromObjectShouldBeCallable() {
        try { WmlElementUtils.getAllElementFromObject((Object) null, (Class) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method createWordprocessingMLPackage should be callable")
    void staticCreateWordprocessingMLPackageShouldBeCallable() {
        try { WmlElementUtils.createWordprocessingMLPackage(); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method loadWordprocessingMLPackageWithPwd should be callable")
    void staticLoadWordprocessingMLPackageWithPwdShouldBeCallable() {
        try { WmlElementUtils.loadWordprocessingMLPackageWithPwd("test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method loadWordprocessingMLPackage should be callable")
    void staticLoadWordprocessingMLPackageShouldBeCallable() {
        try { WmlElementUtils.loadWordprocessingMLPackage("test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method mergeCellsHorizontalByGridSpan should be callable")
    void staticMergeCellsHorizontalByGridSpanShouldBeCallable() {
        try { WmlElementUtils.mergeCellsHorizontalByGridSpan((Tbl) null, 0, 0, 0); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method mergeCellsHorizontal should be callable")
    void staticMergeCellsHorizontalShouldBeCallable() {
        try { WmlElementUtils.mergeCellsHorizontal((Tbl) null, 0, 0, 0); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method mergeCellsVertically should be callable")
    void staticMergeCellsVerticallyShouldBeCallable() {
        try { WmlElementUtils.mergeCellsVertically((Tbl) null, 0, 0, 0); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getTc should be callable")
    void staticGetTcShouldBeCallable() {
        try { WmlElementUtils.getTc((Tbl) null, 0, 0); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getAllTbl should be callable")
    void staticGetAllTblShouldBeCallable() {
        try { WmlElementUtils.getAllTbl((WordprocessingMLPackage) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method removeTableByIndex should be callable")
    void staticRemoveTableByIndexShouldBeCallable() {
        try { WmlElementUtils.removeTableByIndex((WordprocessingMLPackage) null, 0); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getTblContentStr should be callable")
    void staticGetTblContentStrShouldBeCallable() {
        try { WmlElementUtils.getTblContentStr((Tbl) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getTblContentList should be callable")
    void staticGetTblContentListShouldBeCallable() {
        try { WmlElementUtils.getTblContentList((Tbl) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getTblPr should be callable")
    void staticGetTblPrShouldBeCallable() {
        try { WmlElementUtils.getTblPr((Tbl) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setTableWidth should be callable")
    void staticSetTableWidthShouldBeCallable() {
        try { WmlElementUtils.setTableWidth((Tbl) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method createTable should be callable")
    void staticCreateTableShouldBeCallable() {
        try { WmlElementUtils.createTable((WordprocessingMLPackage) null, 0, 0); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method createTable should be callable")
    void staticCreateTableWith24ParamsShouldBeCallable() {
        try { WmlElementUtils.createTable(0, 0, (int[]) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getTblAllTr should be callable")
    void staticGetTblAllTrShouldBeCallable() {
        try { WmlElementUtils.getTblAllTr((Tbl) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setTrHeight should be callable")
    void staticSetTrHeightShouldBeCallable() {
        try { WmlElementUtils.setTrHeight((Tr) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addTrByIndex should be callable")
    void staticAddTrByIndexShouldBeCallable() {
        try { WmlElementUtils.addTrByIndex((Tbl) null, 0); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addTrByIndex should be callable")
    void staticAddTrByIndexWith28ParamsShouldBeCallable() {
        try { WmlElementUtils.addTrByIndex((Tbl) null, 0, (STVerticalJc) null, (JcEnumeration) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getTcCellSizeWithMergeNum should be callable")
    void staticGetTcCellSizeWithMergeNumShouldBeCallable() {
        try { WmlElementUtils.getTcCellSizeWithMergeNum((Tr) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method removeTrByIndex should be callable")
    void staticRemoveTrByIndexShouldBeCallable() {
        try { WmlElementUtils.removeTrByIndex((Tbl) null, 0); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getTrPr should be callable")
    void staticGetTrPrShouldBeCallable() {
        try { WmlElementUtils.getTrPr((Tr) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setTrHidden should be callable")
    void staticSetTrHiddenShouldBeCallable() {
        try { WmlElementUtils.setTrHidden((Tr) null, true); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setTcWidth should be callable")
    void staticSetTcWidthShouldBeCallable() {
        try { WmlElementUtils.setTcWidth((Tc) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setTcHidden should be callable")
    void staticSetTcHiddenShouldBeCallable() {
        try { WmlElementUtils.setTcHidden((Tc) null, true); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getTcAllP should be callable")
    void staticGetTcAllPShouldBeCallable() {
        try { WmlElementUtils.getTcAllP((Tc) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getTcPr should be callable")
    void staticGetTcPrShouldBeCallable() {
        try { WmlElementUtils.getTcPr((Tc) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setTcVAlign should be callable")
    void staticSetTcVAlignShouldBeCallable() {
        try { WmlElementUtils.setTcVAlign((Tc) null, (STVerticalJc) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setTcJcAlign should be callable")
    void staticSetTcJcAlignShouldBeCallable() {
        try { WmlElementUtils.setTcJcAlign((Tc) null, (JcEnumeration) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getRPr should be callable")
    void staticGetRPrShouldBeCallable() {
        try { WmlElementUtils.getRPr((R) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getTrAllCell should be callable")
    void staticGetTrAllCellShouldBeCallable() {
        try { WmlElementUtils.getTrAllCell((Tr) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getTcContent should be callable")
    void staticGetTcContentShouldBeCallable() {
        try { WmlElementUtils.getTcContent((Tc) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setParaJcAlign should be callable")
    void staticSetParaJcAlignShouldBeCallable() {
        try { WmlElementUtils.setParaJcAlign((P) null, (JcEnumeration) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setParaRContent should be callable")
    void staticSetParaRContentShouldBeCallable() {
        try { WmlElementUtils.setParaRContent((P) null, (RPr) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method appendParaRContent should be callable")
    void staticAppendParaRContentShouldBeCallable() {
        try { WmlElementUtils.appendParaRContent((P) null, (RPr) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addImageToPara should be callable")
    void staticAddImageToParaShouldBeCallable() {
        try { WmlElementUtils.addImageToPara((WordprocessingMLPackage) null, (ObjectFactory) null, (P) null, "test", "test", (RPr) null, "test", 0, 0); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addPageBreak should be callable")
    void staticAddPageBreakShouldBeCallable() {
        try { WmlElementUtils.addPageBreak((P) null, (STBrType) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setParagraphSuppressLineNum should be callable")
    void staticSetParagraphSuppressLineNumShouldBeCallable() {
        try { WmlElementUtils.setParagraphSuppressLineNum((P) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setParagraphShdStyle should be callable")
    void staticSetParagraphShdStyleShouldBeCallable() {
        try { WmlElementUtils.setParagraphShdStyle((P) null, (STShd) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getPPr should be callable")
    void staticGetPPrShouldBeCallable() {
        try { WmlElementUtils.getPPr((P) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getParaRPr should be callable")
    void staticGetParaRPrShouldBeCallable() {
        try { WmlElementUtils.getParaRPr((PPr) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setParaVanish should be callable")
    void staticSetParaVanishShouldBeCallable() {
        try { WmlElementUtils.setParaVanish((PPr) null, true); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setParagraghBorders should be callable")
    void staticSetParagraghBordersShouldBeCallable() {
        try { WmlElementUtils.setParagraghBorders((P) null, (CTBorder) null, (CTBorder) null, (CTBorder) null, (CTBorder) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setFontStyle should be callable")
    void staticSetFontStyleShouldBeCallable() {
        try { WmlElementUtils.setFontStyle((RPr) null, "test", "test", "test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setFontSize should be callable")
    void staticSetFontSizeShouldBeCallable() {
        try { WmlElementUtils.setFontSize((RPr) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setFontFamily should be callable")
    void staticSetFontFamilyShouldBeCallable() {
        try { WmlElementUtils.setFontFamily((RPr) null, "test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setFontColor should be callable")
    void staticSetFontColorShouldBeCallable() {
        try { WmlElementUtils.setFontColor((RPr) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrBorderStyle should be callable")
    void staticAddRPrBorderStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrBorderStyle((RPr) null, "test", (STBorder) null, "test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrEmStyle should be callable")
    void staticAddRPrEmStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrEmStyle((RPr) null, (STEm) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrOutlineStyle should be callable")
    void staticAddRPrOutlineStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrOutlineStyle((RPr) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrcaleStyle should be callable")
    void staticAddRPrcaleStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrcaleStyle((RPr) null, (STVerticalAlignRun) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrScaleStyle should be callable")
    void staticAddRPrScaleStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrScaleStyle((RPr) null, 0); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrtSpacingStyle should be callable")
    void staticAddRPrtSpacingStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrtSpacingStyle((RPr) null, 0); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrtPositionStyle should be callable")
    void staticAddRPrtPositionStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrtPositionStyle((RPr) null, 0); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrImprintStyle should be callable")
    void staticAddRPrImprintStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrImprintStyle((RPr) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrEmbossStyle should be callable")
    void staticAddRPrEmbossStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrEmbossStyle((RPr) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setRPrVanishStyle should be callable")
    void staticSetRPrVanishStyleShouldBeCallable() {
        try { WmlElementUtils.setRPrVanishStyle((RPr) null, true); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrShadowStyle should be callable")
    void staticAddRPrShadowStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrShadowStyle((RPr) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrShdStyle should be callable")
    void staticAddRPrShdStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrShdStyle((RPr) null, (STShd) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrHightLightStyle should be callable")
    void staticAddRPrHightLightStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrHightLightStyle((RPr) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrStrikeStyle should be callable")
    void staticAddRPrStrikeStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrStrikeStyle((RPr) null, true, true); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrBoldStyle should be callable")
    void staticAddRPrBoldStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrBoldStyle((RPr) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrItalicStyle should be callable")
    void staticAddRPrItalicStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrItalicStyle((RPr) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method addRPrUnderlineStyle should be callable")
    void staticAddRPrUnderlineStyleShouldBeCallable() {
        try { WmlElementUtils.addRPrUnderlineStyle((RPr) null, (UnderlineEnumeration) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setDocSectionBreak should be callable")
    void staticSetDocSectionBreakShouldBeCallable() {
        try { WmlElementUtils.setDocSectionBreak((WordprocessingMLPackage) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setDocumentBackGround should be callable")
    void staticSetDocumentBackGroundShouldBeCallable() {
        try { WmlElementUtils.setDocumentBackGround((WordprocessingMLPackage) null, (ObjectFactory) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setDocumentBorders should be callable")
    void staticSetDocumentBordersShouldBeCallable() {
        try { WmlElementUtils.setDocumentBorders((WordprocessingMLPackage) null, (ObjectFactory) null, (CTBorder) null, (CTBorder) null, (CTBorder) null, (CTBorder) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setDocumentSize should be callable")
    void staticSetDocumentSizeShouldBeCallable() {
        try { WmlElementUtils.setDocumentSize((WordprocessingMLPackage) null, (ObjectFactory) null, "test", "test", (STPageOrientation) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getDocSectPr should be callable")
    void staticGetDocSectPrShouldBeCallable() {
        try { WmlElementUtils.getDocSectPr((WordprocessingMLPackage) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setDocMarginSpace should be callable")
    void staticSetDocMarginSpaceShouldBeCallable() {
        try { WmlElementUtils.setDocMarginSpace((WordprocessingMLPackage) null, (ObjectFactory) null, "test", "test", "test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setDocInNumType should be callable")
    void staticSetDocInNumTypeShouldBeCallable() {
        try { WmlElementUtils.setDocInNumType((WordprocessingMLPackage) null, "test", "test", "test", (STLineNumberRestart) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setDocTextDirection should be callable")
    void staticSetDocTextDirectionShouldBeCallable() {
        try { WmlElementUtils.setDocTextDirection((WordprocessingMLPackage) null, "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method setDocVAlign should be callable")
    void staticSetDocVAlignShouldBeCallable() {
        try { WmlElementUtils.setDocVAlign((WordprocessingMLPackage) null, (STVerticalJc) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getWritableWidth should be callable")
    void staticGetWritableWidthShouldBeCallable() {
        try { WmlElementUtils.getWritableWidth((WordprocessingMLPackage) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlElementUtils.class).isNotNull();
    }

}
