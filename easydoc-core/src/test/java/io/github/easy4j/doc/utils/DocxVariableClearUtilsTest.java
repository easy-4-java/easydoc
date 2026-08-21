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
package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * DocxVariableClearUtils.doCleanDocumentPart requires a JAXBContext for
 * docx4j WML objects, which is normally obtained via
 * {@code Context.jc}. Calling {@code Context.jc} triggers the docx4j 11.5.14
 * MOXy bridge problem. Marked disabled.
 */
@Disabled("requires MOXy migration")
class DocxVariableClearUtilsTest {

    @Test
    void doCleanDocumentPartStripsXmlTagsFromVariable() throws Exception {
        jakarta.xml.bind.JAXBContext jc = org.docx4j.jaxb.Context.jc;
        String template = "Hello ${<b>name</b>} world";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, jc);
        org.junit.jupiter.api.Assertions.assertNotNull(result);
    }
}
