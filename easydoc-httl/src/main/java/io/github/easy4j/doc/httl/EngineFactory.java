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

import java.io.IOException;
import java.util.Properties;

import org.docx4j.Docx4jProperties;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.utils.ConfigUtils;

import httl.Engine;

/**
 * Immutable factory that lazily creates and caches an HTTL {@link Engine}
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
                    Properties props = ConfigUtils.filterWithPrefix("docx4j.httl.", "docx4j.httl.", Docx4jProperties.getProperties(), false);
                    props.setProperty("template.directory", props.getProperty("template.directory"));
                    props.setProperty("template.suffix", props.getProperty("template.suffix", ".httl"));
                    props.setProperty("input.encoding", props.getProperty("input.encoding", Docx4jConstants.DEFAULT_CHARSETNAME));
                    props.setProperty("output.encoding", props.getProperty("output.encoding", Docx4jConstants.DEFAULT_CHARSETNAME));
                    local = Engine.getEngine(props);
                    engine = local;
                }
            }
        }
        return local;
    }
}
