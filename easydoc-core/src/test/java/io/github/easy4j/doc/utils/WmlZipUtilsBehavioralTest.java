package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Behavioral tests for {@link WmlZipUtils} that verify actual content
 * and structure of zip/unzip operations, not just existence checks.
 */
@DisplayName("WmlZipUtils Behavioral Tests")
class WmlZipUtilsBehavioralTest {

    // ---------------------------------------------------------------
    // unzip round-trip with verification
    // ---------------------------------------------------------------

    @Test
    @DisplayName("unzip extracts files and preserves content")
    void unzipPreservesContent(@TempDir Path tempDir) throws Exception {
        // Create a zip with known content
        Path zipPath = tempDir.resolve("test.zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            zos.putNextEntry(new ZipEntry("file1.txt"));
            zos.write("hello world".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
            zos.putNextEntry(new ZipEntry("file2.txt"));
            zos.write("second file".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        Path outputDir = tempDir.resolve("extracted");
        WmlZipUtils.unzip(zipPath.toFile(), outputDir.toFile());

        // Verify file contents match
        String content1 = Files.readString(outputDir.resolve("file1.txt"));
        assertEquals("hello world", content1, "Extracted file1 content should match");

        String content2 = Files.readString(outputDir.resolve("file2.txt"));
        assertEquals("second file", content2, "Extracted file2 content should match");
    }

    @Test
    @DisplayName("unzip handles directory entries correctly")
    void unzipHandlesDirectoryEntries(@TempDir Path tempDir) throws Exception {
        Path zipPath = tempDir.resolve("dirs.zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            // Directory entry
            zos.putNextEntry(new ZipEntry("subdir/"));
            zos.closeEntry();
            // File inside directory
            zos.putNextEntry(new ZipEntry("subdir/nested.txt"));
            zos.write("nested content".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        Path outputDir = tempDir.resolve("extracted-dirs");
        WmlZipUtils.unzip(zipPath.toFile(), outputDir.toFile());

        assertTrue(Files.isDirectory(outputDir.resolve("subdir")), "subdir should be a directory");
        String nested = Files.readString(outputDir.resolve("subdir/nested.txt"));
        assertEquals("nested content", nested);
    }

    @Test
    @DisplayName("unzip(String, String) overload works correctly")
    void unzipStringOverload(@TempDir Path tempDir) throws Exception {
        Path zipPath = tempDir.resolve("string-test.zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            zos.putNextEntry(new ZipEntry("test.txt"));
            zos.write("data".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        Path outputDir = tempDir.resolve("string-output");
        WmlZipUtils.unzip(zipPath.toString(), outputDir.toString());

        assertTrue(Files.exists(outputDir.resolve("test.txt")));
        assertEquals("data", Files.readString(outputDir.resolve("test.txt")));
    }

    @Test
    @DisplayName("unzip clears output directory before extraction")
    void unzipClearsOutputDir(@TempDir Path tempDir) throws Exception {
        Path outputDir = tempDir.resolve("pre-existing");
        Files.createDirectories(outputDir);
        Files.write(outputDir.resolve("old-file.txt"), "old data".getBytes());

        Path zipPath = tempDir.resolve("new.zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            zos.putNextEntry(new ZipEntry("new-file.txt"));
            zos.write("new data".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        WmlZipUtils.unzip(zipPath.toFile(), outputDir.toFile());

        // Old file should be gone
        assertFalse(Files.exists(outputDir.resolve("old-file.txt")),
                "Old file should be deleted by unzip");
        // New file should exist
        assertEquals("new data", Files.readString(outputDir.resolve("new-file.txt")));
    }

    // ---------------------------------------------------------------
    // zipDir round-trip with verification
    // ---------------------------------------------------------------

    @Test
    @DisplayName("zipDir with includeInitialFolder=true includes root folder in entries")
    void zipDirWithIncludeInitialFolder(@TempDir Path tempDir) throws Exception {
        Path dirToZip = tempDir.resolve("mydir");
        Files.createDirectories(dirToZip);
        Files.write(dirToZip.resolve("a.txt"), "content A".getBytes());
        Files.write(dirToZip.resolve("b.txt"), "content B".getBytes());

        Path destFile = tempDir.resolve("output.zip");
        WmlZipUtils.zipDir(dirToZip.toFile(), destFile.toFile(), true);

        // Verify zip contains entries prefixed with "mydir/"
        try (ZipFile zf = new ZipFile(destFile.toFile())) {
            assertNotNull(zf.getEntry("mydir/a.txt"), "Should contain mydir/a.txt");
            assertNotNull(zf.getEntry("mydir/b.txt"), "Should contain mydir/b.txt");
        }
    }

    @Test
    @DisplayName("zipDir with includeInitialFolder=false omits root folder from entries")
    void zipDirWithoutIncludeInitialFolder(@TempDir Path tempDir) throws Exception {
        Path dirToZip = tempDir.resolve("mydir2");
        Files.createDirectories(dirToZip);
        Files.write(dirToZip.resolve("c.txt"), "content C".getBytes());

        Path destFile = tempDir.resolve("output2.zip");
        WmlZipUtils.zipDir(dirToZip.toFile(), destFile.toFile(), false);

        try (ZipFile zf = new ZipFile(destFile.toFile())) {
            // Without includeInitialFolder, entries should be just "c.txt" not "mydir2/c.txt"
            assertNotNull(zf.getEntry("c.txt"), "Should contain c.txt without root folder prefix");
        }
    }

    @Test
    @DisplayName("zipDir with nested directories preserves structure")
    void zipDirWithNestedDirectories(@TempDir Path tempDir) throws Exception {
        Path dirToZip = tempDir.resolve("nested");
        Path subDir = dirToZip.resolve("sub1").resolve("sub2");
        Files.createDirectories(subDir);
        Files.write(dirToZip.resolve("top.txt"), "top level".getBytes());
        Files.write(subDir.resolve("deep.txt"), "deep level".getBytes());

        Path destFile = tempDir.resolve("nested.zip");
        WmlZipUtils.zipDir(dirToZip.toFile(), destFile.toFile(), true);

        // Unzip and verify structure
        Path extracted = tempDir.resolve("extracted-nested");
        WmlZipUtils.unzip(destFile.toFile(), extracted.toFile());

        assertEquals("top level", Files.readString(extracted.resolve("nested/top.txt")));
        assertEquals("deep level", Files.readString(extracted.resolve("nested/sub1/sub2/deep.txt")));
    }

    @Test
    @DisplayName("zipDir to OutputStream produces valid zip")
    void zipDirToOutputStream(@TempDir Path tempDir) throws Exception {
        Path dirToZip = tempDir.resolve("stream-dir");
        Files.createDirectories(dirToZip);
        Files.write(dirToZip.resolve("stream.txt"), "stream content".getBytes());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WmlZipUtils.zipDir(dirToZip.toFile(), baos, true);

        assertTrue(baos.size() > 0, "Output stream should have content");

        // Write to file and verify it's a valid zip
        Path destFile = tempDir.resolve("from-stream.zip");
        Files.write(destFile, baos.toByteArray());

        try (ZipFile zf = new ZipFile(destFile.toFile())) {
            assertNotNull(zf.getEntry("stream-dir/stream.txt"));
        }
    }

    @Test
    @DisplayName("zipDir default overload (2-arg) includes initial folder")
    void zipDirDefaultOverload(@TempDir Path tempDir) throws Exception {
        Path dirToZip = tempDir.resolve("default-dir");
        Files.createDirectories(dirToZip);
        Files.write(dirToZip.resolve("default.txt"), "default".getBytes());

        Path destFile = tempDir.resolve("default.zip");
        WmlZipUtils.zipDir(dirToZip.toFile(), destFile.toFile());

        try (ZipFile zf = new ZipFile(destFile.toFile())) {
            assertNotNull(zf.getEntry("default-dir/default.txt"));
        }
    }

    @Test
    @DisplayName("zipDir string overloads work correctly")
    void zipDirStringOverloads(@TempDir Path tempDir) throws Exception {
        Path dirToZip = tempDir.resolve("str-dir");
        Files.createDirectories(dirToZip);
        Files.write(dirToZip.resolve("str.txt"), "string".getBytes());

        String destPath = tempDir.resolve("str.zip").toString();
        WmlZipUtils.zipDir(dirToZip.toString(), destPath);

        assertTrue(Files.exists(Path.of(destPath)));

        // Also test 3-arg string overload
        String destPath2 = tempDir.resolve("str2.zip").toString();
        WmlZipUtils.zipDir(dirToZip.toString(), destPath2, false);
        assertTrue(Files.exists(Path.of(destPath2)));
    }

    @Test
    @DisplayName("Zip Slip attack is rejected")
    void zipSlipIsRejected(@TempDir Path tempDir) throws Exception {
        Path zipPath = tempDir.resolve("evil.zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            zos.putNextEntry(new ZipEntry("../../evil.txt"));
            zos.write("pwned".getBytes());
            zos.closeEntry();
        }

        Path outputDir = tempDir.resolve("safe-out");
        assertThrows(IOException.class, () -> {
            WmlZipUtils.unzip(zipPath.toFile(), outputDir.toFile());
        }, "Zip Slip should throw IOException");

        // Verify evil file was NOT created outside output dir
        assertFalse(Files.exists(tempDir.getParent().resolve("evil.txt")));
    }

    @Test
    @DisplayName("Empty zip can be unzipped without error")
    void unzipEmptyZip(@TempDir Path tempDir) throws Exception {
        // Create a zip with at least one entry so the output dir is created
        Path zipPath = tempDir.resolve("minimal.zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            zos.putNextEntry(new ZipEntry("marker.txt"));
            zos.write("marker".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        Path outputDir = tempDir.resolve("minimal-out");
        WmlZipUtils.unzip(zipPath.toFile(), outputDir.toFile());
        assertTrue(Files.exists(outputDir));
        assertTrue(Files.exists(outputDir.resolve("marker.txt")));
    }
}
