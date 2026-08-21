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
import java.net.URL;

import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

/**
 * End-to-end coverage for the {@code buildWithXhtml(File, ...)} overloads on
 * {@link WordprocessingMLPackageBuilder}. Uses the test-classpath resource
 * {@code tpl/report.html} as input.
 */
class WordprocessingMLPackageBuilderBuildWithXhtmlFileTest {

    private static File reportHtml() {
        URL url = WordprocessingMLPackageBuilderBuildWithXhtmlFileTest.class
                .getClassLoader().getResource("tpl/report.html");
        if (url == null) {
            throw new IllegalStateException("tpl/report.html missing from test classpath");
        }
        return new File(url.getFile());
    }

    // buildWithXhtml(File, boolean) — creates default A4 portrait
    @Test
    void buildWithXhtmlFile2ArgsReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtml(reportHtml(), false);
        assertNotNull(pkg);
    }

    // buildWithXhtml(File, boolean landscape, boolean altChunk)
    @Test
    void buildWithXhtmlFile3ArgsLandscapeReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtml(reportHtml(), true, false);
        assertNotNull(pkg);
    }

    // buildWithXhtml(File, PageSizePaper, boolean landscape, boolean altChunk)
    @Test
    void buildWithXhtmlFile4ArgsReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtml(reportHtml(), PageSizePaper.A4, false, false);
        assertNotNull(pkg);
    }

    @Test
    void buildWithXhtmlFile4ArgsLetterLandscapeReturnsNonNull() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtml(reportHtml(), PageSizePaper.LETTER, true, false);
        assertNotNull(pkg);
    }

    // buildWithXhtml(WordprocessingMLPackage, File, boolean)
    @Test
    void buildWithXhtmlFileWithExistingPackageReturnsNonNull() throws Exception {
        WordprocessingMLPackage existing = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage pkg = WordprocessingMLPackageBuilder.getWMLPackageBuilder()
                .buildWithXhtml(existing, reportHtml(), false);
        assertNotNull(pkg);
    }
}
