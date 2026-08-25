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
 * Selects which {@link WordprocessingMLDocxTemplate} implementation the
 * {@link DocxTemplates} factory instantiates.
 *
 * <ul>
 *   <li>{@link #DEFAULT} — standard {@link WordprocessingMLDocxTemplate} backed
 *       by {@code org.docx4j} (JAXB-bound).</li>
 *   <li>{@link #SAX} — {@link WordprocessingMLDocxSaxTemplate}, SAX-driven path.</li>
 *   <li>{@link #STAX} — {@link WordprocessingMLDocxStAXTemplate}, StAX-driven path.</li>
 * </ul>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public enum DocxMode {

	DEFAULT,

	/**
	 * SAX-driven path.
	 *
	 * @deprecated since 3.0 — docx4j 17.0.3's {@code SAXHandler} is incompatible with
	 * JDK 21+ (the {@code Transformer} does not invoke {@code SAXSource.setContentHandler}).
	 * The {@link DocxTemplates} factory now returns {@link #STAX} directly on JDK 21+,
	 * making this enum constant semantically equivalent to {@code STAX} at creation time.
	 * The runtime fallback in {@link VariableReplacer.Sax} is retained only as a defence
	 * against direct {@code new WordprocessingMLDocxSaxTemplate()} instantiation.
	 */
	@Deprecated(since = "3.0")
	SAX,
	STAX
}
