package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link Assert} 契约回归测试。
 *
 * <p>修复背景（P0，port from 3.0.x c2b1c4a）：{@code Assert.notEmpty(Object[])} 原实现条件写反，
 * 非空数组抛异常、null/空数组放行。本测试锁定修复后的契约行为。</p>
 */
@DisplayName("Assert contract regression tests")
class AssertContractRegressionTest {

    @Test
    @DisplayName("notEmpty(Object[]) — null array throws")
    void notEmptyArrayRejectsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty((Object[]) null, "array is required"));
        assertEquals("array is required", ex.getMessage(), "自定义消息应原样传递");
    }

    @Test
    @DisplayName("notEmpty(Object[]) — zero-length array throws")
    void notEmptyArrayRejectsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty(new Object[0], "empty"));
        assertThrows(IllegalArgumentException.class, () -> Assert.notEmpty(new String[0]));
    }

    @Test
    @DisplayName("notEmpty(Object[]) — non-empty arrays are accepted")
    void notEmptyArrayAcceptsNonEmpty() {
        assertDoesNotThrow(() -> Assert.notEmpty(new Object[] { new Object() }, "ok"));
        assertDoesNotThrow(() -> Assert.notEmpty(new Integer[] { 1, 2, 3 }));
        // 数组元素为 null 不影响 notEmpty 的“非空”判定（该语义由 noNullElements 承担）
        assertDoesNotThrow(() -> Assert.notEmpty(new String[] { null }, "ok"));
    }

    @Test
    @DisplayName("hasLength — rejects null, empty and whitespace-only strings (documented semantics)")
    void hasLengthRejectsBlankFamily() {
        // 已约定的语义：本类 hasLength 与 hasText 相同，均拒绝纯空白串
        assertThrows(IllegalArgumentException.class, () -> Assert.hasLength(null));
        assertThrows(IllegalArgumentException.class, () -> Assert.hasLength(""));
        assertThrows(IllegalArgumentException.class, () -> Assert.hasLength("   "));
        assertThrows(IllegalArgumentException.class, () -> Assert.hasLength("\t\n", "blank"));
    }

    @Test
    @DisplayName("hasLength — accepts strings with at least one character")
    void hasLengthAcceptsAnyCharSequence() {
        assertDoesNotThrow(() -> Assert.hasLength("a"));
        assertDoesNotThrow(() -> Assert.hasLength(" x ", "ok"));
    }

    @Test
    @DisplayName("hasText — unchanged baseline behavior for comparison")
    void hasTextBaseline() {
        assertThrows(IllegalArgumentException.class, () -> Assert.hasText(" "));
        assertDoesNotThrow(() -> Assert.hasText("x"));
    }
}
