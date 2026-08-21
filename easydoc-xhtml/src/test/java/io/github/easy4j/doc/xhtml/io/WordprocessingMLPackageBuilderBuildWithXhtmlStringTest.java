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

/**
 * End-to-end coverage for the {@code buildWithXhtml(String, ...)} overloads on
 * {@link WordprocessingMLPackageBuilder}.
 */
class WordprocessingMLPackageBuilderBuildWithXhtmlStringTest {

    private static final String SIMPLE_HTML = "<html><body><p>string xhtml test</p></body></html>";

    // buildWithXhtml(String, boolean) — creates default A4 portrait
    @Test
    void buildWithXhtmlString2ArgsReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtml(SIMPLE_HTML, false);
        assertNotNull(pkg);
    }

    // buildWithXhtml(String, boolean landscape, boolean altChunk)
    @Test
    void buildWithXhtmlString3ArgsLandscapeReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtml(SIMPLE_HTML, true, false);
        assertNotNull(pkg);
    }

    // buildWithXhtml(String, PageSizePaper, boolean landscape, boolean altChunk)
    @Test
    void buildWithXhtmlString4ArgsReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtml(SIMPLE_HTML, PageSizePaper.A4, false, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWithXhtmlString4ArgsLetterLandscapeReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtml(SIMPLE_HTML, PageSizePaper.LETTER, true, false);
        assertNotNull(pkg);
    }

    // buildWithXhtml(WordprocessingMLPackage, String, boolean)
    @Test
    void buildWithXhtmlStringWithExistingPackageReturnsNonNull() throws Exception {
        WordprocessingMLPackage existing = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtml(existing, SIMPLE_HTML, false);
        assertNotNull(pkg);
    }
}
