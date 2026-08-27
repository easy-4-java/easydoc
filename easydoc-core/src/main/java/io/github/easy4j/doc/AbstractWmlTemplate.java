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
import java.util.HashMap;
import java.util.Map;

import org.docx4j.Docx4J;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easy4j.doc.fonts.FontMapperHolder;
import io.github.easy4j.doc.utils.WMLPackageUtils;

/**
 * 本包内所有 {@link WordprocessingMLTemplate} 实现的骨架。三个历史子类
 * （{@link WordprocessingMLDocxTemplate},
 * {@link WordprocessingMLDocxSaxTemplate},
 * {@link WordprocessingMLDocxStAXTemplate}）的差异仅在变量替换阶段；
 * 该阶段被建模为公开的 {@link VariableReplacer} SPI，并内置三种实现
 * （{@link VariableReplacer.Default}, {@link VariableReplacer.StAX},
 * {@link VariableReplacer.Sax}）。
 *
 * <p>本类负责共享管道：
 * <pre>
 *   Docx4J.load(template)  // 1. 定位并解析模板（或构建占位文档）
 *   VariablePrepare.prepare // 2. 拍平嵌套 ${} 文本段
 *   WMLPackageUtils.cleanDocumentPart // 3. marshal/unwrap 往返规范化
 *   VariableReplacer.apply // 4. 实际的变量替换（策略）
 *   FontMapperHolder.useFontMapper // 5. 挂载用户提供的字体映射器
 * </pre>
 *
 * <p>子类通过 {@link #replacer()} 提供内置
 * {@link VariableReplacer}；调用方可在运行时通过
 * {@link #setReplacer(VariableReplacer)} 覆盖策略，以接入自定义表达式语言
 * （MVEL、SpEL 等）。JDK 21 上 SAX 到 StAX 的透明降级在基类层只实现一次，
 * 通过 {@link VariableReplacer.Sax} 类上的 volatile 字段双重检查完成
 * （与原先 {@code WordprocessingMLDocxSaxTemplate} 中的降级逻辑一致）。
 */
abstract class AbstractWmlTemplate implements WordprocessingMLTemplate {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	/** 变量占位符起始标记，默认：${ */
	protected String placeholderStart = "${";

	/** 变量占位符结束标记，默认：} */
	protected String placeholderEnd = "}";

	/** 具体子类决定内置的变量替换策略。 */
	protected abstract VariableReplacer replacer();

	/**
	 * 可选的自定义 {@link VariableReplacer}，由调用方注入。
	 * 非空时优先于内置的 {@link #replacer()}。
	 */
	protected volatile VariableReplacer customReplacer = null;

	/**
	 * 设置自定义 {@link VariableReplacer} 以覆盖内置替换策略。
	 * 传入 {@code null} 则回退到具体子类提供的内置策略。
	 *
	 * @param replacer 自定义 replacer；传 {@code null} 使用默认策略
	 */
	public void setReplacer(VariableReplacer replacer) {
		this.customReplacer = replacer;
	}

	/**
	 * 返回生效中的 replacer：已设置自定义策略时返回自定义，
	 * 否则返回 {@link #replacer()} 的内置策略。
	 */
	protected VariableReplacer currentReplacer() {
		return customReplacer != null ? customReplacer : replacer();
	}

	@Override
	public WordprocessingMLPackage process(File template, Map<String, Object> variables) throws Exception {
		// Replacer-level JDK 21 fallback (Sax strategy triggers its own checkJdk21OrFallback()).
		currentReplacer().beforeProcess(this);
		WordprocessingMLPackage pkg = loadOrCreate(template);
		if (nonEmpty(variables)) {
			apply(pkg, variables);
		}
		return FontMapperHolder.useFontMapper(pkg);
	}

	@Override
	public WordprocessingMLPackage process(InputStream template, Map<String, Object> variables) throws Exception {
		currentReplacer().beforeProcess(this);
		WordprocessingMLPackage pkg = loadOrCreate(template);
		if (nonEmpty(variables)) {
			apply(pkg, variables);
		}
		return FontMapperHolder.useFontMapper(pkg);
	}

	private WordprocessingMLPackage loadOrCreate(File template) throws Exception {
		if (template == null || !template.exists() || !template.isFile()) {
			log.debug("No imput path passed, creating dummy document");
			WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
			SampleDocument.createContent(pkg.getMainDocumentPart());
			return pkg;
		}
		log.debug("Loading file from " + template.getAbsolutePath());
		return Docx4J.load(template);
	}

	private WordprocessingMLPackage loadOrCreate(InputStream template) throws Exception {
		if (template == null) {
			log.debug("No imput path passed, creating dummy document");
			WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
			SampleDocument.createContent(pkg.getMainDocumentPart());
			return pkg;
		}
		log.debug("Loading file from InputStream");
		return Docx4J.load(template);
	}

	private void apply(WordprocessingMLPackage pkg, Map<String, Object> variables) throws Exception {
		MainDocumentPart documentPart = pkg.getMainDocumentPart();
		// Collapse nested ${} runs into one level.
		VariablePrepare.prepare(pkg);
		// Marshal/unwrap round-trip so JAXB elements are normalized.
		WMLPackageUtils.cleanDocumentPart(documentPart);
		// Strategy-specific variable substitution.
		currentReplacer().apply(documentPart, this, variables);
	}

	private static boolean nonEmpty(Map<String, Object> variables) {
		return variables != null && !variables.isEmpty();
	}

	/**
	 * 使用 {@code toString()} 将 {@code Map<String, Object>} 拍平为
	 * {@code HashMap<String, String>}（null 转空字符串）。由
	 * {@link VariableReplacer.Default} 共享使用，历史上也被旧的
	 * {@code WordprocessingMLDocxTemplate} 使用；保持 protected 以便同包测试
	 * （如 {@code WordprocessingMLTemplateVariantsTest}）仍能以实例方法调用。
	 */
	protected HashMap<String, String> getStaticData(Map<String, Object> variables) {
		HashMap<String, String> dataMap = new HashMap<>();
		if (variables != null) {
			for (Map.Entry<String, Object> e : variables.entrySet()) {
				Object val = e.getValue();
				dataMap.put(e.getKey(), val == null ? "" : val.toString());
			}
		}
		return dataMap;
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