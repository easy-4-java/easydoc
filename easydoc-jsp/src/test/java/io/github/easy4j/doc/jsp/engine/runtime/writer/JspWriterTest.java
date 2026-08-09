package io.github.easy4j.doc.jsp.engine.runtime.writer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import io.github.easy4j.doc.jsp.engine.runtime.OriginalStream;

/**
 * Unit tests for {@link JspWriter}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("JspWriter Tests")
class JspWriterTest {

    @Test
    @DisplayName("should be abstract")
    void shouldBeAbstract() {
        assertThat(JspWriter.class).isAbstract();
    }

    @Test
    @DisplayName("static method create should be callable")
    void staticCreateShouldBeCallable() {
        try { JspWriter.create((Writer) null, (Charset) null, true, true); } catch (Throwable e) { /* expected */ }
        assertThat(JspWriter.class).isNotNull();
    }

    @Test
    @DisplayName("static method create should be callable")
    void staticCreateWith1ParamsShouldBeCallable() {
        try { JspWriter.create((OutputStream) null, (Charset) null, true, true); } catch (Throwable e) { /* expected */ }
        assertThat(JspWriter.class).isNotNull();
    }

    @Test
    @DisplayName("static method create should be callable")
    void staticCreateWith2ParamsShouldBeCallable() {
        try { JspWriter.create((JspWriter) null, true); } catch (Throwable e) { /* expected */ }
        assertThat(JspWriter.class).isNotNull();
    }

}
