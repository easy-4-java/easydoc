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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void tokenizeToStringArrayNullReturnsNull() {
        assertNull(StringUtils.tokenizeToStringArray(null));
    }

    @Test
    void tokenizeToStringArrayDefaultDelimiters() {
        String[] tokens = StringUtils.tokenizeToStringArray("a, b; c\td");
        assertArrayEquals(new String[]{"a", "b", "c", "d"}, tokens);
    }

    @Test
    void tokenizeToStringArrayCustomDelimiters() {
        String[] tokens = StringUtils.tokenizeToStringArray("a,b,c", ",");
        assertArrayEquals(new String[]{"a", "b", "c"}, tokens);
    }

    @Test
    void tokenizeToStringArrayIgnoresEmpty() {
        String[] tokens = StringUtils.tokenizeToStringArray("a,,b", ",", true, true);
        assertArrayEquals(new String[]{"a", "b"}, tokens);
    }

    @Test
    void tokenizeToStringArrayFullArgsNoTrim() {
        String[] tokens = StringUtils.tokenizeToStringArray("a b c", " ", false, false);
        assertArrayEquals(new String[]{"a", "b", "c"}, tokens);
    }

    @Test
    void tokenizeToStringArrayFullArgsTrimAndIgnoreEmpty() {
        String[] tokens = StringUtils.tokenizeToStringArray(" a , b ,, c ", ",", true, true);
        assertArrayEquals(new String[]{"a", "b", "c"}, tokens);
    }

    @Test
    void toStringArrayNullReturnsNull() {
        assertNull(StringUtils.toStringArray(null));
    }

    @Test
    void toStringArrayConvertsList() {
        List<String> list = Arrays.asList("a", "b", "c");
        String[] arr = StringUtils.toStringArray(list);
        assertNotNull(arr);
        assertArrayEquals(new String[]{"a", "b", "c"}, arr);
    }

    @Test
    void toStringArrayEmptyList() {
        String[] arr = StringUtils.toStringArray(new java.util.ArrayList<>());
        assertNotNull(arr);
        assertEquals(0, arr.length);
    }

    @Test
    void configLocationDelimitersConstantIsNonBlank() {
        assertNotNull(StringUtils.CONFIG_LOCATION_DELIMITERS);
        org.junit.jupiter.api.Assertions.assertTrue(StringUtils.CONFIG_LOCATION_DELIMITERS.length() > 0);
    }
}
