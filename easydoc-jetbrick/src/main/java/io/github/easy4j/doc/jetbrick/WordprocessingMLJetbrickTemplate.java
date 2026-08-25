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
package io.github.easy4j.doc.jetbrick;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.docx4j.Docx4jProperties;
import io.github.easy4j.doc.utils.ConfigUtils;
import io.github.easy4j.doc.xhtml.AbstractStringTemplateWrappingTemplate;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetbrick.config.ConfigLoader;
import jetbrick.template.JetConfig;
import jetbrick.template.JetEngine;

/**
 * 该模板仅负责使用Jetbrick模板引擎将指定模板生成HTML并将HTML转换成XHTML后，作为模板生成WordprocessingMLPackage对象
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLJetbrickTemplate extends AbstractStringTemplateWrappingTemplate {

	protected final Logger LOG = LoggerFactory.getLogger(WordprocessingMLJetbrickTemplate.class);
	protected volatile JetEngine engine;

	public WordprocessingMLJetbrickTemplate() {
		super();
	}

	public WordprocessingMLJetbrickTemplate(boolean landscape, boolean altChunk) {
		super(landscape, altChunk);
	}

	public WordprocessingMLJetbrickTemplate(WordprocessingMLHtmlTemplate template) {
		super(template);
	}

	public JetEngine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(JetEngine engine) {
		this.engine = engine;
	}

	protected JetEngine getInternalEngine() throws IOException{
		JetEngine local = engine;
		if (local == null) {
			synchronized (this) {
				local = engine;
				if (local == null) {
					Properties ps = new Properties();
					ConfigLoader loader = new ConfigLoader();
					try {
						LOG.info("Loading config file: {}", JetConfig.DEFAULT_CONFIG_FILE);
					    loader.load(JetConfig.DEFAULT_CONFIG_FILE);
					    ps = loader.asProperties();
					} catch (Exception e) {
					     // 默认配置文件不存在
						LOG.warn("No default config file found: {}", JetConfig.DEFAULT_CONFIG_FILE);
						ps = ConfigUtils.filterWithPrefix("docx4j.jetx.", "docx4j.", Docx4jProperties.getProperties(), true);
					}
					local = JetEngine.create(ps);
					engine = local;
				}
			}
		}
		return local;
	}

	@Override
	protected String render(String template, Map<String, Object> variables) throws Exception {
		StringWriter output = new StringWriter();
		getEngine().getTemplate(template).render(variables, output);
		return output.toString();
	}
}
