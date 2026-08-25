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
package io.github.easy4j.doc.rythm;

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

import org.rythmengine.RythmEngine;

/**
 * Implementation of wordprocessing m l rythm template functionality.
 *
 * <p><b>Note:</b> Rythm upstream is unmaintained (last release: 2015, version 1.4.2).
 * Functionality is available and test-covered, but new projects should use
 * Freemarker / Thymeleaf / Velocity instead.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @deprecated since 1.0 &mdash; upstream Rythm is unmaintained (last release: 2015).
 *     New projects should use Freemarker, Thymeleaf, or Velocity instead.
 */
@Deprecated
public class WordprocessingMLRythmTemplate implements WordprocessingMLTemplate {

	private final EngineFactory factory = new EngineFactory();
	private final Renderer renderer = new Renderer();
	protected volatile RythmEngine engine;
	protected WordprocessingMLHtmlTemplate mlHtmlTemplate;

	public WordprocessingMLRythmTemplate() {
		this(false, false);
	}

	public WordprocessingMLRythmTemplate(boolean landscape, boolean altChunk) {
		this.mlHtmlTemplate = new WordprocessingMLHtmlTemplate(landscape, altChunk) ;
	}

	public WordprocessingMLRythmTemplate(WordprocessingMLHtmlTemplate template) {
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

	@Override
	public WordprocessingMLPackage process(String template, Map<String, Object> variables) throws Exception {
		//使用Rythm模板引擎渲染模板
		String html = renderer.render(template, variables, getEngine());
		//获取模板渲染后的结果
		//使用HtmlTemplate进行渲染
		return mlHtmlTemplate.process(html, variables);
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

}
