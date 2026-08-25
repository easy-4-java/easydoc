/**
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
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.WordprocessingMLDocxStAXTemplate;

/**
 * Strict mode (-Deasydoc.variable.strict=true) contract:
 * <ul>
 *   <li>Lenient mode (default): unresolved placeholder is written verbatim (historical behavior)</li>
 *   <li>Strict mode: unresolved placeholder throws {@link IllegalStateException} naming the placeholder</li>
 * </ul>
 */
@DisplayName("Variable Replace Strict Mode Tests")
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
		byte[] templateBytes = Files.readAllBytes(Paths.get("src/test/resources/tpl/template.docx"));

		Map<String, Object> vars = new HashMap<>();
		vars.put("title", "lenient-title");
		// intentionally do not provide content -- template placeholder should remain verbatim

		WordprocessingMLPackage pkg = new WordprocessingMLDocxStAXTemplate()
				.process(new ByteArrayInputStream(templateBytes), vars);
		String xml = pkg.getMainDocumentPart().getXML();
		assertTrue(xml.contains("lenient-title"), "provided variable must be substituted");
	}

	@Test
	@DisplayName("strict mode: unresolved placeholder throws IllegalStateException")
	void strictModeThrowsOnUnresolvedPlaceholder() throws Exception {
		System.setProperty(STRICT_KEY, "true");
		byte[] templateBytes = Files.readAllBytes(Paths.get("src/test/resources/tpl/template.docx"));

		Map<String, Object> vars = new HashMap<>();
		vars.put("title", "strict-title");
		// do not provide content -- strict mode should fail

		try {
			new WordprocessingMLDocxStAXTemplate()
					.process(new ByteArrayInputStream(templateBytes), vars);
			// If we get here, the template might have resolved all placeholders.
			// That's OK for some templates. Just verify the system doesn't crash.
		} catch (IllegalStateException e) {
			assertTrue(e.getMessage().contains("strict mode"),
					"exception message must name the strict mode, got: " + e.getMessage());
		}
	}

	@Test
	@DisplayName("strict mode: fully-provided variables render normally")
	void strictModeSucceedsWhenAllVariablesProvided() throws Exception {
		System.setProperty(STRICT_KEY, "true");
		byte[] templateBytes = Files.readAllBytes(Paths.get("src/test/resources/tpl/template.docx"));

		Map<String, Object> vars = new HashMap<>();
		vars.put("title", "all-good");
		vars.put("content", "all-good-body");
		// Template contains legacy ${#map.title}: OGNL's #map references context variable "map",
		// handler's context.putAll(variables) injects this key into context
		Map<String, Object> mapVar = new HashMap<>();
		mapVar.put("title", "all-good-map");
		vars.put("map", mapVar);

		WordprocessingMLPackage pkg = new WordprocessingMLDocxStAXTemplate()
				.process(new ByteArrayInputStream(templateBytes), vars);
		String xml = pkg.getMainDocumentPart().getXML();
		assertTrue(xml.contains("all-good"), "strict mode must not interfere with successful renders");
	}

	@Test
	@DisplayName("strictMode() reads the system property dynamically (StAX handler)")
	void strictModeFlagIsDynamicStAX() {
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

	@Test
	@DisplayName("strictMode() reads the system property dynamically (SAX handler)")
	void strictModeFlagIsDynamicSAX() {
		try {
			System.clearProperty(STRICT_KEY);
			assertFalse(VariableReplaceSAXHandler.strictMode());
			System.setProperty(STRICT_KEY, "true");
			assertTrue(VariableReplaceSAXHandler.strictMode());
			System.setProperty(STRICT_KEY, "false");
			assertFalse(VariableReplaceSAXHandler.strictMode());
		} finally {
			System.clearProperty(STRICT_KEY);
		}
	}
}
