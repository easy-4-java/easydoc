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
 * Factory that resolves a {@link WordprocessingMLTemplate} implementation for
 * a {@link WordprocessingMLDocxTemplate}-style docx pipeline based on the
 * requested {@link DocxMode}.
 *
 * <p>Same key as the engine modules' factories (e.g. Thymeleaf/Freemarker): the
 * three Docx variants already exist as concrete classes; this factory only
 * selects between them.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class DocxTemplates {

	private DocxTemplates() {
	}

	/**
	 * Create the {@link WordprocessingMLTemplate} backing the requested docx mode.
	 *
	 * @param mode docx engine selector; {@code null} falls through to {@link DocxMode#DEFAULT}.
	 * @return a fresh {@link WordprocessingMLDocxTemplate}, {@link WordprocessingMLDocxSaxTemplate},
	 *         or {@link WordprocessingMLDocxStAXTemplate} — all wrapped as a {@link WordprocessingMLTemplate}.
	 */
	public static WordprocessingMLTemplate create(DocxMode mode) {
		// 注册制工厂：新增 DocxMode 只需向 FACTORIES 注册，无需改 switch
		DocxMode resolved = mode == null ? DocxMode.DEFAULT : mode;
		return FACTORIES.getOrDefault(resolved, FACTORIES.get(DocxMode.DEFAULT)).get();
	}

	private static final java.util.Map<DocxMode, java.util.function.Supplier<WordprocessingMLTemplate>> FACTORIES =
			java.util.Map.of(
					DocxMode.DEFAULT, WordprocessingMLDocxTemplate::new,
					DocxMode.SAX, WordprocessingMLDocxSaxTemplate::new,
					DocxMode.STAX, WordprocessingMLDocxStAXTemplate::new);
}
