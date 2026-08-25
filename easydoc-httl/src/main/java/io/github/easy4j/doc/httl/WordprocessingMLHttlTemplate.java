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
package io.github.easy4j.doc.httl;

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

import httl.Engine;

/**
 * Implementation of wordprocessing m l httl template functionality.
 *
 * <p><b>Note:</b> HTTL upstream is unmaintained (last release: 2014, version 1.0.12).
 * Functional and test-covered, but new projects should prefer Freemarker / Thymeleaf / Velocity.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @deprecated since 2.0 — upstream HTTL is unmaintained (last release: 2014).
 *     New projects should use Freemarker, Thymeleaf, or Velocity instead.
 */
@Deprecated(since = "2.0")
public class WordprocessingMLHttlTemplate implements WordprocessingMLTemplate {

	private final EngineFactory factory = new EngineFactory();
	private final Renderer renderer = new Renderer();
	protected volatile Engine engine;
	protected WordprocessingMLHtmlTemplate mlHtmlTemplate;

	public WordprocessingMLHttlTemplate() {
		this(false, false);
	}

	public WordprocessingMLHttlTemplate(boolean landscape, boolean altChunk) {
		this.mlHtmlTemplate = new WordprocessingMLHtmlTemplate(landscape, altChunk);
	}

	public WordprocessingMLHttlTemplate(WordprocessingMLHtmlTemplate template) {
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
	 * 使用Httl模板引擎渲染模板
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

	public Engine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	protected Engine getInternalEngine() throws IOException {
		return factory.get();
	}

}
