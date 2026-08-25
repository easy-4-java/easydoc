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
package io.github.easy4j.doc.xhtml.handler.def;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.jsoup.nodes.Document;

import io.github.easy4j.doc.xhtml.handler.DocumentHandler;

/**
 * Coverage for the {@code handle(...)} overloads on {@link XHTMLDocumentHandler}
 * including the file:/jar: local protocol fix.
 */
class XHTMLDocumentHandlerHandleTest {

    private static final String HTML = "<html><body><p>handler test</p></body></html>";

    private static File reportHtml() {
        URL url = XHTMLDocumentHandlerHandleTest.class
                .getClassLoader().getResource("tpl/report.html");
        if (url == null) {
            throw new IllegalStateException("tpl/report.html missing from test classpath");
        }
        return new File(url.getFile());
    }

    // handle(File)
    @Test
    void handleFileReturnsNonNullDocument() throws Exception {
        DocumentHandler handler = XHTMLDocumentHandler.getDocumentHandler();
        Document doc = handler.handle(reportHtml());
        assertNotNull(doc, "handle(File) must return a non-null Document");
        assertTrue(doc.html().length() > 0, "parsed document must have content");
    }

    // handle(InputStream)
    @Test
    void handleInputStreamReturnsNonNullDocument() throws Exception {
        DocumentHandler handler = XHTMLDocumentHandler.getDocumentHandler();
        InputStream in = new ByteArrayInputStream(HTML.getBytes(StandardCharsets.UTF_8));
        Document doc = handler.handle(in);
        assertNotNull(doc, "handle(InputStream) must return a non-null Document");
        assertTrue(doc.html().length() > 0, "parsed document must have content");
    }

    // handle(String, boolean) — full document mode
    @Test
    void handleStringFullDocumentReturnsNonNull() throws Exception {
        DocumentHandler handler = XHTMLDocumentHandler.getDocumentHandler();
        Document doc = handler.handle(HTML, false);
        assertNotNull(doc);
    }

    // handle(String, boolean) — fragment mode
    @Test
    void handleStringFragmentReturnsNonNull() throws Exception {
        DocumentHandler handler = XHTMLDocumentHandler.getDocumentHandler();
        Document doc = handler.handle("<p>fragment</p>", true);
        assertNotNull(doc);
    }

    // getDocumentHandler() singleton
    @Test
    void getDocumentHandlerReturnsSameInstance() {
        DocumentHandler a = XHTMLDocumentHandler.getDocumentHandler();
        DocumentHandler b = XHTMLDocumentHandler.getDocumentHandler();
        assertNotNull(a);
        assertNotNull(b);
        assertTrue(a == b, "getDocumentHandler must return the same singleton");
    }

    // handle(URL) with file: protocol (local protocol fix)
    @Test
    void handleFileUrlReturnsNonNullDocument() throws Exception {
        File htmlFile = reportHtml();
        URL fileUrl = htmlFile.toURI().toURL();
        DocumentHandler handler = XHTMLDocumentHandler.getDocumentHandler();
        Document doc = handler.handle(fileUrl);
        assertNotNull(doc, "handle(URL) with file: protocol must return a non-null Document");
        assertTrue(doc.html().length() > 0, "parsed document must have content");
    }

    // handle(URL) with file: protocol should set prettyPrint(false)
    @Test
    void handleFileUrlDisablesPrettyPrint() throws Exception {
        File htmlFile = reportHtml();
        URL fileUrl = htmlFile.toURI().toURL();
        DocumentHandler handler = XHTMLDocumentHandler.getDocumentHandler();
        Document doc = handler.handle(fileUrl);
        assertNotNull(doc);
        assertFalse(doc.outputSettings().prettyPrint(),
                "file: URL handler must set prettyPrint(false)");
    }
}
