/* Copyright (c) 2024 */
package io.github.easy4j.doc.ognl;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** Unit tests for {@link DefaultMemberAccess}. @author <a href="https://github.com/loong10k">Loong Wan</a> */
class DefaultMemberAccess_Test {
    @Test void test_exists() { assertThat(DefaultMemberAccess.class).isNotNull(); }
}
