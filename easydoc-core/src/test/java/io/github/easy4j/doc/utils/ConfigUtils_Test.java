/*
 * Copyright (c) 2024, hiwepy (https://github.com/easy-4-java).
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

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Unit tests for {@link ConfigUtils}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class ConfigUtils_Test {

    @Test
    void test_filterWithPrefix_properties_withMatchingPrefix() {
        Properties input = new Properties();
        input.setProperty("app.name", "test");
        input.setProperty("app.version", "1.0");
        input.setProperty("other.key", "value");

        Properties result = ConfigUtils.filterWithPrefix("app.", "app.", input, false);
        assertThat(result).containsEntry("name", "test");
        assertThat(result).containsEntry("version", "1.0");
        assertThat(result).doesNotContainKey("other.key");
    }

    @Test
    void test_filterWithPrefix_properties_withEscape() {
        Properties input = new Properties();
        input.setProperty("app.test.key", "+value");

        Properties result = ConfigUtils.filterWithPrefix("app.", "app.", input, true);
        assertThat(result).hasSize(1);
    }

    @Test
    void test_filterWithPrefix_properties_withMinusEscape() {
        Properties input = new Properties();
        input.setProperty("app.test.key", "-value");

        Properties result = ConfigUtils.filterWithPrefix("app.", "app.", input, true);
        assertThat(result).hasSize(1);
    }

    @Test
    void test_filterWithPrefix_map_withMatchingPrefix() {
        Map<String, String> input = new HashMap<String, String>();
        input.put("app.name", "test");
        input.put("other.key", "value");

        Map<String, String> result = ConfigUtils.filterWithPrefix("app.", input, false);
        assertThat(result).containsEntry("name", "test");
        assertThat(result).doesNotContainKey("other.key");
    }

    @Test
    void test_filterWithPrefix_map_withEscape() {
        Map<String, String> input = new HashMap<String, String>();
        input.put("app.test.key", "+value");

        Map<String, String> result = ConfigUtils.filterWithPrefix("app.", input, true);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test_filterWithPrefix_map_withMinusEscape() {
        Map<String, String> input = new HashMap<String, String>();
        input.put("app.test.key", "-value");

        Map<String, String> result = ConfigUtils.filterWithPrefix("app.", input, true);
        assertThat(result).isNotEmpty();
    }

    @Test
    void test_filterWithPrefix_map_noMatch() {
        Map<String, String> input = new HashMap<String, String>();
        input.put("other.key", "value");

        Map<String, String> result = ConfigUtils.filterWithPrefix("app.", input, false);
        assertThat(result).isEmpty();
    }
}
