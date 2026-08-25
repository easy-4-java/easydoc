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
package io.github.easy4j.doc.thymeleaf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;

/**
 * Implementation of wordprocessing m l thymeleaf template functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLThymeleafTemplate implements WordprocessingMLTemplate {

	protected volatile TemplateEngine engine;
	protected volatile EngineFactory factory;
	private final Renderer renderer = new Renderer();
	protected WordprocessingMLHtmlTemplate mlHtmlTemplate;

	@Deprecated
	protected AbstractConfigurableTemplateResolver templateResolver;

	public WordprocessingMLThymeleafTemplate() {
		this(false, false);
	}

	public WordprocessingMLThymeleafTemplate(boolean landscape, boolean altChunk) {
		this.mlHtmlTemplate = new WordprocessingMLHtmlTemplate(landscape, altChunk);
	}

	public WordprocessingMLThymeleafTemplate(WordprocessingMLHtmlTemplate template) {
		this.mlHtmlTemplate = template;
	}

	@Override
	public WordprocessingMLPackage process(File template, Map<String, Object> variables) throws Exception {
		return this.process(FileUtils.readFileToString(template, StandardCharsets.UTF_8), variables);
	}

	@Override
	public WordprocessingMLPackage process(InputStream template, Map<String, Object> variables) throws Exception {
		return this.process(IOUtils.toString(template, StandardCharsets.UTF_8), variables);
	}

	/**
	 * 使用Thymeleaf模板引擎渲染模板
	 * @param template ：模板内容
	 * @param variables ：变量
	 * @return {@link WordprocessingMLPackage} 对象
	 * @throws Exception ：异常对象
	 */
	@Override
	public WordprocessingMLPackage process(String template, Map<String, Object> variables) throws Exception {
		String html = renderer.render(template, variables, getEngine());
		return mlHtmlTemplate.process(html, variables);
	}

	public TemplateEngine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(TemplateEngine engine) {
		this.engine = engine;
	}

	protected TemplateEngine getInternalEngine() throws IOException {
		EngineFactory f = factory;
		if (f == null) {
			synchronized (this) {
				f = factory;
				if (f == null) {
					f = new EngineFactory(templateResolver);
					factory = f;
				}
			}
		}
		return f.get();
	}

	/**
	 * @deprecated configure via {@link EngineFactory} instead
	 */
	@Deprecated
	public AbstractConfigurableTemplateResolver getTemplateResolver() {
		return templateResolver;
	}

	/**
	 * @deprecated configure via {@link EngineFactory} instead
	 */
	@Deprecated
	public void setTemplateResolver(AbstractConfigurableTemplateResolver templateResolver) {
		this.templateResolver = templateResolver;
		this.factory = null;
	}

}
