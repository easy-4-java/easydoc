/**
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateModel;

/**
 * Implementation of wordprocessing m l freemarker template functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLFreemarkerTemplate implements WordprocessingMLTemplate {

	protected final Logger LOG = LoggerFactory.getLogger(WordprocessingMLFreemarkerTemplate.class);
	protected volatile Configuration engine;
	protected volatile EngineFactory factory;
	protected WordprocessingMLHtmlTemplate mlHtmlTemplate;

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
		this(false, false);
	}

	public WordprocessingMLFreemarkerTemplate(boolean landscape, boolean altChunk) {
		this.mlHtmlTemplate = new WordprocessingMLHtmlTemplate(landscape, altChunk);
	}

	public WordprocessingMLFreemarkerTemplate(WordprocessingMLHtmlTemplate template) {
		this.mlHtmlTemplate = template;
	}

	@Override
	public WordprocessingMLPackage process(File template, Map<String, Object> variables) throws Exception {
		return this.process(FileUtils.readFileToString(template, StandardCharsets.UTF_8), variables);
	}

	@Override
	public WordprocessingMLPackage process(InputStream template, Map<String, Object> variables) throws Exception {
		return this.process(IOUtils.toString(template, StandardCharsets.UTF_8), variables);
	}

	/**
	 * 使用Freemarker模板引擎渲染模板
	 * @param template ：模板内容
	 * @param variables ：变量
	 * @return {@link WordprocessingMLPackage} 对象
	 * @throws Exception ：异常对象
	 */
	@Override
	public WordprocessingMLPackage process(String template, Map<String, Object> variables) throws Exception {
		String html = resolveRenderer().render(template, variables, getEngine());
		return mlHtmlTemplate.process(html, variables);
	}

	public Configuration getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(Configuration engine) {
		this.engine = engine;
	}

	/**
	 * 与其他引擎模块一致：工厂在首次访问时创建并缓存，引擎由 {@link EngineFactory#get()} 缓存；
	 * Renderer 同样随工厂在其内部的双重检查锁中一次性构建，调用方无需（也不应）重建。
	 */
	protected Configuration getInternalEngine() throws IOException {
		return getOrCreateFactory().get();
	}

	/**
	 * 返回与当前 {@link EngineFactory} 绑定的共享 Renderer 实例：
	 * 先确保工厂完成初始化，再取其在锁内一次性创建的 Renderer，多次调用返回同一实例。
	 */
	protected Renderer resolveRenderer() throws IOException {
		EngineFactory f = getOrCreateFactory();
		f.get(); // 确保 DCL 完成：engine / templateModel / renderer 在同一把锁内一起创建
		return f.getRenderer();
	}

	private EngineFactory getOrCreateFactory() {
		EngineFactory f = factory;
		if (f == null) {
			synchronized (this) {
				f = factory;
				if (f == null) {
					f = new EngineFactory(freemarkerSettings, freemarkerVariables, defaultEncoding, preTemplateLoaders, postTemplateLoaders);
					factory = f;
				}
			}
		}
		return f;
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
		resetEngineState();
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
		resetEngineState();
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
		resetEngineState();
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
		resetEngineState();
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
		resetEngineState();
	}

	/**
	 * 作废工厂缓存：废弃 setter 修改配置后调用。EngineFactory 内部同时缓存了 Configuration、
	 * TemplateModel 与 Renderer，因此置空 factory 即一次性作废全部陈旧实例，下次访问按新配置重建。
	 * 注意：用户通过 {@link #setEngine(Configuration)} 显式注入的 engine 不受影响。
	 */
	private void resetEngineState() {
		this.factory = null;
	}
}
