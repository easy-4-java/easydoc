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

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.WordprocessingMLTemplate;

/**
 * Skeleton for any template-engine wrapper whose output is HTML and is rendered via
 * {@link WordprocessingMLHtmlTemplate}. Concrete subclasses provide only the engine-specific
 * render step via {@link #render(String, Map)}; the File/InputStream→String trampoline,
 * the StringWriter, and the {@code mlHtmlTemplate.process(html, vars)} delegation are shared.
 *
 * <p>Engines with extra fields (Freemarker settings, Thymeleaf resolver, etc.) continue to
 * expose their own {@code getEngine()}/{@code setEngine()}/{@code getInternalEngine()} API.
 *
 * <p>Placed in the {@code xhtml} module rather than {@code core} to avoid a cyclic dependency:
 * the base references {@link WordprocessingMLHtmlTemplate}, which lives here, and every engine
 * module already depends on {@code easydoc-xhtml}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public abstract class AbstractStringTemplateWrappingTemplate implements WordprocessingMLTemplate {

	private final WordprocessingMLHtmlTemplate mlHtmlTemplate;

	protected AbstractStringTemplateWrappingTemplate() {
		this(false, false);
	}

	protected AbstractStringTemplateWrappingTemplate(boolean landscape, boolean altChunk) {
		this(new WordprocessingMLHtmlTemplate(landscape, altChunk));
	}

	protected AbstractStringTemplateWrappingTemplate(WordprocessingMLHtmlTemplate template) {
		if (template == null) {
			throw new IllegalArgumentException("template must not be null");
		}
		this.mlHtmlTemplate = template;
	}

	public WordprocessingMLHtmlTemplate getMlHtmlTemplate() {
		return mlHtmlTemplate;
	}

	@Override
	public WordprocessingMLPackage process(File template, Map<String, Object> variables) throws Exception {
		Charset cs = Charset.forName(Docx4jConstants.DEFAULT_CHARSETNAME);
		String content = (template == null) ? null : new String(Files.readAllBytes(template.toPath()), cs);
		return process(content, variables);
	}

	@Override
	public WordprocessingMLPackage process(InputStream template, Map<String, Object> variables) throws Exception {
		Charset cs = Charset.forName(Docx4jConstants.DEFAULT_CHARSETNAME);
		String content = (template == null) ? null : new String(template.readAllBytes(), cs);
		return process(content, variables);
	}

	@Override
	public WordprocessingMLPackage process(String template, Map<String, Object> variables) throws Exception {
		String html = render(template, variables);
		return mlHtmlTemplate.process(html, variables);
	}

	/**
	 * Engine-specific render: turn {@code template} (raw template content / path) into the
	 * rendered HTML string that {@link WordprocessingMLHtmlTemplate} will convert to docx.
	 *
	 * @param template  template content (typically a template path/name, not raw HTML).
	 * @param variables variables bound during rendering.
	 */
	protected abstract String render(String template, Map<String, Object> variables) throws Exception;
}