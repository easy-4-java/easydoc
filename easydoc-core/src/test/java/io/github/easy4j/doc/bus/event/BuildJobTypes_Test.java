/* Copyright (c) 2024 */
package io.github.easy4j.doc.bus.event;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/** Unit tests for {@link BuildJobTypes}. @author <a href="https://github.com/loong10k">Loong Wan</a> */
class BuildJobTypes_Test {
    @Test void test_values() { assertThat(BuildJobTypes.values()).isNotEmpty(); }
    @Test void test_valueOf() { assertThat(BuildJobTypes.valueOf(BuildJobTypes.values()[0].name())).isNotNull(); }
}
