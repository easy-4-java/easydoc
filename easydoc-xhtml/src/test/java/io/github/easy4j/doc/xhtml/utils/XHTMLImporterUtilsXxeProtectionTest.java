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
package io.github.easy4j.doc.xhtml.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 契约测试：{@link XHTMLImporterUtils#handle} 在解析期间通过 JAXP 系统属性
 * （javax.xml.accessExternalDTD / accessExternalSchema = ""）真正禁止外部
 * DTD/Schema（openhtmltopdf 的 setProperty 被 JDK 解析器拒绝时的兜底），
 * 且调用结束后恢复调用方原值——不泄漏全局状态。
 */
class XHTMLImporterUtilsXxeProtectionTest {

	private static final String DTD = "javax.xml.accessExternalDTD";
	private static final String SCHEMA = "javax.xml.accessExternalSchema";

	@AfterEach
	void clearProps() {
		System.clearProperty(DTD);
		System.clearProperty(SCHEMA);
	}

	@Test
	@DisplayName("handle() restores pre-existing JAXP property values after parsing")
	void handleRestoresPreexistingPropertyValues() throws Exception {
		System.setProperty(DTD, "file");
		System.setProperty(SCHEMA, "http");

		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		XHTMLImporterUtils.handle(pkg, Jsoup.parse("<html><body><p>hi</p></body></html>"), true, false);

		// 解析结束后恢复调用方原值
		assertEquals("file", System.getProperty(DTD), "pre-existing DTD property must be restored");
		assertEquals("http", System.getProperty(SCHEMA), "pre-existing schema property must be restored");
	}

	@Test
	@DisplayName("handle() clears the properties when the caller had none set")
	void handleClearsWhenCallerHadNone() throws Exception {
		System.clearProperty(DTD);
		System.clearProperty(SCHEMA);

		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		XHTMLImporterUtils.handle(pkg, Jsoup.parse("<html><body><p>hi</p></body></html>"), true, false);

		assertNull(System.getProperty(DTD), "DTD property must be cleared after handle() when caller had none");
		assertNull(System.getProperty(SCHEMA), "schema property must be cleared after handle() when caller had none");
	}

	@Test
	@DisplayName("the resulting package is usable after XXE-protected parsing")
	void producesUsablePackage() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		WordprocessingMLPackage result = XHTMLImporterUtils.handle(
				pkg, Jsoup.parse("<html><body><p>xxe-guard</p></body></html>"), true, false);
		assertTrue(result.getMainDocumentPart().getContent().size() > 0,
				"parsed content must be present in the package");
	}
}