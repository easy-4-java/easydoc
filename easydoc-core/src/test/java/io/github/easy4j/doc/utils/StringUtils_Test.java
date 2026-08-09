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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Unit tests for {@link StringUtils}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class StringUtils_Test {

    @Test
    void test_tokenizeToStringArray_withDefaultDelimiters() {
        String[] result = StringUtils.tokenizeToStringArray("a,b;c d");
        assertThat(result).containsExactly("a", "b", "c", "d");
    }

    @Test
    void test_tokenizeToStringArray_withNullInput() {
        String[] result = StringUtils.tokenizeToStringArray(null);
        assertThat(result).isNull();
    }

    @Test
    void test_tokenizeToStringArray_withCustomDelimiters() {
        String[] result = StringUtils.tokenizeToStringArray("a|b|c", "|");
        assertThat(result).containsExactly("a", "b", "c");
    }

    @Test
    void test_tokenizeToStringArray_withTrimAndIgnoreEmpty() {
        String[] result = StringUtils.tokenizeToStringArray(" a , , b ", ",", true, true);
        assertThat(result).containsExactly("a", "b");
    }

    @Test
    void test_tokenizeToStringArray_withNoTrim() {
        String[] result = StringUtils.tokenizeToStringArray(" a , b ", ",", false, true);
        assertThat(result).containsExactly(" a ", " b ");
    }

    @Test
    void test_tokenizeToStringArray_withEmptyTokens() {
        String[] result = StringUtils.tokenizeToStringArray("a,,b", ",", true, false);
        assertThat(result).contains("a", "b");
    }

    @Test
    void test_toStringArray_withNullCollection() {
        String[] result = StringUtils.toStringArray(null);
        assertThat(result).isNull();
    }

    @Test
    void test_toStringArray_withValidCollection() {
        List<String> list = Arrays.asList("a", "b", "c");
        String[] result = StringUtils.toStringArray(list);
        assertThat(result).containsExactly("a", "b", "c");
    }

    @Test
    void test_toStringArray_withEmptyCollection() {
        List<String> list = new ArrayList<String>();
        String[] result = StringUtils.toStringArray(list);
        assertThat(result).isEmpty();
    }

    @Test
    void test_configLocationDelimiters() {
        assertThat(StringUtils.CONFIG_LOCATION_DELIMITERS).isEqualTo(",; \t\n");
    }
}
