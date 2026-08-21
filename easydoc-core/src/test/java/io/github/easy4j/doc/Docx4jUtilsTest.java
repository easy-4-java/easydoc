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
package io.github.easy4j.doc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.utils.Docx4jUtils;

class Docx4jUtilsTest {

	@Test
	void getTempPathReturnsTempDirPath() {
		assertTrue(Docx4jUtils.getTempPath().startsWith(System.getProperty("java.io.tmpdir")));
	}

	@Test
	void mergeDocxEmptyListReturnsNull() throws Exception {
		Docx4jUtils utils = new Docx4jUtils();
		assertNull(utils.mergeDocx(Collections.emptyList()));
	}

	@Test
	void mergeDocxSingleStreamRoundTrips_C4Regression() throws Exception {
		byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/tpl/template.docx"));
		InputStream in = new ByteArrayInputStream(bytes);
		Docx4jUtils utils = new Docx4jUtils();
		InputStream merged = utils.mergeDocx(Arrays.asList(in));
		assertNotNull(merged);
		try {
			ByteArrayOutputStream sink = new ByteArrayOutputStream();
			byte[] chunk = new byte[4096];
			int n;
			while ((n = merged.read(chunk)) > 0) {
				sink.write(chunk, 0, n);
			}
			byte[] mergedBytes = sink.toByteArray();
			assertTrue(mergedBytes.length > 0, "merged docx stream must be non-empty");
			try (ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(mergedBytes))) {
				assertNotNull(zin.getNextEntry(),
						"merged output must be a valid ZIP container (i.e. a docx)");
			}
		} finally {
			merged.close();
		}
	}
}