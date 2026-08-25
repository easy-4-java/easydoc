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

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Additional tests to push JaCoCo line coverage of
 * {@link WordprocessingMLJspTemplate} above 90 %.
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
     * Mock request whose {@code getRequestDispatcher(path)} returns a
     * dispatcher that writes {@link #SIMPLE_HTML} to the response writer
     * when {@code include()} is called.
     */
    private static HttpServletRequest mockRequestWithDispatcher() {
        return (HttpServletRequest) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class },
                (proxy, method, args) -> {
                    if ("getRequestDispatcher".equals(method.getName())) {
                        return mockRequestDispatcher();
                    }
                    return null;
                });
    }

    /**
     * Mock request whose {@code getRequestDispatcher(path)} returns null,
     * triggering the {@code IllegalStateException} path.
     */
    private static HttpServletRequest mockRequestWithNullDispatcher() {
        return (HttpServletRequest) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class },
                (proxy, method, args) -> null);
    }

    private static RequestDispatcher mockRequestDispatcher() {
        return (RequestDispatcher) Proxy.newProxyInstance(
                WordprocessingMLJspTemplate.class.getClassLoader(),
                new Class<?>[] { RequestDispatcher.class },
                (proxy, method, args) -> {
                    if ("include".equals(method.getName())) {
                        // args[0] = request, args[1] = response
                        HttpServletResponse resp = (HttpServletResponse) args[1];
                        // The wrappedResponse overrides getWriter() to capture output;
                        // call resp.getWriter() which returns the capturing PrintWriter.
                        PrintWriter writer = resp.getWriter();
                        writer.write(SIMPLE_HTML);
                        writer.flush();
                    }
                    return null;
                });
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

    // ---- process(String, Map) full render path ----

    @Test
    void processStringRendersThroughRequestDispatcher() throws Exception {
        HttpServletRequest request = mockRequestWithDispatcher();
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/WEB-INF/views/hello.jsp", "/render");

        WordprocessingMLPackage pkg = t.process("/render", java.util.Map.of());
        assertNotNull(pkg);
        assertTrue(pkg.getMainDocumentPart().getXML().contains("Hello world"),
                "rendered docx must contain 'Hello world'");
    }

    // ---- process(File, Map) delegates to process(String, Map) ----

    @Test
    void processFileDelegatesToString(@TempDir File tempDir) throws Exception {
        HttpServletRequest request = mockRequestWithDispatcher();
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/WEB-INF/views/hello.jsp", "/render");

        File htmlFile = new File(tempDir, "test.html");
        Files.write(htmlFile.toPath(), SIMPLE_HTML.getBytes(StandardCharsets.UTF_8));

        WordprocessingMLPackage pkg = t.process(htmlFile, java.util.Map.of());
        assertNotNull(pkg);
        assertTrue(pkg.getMainDocumentPart().getXML().contains("Hello world"));
    }

    // ---- process(InputStream, Map) delegates to process(String, Map) ----

    @Test
    void processInputStreamDelegatesToString() throws Exception {
        HttpServletRequest request = mockRequestWithDispatcher();
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/WEB-INF/views/hello.jsp", "/render");

        ByteArrayInputStream bais = new ByteArrayInputStream(
                SIMPLE_HTML.getBytes(StandardCharsets.UTF_8));

        WordprocessingMLPackage pkg = t.process(bais, java.util.Map.of());
        assertNotNull(pkg);
        assertTrue(pkg.getMainDocumentPart().getXML().contains("Hello world"));
    }

    // ---- render() with null dispatcher → IllegalStateException ----

    @Test
    void processThrowsWhenNoDispatcher() {
        HttpServletRequest request = mockRequestWithNullDispatcher();
        HttpServletResponse response = mockResponse();

        WordprocessingMLJspTemplate t = new WordprocessingMLJspTemplate(
                request, response, "/WEB-INF/views/hello.jsp", "/nonexistent");

        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> t.process("/nonexistent", java.util.Map.of()));
    }
}
