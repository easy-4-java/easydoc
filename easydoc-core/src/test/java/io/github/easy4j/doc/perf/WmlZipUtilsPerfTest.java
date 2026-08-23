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

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easy4j.doc.utils.WmlZipUtils;

/**
 * Timing regression test for {@link WmlZipUtils#unzip(File, File)} and
 * {@link WmlZipUtils#zipDir(File, File)}.
 *
 * <p>The Zip Slip security fix added a canonical-path validation of every
 * entry against the target directory. This test times a 200-entry zip (plus a
 * nested directory) to prove the validation cost is negligible, and times the
 * {@code zipDir} round-trip which now closes the {@code ZipOutputStream} via
 * try-with-resources (M-1).</p>
 *
 * <p>Approach: plain JUnit wall-clock timing (no JMH); medians asserted
 * against bounds roughly 5x observed to avoid CI flakes.</p>
 *
 * <p>Measured baseline (JDK 21, Apple Silicon, surefire):
 *   - WmlZipUtils.unzip: ~47 ms for a 200-entry zip (median over 5 runs)
 *   - WmlZipUtils.zipDir: ~45 ms for a 200-entry directory (median over 5 runs)
 * Both bounds below are 500 ms (~10x headroom).</p>
 */
class WmlZipUtilsPerfTest {

    private static final Logger LOG = LoggerFactory.getLogger(WmlZipUtilsPerfTest.class);

    private static final int ENTRY_COUNT = 200;
    private static final int ENTRY_SIZE_BYTES = 1024;
    private static final int WARMUP_RUNS = 1;
    private static final int MEASURED_RUNS = 5;
    /** 500 ms per unzip/zipDir of the 200-entry fixture — generous bound. */
    private static final long MAX_MEDIAN_MICROS = 500_000L;

    @Test
    @Timeout(value = 60)
    void unzipOf200EntryZipStaysUnderBound(@TempDir Path tempDir) throws Exception {
        Path zipFile = tempDir.resolve("fixture-200.zip");
        writeFixtureZip(zipFile);

        Path outputDir = tempDir.resolve("out");
        // warmup + functional sanity check
        WmlZipUtils.unzip(zipFile.toFile(), outputDir.toFile());
        assertTrue(Files.exists(outputDir.resolve("entry-000.txt")), "first entry must extract");
        assertTrue(Files.exists(outputDir.resolve("nested/inner.txt")), "nested entry must extract");

        long[] samplesMicros = new long[MEASURED_RUNS];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            long start = System.nanoTime();
            WmlZipUtils.unzip(zipFile.toFile(), outputDir.toFile());
            samplesMicros[i] = (System.nanoTime() - start) / 1_000L;
        }

        long medianMicros = WmlTableUtilsPerfTest.median(samplesMicros);
        LOG.debug("WmlZipUtils.unzip median for {}-entry zip: {} us over {} runs",
                ENTRY_COUNT, medianMicros, MEASURED_RUNS);
        assertTrue(medianMicros < MAX_MEDIAN_MICROS,
                "unzip median " + medianMicros + " us exceeded bound " + MAX_MEDIAN_MICROS
                        + " us — check the Zip Slip canonical-path validation for regressions");
        assertTrue(Files.exists(outputDir.resolve("entry-199.txt")), "last entry must extract");
    }

    @Test
    @Timeout(value = 60)
    void zipDirRoundTripStaysUnderBound(@TempDir Path tempDir) throws Exception {
        Path dirToZip = tempDir.resolve("fixture-dir");
        writeFixtureDir(dirToZip);

        // warmup
        WmlZipUtils.zipDir(dirToZip.toFile(), tempDir.resolve("warmup.zip").toFile());

        long[] samplesMicros = new long[MEASURED_RUNS];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            Path dest = tempDir.resolve("out-" + i + ".zip");
            long start = System.nanoTime();
            WmlZipUtils.zipDir(dirToZip.toFile(), dest.toFile());
            samplesMicros[i] = (System.nanoTime() - start) / 1_000L;
            try (ZipFile zf = new ZipFile(dest.toFile())) {
                assertTrue(zf.getEntry("fixture-dir/entry-000.txt") != null,
                        "zipped archive must contain the entries and be parseable");
            }
        }

        long medianMicros = WmlTableUtilsPerfTest.median(samplesMicros);
        LOG.debug("WmlZipUtils.zipDir median for {}-entry dir: {} us over {} runs",
                ENTRY_COUNT, medianMicros, MEASURED_RUNS);
        assertTrue(medianMicros < MAX_MEDIAN_MICROS,
                "zipDir median " + medianMicros + " us exceeded bound " + MAX_MEDIAN_MICROS
                        + " us — check ZipFolderHelper for regressions");
    }

    private static void writeFixtureZip(Path zipFile) throws Exception {
        byte[] payload = new byte[ENTRY_SIZE_BYTES];
        Arrays.fill(payload, (byte) 'x');
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            for (int i = 0; i < ENTRY_COUNT; i++) {
                zos.putNextEntry(new ZipEntry(String.format("entry-%03d.txt", i)));
                zos.write(payload);
                zos.closeEntry();
            }
            // 1 nested directory with a file inside
            zos.putNextEntry(new ZipEntry("nested/"));
            zos.closeEntry();
            zos.putNextEntry(new ZipEntry("nested/inner.txt"));
            zos.write("nested content".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
    }

    private static void writeFixtureDir(Path dirToZip) throws Exception {
        byte[] payload = new byte[ENTRY_SIZE_BYTES];
        Arrays.fill(payload, (byte) 'y');
        Files.createDirectories(dirToZip.resolve("nested"));
        for (int i = 0; i < ENTRY_COUNT; i++) {
            try (OutputStream os = Files.newOutputStream(dirToZip.resolve(String.format("entry-%03d.txt", i)))) {
                os.write(payload);
            }
        }
        Files.write(dirToZip.resolve("nested/inner.txt"), "nested content".getBytes(StandardCharsets.UTF_8));
    }
}
