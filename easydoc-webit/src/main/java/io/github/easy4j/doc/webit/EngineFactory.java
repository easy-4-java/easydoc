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
package io.github.easy4j.doc.webit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.docx4j.Docx4jProperties;

import webit.script.CFG;
import webit.script.Engine;

/**
 * Immutable factory that lazily creates and caches a Webit {@link Engine}
 * instance using double-checked locking.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class EngineFactory {

    private volatile Engine engine;

    /**
     * Returns the shared {@link Engine} instance, creating it on first access.
     */
    public Engine get() throws IOException {
        Engine local = engine;
        if (local == null) {
            synchronized (this) {
                local = engine;
                if (local == null) {
                    Map<String, Object> ps = new HashMap<String, Object>();
                    ps.put(CFG.APPEND_LOST_SUFFIX, Docx4jProperties.getProperty("docx4j.webit.engine.appendLostSuffix", false));
                    ps.put(CFG.INIT_TEMPLATES, Docx4jProperties.getProperty("docx4j.webit.engine.initTemplates"));
                    ps.put(CFG.LOADER, Docx4jProperties.getProperty("docx4j.webit.engine.resourceLoader", "webit.script.loaders.impl.ClasspathLoader"));
                    ps.put(CFG.LOADER_ENCODING, Docx4jProperties.getProperty("docx4j.webit.loader.encoding", Engine.UTF_8));
                    ps.put(CFG.LOADER_ROOT, Docx4jProperties.getProperty("docx4j.webit.loader.root"));
                    ps.put(CFG.LOGGER, Docx4jProperties.getProperty("docx4j.webit.engine.logger", "webit.script.loggers.impl.NOPLogger"));
                    ps.put(CFG.LOOSE_VAR, Docx4jProperties.getProperty("docx4j.webit.engine.looseVar", false));
                    ps.put(CFG.OUT_ENCODING, Docx4jProperties.getProperty("docx4j.webit.engine.encoding", Engine.UTF_8));
                    ps.put(CFG.SHARE_ROOT, Docx4jProperties.getProperty("docx4j.webit.engine.shareRootData", true));
                    ps.put(CFG.SUFFIX, Docx4jProperties.getProperty("docx4j.webit.engine.suffix", ".wit"));
                    ps.put(CFG.TEXT_FACTORY, Docx4jProperties.getProperty("docx4j.webit.engine.textStatementFactory", CFG.SIMPLE_TEXT_FACTORY));
                    ps.put(CFG.TRIM_CODE_LINE, Docx4jProperties.getProperty("docx4j.webit.engine.trimCodeBlockBlankLine", true));
                    ps.put(CFG.VARS, Docx4jProperties.getProperty("docx4j.webit.engine.vars"));

                    local = Engine.create("", ps);
                    engine = local;
                }
            }
        }
        return local;
    }
}
