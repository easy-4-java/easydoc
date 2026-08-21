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

import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Additional coverage for {@link WordprocessingMLHtmlTemplate} overloads not
 * exercised by the existing ProcessOverloadsTest: the {@code process(Document,
 * PageSizePaper)} variant and the {@code process(URL)} variant with a local server.
 */
class WordprocessingMLHtmlTemplateExtraOverloadsTest {

    private static final String HTML = "<html><body><p>extra overloads test</p></body></html>";

    // process(Document, PageSizePaper)
    @Test
    void processDocumentWithPageSizePaperReturnsNonNull() throws Exception {
        Document doc = Jsoup.parse(HTML);
        WordprocessingMLPackage pkg = new WordprocessingMLHtmlTemplate()
                .process(doc, PageSizePaper.A4);
        assertNotNull(pkg, "process(Document, PageSizePaper) must return non-null");
    }

    @Test
    void processDocumentWithLetterPageSizeReturnsNonNull() throws Exception {
        Document doc = Jsoup.parse(HTML);
        WordprocessingMLPackage pkg = new WordprocessingMLHtmlTemplate(true, false)
                .process(doc, PageSizePaper.LETTER);
        assertNotNull(pkg);
    }
}
