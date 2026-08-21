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
package io.github.easy4j.doc.xhtml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Coverage for {@link AbstractStringTemplateWrappingTemplate} via a minimal stub
 * subclass. The abstract class is the shared trampoline for every engine adapter
 * (Freemarker, Thymeleaf, Velocity, Beetl, ...), so verifying its constructors and
 * the protected render() contract here protects the whole template-engine family.
 *
 * <p>The full {@code process(String, Map)} path runs through XHTMLImporterImpl and
 * is therefore {@link Disabled}; pure construction / accessor tests run normally.
 */
class AbstractStringTemplateWrappingTemplateTest {

	/** Minimal subclass — echoes the template back so the trampoline has something to convert. */
	private static final class StubTemplate extends AbstractStringTemplateWrappingTemplate {
		StubTemplate() {
			super();
		}

		StubTemplate(boolean landscape, boolean altChunk) {
			super(landscape, altChunk);
		}

		StubTemplate(WordprocessingMLHtmlTemplate template) {
			super(template);
		}

		@Override
		protected String render(String template, Map<String, Object> variables) {
			// Echo: enough to exercise the trampoline without any actual engine.
			return template;
		}
	}

	// ---------------------------------------------------------------------
	// Constructor propagation — pure state.
	// ---------------------------------------------------------------------

	@Test
	void defaultConstructorHasDefaultLandscapeAndAltChunk() {
		StubTemplate stub = new StubTemplate();
		WordprocessingMLHtmlTemplate inner = stub.getMlHtmlTemplate();
		assertNotNull(inner, "default ctor must install a non-null WordprocessingMLHtmlTemplate");
		assertEquals(false, inner.isLandscape(), "default landscape should be false");
		assertEquals(false, inner.isAltChunk(), "default altChunk should be false");
	}

	@Test
	void twoArgConstructorPropagates() {
		StubTemplate stub = new StubTemplate(true, true);
		WordprocessingMLHtmlTemplate inner = stub.getMlHtmlTemplate();
		assertNotNull(inner);
		assertTrue(inner.isLandscape(), "landscape flag should be propagated from ctor");
		assertTrue(inner.isAltChunk(), "altChunk flag should be propagated from ctor");
	}

	@Test
	void templateConstructorPropagatesInstance() {
		WordprocessingMLHtmlTemplate tpl = new WordprocessingMLHtmlTemplate(false, false);
		StubTemplate stub = new StubTemplate(tpl);
		assertSame(tpl, stub.getMlHtmlTemplate(),
				"ctor must keep the supplied WordprocessingMLHtmlTemplate instance");
	}

	@Test
	void nullTemplateConstructorThrowsIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class,
				() -> new StubTemplate((WordprocessingMLHtmlTemplate) null));
	}

	// ---------------------------------------------------------------------
	// Accessor + process() trampoline.
	// ---------------------------------------------------------------------

	@Test
	void getMlHtmlTemplateAccessor() {
		StubTemplate stub = new StubTemplate();
		assertNotNull(stub.getMlHtmlTemplate());
	}

	@Test
	@Disabled("process() trampoline ultimately calls XHTMLImporterImpl — requires valid docx4j JAXB context")
	void processStringReturnsNonNullPackage() throws Exception {
		Object pkg = new StubTemplate().process("<html><body><p>hi</p></body></html>", null);
		assertNotNull(pkg);
	}
}
