package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.testutil.FontDiscoveryTestBase;

/**
 * Additional tests for PhysicalFontUtils to cover setWmlPackageFonts
 * and both setPhysicalFont overloads.
 *
 * <p>These paths require working font discovery, so the suite extends
 * {@link FontDiscoveryTestBase}: on machines where
 * {@code PhysicalFonts.discoverPhysicalFonts()} throws (macOS FOP issue) the
 * tests SKIP instead of swallowing errors and passing vacuously. Absence of a
 * specific font family ("Arial", "Courier New") stays guarded with null checks,
 * because that is legitimate per-machine variation, not an environment fault.</p>
 */
class PhysicalFontUtilsExtendedTest extends FontDiscoveryTestBase {

    @Test
    void setWmlPackageFontsAppliesFontMapper() throws Exception {
        assumeFontDiscoveryWorks();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        // setWmlPackageFonts creates an IdentityPlusMapper with Chinese font mappings;
        // those lookups are name-based and tolerate missing fonts at runtime.
        PhysicalFontUtils.setWmlPackageFonts(pkg);
        assertNotNull(pkg.getFontMapper());
    }

    @Test
    void setPhysicalFontWithPhysicalFontObject() throws Exception {
        assumeFontDiscoveryWorks();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        PhysicalFont font = PhysicalFonts.get("Arial");
        if (font != null) {
            PhysicalFontUtils.setPhysicalFont(pkg, font);
            assertNotNull(pkg.getFontMapper());
        }
    }

    @Test
    void setPhysicalFontWithFontName() throws Exception {
        assumeFontDiscoveryWorks();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        if (PhysicalFonts.get("Arial") == null) {
            return; // Arial genuinely absent on this machine
        }
        PhysicalFontUtils.setPhysicalFont(pkg, "Arial");
        assertNotNull(pkg.getFontMapper());
    }

    @Test
    void setPhysicalFontWithExistingMapper() throws Exception {
        assumeFontDiscoveryWorks();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        if (PhysicalFonts.get("Arial") == null || PhysicalFonts.get("Courier New") == null) {
            return; // specific families absent on this machine
        }
        // Set default font first to initialize the mapper
        PhysicalFontUtils.setDefaultFont(pkg, "Arial");
        // Now set a physical font using the existing mapper
        PhysicalFontUtils.setPhysicalFont(pkg, "Courier New");
        assertNotNull(pkg.getFontMapper());
    }
}
