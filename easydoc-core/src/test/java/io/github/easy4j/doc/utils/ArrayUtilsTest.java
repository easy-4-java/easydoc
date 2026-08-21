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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

class ArrayUtilsTest {

    @Test
    void asSetReturnsSetOfElements() {
        Set<String> set = ArrayUtils.asSet("a", "b", "c");
        assertNotNull(set);
        assertEquals(3, set.size());
        assertTrue(set.contains("a"));
        assertTrue(set.contains("b"));
        assertTrue(set.contains("c"));
    }

    @Test
    void asSetReturnsEmptySet() {
        Set<String> set = ArrayUtils.asSet();
        assertNotNull(set);
        assertEquals(0, set.size());
    }

    @Test
    void asSetDeduplicates() {
        Set<String> set = ArrayUtils.asSet("a", "b", "a");
        assertNotNull(set);
        assertEquals(2, set.size());
    }

    @Test
    void asSetPreservesIntegers() {
        Set<Integer> set = ArrayUtils.asSet(1, 2, 3);
        assertNotNull(set);
        assertEquals(3, set.size());
        assertTrue(set.contains(1));
    }

    @Test
    void asSetPreservesNull() {
        Set<String> set = ArrayUtils.asSet("a", null);
        assertNotNull(set);
        // HashSet accepts null
        assertTrue(set.contains(null));
    }
}
