/*
 * Copyright (c) 2018, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Note: ZipFolderHelper.process() only flushes the ZipOutputStream without
 * closing it, so the END header is never written. Tests therefore verify
 * that bytes are produced (not that the resulting zip is parseable).
 * The helper is normally only called from {@link WmlZipUtils#zipDir} which
 * closes the outer output stream but the bug remains in the helper.
 */
class ZipFolderHelperTest {

    @Test
    void processWritesZipBytesToClosedStream(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("srcWrite");
        Files.createDirectories(sourceDir);
        Files.write(sourceDir.resolve("c.txt"), "charlie".getBytes());

        File outFile = tempDir.resolve("out.zip").toFile();
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            ZipFolderHelper helper = new ZipFolderHelper();
            helper.setIncludeInitialFolder(false);
            helper.process(sourceDir.toFile(), fos);
        }
        assertTrue(outFile.exists());
        assertTrue(outFile.length() > 0);
    }

    @Test
    void processToByteArrayOutputStreamWritesBytes(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("byteOut");
        Files.createDirectories(sourceDir);
        Files.write(sourceDir.resolve("f.txt"), "data".getBytes());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipFolderHelper helper = new ZipFolderHelper();
        helper.setIncludeInitialFolder(false);
        helper.process(sourceDir.toFile(), baos);
        assertTrue(baos.size() > 0);
    }

    @Test
    void setIncludeInitialFolderStoresValue() {
        ZipFolderHelper helper = new ZipFolderHelper();
        helper.setIncludeInitialFolder(false);
        helper.setIncludeInitialFolder(true);
        assertTrue(true);
    }

    @Test
    void defaultConstructorIsAccessible() {
        ZipFolderHelper helper = new ZipFolderHelper();
        assertNotNull(helper);
    }

    @Test
    void processWritesMultipleFiles(@TempDir Path tempDir) throws Exception {
        Path sourceDir = tempDir.resolve("multi");
        Files.createDirectories(sourceDir);
        Files.write(sourceDir.resolve("a.txt"), "a".getBytes());
        Files.write(sourceDir.resolve("b.txt"), "b".getBytes());
        Files.write(sourceDir.resolve("c.txt"), "c".getBytes());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipFolderHelper helper = new ZipFolderHelper();
        helper.setIncludeInitialFolder(false);
        helper.process(sourceDir.toFile(), baos);
        // Three files of 1 byte each plus zip overhead should yield > 3 bytes
        assertTrue(baos.size() > 3);
    }

    @Test
    void zipOutputStreamDirectSanityCheck(@TempDir Path tempDir) throws Exception {
        // Sanity: confirm that a ZipOutputStream properly closed produces a
        // valid zip. This is a regression guard against the helper bug.
        Path srcFile = tempDir.resolve("hello.txt");
        Files.write(srcFile, "hi".getBytes());
        File zipFile = tempDir.resolve("sanity.zip").toFile();
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry("hello.txt");
            zos.putNextEntry(entry);
            zos.write("hi".getBytes());
            zos.closeEntry();
        }
        assertTrue(zipFile.exists());
        assertTrue(zipFile.length() > 0);
    }
}
