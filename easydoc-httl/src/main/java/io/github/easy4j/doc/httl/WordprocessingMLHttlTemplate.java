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
package io.github.easy4j.doc.httl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.docx4j.Docx4jProperties;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.utils.ConfigUtils;
import io.github.easy4j.doc.xhtml.AbstractStringTemplateWrappingTemplate;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;

import httl.Engine;

/**
 * 该模板仅负责使用Httl模板引擎将指定模板生成HTML并将HTML转换成XHTML后，作为模板生成WordprocessingMLPackage对象
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLHttlTemplate extends AbstractStringTemplateWrappingTemplate {

	protected Engine engine;

	public WordprocessingMLHttlTemplate() {
		super();
	}

	public WordprocessingMLHttlTemplate(boolean landscape, boolean altChunk) {
		super(landscape, altChunk);
	}

	public WordprocessingMLHttlTemplate(WordprocessingMLHtmlTemplate template) {
		super(template);
	}

	public Engine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	protected synchronized Engine getInternalEngine() throws IOException{

		Properties props = ConfigUtils.filterWithPrefix("docx4j.httl.", "docx4j.httl.", Docx4jProperties.getProperties(), false);
		props.setProperty("template.directory", props.getProperty("template.directory"));
		props.setProperty("template.suffix", props.getProperty("template.suffix",".httl"));
		props.setProperty("input.encoding", props.getProperty("input.encoding", Docx4jConstants.DEFAULT_CHARSETNAME));
		props.setProperty("output.encoding", props.getProperty("output.encoding", Docx4jConstants.DEFAULT_CHARSETNAME));
		Engine engine = Engine.getEngine(props);
		// 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}

	@Override
	protected String render(String template, Map<String, Object> variables) throws Exception {
		StringWriter output = new StringWriter();
		getEngine().getTemplate(template).render(variables, output);
		return output.toString();
	}
}
