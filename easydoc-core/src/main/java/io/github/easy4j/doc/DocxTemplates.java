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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOG = LoggerFactory.getLogger(DocxTemplates.class);

	private DocxTemplates() {
	}

	/**
	 * Create the {@link WordprocessingMLTemplate} backing the requested docx mode.
	 *
	 * <p>On JDK 21+ a {@link DocxMode#SAX} request is short-circuited to the
	 * {@link DocxMode#STAX} factory at creation time (docx4j 17.0.3's
	 * {@code SAXHandler} is incompatible with JDK 21's {@code Transformer}).
	 * The runtime fallback inside {@link VariableReplacer.Sax} is retained
	 * only as a defence against direct {@code new WordprocessingMLDocxSaxTemplate()}
	 * instantiation.</p>
	 *
	 * @param mode docx engine selector; {@code null} falls through to {@link DocxMode#DEFAULT}.
	 * @return a fresh {@link WordprocessingMLDocxTemplate}, {@link WordprocessingMLDocxSaxTemplate},
	 *         or {@link WordprocessingMLDocxStAXTemplate} — all wrapped as a {@link WordprocessingMLTemplate}.
	 */
	public static WordprocessingMLTemplate create(DocxMode mode) {
		DocxMode resolved = mode == null ? DocxMode.DEFAULT : mode;
		// JDK 21+ static short-circuit: SAX → STAX at factory level
		if (resolved == DocxMode.SAX && Runtime.version().feature() >= 21) {
			LOG.info("DocxMode.SAX short-circuited to STAX on JDK {} "
					+ "(docx4j 17.0.3 SAXHandler is incompatible with JDK 21+ Transformer); "
					+ "returning WordprocessingMLDocxStAXTemplate directly.",
					Runtime.version().feature());
			return FACTORIES.get(DocxMode.STAX).get();
		}
		return FACTORIES.getOrDefault(resolved, FACTORIES.get(DocxMode.DEFAULT)).get();
	}

	private static final java.util.Map<DocxMode, java.util.function.Supplier<WordprocessingMLTemplate>> FACTORIES =
			java.util.Map.of(
					DocxMode.DEFAULT, WordprocessingMLDocxTemplate::new,
					DocxMode.SAX, WordprocessingMLDocxSaxTemplate::new,
					DocxMode.STAX, WordprocessingMLDocxStAXTemplate::new);
}
