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

import java.io.IOException;
import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import io.github.easy4j.doc.xhtml.AbstractStringTemplateWrappingTemplate;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;

/**
 * 该模板仅负责使用Thymeleaf模板引擎将指定模板生成HTML并将HTML转换成XHTML后，作为模板生成WordprocessingMLPackage对象
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLThymeleafTemplate extends AbstractStringTemplateWrappingTemplate {

	protected volatile TemplateEngine engine;
	protected volatile EngineFactory factory;
	private final Renderer renderer = new Renderer();

	@Deprecated
	protected AbstractConfigurableTemplateResolver templateResolver;

	public WordprocessingMLThymeleafTemplate() {
		super();
	}

	public WordprocessingMLThymeleafTemplate(boolean landscape, boolean altChunk) {
		super(landscape, altChunk);
	}

	public WordprocessingMLThymeleafTemplate(WordprocessingMLHtmlTemplate template) {
		super(template);
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
		// EngineFactory 内部同时缓存了解析器副本与 TemplateEngine，置空 factory 即一次性作废全部陈旧状态；
		// 本类的 Renderer 为无状态 final 字段，不持有引擎相关缓存，无需额外重置。
		this.factory = null;
	}

	@Override
	protected String render(String template, Map<String, Object> variables) throws Exception {
		return renderer.render(template, variables, getEngine());
	}
}
