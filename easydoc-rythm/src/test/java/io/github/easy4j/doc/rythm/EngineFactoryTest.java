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
package io.github.easy4j.doc.rythm;

import java.io.IOException;
import java.util.Properties;

import org.docx4j.Docx4jProperties;
import org.junit.jupiter.api.Test;
import org.rythmengine.RythmEngine;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression tests for {@link EngineFactory}: constructing the factory against a
 * completely EMPTY {@code docx4j.properties} must initialize the engine without
 * throwing; an illegal engine.mode must fail fast with context instead of a raw
 * {@link IllegalArgumentException}.
 */
class EngineFactoryTest {

    @Test
    void factoryWithEmptyDocx4jPropertiesInitializes() throws Exception {
        Properties global = Docx4jProperties.getProperties();
        Properties backup = new Properties();
        backup.putAll(global);
        try {
            // 模拟“空”的 docx4j.properties：清空全局属性后构建工厂，引擎必须正常初始化（不抛 NPE）
            global.clear();
            EngineFactory factory = new EngineFactory();
            // 若初始化抛出任何异常，本测试将直接失败
            RythmEngine engine = factory.get();
            assertNotNull(engine, "engine must initialize without throwing on empty properties");
            assertSame(engine, factory.get(), "factory.get() must return the same cached instance");
        } finally {
            // 恢复全局属性，避免污染同一 JVM 中运行的其他测试
            global.clear();
            global.putAll(backup);
        }
    }

    @Test
    void illegalEngineModeFailsWithContext() {
        Properties global = Docx4jProperties.getProperties();
        Properties backup = new Properties();
        backup.putAll(global);
        try {
            // 非法 engine.mode：必须以带上下文的 IOException 快速失败，而不是裸 IllegalArgumentException
            Docx4jProperties.setProperty("docx4j.rythm.engine.mode", "not-a-mode");
            EngineFactory factory = new EngineFactory();
            IOException ex = assertThrows(IOException.class, factory::get);
            assertTrue(ex.getMessage().contains("not-a-mode"),
                    "error message must mention the offending value: " + ex.getMessage());
        } finally {
            // 恢复全局属性，避免污染同一 JVM 中运行的其他测试
            global.clear();
            global.putAll(backup);
        }
    }
}
