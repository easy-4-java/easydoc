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
package io.github.easy4j.doc.xhtml;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

/**
 * Coverage for the {@code process(File, Map)} and {@code process(InputStream, Map)}
 * overloads on {@link AbstractStringTemplateWrappingTemplate}, which are not
 * exercised by the existing test class.
 */
class AbstractStringTemplateWrappingTemplateFileInputStreamTest {

    private static final String HTML = "<html><body><p>wrapping test</p></body></html>";

    /** Minimal stub that echoes the template content back. */
    private static final class StubTemplate extends AbstractStringTemplateWrappingTemplate {
        StubTemplate() {
            super();
        }

        @Override
        protected String render(String template, Map<String, Object> variables) {
            return template;
        }
    }

    private static File reportHtml() {
        URL url = AbstractStringTemplateWrappingTemplateFileInputStreamTest.class
                .getClassLoader().getResource("tpl/report.html");
        if (url == null) {
            throw new IllegalStateException("tpl/report.html missing from test classpath");
        }
        return new File(url.getFile());
    }

    // process(File, Map) — reads file bytes, delegates to process(String, Map)
    @Test
    void processFileReturnsNonNullPackage() throws Exception {
        StubTemplate stub = new StubTemplate();
        WordprocessingMLPackage pkg = stub.process(reportHtml(), null);
        assertNotNull(pkg, "process(File, Map) must return a non-null package");
    }

    // process(InputStream, Map) — reads stream bytes, delegates to process(String, Map)
    @Test
    void processInputStreamReturnsNonNullPackage() throws Exception {
        StubTemplate stub = new StubTemplate();
        InputStream in = new ByteArrayInputStream(HTML.getBytes(StandardCharsets.UTF_8));
        WordprocessingMLPackage pkg = stub.process(in, null);
        assertNotNull(pkg, "process(InputStream, Map) must return a non-null package");
    }

    // process(String, Map) — the base trampoline (already covered, but adding for completeness)
    @Test
    void processStringReturnsNonNullPackage() throws Exception {
        StubTemplate stub = new StubTemplate();
        WordprocessingMLPackage pkg = stub.process(HTML, null);
        assertNotNull(pkg, "process(String, Map) must return a non-null package");
    }
}
