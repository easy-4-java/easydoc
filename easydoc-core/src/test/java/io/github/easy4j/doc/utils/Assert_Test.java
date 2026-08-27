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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for {@link Assert}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class Assert_Test {

    // --- isTrue ---

    @Test
    void test_isTrue_whenTrue_doesNotThrow() {
        Assert.isTrue(true, "should not throw");
    }

    @Test
    void test_isTrue_whenFalse_throwsException() {
        assertThatThrownBy(() -> Assert.isTrue(false, "must be true"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("must be true");
    }

    @Test
    void test_isTrue_noMessage_whenFalse() {
        assertThatThrownBy(() -> Assert.isTrue(false))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // --- isNull ---

    @Test
    void test_isNull_withNull_doesNotThrow() {
        Assert.isNull(null, "should not throw");
    }

    @Test
    void test_isNull_withNonNull_throwsException() {
        assertThatThrownBy(() -> Assert.isNull("value", "must be null"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("must be null");
    }

    @Test
    void test_isNull_noMessage_withNonNull() {
        assertThatThrownBy(() -> Assert.isNull("value"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // --- notNull ---

    @Test
    void test_notNull_withNonNull_doesNotThrow() {
        Assert.notNull("value", "should not throw");
    }

    @Test
    void test_notNull_withNull_throwsException() {
        assertThatThrownBy(() -> Assert.notNull(null, "must not be null"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("must not be null");
    }

    @Test
    void test_notNull_noMessage_withNull() {
        assertThatThrownBy(() -> Assert.notNull(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // --- hasLength ---

    @Test
    void test_hasLength_withNonEmpty_doesNotThrow() {
        Assert.hasLength("test", "should not throw");
    }

    @Test
    void test_hasLength_withEmpty_throwsException() {
        assertThatThrownBy(() -> Assert.hasLength("", "must have length"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_hasLength_noMessage_withNonEmpty() {
        Assert.hasLength("test");
    }

    // --- hasText ---

    @Test
    void test_hasText_withValidText_doesNotThrow() {
        Assert.hasText("hello", "should not throw");
    }

    @Test
    void test_hasText_withBlank_throwsException() {
        assertThatThrownBy(() -> Assert.hasText("   ", "must have text"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_hasText_noMessage_withValidText() {
        Assert.hasText("hello");
    }

    // --- doesNotContain ---

    @Test
    void test_doesNotContain_whenNotContained_doesNotThrow() {
        Assert.doesNotContain("hello world", "xyz", "should not throw");
    }

    @Test
    void test_doesNotContain_whenContained_throwsException() {
        assertThatThrownBy(() -> Assert.doesNotContain("hello world", "world", "must not contain"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_doesNotContain_noMessage_whenContained() {
        assertThatThrownBy(() -> Assert.doesNotContain("hello world", "world"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // --- notEmpty (Object[]) ---
    // 契约行为（生产缺陷已修复）：修复前条件写反——非空数组抛异常、null/空数组放行；
    // 现按 javadoc 契约断言：null/空抛 IllegalArgumentException，非空放行。

    @Test
    void test_notEmpty_array_withEmpty_throwsException() {
        assertThatThrownBy(() -> Assert.notEmpty(new Object[0], "must not be empty"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_notEmpty_array_withNull_throwsException() {
        assertThatThrownBy(() -> Assert.notEmpty((Object[]) null, "must not be null"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_notEmpty_array_withNonEmpty_doesNotThrow() {
        Assert.notEmpty(new Object[]{"a"}, "should not throw");
    }

    // --- noNullElements ---

    @Test
    void test_noNullElements_withNoNulls_doesNotThrow() {
        Assert.noNullElements(new Object[]{"a", "b"}, "should not throw");
    }

    @Test
    void test_noNullElements_withNulls_throwsException() {
        assertThatThrownBy(() -> Assert.noNullElements(new Object[]{"a", null}, "has null"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_noNullElements_withNullArray_doesNotThrow() {
        Assert.noNullElements(null);
    }

    // --- notEmpty (Collection) ---

    @Test
    void test_notEmpty_collection_withNonEmpty_doesNotThrow() {
        Collection<String> c = new ArrayList<String>();
        c.add("a");
        Assert.notEmpty(c, "should not throw");
    }

    @Test
    void test_notEmpty_collection_withEmpty_throwsException() {
        assertThatThrownBy(() -> Assert.notEmpty(new ArrayList<String>(), "must not be empty"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_notEmpty_collection_withNull_throwsException() {
        assertThatThrownBy(() -> Assert.notEmpty((Collection<?>) null, "must not be null"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // --- notEmpty (Map) ---

    @Test
    void test_notEmpty_map_withNonEmpty_doesNotThrow() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key", "value");
        Assert.notEmpty(map, "should not throw");
    }

    @Test
    void test_notEmpty_map_withEmpty_throwsException() {
        assertThatThrownBy(() -> Assert.notEmpty(new HashMap<String, String>(), "must not be empty"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void test_notEmpty_map_withNull_throwsException() {
        assertThatThrownBy(() -> Assert.notEmpty((Map<?, ?>) null, "must not be null"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // --- isInstanceOf ---

    @Test
    void test_isInstanceOf_withMatchingType_doesNotThrow() {
        Assert.isInstanceOf(String.class, "hello");
    }

    @Test
    void test_isInstanceOf_withNonMatchingType_throwsException() {
        assertThatThrownBy(() -> Assert.isInstanceOf(String.class, 123))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // --- isAssignable ---

    @Test
    void test_isAssignable_withAssignable_doesNotThrow() {
        Assert.isAssignable(Number.class, Integer.class);
    }

    @Test
    void test_isAssignable_withNonAssignable_throwsException() {
        assertThatThrownBy(() -> Assert.isAssignable(String.class, Integer.class))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // --- state ---

    @Test
    void test_state_whenTrue_doesNotThrow() {
        Assert.state(true, "should not throw");
    }

    @Test
    void test_state_whenFalse_throwsException() {
        assertThatThrownBy(() -> Assert.state(false, "invalid state"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("invalid state");
    }

    @Test
    void test_state_noMessage_whenFalse() {
        assertThatThrownBy(() -> Assert.state(false))
            .isInstanceOf(IllegalStateException.class);
    }
}
