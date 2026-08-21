package io.github.easy4j.doc.xhtml;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Coverage for the two {@link WordprocessingMLHtmlTemplate} overloads that were
 * previously {@code @Disabled} because they require a live HTTP endpoint:
 *
 * <ul>
 *   <li>{@code process(URL)} — line 98</li>
 *   <li>{@code process(String url, Map, PageSizePaper)} — line 105</li>
 * </ul>
 *
 * <p>A lightweight local HTTP server serves a static HTML snippet so tests run
 * without external network access.</p>
 */
class WordprocessingMLHtmlTemplateURLTest {

    private static final String HTML = "<html><body><p>url template test</p></body></html>";
    private static HttpServer server;
    private static int port;

    @BeforeAll
    static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();
        server.createContext("/page.html", exchange -> {
            byte[] body = HTML.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.setExecutor(null);
        server.start();
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    /** Cover line 98: {@code process(URL)} with a real HTTP URL. */
    @Test
    void processUrlReturnsNonNullPackage() throws Exception {
        URL url = new URL("http://localhost:" + port + "/page.html");
        WordprocessingMLPackage pkg = new WordprocessingMLHtmlTemplate().process(url);
        assertNotNull(pkg, "process(URL) must return non-null");
    }

    /** Cover line 105: {@code process(String url, Map, PageSizePaper)} with a real HTTP URL. */
    @Test
    void processStringUrlDataMapPageSizeReturnsNonNull() throws Exception {
        String url = "http://localhost:" + port + "/page.html";
        Map<String, String> params = Collections.emptyMap();
        WordprocessingMLPackage pkg = new WordprocessingMLHtmlTemplate()
                .process(url, params, PageSizePaper.A4);
        assertNotNull(pkg, "process(String, Map, PageSizePaper) must return non-null");
    }

    /** Additional: process(URL) with landscape flag. */
    @Test
    void processUrlWithLandscapeReturnsNonNull() throws Exception {
        URL url = new URL("http://localhost:" + port + "/page.html");
        WordprocessingMLPackage pkg = new WordprocessingMLHtmlTemplate(true, false).process(url);
        assertNotNull(pkg);
    }

    /** Additional: process(String, Map, PageSizePaper) with landscape flag. */
    @Test
    void processStringUrlDataMapPageSizeWithLandscapeReturnsNonNull() throws Exception {
        String url = "http://localhost:" + port + "/page.html";
        WordprocessingMLPackage pkg = new WordprocessingMLHtmlTemplate(true, false)
                .process(url, Collections.<String, String>emptyMap(), PageSizePaper.LETTER);
        assertNotNull(pkg);
    }
}
