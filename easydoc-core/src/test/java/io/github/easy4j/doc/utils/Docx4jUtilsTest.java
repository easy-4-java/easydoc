package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

class Docx4jUtilsTest {

    @Test
    void getTempPathReturnsNonEmptyString() {
        String path = Docx4jUtils.getTempPath();
        assertNotNull(path);
        assertTrue(path.length() > 0);
    }

    @Test
    void getTempPathContainsTempDir() {
        String path = Docx4jUtils.getTempPath();
        assertTrue(path.startsWith(System.getProperty("java.io.tmpdir")));
    }
}
