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
package io.github.easy4j.doc.httl;

import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordprocessingMLHttlHelloTest {

	@Test
	@Disabled("requires MOXy migration — see easydoc-core/pom.xml TODO")
	void rendersHelloTemplate() throws Exception {
		WordprocessingMLHttlTemplate t = new WordprocessingMLHttlTemplate();
		Map<String, Object> vars = Map.of("name", "world");
		WordprocessingMLPackage pkg = t.process("hello.htt", vars);
		assertNotNull(pkg);
		assertTrue(pkg.getMainDocumentPart().getXML().contains("Hello world"),
				"rendered docx must contain 'Hello world'");
	}

}