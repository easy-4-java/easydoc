package io.github.easy4j.doc.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Additional tests for WordprocessingMLPackageWriter to cover
 * writeToDocx, writeToHtml, writeToPDF, and assertion paths.
 */
class WordprocessingMLPackageWriterExtendedTest {

    @Test
    void getWMLPackageWriterReturnsSingleton() {
        WordprocessingMLPackageWriter a = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackageWriter b = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertSame(a, b);
    }

    @Test
    void writeToDocxWithPackageCreatesFile(@TempDir Path tempDir) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        // P0-2 fix: Assert.isTrue(outFile.exists()) removed; the no-arg version
        // now creates a temp file and writes successfully.
        File result = writer.writeToDocx(pkg);
        assertNotNull(result);
        assertTrue(result.exists());
    }

    @Test
    void writeToDocxWithOutputStreamWritesData() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeToDocx(pkg, baos);
        assertTrue(baos.size() > 0);
    }

    @Test
    void writeToDocxWithFileWritesData(@TempDir Path tempDir) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        File outFile = tempDir.resolve("test.docx").toFile();
        outFile.createNewFile();
        File result = writer.writeToDocx(pkg, outFile);
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    @Test
    void writeToDocxWithStringPathWritesData(@TempDir Path tempDir) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        File outFile = tempDir.resolve("test2.docx").toFile();
        outFile.createNewFile();
        File result = writer.writeToDocx(pkg, outFile.getAbsolutePath());
        assertNotNull(result);
    }

    @Test
    void writeToDocxNullPackageThrowsAssertion() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToDocx(null);
        });
    }

    @Test
    void writeToDocxNullOutputStreamThrowsAssertion() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToDocx(pkg, (OutputStream) null);
        });
    }

    @Test
    void writeToDocxNullStringPathThrowsAssertion() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToDocx(pkg, (String) null);
        });
    }

    @Test
    void writeToHtmlNullPackageThrowsAssertion() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToHtml(null);
        });
    }

    @Test
    void writeToHtmlNullStringPathThrowsAssertion() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToHtml(pkg, (String) null);
        });
    }

    @Test
    void writeToPDFNullPackageThrowsAssertion() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToPDF(null);
        });
    }

    @Test
    void writeToPDFNullStringPathThrowsAssertion() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToPDF(pkg, (String) null);
        });
    }

    @Test
    void writeToPDFNullOutputStreamThrowsAssertion() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToPDF(pkg, (OutputStream) null);
        });
    }

    @Test
    void getAndSetHyperlinkHandler() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertNotNull(writer.getHyperlinkHandler());
        // Setting null is allowed
        writer.setHyperlinkHandler(null);
        assertNull(writer.getHyperlinkHandler());
        // Restore
        writer.setHyperlinkHandler(
                io.github.easy4j.doc.handler.OutputConversionHyperlinkHandler.getHyperlinkHandler());
    }

    @Test
    void getAndSetStyleElementHandler() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertNotNull(writer.getStyleElementHandler());
        writer.setStyleElementHandler(null);
        assertNull(writer.getStyleElementHandler());
        writer.setStyleElementHandler(
                io.github.easy4j.doc.handler.OutputConversionHTMLStyleElementHandler.getStyleElementHandler());
    }

    @Test
    void getAndSetScriptElementHandler() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertNotNull(writer.getScriptElementHandler());
        writer.setScriptElementHandler(null);
        assertNull(writer.getScriptElementHandler());
        writer.setScriptElementHandler(
                io.github.easy4j.doc.handler.OutputConversionHTMLScriptElementHandler.getScriptElementHandler());
    }

    @Test
    void writeToPDFWithOutputStream(@TempDir Path tempDir) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Empty package => deterministic Docx4JException from toPDF; the former
        // catch(Throwable) masked that failure and any assertion inside the try.
        assertThrows(Docx4JException.class, () -> writer.writeToPDF(pkg, baos));
    }

    @Test
    void writeToPDFWhithFoWithOutputStream(@TempDir Path tempDir) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Empty package => deterministic Docx4JException from the FO pipeline.
        assertThrows(Docx4JException.class, () -> writer.writeToPDFWhithFo(pkg, baos));
    }
}
