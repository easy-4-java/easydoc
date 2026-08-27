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
package io.github.easy4j.doc.thymeleaf.perf;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import java.io.File;

import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easy4j.doc.thymeleaf.WordprocessingMLThymeleafTemplate;

/**
 * Timing regression for the Thymeleaf engine render step and the end-to-end
 * template adapter path ({@code process}).
 *
 * <p>Mirrors {@code WordprocessingMLThymeleafHelloTest}: the test-resource
 * {@code docx4j.properties} configures a UrlTemplateResolver with a file
 * system prefix which does not resolve classpath templates, so both tests
 * switch to {@code ClassLoaderTemplateResolver} with an empty prefix for the
 * duration of the class (restored afterwards).</p>
 *
 * <p>{@code render(String, Map)} is protected, so a local subclass widens it
 * to public for direct timing. Plain JUnit wall-clock timing: 50 warmup +
 * 1 000 measured renders of {@code /tpl/hello.html}; the median per call is
 * asserted against a bound roughly 5x+ observed.</p>
 *
 * <p>Measured baseline (JDK 21, Apple Silicon, surefire):
 *   - render /tpl/hello.html: ~34 us per call (median over 1 000 renders)
 *   - process /tpl/hello.html (end-to-end docx): ~10 ms per call (median of 3)</p>
 */
// 绝对时间门（render 中位数上限）：对持续高负载敏感，建议以
// -DexcludedGroups=perf-absolute 移出常规 CI，由专用 perf 任务
// 以 -Dgroups=perf-absolute 单独调度（审计 #28）。
@Tag("perf-absolute")
class WordprocessingMLThymeleafRenderPerfTest {

    private static final Logger LOG = LoggerFactory.getLogger(WordprocessingMLThymeleafRenderPerfTest.class);

    private static final String TEMPLATE = "/tpl/hello.html";

    private static final int RENDER_WARMUP = 50;
    private static final int RENDER_MEASURED = 1_000;
    private static final long MAX_MEDIAN_RENDER_MICROS = 50_000L;

    private static final int PROCESS_MEASURED = 3;
    /** 端到端负载容忍倍率：hello 全流程耗时上限 = 该倍率 x 同机“空包 create+save”基线。
     *  基线走同一条 JAXB 编组 + zip 输出重路径，整机变慢时分子分母同比放大，比值稳定；
     *  表达式求值/模板渲染一旦数量级退化，只有分子抬升（审计 #28）。 */
    private static final double MAX_EMPTY_TO_HELLO_RATIO = 25d;

    private static String prevResolver;
    private static String prevPrefix;

    @BeforeAll
    static void switchToClassLoaderResolver() {
        // 测试资源 docx4j.properties 配置 UrlTemplateResolver + 文件系统 prefix，
        // 对 classpath 模板不适用；切换为 ClassLoaderTemplateResolver 并清空 prefix
        Properties props = Docx4jProperties.getProperties();
        prevResolver = props.getProperty("docx4j.thymeleaf.templateResolver");
        prevPrefix = props.getProperty("docx4j.thymeleaf.prefix");
        props.setProperty("docx4j.thymeleaf.templateResolver",
                "org.thymeleaf.templateresolver.ClassLoaderTemplateResolver");
        props.setProperty("docx4j.thymeleaf.prefix", "");
    }

    @AfterAll
    static void restoreResolver() {
        Properties props = Docx4jProperties.getProperties();
        props.remove("docx4j.thymeleaf.templateResolver");
        props.remove("docx4j.thymeleaf.prefix");
        if (prevResolver != null) {
            props.setProperty("docx4j.thymeleaf.templateResolver", prevResolver);
        }
        if (prevPrefix != null) {
            props.setProperty("docx4j.thymeleaf.prefix", prevPrefix);
        }
    }

    /** Local subclass exposing the protected engine render step for direct timing. */
    private static class ExposedTemplate extends WordprocessingMLThymeleafTemplate {
        @Override
        public String render(String template, Map<String, Object> variables) throws Exception {
            return super.render(template, variables);
        }
    }

    @Test
    @Timeout(value = 60)
    void renderHelloTemplateUnderBound() throws Exception {
        ExposedTemplate t = new ExposedTemplate();
        Map<String, Object> vars = Map.of("name", "world");

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
        LOG.debug("thymeleaf render {}: {} us median over {} renders", TEMPLATE, medianMicros, RENDER_MEASURED);
        assertTrue(out.contains("Hello world"));
        assertTrue(medianMicros < MAX_MEDIAN_RENDER_MICROS,
                "thymeleaf render median " + medianMicros + " us exceeded bound "
                        + MAX_MEDIAN_RENDER_MICROS + " us");
    }

    @Test
    @Timeout(value = 120)
    void processHelloTemplateEndToEndLoadTolerantBound(@TempDir Path tempDir) throws Exception {
        WordprocessingMLThymeleafTemplate t = new WordprocessingMLThymeleafTemplate();
        Map<String, Object> vars = Map.of("name", "world");

        // warmup (engine + docx pipeline init)
        assertNotNull(t.process(TEMPLATE, vars));

        long[] samplesMillis = new long[PROCESS_MEASURED];
        for (int i = 0; i < PROCESS_MEASURED; i++) {
            long start = System.nanoTime();
            WordprocessingMLPackage pkg = t.process(TEMPLATE, vars);
            samplesMillis[i] = (System.nanoTime() - start) / 1_000_000L;
            assertNotNull(pkg);
        }
        long medianHello = median(samplesMillis);
        long medianEmpty = median(emptyPackageSaveSamples(tempDir));
        LOG.debug("thymeleaf process {} end-to-end: {} ms median vs empty-save {} ms (limit factor {})",
                TEMPLATE, medianHello, medianEmpty, MAX_EMPTY_TO_HELLO_RATIO);
        assertTrue(medianHello < MAX_EMPTY_TO_HELLO_RATIO * Math.max(1L, medianEmpty),
                "thymeleaf hello-process median " + medianHello + " ms is "
                        + String.format("%.1fx", (double) medianHello / Math.max(1L, medianEmpty))
                        + " of an equal-count empty-package save — pipeline regression");
    }

    /** 基线：与被测路径同 JVM 的“空文档 create+save”中位数样本。 */
    private static long[] emptyPackageSaveSamples(Path tempDir) throws Exception {
        long[] samples = new long[PROCESS_MEASURED];
        File dir = tempDir.toFile();
        for (int i = 0; i < PROCESS_MEASURED; i++) {
            File out = new File(dir, "empty-baseline-" + i + ".docx");
            long start = System.nanoTime();
            WordprocessingMLPackage.createPackage().save(out);
            samples[i] = (System.nanoTime() - start) / 1_000_000L;
            assertTrue(out.delete() || !out.exists(), "baseline file must be deletable");
        }
        return samples;
    }

    private static long median(long[] samples) {
        long[] sorted = samples.clone();
        Arrays.sort(sorted);
        int mid = sorted.length / 2;
        return (sorted.length % 2 == 1) ? sorted[mid] : (sorted[mid - 1] + sorted[mid]) / 2L;
    }
}
