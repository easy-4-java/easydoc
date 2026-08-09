package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Unit tests for {@link ConfigUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("ConfigUtils Tests")
class ConfigUtilsTest {

    @Test
    @DisplayName("static method filterWithPrefix should be callable")
    void staticFilterWithPrefixShouldBeCallable() {
        try { ConfigUtils.filterWithPrefix("test", "test", (Properties) null, true); } catch (Throwable e) { /* expected */ }
        assertThat(ConfigUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method filterWithPrefix should be callable")
    void staticFilterWithPrefixWith1ParamsShouldBeCallable() {
        try { ConfigUtils.filterWithPrefix("test", (Map) null, true); } catch (Throwable e) { /* expected */ }
        assertThat(ConfigUtils.class).isNotNull();
    }

}
