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
package io.github.easy4j.doc.beetl;

import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordprocessingMLBeetlHelloTest {

	@Test
	void rendersHelloTemplate() throws Exception {
		WordprocessingMLBeetlTemplate t = new WordprocessingMLBeetlTemplate();
		Map<String, Object> vars = Map.of("name", "world");
		WordprocessingMLPackage pkg = t.process("/tpl/hello.btl", vars);
		assertNotNull(pkg);
		assertTrue(pkg.getMainDocumentPart().getXML().contains("Hello world"),
				"rendered docx must contain 'Hello world'");
	}

	@Test
	void setEngineOverridesDefault() throws Exception {
		// 覆盖 getEngine() 的 engine != null 分支（默认走 getInternalEngine()）
		WordprocessingMLBeetlTemplate t = new WordprocessingMLBeetlTemplate();
		org.beetl.core.GroupTemplate gt = t.getEngine();
		t.setEngine(gt);
		assertNotNull(t.getEngine());
	}

	@Test
	void twoArgConstructorPropagatesFlags() {
		WordprocessingMLBeetlTemplate t = new WordprocessingMLBeetlTemplate(true, true);
		assertTrue(t.getMlHtmlTemplate().isLandscape());
		assertTrue(t.getMlHtmlTemplate().isAltChunk());
	}

	@Test
	void templateConstructorPropagatesInstance() {
		io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate html =
				new io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate(false, true);
		WordprocessingMLBeetlTemplate t = new WordprocessingMLBeetlTemplate(html);
		assertNotNull(t.getMlHtmlTemplate());
	}

}