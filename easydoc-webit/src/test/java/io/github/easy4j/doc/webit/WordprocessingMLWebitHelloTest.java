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
package io.github.easy4j.doc.webit;

import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordprocessingMLWebitHelloTest {

	@Test
	void rendersHelloTemplate() throws Exception {
		// webit script 默认 looseVar=false，未声明变量会报 "Can't locate vars"；
		// 测试模板是单变量 hello 模板，开启 looseVar 以允许未声明变量
		java.util.Properties props = org.docx4j.Docx4jProperties.getProperties();
		String prev = props.getProperty("docx4j.webit.engine.looseVar");
		props.setProperty("docx4j.webit.engine.looseVar", "true");
		try {
			WordprocessingMLWebitTemplate t = new WordprocessingMLWebitTemplate();
			Map<String, Object> vars = Map.of("name", "world");
			WordprocessingMLPackage pkg = t.process("/tpl/hello.html", vars);
			assertNotNull(pkg);
			assertTrue(pkg.getMainDocumentPart().getXML().contains("Hello world"),
					"rendered docx must contain 'Hello world'");
		} finally {
			props.remove("docx4j.webit.engine.looseVar");
			if (prev != null) {
				props.setProperty("docx4j.webit.engine.looseVar", prev);
			}
		}
	}

}