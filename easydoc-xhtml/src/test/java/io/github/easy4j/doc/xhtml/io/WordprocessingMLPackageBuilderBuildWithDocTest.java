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

import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * End-to-end coverage for the4 {@code buildWithDoc(...)} overloads on
 * {@link WordprocessingMLPackageBuilder}. Each exercises the full
 * {@code execute(BuildRequest)} pipeline: StartEvent -> XHTMLImporter ->
 * EventFinished -> FontMapperHolder.
 */
class WordprocessingMLPackageBuilderBuildWithDocTest {

    private static final String SIMPLE_HTML = "<html><body><p>doc test</p></body></html>";

    private static Document doc() {
        return Jsoup.parse(SIMPLE_HTML);
    }

    // buildWithDoc(Document, boolean) — creates default A4 portrait package
    @Test
    void buildWithDoc2ArgsReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithDoc(doc(), false);
        assertNotNull(pkg);
    }

    // buildWithDoc(Document, boolean landscape, boolean altChunk)
    @Test
    void buildWithDoc3ArgsLandscapeReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithDoc(doc(), true, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWithDoc3ArgsPortraitReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithDoc(doc(), false, false);
        assertNotNull(pkg);
    }

    // buildWithDoc(Document, PageSizePaper, boolean landscape, boolean altChunk)
    @Test
    void buildWithDoc4ArgsWithPageSizePaperReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithDoc(doc(), PageSizePaper.A4, false, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWithDoc4ArgsWithLetterSizeReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithDoc(doc(), PageSizePaper.LETTER, true, false);
        assertNotNull(pkg);
    }

    // buildWithDoc(WordprocessingMLPackage, Document, boolean)
    @Test
    void buildWithDocWithExistingPackageReturnsNonNull() throws Exception {
        WordprocessingMLPackage existing = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithDoc(existing, doc(), false);
        assertNotNull(pkg);
    }
}
