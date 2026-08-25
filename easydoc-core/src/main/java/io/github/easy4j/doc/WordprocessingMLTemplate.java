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
package io.github.easy4j.doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模板处理接口
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public interface WordprocessingMLTemplate {

	Logger LOG = LoggerFactory.getLogger(WordprocessingMLTemplate.class);

	/**
	 * @param template ：模板文件
	 * @param variables ：变量
	 * @return {@link WordprocessingMLPackage} 对象
	 * @throws Exception ：异常对象
	 *
	 * <p>本项目内所有完整管线骨架（{@link AbstractWmlTemplate}、
	 * {@code AbstractStringTemplateWrappingTemplate}、
	 * {@code WordprocessingMLHtmlTemplate}、{@code WordprocessingMLJspTemplate}）
	 * 均覆盖此方法并加入变量替换等后续步骤；此处 default 仅作为最小实现的兜底
	 * （加载模板或创建空文档），供契约测试与第三方最小实现使用。</p>
	 */
	default WordprocessingMLPackage process(File template, Map<String, Object> variables) throws Exception{
		// Document loading (required)
		WordprocessingMLPackage wordMLPackage;
		if (template == null || !template.exists() || !template.isFile() ) {
			// Create a docx
			LOG.debug("No input path passed, creating dummy document");
			wordMLPackage = WordprocessingMLPackage.createPackage();
			SampleDocument.createContent(wordMLPackage.getMainDocumentPart());
		} else {
			LOG.debug("Loading file from {}", template.getAbsolutePath());
			wordMLPackage = Docx4J.load(template);
		}
		return wordMLPackage;
	}

	/**
	 * @param template ：模板文件流
	 * @param variables ：变量
	 * @return {@link WordprocessingMLPackage} 对象
	 * @throws Exception ：异常对象
	 *
	 * <p>同 {@link #process(File, Map)}：default 仅作为未覆盖者的兜底，完整管线
	 * 均覆盖此方法。</p>
	 */
	default WordprocessingMLPackage process(InputStream template, Map<String, Object> variables) throws Exception{
		// Document loading (required)
		WordprocessingMLPackage wordMLPackage;
		if (template == null) {
			// Create a docx
			LOG.debug("No input path passed, creating dummy document");
			wordMLPackage = WordprocessingMLPackage.createPackage();
			SampleDocument.createContent(wordMLPackage.getMainDocumentPart());
		} else {
			LOG.debug("Loading file from InputStream");
			wordMLPackage = Docx4J.load(template);
		}
		return wordMLPackage;
	}
	
	/**
	 * @param template ：模板内容/路径
	 * @param variables ：变量
	 * @return {@link WordprocessingMLPackage} 对象
	 * @throws Exception ：异常对象
	 *
	 * <p>最简默认实现：将字符串作为文件路径委托给
	 * {@link #process(InputStream, Map)}。{@link AbstractWmlTemplate} 唯一继承
	 * 的 default 即此方法。</p>
	 */
	default WordprocessingMLPackage process(String template, Map<String, Object> variables) throws Exception{
		return this.process(new FileInputStream(template), variables);
	}
	
	
}
