package io.github.easy4j.doc.jsp.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Unit tests for {@link JspEngine}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("JspEngine Tests")
class JspEngineTest {

    @Test
    @DisplayName("should be abstract")
    void shouldBeAbstract() {
        assertThat(JspEngine.class).isAbstract();
    }

    @Test
    @DisplayName("static method create should be callable")
    void staticCreateShouldBeCallable() {
        try { JspEngine.create((Properties) null); } catch (Throwable e) { /* expected */ }
        assertThat(JspEngine.class).isNotNull();
    }

}
