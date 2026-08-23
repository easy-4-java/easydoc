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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easy4j.doc.utils.Docx4jUtils;

/**
 * Timing regression test for {@link Docx4jUtils#mergeDocx(List)}.
 *
 * <p>{@code mergeDocx} owns a temp-file lifecycle (stream copy → load → save →
 * re-open, with {@code deleteOnExit()} added by review fix C-4). This test
 * times the single-document round-trip to make sure the temp file handling did
 * not regress the cost of a merge.</p>
 *
 * <p>Plain JUnit wall-clock timing, 2 warmup + 10 measured runs, median
 * asserted against a bound roughly 5x observed to avoid CI flakes.</p>
 *
 * <p>Measured baseline (JDK 21, Apple Silicon, surefire):
 *   - mergeDocx(single stream): ~4 ms per round-trip (median over 10 runs)
 * The asserted bound is 100 ms (~25x headroom).</p>
 */
class Docx4jUtilsPerfTest {

    private static final Logger LOG = LoggerFactory.getLogger(Docx4jUtilsPerfTest.class);

    private static final int WARMUP_RUNS = 2;
    private static final int MEASURED_RUNS = 10;
    /** 100 ms per single-stream merge round-trip — generous bound (~25x observed). */
    private static final long MAX_MEDIAN_MICROS = 100_000L;

    @Test
    @Timeout(value = 120)
    void mergeDocxSingleStreamMedianStaysUnderBound(@TempDir Path tempDir) throws Exception {
        Path sourceDocx = tempDir.resolve("perf-source.docx");
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        pkg.getMainDocumentPart().addParagraphOfText("Docx4jUtilsPerfTest merge round-trip");
        pkg.save(sourceDocx.toFile());

        Docx4jUtils utils = new Docx4jUtils();

        for (int i = 0; i < WARMUP_RUNS; i++) {
            mergeOnce(utils, sourceDocx);
        }

        long[] samplesMicros = new long[MEASURED_RUNS];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            long start = System.nanoTime();
            try (InputStream merged = mergeOnce(utils, sourceDocx)) {
                samplesMicros[i] = (System.nanoTime() - start) / 1_000L;
            }
        }

        long medianMicros = WmlTableUtilsPerfTest.median(samplesMicros);
        LOG.debug("mergeDocx(single stream) median per round-trip: {} us over {} runs",
                medianMicros, MEASURED_RUNS);
        assertTrue(medianMicros < MAX_MEDIAN_MICROS,
                "mergeDocx median round-trip " + medianMicros + " us exceeded bound "
                        + MAX_MEDIAN_MICROS + " us — check the temp file lifecycle for regressions");
    }

    private static InputStream mergeOnce(Docx4jUtils utils, Path sourceDocx) throws Exception {
        InputStream merged = utils.mergeDocx(List.of(Files.newInputStream(sourceDocx)));
        assertTrue(merged != null, "mergeDocx must return a stream for a non-empty input list");
        assertTrue(merged.read() != -1, "merged stream must not be empty");
        return merged;
    }
}
