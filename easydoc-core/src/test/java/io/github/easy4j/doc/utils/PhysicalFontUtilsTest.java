package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

class PhysicalFontUtilsTest {

    @Test
    void setSimSunFontAppliesDefaultFont() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        assertNotNull(pkg);
        PhysicalFontUtils.setSimSunFont(pkg);
        assertNotNull(pkg.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr());
        assertNotNull(pkg.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr().getRFonts());
    }

    @Test
    void setDefaultFontAppliesFont() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        PhysicalFontUtils.setDefaultFont(pkg, "Arial");
        assertNotNull(pkg.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr().getRFonts());
    }
}
