/**
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for Zip Slip protection in {@link WmlZipUtils#unzip(File, File)}.
 */
@DisplayName("WmlZipUtils Zip Slip Protection Tests")
class WmlZipUtilsSlipTest {

	private File tempDir;
	private File outputDir;

	@BeforeEach
	void setUp() throws IOException {
		tempDir = createTempDirectory("ziptest");
		outputDir = new File(tempDir, "output");
	}

	@AfterEach
	void tearDown() {
		deleteRecursive(tempDir);
	}

	@Test
	@DisplayName("unzip rejects path traversal entries (Zip Slip)")
	void unzipRejectsPathTraversalEntry() throws Exception {
		// Create a malicious zip with a path traversal entry
		File maliciousZip = new File(tempDir, "evil.zip");
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(maliciousZip))) {
			ZipEntry entry = new ZipEntry("../../etc/passwd");
			zos.putNextEntry(entry);
			zos.write("malicious content".getBytes());
			zos.closeEntry();
		}

		// This should throw IOException due to Zip Slip protection
		assertThrows(IOException.class,
				() -> WmlZipUtils.unzip(maliciousZip, outputDir),
				"Zip Slip attack must be rejected");
	}

	@Test
	@DisplayName("unzip accepts normal entries")
	void unzipAcceptsNormalEntries() throws Exception {
		// Create a normal zip
		File normalZip = new File(tempDir, "normal.zip");
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(normalZip))) {
			ZipEntry entry = new ZipEntry("document.xml");
			zos.putNextEntry(entry);
			zos.write("<root>hello</root>".getBytes());
			zos.closeEntry();
		}

		// This should succeed
		WmlZipUtils.unzip(normalZip, outputDir);
		File extracted = new File(outputDir, "document.xml");
		assertTrue(extracted.exists(), "normal entry should be extracted");
	}

	@Test
	@DisplayName("unzip accepts nested normal entries")
	void unzipAcceptsNestedNormalEntries() throws Exception {
		// Create a zip with nested but safe entries
		File nestedZip = new File(tempDir, "nested.zip");
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(nestedZip))) {
			ZipEntry entry = new ZipEntry("subdir/document.xml");
			zos.putNextEntry(entry);
			zos.write("<root>nested</root>".getBytes());
			zos.closeEntry();
		}

		// This should succeed
		WmlZipUtils.unzip(nestedZip, outputDir);
		File extracted = new File(outputDir, "subdir/document.xml");
		assertTrue(extracted.exists(), "nested entry should be extracted");
	}

	private static File createTempDirectory(String prefix) throws IOException {
		File temp = File.createTempFile(prefix, "");
		if (!temp.delete() || !temp.mkdirs()) {
			throw new IOException("Failed to create temp directory: " + temp);
		}
		return temp;
	}

	private static void deleteRecursive(File file) {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (File child : children) {
					deleteRecursive(child);
				}
			}
		}
		file.delete();
	}
}
