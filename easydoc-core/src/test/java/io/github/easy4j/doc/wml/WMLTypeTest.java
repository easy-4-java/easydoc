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
package io.github.easy4j.doc.wml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WMLTypeTest {

	@Test
	void enumIsNotEmpty() {
		assertNotNull(WMLType.values());
		assertTrue(WMLType.values().length > 0);
	}

	@Test
	void everyValueHasNonBlankSuffix() {
		for (WMLType t : WMLType.values()) {
			String suffix = t.getSuffix();
			assertNotNull(suffix, t.name() + ".getSuffix() must not be null");
			assertTrue(suffix.length() > 0,
					t.name() + ".getSuffix() must not be blank");
		}
	}

	@Test
	void pdfSuffixIsPdf() {
		assertEquals(".pdf", WMLType.PDF_SUFFIX.getSuffix());
	}

	@Test
	void docxSuffixIsDocx() {
		assertEquals(".docx", WMLType.DOCX_SUFFIX.getSuffix());
	}
}
