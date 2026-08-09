/*
 * Copyright (c) 2024, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.easy4j.doc.bus.event;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link BuildJobTypes}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class BuildJobTypes_Test {

    @Test
    void test_values() {
        BuildJobTypes[] values = BuildJobTypes.values();
        assertThat(values).hasSize(3);
        assertThat(values).containsExactly(BuildJobTypes.DOC, BuildJobTypes.HTML, BuildJobTypes.URL);
    }

    @Test
    void test_valueOf() {
        assertThat(BuildJobTypes.valueOf("DOC")).isEqualTo(BuildJobTypes.DOC);
        assertThat(BuildJobTypes.valueOf("HTML")).isEqualTo(BuildJobTypes.HTML);
        assertThat(BuildJobTypes.valueOf("URL")).isEqualTo(BuildJobTypes.URL);
    }

    @Test
    void test_valueOf_invalid() {
        assertThatThrownBy(() -> BuildJobTypes.valueOf("INVALID"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
