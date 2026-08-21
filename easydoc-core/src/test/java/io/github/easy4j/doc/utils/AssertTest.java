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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

class AssertTest {

    @Test
    void isTrueAcceptsTrue() {
        assertDoesNotThrow(() -> Assert.isTrue(true));
        assertDoesNotThrow(() -> Assert.isTrue(true, "msg"));
    }

    @Test
    void isTrueRejectsFalse() {
        assertThrows(IllegalArgumentException.class, () -> Assert.isTrue(false));
        assertThrows(IllegalArgumentException.class, () -> Assert.isTrue(false, "boom"));
    }

    @Test
    void isNullAcceptsNull() {
        assertDoesNotThrow(() -> Assert.isNull(null));
        assertDoesNotThrow(() -> Assert.isNull(null, "ok"));
    }

    @Test
    void isNullRejectsNonNull() {
        assertThrows(IllegalArgumentException.class, () -> Assert.isNull("x"));
        assertThrows(IllegalArgumentException.class, () -> Assert.isNull("x", "boom"));
    }

    @Test
    void notNullAcceptsNonNull() {
        assertDoesNotThrow(() -> Assert.notNull("x"));
        assertDoesNotThrow(() -> Assert.notNull("x", "ok"));
    }

    @Test
    void notNullRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> Assert.notNull(null));
        assertThrows(IllegalArgumentException.class, () -> Assert.notNull(null, "boom"));
    }

    @Test
    void hasLengthAcceptsNonEmpty() {
        assertDoesNotThrow(() -> Assert.hasLength("x"));
        assertDoesNotThrow(() -> Assert.hasLength("x", "ok"));
    }

    @Test
    void hasLengthRejectsNullAndEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Assert.hasLength(null));
        assertThrows(IllegalArgumentException.class, () -> Assert.hasLength(""));
        assertThrows(IllegalArgumentException.class, () -> Assert.hasLength("  "));
    }

    @Test
    void hasTextRejectsBlank() {
        assertThrows(IllegalArgumentException.class, () -> Assert.hasText(" "));
        assertThrows(IllegalArgumentException.class, () -> Assert.hasText(""));
    }

    @Test
    void hasTextAcceptsNonBlank() {
        assertDoesNotThrow(() -> Assert.hasText("x"));
    }

    @Test
    void isInstanceOfAcceptsInstance() {
        assertDoesNotThrow(() -> Assert.isInstanceOf(String.class, "abc"));
    }

    @Test
    void isInstanceOfRejectsMismatch() {
        assertThrows(IllegalArgumentException.class, () -> Assert.isInstanceOf(Integer.class, "abc"));
    }

    @Test
    void isAssignableAcceptsSubclass() {
        assertDoesNotThrow(() -> Assert.isAssignable(Number.class, Integer.class));
    }

    @Test
    void isAssignableRejectsNonAssignable() {
        assertThrows(IllegalArgumentException.class, () -> Assert.isAssignable(Integer.class, String.class));
    }

    @Test
    void isAssignableRejectsNullSubType() {
        assertThrows(IllegalArgumentException.class, () -> Assert.isAssignable(Integer.class, null));
    }

    @Test
    void isAssignableRejectsNullSuperType() {
        assertThrows(IllegalArgumentException.class, () -> Assert.isAssignable(null, Integer.class));
    }

    @Test
    void doesNotContainAcceptsCleanText() {
        assertDoesNotThrow(() -> Assert.doesNotContain("hello", "world"));
    }

    @Test
    void doesNotContainRejectsSubstring() {
        assertThrows(IllegalArgumentException.class, () -> Assert.doesNotContain("hello world", "world"));
    }

    @Test
    void stateAcceptsTrue() {
        assertDoesNotThrow(() -> Assert.state(true));
    }

    @Test
    void stateRejectsFalse() {
        assertThrows(IllegalStateException.class, () -> Assert.state(false));
    }

    @Test
    void noNullElementsAcceptsCleanArray() {
        assertDoesNotThrow(() -> Assert.noNullElements(new Object[]{"a", "b"}));
    }

    @Test
    void noNullElementsRejectsNullElement() {
        assertThrows(IllegalArgumentException.class, () -> Assert.noNullElements(new Object[]{"a", null}));
    }

    @Test
    void notEmptyCollectionRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> Assert.notEmpty((List<?>) null));
    }

    @Test
    void notEmptyCollectionRejectsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Assert.notEmpty(new ArrayList<>()));
    }

    @Test
    void notEmptyCollectionAcceptsPopulated() {
        assertDoesNotThrow(() -> Assert.notEmpty(Collections.singletonList("a")));
    }

    @Test
    void notEmptyMapRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> Assert.notEmpty((java.util.Map<?, ?>) null));
    }

    @Test
    void notEmptyMapRejectsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Assert.notEmpty(new HashMap<>()));
    }

    @Test
    void notEmptyMapAcceptsPopulated() {
        java.util.Map<String, String> m = new HashMap<>();
        m.put("a", "b");
        assertDoesNotThrow(() -> Assert.notEmpty(m));
    }
}
