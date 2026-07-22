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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import io.github.easy4j.doc.utils.ConfigUtils;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;

import httl.Engine;

/**
 * 该模板仅负责使用Httl模板引擎将指定模板生成HTML并将HTML转换成XHTML后，作为模板生成WordprocessingMLPackage对象
 * @author <a href="https://github.com/easy-4-java">hiwepy</a>
 */
public class WordprocessingMLHttlTemplate implements WordprocessingMLTemplate {
	
	protected Engine engine;
	protected WordprocessingMLHtmlTemplate mlHtmlTemplate;

	public WordprocessingMLHttlTemplate() {
		this(false, false);
	}
	
	public WordprocessingMLHttlTemplate(boolean landscape, boolean altChunk) {
		this.mlHtmlTemplate = new WordprocessingMLHtmlTemplate(landscape, altChunk) ;
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
		// 创建模板输出内容接收对象
		StringWriter output = new StringWriter();
		// 使用Httl模板引擎渲染模板
		getEngine().getTemplate(template).render(variables, output);
		//获取模板渲染后的结果
		String html = output.toString();
		//使用HtmlTemplate进行渲染
		return mlHtmlTemplate.process(html, variables);
	}
	
	public Engine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}
	
	protected synchronized Engine getInternalEngine() throws IOException{
		
		Properties props = ConfigUtils.filterWithPrefix("docx4j.httl.", "docx4j.httl.", Docx4jProperties.getProperties(), false);
        //props.setProperty("filter", "null");
        //props.setProperty("logger", "null");
		props.setProperty("template.directory", props.getProperty("template.directory"));
		props.setProperty("template.suffix", props.getProperty("template.suffix",".httl"));
		props.setProperty("input.encoding", props.getProperty("input.encoding", Docx4jConstants.DEFAULT_CHARSETNAME));
		props.setProperty("output.encoding", props.getProperty("output.encoding", Docx4jConstants.DEFAULT_CHARSETNAME));
		Engine engine = Engine.getEngine(props);
		// 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}

}
