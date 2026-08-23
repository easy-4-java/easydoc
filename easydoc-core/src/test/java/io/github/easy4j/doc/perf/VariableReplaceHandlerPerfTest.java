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
package io.github.easy4j.doc.perf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easy4j.doc.handler.VariableReplaceSAXHandler;
import io.github.easy4j.doc.handler.VariableReplaceSaTXHandler;

/**
 * Timing regression test for the variable-replacement hot path exercised by
 * docx templating: {@link VariableReplaceSAXHandler} (SAX) and
 * {@link VariableReplaceSaTXHandler} (StAX).
 *
 * <p>Both handlers run {@code replace(...)} once per text node of every
 * document, so it is the single hottest loop in template rendering. The OGNL
 * security fix (restricted {@code DefaultMemberAccess}) must not have made
 * expression evaluation measurably slower.</p>
 *
 * <p>{@code replace(String, int, StringBuilder, Map)} is private on both
 * handlers, so it is invoked reflectively (same unnamed module —
 * {@code setAccessible} is permitted). Plain JUnit timing, 1 000 warmup +
 * 10 000 measured iterations per case; the asserted bound is the total wall
 * time of the measured block, roughly 5x observed to avoid CI flakes.</p>
 *
 * <p>Note: docx4j 11.5.14 {@code SAXHandler} cannot be constructed on JDK 21+
 * ("Transformer didn't set ContentHandler" — see
 * {@code WordprocessingMLDocxSaxTemplate#assertJdkCompatible}); on such JDKs
 * the SAX timing cases are skipped via assumption and only the StAX variant
 * (the working path on modern JDKs) is timed.</p>
 *
 * <p>Measured baseline (JDK 21, Apple Silicon, surefire):
 *   - StAX mapped key ${var007} x 10 000: ~3 ms total (~0.3 us/call)
 *   - StAX OGNL expression ${user.name} x 10 000: ~190 ms total (~19 us/call)
 * Bounds are 200 ms / 2 000 ms respectively (~65x / ~10x headroom).</p>
 */
class VariableReplaceHandlerPerfTest {

    private static final Logger LOG = LoggerFactory.getLogger(VariableReplaceHandlerPerfTest.class);

    private static final int WARMUP_ITERATIONS = 1_000;
    private static final int MEASURED_ITERATIONS = 10_000;

    private static final long MAPPED_KEY_MAX_TOTAL_MILLIS = 200L;
    private static final long OGNL_EXPRESSION_MAX_TOTAL_MILLIS = 2_000L;

    private static Map<String, Object> variables;
    private static Method saxReplace;
    private static Method staxReplace;

    @BeforeAll
    static void setUp() throws Exception {
        variables = new HashMap<>(128);
        for (int i = 0; i < 100; i++) {
            variables.put(String.format("var%03d", i), "value-" + i);
        }
        // “user.name” misses the map lookup on purpose, forcing the OGNL
        // parse+evaluate path that the security hardening touched
        variables.put("user", Map.of("name", "easy4j"));

        saxReplace = replaceMethod(VariableReplaceSAXHandler.class);
        staxReplace = replaceMethod(VariableReplaceSaTXHandler.class);
    }

    @Test
    @Timeout(value = 60)
    void saxMappedKeyReplaceUnderBound() throws Exception {
        VariableReplaceSAXHandler handler = newSaxHandlerOrNull();
        Assumptions.assumeTrue(handler != null,
                "SAXHandler cannot be constructed on JDK 21+ (docx4j fail-fast); StAX cases cover this path");
        StringBuilder out = timedLoop("SAX ${var007}", saxReplace, handler, "${var007}");
        assertEquals("value-7", out.toString());
    }

    @Test
    @Timeout(value = 60)
    void saxOgnlExpressionReplaceUnderBound() throws Exception {
        VariableReplaceSAXHandler handler = newSaxHandlerOrNull();
        Assumptions.assumeTrue(handler != null,
                "SAXHandler cannot be constructed on JDK 21+ (docx4j fail-fast); StAX cases cover this path");
        StringBuilder out = timedLoop("SAX ${user.name}", saxReplace, handler, "${user.name}");
        assertEquals("easy4j", out.toString());
    }

    @Test
    @Timeout(value = 60)
    void staxMappedKeyReplaceUnderBound() throws Exception {
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler(variables);
        StringBuilder out = timedLoop("StAX ${var007}", staxReplace, handler, "${var007}");
        assertEquals("value-7", out.toString());
    }

    @Test
    @Timeout(value = 60)
    void staxOgnlExpressionReplaceUnderBound() throws Exception {
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler(variables);
        StringBuilder out = timedLoop("StAX ${user.name}", staxReplace, handler, "${user.name}");
        assertEquals("easy4j", out.toString());
    }

    private static StringBuilder timedLoop(String label, Method replace, Object handler, String template)
            throws Exception {
        StringBuilder last = null;
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            last = (StringBuilder) replace.invoke(handler, template, 0, new StringBuilder(), variables);
        }
        long start = System.nanoTime();
        for (int i = 0; i < MEASURED_ITERATIONS; i++) {
            last = (StringBuilder) replace.invoke(handler, template, 0, new StringBuilder(), variables);
        }
        long totalMillis = (System.nanoTime() - start) / 1_000_000L;

        long maxMillis = template.contains(".") ? OGNL_EXPRESSION_MAX_TOTAL_MILLIS
                : MAPPED_KEY_MAX_TOTAL_MILLIS;
        LOG.debug("{} : {} ms total for {} iterations ({} us/call)",
                label, totalMillis, MEASURED_ITERATIONS, totalMillis * 1_000L / MEASURED_ITERATIONS);
        assertTrue(totalMillis < maxMillis,
                label + " took " + totalMillis + " ms for " + MEASURED_ITERATIONS
                        + " iterations, exceeding the " + maxMillis + " ms bound");
        return last;
    }

    private static Method replaceMethod(Class<?> handlerType) throws NoSuchMethodException {
        // 两个 handler 的 replace 均为 private，同 unnamed module 下反射可访问
        Method m = handlerType.getDeclaredMethod("replace",
                String.class, int.class, StringBuilder.class, Map.class);
        m.setAccessible(true);
        return m;
    }

    /**
     * docx4j SAXHandler 在 JDK 21+ 无法实例化（"Transformer didn't set
     * ContentHandler"）；返回 null 让调用方以 Assumption 跳过 SAX 计时。
     */
    private static VariableReplaceSAXHandler newSaxHandlerOrNull() {
        try {
            return new VariableReplaceSAXHandler(variables);
        } catch (Throwable e) {
            return null;
        }
    }
}
