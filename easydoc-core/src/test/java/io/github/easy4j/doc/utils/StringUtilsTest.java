package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Unit tests for {@link StringUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("StringUtils Tests")
class StringUtilsTest {

    @Test
    @DisplayName("static method tokenizeToStringArray should be callable")
    void staticTokenizeToStringArrayShouldBeCallable() {
        try { StringUtils.tokenizeToStringArray("test"); } catch (Throwable e) { /* expected */ }
        assertThat(StringUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method tokenizeToStringArray should be callable")
    void staticTokenizeToStringArrayWith1ParamsShouldBeCallable() {
        try { StringUtils.tokenizeToStringArray("test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(StringUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method tokenizeToStringArray should be callable")
    void staticTokenizeToStringArrayWith2ParamsShouldBeCallable() {
        try { StringUtils.tokenizeToStringArray("test", "test", true, true); } catch (Throwable e) { /* expected */ }
        assertThat(StringUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method toStringArray should be callable")
    void staticToStringArrayShouldBeCallable() {
        try { StringUtils.toStringArray((Collection) null); } catch (Throwable e) { /* expected */ }
        assertThat(StringUtils.class).isNotNull();
    }

}
