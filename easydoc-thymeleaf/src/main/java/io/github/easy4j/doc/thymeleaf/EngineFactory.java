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
package io.github.easy4j.doc.thymeleaf;

import java.io.IOException;

import org.docx4j.Docx4jProperties;
import io.github.easy4j.doc.utils.ArrayUtils;
import io.github.easy4j.doc.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

/**
 * Immutable factory that lazily creates and caches a Thymeleaf {@link TemplateEngine}
 * instance using double-checked locking.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class EngineFactory {

    private static final Logger LOG = LoggerFactory.getLogger(EngineFactory.class);

    private final AbstractConfigurableTemplateResolver templateResolver;

    private volatile TemplateEngine engine;

    /**
     * Creates a new EngineFactory with the given template resolver.
     *
     * @param templateResolver the template resolver to use (nullable; if null,
     *                         a resolver is created from Docx4jProperties)
     */
    public EngineFactory(AbstractConfigurableTemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    /**
     * Returns the shared {@link TemplateEngine} instance, creating it on first access.
     */
    public TemplateEngine get() throws IOException {
        TemplateEngine local = engine;
        if (local == null) {
            synchronized (this) {
                local = engine;
                if (local == null) {
                    AbstractConfigurableTemplateResolver resolver = this.templateResolver;
                    if (resolver == null) {
                        String resolverClassName = Docx4jProperties.getProperty("docx4j.thymeleaf.templateResolver", "org.thymeleaf.templateresolver.FileTemplateResolver");
                        //
                        // 设计取舍（低风险方案）：这里不做反射实例化自定义解析器——未知的类名只会降级为
                        // FileTemplateResolver 并记录 WARN 日志，而不是静默变换类型或抛出异常。
                        if ("org.thymeleaf.templateresolver.FileTemplateResolver".equalsIgnoreCase(resolverClassName)) {
                            resolver = new FileTemplateResolver();
                        } else if ("org.thymeleaf.templateresolver.ClassLoaderTemplateResolver".equalsIgnoreCase(resolverClassName)) {
                            resolver = new ClassLoaderTemplateResolver();
                        } else if ("org.thymeleaf.templateresolver.UrlTemplateResolver".equalsIgnoreCase(resolverClassName)) {
                            resolver = new UrlTemplateResolver();
                        } else {
                            LOG.warn("Unknown templateResolver class '{}'; falling back to FileTemplateResolver", resolverClassName);
                            resolver = new FileTemplateResolver();
                        }
                    }
                    resolver.setCacheable(Docx4jProperties.getProperty("docx4j.thymeleaf.cacheable", true));
                    resolver.setCacheablePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.cacheablePatterns", ""))));
                    String cacheTTLMs = Docx4jProperties.getProperty("docx4j.thymeleaf.cacheTTLMs");
                    resolver.setCacheTTLMs((cacheTTLMs == null || cacheTTLMs.trim().isEmpty()) ? null : Long.valueOf(cacheTTLMs));
                    resolver.setCharacterEncoding(Docx4jProperties.getProperty("docx4j.thymeleaf.charset", "UTF-8"));
                    resolver.setCheckExistence(Docx4jProperties.getProperty("docx4j.thymeleaf.checkExistence", false));
                    resolver.setCSSTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newCSSTemplateModePatterns", ""))));
                    resolver.setHtmlTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newHtmlTemplateModePatterns", ""))));
                    resolver.setJavaScriptTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newJavaScriptTemplateModePatterns", ""))));
                    resolver.setName(Docx4jProperties.getProperty("docx4j.thymeleaf.name", resolver.getClass().getName()));
                    resolver.setNonCacheablePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.nonCacheablePatterns", ""))));
                    resolver.setOrder(Integer.valueOf(Docx4jProperties.getProperty("docx4j.thymeleaf.order", "1")));
                    resolver.setPrefix(Docx4jProperties.getProperty("docx4j.thymeleaf.prefix"));
                    resolver.setRawTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newRawTemplateModePatterns", ""))));
                    resolver.setResolvablePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.resolvablePatterns", ""))));
                    resolver.setSuffix(Docx4jProperties.getProperty("docx4j.thymeleaf.suffix", ".tpl"));
                    resolver.setTemplateMode(Docx4jProperties.getProperty("docx4j.thymeleaf.templateMode", "XHTML"));
                    resolver.setTextTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newTextTemplateModePatterns", ""))));
                    resolver.setUseDecoupledLogic(Docx4jProperties.getProperty("docx4j.thymeleaf.useDecoupledLogic", false));
                    resolver.setXmlTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newXmlTemplateModePatterns", ""))));
                    TemplateEngine e = new TemplateEngine();
                    e.setTemplateResolver(resolver);
                    e.getConfiguration();
                    local = e;
                    engine = local;
                }
            }
        }
        return local;
    }
}
