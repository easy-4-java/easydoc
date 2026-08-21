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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class ConfigUtilsTest {

    @Test
    void filterWithPrefixPropertiesNoEscape() {
        Properties input = new Properties();
        input.setProperty("docx.name", "alice");
        input.setProperty("docx.age", "30");
        input.setProperty("other.key", "x");

        Properties result = ConfigUtils.filterWithPrefix("docx.", "docx.", input, false);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("alice", result.getProperty("name"));
        assertEquals("30", result.getProperty("age"));
        assertEquals(null, result.getProperty("other.key"));
    }

    @Test
    void filterWithPrefixPropertiesWithEscape() {
        Properties input = new Properties();
        input.setProperty("docx.something", "value");
        Properties result = ConfigUtils.filterWithPrefix("docx.", "docx.", input, true);
        assertNotNull(result);
        assertEquals("value", result.getProperty("something"));
    }

    @Test
    void filterWithPrefixPropertiesWithEscapePlus() {
        Properties input = new Properties();
        input.setProperty("docx.key", "+hello");
        Properties result = ConfigUtils.filterWithPrefix("docx.", "docx.", input, true);
        assertNotNull(result);
        assertEquals("hello", result.getProperty("key+"));
    }

    @Test
    void filterWithPrefixPropertiesWithEscapeMinus() {
        Properties input = new Properties();
        input.setProperty("docx.key", "-value");
        Properties result = ConfigUtils.filterWithPrefix("docx.", "docx.", input, true);
        assertNotNull(result);
        assertEquals("value", result.getProperty("key-"));
    }

    @Test
    void filterWithPrefixMapNoEscape() {
        Map<String, String> input = new HashMap<>();
        input.put("docx.name", "alice");
        input.put("docx.age", "30");
        input.put("other.key", "x");

        Map<String, String> result = ConfigUtils.filterWithPrefix("docx.", input, false);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("alice", result.get("name"));
    }

    @Test
    void filterWithPrefixMapWithEscape() {
        Map<String, String> input = new HashMap<>();
        input.put("docx.key", "value");
        Map<String, String> result = ConfigUtils.filterWithPrefix("docx.", input, true);
        assertNotNull(result);
        assertEquals("value", result.get("key"));
    }

    @Test
    void filterWithPrefixMapWithEscapePlus() {
        Map<String, String> input = new HashMap<>();
        input.put("docx.key", "+hello");
        Map<String, String> result = ConfigUtils.filterWithPrefix("docx.", input, true);
        assertNotNull(result);
        assertEquals("hello", result.get("key+"));
    }

    @Test
    void filterWithPrefixMapEmptyResult() {
        Map<String, String> input = new HashMap<>();
        input.put("other.key", "x");
        Map<String, String> result = ConfigUtils.filterWithPrefix("docx.", input, false);
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
