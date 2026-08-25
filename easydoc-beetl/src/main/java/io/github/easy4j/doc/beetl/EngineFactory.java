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
package io.github.easy4j.doc.beetl;

import java.io.IOException;
import java.util.Map;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.docx4j.Docx4jProperties;
import io.github.easy4j.doc.Docx4jConstants;

/**
 * Immutable factory that lazily creates and caches a Beetl {@link GroupTemplate}
 * instance using double-checked locking.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class EngineFactory {

    private volatile GroupTemplate engine;

    /**
     * Returns the shared {@link GroupTemplate} instance, creating it on first access.
     */
    public GroupTemplate get() throws IOException {
        GroupTemplate local = engine;
        if (local == null) {
            synchronized (this) {
                local = engine;
                if (local == null) {
                    ClasspathResourceLoader loader = new ClasspathResourceLoader();
                    Configuration cfg = Configuration.defaultConfiguration();
                    cfg.setCharset(Docx4jProperties.getProperty("docx4j.beetl.charset", Docx4jConstants.DEFAULT_CHARSETNAME));
                    cfg.setPlaceholderStart(Docx4jProperties.getProperty("docx4j.beetl.placeholderStart", "${"));
                    cfg.setPlaceholderEnd(Docx4jProperties.getProperty("docx4j.beetl.placeholderEnd", "<%"));
                    cfg.setStatementStart(Docx4jProperties.getProperty("docx4j.beetl.statementStart", "%>"));
                    cfg.setStatementEnd(Docx4jProperties.getProperty("docx4j.beetl.statementEnd", "}"));
                    cfg.setHtmlTagSupport(Docx4jProperties.getProperty("docx4j.beetl.htmlTagSupport", false));
                    cfg.setHtmlTagFlag(Docx4jProperties.getProperty("docx4j.beetl.htmlTagFlag", "#"));
                    cfg.setHtmlTagBindingAttribute(Docx4jProperties.getProperty("docx4j.beetl.htmlTagBindingAttribute", "var"));
                    cfg.setNativeCall(Docx4jProperties.getProperty("docx4j.beetl.nativeCall", false));
                    cfg.setDirectByteOutput(Docx4jProperties.getProperty("docx4j.beetl.directByteOutput", true));
                    cfg.setStrict(Docx4jProperties.getProperty("docx4j.beetl.strict", false));
                    cfg.setIgnoreClientIOError(Docx4jProperties.getProperty("docx4j.beetl.ignoreClientIOError", true));
                    cfg.setErrorHandlerClass(Docx4jProperties.getProperty("docx4j.beetl.errorHandlerClass", "org.beetl.core.ConsoleErrorHandler"));

                    Map<String, String> resourceMap = cfg.getResourceMap();
                    resourceMap.put("root", Docx4jProperties.getProperty("docx4j.beetl.resource.root", "/"));
                    resourceMap.put("autoCheck", Docx4jProperties.getProperty("docx4j.beetl.resource.autoCheck", "true"));
                    resourceMap.put("functionRoot", Docx4jProperties.getProperty("docx4j.beetl.resource.functionRoot", "functions"));
                    resourceMap.put("functionSuffix", Docx4jProperties.getProperty("docx4j.beetl.resource.functionSuffix", "html"));
                    resourceMap.put("tagRoot", Docx4jProperties.getProperty("docx4j.beetl.resource.tagRoot", "htmltag"));
                    resourceMap.put("tagSuffix", Docx4jProperties.getProperty("docx4j.beetl.resource.tagSuffix", "tag"));
                    cfg.setResourceMap(resourceMap);
                    local = new GroupTemplate(loader, cfg);
                    engine = local;
                }
            }
        }
        return local;
    }
}
