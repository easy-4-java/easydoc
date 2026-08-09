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
package io.github.easy4j.doc.jsp.engine;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.easy4j.doc.utils.PathUtils;

/**
 * 
 * TODO
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class JspEngineImpl extends JspEngine{

    // 模板对象缓存
    private final ConcurrentMap<String, JspTemplate> cache = new ConcurrentHashMap<String, JspTemplate>(128);

    private final JspConfig config;
	
	public JspEngineImpl(JspConfig config) {
		this.config = config;
	}

	@Override
	public JspTemplate getTemplate(HttpServletRequest request,HttpServletResponse response,String name) throws IOException {
		JspTemplate template = internalGetTemplate(request, response, name);
        if (template != null) {
            try {
                return template;
            } catch (Exception e) {
                cache.remove(template.getName());
            }
        }
        throw new IOException(name);
	}

    @Override
    public JspTemplate createTemplate(HttpServletRequest request,HttpServletResponse response,String name) {
        JspTemplate template = new JspTemplateImpl(this, request, response, name);
        return template;
    }

    private JspTemplate internalGetTemplate(HttpServletRequest request,HttpServletResponse response,String name) {
        // 将一个模板路径名称转为标准格式
        name = PathUtils.normalize(name);
        if (name.startsWith("../")) {
            return null;
        }

        JspTemplate template = cache.get(name);
        if (template != null) {
            return template;
        }

        // create a new template
        template = new JspTemplateImpl(this, request, response, name);
        JspTemplate old = cache.putIfAbsent(name, template);
        if (old != null) {
            template = old;
        }
        return template;
    }
	
	@Override
	public JspConfig getConfig() {
		return config;
	}

}
