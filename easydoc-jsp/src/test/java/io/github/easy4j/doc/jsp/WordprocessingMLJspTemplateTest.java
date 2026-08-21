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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class WordprocessingMLJspTemplateTest {

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

	/**
	 * The full process() path delegates to {@code WordprocessingMLHtmlTemplate},
	 * which transitively touches the docx4j JAXB/MOXy bridge that fails on
	 * docx4j 11.5.14. Disabled until the MOXy migration in easydoc-core lands.
	 */
	@Test
	@Disabled("requires MOXy migration — WordprocessingMLHtmlTemplate.process ultimately calls load(File)")
	void processStringWithNullHttpContextIsDisabled() {
		// intentionally empty — kept as a guard for the moment the MOXy fix lands.
	}
}
