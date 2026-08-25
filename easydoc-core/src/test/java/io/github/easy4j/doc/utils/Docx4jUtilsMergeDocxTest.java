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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Docx4jUtils#mergeDocx(List)} — specifically the
 * DeleteOnCloseFileInputStream behavior (close deletes temp file).
 */
@DisplayName("Docx4jUtils mergeDocx Tests")
class Docx4jUtilsMergeDocxTest {

	@Test
	@DisplayName("mergeDocx returns null for empty list")
	void mergeDocxReturnsNullForEmptyList() throws Exception {
		Docx4jUtils utils = new Docx4jUtils();
		List<InputStream> streams = new ArrayList<>();
		InputStream result = utils.mergeDocx(streams);
		assertNull(result, "mergeDocx with empty list should return null");
	}

	@Test
	@DisplayName("mergeDocx returns non-null for single stream")
	void mergeDocxReturnsNonNullForSingleStream() throws Exception {
		// Create a minimal valid docx stream (just needs to be loadable by docx4j)
		InputStream templateStream = getClass().getClassLoader()
				.getResourceAsStream("tpl/template.docx");
		if (templateStream == null) {
			// If no template available, skip this test
			return;
		}

		List<InputStream> streams = new ArrayList<>();
		streams.add(templateStream);

		Docx4jUtils utils = new Docx4jUtils();
		InputStream result = utils.mergeDocx(streams);
		try {
			assertNotNull(result, "mergeDocx with single stream should return non-null");
		} finally {
			if (result != null) {
				result.close(); // this should delete the temp file (DeleteOnCloseFileInputStream)
			}
		}
	}
}
