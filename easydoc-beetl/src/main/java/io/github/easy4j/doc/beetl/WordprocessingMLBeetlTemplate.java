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
package io.github.easy4j.doc.beetl;

import java.io.IOException;
import java.util.Map;

import org.beetl.core.GroupTemplate;
import io.github.easy4j.doc.xhtml.AbstractStringTemplateWrappingTemplate;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;

/**
 * 该模板仅负责使用Beetl模板引擎将指定模板生成HTML并将HTML转换成XHTML后，作为模板生成WordprocessingMLPackage对象
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLBeetlTemplate extends AbstractStringTemplateWrappingTemplate {

	private final EngineFactory factory = new EngineFactory();
	private final Renderer renderer = new Renderer();
	protected volatile GroupTemplate engine;

	public WordprocessingMLBeetlTemplate() {
		super();
	}

	public WordprocessingMLBeetlTemplate(boolean landscape, boolean altChunk) {
		super(landscape, altChunk);
	}

	public WordprocessingMLBeetlTemplate(WordprocessingMLHtmlTemplate template) {
		super(template);
	}

	public GroupTemplate getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(GroupTemplate engine) {
		this.engine = engine;
	}

	protected GroupTemplate getInternalEngine() throws IOException {
		return factory.get();
	}

	@Override
	protected String render(String template, Map<String, Object> variables) throws Exception {
		return renderer.render(template, variables, getEngine());
	}
}
