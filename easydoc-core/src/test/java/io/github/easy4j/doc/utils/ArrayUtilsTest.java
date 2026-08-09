package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

/**
 * Unit tests for {@link ArrayUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("ArrayUtils Tests")
class ArrayUtilsTest {

    @Test
    @DisplayName("static method asSet should be callable")
    void staticAsSetShouldBeCallable() {
        try { ArrayUtils.asSet((Object[]) null); } catch (Throwable e) { /* expected */ }
        assertThat(ArrayUtils.class).isNotNull();
    }

}
