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
import io.github.easy4j.doc.handler.VariableReplaceSAXHandler;
import io.github.easy4j.doc.utils.WMLPackageUtils;

/**
 * 该模板负责对WordprocessingMLPackage进行普通变量替换和复杂变量替换并返回处理后的WordprocessingMLPackage对象
 * 备注：该工具只能解决固定模板的word生成（来自：https://blog.csdn.net/qq_35598240/article/details/84439929）
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLDocxSaxTemplate implements WordprocessingMLTemplate {
	
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
		// JDK 21+：docx4j 的 SAXHandler 在该环境下不可用（见 checkJdk21OrFallback），
		// 在加载文档前就透明降级到 StAX 模板，避免同一文档被加载两次
		if (null != variables && !variables.isEmpty()) {
			checkJdk21OrFallback();
			if (jdk21FallbackTriggered) {
				return fallback().process(template, variables);
			}
		}
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
        	documentPart.pipe( new VariableReplaceSAXHandler( this.getPlaceholderStart() , this.getPlaceholderEnd(), variables) );
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
		// JDK 21+：docx4j 的 SAXHandler 在该环境下不可用（见 checkJdk21OrFallback），
		// 在加载文档前就透明降级到 StAX 模板，避免同一文档被加载两次
		if (null != variables && !variables.isEmpty()) {
			checkJdk21OrFallback();
			if (jdk21FallbackTriggered) {
				return fallback().process(template, variables);
			}
		}
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
        	documentPart.pipe( new VariableReplaceSAXHandler( this.getPlaceholderStart() , this.getPlaceholderEnd(), variables) );
         }
        // 返回WordprocessingMLPackage对象
		return FontMapperHolder.useFontMapper(wordMLPackage);
	}

	/**
	 * docx4j（含 17.0.3，其 SAXHandler 与 11.5.3 相比没有变化）的
	 * {@code SAXHandler} 在 JDK 21+ 下无法工作：Transformer（无论 JDK 内置
	 * XSLTC 还是 docx4j 的 Xalan interpretive）都不会通过 SAXSource 的
	 * XMLReader 触发 setContentHandler 回调，抛出 "Transformer didn't set
	 * ContentHandler"。与其让用户在最深处看到 cryptic 错误，首次使用时
	 * 记录一次 WARN 并透明降级到 StAX 模板。
	 */
	// volatile + synchronized single-flight init: process(...) may be invoked
	// concurrently from many virtual threads; the fallback must be published
	// safely (visibility) and created exactly once (no duplicate instances).
	private volatile boolean jdk21FallbackTriggered = false;

	private volatile WordprocessingMLDocxStAXTemplate staxFallback;

	/**
	 * 懒加载并复用 StAX 降级模板；占位符配置与当前 SAX 模板保持一致，
	 * 保证降级前后的变量替换语义透明一致。
	 */
	private WordprocessingMLDocxStAXTemplate fallback() {
		WordprocessingMLDocxStAXTemplate local = staxFallback;
		if (local == null) {
			synchronized (this) {
				local = staxFallback;
				if (local == null) {
					local = new WordprocessingMLDocxStAXTemplate();
					local.setPlaceholderStart(this.getPlaceholderStart());
					local.setPlaceholderEnd(this.getPlaceholderEnd());
					staxFallback = local;
				}
			}
		}
		return local;
	}

	/**
	 * JDK 21+ 下 docx4j 的 SAXHandler 不可用（见 {@link #fallback()} 的说明），
	 * 首次触发时记录一次 WARN；{@link #jdk21FallbackTriggered} 置位后
	 * {@code process(...)} 会透明地委托给 StAX 模板。
	 */
	private void checkJdk21OrFallback() {
		int major = Runtime.version().feature();
		if (major >= 21 && !jdk21FallbackTriggered) {
			jdk21FallbackTriggered = true;
			LOG.warn("WordprocessingMLDocxSaxTemplate is incompatible with JDK {} "
					+ "(docx4j 17.0.3 SAXHandler limitation: Transformer doesn't invoke "
					+ "SAXSource.setContentHandler). Falling back transparently to "
					+ "WordprocessingMLDocxStAXTemplate; consider switching "
					+ "DocxTemplates.create(DocxMode.SAX) to DocxMode.STAX explicitly.", major);
		}
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
