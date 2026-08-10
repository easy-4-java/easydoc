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
package io.github.easy4j.doc.thymeleaf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import io.github.easy4j.doc.utils.ArrayUtils;
import io.github.easy4j.doc.utils.StringUtils;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

/**
 * Implementation of wordprocessing m l thymeleaf template functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLThymeleafTemplate implements WordprocessingMLTemplate {
	
	protected TemplateEngine engine;
	protected AbstractConfigurableTemplateResolver templateResolver;
	protected WordprocessingMLHtmlTemplate mlHtmlTemplate;

	public WordprocessingMLThymeleafTemplate() {
		this(false, false);
	}
	
	public WordprocessingMLThymeleafTemplate(boolean landscape, boolean altChunk) {
		this.mlHtmlTemplate = new WordprocessingMLHtmlTemplate(landscape, altChunk) ;
	}
	
	public WordprocessingMLThymeleafTemplate(WordprocessingMLHtmlTemplate template) {
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
 * Implementation of wordprocessing m l thymeleaf template functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
	@Override
	public WordprocessingMLPackage process(String template, Map<String, Object> variables) throws Exception {
		// 创建模板输出内容接收对象
		StringWriter output = new StringWriter();
		//设置上下文参数
		Context ctx = new Context();
        ctx.setVariables(variables);
		// 使用Thymeleaf模板引擎渲染模板
		getEngine().process(template , ctx , output);
		//获取模板渲染后的结果
		String html = output.toString();
		//使用HtmlTemplate进行渲染
		return mlHtmlTemplate.process(html, variables);
	}
	
	public TemplateEngine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(TemplateEngine engine) {
		this.engine = engine;
	}
	
	protected synchronized TemplateEngine getInternalEngine() throws IOException{
		//初始化模板解析器
		AbstractConfigurableTemplateResolver templateResolver =  getTemplateResolver();
		if( getTemplateResolver() == null){
			String resolver = Docx4jProperties.getProperty("docx4j.thymeleaf.templateResolver","org.thymeleaf.templateresolver.FileTemplateResolver");
			if("org.thymeleaf.templateresolver.FileTemplateResolver".equalsIgnoreCase(resolver)){
				templateResolver = new FileTemplateResolver();
			}else if("org.thymeleaf.templateresolver.ClassLoaderTemplateResolver".equalsIgnoreCase(resolver)){
				templateResolver = new ClassLoaderTemplateResolver();
			}else if("org.thymeleaf.templateresolver.UrlTemplateResolver".equalsIgnoreCase(resolver)){
				templateResolver = new UrlTemplateResolver();
			}else{
				templateResolver = new FileTemplateResolver();
			}
		}
		templateResolver.setCacheable(Docx4jProperties.getProperty("docx4j.thymeleaf.cacheable", true));
		templateResolver.setCacheablePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.cacheablePatterns", ""))));
		String cacheTTLMs = Docx4jProperties.getProperty("docx4j.thymeleaf.cacheTTLMs");
		templateResolver.setCacheTTLMs( cacheTTLMs == null ? null : Long.valueOf(cacheTTLMs)); 
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
		//templateResolver.setTemplateAliases(templateAliases);
		templateResolver.setTemplateMode(Docx4jProperties.getProperty("docx4j.thymeleaf.templateMode","XHTML"));
		templateResolver.setTextTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newTextTemplateModePatterns", ""))));
		templateResolver.setUseDecoupledLogic(Docx4jProperties.getProperty("docx4j.thymeleaf.useDecoupledLogic", false ));
		templateResolver.setXmlTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newXmlTemplateModePatterns", ""))));
        //初始化引擎对象
		TemplateEngine engine = new TemplateEngine();
		engine.setTemplateResolver(templateResolver);
        //调用getConfiguration初始化引擎
		engine.getConfiguration();
		// 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}

	public AbstractConfigurableTemplateResolver getTemplateResolver() {
		return templateResolver;
	}

	public void setTemplateResolver(AbstractConfigurableTemplateResolver templateResolver) {
		this.templateResolver = templateResolver;
	}
	
}
