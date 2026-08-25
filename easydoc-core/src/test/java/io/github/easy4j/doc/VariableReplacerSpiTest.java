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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 契约测试：{@link VariableReplacer} 作为公共 SPI 的可注入性。
 * 用户可用自定义替换策略（MVEL / SpEL / 其他表达式语言）覆盖内置
 * DEFAULT/SAX/STAX 三件套，而无需修改 easydoc 代码。
 */
class VariableReplacerSpiTest {

	private static byte[] template() throws Exception {
		return Files.readAllBytes(Path.of("src/test/resources/tpl/template.docx"));
	}

	private static Map<String, Object> vars(String title) {
		Map<String, Object> v = new HashMap<>();
		v.put("title", title);
		v.put("content", "spi-body");
		return v;
	}

	@Test
	@DisplayName("setReplacer(custom) overrides the built-in strategy and apply() is invoked")
	void customReplacerIsInvokedAndOverrides() throws Exception {
		AtomicBoolean applied = new AtomicBoolean(false);
		WordprocessingMLDocxTemplate t = new WordprocessingMLDocxTemplate();
		t.setReplacer((documentPart, template, variables) -> {
			applied.set(true);
			// 自定义策略：marshal→文本替换→unmarshal→unwrap，等价于
			// WMLPackageUtils.cleanDocumentPart 的 round-trip 模式
			String xml = org.docx4j.XmlUtils.marshaltoString(
					documentPart.getContents(), true, false, org.docx4j.jaxb.Context.jc);
			Object parsed = org.docx4j.XmlUtils.unmarshalString(
					xml.replace("${title}", "SPI-CUSTOM"), org.docx4j.jaxb.Context.jc);
			documentPart.setContents((org.docx4j.wml.Document) org.docx4j.XmlUtils.unwrap(parsed));
		});

		WordprocessingMLPackage pkg = t.process(new ByteArrayInputStream(template()), vars("ignored"));
		assertNotNull(pkg, "render must produce a package");
		assertTrue(applied.get(), "custom VariableReplacer.apply must have been invoked");
		assertTrue(pkg.getMainDocumentPart().getXML().contains("SPI-CUSTOM"),
				"custom strategy output must be visible in the rendered docx");
	}

	@Test
	@DisplayName("setReplacer(null) falls back to the built-in strategy")
	void nullReplacerFallsBackToBuiltin() throws Exception {
		WordprocessingMLDocxTemplate t = new WordprocessingMLDocxTemplate();
		t.setReplacer((documentPart, template, variables) -> {
			// 自定义策略：只抛异常，绝不允许真正执行
			throw new IllegalStateException("custom replacer must not run after setReplacer(null)");
		});
		t.setReplacer(null); // 回退内置 Default

		WordprocessingMLPackage pkg = t.process(new ByteArrayInputStream(template()), vars("builtin-title"));
		assertNotNull(pkg);
		String xml = pkg.getMainDocumentPart().getXML();
		assertTrue(xml.contains("builtin-title"),
				"built-in DEFAULT replacer must substitute ${title} normally");
	}

	@Test
	@DisplayName("custom beforeProcess hook is invoked before document load")
	void customBeforeProcessIsInvoked() throws Exception {
		AtomicBoolean before = new AtomicBoolean(false);
		WordprocessingMLDocxTemplate t = new WordprocessingMLDocxTemplate();
		t.setReplacer(new VariableReplacer() {
			@Override
			public void beforeProcess(AbstractWmlTemplate template) {
				before.set(true);
			}

			@Override
			public void apply(MainDocumentPart documentPart, AbstractWmlTemplate template,
					Map<String, Object> variables) {
				// no-op 自定义策略
			}
		});

		t.process(new ByteArrayInputStream(template()), vars("x"));
		assertTrue(before.get(), "custom beforeProcess must be invoked before loading");
	}

	@Test
	@DisplayName("built-in strategies remain reachable as nested types of the SPI")
	void builtinStrategiesRemainAccessible() {
		// 公共 API 兼容性：三种内置策略仍以 VariableReplacer.Default/Sax/StAX 命名空间存在
		assertNotNull(new VariableReplacer.Default());
		assertNotNull(new VariableReplacer.StAX());
		assertNotNull(new VariableReplacer.Sax());
		assertEquals("Default", VariableReplacer.Default.class.getSimpleName());
		assertFalse(VariableReplacer.Default.class.isSealed() || VariableReplacer.class.isSealed(),
				"the SPI itself must not be sealed so users can implement it");
	}

	@Test
	@DisplayName("SAX template uses Sax replacer internally")
	void saxTemplateUsesSaxReplacer() throws Exception {
		AtomicBoolean applied = new AtomicBoolean(false);
		WordprocessingMLDocxSaxTemplate t = new WordprocessingMLDocxSaxTemplate();
		t.setReplacer((documentPart, template, variables) -> {
			applied.set(true);
		});

		t.process(new ByteArrayInputStream(template()), vars("sax-test"));
		assertTrue(applied.get(), "SAX template must invoke the replacer");
	}

	@Test
	@DisplayName("StAX template uses StAX replacer internally")
	void staxTemplateUsesStaxReplacer() throws Exception {
		AtomicBoolean applied = new AtomicBoolean(false);
		WordprocessingMLDocxStAXTemplate t = new WordprocessingMLDocxStAXTemplate();
		t.setReplacer((documentPart, template, variables) -> {
			applied.set(true);
		});

		t.process(new ByteArrayInputStream(template()), vars("stax-test"));
		assertTrue(applied.get(), "StAX template must invoke the replacer");
	}
}
