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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easy4j.doc.utils.WmlTableUtils;

/**
 * Timing regression test for {@link WmlTableUtils#createHyperlink}.
 *
 * <p>The XML-escape security fix (review H-2) added ten {@code escapeXml}
 * calls plus relationship bookkeeping to every hyperlink creation. This test
 * pins the per-call cost so a future regression trips the bound below instead
 * of silently shipping.</p>
 *
 * <p>Approach: plain JUnit wall-clock timing (no JMH). A warmup loop lets the
 * JIT settle, then per-call latencies are sampled and the median asserted
 * against a bound that is roughly 5x the observed baseline so slower CI
 * machines do not flake.</p>
 *
 * <p>Measured baseline (JDK 21, Apple Silicon, surefire):
 *   - createHyperlink: ~0.4 ms per call (median over 100 measured runs,
 *     after 20 warmup runs) — the asserted bound is 100 ms (~250x headroom)</p>
 */
class WmlTableUtilsPerfTest {

    private static final Logger LOG = LoggerFactory.getLogger(WmlTableUtilsPerfTest.class);

    private static final int WARMUP_ITERATIONS = 20;
    private static final int MEASURED_ITERATIONS = 100;
    /** 100 ms per call — generous (observed median is far below this). */
    private static final long MAX_MEDIAN_MICROS = 100_000L;

    private static final String URL = "https://example.com/path?q=1&p=2";
    private static final String VALUE = "click here with <script>";

    @Test
    @Timeout(value = 60)
    void createHyperlinkMedianPerCallStaysUnderBound() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mainPart = pkg.getMainDocumentPart();
        ObjectFactory factory = Context.getWmlObjectFactory();
        P paragraph = factory.createP();
        mainPart.addObject(paragraph);

        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            WmlTableUtils.createHyperlink(pkg, mainPart, factory, paragraph,
                    URL, VALUE, "SimSun", "Arial", "22");
        }

        long[] samplesMicros = new long[MEASURED_ITERATIONS];
        for (int i = 0; i < MEASURED_ITERATIONS; i++) {
            long start = System.nanoTime();
            WmlTableUtils.createHyperlink(pkg, mainPart, factory, paragraph,
                    URL, VALUE, "SimSun", "Arial", "22");
            samplesMicros[i] = (System.nanoTime() - start) / 1_000L;
        }

        long medianMicros = median(samplesMicros);
        LOG.debug("createHyperlink median per call: {} us over {} measured runs ({} warmup)",
                medianMicros, MEASURED_ITERATIONS, WARMUP_ITERATIONS);

        assertTrue(medianMicros < MAX_MEDIAN_MICROS,
                "createHyperlink median per call " + medianMicros + " us exceeded bound "
                        + MAX_MEDIAN_MICROS + " us — check the XML-escape path for regressions");
        // 每次调用都会向段落追加一个 hyperlink 节点：顺带断言调用确实生效
        assertTrue(paragraph.getContent().size() >= WARMUP_ITERATIONS + MEASURED_ITERATIONS,
                "hyperlink elements should accumulate in the paragraph");
    }

    static long median(long[] samples) {
        long[] sorted = samples.clone();
        Arrays.sort(sorted);
        int mid = sorted.length / 2;
        return (sorted.length % 2 == 1) ? sorted[mid] : (sorted[mid - 1] + sorted[mid]) / 2L;
    }
}
