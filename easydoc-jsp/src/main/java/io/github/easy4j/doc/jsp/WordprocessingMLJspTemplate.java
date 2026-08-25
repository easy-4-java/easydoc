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
package io.github.easy4j.doc.jsp;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.nio.file.Files;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 该模板仅负责使用JSP模板引擎将指定模板生成HTML并将HTML转换成XHTML后，作为模板生成WordprocessingMLPackage对象。
 *
 * <p>This implementation delegates rendering to the container's JSP engine via
 * {@link RequestDispatcher#include(HttpServletRequest, HttpServletResponse)} on a
 * servlet-path such as {@code /WEB-INF/views/foo.jsp}. The original hand-rolled
 * JSP engine (~15 files) was replaced by Apache Tomcat's Jasper; the runtime
	 * is responsible for compiling and executing the JSP (Tomcat 10.1.x is required
	 * because this module uses {@code jakarta.servlet}, the Jakarta EE 10 namespace).
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
	protected final WordprocessingMLHtmlTemplate mlHtmlTemplate;

	public WordprocessingMLJspTemplate(HttpServletRequest request, HttpServletResponse response,
									   String name, String requestURL) {
		this(request, response, name, requestURL, false, false);
	}

	public WordprocessingMLJspTemplate(HttpServletRequest request, HttpServletResponse response,
									   String name, String requestURL, boolean landscape, boolean altChunk) {
		this.request = request;
		this.response = response;
		this.name = name;
		this.requestURL = requestURL;
		this.mlHtmlTemplate = new WordprocessingMLHtmlTemplate(landscape, altChunk);
	}

	public WordprocessingMLJspTemplate(HttpServletRequest request, HttpServletResponse response,
									   String name, String requestURL, WordprocessingMLHtmlTemplate template) {
		this.request = request;
		this.response = response;
		this.name = name;
		this.requestURL = requestURL;
		this.mlHtmlTemplate = template;
	}

	@Override
	public WordprocessingMLPackage process(File template, Map<String, Object> variables) throws Exception {
		return this.process(Files.readString(template.toPath()), variables);
	}

	@Override
	public WordprocessingMLPackage process(InputStream template, Map<String, Object> variables) throws Exception {
		return this.process(new String(template.readAllBytes(), StandardCharsets.UTF_8), variables);
	}

	/**
	 * Render the JSP referenced by {@link #requestURL} (set at construction time) into a
	 * string via {@link RequestDispatcher#include} against an {@link HttpServletResponseWrapper}
	 * that captures the output into a {@link StringWriter}, then run the captured HTML
	 * through the underlying {@link WordprocessingMLHtmlTemplate} to produce a docx.
	 */
	@Override
	public WordprocessingMLPackage process(String template, Map<String, Object> variables) throws Exception {
		String html = render(variables);
		return mlHtmlTemplate.process(html, variables);
	}

	protected String render(Map<String, Object> variables) throws Exception {
		// 把调用方变量注入 request attributes，让 JSP EL（${name} 等）能在容器
		// 编译执行 JSP 时解析到值。此前 variables 完全未传给容器，模板里
		// 的 EL 表达式永远无法被赋值。
		if (variables != null) {
			variables.forEach(request::setAttribute);
		}
		StringWriter output = new StringWriter();
		HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(response) {
			@Override
			public PrintWriter getWriter() {
				return new PrintWriter(output);
			}
		};
		RequestDispatcher dispatcher = request.getRequestDispatcher(requestURL);
		if (dispatcher == null) {
			throw new IllegalStateException("No RequestDispatcher for JSP path: " + requestURL);
		}
		LOG.debug("Including JSP via RequestDispatcher: {}", requestURL);
		dispatcher.include(request, wrappedResponse);
		wrappedResponse.getWriter().flush();
		return output.toString();
	}
}
