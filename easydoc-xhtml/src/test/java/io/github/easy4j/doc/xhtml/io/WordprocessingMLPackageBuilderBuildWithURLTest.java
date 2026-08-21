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

import io.github.easy4j.doc.xhtml.DataMap;

/**
 * End-to-end coverage for the {@code buildWithURL(...)} overloads on
 * {@link WordprocessingMLPackageBuilder}. A lightweight local HTTP server
 * serves a static HTML snippet so tests run without external network access.
 */
class WordprocessingMLPackageBuilderBuildWithURLTest {

    private static final String HTML = "<html><body><p>url test</p></body></html>";
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

    private static URL testUrl() throws Exception {
        return new URL("http://localhost:" + port + "/test.html");
    }

    // buildWithURL(URL, boolean)
    @Test
    void buildWithURL2ArgsReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithURL(testUrl(), false);
        assertNotNull(pkg);
    }

    // buildWithURL(URL, boolean landscape, boolean altChunk)
    @Test
    void buildWithURL3ArgsLandscapeReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithURL(testUrl(), true, false);
        assertNotNull(pkg);
    }

    // buildWithURL(URL, PageSizePaper, boolean landscape, boolean altChunk)
    @Test
    void buildWithURL4ArgsReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithURL(testUrl(), PageSizePaper.A4, false, false);
        assertNotNull(pkg);
    }

    // buildWithURL(WordprocessingMLPackage, URL, boolean)
    @Test
    void buildWithURLWithExistingPackageReturnsNonNull() throws Exception {
        WordprocessingMLPackage existing = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithURL(existing, testUrl(), false);
        assertNotNull(pkg);
    }

    // buildWithURL(String, DataMap, boolean)
    @Test
    void buildWithURLWithDataMap2ArgsReturnsNonNull() throws Exception {
        String url = "http://localhost:" + port + "/test.html";
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithURL(url, new DataMap(), false);
        assertNotNull(pkg);
    }

    // buildWithURL(String, DataMap, boolean landscape, boolean altChunk)
    @Test
    void buildWithURLWithDataMap3ArgsReturnsNonNull() throws Exception {
        String url = "http://localhost:" + port + "/test.html";
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithURL(url, new DataMap(), true, false);
        assertNotNull(pkg);
    }

    // buildWithURL(String, DataMap, PageSizePaper, boolean landscape, boolean altChunk)
    @Test
    void buildWithURLWithDataMap4ArgsReturnsNonNull() throws Exception {
        String url = "http://localhost:" + port + "/test.html";
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithURL(url, new DataMap(), PageSizePaper.A4, false, false);
        assertNotNull(pkg);
    }

    // buildWithURL(WordprocessingMLPackage, String, DataMap, boolean)
    @Test
    void buildWithURLWithDataMapWithExistingPackageReturnsNonNull() throws Exception {
        String url = "http://localhost:" + port + "/test.html";
        WordprocessingMLPackage existing = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithURL(existing, url, new DataMap(), false);
        assertNotNull(pkg);
    }
}
