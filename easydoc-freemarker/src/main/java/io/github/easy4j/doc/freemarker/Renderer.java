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
package io.github.easy4j.doc.freemarker;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.TemplateModel;

/**
 * Stateless renderer that delegates to the Freemarker {@link Configuration} to
 * produce output from a named template and a set of variables.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class Renderer {

    private final TemplateModel templateModel;

    /**
     * Creates a new Renderer with the given String static methods model.
     *
     * @param templateModel the TemplateModel for String static methods (nullable)
     */
    public Renderer(TemplateModel templateModel) {
        this.templateModel = templateModel;
    }

    /**
     * Renders the given template using the supplied engine and variables.
     *
     * @param template  the template name / path
     * @param variables the template variables
     * @param engine    the Freemarker engine to use
     * @return the rendered output
     */
    public String render(String template, Map<String, Object> variables, Configuration engine) throws Exception {
        // 防御性拷贝：不再向调用方的 Map 写入 "String" 键（原实现污染调用方数据）
        Map<String, Object> renderVars = new HashMap<String, Object>(variables);
        if (templateModel != null) {
            renderVars.put("String", templateModel);
        }
        StringWriter output = new StringWriter();
        engine.getTemplate(template).process(renderVars, output);
        return output.toString();
    }
}
