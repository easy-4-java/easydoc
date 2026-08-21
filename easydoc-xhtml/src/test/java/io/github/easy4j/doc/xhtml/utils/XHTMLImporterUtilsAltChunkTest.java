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
package io.github.easy4j.doc.xhtml.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Additional coverage for {@link XHTMLImporterUtils}. The existing smoke test
 * covers the non-fragment, non-altChunk path. This class exercises:
 * <ul>
 *   <li>{@code altChunk=true} (hits the {@code addAltChunk} / {@code convertAltChunks} branch)</li>
 *   <li>{@code fragment=true} (hits the {@code parseBodyFragment} branch in the handler)</li>
 *   <li>Both flags combined</li>
 * </ul>
 */
class XHTMLImporterUtilsAltChunkTest {

    private static final String FULL_HTML = "<html><body><p>altChunk test</p></body></html>";
    private static final String FRAGMENT = "<p>fragment <b>bold</b> content</p>";

    @Test
    void handleWithAltChunkReturnsNonNull() throws Exception {
        Document doc = Jsoup.parse(FULL_HTML);
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage out = XHTMLImporterUtils.handle(wmlPackage, doc, false, true);
        assertNotNull(out, "altChunk path must return a non-null package");
    }

    @Test
    void handleWithFragmentReturnsNonNull() throws Exception {
        Document doc = Jsoup.parseBodyFragment(FRAGMENT);
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage out = XHTMLImporterUtils.handle(wmlPackage, doc, true, false);
        assertNotNull(out, "fragment path must return a non-null package");
    }

    @Test
    void handleWithFragmentAndAltChunkReturnsNonNull() throws Exception {
        Document doc = Jsoup.parseBodyFragment(FRAGMENT);
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
        WordprocessingMLPackage out = XHTMLImporterUtils.handle(wmlPackage, doc, true, true);
        assertNotNull(out, "fragment+altChunk path must return a non-null package");
    }
}
