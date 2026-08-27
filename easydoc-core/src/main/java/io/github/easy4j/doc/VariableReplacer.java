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

import java.util.Map;

import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.slf4j.LoggerFactory;

import io.github.easy4j.doc.handler.VariableReplaceSAXHandler;
import io.github.easy4j.doc.handler.VariableReplaceSaTXHandler;

/**
 * WordprocessingML 文档变量替换的策略接口（SPI）。
 *
 * <p>这是对外公开的扩展点，允许使用者注入自定义的变量替换策略
 * （如 MVEL、SpEL 等任意表达式语言）。内置实现
 * {@link Default}、{@link Sax}、{@link StAX}
 * 与原有基于 docx4j 的处理管道保持完全兼容。
 *
 * <p>自定义实现示例：
 * <pre>{@code
 * WordprocessingMLDocxTemplate template = new WordprocessingMLDocxTemplate();
 * template.setReplacer((documentPart, tpl, variables) -> {
 *     // 自定义变量替换逻辑
 * });
 * template.process(templateFile, variables);
 * }</pre>
 *
 * <p>{@code beforeProcess(template)} 在 {@code Docx4J.load} 之前被调用，
 * 策略可借此短路降级到备用路径（例如 {@link Sax} 检测到 JDK 21
 * 后委托给 {@link StAX}）。
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see AbstractWmlTemplate#setReplacer(VariableReplacer)
 */
public interface VariableReplacer {

	/** 策略级预处理钩子（例如 JDK 21 下的降级触发），默认空操作。 */
	default void beforeProcess(AbstractWmlTemplate template) { /* no-op */ }

	/** 对 {@code documentPart} 执行变量替换。 */
	void apply(MainDocumentPart documentPart, AbstractWmlTemplate template,
			Map<String, Object> variables) throws Exception;

	/**
	 * Docx4j 原生 {@code variableReplace} 路径——纯字符串替换。
	 * 通过 {@link MainDocumentPart#variableReplace(Map)} 替换占位符。
	 */
	record Default() implements VariableReplacer {
		@Override
		public void apply(MainDocumentPart documentPart, AbstractWmlTemplate template,
				Map<String, Object> variables) throws Exception {
			documentPart.variableReplace(template.getStaticData(variables));
		}
	}

	/**
	 * 基于 StAX 流式管道 + OGNL 表达式求值的替换策略。
	 * 使用 {@link VariableReplaceSaTXHandler} 进行流式变量替换。
	 */
	record StAX() implements VariableReplacer {
		@Override
		public void apply(MainDocumentPart documentPart, AbstractWmlTemplate template,
				Map<String, Object> variables) throws Exception {
			documentPart.pipe(new VariableReplaceSaTXHandler(
					template.getPlaceholderStart(),
					template.getPlaceholderEnd(),
					variables));
		}
	}

	/**
	 * SAX 管道。JDK 21+ 上底层 docx4j SAXHandler 存在缺陷
	 * （Transformer 不会调用 SAXSource 的 setContentHandler），因此本类
	 * 通过 volatile 字段上的双重检查机制透明降级到 {@link StAX} 实例。
	 * 每个模板实例仅告警一次——保持历史上
	 * {@code WordprocessingMLDocxSaxTemplate} 的行为不变。
	 *
	 * <p><b>JDK 17 基线说明：</b>在 2.0.x 的 JDK 17 基线上，
	 * {@code Runtime.version().feature()} 返回 17（小于 21），
	 * 上述降级逻辑永远不会触发；代码予以保留以保持前向兼容。
	 */
	final class Sax implements VariableReplacer {
		// volatile + 单次触发：beforeProcess() 可能被多个线程并发调用；
		// 该标志必须安全发布且只被置位一次。
		private volatile boolean jdk21FallbackTriggered = false;

		@Override
		public void beforeProcess(AbstractWmlTemplate template) {
			int major = Runtime.version().feature();
			if (major >= 21 && !jdk21FallbackTriggered) {
				jdk21FallbackTriggered = true;
				LoggerFactory.getLogger(AbstractWmlTemplate.class).warn(
						"WordprocessingMLDocxSaxTemplate is incompatible with JDK {} "
								+ "(docx4j 17.0.3 SAXHandler limitation: Transformer doesn't "
								+ "invoke SAXSource.setContentHandler). Falling back "
								+ "transparently to WordprocessingMLDocxStAXTemplate; "
								+ "consider switching to StAX explicitly.",
						major);
			}
		}

		@Override
		public void apply(MainDocumentPart documentPart, AbstractWmlTemplate template,
				Map<String, Object> variables) throws Exception {
			if (jdk21FallbackTriggered) {
				// JDK 21 降级：复用与 StAX 策略相同的 VariableReplaceSaTXHandler，
				// 而不是绕经 WordprocessingMLDocxStAXTemplate（那会重复执行
				// 已完成的 load + VariablePrepare 阶段）。
				documentPart.pipe(new VariableReplaceSaTXHandler(
						template.getPlaceholderStart(),
						template.getPlaceholderEnd(),
						variables));
				return;
			}
			// JDK < 21 的真实 SAX 路径。
			documentPart.pipe(new VariableReplaceSAXHandler(
					template.getPlaceholderStart(),
					template.getPlaceholderEnd(),
					variables));
		}
	}
}
