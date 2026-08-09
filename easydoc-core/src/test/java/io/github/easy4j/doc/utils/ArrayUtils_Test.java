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

import java.util.Set;

/**
 * Unit tests for {@link ArrayUtils}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class ArrayUtils_Test {

    @Test
    void test_asSet_withMultipleElements() {
        Set<String> result = ArrayUtils.asSet("a", "b", "c");
        assertThat(result).containsExactlyInAnyOrder("a", "b", "c");
    }

    @Test
    void test_asSet_withSingleElement() {
        Set<Integer> result = ArrayUtils.asSet(1);
        assertThat(result).containsExactly(1);
    }

    @Test
    void test_asSet_withEmptyArray() {
        Set<String> result = ArrayUtils.asSet();
        assertThat(result).isEmpty();
    }

    @Test
    void test_asSet_withDuplicates() {
        Set<String> result = ArrayUtils.asSet("a", "a", "b");
        assertThat(result).hasSize(2);
    }
}
