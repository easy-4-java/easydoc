package io.github.easy4j.doc.jsp.engine.runtime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link OriginalStream}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("OriginalStream Tests")
class OriginalStreamTest {

    @Test
    @DisplayName("should be an interface")
    void shouldBeAnInterface() {
        assertThat(OriginalStream.class).isInterface();
    }

    @Test
    @DisplayName("class should be loadable")
    void classShouldBeLoadable() {
        assertThat(OriginalStream.class.getName()).isEqualTo("io.github.easy4j.doc.jsp.engine.runtime.OriginalStream");
    }

}
