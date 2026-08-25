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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import io.github.easy4j.doc.xhtml.AbstractStringTemplateWrappingTemplate;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 该模板仅负责使用Freemarker模板引擎将指定模板生成HTML并将HTML转换成XHTML后，作为模板生成WordprocessingMLPackage对象
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLFreemarkerTemplate extends AbstractStringTemplateWrappingTemplate {

	protected final Logger LOG = LoggerFactory.getLogger(WordprocessingMLFreemarkerTemplate.class);
	protected volatile Configuration engine;
	protected volatile EngineFactory factory;
	protected volatile Renderer renderer;

	@Deprecated
	protected Properties freemarkerSettings;
	@Deprecated
	protected Map<String, Object> freemarkerVariables;
	@Deprecated
	protected String defaultEncoding;
	@Deprecated
	protected final List<TemplateLoader> templateLoaders = new ArrayList<TemplateLoader>();
	@Deprecated
	protected List<TemplateLoader> preTemplateLoaders;
	@Deprecated
	protected List<TemplateLoader> postTemplateLoaders;
	@Deprecated
	protected TemplateModel templateModel;

	public WordprocessingMLFreemarkerTemplate() {
		super();
	}

	public WordprocessingMLFreemarkerTemplate(boolean landscape, boolean altChunk) {
		super(landscape, altChunk);
	}

	public WordprocessingMLFreemarkerTemplate(WordprocessingMLHtmlTemplate template) {
		super(template);
	}

	public Configuration getEngine() throws IOException, TemplateException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(Configuration engine) {
		this.engine = engine;
	}

	protected Configuration getInternalEngine() throws IOException, TemplateException {
		EngineFactory f = factory;
		if (f == null) {
			synchronized (this) {
				f = factory;
				if (f == null) {
					f = new EngineFactory(freemarkerSettings, freemarkerVariables, defaultEncoding, preTemplateLoaders, postTemplateLoaders);
					factory = f;
					renderer = new Renderer(null);
				}
			}
		}
		Configuration cfg = f.get();
		// Update renderer with the templateModel produced by the factory
		Renderer r = renderer;
		if (r == null || f.getTemplateModel() != null) {
			renderer = new Renderer(f.getTemplateModel());
		}
		return cfg;
	}

	/**
	 * Return a TemplateLoader based on the given TemplateLoader list.
	 * If more than one TemplateLoader has been registered, a FreeMarker
	 * MultiTemplateLoader needs to be created.
	 * @param templateLoaders the final List of TemplateLoader instances
	 * @return the aggregate TemplateLoader
	 * @deprecated use {@link EngineFactory} instead
	 */
	@Deprecated
	protected TemplateLoader getAggregateTemplateLoader(List<TemplateLoader> templateLoaders) {
		return EngineFactory.getAggregateTemplateLoader(templateLoaders);
	}

	/**
	 * To be overridden by subclasses that want to register custom
	 * TemplateLoader instances after this factory created its default
	 * template loaders.
	 * @param templateLoaders the current List of TemplateLoader instances,
	 * to be modified by a subclass
	 * @deprecated use {@link EngineFactory} instead
	 */
	@Deprecated
	protected void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
		EngineFactory.postProcessTemplateLoaders(templateLoaders);
	}

	/**
	 * Set properties that contain well-known FreeMarker keys which will be
	 * passed to FreeMarker's {@code Configuration.setSettings} method.
	 * @param settings properties
	 * @see freemarker.template.Configuration#setSettings
	 * @deprecated configure via {@link EngineFactory} instead
	 */
	@Deprecated
	public void setFreemarkerSettings(Properties settings) {
		this.freemarkerSettings = settings;
		this.factory = null;
	}

	/**
	 * Set a Map that contains well-known FreeMarker objects which will be passed
	 * to FreeMarker's {@code Configuration.setAllSharedVariables()} method.
	 * @param variables   variables
	 * @see freemarker.template.Configuration#setAllSharedVariables
	 * @deprecated configure via {@link EngineFactory} instead
	 */
	@Deprecated
	public void setFreemarkerVariables(Map<String, Object> variables) {
		this.freemarkerVariables = variables;
		this.factory = null;
	}

	/**
	 * Set the default encoding for the FreeMarker configuration.
	 * If not specified, FreeMarker will use the platform file encoding.
	 * <p>Used for template rendering unless there is an explicit encoding specified
	 * for the rendering process (for example, on Spring's FreeMarkerView).
	 * @param defaultEncoding Default Encoding
	 * @see freemarker.template.Configuration#setDefaultEncoding
	 * @deprecated configure via {@link EngineFactory} instead
	 */
	@Deprecated
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
		this.factory = null;
	}

	/**
	 * Set a List of {@code TemplateLoader}s that will be used to search
	 * for templates. For example, one or more custom loaders such as database
	 * loaders could be configured and injected here.
	 * <p>The {@link TemplateLoader TemplateLoaders} specified here will be
	 * registered <i>before</i> the default template loaders that this factory
	 * registers (such as loaders for specified "templateLoaderPaths" or any
	 * loaders registered in {@link #postProcessTemplateLoaders}).
	 * @param preTemplateLoaders the Array of TemplateLoader instances,
	 * to be modified by a subclass
	 * @see #postProcessTemplateLoaders
	 * @deprecated configure via {@link EngineFactory} instead
	 */
	@Deprecated
	public void setPreTemplateLoaders(TemplateLoader... preTemplateLoaders) {
		this.preTemplateLoaders = Arrays.asList(preTemplateLoaders);
		this.factory = null;
	}

	/**
	 * Set a List of {@code TemplateLoader}s that will be used to search
	 * for templates. For example, one or more custom loaders such as database
	 * loaders can be configured.
	 * <p>The {@link TemplateLoader TemplateLoaders} specified here will be
	 * registered <i>after</i> the default template loaders that this factory
	 * registers (such as loaders for specified "templateLoaderPaths" or any
	 * loaders registered in {@link #postProcessTemplateLoaders}).
	 * @param postTemplateLoaders the Array of TemplateLoader instances,
	 * to be modified by a subclass
	 * @see #postProcessTemplateLoaders
	 * @deprecated configure via {@link EngineFactory} instead
	 */
	@Deprecated
	public void setPostTemplateLoaders(TemplateLoader... postTemplateLoaders) {
		this.postTemplateLoaders = Arrays.asList(postTemplateLoaders);
		this.factory = null;
	}

	@Override
	protected String render(String template, Map<String, Object> variables) throws Exception {
		Renderer r = renderer;
		if (r == null) {
			r = new Renderer(templateModel);
			renderer = r;
		}
		return r.render(template, variables, getEngine());
	}
}
