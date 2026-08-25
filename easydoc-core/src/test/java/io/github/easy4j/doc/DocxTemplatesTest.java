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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class DocxTemplatesTest {

	@Test
	void createNullReturnsWordprocessingMLDocxTemplate() {
		WordprocessingMLTemplate template = DocxTemplates.create(null);
		assertNotNull(template);
		assertInstanceOf(WordprocessingMLDocxTemplate.class, template);
	}

	@Test
	void createDefaultReturnsWordprocessingMLDocxTemplate() {
		WordprocessingMLTemplate template = DocxTemplates.create(DocxMode.DEFAULT);
		assertNotNull(template);
		assertInstanceOf(WordprocessingMLDocxTemplate.class, template);
	}

	@Test
	@SuppressWarnings("deprecation")
	void createSAXReturnsCorrectTypeForJdkVersion() {
		WordprocessingMLTemplate template = DocxTemplates.create(DocxMode.SAX);
		assertNotNull(template);
		if (Runtime.version().feature() >= 21) {
			// JDK 21+ factory short-circuits SAX → STAX
			assertInstanceOf(WordprocessingMLDocxStAXTemplate.class, template);
		} else {
			assertInstanceOf(WordprocessingMLDocxSaxTemplate.class, template);
		}
	}

	@Test
	void createSTAXReturnsWordprocessingMLDocxStAXTemplate() {
		WordprocessingMLTemplate template = DocxTemplates.create(DocxMode.STAX);
		assertNotNull(template);
		assertInstanceOf(WordprocessingMLDocxStAXTemplate.class, template);
	}
}
