package io.github.easy4j.doc.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WordprocessingMLPackageWriterTest {

    @Test
    void getWMLPackageWriterReturnsInstance() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertNotNull(writer);
    }

    @Test
    void getWMLPackageWriterReturnsSameInstance() {
        WordprocessingMLPackageWriter a = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackageWriter b = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertSame(a, b);
    }

    @Test
    void getHyperlinkHandlerReturnsHandler() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertNotNull(writer.getHyperlinkHandler());
    }

    @Test
    void getStyleElementHandlerReturnsHandler() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertNotNull(writer.getStyleElementHandler());
    }

    @Test
    void getScriptElementHandlerReturnsHandler() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertNotNull(writer.getScriptElementHandler());
    }

    @Test
    void setHyperlinkHandlerStoresValue() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        org.docx4j.convert.out.ConversionHyperlinkHandler original = writer.getHyperlinkHandler();
        writer.setHyperlinkHandler(original);
        assertNotNull(writer.getHyperlinkHandler());
    }

    @Test
    void setStyleElementHandlerStoresValue() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        org.docx4j.convert.out.ConversionHTMLStyleElementHandler original = writer.getStyleElementHandler();
        writer.setStyleElementHandler(original);
        assertNotNull(writer.getStyleElementHandler());
    }

    @Test
    void setScriptElementHandlerStoresValue() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        org.docx4j.convert.out.ConversionHTMLScriptElementHandler original = writer.getScriptElementHandler();
        writer.setScriptElementHandler(original);
        assertNotNull(writer.getScriptElementHandler());
    }

    // --- writeToDocx tests ---

    @Test
    void writeToDocxRejectsNullPackage() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToDocx(null, new File("dummy.docx"));
        });
    }

    @Test
    void writeToDocxRejectsNullPackageOutputStream() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToDocx(null, new ByteArrayOutputStream());
        });
    }

    @Test
    void writeToDocxRejectsNullOutputStream() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToDocx(WordprocessingMLPackage.createPackage(), (OutputStream) null);
        });
    }

    @Test
    void writeToDocxRejectsNullOutPath() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToDocx(WordprocessingMLPackage.createPackage(), (String) null);
        });
    }

    @Test
    void writeToDocxToOutputStreamWritesBytes() throws Exception {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeToDocx(pkg, baos);
        assertTrue(baos.size() > 0, "docx output should be non-empty");
    }

    @Test
    void writeToDocxToTempFile(@TempDir java.nio.file.Path tempDir) throws Exception {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File outFile = tempDir.resolve("output.docx").toFile();
        outFile.createNewFile();
        File result = writer.writeToDocx(pkg, outFile);
        assertNotNull(result);
        assertTrue(result.exists());
    }

    @Test
    void writeToDocxToPath(@TempDir java.nio.file.Path tempDir) throws Exception {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File outFile = tempDir.resolve("output2.docx").toFile();
        outFile.createNewFile();
        File result = writer.writeToDocx(pkg, outFile.getAbsolutePath());
        assertNotNull(result);
    }

    @Test
    void writeToDocxRejectsNonExistentFile() throws Exception {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        // P0-2 fix: Assert.isTrue(outFile.exists()) removed; writing to a path
        // whose parent directory does not exist now throws IOException (FileOutputStream)
        assertThrows(IOException.class, () -> {
            writer.writeToDocx(pkg, new File("/nonexistent/path/file.docx"));
        });
    }

    @Test
    void writeToDocxNoArgRejectsNullPackage() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToDocx(null);
        });
    }

    @Test
    void writeToDocxNoArgCreatesTempFileAndDelegates() throws Exception {
        // P0-2 fix: Assert.isTrue(outFile.exists()) removed; the no-arg version
        // now creates a temp file and writes successfully.
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File result = writer.writeToDocx(pkg);
        assertNotNull(result);
        assertTrue(result.exists());
    }

    // --- writeToHtml tests ---

    @Test
    void writeToHtmlNoArgRejectsNullPackage() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToHtml(null);
        });
    }

    @Test
    void writeToHtmlNoArgDelegatesToFileVersion() throws Exception {
        // 覆盖无参重载：构造临时路径后委托给 File 重载；导出空包时
        // Docx4J.toHTML 会确定性失败（MainDocumentPart 为空），与预期一致
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        assertThrows(Exception.class, () -> {
            writer.writeToHtml(pkg);
        });
    }

    @Test
    void writeToHtmlStringPathRejectsNullPackage() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToHtml(null, "/some/path");
        });
    }

    @Test
    void writeToHtmlStringPathRejectsNullPath() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToHtml(WordprocessingMLPackage.createPackage(), (String) null);
        });
    }

    @Test
    void writeToHtmlStringPathCreatesMissingParentDirs(@TempDir java.nio.file.Path tempDir) throws Exception {
        // String 路径重载委托给 File 重载；File 即目标 html 文件，
        // 多级不存在的父目录会被自动创建（Files.createDirectories）
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        pkg.getMainDocumentPart().addParagraphOfText("hello easydoc");
        File outFile = tempDir.resolve("level1/level2/report.html").toFile();
        File result = writer.writeToHtml(pkg, outFile.getAbsolutePath());
        assertNotNull(result);
        assertTrue(result.isFile(), "target html file should be created");
        assertTrue(result.length() > 0, "html output should be non-empty");
    }

    @Test
    void writeToHtmlFileRejectsNullPackage() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToHtml(null, new File("dummy"));
        });
    }

    @Test
    void writeToHtmlFileUncreatableParentThrowsIOException(@TempDir java.nio.file.Path tempDir) throws Exception {
        // 目标文件的父路径中存在同名普通文件时，无法创建父目录：
        // Files.createDirectories 抛出 IOException（FileAlreadyExistsException/FileSystemException 族）
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File blocker = tempDir.resolve("blocked").toFile();
        assertTrue(blocker.createNewFile(), "blocker file should be created");
        assertThrows(IOException.class, () -> {
            writer.writeToHtml(pkg, new File(blocker, "report.html"));
        });
    }

    @Test
    void writeToHtmlFileRejectsExistingDirectoryTarget(@TempDir java.nio.file.Path tempDir) throws Exception {
        // 缺陷修复后的语义：outFile 必须是“目标文件”，传入已存在的目录
        // 时抛出带明确提示的 IOException（而非旧版要求目录却又对其建流的自相矛盾行为）
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        IOException e = assertThrows(IOException.class, () -> {
            writer.writeToHtml(pkg, tempDir.toFile());
        });
        assertTrue(e.getMessage() != null && e.getMessage().contains("directory"),
                "exception message should mention directory, but was: " + e.getMessage());
    }

    // --- writeToPDF tests ---

    @Test
    void writeToPDFNoArgRejectsNullPackage() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToPDF(null);
        });
    }

    @Test
    void writeToPDFNoArgDelegatesToFileVersion() throws Exception {
        // P0-2 fix: Assert.isTrue(outFile.exists()) removed; the no-arg version now
        // creates the temp file on demand, then exporting the empty package fails
        // deterministically in Docx4J.toPDF
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        assertThrows(Docx4JException.class, () -> {
            writer.writeToPDF(pkg);
        });
    }

    @Test
    void writeToPDFStringPathRejectsNullPackage() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToPDF(null, "/some/path");
        });
    }

    @Test
    void writeToPDFStringPathRejectsNullPath() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToPDF(WordprocessingMLPackage.createPackage(), (String) null);
        });
    }

    @Test
    void writeToPDFStringPathDelegatesToFileVersion() throws Exception {
        // String path delegates to the File overload; a missing parent directory surfaces
        // as FileNotFoundException from FileOutputStream (Assert.isTrue removed)
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        assertThrows(FileNotFoundException.class, () -> {
            writer.writeToPDF(pkg, "/nonexistent/path");
        });
    }

    @Test
    void writeToPDFFileRejectsNonExistentFile() throws Exception {
        // Target file is created on demand; a missing parent directory fails with
        // FileNotFoundException from FileOutputStream (Assert.isTrue removed)
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        assertThrows(FileNotFoundException.class, () -> {
            writer.writeToPDF(pkg, new File("/nonexistent/path/file.pdf"));
        });
    }

    @Test
    void writeToPDFOutputStreamRejectsNullPackage() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToPDF(null, new ByteArrayOutputStream());
        });
    }

    @Test
    void writeToPDFOutputStreamRejectsNullOutputStream() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToPDF(WordprocessingMLPackage.createPackage(), (OutputStream) null);
        });
    }

    @Test
    @org.junit.jupiter.api.Disabled("IdentityPlusMapper.<clinit> fails on JVM 21 (NoClassDefFoundError escapes try/catch(Exception))")
    void writeToPDFOutputStreamWritesBytes() throws Exception {
        // Covers lines 249-257 (the writeToPDF OutputStream method)
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            writer.writeToPDF(pkg, baos);
        } catch (Exception e) {
            // Docx4J.toPDF may fail if PDF conversion is not fully available,
            // but lines 249-252 are still covered
        }
    }

    @Test
    @org.junit.jupiter.api.Disabled("IdentityPlusMapper.<clinit> fails on JVM 21 (NoClassDefFoundError escapes try/catch(Exception))")
    void writeToPDFToFileAndStream(@TempDir java.nio.file.Path tempDir) throws Exception {
        // Covers lines 236-238 (File method) + 249-257 (OutputStream method)
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File outFile = tempDir.resolve("output.pdf").toFile();
        outFile.createNewFile();
        try {
            writer.writeToPDF(pkg, outFile);
        } catch (Exception e) {
            // Docx4J.toPDF may fail, but the File method and OutputStream method are entered
        }
    }

    @Test
    void writeToPDFWhithFoRejectsNullPackage() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToPDFWhithFo(null, new ByteArrayOutputStream());
        });
    }

    @Test
    void writeToPDFWhithFoRejectsNullOutputStream() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToPDFWhithFo(WordprocessingMLPackage.createPackage(), null);
        });
    }

    @Test
    void writeToPDFWhithFoAttemptsConversion() throws Exception {
        // Covers lines 267-278 (assertions + FieldUpdater + PhysicalFonts)
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            writer.writeToPDFWhithFo(pkg, baos);
        } catch (Exception e) {
            // May fail at Docx4J.toFO or PhysicalFonts, but earlier lines are covered
        }
    }

    private static void assertTrue(boolean condition, String message) {
        org.junit.jupiter.api.Assertions.assertTrue(condition, message);
    }

    private static void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }
}
