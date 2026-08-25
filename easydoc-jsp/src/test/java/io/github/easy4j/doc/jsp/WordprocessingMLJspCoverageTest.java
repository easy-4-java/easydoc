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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Additional tests to push JaCoCo line coverage of
 * {@link WordprocessingMLJspTemplate} above 90 % and verify
 * request-attribute injection for JSP EL visibility.
 *
 * <p>Note: 2.0.x's {@code JspTemplateImpl.doInterpret()} obtains the
 * {@link RequestDispatcher} via
 * {@code request.getSession().getServletContext().getRequestDispatcher(path)},
 * so mocks must chain through {@link HttpSession} and {@link ServletContext}.
 */
class WordprocessingMLJspCoverageTest {

    private static final String SIMPLE_HTML =
            "<html><body><p>Hello world</p></body></html>";

    // ---- mock helpers ----

    private static HttpServletResponse mockResponse() {
        return (HttpServletResponse) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { HttpServletResponse.class },
                (proxy, method, args) -> null);
    }

    /**
     * Mock {@link RequestDispatcher} that writes {@link #SIMPLE_HTML} to the
     * response writer when {@code include()} is called.
     */
    private static RequestDispatcher mockRequestDispatcher() {
        return (RequestDispatcher) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { RequestDispatcher.class },
                (proxy, method, args) -> {
                    if ("include".equals(method.getName())) {
                        HttpServletResponse resp = (HttpServletResponse) args[1];
                        PrintWriter writer = resp.getWriter();
                        writer.write(SIMPLE_HTML);
                        writer.flush();
                    }
                    return null;
                });
    }

    /**
     * Build a mock {@link HttpServletRequest} whose chain
     * {@code getSession().getServletContext().getRequestDispatcher(path)}
     * returns a working dispatcher. Attribute captures go into the supplied map.
     */
    private static HttpServletRequest mockRequest(Map<String, Object> capturedAttributes) {
        // Mock ServletContext: getRequestDispatcher returns our mock dispatcher
        ServletContext mockContext = (ServletContext) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { ServletContext.class },
                (proxy, method, args) -> {
                    if ("getRequestDispatcher".equals(method.getName())) {
                        return mockRequestDispatcher();
                    }
                    return null;
                });

        // Mock HttpSession: getServletContext returns our mock context
        HttpSession mockSession = (HttpSession) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { HttpSession.class },
                (proxy, method, args) -> {
                    if ("getServletContext".equals(method.getName())) {
                        return mockContext;
                    }
                    return null;
                });

        // Mock HttpServletRequest: getSession + setAttribute
        return (HttpServletRequest) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class },
                (proxy, method, args) -> {
                    if ("getSession".equals(method.getName())) {
                        return mockSession;
                    }
                    if ("setAttribute".equals(method.getName())) {
                        if (capturedAttributes != null) {
                            capturedAttributes.put((String) args[0], args[1]);
                        }
                        return null;
                    }
                    return null;
                });
    }

    /**
     * Mock request with no dispatcher (getSession().getServletContext()
     * returns a context whose getRequestDispatcher returns null).
     */
    private static HttpServletRequest mockRequestWithNullDispatcher() {
        ServletContext mockContext = (ServletContext) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { ServletContext.class },
                (proxy, method, args) -> null);

        HttpSession mockSession = (HttpSession) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { HttpSession.class },
                (proxy, method, args) -> {
                    if ("getServletContext".equals(method.getName())) {
                        return mockContext;
                    }
                    return null;
                });

        return (HttpServletRequest) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class },
                (proxy, method, args) -> {
                    if ("getSession".equals(method.getName())) {
                        return mockSession;
                    }
                    return null;
                });
    }

    // ---- 5-arg constructor (with WordprocessingMLHtmlTemplate) ----

    @Test
    void constructorWithHtmlTemplateStoresFields() throws Exception {
        HttpServletRequest request = mockRequest(null);
        HttpServletResponse response = mockResponse();
        WordprocessingMLHtmlTemplate htmlTemplate = new WordprocessingMLHtmlTemplate(true, false);

        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/name", "/url", htmlTemplate);

        Field mlHtmlField = WordprocessingMLJspTemplate.class.getDeclaredField("mlHtmlTemplate");
        mlHtmlField.setAccessible(true);
        assertSame(htmlTemplate, mlHtmlField.get(t));
    }

    // ---- 6-arg constructor (with landscape/altChunk) ----

    @Test
    void constructorWithLandscapeAndAltChunk() throws Exception {
        HttpServletRequest request = mockRequest(null);
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/name", "/url", true, true);

        Field mlHtmlField = WordprocessingMLJspTemplate.class.getDeclaredField("mlHtmlTemplate");
        mlHtmlField.setAccessible(true);
        WordprocessingMLHtmlTemplate htmlTemplate =
                (WordprocessingMLHtmlTemplate) mlHtmlField.get(t);
        assertNotNull(htmlTemplate);
    }

    // ---- process(String, Map) full render path ----

    @Test
    void processStringRendersThroughRequestDispatcher() throws Exception {
        HttpServletRequest request = mockRequest(null);
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/WEB-INF/views/hello.jsp", "/render");

        WordprocessingMLPackage pkg = t.process("/render", java.util.Map.of());
        assertNotNull(pkg);
    }

    // ---- process(File, Map) delegates to process(String, Map) ----

    @Test
    void processFileDelegatesToString(@TempDir File tempDir) throws Exception {
        HttpServletRequest request = mockRequest(null);
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/WEB-INF/views/hello.jsp", "/render");

        File htmlFile = new File(tempDir, "test.html");
        Files.write(htmlFile.toPath(), SIMPLE_HTML.getBytes(StandardCharsets.UTF_8));

        WordprocessingMLPackage pkg = t.process(htmlFile, java.util.Map.of());
        assertNotNull(pkg);
    }

    // ---- process(InputStream, Map) delegates to process(String, Map) ----

    @Test
    void processInputStreamDelegatesToString() throws Exception {
        HttpServletRequest request = mockRequest(null);
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/WEB-INF/views/hello.jsp", "/render");

        ByteArrayInputStream bais = new ByteArrayInputStream(
                SIMPLE_HTML.getBytes(StandardCharsets.UTF_8));

        WordprocessingMLPackage pkg = t.process(bais, java.util.Map.of());
        assertNotNull(pkg);
    }

    // ---- render() with null dispatcher -- NullPointerException ----

    @Test
    void processThrowsWhenNoDispatcher() {
        HttpServletRequest request = mockRequestWithNullDispatcher();
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/WEB-INF/views/hello.jsp", "/nonexistent");

        // 2.0.x JspTemplateImpl.doInterpret() gets NPE when
        // getSession().getServletContext().getRequestDispatcher() returns null
        // and then rd.include() is called on null
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
                () -> t.process("/nonexistent", java.util.Map.of()));
    }

    // ---- variables injection into request attributes ----

    @Test
    void processInjectsVariablesAsRequestAttributes() throws Exception {
        Map<String, Object> capturedAttributes = new HashMap<>();
        HttpServletRequest request = mockRequest(capturedAttributes);
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/WEB-INF/views/hello.jsp", "/render");

        Map<String, Object> variables = new HashMap<>();
        variables.put("title", "My Title");
        variables.put("count", 42);

        WordprocessingMLPackage pkg = t.process("/render", variables);
        assertNotNull(pkg);
        // Verify variables were injected as request attributes
        assertSame("My Title", capturedAttributes.get("title"),
                "variables must be set as request attributes for JSP EL");
        assertSame(42, capturedAttributes.get("count"),
                "variables must be set as request attributes for JSP EL");
    }

    @Test
    void processWithNullVariablesDoesNotThrow() throws Exception {
        HttpServletRequest request = mockRequest(null);
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/WEB-INF/views/hello.jsp", "/render");

        WordprocessingMLPackage pkg = t.process("/render", null);
        assertNotNull(pkg);
    }
}
