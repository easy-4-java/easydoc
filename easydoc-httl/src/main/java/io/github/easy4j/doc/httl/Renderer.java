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

import java.io.StringWriter;
import java.util.Map;

import httl.Engine;

/**
 * Stateless renderer that delegates to the HTTL {@link Engine} to produce
 * output from a named template and a set of variables.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class Renderer {

    /**
     * Renders the given template using the supplied engine and variables.
     *
     * @param template  the template name / path
     * @param variables the template variables
     * @param engine    the HTTL engine to use
     * @return the rendered output
     */
    public String render(String template, Map<String, Object> variables, Engine engine) throws Exception {
        StringWriter output = new StringWriter();
        engine.getTemplate(template).render(variables, output);
        return output.toString();
    }
}
