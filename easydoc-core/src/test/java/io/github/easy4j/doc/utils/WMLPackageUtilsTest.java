package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.P;
import org.docx4j.wml.Text;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WMLPackageUtilsTest {

    @Test
    void cleanDocumentPartWithNullReturnsFalse() throws Exception {
        assertFalse(WMLPackageUtils.cleanDocumentPart(null));
    }

    @Test
    void cleanDocumentPartWithValidPartReturnsTrue() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = pkg.getMainDocumentPart();
        assertTrue(WMLPackageUtils.cleanDocumentPart(mdp));
    }

    @Test
    void replacePlaceholderReplacesText() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = pkg.getMainDocumentPart();
        // Add a paragraph with a placeholder
        P p = new P();
        Text t = new Text();
        t.setValue("${name}");
        org.docx4j.wml.R r = new org.docx4j.wml.R();
        r.getContent().add(t);
        p.getContent().add(r);
        mdp.getContent().add(p);

        WMLPackageUtils.replacePlaceholder(mdp, "${name}", "Alice");

        // Verify replacement
        List<Text> texts = WmlElementTraversal.getTargetElements(mdp, Text.class);
        boolean found = false;
        for (Text txt : texts) {
            if ("Alice".equals(txt.getValue())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void replaceTextWithNullObjectDoesNothing() throws Exception {
        CTBookmark bm = new CTBookmark();
        bm.setName("test");
        WMLPackageUtils.replaceText(bm, null);
        // null object => early return, no exception
    }

    @Test
    void replaceTextWithNullNameDoesNothing() throws Exception {
        CTBookmark bm = new CTBookmark();
        bm.setName(null);
        WMLPackageUtils.replaceText(bm, "value");
        // null name => early return
    }

    @Test
    void imageToByteArrayReadsFile(@TempDir Path tempDir) throws Exception {
        byte[] expected = "hello world".getBytes();
        File f = tempDir.resolve("test.txt").toFile();
        Files.write(f.toPath(), expected);
        byte[] result = WMLPackageUtils.imageToByteArray(f);
        assertNotNull(result);
        assertEquals(expected.length, result.length);
    }

    @Test
    void imageToByteArrayReadsEmptyFile(@TempDir Path tempDir) throws Exception {
        File f = tempDir.resolve("empty.txt").toFile();
        f.createNewFile();
        byte[] result = WMLPackageUtils.imageToByteArray(f);
        assertNotNull(result);
        assertEquals(0, result.length);
    }
}
