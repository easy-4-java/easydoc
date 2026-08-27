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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.docx4j.Docx4jProperties;
import io.github.easy4j.doc.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.HtmlEscape;
import freemarker.template.utility.XmlEscape;

/**
 * Immutable factory that lazily creates and caches a Freemarker {@link Configuration}
 * instance using double-checked locking. The shared {@link Renderer} bound to the
 * resolved {@link TemplateModel} is created exactly once inside the same lock,
 * so repeated {@link #get()} / {@link #getRenderer()} calls never churn instances.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class EngineFactory {

    private static final Logger LOG = LoggerFactory.getLogger(EngineFactory.class);

    private final Properties freemarkerSettings;
    private final Map<String, Object> freemarkerVariables;
    private final String defaultEncoding;
    private final List<TemplateLoader> preTemplateLoaders;
    private final List<TemplateLoader> postTemplateLoaders;

    private volatile Configuration engine;
    private volatile TemplateModel templateModel;
    private volatile Renderer renderer;

    /**
     * Creates a new EngineFactory with the given configuration.
     *
     * @param freemarkerSettings   FreeMarker settings (nullable)
     * @param freemarkerVariables  FreeMarker shared variables (nullable)
     * @param defaultEncoding      default encoding (nullable)
     * @param preTemplateLoaders   pre template loaders (nullable)
     * @param postTemplateLoaders  post template loaders (nullable)
     */
    public EngineFactory(Properties freemarkerSettings, Map<String, Object> freemarkerVariables,
                         String defaultEncoding, List<TemplateLoader> preTemplateLoaders,
                         List<TemplateLoader> postTemplateLoaders) {
        this.freemarkerSettings = freemarkerSettings;
        this.freemarkerVariables = freemarkerVariables;
        this.defaultEncoding = defaultEncoding;
        this.preTemplateLoaders = preTemplateLoaders;
        this.postTemplateLoaders = postTemplateLoaders;
    }

    /**
     * Returns the shared {@link Configuration} instance, creating it on first access.
     * FreeMarker 的受检异常（{@link TemplateException} 等）统一包装为 {@link IOException}，
     * 使模板类层面的异常面与其他引擎模块保持一致（仅声明 IOException）。
     */
    public Configuration get() throws IOException {
        Configuration local = engine;
        if (local == null) {
            synchronized (this) {
                local = engine;
                if (local == null) {

                    TemplateModel stringStaticModel;
                    try {
                        BeansWrapper beansWrapper = new BeansWrapper(Configuration.VERSION_2_3_23);
                        stringStaticModel = beansWrapper.getStaticModels().get(String.class.getName());
                    } catch (TemplateModelException e) {
                        throw new IOException(e.getMessage(), e.getCause());
                    }

                    // 创建 Configuration 实例
                    Configuration config = new Configuration(Configuration.VERSION_2_3_23);

                    Properties props = ConfigUtils.filterWithPrefix("docx4j.freemarker.", "docx4j.freemarker.", Docx4jProperties.getProperties(), false);

                    // FreeMarker will only accept known keys in its setSettings and
                    // setAllSharedVariables methods.
                    try {
                        if (!props.isEmpty()) {
                            config.setSettings(props);
                        }

                        if (this.freemarkerVariables != null && !this.freemarkerVariables.isEmpty()) {
                            config.setAllSharedVariables(new SimpleHash(this.freemarkerVariables, config.getObjectWrapper()));
                        }
                    } catch (TemplateException e) {
                        throw new IOException("Failed to apply FreeMarker settings/shared variables: " + e.getMessage(), e);
                    }

                    if (this.defaultEncoding != null) {
                        config.setDefaultEncoding(this.defaultEncoding);
                    }

                    List<TemplateLoader> templateLoaders = new LinkedList<TemplateLoader>();

                    // Register template loaders that are supposed to kick in early.
                    if (this.preTemplateLoaders != null) {
                        templateLoaders.addAll(this.preTemplateLoaders);
                    }

                    postProcessTemplateLoaders(templateLoaders);

                    // Register template loaders that are supposed to kick in late.
                    if (this.postTemplateLoaders != null) {
                        templateLoaders.addAll(this.postTemplateLoaders);
                    }

                    TemplateLoader loader = getAggregateTemplateLoader(templateLoaders);
                    if (loader != null) {
                        config.setTemplateLoader(loader);
                    }
                    config.setSharedVariable("fmXmlEscape", new XmlEscape());
                    config.setSharedVariable("fmHtmlEscape", new HtmlEscape());

                    // 引擎构建成功后一次性发布共享状态（处于同一把锁内，不会产生部分初始化）
                    this.templateModel = stringStaticModel;
                    this.renderer = new Renderer(stringStaticModel);

                    local = config;
                    engine = local;
                }
            }
        }
        return local;
    }

    /**
     * Returns the shared {@link Renderer} bound to the resolved {@link TemplateModel}.
     * Available after {@link #get()} has been called at least once (callers should
     * invoke {@link #get()} first to guarantee initialization).
     */
    public Renderer getRenderer() {
        return renderer;
    }

    /**
     * Returns the {@link TemplateModel} for String static methods, available
     * after {@link #get()} has been called at least once.
     */
    public TemplateModel getTemplateModel() {
        return templateModel;
    }

    /**
     * Return a TemplateLoader based on the given TemplateLoader list.
     * If more than one TemplateLoader has been registered, a FreeMarker
     * MultiTemplateLoader needs to be created.
     * @param templateLoaders the final List of TemplateLoader instances
     * @return the aggregate TemplateLoader
     */
    static TemplateLoader getAggregateTemplateLoader(List<TemplateLoader> templateLoaders) {
        int loaderCount = templateLoaders.size();
        switch (loaderCount) {
            case 0:
                LOG.info("No FreeMarker TemplateLoaders specified");
                return null;
            case 1:
                return templateLoaders.get(0);
            default:
                TemplateLoader[] loaders = templateLoaders.toArray(new TemplateLoader[loaderCount]);
                return new MultiTemplateLoader(loaders);
        }
    }

    /**
     * To be overridden by subclasses that want to register custom
     * TemplateLoader instances after this factory created its default
     * template loaders.
     * @param templateLoaders the current List of TemplateLoader instances,
     * to be modified by a subclass
     */
    static void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
        templateLoaders.add(new ClassTemplateLoader(WordprocessingMLFreemarkerTemplate.class, ""));
        LOG.info("ClassTemplateLoader for WordprocessingMLFreemarkerTemplate added to FreeMarker configuration");
    }
}
