/**
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
 * Skeleton for all {@link WordprocessingMLTemplate} implementations in this
 * package. The three historical subclasses
 * ({@link WordprocessingMLDocxTemplate},
 * {@link WordprocessingMLDocxSaxTemplate},
 * {@link WordprocessingMLDocxStAXTemplate}) differ only in the variable
 * replacement stage; that stage is modeled as a public {@link VariableReplacer}
 * SPI with three built-in implementations
 * ({@link VariableReplacer.Default}, {@link VariableReplacer.StAX},
 * {@link VariableReplacer.Sax}).
 *
 * <p>This class owns the shared pipeline:
 * <pre>
 *   Docx4J.load(template)  // 1. resolve + parse template (or build dummy)
 *   VariablePrepare.prepare // 2. flatten nested ${} runs
 *   WMLPackageUtils.cleanDocumentPart // 3. marshal/unwrap round-trip
 *   VariableReplacer.apply // 4. actual variable substitution (strategy)
 *   FontMapperHolder.useFontMapper // 5. attach user-supplied font mapper
 * </pre>
 *
 * <p>Subclasses provide a built-in {@link VariableReplacer} via
 * {@link #replacer()}; callers may override the strategy at runtime via
 * {@link #setReplacer(VariableReplacer)} for custom expression languages
 * (MVEL, SpEL, etc.). The JDK 21 SAX-to-StAX transparent fallback is
 * implemented once at the base level via a double-checked-locking volatile
 * field on the {@link VariableReplacer.Sax} class (matching the previous
 * fallback in {@code WordprocessingMLDocxSaxTemplate}).
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
abstract class AbstractWmlTemplate implements WordprocessingMLTemplate {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	/** Variable placeholder start, default: ${ */
	protected String placeholderStart = "${";

	/** Variable placeholder end, default: } */
	protected String placeholderEnd = "}";

	/** Concrete subclass picks the variable-replacement strategy. */
	protected abstract VariableReplacer replacer();

	/**
	 * Optional custom {@link VariableReplacer} injected by the caller.
	 * When non-null, this takes precedence over the built-in {@link #replacer()}.
	 */
	protected volatile VariableReplacer customReplacer = null;

	/**
	 * Set a custom {@link VariableReplacer} to override the built-in strategy.
	 * Pass {@code null} to revert to the built-in strategy provided by the
	 * concrete subclass.
	 *
	 * @param replacer the custom replacer, or {@code null} to use the default
	 */
	public void setReplacer(VariableReplacer replacer) {
		this.customReplacer = replacer;
	}

	/**
	 * Returns the effective replacer: the custom one if set, otherwise the
	 * built-in strategy from {@link #replacer()}.
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
	 * Flatten {@code Map<String, Object>} to {@code HashMap<String, String>} using
	 * {@code toString()} (null -> empty string). Shared by
	 * {@link VariableReplacer.Default} and historically used by the old
	 * {@code WordprocessingMLDocxTemplate}; protected so that same-package tests
	 * can still call it as an instance method.
	 */
	protected HashMap<String, String> getStaticData(Map<String, Object> variables) {
		HashMap<String, String> dataMap = new HashMap<String, String>();
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
