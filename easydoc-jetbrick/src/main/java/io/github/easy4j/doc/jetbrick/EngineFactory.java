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
package io.github.easy4j.doc.jetbrick;

import java.io.IOException;
import java.util.Properties;

import org.docx4j.Docx4jProperties;
import io.github.easy4j.doc.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetbrick.config.ConfigLoader;
import jetbrick.template.JetConfig;
import jetbrick.template.JetEngine;

/**
 * Immutable factory that lazily creates and caches a Jetbrick {@link JetEngine}
 * instance using double-checked locking.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class EngineFactory {

    private static final Logger LOG = LoggerFactory.getLogger(EngineFactory.class);

    private volatile JetEngine engine;

    /**
     * Returns the shared {@link JetEngine} instance, creating it on first access.
     */
    public JetEngine get() throws IOException {
        JetEngine local = engine;
        if (local == null) {
            synchronized (this) {
                local = engine;
                if (local == null) {
                    /*
                     * 配置优先级：
                     * 1) 优先加载 Jetbrick 自身的默认配置文件 classpath:/jetbrick-template.properties（存在即整份生效）；
                     * 2) 仅当其不存在时，才回退使用 docx4j.properties 中以 docx4j.jetx.* 命名的配置
                     *    （键名中的下划线会被还原为点号，见 ConfigUtils#filterWithPrefix 的 escape 语义）。
                     */
                    Properties ps = new Properties();
                    ConfigLoader loader = new ConfigLoader();
                    try {
                        LOG.info("Loading config file: {}", JetConfig.DEFAULT_CONFIG_FILE);
                        loader.load(JetConfig.DEFAULT_CONFIG_FILE);
                        ps = loader.asProperties();
                    } catch (IllegalStateException e) {
                        // ConfigLoader 在默认配置文件不存在（或读取失败）时抛出 IllegalStateException，
                        // 记录回退原因后改用 docx4j.jetx.* 属性，避免整个异常链被静默吞掉
                        LOG.warn("无法从 '{}' 加载 Jetbrick 配置（{}），回退为 docx4j.properties 中 docx4j.jetx.* 前缀的配置",
                                JetConfig.DEFAULT_CONFIG_FILE, e.getMessage(), e);
                        ps = ConfigUtils.filterWithPrefix("docx4j.jetx.", "docx4j.", Docx4jProperties.getProperties(), true);
                    }
                    local = JetEngine.create(ps);
                    engine = local;
                }
            }
        }
        return local;
    }
}
