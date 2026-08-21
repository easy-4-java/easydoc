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
package io.github.easy4j.doc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class Docx4jConstantsTest {

    @Test
    void allStringConstantsAreNonNullAndNonEmpty() throws Exception {
        Field[] fields = Docx4jConstants.class.getDeclaredFields();
        for (Field f : fields) {
            if (java.lang.reflect.Modifier.isStatic(f.getModifiers())
                    && f.getType() == String.class) {
                f.setAccessible(true);
                Object value = f.get(null);
                assertNotNull(value, "Constant " + f.getName() + " is null");
                assertTrue(((String) value).length() > 0,
                        "Constant " + f.getName() + " is empty");
            }
        }
    }

    @Test
    void defaultCharsetIsUtf8() {
        assertNotNull(Docx4jConstants.DEFAULT_CHARSETNAME);
        assertTrue(Docx4jConstants.DEFAULT_CHARSETNAME.length() > 0);
    }

    @Test
    void defaultTimeoutMillisIsPositive() {
        assertTrue(Docx4jConstants.DEFAULT_TIMEOUTMILLIS > 0);
    }

    @Test
    void docx4jParam01IsDocx4jAppWrite() {
        assertNotNull(Docx4jConstants.DOCX4J_PARAM_01);
        assertTrue(Docx4jConstants.DOCX4J_PARAM_01.startsWith("docx4j"));
    }

    @Test
    void docx4jJsoupParseTimeoutMillisStartsWithDocx4j() {
        assertNotNull(Docx4jConstants.DOCX4J_JSOUP_PARSE_TIMEOUTMILLIS);
        assertTrue(Docx4jConstants.DOCX4J_JSOUP_PARSE_TIMEOUTMILLIS.startsWith("docx4j"));
    }

    @Test
    void docx4jFontsExternalMappingStartsWithDocx4j() {
        assertNotNull(Docx4jConstants.DOCX4J_FONTS_EXTERNAL_MAPPING);
        assertTrue(Docx4jConstants.DOCX4J_FONTS_EXTERNAL_MAPPING.startsWith("docx4j"));
    }
}
