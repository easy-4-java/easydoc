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
package io.github.easy4j.doc.rythm;

import java.io.IOException;
import java.util.Map;

import io.github.easy4j.doc.xhtml.AbstractStringTemplateWrappingTemplate;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;

import org.rythmengine.RythmEngine;

/**
 * 该模板仅负责使用Rythm模板引擎将指定模板生成HTML并将HTML转换成XHTML后，作为模板生成WordprocessingMLPackage对象。
 *
 * <p><b>注意：</b>Rythm 上游已停止维护（最后发布：2015 年，版本 1.4.2）。
 * 功能可用且有测试覆盖，但新项目建议选用 Freemarker / Thymeleaf / Velocity。</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @deprecated since 3.0 — upstream Rythm is unmaintained (last release: 2015).
 *     New projects should use Freemarker, Thymeleaf, or Velocity instead.
 */
@Deprecated(since = "3.0")
public class WordprocessingMLRythmTemplate extends AbstractStringTemplateWrappingTemplate {

	private final EngineFactory factory = new EngineFactory();
	private final Renderer renderer = new Renderer();
	protected volatile RythmEngine engine;

	public WordprocessingMLRythmTemplate() {
		super();
	}

	public WordprocessingMLRythmTemplate(boolean landscape, boolean altChunk) {
		super(landscape, altChunk);
	}

	public WordprocessingMLRythmTemplate(WordprocessingMLHtmlTemplate template) {
		super(template);
	}

	public RythmEngine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(RythmEngine engine) {
		this.engine = engine;
	}

	protected RythmEngine getInternalEngine() throws IOException {
		return factory.get();
	}

	@Override
	protected String render(String template, Map<String, Object> variables) throws Exception {
		return renderer.render(template, variables, getEngine());
	}
}
