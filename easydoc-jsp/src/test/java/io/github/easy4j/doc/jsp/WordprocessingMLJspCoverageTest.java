package io.github.easy4j.doc.jsp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
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

/**
 * Tests to verify that variables are injected as request attributes
 * before JSP rendering, and coverage for constructor variants.
 *
 * <p>Uses a stub {@link WordprocessingMLHtmlTemplate} injected via reflection
 * to avoid hitting the full docx4j/pdfbox pipeline (which has class
 * initialization issues in the test environment).
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
     * Mock request that captures setAttribute calls.
     * Provides getSession() -> getServletContext() -> getRequestDispatcher() chain
     * since 1.0.x JspTemplateImpl.doInterpret() uses this path.
     */
    private static HttpServletRequest mockRequestCapturingAttributes(
            final Map<String, Object> capturedAttributes) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class },
                (proxy, method, args) -> {
                    if ("setAttribute".equals(method.getName())) {
                        capturedAttributes.put((String) args[0], args[1]);
                        return null;
                    }
                    if ("getSession".equals(method.getName())) {
                        return mockSession();
                    }
                    return null;
                });
    }

    /**
     * Mock request whose getSession() -> getServletContext() -> getRequestDispatcher()
     * chain returns a dispatcher that writes {@link #SIMPLE_HTML}.
     */
    private static HttpServletRequest mockRequestWithDispatcher() {
        return (HttpServletRequest) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class },
                (proxy, method, args) -> {
                    if ("getSession".equals(method.getName())) {
                        return mockSession();
                    }
                    return null;
                });
    }

    private static HttpSession mockSession() {
        return (HttpSession) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { HttpSession.class },
                (proxy, method, args) -> {
                    if ("getServletContext".equals(method.getName())) {
                        return mockServletContext();
                    }
                    return null;
                });
    }

    private static ServletContext mockServletContext() {
        return (ServletContext) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { ServletContext.class },
                (proxy, method, args) -> {
                    if ("getRequestDispatcher".equals(method.getName())) {
                        return mockRequestDispatcher();
                    }
                    return null;
                });
    }

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
     * Create a WordprocessingMLJspTemplate with a stub mlHtmlTemplate
     * that bypasses the full docx4j/pdfbox pipeline.
     */
    private WordprocessingMLJspTemplate createTemplateWithStub(
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/WEB-INF/views/hello.jsp", "/render");
        // Inject a stub mlHtmlTemplate that returns a minimal package
        WordprocessingMLHtmlTemplate stub = new WordprocessingMLHtmlTemplate(true, false) {
            @Override
            public WordprocessingMLPackage process(String template, java.util.Map<String, Object> variables) throws Exception {
                // Return a minimal empty package to avoid pdfbox pipeline
                return WordprocessingMLPackage.createPackage();
            }
        };
        Field mlHtmlField = WordprocessingMLJspTemplate.class.getDeclaredField("mlHtmlTemplate");
        mlHtmlField.setAccessible(true);
        mlHtmlField.set(t, stub);
        return t;
    }

    // ---- variables injected as request attributes ----

    @Test
    void processInjectsVariablesAsRequestAttributes() throws Exception {
        Map<String, Object> capturedAttributes = new HashMap<String, Object>();
        HttpServletRequest request = mockRequestCapturingAttributes(capturedAttributes);
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = createTemplateWithStub(request, response);

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("title", "My Title");
        variables.put("count", Integer.valueOf(42));

        WordprocessingMLPackage pkg = t.process("/render", variables);
        assertNotNull(pkg);
        assertEquals("My Title", capturedAttributes.get("title"),
                "variables must be injected as request attributes");
        assertEquals(Integer.valueOf(42), capturedAttributes.get("count"),
                "numeric variables must be injected as request attributes");
    }

    @Test
    void processWithNullVariablesDoesNotThrow() throws Exception {
        HttpServletRequest request = mockRequestWithDispatcher();
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = createTemplateWithStub(request, response);

        WordprocessingMLPackage pkg = t.process("/render", null);
        assertNotNull(pkg, "process with null variables must not throw");
    }

    // ---- 5-arg constructor (with WordprocessingMLHtmlTemplate) ----

    @Test
    void constructorWithHtmlTemplateStoresFields() throws Exception {
        HttpServletRequest request = mockRequestWithDispatcher();
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
        HttpServletRequest request = mockRequestWithDispatcher();
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/name", "/url", true, true);

        Field mlHtmlField = WordprocessingMLJspTemplate.class.getDeclaredField("mlHtmlTemplate");
        mlHtmlField.setAccessible(true);
        WordprocessingMLHtmlTemplate htmlTemplate =
                (WordprocessingMLHtmlTemplate) mlHtmlField.get(t);
        assertNotNull(htmlTemplate);
    }

    // ---- process(String, Map) with stub ----

    @Test
    void processStringRendersThroughJspEngine() throws Exception {
        HttpServletRequest request = mockRequestWithDispatcher();
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = createTemplateWithStub(request, response);

        WordprocessingMLPackage pkg = t.process("/render", new HashMap<String, Object>());
        assertNotNull(pkg);
    }

    // ---- process(InputStream, Map) with stub ----

    @Test
    void processInputStreamDelegatesToString() throws Exception {
        HttpServletRequest request = mockRequestWithDispatcher();
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = createTemplateWithStub(request, response);

        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(
                SIMPLE_HTML.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        WordprocessingMLPackage pkg = t.process(bais, new HashMap<String, Object>());
        assertNotNull(pkg);
    }
}
