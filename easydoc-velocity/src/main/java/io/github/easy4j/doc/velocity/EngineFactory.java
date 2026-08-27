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
import java.net.URL;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.docx4j.Docx4jProperties;
import io.github.easy4j.doc.Docx4jConstants;

/**
 * Immutable factory that lazily creates and caches a Velocity {@link VelocityEngine}
 * instance using double-checked locking.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class EngineFactory {

    private volatile VelocityEngine engine;

    /**
     * Returns the shared {@link VelocityEngine} instance, creating it on first access.
     */
    public VelocityEngine get() throws IOException {
        VelocityEngine local = engine;
        if (local == null) {
            synchronized (this) {
                local = engine;
                if (local == null) {
                    Properties ps = new Properties();
                    // 资源加载器：文件系统（classpath 中的模板目录）
                    ps.setProperty("resource.loader", Docx4jProperties.getProperty("docx4j.velocity.resource.loader", "file"));
                    ps.setProperty("file.resource.loader.class", Docx4jProperties.getProperty("docx4j.velocity.file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader"));
                    ps.setProperty("file.resource.loader.cache", Docx4jProperties.getProperty("docx4j.velocity.file.resource.loader.cache", "true"));
                    // 模板目录：从 classpath 解析出物理路径，资源不存在时给出可诊断的错误而非 NPE
                    String loaderPath = Docx4jProperties.getProperty("docx4j.velocity.file.resource.loader.path", "/template");
                    URL templateUrl = this.getClass().getResource(loaderPath);
                    if (templateUrl == null) {
                        throw new IOException("Velocity 模板目录未找到: 请确认 classpath 中存在 '" + loaderPath
                                + "'，或通过属性 docx4j.velocity.file.resource.loader.path 指定有效的 classpath 模板目录");
                    }
                    ps.setProperty("file.resource.loader.path", templateUrl.getPath());
                    //模板输入输出编码格式
                    String input_charset = Docx4jProperties.getProperty("docx4j.velocity.input.encoding", Docx4jConstants.DEFAULT_CHARSETNAME);
                    String output_charset = Docx4jProperties.getProperty("docx4j.velocity.output.encoding", Docx4jConstants.DEFAULT_CHARSETNAME);
                    ps.setProperty("input.encoding", input_charset);
                    ps.setProperty("output.encoding", output_charset);
                    VelocityEngine e = new VelocityEngine();
                    e.init(ps);
                    local = e;
                    engine = local;
                }
            }
        }
        return local;
    }
}
