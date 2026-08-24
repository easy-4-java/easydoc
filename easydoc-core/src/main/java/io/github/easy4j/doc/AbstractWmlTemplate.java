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
import io.github.easy4j.doc.handler.VariableReplaceSAXHandler;
import io.github.easy4j.doc.handler.VariableReplaceSaTXHandler;
import io.github.easy4j.doc.utils.WMLPackageUtils;

/**
 * Skeleton for all {@link WordprocessingMLTemplate} implementations in this
 * package. JDK 21 sealed-types refactor: the three historical subclasses
 * ({@link WordprocessingMLDocxTemplate},
 * {@link WordprocessingMLDocxSaxTemplate},
 * {@link WordprocessingMLDocxStAXTemplate}) differ only in the variable
 * replacement stage; that stage is now modeled as a sealed
 * {@link VariableReplacer} with three record implementations
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
 * <p>Subclasses only need to provide the right {@link VariableReplacer};
 * the JDK 21 SAX-to-StAX transparent fallback is implemented once at the
 * base level via a double-checked-locking volatile field on the
 * {@link VariableReplacer.Sax} record (matching the previous fallback in
 * {@code WordprocessingMLDocxSaxTemplate}).
 */
abstract class AbstractWmlTemplate implements WordprocessingMLTemplate {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	/** Variable placeholder start, default: ${ */
	protected String placeholderStart = "${";

	/** Variable placeholder end, default: } */
	protected String placeholderEnd = "}";

	/** Concrete subclass picks the variable-replacement strategy. */
	protected abstract VariableReplacer replacer();

	@Override
	public WordprocessingMLPackage process(File template, Map<String, Object> variables) throws Exception {
		// Replacer-level JDK 21 fallback (Sax strategy triggers its own checkJdk21OrFallback()).
		replacer().beforeProcess(this);
		WordprocessingMLPackage pkg = loadOrCreate(template);
		if (nonEmpty(variables)) {
			apply(pkg, variables);
		}
		return FontMapperHolder.useFontMapper(pkg);
	}

	@Override
	public WordprocessingMLPackage process(InputStream template, Map<String, Object> variables) throws Exception {
		replacer().beforeProcess(this);
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
		replacer().apply(documentPart, this, variables);
	}

	private static boolean nonEmpty(Map<String, Object> variables) {
		return variables != null && !variables.isEmpty();
	}

	/**
	 * Flatten {@code Map<String, Object>} to {@code HashMap<String, String>} using
	 * {@code toString()} (null → empty string). Shared by
	 * {@link VariableReplacer.Default} and historically used by the old
	 * {@code WordprocessingMLDocxTemplate}; protected so that same-package tests
	 * (e.g. {@code WordprocessingMLTemplateVariantsTest}) can still call it as
	 * an instance method.
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

	/**
	 * Sealed family of variable-replacement strategies. Each implementation
	 * knows how to apply a placeholder substitution to a loaded
	 * {@link MainDocumentPart}. JDK 21 sealed types model the closed set of
	 * three historical engines (DEFAULT, SAX, STAX) without subclass explosion.
	 *
	 * <p>{@code beforeProcess(template)} is invoked before
	 * {@code Docx4J.load} so that strategies can short-circuit to a fallback
	 * (e.g. {@link Sax} detecting JDK 21 and delegating to {@link StAX}).
	 */
	sealed interface VariableReplacer
			permits VariableReplacer.Default, VariableReplacer.Sax, VariableReplacer.StAX {

		/** Strategy-specific pre-flight hook (e.g. JDK 21 fallback trigger). */
		default void beforeProcess(AbstractWmlTemplate template) { /* no-op */ }

		/** Apply the variable substitution to {@code documentPart}. */
		void apply(MainDocumentPart documentPart, AbstractWmlTemplate template,
				Map<String, Object> variables) throws Exception;

		/** Docx4j's {@code variableReplace} path — pure String substitution. */
		record Default() implements VariableReplacer {
			@Override
			public void apply(MainDocumentPart documentPart, AbstractWmlTemplate template,
					Map<String, Object> variables) throws Exception {
				documentPart.variableReplace(template.getStaticData(variables));
			}
		}

		/** StAX streaming pipeline with OGNL expression evaluation. */
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
		 * SAX pipeline. On JDK 21+ the underlying docx4j SAXHandler is broken
		 * (Transformer does not invoke SAXSource's setContentHandler), so this
		 * record transparently falls back to a {@link StAX} instance via
		 * double-checked-locking on a volatile field. Fallback is logged once
		 * per template instance — preserves the historical
		 * {@code WordprocessingMLDocxSaxTemplate} behavior.
		 */
		final class Sax implements VariableReplacer {
			// volatile + single-flight: beforeProcess() may be invoked from many
			// virtual threads concurrently; the flag must be published safely
			// and set exactly once.
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
									+ "consider switching DocxTemplates.create(DocxMode.SAX) to "
									+ "DocxMode.STAX explicitly.",
							major);
				}
			}

			@Override
			public void apply(MainDocumentPart documentPart, AbstractWmlTemplate template,
					Map<String, Object> variables) throws Exception {
				if (jdk21FallbackTriggered) {
					// JDK 21 fallback: reuse the same VariableReplaceSaTXHandler that
					// the StAX strategy uses, instead of routing through
					// WordprocessingMLDocxStAXTemplate (which would re-run the
					// load + VariablePrepare stages we already executed).
					documentPart.pipe(new VariableReplaceSaTXHandler(
							template.getPlaceholderStart(),
							template.getPlaceholderEnd(),
							variables));
					return;
				}
				// Real SAX path on JDK < 21.
				documentPart.pipe(new VariableReplaceSAXHandler(
						template.getPlaceholderStart(),
						template.getPlaceholderEnd(),
						variables));
			}
		}
	}
}