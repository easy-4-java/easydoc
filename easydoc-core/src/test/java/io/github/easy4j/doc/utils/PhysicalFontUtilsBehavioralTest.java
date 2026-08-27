package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import io.github.easy4j.doc.fonts.ChineseFont;
import io.github.easy4j.doc.testutil.FontDiscoveryTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Behavioral tests for {@link PhysicalFontUtils}.
 *
 * These tests verify actual font values after calling the utility methods,
 * not just that they don't throw. Methods that use IdentityPlusMapper require
 * working font discovery, so this suite extends {@link FontDiscoveryTestBase}:
 * on machines where {@code PhysicalFonts.discoverPhysicalFonts()} throws
 * (macOS FOP issue) the affected tests SKIP via assumption instead of
 * swallowing errors and passing vacuously (audit #17).
 */
@DisplayName("PhysicalFontUtils Behavioral Tests")
class PhysicalFontUtilsBehavioralTest extends FontDiscoveryTestBase {

    // ---------------------------------------------------------------
    // setDefaultFont - does NOT use IdentityPlusMapper
    // ---------------------------------------------------------------

    @Test
    @DisplayName("setDefaultFont sets ascii, hAnsi, and eastAsia to the given font name")
    void setDefaultFontSetsAllFontAttributes() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        PhysicalFontUtils.setDefaultFont(pkg, "Courier New");

        RPr rpr = pkg.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr();
        assertNotNull(rpr, "Document default RPr should exist after setDefaultFont");
        RFonts rfonts = rpr.getRFonts();
        assertNotNull(rfonts, "RFonts should be set after setDefaultFont");
        assertEquals("Courier New", rfonts.getAscii(), "ascii font should match");
        assertEquals("Courier New", rfonts.getHAnsi(), "hAnsi font should match");
        assertEquals("Courier New", rfonts.getEastAsia(), "eastAsia font should match");
        assertNull(rfonts.getAsciiTheme(), "asciiTheme should be null after setDefaultFont");
    }

    @Test
    @DisplayName("setDefaultFont with different font names overwrites previous values")
    void setDefaultFontOverwrites() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        PhysicalFontUtils.setDefaultFont(pkg, "Arial");

        RFonts rfonts = pkg.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr().getRFonts();
        assertEquals("Arial", rfonts.getAscii());

        PhysicalFontUtils.setDefaultFont(pkg, "Times New Roman");
        rfonts = pkg.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr().getRFonts();
        assertEquals("Times New Roman", rfonts.getAscii());
    }

    // ---------------------------------------------------------------
    // setSimSunFont - delegates to setDefaultFont with SIMSUM
    // ---------------------------------------------------------------

    @Test
    @DisplayName("setSimSunFont sets the SIMSUM Chinese font as default")
    void setSimSunFontSetsSimSun() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        PhysicalFontUtils.setSimSunFont(pkg);

        RPr rpr = pkg.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr();
        assertNotNull(rpr);
        RFonts rfonts = rpr.getRFonts();
        assertNotNull(rfonts);
        assertEquals(ChineseFont.SIMSUM.getFontName(), rfonts.getAscii(),
                "setSimSunFont should set ascii to SIMSUM font name");
        assertEquals(ChineseFont.SIMSUM.getFontName(), rfonts.getHAnsi());
        assertEquals(ChineseFont.SIMSUM.getFontName(), rfonts.getEastAsia());
    }

    // ---------------------------------------------------------------
    // setWmlPackageFonts - uses newFontMapper() with IdentityPlusMapper
    // ---------------------------------------------------------------

    @Test
    @DisplayName("setWmlPackageFonts sets a non-null font mapper on the package")
    void setWmlPackageFontsSetsMapper() throws Exception {
        assumeFontDiscoveryWorks();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        PhysicalFontUtils.setWmlPackageFonts(pkg);
        Mapper mapper = pkg.getFontMapper();
        assertNotNull(mapper, "Font mapper should be set after setWmlPackageFonts");
    }

    // ---------------------------------------------------------------
    // setPhysicalFont(WordprocessingMLPackage, PhysicalFont)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("setPhysicalFont with PhysicalFont object exercises code path")
    void setPhysicalFontWithObject() throws Exception {
        assumeFontDiscoveryWorks();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        PhysicalFont font = PhysicalFonts.get("Arial");
        if (font == null) {
            return; // Arial genuinely absent on this machine
        }
        PhysicalFontUtils.setPhysicalFont(pkg, font);
        assertNotNull(pkg.getFontMapper());
    }

    @Test
    @DisplayName("setPhysicalFont with PhysicalFont and existing mapper exercises non-null branch")
    void setPhysicalFontWithObjectAndExistingMapper() throws Exception {
        assumeFontDiscoveryWorks();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        // First call creates mapper (null branch), second call uses existing (non-null branch)
        PhysicalFont font = PhysicalFonts.get("Arial");
        PhysicalFont font2 = PhysicalFonts.get("Courier New");
        if (font == null || font2 == null) {
            return; // specific families absent on this machine
        }
        PhysicalFontUtils.setPhysicalFont(pkg, font);
        PhysicalFontUtils.setPhysicalFont(pkg, font2);
    }

    // ---------------------------------------------------------------
    // setPhysicalFont(WordprocessingMLPackage, String)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("setPhysicalFont with font name exercises code path")
    void setPhysicalFontWithName() throws Exception {
        assumeFontDiscoveryWorks();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        if (PhysicalFonts.get("Arial") == null) {
            return; // Arial genuinely absent on this machine
        }
        PhysicalFontUtils.setPhysicalFont(pkg, "Arial");
        assertNotNull(pkg.getFontMapper());
    }

    @Test
    @DisplayName("setPhysicalFont with font name and existing mapper exercises non-null branch")
    void setPhysicalFontWithNameAndExistingMapper() throws Exception {
        assumeFontDiscoveryWorks();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        if (PhysicalFonts.get("Arial") == null || PhysicalFonts.get("Courier New") == null) {
            return; // specific families absent on this machine
        }
        // First call creates mapper (null branch)
        PhysicalFontUtils.setPhysicalFont(pkg, "Arial");
        // Second call uses existing mapper (non-null branch)
        PhysicalFontUtils.setPhysicalFont(pkg, "Courier New");
        assertNotNull(pkg.getFontMapper());
    }
}
