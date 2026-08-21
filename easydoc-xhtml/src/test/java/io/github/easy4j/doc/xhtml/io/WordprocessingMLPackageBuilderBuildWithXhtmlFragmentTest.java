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
 * End-to-end coverage for the {@code buildWithXhtmlFragment(...)} overloads on
 * {@link WordprocessingMLPackageBuilder}. Fragment mode uses
 * {@code Jsoup.parseBodyFragment()} instead of {@code Jsoup.parse()}.
 */
class WordprocessingMLPackageBuilderBuildWithXhtmlFragmentTest {

    private static final String FRAGMENT = "<p>fragment <b>test</b> content</p>";

    // buildWithXhtmlFragment(String, boolean) — creates default A4 portrait
    @Test
    void buildWithXhtmlFragment2ArgsReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtmlFragment(FRAGMENT, false);
        assertNotNull(pkg);
    }

    // buildWithXhtmlFragment(String, boolean landscape, boolean altChunk)
    @Test
    void buildWithXhtmlFragment3ArgsLandscapeReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtmlFragment(FRAGMENT, true, false);
        assertNotNull(pkg);
    }

    // buildWithXhtmlFragment(String, PageSizePaper, boolean landscape, boolean altChunk)
    @Test
    void buildWithXhtmlFragment4ArgsReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtmlFragment(FRAGMENT, PageSizePaper.A4, false, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWithXhtmlFragment4ArgsLetterLandscapeReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtmlFragment(FRAGMENT, PageSizePaper.LETTER, true, false);
        assertNotNull(pkg);
    }

    // buildWithXhtmlFragment(WordprocessingMLPackage, String, boolean)
    @Test
    void buildWithXhtmlFragmentWithExistingPackageReturnsNonNull() throws Exception {
        WordprocessingMLPackage existing = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtmlFragment(existing, FRAGMENT, false);
        assertNotNull(pkg);
    }
}
