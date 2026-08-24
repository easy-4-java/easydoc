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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

	@Test
	void mergeDocxDeletesTempFileWhenStreamClosed() throws Exception {
		// mergeDocx 返回 DeleteOnCloseFileInputStream：close() 必须立即回收临时文件，
		// 不再依赖 deleteOnExit（长生命周期服务/虚拟线程场景下会无限累积临时文件）
		byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/tpl/template.docx"));
		int before = countTempGeneratedFiles();

		InputStream merged = new Docx4jUtils().mergeDocx(Arrays.asList(new ByteArrayInputStream(bytes)));
		assertNotNull(merged);
		assertEquals(before + 1, countTempGeneratedFiles(),
				"an open merge stream must hold exactly one live temp file");

		merged.close();
		assertEquals(before, countTempGeneratedFiles(),
				"closing the stream must delete the temp file immediately");
	}

	private static int countTempGeneratedFiles() {
		File dir = new File(System.getProperty("java.io.tmpdir"));
		String[] names = dir.list((d, name) -> name.startsWith("generated") && name.endsWith(".docx"));
		return names == null ? 0 : names.length;
	}
}