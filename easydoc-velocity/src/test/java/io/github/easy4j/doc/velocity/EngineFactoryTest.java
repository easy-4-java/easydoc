/*
 * Copyright (c) 2018, hiwepy (https://github.com/easy-4-java).
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
package io.github.easy4j.doc.velocity;

import java.io.IOException;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.docx4j.Docx4jProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression tests for {@link EngineFactory}: a missing classpath template
 * directory must produce a descriptive {@link IOException} instead of an NPE,
 * and the surviving Velocity properties (resource loader, encodings) must be
 * accepted by the engine.
 */
class EngineFactoryTest {

    @Test
    void factoryInitializesWithConfiguredTemplateDirectory() throws Exception {
        // 测试类路径下存在 /tpl 模板目录（见 src/test/resources/docx4j.properties）
        EngineFactory factory = new EngineFactory();
        VelocityEngine first = assertDoesNotThrow(factory::get);
        assertNotNull(first);
        VelocityEngine second = factory.get();
        assertSame(first, second, "factory.get() must return the same cached instance");
    }

    @Test
    void missingTemplateDirectoryFailsWithDescriptiveError() {
        Properties global = Docx4jProperties.getProperties();
        Properties backup = new Properties();
        backup.putAll(global);
        try {
            // 将模板目录指向一个 classpath 上不存在的资源，必须得到可诊断的 IOException 而非 NPE
            Docx4jProperties.setProperty("docx4j.velocity.file.resource.loader.path", "/definitely/missing/template/dir");
            EngineFactory factory = new EngineFactory();
            IOException ex = assertThrows(IOException.class, factory::get);
            assertTrue(ex.getMessage().contains("/definitely/missing/template/dir"),
                    "error message must mention the offending path: " + ex.getMessage());
        } finally {
            // 恢复全局属性，避免污染同一 JVM 中运行的其他测试
            global.clear();
            global.putAll(backup);
        }
    }
}
