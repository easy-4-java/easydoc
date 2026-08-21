package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.STLineNumberRestart;
import org.docx4j.wml.STVerticalJc;
import org.junit.jupiter.api.Test;

class WmlDocumentUtilsTest {

    @Test
    void createWordprocessingMLPackageReturnsPackage() throws Exception {
        WordprocessingMLPackage pkg = WmlDocumentUtils.createWordprocessingMLPackage();
        assertNotNull(pkg);
        assertNotNull(pkg.getMainDocumentPart());
    }

    @Test
    void setDocumentBackGroundSetsColor() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ObjectFactory factory = Context.getWmlObjectFactory();
        WmlDocumentUtils.setDocumentBackGround(pkg, factory, "FF0000");
        assertNotNull(pkg.getMainDocumentPart().getContents().getBackground());
    }

    @Test
    void setDocumentBackGroundWithBlankColorDoesNothing() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ObjectFactory factory = Context.getWmlObjectFactory();
        WmlDocumentUtils.setDocumentBackGround(pkg, factory, "");
        // blank color => no background set
        assertNull(pkg.getMainDocumentPart().getContents().getBackground());
    }

    @Test
    void setDocumentBordersSetsBorders() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ObjectFactory factory = Context.getWmlObjectFactory();
        CTBorder top = factory.createCTBorder();
        CTBorder bottom = factory.createCTBorder();
        CTBorder left = factory.createCTBorder();
        CTBorder right = factory.createCTBorder();
        WmlDocumentUtils.setDocumentBorders(pkg, factory, top, right, bottom, left);
        assertNotNull(WmlSectionUtils.getDocSectPr(pkg).getPgBorders());
    }

    @Test
    void setDocumentBordersWithNullBorders() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ObjectFactory factory = Context.getWmlObjectFactory();
        WmlDocumentUtils.setDocumentBorders(pkg, factory, null, null, null, null);
        assertNotNull(WmlSectionUtils.getDocSectPr(pkg).getPgBorders());
    }

    @Test
    void setDocInNumTypeSetsLineNumbers() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WmlDocumentUtils.setDocInNumType(pkg, "1", "567", "0", STLineNumberRestart.CONTINUOUS);
        assertNotNull(WmlSectionUtils.getDocSectPr(pkg).getLnNumType());
    }

    @Test
    void setDocInNumTypeWithBlankArgs() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WmlDocumentUtils.setDocInNumType(pkg, "", "", "", null);
        assertNotNull(WmlSectionUtils.getDocSectPr(pkg).getLnNumType());
    }

    @Test
    void setDocTextDirectionSetsDirection() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WmlDocumentUtils.setDocTextDirection(pkg, "tbRl");
        assertNotNull(WmlSectionUtils.getDocSectPr(pkg).getTextDirection());
    }

    @Test
    void setDocTextDirectionWithBlankDoesNothing() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WmlDocumentUtils.setDocTextDirection(pkg, "");
        // blank => no text direction set
    }

    @Test
    void setDocVAlignSetsVerticalAlignment() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WmlDocumentUtils.setDocVAlign(pkg, STVerticalJc.CENTER);
        assertNotNull(WmlSectionUtils.getDocSectPr(pkg).getVAlign());
    }

    @Test
    void setDocVAlignWithNullDoesNothing() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WmlDocumentUtils.setDocVAlign(pkg, null);
        // null => no valign set
    }
}
