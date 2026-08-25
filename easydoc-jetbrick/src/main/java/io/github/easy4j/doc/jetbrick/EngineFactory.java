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
                    Properties ps = new Properties();
                    ConfigLoader loader = new ConfigLoader();
                    try {
                        LOG.info("Loading config file: {}", JetConfig.DEFAULT_CONFIG_FILE);
                        loader.load(JetConfig.DEFAULT_CONFIG_FILE);
                        ps = loader.asProperties();
                    } catch (Exception e) {
                        // 默认配置文件不存在
                        LOG.warn("No default config file found: {}", JetConfig.DEFAULT_CONFIG_FILE);
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
