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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

class WordprocessingMLJspTemplateTest {

	/** HTML the "container" renders for the JSP; contains an easydoc placeholder. */
	private static final String JSP_RENDERED_HTML =
			"<html><body><p>Hello ${name}</p></body></html>";

	/**
	 * Mock {@link HttpServletRequest} whose {@code getRequestDispatcher} returns a
	 * fake {@link RequestDispatcher} that renders the JSP "template" — resolving
	 * the {@code ${name}} EL against the request attribute that
	 * {@code render(Map)} sets from the caller's variables — and writes the
	 * resulting HTML into the response's writer (simulating a servlet container
	 * executing the JSP).
	 */
	private static HttpServletRequest mockRequestWithDispatcher() {
		// request attributes 的真实存储：render() 用 setAttribute 注入变量，
		// mock dispatcher 用 getAttribute 读取（模拟容器 JSP EL 求值）
		Map<String, Object> attributes = new HashMap<>();
		return (HttpServletRequest) Proxy.newProxyInstance(
				WordprocessingMLJspTemplate.class.getClassLoader(),
				new Class<?>[] { HttpServletRequest.class },
				(proxy, method, args) -> {
					switch (method.getName()) {
						case "getRequestDispatcher":
							return Proxy.newProxyInstance(
									WordprocessingMLJspTemplate.class.getClassLoader(),
									new Class<?>[] { RequestDispatcher.class },
									(dispatcherProxy, dispatcherMethod, dispatcherArgs) -> {
										if (dispatcherMethod.getName().equals("include")) {
											// include(request, response): response 是
											// HttpServletResponseWrapper，getWriter() 写
											// 进 StringWriter；name 取 request attribute
											HttpServletResponse response =
													(HttpServletResponse) dispatcherArgs[1];
											Object name = attributes.get("name");
											String html = name == null
													? JSP_RENDERED_HTML
													: "<html><body><p>Hello " + name + "</p></body></html>";
											response.getWriter().write(html);
											return null;
										}
										return null;
									});
						case "setAttribute":
							attributes.put((String) args[0], args[1]);
							return null;
						case "getAttribute":
							return attributes.get((String) args[0]);
						default:
							return null;
					}
				});
	}

	private static HttpServletRequest mockRequest() {
		return (HttpServletRequest) Proxy.newProxyInstance(
				WordprocessingMLJspTemplate.class.getClassLoader(),
				new Class<?>[] { HttpServletRequest.class },
				(proxy, method, args) -> null);
	}

	private static HttpServletResponse mockResponse() {
		return (HttpServletResponse) Proxy.newProxyInstance(
				WordprocessingMLJspTemplate.class.getClassLoader(),
				new Class<?>[] { HttpServletResponse.class },
				(proxy, method, args) -> null);
	}

	@Test
	void constructorStoresFields() throws Exception {
		HttpServletRequest request = mockRequest();
		HttpServletResponse response = mockResponse();
		String name = "/WEB-INF/views/foo.jsp";
		String requestURL = "/frontStage/foo.jsp";

		WordprocessingMLJspTemplate template = new WordprocessingMLJspTemplate(
				request, response, name, requestURL);

		assertNotNull(template);

		Field requestField = WordprocessingMLJspTemplate.class.getDeclaredField("request");
		requestField.setAccessible(true);
		assertSame(request, requestField.get(template));

		Field responseField = WordprocessingMLJspTemplate.class.getDeclaredField("response");
		responseField.setAccessible(true);
		assertSame(response, responseField.get(template));

		Field nameField = WordprocessingMLJspTemplate.class.getDeclaredField("name");
		nameField.setAccessible(true);
		assertSame(name, nameField.get(template));

		Field urlField = WordprocessingMLJspTemplate.class.getDeclaredField("requestURL");
		urlField.setAccessible(true);
		assertSame(requestURL, urlField.get(template));
	}

	@Test
	void processRendersJspOutputToDocx() throws Exception {
		// 全路径：mock 容器渲染 JSP → 捕获 HTML → 变量替换 → docx。
		// 这是 JSP 渲染路径的真实功能性覆盖（替代此前被禁的空测试）。
		HttpServletRequest request = mockRequestWithDispatcher();
		HttpServletResponse response = mockResponse();
		WordprocessingMLJspTemplate template = new WordprocessingMLJspTemplate(
				request, response, "/WEB-INF/views/hello.jsp", "/frontStage/hello.jsp");

		Map<String, Object> vars = new HashMap<>();
		vars.put("name", "jsp-world");

		WordprocessingMLPackage pkg = template.process("ignored-template-arg", vars);
		assertNotNull(pkg, "JSP render must produce a WordprocessingMLPackage");

		String xml = pkg.getMainDocumentPart().getXML();
		assertTrue(xml.contains("jsp-world"),
				"${name} must be substituted with the variable value in the rendered docx");
		assertTrue(xml.contains("Hello"),
				"the JSP-rendered HTML content must survive the conversion to docx");
	}

	@Test
	void renderCapturesJspOutput() throws Exception {
		// 验证 render() 单独捕获的 HTML 就是 mock 容器写入的内容（不含 docx 转换）
		HttpServletRequest request = mockRequestWithDispatcher();
		HttpServletResponse response = mockResponse();
		WordprocessingMLJspTemplate template = new WordprocessingMLJspTemplate(
				request, response, "/WEB-INF/views/hello.jsp", "/frontStage/hello.jsp");

		String html = template.render(new HashMap<>());
		assertEquals(JSP_RENDERED_HTML, html,
				"render() must capture exactly what the container wrote to the response");
	}
}