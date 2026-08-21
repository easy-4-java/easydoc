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
import java.io.InputStream;
import java.util.Map;

import org.docx4j.Docx4J;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import io.github.easy4j.doc.fonts.FontMapperHolder;
import io.github.easy4j.doc.handler.VariableReplaceSaTXHandler;
import io.github.easy4j.doc.utils.WMLPackageUtils;

/**
 * 该模板负责对WordprocessingMLPackage进行普通变量替换和复杂变量替换并返回处理后的WordprocessingMLPackage对象
 * 备注：该工具只能解决固定模板的word生成（来自：https://blog.csdn.net/qq_35598240/article/details/84439929）
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLDocxStAXTemplate implements WordprocessingMLTemplate {
	
	/**
	 * 变量占位符开始位，默认：${
	 */
	protected String placeholderStart = "${";
	/**
	 * 变量占位符结束位，默认：}
	 */
	protected String placeholderEnd = "}";
	
	/**
	 * @param template ：模板文件
	 * @param variables ：变量
	 * @return {@link WordprocessingMLPackage} 对象
	 * @throws Exception ：异常对象
	 */
	@Override
	public WordprocessingMLPackage process(File template, Map<String, Object> variables) throws Exception{
		// Document loading (required)
		WordprocessingMLPackage wordMLPackage;
		if (template == null || !template.exists() || !template.isFile() ) {
			// Create a docx
			LOG.debug("No imput path passed, creating dummy document");
			wordMLPackage = WordprocessingMLPackage.createPackage();
			SampleDocument.createContent(wordMLPackage.getMainDocumentPart());	
		} else {
			LOG.debug("Loading file from " + template.getAbsolutePath());
			wordMLPackage = Docx4J.load(template);
		}
		if (null != variables && !variables.isEmpty()) {
        	// 替换变量并输出Word文档 
        	MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();  
        	// 将${}里的内容结构层次替换为一层
        	VariablePrepare.prepare(wordMLPackage);
        	WMLPackageUtils.cleanDocumentPart(documentPart);
        	// 替换变量
        	documentPart.pipe( new VariableReplaceSaTXHandler( this.getPlaceholderStart() , this.getPlaceholderEnd(), variables) );
         }
        // 返回WordprocessingMLPackage对象
		return FontMapperHolder.useFontMapper(wordMLPackage);
	}
	
	/**
	 * 变量替换方式实现（只能解决固定模板的word生成）
	 * @param template ：模板内容
	 * @param variables ：变量
	 * @return {@link WordprocessingMLPackage} 对象
	 * @throws Exception ：异常对象
	 */
	@Override
	public WordprocessingMLPackage process(InputStream template, Map<String, Object> variables) throws Exception {
		// Document loading (required)
		WordprocessingMLPackage wordMLPackage;
		if (template == null) {
			// Create a docx
			LOG.debug("No imput path passed, creating dummy document");
			wordMLPackage = WordprocessingMLPackage.createPackage();
			SampleDocument.createContent(wordMLPackage.getMainDocumentPart());	
		} else {
			LOG.debug("Loading file from InputStream");
			wordMLPackage = Docx4J.load(template);
		}
        if (null != variables && !variables.isEmpty()) {
        	// 替换变量并输出Word文档 
        	MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
        	// 将${}里的内容结构层次替换为一层
        	VariablePrepare.prepare(wordMLPackage);
        	WMLPackageUtils.cleanDocumentPart(documentPart);
        	// 替换变量
        	documentPart.pipe( new VariableReplaceSaTXHandler( this.getPlaceholderStart() , this.getPlaceholderEnd(), variables) );
         }
        // 返回WordprocessingMLPackage对象
		return FontMapperHolder.useFontMapper(wordMLPackage);
	}
	
	public String getPlaceholderStart() {
		return placeholderStart;
	}

	public void setPlaceholderStart(String placeholderStart) {
		this.placeholderStart = placeholderStart;
	}

	public String getPlaceholderEnd() {
		return placeholderEnd;
	}

	public void setPlaceholderEnd(String placeholderEnd) {
		this.placeholderEnd = placeholderEnd;
	}
	
}
