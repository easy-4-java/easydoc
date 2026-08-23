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
package io.github.easy4j.doc.xhtml.perf;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;

/**
 * End-to-end render timing for {@link WordprocessingMLHtmlTemplate}:
 * inline-generated HTML (1 KB / 10 KB / 100 KB) converted through the full
 * jsoup-clean → XHTMLImporter → docx pipeline. Test data is built in memory on
 * purpose — real files would add disk I/O noise to the measurement.
 *
 * <p>Plain JUnit wall-clock timing (no JMH): one warmup render lets the JIT
 * and docx4j statics settle, then a few measured renders, median asserted
 * against bounds roughly 5x observed so slower CI machines do not flake.</p>
 *
 * <p>Measured baseline (JDK 21, Apple Silicon, surefire):
 *   - XHTML render 1 KB: ~10 ms (bound 500 ms, ~50x headroom)
 *   - XHTML render 10 KB: ~40 ms (bound 1 000 ms, ~25x headroom)
 *   - XHTML render 100 KB: ~150 ms (bound 5 000 ms, ~33x headroom)</p>
 */
class HtmlTemplateRenderPerfTest {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlTemplateRenderPerfTest.class);

    private static final long MAX_MEDIAN_MILLIS_1KB = 500L;
    private static final long MAX_MEDIAN_MILLIS_10KB = 1_000L;
    private static final long MAX_MEDIAN_MILLIS_100KB = 5_000L;

    @Test
    @Timeout(value = 120)
    void render1KbHtmlUnderBound() throws Exception {
        assertRenderUnderBound(htmlOfAtLeast(1024), 1, 5, MAX_MEDIAN_MILLIS_1KB, "1 KB");
    }

    @Test
    @Timeout(value = 120)
    void render10KbHtmlUnderBound() throws Exception {
        assertRenderUnderBound(htmlOfAtLeast(10 * 1024), 1, 3, MAX_MEDIAN_MILLIS_10KB, "10 KB");
    }

    @Test
    @Timeout(value = 300)
    void render100KbHtmlUnderBound() throws Exception {
        assertRenderUnderBound(htmlOfAtLeast(100 * 1024), 0, 2, MAX_MEDIAN_MILLIS_100KB, "100 KB");
    }

    private static void assertRenderUnderBound(String html, int warmupRenders, int measuredRenders,
            long maxMedianMillis, String label) throws Exception {
        WordprocessingMLHtmlTemplate template = new WordprocessingMLHtmlTemplate();
        Map<String, Object> vars = Map.of();

        for (int i = 0; i < warmupRenders; i++) {
            assertNotNull(template.process(html, vars));
        }
        long[] samplesMillis = new long[measuredRenders];
        for (int i = 0; i < measuredRenders; i++) {
            long start = System.nanoTime();
            WordprocessingMLPackage pkg = template.process(html, vars);
            samplesMillis[i] = (System.nanoTime() - start) / 1_000_000L;
            assertNotNull(pkg);
        }

        long medianMillis = median(samplesMillis);
        LOG.debug("WordprocessingMLHtmlTemplate render {}: {} ms median over {} renders",
                label, medianMillis, measuredRenders);
        assertTrue(medianMillis < maxMedianMillis,
                "HTML render (" + label + ") median " + medianMillis + " ms exceeded bound "
                        + maxMedianMillis + " ms — check the XHTML import pipeline for regressions");
    }

    /** Builds a full HTML document with inline-styled paragraphs of at least {@code targetBytes}. */
    static String htmlOfAtLeast(int targetBytes) {
        StringBuilder sb = new StringBuilder(targetBytes + 512);
        sb.append("<html><head><title>perf</title></head><body>");
        int paragraph = 0;
        while (sb.length() < targetBytes) {
            sb.append("<p style=\"margin:4px\">Paragraph ").append(paragraph++)
                    .append(": lorem ipsum dolor sit amet, consectetur adipiscing elit. ")
                    .append("<b>bold</b> and <i>italic</i> and <span style=\"color:#333\">span</span> text.</p>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    static long median(long[] samples) {
        long[] sorted = samples.clone();
        Arrays.sort(sorted);
        int mid = sorted.length / 2;
        return (sorted.length % 2 == 1) ? sorted[mid] : (sorted[mid - 1] + sorted[mid]) / 2L;
    }
}
