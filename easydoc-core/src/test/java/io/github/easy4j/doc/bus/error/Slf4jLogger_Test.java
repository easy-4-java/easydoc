/* Copyright (c) 2024 */
package io.github.easy4j.doc.bus.error;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** Unit tests for {@link Slf4jLogger}. @author <a href="https://github.com/loong10k">Loong Wan</a> */
class Slf4jLogger_Test {
    @Test void test_constructor() throws Exception { Slf4jLogger o = new Slf4jLogger(); assertThat(o).isNotNull(); }
}
