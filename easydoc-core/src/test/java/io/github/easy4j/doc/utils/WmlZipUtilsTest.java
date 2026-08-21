package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WmlZipUtilsTest {

    private static final String TEMPLATE_PATH = "tpl/template.docx";

    @Test
    void unzipExtractsDocx(@TempDir Path tempDir) throws Exception {
        Path source = tempDir.resolve("template.docx");
        try {
            Files.copy(new File(TEMPLATE_PATH).toPath(), source, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            return;
        }
        Path outputDir = tempDir.resolve("out");
        WmlZipUtils.unzip(source.toFile(), outputDir.toFile());
        assertNotNull(outputDir);
        assertTrue(outputDir.toFile().exists());
        assertTrue(Files.exists(outputDir.resolve("[Content_Types].xml")));
    }

    @Test
    void unzipStringOverloadExtractsDocx(@TempDir Path tempDir) throws Exception {
        File src = new File(TEMPLATE_PATH);
        if (!src.exists()) return;
        Path source = tempDir.resolve("template.docx");
        Files.copy(src.toPath(), source, StandardCopyOption.REPLACE_EXISTING);
        Path outputDir = tempDir.resolve("out2");
        WmlZipUtils.unzip(source.toString(), outputDir.toString());
        assertTrue(Files.exists(outputDir.resolve("[Content_Types].xml")));
    }

    @Test
    void zipDirCreatesZipFile(@TempDir Path tempDir) throws Exception {
        // Create a directory with some files
        Path dirToZip = tempDir.resolve("mydir");
        Files.createDirectories(dirToZip);
        Files.write(dirToZip.resolve("file1.txt"), "hello".getBytes());
        Files.write(dirToZip.resolve("file2.txt"), "world".getBytes());

        Path destFile = tempDir.resolve("output.zip");
        WmlZipUtils.zipDir(dirToZip.toFile(), destFile.toFile());
        assertTrue(Files.exists(destFile));
        assertTrue(Files.size(destFile) > 0);
    }

    @Test
    void zipDirWithIncludeInitialFolderFalse(@TempDir Path tempDir) throws Exception {
        Path dirToZip = tempDir.resolve("mydir2");
        Files.createDirectories(dirToZip);
        Files.write(dirToZip.resolve("a.txt"), "content".getBytes());

        Path destFile = tempDir.resolve("output2.zip");
        WmlZipUtils.zipDir(dirToZip.toFile(), destFile.toFile(), false);
        assertTrue(Files.exists(destFile));
    }

    @Test
    void zipDirStringOverloads(@TempDir Path tempDir) throws Exception {
        Path dirToZip = tempDir.resolve("mydir3");
        Files.createDirectories(dirToZip);
        Files.write(dirToZip.resolve("b.txt"), "data".getBytes());

        String destPath = tempDir.resolve("output3.zip").toString();
        WmlZipUtils.zipDir(dirToZip.toString(), destPath);
        assertTrue(Files.exists(Path.of(destPath)));
    }

    @Test
    void zipDirStringOverloadWithIncludeFolder(@TempDir Path tempDir) throws Exception {
        Path dirToZip = tempDir.resolve("mydir4");
        Files.createDirectories(dirToZip);
        Files.write(dirToZip.resolve("c.txt"), "data".getBytes());

        String destPath = tempDir.resolve("output4.zip").toString();
        WmlZipUtils.zipDir(dirToZip.toString(), destPath, true);
        assertTrue(Files.exists(Path.of(destPath)));
    }

    @Test
    void zipDirToOutputStream(@TempDir Path tempDir) throws Exception {
        Path dirToZip = tempDir.resolve("mydir5");
        Files.createDirectories(dirToZip);
        Files.write(dirToZip.resolve("d.txt"), "data".getBytes());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WmlZipUtils.zipDir(dirToZip.toFile(), baos, true);
        assertTrue(baos.size() > 0);
    }

    @Test
    void unzipRejectsPathTraversalEntry(@TempDir Path tempDir) throws Exception {
        // 构造含 ../ 条目的恶意 zip
        Path zipPath = tempDir.resolve("evil.zip");
        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(
                Files.newOutputStream(zipPath))) {
            zos.putNextEntry(new java.util.zip.ZipEntry("../../evil.txt"));
            zos.write("pwned".getBytes());
            zos.closeEntry();
        }
        Path outputDir = tempDir.resolve("out");
        org.junit.jupiter.api.Assertions.assertThrows(java.io.IOException.class,
                () -> WmlZipUtils.unzip(zipPath.toFile(), outputDir.toFile()));
        org.junit.jupiter.api.Assertions.assertFalse(Files.exists(tempDir.resolve("evil.txt")));
    }

    @Test
    void zipOutputIsAValidZipArchive(@TempDir Path tempDir) throws Exception {
        // M-1 回归：ZipFolderHelper 之前不 close ZipOutputStream，产出缺少 END 记录的损坏 zip
        Path dirToZip = tempDir.resolve("zdir");
        Files.createDirectories(dirToZip);
        Files.write(dirToZip.resolve("f.txt"), "hello".getBytes());
        Path destFile = tempDir.resolve("valid.zip");
        WmlZipUtils.zipDir(dirToZip.toFile(), destFile.toFile());
        try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(destFile.toFile())) {
            org.junit.jupiter.api.Assertions.assertNotNull(zf.getEntry("zdir/f.txt"),
                    "zip must contain the entry and be parseable (END record present)");
        }
    }
}
