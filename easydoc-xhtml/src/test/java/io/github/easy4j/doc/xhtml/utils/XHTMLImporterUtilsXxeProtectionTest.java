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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
	private static final String DBF = "javax.xml.parsers.DocumentBuilderFactory";

	@AfterEach
	void clearProps() {
		System.clearProperty(DTD);
		System.clearProperty(SCHEMA);
		System.clearProperty(DBF);
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

	@Test
	@DisplayName("handle() 拒绝含 DOCTYPE 声明的恶意输入（抛异常而非返回含实体内容的 docx）")
	void handleRejectsDoctypeDeclaration() throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		// 含 DOCTYPE + 外部实体声明的恶意 XHTML
		String maliciousHtml =
			"<!DOCTYPE foo [<!ENTITY x SYSTEM 'file:///etc/passwd'>]>"
			+ "<html><body><p>&x;</p></body></html>";
		assertThrows(Exception.class,
			() -> XHTMLImporterUtils.handle(pkg, Jsoup.parse(maliciousHtml), true, false),
			"含 DOCTYPE 声明的输入必须被拒绝（SAXParseException / XHTMLImportException / Docx4JException）");
	}

	@Test
	@DisplayName("handle() 恢复 caller 预先设置的 DocumentBuilderFactory 系统属性")
	void handleRestoresDocumentBuilderFactoryProperty() throws Exception {
		// caller 预先设置了一个自定义工厂类名
		System.setProperty(DBF, "com.example.OtherFactory");

		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		XHTMLImporterUtils.handle(pkg, Jsoup.parse("<html><body><p>hi</p></body></html>"), true, false);

		// handle 结束后恢复 caller 原值
		assertEquals("com.example.OtherFactory", System.getProperty(DBF),
			"caller 预先设置的 DocumentBuilderFactory 属性必须被恢复");
	}

	@Test
	@DisplayName("handle() 清除 caller 未设置的 DocumentBuilderFactory 系统属性")
	void handleClearsDocumentBuilderFactoryProperty() throws Exception {
		// caller 未设置该属性
		System.clearProperty(DBF);

		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		XHTMLImporterUtils.handle(pkg, Jsoup.parse("<html><body><p>hi</p></body></html>"), true, false);

		// handle 结束后属性被清除（不残留 SecureDocumentBuilderFactory 类名）
		assertNull(System.getProperty(DBF),
			"caller 未设置 DocumentBuilderFactory 属性时，handle() 后必须清除");
	}
}
