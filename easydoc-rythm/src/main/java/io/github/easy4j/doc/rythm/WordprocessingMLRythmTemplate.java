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
package io.github.easy4j.doc.rythm;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

import org.docx4j.Docx4jProperties;
import io.github.easy4j.doc.utils.ConfigUtils;
import io.github.easy4j.doc.xhtml.AbstractStringTemplateWrappingTemplate;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;

/**
 * 该模板仅负责使用Rythm模板引擎将指定模板生成HTML并将HTML转换成XHTML后，作为模板生成WordprocessingMLPackage对象
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLRythmTemplate extends AbstractStringTemplateWrappingTemplate {

	protected volatile RythmEngine engine;

	public WordprocessingMLRythmTemplate() {
		super();
	}

	public WordprocessingMLRythmTemplate(boolean landscape, boolean altChunk) {
		super(landscape, altChunk);
	}

	public WordprocessingMLRythmTemplate(WordprocessingMLHtmlTemplate template) {
		super(template);
	}

	public RythmEngine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(RythmEngine engine) {
		this.engine = engine;
	}

	protected RythmEngine getInternalEngine() throws IOException{
		RythmEngine local = engine;
		if (local == null) {
			synchronized (this) {
				local = engine;
				if (local == null) {
					Properties props =  ConfigUtils.filterWithPrefix("docx4j.rythm.", "docx4j.rythm.", Docx4jProperties.getProperties(), false);

					props.put("engine.mode", Rythm.Mode.valueOf(props.getProperty("engine.mode", "dev")));
					props.put("log.enabled", false);
					props.put("feature.smart_escape.enabled", false);
					props.put("feature.transform.enabled", false);
					try {
						props.put("home.template", Rythm.class.getResource(props.getProperty("home.template")).toURI().toURL().getFile());
					} catch (URISyntaxException e) {
						// ignore
						props.put("home.tmp", "/");
					}
					props.put("codegen.dynamic_exp.enabled", true);
					props.put("built_in.code_type", "false");
					props.put("built_in.transformer", "false");
					props.put("engine.file_write", "false");
					props.put("codegen.compact.enabled", "false");
					local = new RythmEngine(props);
					engine = local;
				}
			}
		}
		return local;
	}

	@Override
	protected String render(String template, Map<String, Object> variables) throws Exception {
		StringWriter output = new StringWriter();
		getEngine().getTemplate(template , variables).render(output);
		return output.toString();
	}
}
