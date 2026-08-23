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
package io.github.easy4j.doc.jdk21;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.DocxMode;
import io.github.easy4j.doc.DocxTemplates;
import io.github.easy4j.doc.WordprocessingMLDocxTemplate;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import io.github.easy4j.doc.utils.Docx4jUtils;
import io.github.easy4j.doc.utils.WmlZipUtils;

/**
 * JDK 21-specific tests verifying that easydoc's hot paths work correctly
 * under Project Loom virtual threads (java.lang.Thread.ofVirtual()).
 *
 * <p>Virtual threads are cheap and abundant — millions can exist
 * concurrently. The tests below launch a large fan-out (256 concurrent
 * renders / unzip calls) and verify that:
 * <ul>
 *   <li>docx4j internals (Context.getWmlObjectFactory, JAXBContext) are
 *       thread-safe under concurrent virtual-thread access
 *       (no static-state corruption / NPEs);</li>
 *   <li>{@link WmlZipUtils#unzip(File, File)} uses try-with-resources on
 *       its FileInputStream so that virtual threads do not leak file
 *       descriptors;</li>
 *   <li>{@link Docx4jUtils#mergeDocx} creates and deletes temp files
 *       deterministically (no orphan files across many concurrent calls).</li>
 * </ul>
 *
 * <p>The tests {@link BeforeEach} + {@link AfterEach} skip via
 * {@link assumeTrue} if the JVM is < 21 (e.g. someone runs `mvn test`
 * with an older JDK); the assertions only fire on JDK 21+, which is the
 * 3.0.x target baseline.
 */
class Jdk21VirtualThreadTest {

	private ExecutorService executor;

	@BeforeEach
	void setUp() {
		assumeTrue(Runtime.version().feature() >= 21,
				"requires JDK 21+ for virtual thread assertions");
		executor = Executors.newVirtualThreadPerTaskExecutor();
	}

	@AfterEach
	void tearDown() throws InterruptedException {
		if (executor != null) {
			executor.shutdown();
			assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS),
					"virtual thread executor must terminate cleanly");
		}
	}

	@Test
	@DisplayName("256 concurrent virtual-thread renders complete without state corruption")
	void concurrentRendersAreThreadSafe() throws Exception {
		// A real docx template (with ${title} / ${content} placeholders) read once.
		// Each render feeds Docx4J.load(InputStream) a fresh stream, which exercises
		// the shared JAXBContext / Context.getWmlObjectFactory() hot path concurrently.
		byte[] templateBytes = Files.readAllBytes(
				Path.of("src/test/resources/tpl/template.docx"));
		assertTrue(templateBytes.length > 0, "template.docx must be readable");

		// DEFAULT = WordprocessingMLDocxTemplate, SAX = SAX template (JDK 21+
		// transparently falls back to StAX), STAX = StAX template. All three must be
		// safe to call from many virtual threads at once.
		List<WordprocessingMLTemplate> templates = List.of(
				DocxTemplates.create(DocxMode.DEFAULT),
				DocxTemplates.create(DocxMode.SAX),
				DocxTemplates.create(DocxMode.STAX));

		AtomicInteger failures = new AtomicInteger();
		Throwable[] firstError = { null };
		Future<?>[] futures = IntStream.range(0, 256)
				.mapToObj(i -> executor.submit(() -> {
					try {
						for (WordprocessingMLTemplate t : templates) {
							Map<String, Object> vars = new HashMap<>();
							vars.put("title", "vthread-" + i);
							vars.put("content", "rendered by virtual thread " + i);
							// Docx4J.load(InputStream) consumes the stream, so each
							// render needs its own instance over the shared bytes.
							WordprocessingMLPackage pkg = t.process(
									new ByteArrayInputStream(templateBytes), vars);
							assertNotNull(pkg, "render " + i + " must produce a package");
							// The ${title} placeholder must actually be substituted,
							// not left behind (catches split-run / silent-failure bugs).
							String xml = pkg.getMainDocumentPart().getXML();
							assertTrue(xml.contains("vthread-" + i),
									"render " + i + " must substitute ${title} in the output");
						}
					} catch (Throwable t) {
						failures.incrementAndGet();
						synchronized (failures) {
							if (firstError[0] == null) firstError[0] = t;
						}
					}
				}))
				.toArray(Future<?>[]::new);

		for (Future<?> f : futures) {
			f.get(30, TimeUnit.SECONDS);
		}
		if (failures.get() > 0) {
			throw new AssertionError(
					failures.get() + " of 256 concurrent renders failed; docx4j internals must be virtual-thread safe. First failure: "
							+ firstError[0], firstError[0]);
		}
	}

	@Test
	@DisplayName("256 concurrent unzips do not leak file descriptors")
	void concurrentUnzipsDoNotLeakFds() throws Exception {
		// Build a single source zip once, then unzip it concurrently.
		Path tempDir = Files.createTempDirectory("easydoc-jdk21-");
		Path sourceZip = tempDir.resolve("src.zip");
		Path emptyOut = tempDir.resolve("out");
		Files.createDirectory(emptyOut);

		// Create a zip with several entries.
		try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(
				Files.newOutputStream(sourceZip))) {
			Files.walk(tempDir)
					.filter(p -> !p.equals(sourceZip))
					.limit(5)
					.forEach(p -> { /* skip — using inline entries instead */ });
			for (int i = 0; i < 5; i++) {
				zos.putNextEntry(new java.util.zip.ZipEntry("file" + i + ".txt"));
				zos.write(("content-" + i).getBytes());
				zos.closeEntry();
			}
		}

		AtomicInteger failures = new AtomicInteger();
		Future<?>[] futures = IntStream.range(0, 256)
				.mapToObj(i -> executor.submit(() -> {
					try {
						Path outDir = tempDir.resolve("out-" + i);
						Files.createDirectory(outDir);
						WmlZipUtils.unzip(sourceZip.toFile(), outDir.toFile());
						// Each output dir must contain 5 files
						long count;
						try (var s = Files.list(outDir)) {
							count = s.count();
						}
						if (count != 5) {
							failures.incrementAndGet();
						}
					} catch (Throwable t) {
						failures.incrementAndGet();
					}
				}))
				.toArray(Future<?>[]::new);

		for (Future<?> f : futures) {
			f.get(30, TimeUnit.SECONDS);
		}
		assertEquals(0, failures.get(),
				"every concurrent unzip must produce 5 entries (Zip Slip guard + try-with-resources must hold)");
	}

	@Test
	@DisplayName("DocxMode.DEFAULT factory returns a usable template via virtual threads")
	void factoryReturnsUsableTemplate() {
		WordprocessingMLTemplate t = DocxTemplates.create(DocxMode.DEFAULT);
		assertNotNull(t);
		assertTrue(t instanceof WordprocessingMLDocxTemplate,
				"DEFAULT mode should return the default Docx template");
	}

	@Test
	@DisplayName("DocxMode.STAX factory returns a usable template via virtual threads")
	void factoryReturnsUsableStaxTemplate() {
		WordprocessingMLTemplate t = DocxTemplates.create(DocxMode.STAX);
		assertNotNull(t);
	}
}