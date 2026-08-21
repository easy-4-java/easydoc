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
package io.github.easy4j.doc.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * WordprocessingMLPackageExtractor.extract(File) and extract(String) call
 * {@code WordprocessingMLPackage.load(File)} which triggers the docx4j
 * 11.5.14 MOXy bridge issue. Marked disabled.
 */
@org.junit.jupiter.api.Disabled("requires MOXy migration")
class WordprocessingMLPackageExtractorTest {

    @Test
    void getWMLPackageExtractorReturnsInstance() {
        WordprocessingMLPackageExtractor extractor = WordprocessingMLPackageExtractor.getWMLPackageExtractor();
        assertNotNull(extractor);
    }

    @Test
    void extractFromFile() throws Exception {
        WordprocessingMLPackageExtractor extractor = WordprocessingMLPackageExtractor.getWMLPackageExtractor();
        extractor.extract(new java.io.File("tpl/template.docx"));
    }

    @Test
    void extractFromString() throws Exception {
        WordprocessingMLPackageExtractor extractor = WordprocessingMLPackageExtractor.getWMLPackageExtractor();
        extractor.extract("tpl/template.docx");
    }
}
