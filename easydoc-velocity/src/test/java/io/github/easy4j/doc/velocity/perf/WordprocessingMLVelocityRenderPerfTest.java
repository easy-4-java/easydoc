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
package io.github.easy4j.doc.velocity.perf;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easy4j.doc.velocity.WordprocessingMLVelocityTemplate;

/**
 * Timing regression for the Velocity engine render step and the end-to-end
 * template adapter path ({@code process}).
 *
 * <p>Mirrors {@code WordprocessingMLVelocityHelloTest}: the test-resource
 * {@code docx4j.properties} configures a file resource loader rooted at
 * {@code /tpl}, so the template name is {@code hello.vm}; and
 * {@code VelocityTemplate.render()} puts internal keys into the variables
 * map, so a mutable {@link HashMap} is required.</p>
 *
 * <p>{@code render(String, Map)} is protected, so a local subclass widens it
 * to public for direct timing. Plain JUnit wall-clock timing: 50 warmup +
 * 1 000 measured renders; median asserted against a bound roughly 5x+
 * observed.</p>
 *
 * <p>Measured baseline (JDK 21, Apple Silicon, surefire):
 *   - render hello.vm: ~3 us per call (median over 1 000 renders)
 *   - process hello.vm (end-to-end docx): ~23 ms per call (median of 3)</p>
 */
class WordprocessingMLVelocityRenderPerfTest {

    private static final Logger LOG = LoggerFactory.getLogger(WordprocessingMLVelocityRenderPerfTest.class);

    private static final String TEMPLATE = "hello.vm";

    private static final int RENDER_WARMUP = 50;
    private static final int RENDER_MEASURED = 1_000;
    private static final long MAX_MEDIAN_RENDER_MICROS = 50_000L;

    private static final int PROCESS_MEASURED = 3;
    private static final long MAX_MEDIAN_PROCESS_MILLIS = 5_000L;

    /** Local subclass exposing the protected engine render step for direct timing. */
    private static class ExposedTemplate extends WordprocessingMLVelocityTemplate {
        @Override
        public String render(String template, Map<String, Object> variables) throws Exception {
            return super.render(template, variables);
        }
    }

    @Test
    @Timeout(value = 60)
    void renderHelloTemplateUnderBound() throws Exception {
        ExposedTemplate t = new ExposedTemplate();
        Map<String, Object> vars = new HashMap<>(Map.of("name", "world"));

        String out = null;
        for (int i = 0; i < RENDER_WARMUP; i++) {
            out = t.render(TEMPLATE, vars);
        }
        assertTrue(out != null && out.contains("Hello world"), "render must produce the expected HTML");

        long[] samplesMicros = new long[RENDER_MEASURED];
        for (int i = 0; i < RENDER_MEASURED; i++) {
            long start = System.nanoTime();
            out = t.render(TEMPLATE, vars);
            samplesMicros[i] = (System.nanoTime() - start) / 1_000L;
        }
        long medianMicros = median(samplesMicros);
        LOG.debug("velocity render {}: {} us median over {} renders", TEMPLATE, medianMicros, RENDER_MEASURED);
        assertTrue(out.contains("Hello world"));
        assertTrue(medianMicros < MAX_MEDIAN_RENDER_MICROS,
                "velocity render median " + medianMicros + " us exceeded bound "
                        + MAX_MEDIAN_RENDER_MICROS + " us");
    }

    @Test
    @Timeout(value = 120)
    void processHelloTemplateEndToEndUnderBound() throws Exception {
        WordprocessingMLVelocityTemplate t = new WordprocessingMLVelocityTemplate();
        Map<String, Object> vars = new HashMap<>(Map.of("name", "world"));

        // warmup (engine + docx pipeline init)
        assertNotNull(t.process(TEMPLATE, vars));

        long[] samplesMillis = new long[PROCESS_MEASURED];
        for (int i = 0; i < PROCESS_MEASURED; i++) {
            long start = System.nanoTime();
            WordprocessingMLPackage pkg = t.process(TEMPLATE, vars);
            samplesMillis[i] = (System.nanoTime() - start) / 1_000_000L;
            assertNotNull(pkg);
        }
        long medianMillis = median(samplesMillis);
        LOG.debug("velocity process {} end-to-end: {} ms median over {} renders", TEMPLATE, medianMillis, PROCESS_MEASURED);
        assertTrue(medianMillis < MAX_MEDIAN_PROCESS_MILLIS,
                "velocity process median " + medianMillis + " ms exceeded bound "
                        + MAX_MEDIAN_PROCESS_MILLIS + " ms");
    }

    private static long median(long[] samples) {
        long[] sorted = samples.clone();
        Arrays.sort(sorted);
        int mid = sorted.length / 2;
        return (sorted.length % 2 == 1) ? sorted[mid] : (sorted[mid - 1] + sorted[mid]) / 2L;
    }
}
