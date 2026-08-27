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
package io.github.easy4j.doc.httl;

import java.util.Properties;

import org.docx4j.Docx4jProperties;
import org.junit.jupiter.api.Test;

import httl.Engine;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Regression tests for {@link EngineFactory}: constructing the factory against
 * a completely EMPTY {@code docx4j.properties} must not throw a
 * {@link NullPointerException} (the old implementation called
 * {@code props.setProperty(key, props.getProperty(key))}, which passed a null
 * value into the underlying Hashtable).
 */
class EngineFactoryTest {

    @Test
    void factoryWithEmptyDocx4jPropertiesDoesNotThrow() throws Exception {
        Properties global = Docx4jProperties.getProperties();
        Properties backup = new Properties();
        backup.putAll(global);
        try {
            // 模拟“空”的 docx4j.properties：清空全局属性后构建工厂，不得抛出 NPE
            global.clear();
            EngineFactory factory = new EngineFactory();
            Engine engine = factory.get();
            assertNotNull(engine, "HTTL engine must be created even with empty properties");
        } finally {
            // 恢复全局属性，避免污染同一 JVM 中运行的其他测试
            global.clear();
            global.putAll(backup);
        }
    }

    /**
     * DCL short-circuit: repeated calls on a warm factory return the same cached engine.
     */
    @Test
    void factoryReturnsSameEngineOnConsecutiveCalls() throws Exception {
        EngineFactory factory = new EngineFactory();
        Engine first = factory.get();
        Engine second = factory.get();
        assertNotNull(first);
        assertSame(first, second, "factory.get() must return the same cached instance");
    }
}
