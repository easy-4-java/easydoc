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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.DocxMode;
import io.github.easy4j.doc.DocxTemplates;
import io.github.easy4j.doc.WordprocessingMLTemplate;

/**
 * JDK 21-era batch-render coordination. Originally designed against the
 * preview {@code java.util.concurrent.StructuredTaskScope} API; the current
 * JDK toolchain rejects {@code --enable-preview} for source release 21
 * ("only release 26 supports preview language features"), so this suite
 * exercises the same coordination semantics with the stable
 * {@code Executors.newVirtualThreadPerTaskExecutor} + {@link CompletableFuture}
 * primitives available since JDK 21.
 *
 * <p>Coverage:
 * <ul>
 *   <li>{@code allRendersSucceedWithCompletableFuture} — fan-out 32 renders
 *       and join via {@code CompletableFuture.allOf}; first error aborts the
 *       join via the standard CompletionException contract.</li>
 *   <li>{@code firstFailurePropagates} — assert that a thrown render surfaces
 *       as {@code CompletionException} wrapping the original cause.</li>
 *   <li>{@code threadInterruptPropagatesToVirtualThread} — direct
 *       {@code Thread.interrupt()} on a virtual thread must reach its
 *       {@code Thread.sleep} and surface as {@code InterruptedException}.</li>
 *   <li>{@code joinWithTimeoutBoundsTheWait} — {@code Future.get(timeout)}
 *       bounds the wait even if a fork never completes.</li>
 * </ul>
 *
 * <p>All tests {@link org.junit.jupiter.api.Assumptions#assumeTrue assumeTrue}
 * that the JVM is 21+; older JVMs skip the suite silently.
 */
class Jdk21StructuredTaskScopeTest {

	private ExecutorService executor;

	@BeforeEach
	void setUp() {
		assumeTrue(Runtime.version().feature() >= 21,
				"requires JDK 21+ virtual threads");
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

	private static byte[] loadTemplate() throws Exception {
		return Files.readAllBytes(Path.of("src/test/resources/tpl/template.docx"));
	}

	private static Map<String, Object> vars(int i) {
		Map<String, Object> v = new HashMap<>();
		v.put("title", "scope-" + i);
		v.put("content", "structured-task-scope virtual thread " + i);
		return v;
	}

	@Test
	@DisplayName("32 concurrent renders all succeed via CompletableFuture.allOf")
	void allRendersSucceedWithCompletableFuture() throws Exception {
		byte[] templateBytes = loadTemplate();
		WordprocessingMLTemplate t = DocxTemplates.create(DocxMode.STAX);

		AtomicInteger completed = new AtomicInteger();
		List<CompletableFuture<WordprocessingMLPackage>> futures = IntStream.range(0, 32)
				.mapToObj(i -> CompletableFuture.supplyAsync(() -> {
					try {
						WordprocessingMLPackage pkg = t.process(
								new ByteArrayInputStream(templateBytes), vars(i));
						assertNotNull(pkg);
						String xml = pkg.getMainDocumentPart().getXML();
						assertTrue(xml.contains("scope-" + i),
								"render " + i + " must contain 'scope-" + i + "'");
						completed.incrementAndGet();
						return pkg;
					} catch (Exception e) {
						throw new CompletionException(e);
					}
				}, executor))
				.toList();

		CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

		assertEquals(32, completed.get(), "all 32 renders must complete");
		for (CompletableFuture<WordprocessingMLPackage> f : futures) {
			assertTrue(f.isDone(), "each future must be done after allOf().join()");
			assertNotNull(f.get(), "each future must carry a WordprocessingMLPackage");
		}
	}

	@Test
	@DisplayName("First failure propagates as CompletionException with the original cause")
	void firstFailurePropagates() throws Exception {
		assumeTrue(Runtime.version().feature() >= 21,
				"requires JDK 21+ virtual threads");
		byte[] templateBytes = loadTemplate();
		WordprocessingMLTemplate t = DocxTemplates.create(DocxMode.STAX);

		CompletableFuture<Void> firstFailure = CompletableFuture.runAsync(() -> {
			throw new IllegalStateException("intentional first failure");
		}, executor);
		List<CompletableFuture<WordprocessingMLPackage>> slow = IntStream.range(0, 16)
				.mapToObj(i -> CompletableFuture.supplyAsync(() -> {
					try {
						return t.process(new ByteArrayInputStream(templateBytes), vars(i));
					} catch (Exception e) {
						throw new CompletionException(e);
					}
				}, executor))
				.toList();

		CompletableFuture<Void> all = CompletableFuture.allOf(
				Stream.concat(Stream.of(firstFailure),
						slow.stream().map(c -> (CompletableFuture<?>) c))
						.toArray(CompletableFuture[]::new));

		CompletionException ce = assertThrows(CompletionException.class, all::join,
				"anyOf must surface the first failure");
		assertNotNull(ce.getCause(), "CompletionException must wrap a cause");
		assertTrue(ce.getCause() instanceof IllegalStateException,
				"cause must be the original IllegalStateException, got " + ce.getCause());
		assertEquals("intentional first failure", ce.getCause().getMessage());
	}

	@Test
	@DisplayName("Thread.interrupt() reaches a sleeping virtual thread and surfaces as InterruptedException")
	void threadInterruptPropagatesToVirtualThread() throws Exception {
		assumeTrue(Runtime.version().feature() >= 21,
				"requires JDK 21+ virtual threads");

		// Start virtual threads directly via Thread.startVirtualThread() and hold the
		// Thread references — calling Thread.interrupt() is the most reliable way to
		// verify interrupt propagation in JDK 21 (Future.cancel(true) through
		// newVirtualThreadPerTaskExecutor has weaker immediate guarantees).
		AtomicInteger interrupted = new AtomicInteger();
		List<Thread> vts = new java.util.ArrayList<>();
		for (int i = 0; i < 16; i++) {
			Thread vt = Thread.startVirtualThread(() -> {
				try {
					while (!Thread.currentThread().isInterrupted()) {
						Thread.sleep(200);
					}
				} catch (InterruptedException ie) {
					interrupted.incrementAndGet();
					Thread.currentThread().interrupt();
				}
			});
			vts.add(vt);
		}

		// Give them a moment to enter the sleep loop.
		Thread.sleep(50);

		for (Thread vt : vts) {
			vt.interrupt();
		}

		long deadline = System.nanoTime() + Duration.ofSeconds(3).toNanos();
		while (System.nanoTime() < deadline && interrupted.get() < 8) {
			Thread.sleep(20);
		}
		assertTrue(interrupted.get() >= 8,
				"Thread.interrupt() must propagate to at least 8 of 16 sleeping virtual threads; got "
						+ interrupted.get());
	}

	@Test
	@DisplayName("Future.get(timeout) bounds the wait even if a fork never completes")
	void joinWithTimeoutBoundsTheWait() throws Exception {
		assumeTrue(Runtime.version().feature() >= 21,
				"requires JDK 21+ virtual threads");

		Future<Void> sleeping = executor.submit(() -> {
			Thread.sleep(Duration.ofMinutes(10).toMillis());
			return null;
		});

		long started = System.nanoTime();
		assertThrows(TimeoutException.class,
				() -> sleeping.get(500, TimeUnit.MILLISECONDS),
				"Future.get(timeout) must throw TimeoutException for a 10-min sleeping fork");
		long elapsedMs = (System.nanoTime() - started) / 1_000_000L;

		sleeping.cancel(true);
		assertTrue(elapsedMs < 5_000L,
				"Future.get(timeout) must return within ~500ms, not wait for the 10-min sleep; "
						+ "actual elapsed=" + elapsedMs + "ms");
	}
}