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
import java.io.StringWriter;
import java.util.Map;

import org.docx4j.Docx4jProperties;
import io.github.easy4j.doc.utils.ArrayUtils;
import io.github.easy4j.doc.utils.StringUtils;
import io.github.easy4j.doc.xhtml.AbstractStringTemplateWrappingTemplate;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

/**
 * 该模板仅负责使用Thymeleaf模板引擎将指定模板生成HTML并将HTML转换成XHTML后，作为模板生成WordprocessingMLPackage对象
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLThymeleafTemplate extends AbstractStringTemplateWrappingTemplate {

	protected volatile TemplateEngine engine;
	protected AbstractConfigurableTemplateResolver templateResolver;

	public WordprocessingMLThymeleafTemplate() {
		super();
	}

	public WordprocessingMLThymeleafTemplate(boolean landscape, boolean altChunk) {
		super(landscape, altChunk);
	}

	public WordprocessingMLThymeleafTemplate(WordprocessingMLHtmlTemplate template) {
		super(template);
	}

	public TemplateEngine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(TemplateEngine engine) {
		this.engine = engine;
	}

	protected TemplateEngine getInternalEngine() throws IOException{
		TemplateEngine local = engine;
		if (local == null) {
			synchronized (this) {
				local = engine;
				if (local == null) {
					//初始化模板解析器
					AbstractConfigurableTemplateResolver templateResolver =  getTemplateResolver();
					if( getTemplateResolver() == null){
						String resolver = Docx4jProperties.getProperty("docx4j.thymeleaf.templateResolver","org.thymeleaf.templateresolver.FileTemplateResolver");
						// JDK 21 switch expression: 4-arm resolver selector becomes one expression.
						// Unknown values fall back to FileTemplateResolver, matching original else-branch.
						templateResolver = switch (resolver) {
							case "org.thymeleaf.templateresolver.ClassLoaderTemplateResolver" -> new ClassLoaderTemplateResolver();
							case "org.thymeleaf.templateresolver.UrlTemplateResolver" -> new UrlTemplateResolver();
							case String s when s.equalsIgnoreCase("org.thymeleaf.templateresolver.FileTemplateResolver") -> new FileTemplateResolver();
							default -> new FileTemplateResolver();
						};
					}
					templateResolver.setCacheable(Docx4jProperties.getProperty("docx4j.thymeleaf.cacheable", true));
					templateResolver.setCacheablePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.cacheablePatterns", ""))));
					String cacheTTLMs = Docx4jProperties.getProperty("docx4j.thymeleaf.cacheTTLMs");
					templateResolver.setCacheTTLMs( (cacheTTLMs == null || cacheTTLMs.trim().isEmpty()) ? null : Long.valueOf(cacheTTLMs));
					templateResolver.setCharacterEncoding(Docx4jProperties.getProperty("docx4j.thymeleaf.charset","UTF-8"));
					templateResolver.setCheckExistence(Docx4jProperties.getProperty("docx4j.thymeleaf.checkExistence", false ));
					templateResolver.setCSSTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newCSSTemplateModePatterns", ""))));
					templateResolver.setHtmlTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newHtmlTemplateModePatterns", ""))));
					templateResolver.setJavaScriptTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newJavaScriptTemplateModePatterns", ""))));
					templateResolver.setName(Docx4jProperties.getProperty("docx4j.thymeleaf.name",templateResolver.getClass().getName()));
					templateResolver.setNonCacheablePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.nonCacheablePatterns", ""))));
					templateResolver.setOrder(Integer.valueOf(Docx4jProperties.getProperty("docx4j.thymeleaf.order","1")));
					templateResolver.setPrefix(Docx4jProperties.getProperty("docx4j.thymeleaf.prefix"));
					templateResolver.setRawTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newRawTemplateModePatterns", ""))));
					templateResolver.setResolvablePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.resolvablePatterns", ""))));
					templateResolver.setSuffix(Docx4jProperties.getProperty("docx4j.thymeleaf.suffix",".tpl"));
					templateResolver.setTemplateMode(Docx4jProperties.getProperty("docx4j.thymeleaf.templateMode","XHTML"));
					templateResolver.setTextTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newTextTemplateModePatterns", ""))));
					templateResolver.setUseDecoupledLogic(Docx4jProperties.getProperty("docx4j.thymeleaf.useDecoupledLogic", false ));
					templateResolver.setXmlTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newXmlTemplateModePatterns", ""))));
			        //初始化引擎对象
					TemplateEngine e = new TemplateEngine();
					e.setTemplateResolver(templateResolver);
			        //调用getConfiguration初始化引擎
					e.getConfiguration();
					local = e;
					engine = local;
				}
			}
		}
		return local;
	}

	public AbstractConfigurableTemplateResolver getTemplateResolver() {
		return templateResolver;
	}

	public void setTemplateResolver(AbstractConfigurableTemplateResolver templateResolver) {
		this.templateResolver = templateResolver;
	}

	@Override
	protected String render(String template, Map<String, Object> variables) throws Exception {
		StringWriter output = new StringWriter();
		Context ctx = new Context();
        ctx.setVariables(variables);
		getEngine().process(template , ctx , output);
		return output.toString();
	}
}
