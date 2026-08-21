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
package io.github.easy4j.doc.thymeleaf;

import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordprocessingMLThymeleafHelloTest {

	@Test
	void rendersHelloTemplate() throws Exception {
		// 测试资源 docx4j.properties 配置 UrlTemplateResolver + 文件系统 prefix，
		// 对 classpath 模板不适用；此处切换为 ClassLoaderTemplateResolver 并清空 prefix，
		// 使 /tpl/hello.html 从 test-classes 解析
		java.util.Properties props = org.docx4j.Docx4jProperties.getProperties();
		String prevResolver = props.getProperty("docx4j.thymeleaf.templateResolver");
		String prevPrefix = props.getProperty("docx4j.thymeleaf.prefix");
		props.setProperty("docx4j.thymeleaf.templateResolver",
				"org.thymeleaf.templateresolver.ClassLoaderTemplateResolver");
		props.setProperty("docx4j.thymeleaf.prefix", "");
		try {
			WordprocessingMLThymeleafTemplate t = new WordprocessingMLThymeleafTemplate();
			Map<String, Object> vars = Map.of("name", "world");
			WordprocessingMLPackage pkg = t.process("/tpl/hello.html", vars);
			assertNotNull(pkg);
			assertTrue(pkg.getMainDocumentPart().getXML().contains("Hello world"),
					"rendered docx must contain 'Hello world'");
		} finally {
			props.remove("docx4j.thymeleaf.templateResolver");
			props.remove("docx4j.thymeleaf.prefix");
			if (prevResolver != null) {
				props.setProperty("docx4j.thymeleaf.templateResolver", prevResolver);
			}
			if (prevPrefix != null) {
				props.setProperty("docx4j.thymeleaf.prefix", prevPrefix);
			}
		}
	}

}