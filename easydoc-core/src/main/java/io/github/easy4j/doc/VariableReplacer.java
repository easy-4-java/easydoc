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
 * Strategy interface for variable replacement in WordprocessingML documents.
 *
 * <p>This is the public SPI that allows users to inject custom variable
 * replacement strategies (e.g. MVEL, SpEL, or any other expression language).
 * The default implementations ({@link Default}, {@link Sax}, {@link StAX})
 * are provided for backward compatibility with the original docx4j-based
 * processing pipeline.
 *
 * <p>To use a custom implementation:
 * <pre>{@code
 * WordprocessingMLDocxTemplate template = new WordprocessingMLDocxTemplate();
 * template.setReplacer((documentPart, tpl, variables) -> {
 *     // Custom variable replacement logic
 * });
 * template.process(templateFile, variables);
 * }</pre>
 *
 * <p>{@code beforeProcess(template)} is invoked before {@code Docx4J.load}
 * so that strategies can short-circuit to a fallback (e.g. {@link Sax}
 * detecting JDK 21 and delegating to {@link StAX}).
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see AbstractWmlTemplate#setReplacer(VariableReplacer)
 */
public interface VariableReplacer {

    /** Strategy-specific pre-flight hook (e.g. JDK 21 fallback trigger). */
    default void beforeProcess(AbstractWmlTemplate template) { /* no-op */ }

    /** Apply the variable substitution to {@code documentPart}. */
    void apply(MainDocumentPart documentPart, AbstractWmlTemplate template,
            Map<String, Object> variables) throws Exception;

    /**
     * Docx4j's {@code variableReplace} path — pure String substitution.
     * Replaces placeholders using {@link MainDocumentPart#variableReplace(Map)}.
     */
    record Default() implements VariableReplacer {
        @Override
        public void apply(MainDocumentPart documentPart, AbstractWmlTemplate template,
                Map<String, Object> variables) throws Exception {
            documentPart.variableReplace(template.getStaticData(variables));
        }
    }

    /**
     * StAX streaming pipeline with OGNL expression evaluation.
     * Uses {@link VariableReplaceSaTXHandler} for streaming variable substitution.
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
     * SAX pipeline. On JDK 21+ the underlying docx4j SAXHandler is broken
     * (Transformer does not invoke SAXSource's setContentHandler), so this
     * class transparently falls back to a {@link StAX} instance via
     * double-checked-locking on a volatile field. Fallback is logged once
     * per template instance — preserves the historical
     * {@code WordprocessingMLDocxSaxTemplate} behavior.
     *
     * <p><b>Factory-layer note:</b> {@link DocxTemplates#create(DocxMode)} already
     * short-circuits {@code DocxMode.SAX} to {@code WordprocessingMLDocxStAXTemplate}
     * on JDK 21+ (logged once at INFO). The runtime fallback here only applies when
     * a caller directly instantiates {@code new WordprocessingMLDocxSaxTemplate()}
     * bypassing the factory.
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
