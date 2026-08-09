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
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletException;

/**
 * 
 * TODO
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public interface JspTemplate {

	public String getName();
	
	public void render(String requestURL,Map<String, Object> variables, Writer out) throws IOException, ServletException;
	
	public void render(String requestURL,Map<String, Object> variables, OutputStream out) throws IOException, ServletException;
	
}
