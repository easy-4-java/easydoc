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
package io.github.easy4j.doc.jsp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import io.github.easy4j.doc.jsp.engine.JspConfig;
import io.github.easy4j.doc.jsp.engine.JspEngine;
import io.github.easy4j.doc.utils.ConfigUtils;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of wordprocessing m l jsp template functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLJspTemplate implements WordprocessingMLTemplate {
	
    protected final Logger LOG = LoggerFactory.getLogger(WordprocessingMLJspTemplate.class);
	protected final HttpServletRequest request;
	protected final HttpServletResponse response;
	//要生成html的jsp文件路径（如：/frontStage/articleMenuContent.jsp）,这是实际存在的jsp文件
    protected final String name;
    protected final String requestURL;
    protected JspEngine engine;
    protected WordprocessingMLHtmlTemplate mlHtmlTemplate;
    
    public WordprocessingMLJspTemplate(HttpServletRequest request,HttpServletResponse response,String name, String requestURL) {
		this(request, response, name, requestURL, false, false);
	}
    
	public WordprocessingMLJspTemplate(HttpServletRequest request,HttpServletResponse response,String name, String requestURL, boolean landscape, boolean altChunk) {
		this.request = request;
        this.response = response;
        this.name = name;
        this.requestURL = requestURL;
        this.mlHtmlTemplate = new WordprocessingMLHtmlTemplate(landscape, altChunk) ;
	}
	
	public WordprocessingMLJspTemplate(HttpServletRequest request,HttpServletResponse response,String name, String requestURL, WordprocessingMLHtmlTemplate template) {
		this.request = request;
        this.response = response;
        this.name = name;
        this.requestURL = requestURL;
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
 * Implementation of wordprocessing m l jsp template functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
	@Override
	public WordprocessingMLPackage process(String template, Map<String, Object> variables) throws Exception {
		// 把调用方变量注入 request attributes，让 JSP EL（${name} 等）能在容器
		// 编译执行 JSP 时解析到值。此前 variables 完全未传给容器，模板里
		// 的 EL 表达式永远无法被赋值。
		if (variables != null) {
			for (Map.Entry<String, Object> entry : variables.entrySet()) {
				request.setAttribute(entry.getKey(), entry.getValue());
			}
		}
		// 创建模板输出内容接收对象
		StringWriter output = new StringWriter();
		// 使用Jsp模板引擎渲染模板
		getEngine().getTemplate(request, response, name ).render(requestURL, variables, output);
		//获取模板渲染后的结果
		String html = output.toString();
		//使用HtmlTemplate进行渲染
		return mlHtmlTemplate.process(html, variables);
	}
	
	public JspEngine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(JspEngine engine) {
		this.engine = engine;
	}
	
	protected synchronized JspEngine getInternalEngine() throws IOException{
		Properties ps =  ConfigUtils.filterWithPrefix("docx4j.jsp.", "docx4j.jsp.", Docx4jProperties.getProperties(), false);
		//设置默认的参数
		ps.setProperty(JspConfig.TEMPLATE_SUFFIX, Docx4jProperties.getProperty("docx4j.jsp.template.suffix",".httl"));
		ps.setProperty(JspConfig.INPUT_ENCODING, Docx4jProperties.getProperty("docx4j.jsp.input.encoding", Docx4jConstants.DEFAULT_CHARSETNAME));
		ps.setProperty(JspConfig.OUTPUT_ENCODING, Docx4jProperties.getProperty("docx4j.jsp.output.encoding", Docx4jConstants.DEFAULT_CHARSETNAME));
		JspEngine engine = JspEngine.create(ps);
        // 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}

}
