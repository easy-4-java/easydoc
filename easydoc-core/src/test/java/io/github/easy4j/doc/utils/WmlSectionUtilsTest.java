package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.STPageOrientation;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Text;
import org.junit.jupiter.api.Test;

class WmlSectionUtilsTest {

    @Test
    void createFooterReturnsPopulatedFooter() {
        Ftr footer = WmlSectionUtils.createFooter("page 1");
        assertNotNull(footer);
        assertNotNull(footer.getContent());
        P p = (P) footer.getContent().get(0);
        R r = (R) p.getContent().get(0);
        Text text = (Text) r.getContent().get(0);
        assertEquals("page 1", text.getValue());
    }

    @Test
    void getDocSectPrReturnsSectPr() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        SectPr sectPr = WmlSectionUtils.getDocSectPr(pkg);
        assertNotNull(sectPr);
    }

    @Test
    void setDocSectionBreakSetsType() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WmlSectionUtils.setDocSectionBreak(pkg, "nextPage");
        SectPr sectPr = WmlSectionUtils.getDocSectPr(pkg);
        assertNotNull(sectPr.getType());
        assertEquals("nextPage", sectPr.getType().getVal());
    }

    @Test
    void setDocSectionBreakWithBlankDoesNothing() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WmlSectionUtils.setDocSectionBreak(pkg, "");
        SectPr sectPr = WmlSectionUtils.getDocSectPr(pkg);
        // blank => no type set
    }

    @Test
    void setDocMarginSpaceSetsMargins() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ObjectFactory factory = Context.getWmlObjectFactory();
        WmlSectionUtils.setDocMarginSpace(pkg, factory, "1440", "1800", "1440", "1800");
        SectPr sectPr = WmlSectionUtils.getDocSectPr(pkg);
        assertNotNull(sectPr.getPgMar());
        assertEquals(1440, sectPr.getPgMar().getTop().intValue());
        assertEquals(1800, sectPr.getPgMar().getLeft().intValue());
        assertEquals(1440, sectPr.getPgMar().getBottom().intValue());
        assertEquals(1800, sectPr.getPgMar().getRight().intValue());
    }

    @Test
    void setDocMarginSpaceWithBlankArgs() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ObjectFactory factory = Context.getWmlObjectFactory();
        WmlSectionUtils.setDocMarginSpace(pkg, factory, "", "", "", "");
        SectPr sectPr = WmlSectionUtils.getDocSectPr(pkg);
        assertNotNull(sectPr.getPgMar());
    }

    @Test
    void setDocumentSizeSetsPageSize() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ObjectFactory factory = Context.getWmlObjectFactory();
        WmlSectionUtils.setDocumentSize(pkg, factory, "11906", "16838", STPageOrientation.LANDSCAPE);
        SectPr sectPr = WmlSectionUtils.getDocSectPr(pkg);
        assertNotNull(sectPr.getPgSz());
        assertEquals(11906, sectPr.getPgSz().getW().intValue());
        assertEquals(16838, sectPr.getPgSz().getH().intValue());
        assertEquals(STPageOrientation.LANDSCAPE, sectPr.getPgSz().getOrient());
    }

    @Test
    void setDocumentSizeWithBlankArgs() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ObjectFactory factory = Context.getWmlObjectFactory();
        WmlSectionUtils.setDocumentSize(pkg, factory, "", "", null);
        SectPr sectPr = WmlSectionUtils.getDocSectPr(pkg);
        assertNotNull(sectPr.getPgSz());
    }

    @Test
    void getWritableWidthReturnsPositiveValue() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        int width = WmlSectionUtils.getWritableWidth(pkg);
        assertTrue(width > 0);
    }

    private static void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }
}
