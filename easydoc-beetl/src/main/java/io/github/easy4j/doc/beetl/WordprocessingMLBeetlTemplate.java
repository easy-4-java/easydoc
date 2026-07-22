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
package io.github.easy4j.doc.beetl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;

/**
 * 该模板仅负责使用Beetl模板引擎将指定模板生成HTML并将HTML转换成XHTML后，作为模板生成WordprocessingMLPackage对象
 * @author <a href="https://github.com/easy-4-java">hiwepy</a>
 */
public class WordprocessingMLBeetlTemplate implements WordprocessingMLTemplate {
	
	protected GroupTemplate engine;
	protected WordprocessingMLHtmlTemplate mlHtmlTemplate;

	public WordprocessingMLBeetlTemplate() {
		this(false, false);
	}
	
	public WordprocessingMLBeetlTemplate(boolean landscape, boolean altChunk) {
		this.mlHtmlTemplate = new WordprocessingMLHtmlTemplate(landscape, altChunk) ;
	}
	
	public WordprocessingMLBeetlTemplate(WordprocessingMLHtmlTemplate template) {
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
	 * 使用Beetl模板引擎渲染模板
	 * @param template ：模板内容
	 * @param variables ：变量
	 * @return {@link WordprocessingMLPackage} 对象
	 * @throws Exception ：异常对象
	 */
	@Override
	public WordprocessingMLPackage process(String template, Map<String, Object> variables) throws Exception {
		//使用Beetl模板引擎渲染模板
		Template beeTemplate = getEngine().getTemplate(template);
		beeTemplate.binding(variables);
		//获取模板渲染后的结果
		String html = beeTemplate.render();
		//使用HtmlTemplate进行渲染
		return mlHtmlTemplate.process(html, variables);
	}

	public GroupTemplate getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(GroupTemplate engine) {
		this.engine = engine;
	}
	
	protected synchronized GroupTemplate getInternalEngine() throws IOException{
        ClasspathResourceLoader loader = new ClasspathResourceLoader();
        //加载默认参数
        Configuration cfg = Configuration.defaultConfiguration();
        //模板字符集
        cfg.setCharset(Docx4jProperties.getProperty("docx4j.beetl.charset", Docx4jConstants.DEFAULT_CHARSETNAME));
        //模板占位起始符号 
        cfg.setPlaceholderStart(Docx4jProperties.getProperty("docx4j.beetl.placeholderStart", "${"));
        //模板占位结束符号
        cfg.setPlaceholderEnd(Docx4jProperties.getProperty("docx4j.beetl.placeholderEnd", "<%"));
        //控制语句起始符号
        cfg.setStatementStart(Docx4jProperties.getProperty("docx4j.beetl.statementStart", "%>"));
        //控制语句结束符号
        cfg.setStatementEnd(Docx4jProperties.getProperty("docx4j.beetl.statementEnd", "}"));
        //是否允许html tag，在web编程中，有可能用到html tag，最好允许 
        cfg.setHtmlTagSupport(Docx4jProperties.getProperty("docx4j.beetl.htmlTagSupport", false));
        //html tag 标示符号 
        cfg.setHtmlTagFlag(Docx4jProperties.getProperty("docx4j.beetl.htmlTagFlag", "#"));
        //html 绑定的属性，如&lt;aa var="customer">
        cfg.setHtmlTagBindingAttribute(Docx4jProperties.getProperty("docx4j.beetl.htmlTagBindingAttribute", "var"));
        //是否允许直接调用class
        cfg.setNativeCall(Docx4jProperties.getProperty("docx4j.beetl.nativeCall", false));
        //输出模式，默认是字符集输出，改成byte输出提高性能 
        cfg.setDirectByteOutput(Docx4jProperties.getProperty("docx4j.beetl.directByteOutput", true));
        //严格mvc应用，只有变态的的人才打开此选项 
        cfg.setStrict(Docx4jProperties.getProperty("docx4j.beetl.strict", false));
        //是否忽略客户端的网络异常
        cfg.setIgnoreClientIOError(Docx4jProperties.getProperty("docx4j.beetl.ignoreClientIOError", true));
        //错误处理类
        cfg.setErrorHandlerClass(Docx4jProperties.getProperty("docx4j.beetl.errorHandlerClass", "org.beetl.core.ConsoleErrorHandler"));
        
        //资源参数
        Map<String,String> resourceMap = cfg.getResourceMap();
        //classpath 跟路径
        resourceMap.put("root", Docx4jProperties.getProperty("docx4j.beetl.resource.root", "/"));
        //是否检测文件变化
        resourceMap.put("autoCheck", Docx4jProperties.getProperty("docx4j.beetl.resource.autoCheck", "true"));
        //自定义脚本方法文件位置
        resourceMap.put("functionRoot", Docx4jProperties.getProperty("docx4j.beetl.resource.functionRoot", "functions"));
        //自定义脚本方法文件的后缀
        resourceMap.put("functionSuffix", Docx4jProperties.getProperty("docx4j.beetl.resource.functionSuffix", "html"));
        //自定义标签文件位置
        resourceMap.put("tagRoot", Docx4jProperties.getProperty("docx4j.beetl.resource.tagRoot", "htmltag"));
        //自定义标签文件后缀
        resourceMap.put("tagSuffix", Docx4jProperties.getProperty("docx4j.beetl.resource.tagSuffix", "tag"));
        cfg.setResourceMap(resourceMap);
        GroupTemplate engine = new GroupTemplate(loader, cfg);
        // 设置模板引擎，减少重复初始化消耗
        this.setEngine(engine);
        return engine;
	}

}
