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

/**
 * 该模板负责对WordprocessingMLPackage进行普通变量替换和复杂变量替换并返回处理后的WordprocessingMLPackage对象
 * 备注：该工具只能解决固定模板的word生成（来自：https://blog.csdn.net/qq_35598240/article/details/84439929）
 *
 * <p>本类成为
 * {@link AbstractWmlTemplate} + {@link VariableReplacer.Sax}
 * 策略的薄门面。JDK 21 上向 StAX 的透明降级位于该 final 类内部；
 * 公共 API 保持原样。
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLDocxSaxTemplate extends AbstractWmlTemplate {

	private final VariableReplacer.Sax replacer = new VariableReplacer.Sax();

	@Override
	protected VariableReplacer replacer() {
		return replacer;
	}
}
