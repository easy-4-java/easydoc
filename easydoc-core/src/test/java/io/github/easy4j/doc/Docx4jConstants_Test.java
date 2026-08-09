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
package io.github.easy4j.doc;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link Docx4jConstants}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class Docx4jConstants_Test {

    @Test
    void test_defaultCharsetName() {
        assertThat(Docx4jConstants.DEFAULT_CHARSETNAME).isEqualTo("UTF-8");
    }

    @Test
    void test_defaultTimeoutMillis() {
        assertThat(Docx4jConstants.DEFAULT_TIMEOUTMILLIS).isEqualTo(5000);
    }

    @Test
    void test_docx4jParams() {
        assertThat(Docx4jConstants.DOCX4J_PARAM_01).isEqualTo("docx4j.App.write");
        assertThat(Docx4jConstants.DOCX4J_PARAM_02).isEqualTo("docx4j.Application");
        assertThat(Docx4jConstants.DOCX4J_PARAM_03).isEqualTo("docx4j.AppVersion");
    }
}
