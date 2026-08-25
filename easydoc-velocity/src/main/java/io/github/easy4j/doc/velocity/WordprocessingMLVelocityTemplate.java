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
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.docx4j.Docx4jProperties;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.xhtml.AbstractStringTemplateWrappingTemplate;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;

/**
 * 该模板仅负责使用Velocity模板引擎将指定模板生成HTML并将HTML转换成XHTML后，作为模板生成WordprocessingMLPackage对象
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLVelocityTemplate extends AbstractStringTemplateWrappingTemplate {

	protected volatile VelocityEngine engine;
	protected DateTool dateTool = new DateTool();

	public WordprocessingMLVelocityTemplate() {
		super();
	}

	public WordprocessingMLVelocityTemplate(boolean landscape, boolean altChunk) {
		super(landscape, altChunk);
	}

	public WordprocessingMLVelocityTemplate(WordprocessingMLHtmlTemplate template) {
		super(template);
	}

	public VelocityEngine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(VelocityEngine engine) {
		this.engine = engine;
	}

	protected VelocityEngine getInternalEngine() throws IOException{
		VelocityEngine local = engine;
		if (local == null) {
			synchronized (this) {
				local = engine;
				if (local == null) {
					VelocityEngine e = new VelocityEngine();

					Properties ps = new Properties();
			        ps.setProperty(";runtime.log", Docx4jProperties.getProperty("docx4j.velocity.runtime.log", "velocity.log"));
			        ps.setProperty(";runtime.log.logsystem.class", Docx4jProperties.getProperty("docx4j.velocity.runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem"));
			        ps.setProperty("resource.loader", Docx4jProperties.getProperty("docx4j.velocity.resource.loader", "file"));
			        ps.setProperty("file.resource.loader.cache", Docx4jProperties.getProperty("docx4j.velocity.file.resource.loader.cache", "true"));
			        ps.setProperty("file.resource.loader.class ", Docx4jProperties.getProperty("docx4j.velocity.file.resource.loader.class", "Velocity.Runtime.Resource.Loader.FileResourceLoader") );
			        ps.setProperty(";resource.loader", Docx4jProperties.getProperty("docx4j.velocity.resource.loader", "webapp"));
			        ps.setProperty(";webapp.resource.loader.class", Docx4jProperties.getProperty("docx4j.velocity.webapp.resource.loader.class", "org.apache.velocity.tools.view.servlet.WebappLoader"));
			        ps.setProperty(";webapp.resource.loader.cache", Docx4jProperties.getProperty("docx4j.velocity.webapp.resource.loader.cache", "true"));
			        ps.setProperty(";webapp.resource.loader.modificationCheckInterval", Docx4jProperties.getProperty("docx4j.velocity.webapp.resource.loader.modificationCheckInterval", "3") );
			        ps.setProperty(";directive.foreach.counter.name", Docx4jProperties.getProperty("docx4j.velocity.directive.foreach.counter.name", "velocityCount"));
			        ps.setProperty(";directive.foreach.counter.initial.value", Docx4jProperties.getProperty("docx4j.velocity.directive.foreach.counter.initial.value", "1"));
			        ps.setProperty("file.resource.loader.path", this.getClass().getResource(Docx4jProperties.getProperty("docx4j.velocity.file.resource.loader.path", "/template")).getPath());
			        //模板输入输出编码格式
			        String input_charset = Docx4jProperties.getProperty("docx4j.velocity.input.encoding", Docx4jConstants.DEFAULT_CHARSETNAME);
			        String output_charset = Docx4jProperties.getProperty("docx4j.velocity.output.encoding", Docx4jConstants.DEFAULT_CHARSETNAME );
			        ps.setProperty("input.encoding", input_charset);
			        ps.setProperty("output.encoding", output_charset);
			        e.init(ps);
					local = e;
					engine = local;
				}
			}
		}
		return local;
	}

	@Override
	protected String render(String template, Map<String, Object> variables) throws Exception {
		VelocityContext ctx = new VelocityContext(variables);
		ctx.put("dateTool", dateTool);
		StringWriter output = new StringWriter();
		getEngine().getTemplate(template).merge(ctx, output);
		return output.toString();
	}
}
