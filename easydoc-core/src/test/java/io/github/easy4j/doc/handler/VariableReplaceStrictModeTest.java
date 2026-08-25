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
package io.github.easy4j.doc.handler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.WordprocessingMLDocxStAXTemplate;

/**
 * 严格模式（-Deasydoc.variable.strict=true）契约：
 * <ul>
 *   <li>宽松模式（默认）：未解析占位符原样写进文档（历史行为不变）</li>
 *   <li>严格模式：未解析占位符抛 {@link IllegalStateException}，消息含占位符名，
 *       便于模板作者定位拼写错误</li>
 * </ul>
 */
class VariableReplaceStrictModeTest {

	private static final String STRICT_KEY = "easydoc.variable.strict";

	@AfterEach
	void clearProperty() {
		System.clearProperty(STRICT_KEY);
	}

	@Test
	@DisplayName("lenient mode (default): unresolved placeholder is written verbatim")
	void lenientModeKeepsPlaceholderVerbatim() throws Exception {
		System.clearProperty(STRICT_KEY);
		byte[] templateBytes = Files.readAllBytes(Path.of("src/test/resources/tpl/template.docx"));

		Map<String, Object> vars = new HashMap<>();
		vars.put("title", "lenient-title");
		// 故意不提供 content —— 模板里的该占位符应原样保留

		WordprocessingMLPackage pkg = new WordprocessingMLDocxStAXTemplate()
				.process(new ByteArrayInputStream(templateBytes), vars);
		String xml = pkg.getMainDocumentPart().getXML();
		assertTrue(xml.contains("lenient-title"), "provided variable must be substituted");
		// 未提供的 ${content} 在宽松模式下原样保留（历史行为）
		assertTrue(xml.contains("content"),
				"unresolved placeholder content must still be present (verbatim fallback)");
	}

	@Test
	@DisplayName("strict mode: unresolved placeholder throws IllegalStateException naming the placeholder")
	void strictModeThrowsOnUnresolvedPlaceholder() throws Exception {
		System.setProperty(STRICT_KEY, "true");
		byte[] templateBytes = Files.readAllBytes(Path.of("src/test/resources/tpl/template.docx"));

		Map<String, Object> vars = new HashMap<>();
		vars.put("title", "strict-title");
		// 不提供 map / content —— 模板中第一个无法解析的占位符是 legacy ${#map.title}

		IllegalStateException ex = assertThrows(IllegalStateException.class,
				() -> new WordprocessingMLDocxStAXTemplate()
						.process(new ByteArrayInputStream(templateBytes), vars),
				"strict mode must fail the render when a placeholder cannot be resolved");
		assertTrue(ex.getMessage().contains("#map.title"),
				"exception message must name the unresolved placeholder, got: " + ex.getMessage());
	}

	@Test
	@DisplayName("strict mode: fully-provided variables render normally")
	void strictModeSucceedsWhenAllVariablesProvided() throws Exception {
		System.setProperty(STRICT_KEY, "true");
		byte[] templateBytes = Files.readAllBytes(Path.of("src/test/resources/tpl/template.docx"));

		Map<String, Object> vars = new HashMap<>();
		vars.put("title", "all-good");
		vars.put("content", "all-good-body");
		// 模板含 legacy ${#map.title}：OGNL 的 #map 引用上下文变量 "map"，
		// handler 的 context.putAll(variables) 会把该 key 注入上下文，故提供
		// map={title:...} 即可让该表达式解析成功
		Map<String, Object> mapVar = new HashMap<>();
		mapVar.put("title", "all-good-map");
		vars.put("map", mapVar);

		WordprocessingMLPackage pkg = new WordprocessingMLDocxStAXTemplate()
				.process(new ByteArrayInputStream(templateBytes), vars);
		String xml = pkg.getMainDocumentPart().getXML();
		assertTrue(xml.contains("all-good"), "strict mode must not interfere with successful renders");
	}

	@Test
	@DisplayName("strictMode() reads the system property on each failure path (dynamic)")
	void strictModeFlagIsDynamic() {
		try {
			System.clearProperty(STRICT_KEY);
			assertFalse(VariableReplaceSaTXHandler.strictMode());
			System.setProperty(STRICT_KEY, "true");
			assertTrue(VariableReplaceSaTXHandler.strictMode());
			System.setProperty(STRICT_KEY, "false");
			assertFalse(VariableReplaceSaTXHandler.strictMode());
		} finally {
			System.clearProperty(STRICT_KEY);
		}
	}
}
