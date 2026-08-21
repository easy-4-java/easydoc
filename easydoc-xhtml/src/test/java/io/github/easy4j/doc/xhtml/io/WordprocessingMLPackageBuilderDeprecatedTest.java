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
package io.github.easy4j.doc.xhtml.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpServer;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.github.easy4j.doc.xhtml.DataMap;

/**
 * Coverage for the24 deprecated {@code buildWhith*} forwarders on
 * {@link WordprocessingMLPackageBuilder}. Each delegates directly to its
 * {@code buildWith*} counterpart. We call every one at least once to ensure
 * JaCoCo records them as covered.
 */
@SuppressWarnings("deprecation")
class WordprocessingMLPackageBuilderDeprecatedTest {

    private static final String HTML = "<html><body><p>deprecated test</p></body></html>";
    private static HttpServer server;
    private static int port;

    @BeforeAll
    static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();
        server.createContext("/test.html", exchange -> {
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

    private static Document doc() {
        return Jsoup.parse(HTML);
    }

    private static File reportHtml() {
        URL url = WordprocessingMLPackageBuilderDeprecatedTest.class
                .getClassLoader().getResource("tpl/report.html");
        if (url == null) {
            throw new IllegalStateException("tpl/report.html missing from test classpath");
        }
        return new File(url.getFile());
    }

    // --- buildWhithDoc ---

    @Test
    void buildWhithDoc2Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithDoc(doc(), false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithDoc3Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithDoc(doc(), true, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithDoc4Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithDoc(doc(), PageSizePaper.A4, false, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithDocWithPackage() throws Exception {
        WordprocessingMLPackage existing = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithDoc(existing, doc(), false);
        assertNotNull(pkg);
    }

    // --- buildWhithXhtml (File) ---

    @Test
    void buildWhithXhtmlFile2Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithXhtml(reportHtml(), false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithXhtmlFile3Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithXhtml(reportHtml(), true, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithXhtmlFile4Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithXhtml(reportHtml(), PageSizePaper.A4, false, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithXhtmlFileWithPackage() throws Exception {
        WordprocessingMLPackage existing = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithXhtml(existing, reportHtml(), false);
        assertNotNull(pkg);
    }

    // --- buildWhithXhtml (String) ---

    @Test
    void buildWhithXhtmlString2Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithXhtml(HTML, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithXhtmlString3Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithXhtml(HTML, true, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithXhtmlString4Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithXhtml(HTML, PageSizePaper.A4, false, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithXhtmlStringWithPackage() throws Exception {
        WordprocessingMLPackage existing = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithXhtml(existing, HTML, false);
        assertNotNull(pkg);
    }

    // --- buildWhithXhtmlFragment ---

    @Test
    void buildWhithXhtmlFragment2Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithXhtmlFragment("<p>frag</p>", false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithXhtmlFragment3Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithXhtmlFragment("<p>frag</p>", true, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithXhtmlFragment4Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithXhtmlFragment("<p>frag</p>", PageSizePaper.A4, false, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithXhtmlFragmentWithPackage() throws Exception {
        WordprocessingMLPackage existing = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithXhtmlFragment(existing, "<p>frag</p>", false);
        assertNotNull(pkg);
    }

    // --- buildWhithURL ---

    private URL testUrl() throws Exception {
        return new URL("http://localhost:" + port + "/test.html");
    }

    private String testUrlString() {
        return "http://localhost:" + port + "/test.html";
    }

    @Test
    void buildWhithURL2Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithURL(testUrl(), false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithURL3Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithURL(testUrl(), true, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithURL4Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithURL(testUrl(), PageSizePaper.A4, false, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithURLWithPackage() throws Exception {
        WordprocessingMLPackage existing = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithURL(existing, testUrl(), false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithURLDataMap2Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithURL(testUrlString(), new DataMap(), false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithURLDataMap3Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithURL(testUrlString(), new DataMap(), true, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithURLDataMap4Args() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithURL(testUrlString(), new DataMap(), PageSizePaper.A4, false, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWhithURLDataMapWithPackage() throws Exception {
        WordprocessingMLPackage existing = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWhithURL(existing, testUrlString(), new DataMap(), false);
        assertNotNull(pkg);
    }
}
