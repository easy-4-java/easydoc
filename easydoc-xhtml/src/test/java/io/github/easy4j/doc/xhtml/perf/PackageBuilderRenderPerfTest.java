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

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easy4j.doc.xhtml.io.WordprocessingMLPackageBuilder;

/**
 * Timing regression for the {@link WordprocessingMLPackageBuilder} conversion
 * paths: {@code buildWithDoc(pkg, doc, altChunk)} (in-memory jsoup
 * {@link Document} into an existing package — no temp files) and
 * {@code buildWithXhtml(html, landscape, altChunk)} for small / medium HTML.
 *
 * <p>Plain JUnit wall-clock timing (no JMH); medians asserted against bounds
 * roughly 5x observed to avoid CI flakes.</p>
 *
 * <p>Measured baseline (JDK 21, Apple Silicon, surefire):
 *   - buildWithDoc (in-memory, small doc): ~3 ms (bound 500 ms)
 *   - buildWithXhtml (2 KB html): ~6 ms (bound 500 ms)
 *   - buildWithXhtml (10 KB html): ~15 ms (bound 1 500 ms)</p>
 */
// 绝对时间门（render 中位数上限）：对持续高负载敏感，建议以
// -DexcludedGroups=perf-absolute 移出常规 CI，由专用 perf 任务
// 以 -Dgroups=perf-absolute 单独调度（审计 #28）。
@Tag("perf-absolute")
class PackageBuilderRenderPerfTest {

    private static final Logger LOG = LoggerFactory.getLogger(PackageBuilderRenderPerfTest.class);

    private static final long MAX_MEDIAN_MILLIS_SMALL = 500L;
    private static final long MAX_MEDIAN_MILLIS_MEDIUM = 1_500L;

    private static final String SMALL_HTML =
            "<html><body><p>package builder perf small</p><p>second <b>paragraph</b></p></body></html>";

    @Test
    @Timeout(value = 120)
    void buildWithDocInMemoryUnderBound() throws Exception {
        WordprocessingMLPackageBuilder builder = WordprocessingMLPackageBuilder.getWMLPackageBuilder();
        Document doc = Jsoup.parse(SMALL_HTML);

        // warmup
        assertNotNull(builder.buildWithDoc(WordprocessingMLPackage.createPackage(), doc, false));

        long[] samples = new long[5];
        for (int i = 0; i < samples.length; i++) {
            WordprocessingMLPackage fresh = WordprocessingMLPackage.createPackage();
            long start = System.nanoTime();
            WordprocessingMLPackage pkg = builder.buildWithDoc(fresh, doc, false);
            samples[i] = (System.nanoTime() - start) / 1_000_000L;
            assertNotNull(pkg);
        }
        assertMedianUnderBound(samples, MAX_MEDIAN_MILLIS_SMALL, "buildWithDoc (in-memory, small)");
    }

    @Test
    @Timeout(value = 120)
    void buildWithXhtmlSmallHtmlUnderBound() throws Exception {
        timeBuildWithXhtml(SMALL_HTML, 5, MAX_MEDIAN_MILLIS_SMALL, "buildWithXhtml (small)");
    }

    @Test
    @Timeout(value = 180)
    void buildWithXhtmlMediumHtmlUnderBound() throws Exception {
        String mediumHtml = HtmlTemplateRenderPerfTest.htmlOfAtLeast(10 * 1024);
        timeBuildWithXhtml(mediumHtml, 3, MAX_MEDIAN_MILLIS_MEDIUM, "buildWithXhtml (10 KB)");
    }

    private static void timeBuildWithXhtml(String html, int measuredRenders, long maxMedianMillis, String label)
            throws Exception {
        WordprocessingMLPackageBuilder builder = WordprocessingMLPackageBuilder.getWMLPackageBuilder();

        // warmup
        assertNotNull(builder.buildWithXhtml(html, false, false));

        long[] samples = new long[measuredRenders];
        for (int i = 0; i < measuredRenders; i++) {
            long start = System.nanoTime();
            WordprocessingMLPackage pkg = builder.buildWithXhtml(html, false, false);
            samples[i] = (System.nanoTime() - start) / 1_000_000L;
            assertNotNull(pkg);
        }
        assertMedianUnderBound(samples, maxMedianMillis, label);
    }

    private static void assertMedianUnderBound(long[] samplesMillis, long maxMedianMillis, String label) {
        long medianMillis = HtmlTemplateRenderPerfTest.median(samplesMillis);
        LOG.debug("{}: {} ms median over {} renders", label, medianMillis, samplesMillis.length);
        assertTrue(medianMillis < maxMedianMillis,
                label + " median " + medianMillis + " ms exceeded bound " + maxMedianMillis
                        + " ms — check the XHTML import pipeline for regressions");
    }
}
