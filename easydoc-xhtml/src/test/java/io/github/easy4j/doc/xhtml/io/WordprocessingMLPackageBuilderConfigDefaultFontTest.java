package io.github.easy4j.doc.xhtml.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

/**
 * Coverage for {@link WordprocessingMLPackageBuilder#configDefaultFont} (lines 73-78).
 * Unlike {@code configChineseFonts} and {@code configSimSunFont}, this method does
 * NOT trigger IdentityPlusMapper and works on JVM 21.
 *
 * <p>The method also has a catch-and-log branch for invalid font names (line 75),
 * which we exercise with a deliberately bogus font name.</p>
 */
class WordprocessingMLPackageBuilderConfigDefaultFontTest {

    @Test
    void configDefaultFontWithValidFontReturnsThis() throws Exception {
        WordprocessingMLPackageBuilder b = WordprocessingMLPackageBuilder.getWMLPackageBuilder();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageBuilder returned = b.configDefaultFont(pkg, "Arial");
        assertSame(b, returned, "configDefaultFont must be fluent");
    }

    @Test
    void configDefaultFontWithSimSunReturnsThis() throws Exception {
        WordprocessingMLPackageBuilder b = WordprocessingMLPackageBuilder.getWMLPackageBuilder();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageBuilder returned = b.configDefaultFont(pkg, "SimSun");
        assertSame(b, returned);
    }

    /**
     * Exercise the catch-and-log branch (line 75) by passing a font name that
     * may not exist on the system. PhysicalFontUtils.setDefaultFont does not
     * actually look up physical fonts — it just sets RFonts properties — so this
     * should succeed. If the implementation changes to validate font existence,
     * the catch block will fire and the method still returns {@code this}.
     */
    @Test
    void configDefaultFontWithUnknownFontStillReturnsThis() throws Exception {
        WordprocessingMLPackageBuilder b = WordprocessingMLPackageBuilder.getWMLPackageBuilder();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageBuilder returned = b.configDefaultFont(pkg, "NonExistentFont12345");
        assertSame(b, returned, "even with unknown font, method must return builder");
    }

    @Test
    void configDefaultFontPreservesPackage() throws Exception {
        WordprocessingMLPackageBuilder b = WordprocessingMLPackageBuilder.getWMLPackageBuilder();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        org.jsoup.nodes.Document doc = org.jsoup.nodes.Document.createShell("");
        WordprocessingMLPackage returned = b.configDefaultFont(pkg, "Arial")
                .buildWithDoc(doc, false);
        assertNotNull(returned, "chaining configDefaultFont into buildWithDoc must produce a package");
    }

    /**
     * Exercise the catch-and-log branch (lines 74-75) by passing null, which
     * causes {@code PhysicalFontUtils.setDefaultFont} to throw a NullPointerException.
     * The method should catch it, log a warning, and still return {@code this}.
     */
    @Test
    void configDefaultFontWithNullPackageCatchesException() throws Exception {
        WordprocessingMLPackageBuilder b = WordprocessingMLPackageBuilder.getWMLPackageBuilder();
        // null wmlPackage → NPE inside setDefaultFont → caught and logged
        WordprocessingMLPackageBuilder returned = b.configDefaultFont(null, "Arial");
        assertSame(b, returned, "even when setDefaultFont throws, method must return builder");
    }
}
