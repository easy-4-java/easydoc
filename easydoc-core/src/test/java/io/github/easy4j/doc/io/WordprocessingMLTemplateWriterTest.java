package io.github.easy4j.doc.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WordprocessingMLTemplateWriterTest {

    @Test
    void getWMLTemplateWriterReturnsInstance() {
        WordprocessingMLTemplateWriter writer = WordprocessingMLTemplateWriter.getWMLTemplateWriter();
        assertNotNull(writer);
    }

    @Test
    void getWMLTemplateWriterReturnsSameInstance() {
        WordprocessingMLTemplateWriter a = WordprocessingMLTemplateWriter.getWMLTemplateWriter();
        WordprocessingMLTemplateWriter b = WordprocessingMLTemplateWriter.getWMLTemplateWriter();
        assertSame(a, b);
    }

    @Test
    void writeToStringFromPackageReturnsXml() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLTemplateWriter writer = WordprocessingMLTemplateWriter.getWMLTemplateWriter();
        String xml = writer.writeToString(pkg);
        assertNotNull(xml);
        assertTrue(xml.contains("w:document") || xml.contains("w:body"));
    }

    @Test
    void writeToStringFromFileReturnsContent() throws Exception {
        java.net.URL res = getClass().getClassLoader().getResource("tpl/template.docx");
        if (res == null) return;
        File tpl = new File(res.toURI());
        if (!tpl.exists()) return;
        WordprocessingMLTemplateWriter writer = WordprocessingMLTemplateWriter.getWMLTemplateWriter();
        String content = writer.writeToString(tpl);
        assertNotNull(content);
        assertTrue(content.length() > 0);
    }

    @Test
    void writeToStringFromStringPathReturnsContent() throws Exception {
        java.net.URL res = getClass().getClassLoader().getResource("tpl/template.docx");
        if (res == null) return;
        File tpl = new File(res.toURI());
        if (!tpl.exists()) return;
        WordprocessingMLTemplateWriter writer = WordprocessingMLTemplateWriter.getWMLTemplateWriter();
        String content = writer.writeToString(tpl.getAbsolutePath());
        assertNotNull(content);
        assertTrue(content.length() > 0);
    }

    @Test
    void writeToWriterWritesContent() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        WordprocessingMLTemplateWriter writer = WordprocessingMLTemplateWriter.getWMLTemplateWriter();
        StringWriter sw = new StringWriter();
        writer.writeToWriter(pkg, sw);
        String content = sw.toString();
        assertNotNull(content);
        assertTrue(content.length() > 0);
    }

    @Test
    void writeToStreamWritesBytes() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WordprocessingMLTemplateWriter.writeToStream(pkg, baos);
        assertTrue(baos.size() > 0);
    }

    @Test
    void writeToFileWritesToDisk(@TempDir java.nio.file.Path tempDir) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File outFile = tempDir.resolve("output.docx").toFile();
        WordprocessingMLTemplateWriter.writeToFile(pkg, outFile);
        assertTrue(outFile.exists());
        assertTrue(outFile.length() > 0);
    }

    @Test
    void writeToStreamRejectsNullPackage() {
        assertThrows(IllegalArgumentException.class, () -> {
            WordprocessingMLTemplateWriter.writeToStream(null, new ByteArrayOutputStream());
        });
    }

    @Test
    void writeToStreamRejectsNullOutputStream() {
        assertThrows(IllegalArgumentException.class, () -> {
            WordprocessingMLTemplateWriter.writeToStream(WordprocessingMLPackage.createPackage(), null);
        });
    }

    @Test
    void writeToWriterRejectsNullPackage() {
        assertThrows(IllegalArgumentException.class, () -> {
            WordprocessingMLTemplateWriter.getWMLTemplateWriter()
                .writeToWriter(null, new StringWriter());
        });
    }

    @Test
    void writeToWriterRejectsNullWriter() {
        assertThrows(IllegalArgumentException.class, () -> {
            WordprocessingMLTemplateWriter.getWMLTemplateWriter()
                .writeToWriter(WordprocessingMLPackage.createPackage(), null);
        });
    }
}
