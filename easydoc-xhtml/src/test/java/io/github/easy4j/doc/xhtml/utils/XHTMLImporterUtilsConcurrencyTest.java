package io.github.easy4j.doc.xhtml.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 并发回归（audit 23）：{@link XHTMLImporterUtils#handle} 通过 JAXP 系统属性做
 * XXE 兜底，属性是 JVM 全局状态。多个线程并发转换后，调用方预先配置的属性值
 * 必须原样恢复（不得被永久覆盖或残留空串）；转换本身全部成功。
 *
 * <p>实现上以类级锁串行化属性窗口：并发吞吐换全局状态正确性。</p>
 */
class XHTMLImporterUtilsConcurrencyTest {

	private static final String DTD = "javax.xml.accessExternalDTD";
	private static final String SCHEMA = "javax.xml.accessExternalSchema";

	@AfterEach
	void clearProps() {
		System.clearProperty(DTD);
		System.clearProperty(SCHEMA);
	}

	private static Callable<String> conversionTask(final int index) {
		return () -> {
			WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
			XHTMLImporterUtils.handle(pkg,
					Jsoup.parse("<html><body><p>并发-" + index + "</p></body></html>"),
					true, false);
			int size = pkg.getMainDocumentPart().getContent().size();
			assertTrue(size > 0, "conversion " + index + " must produce content");
			return "ok-" + index;
		};
	}

	@Test
	@DisplayName("concurrent conversions restore caller-configured JAXP property values")
	void concurrentConversionsDoNotPermanentlyOverrideUserProperties() throws Exception {
		// 调用方预配置（模拟宿主应用对这两个属性的既有约定）
		System.setProperty(DTD, "file,http");
		System.setProperty(SCHEMA, "");

		int threads = 4;
		int perThread = 2;
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		try {
			List<Future<String>> futures = new ArrayList<Future<String>>();
			for (int i = 0; i < threads * perThread; i++) {
				final int index = i;
				futures.add(pool.submit(conversionTask(index)));
			}
			for (Future<String> future : futures) {
				assertEquals(true, future.get(60, TimeUnit.SECONDS).startsWith("ok-"),
						"every concurrent conversion succeeds");
			}
		} finally {
			pool.shutdownNow();
			pool.awaitTermination(30, TimeUnit.SECONDS);
		}

		// 全部完成后：用户配置值必须精确复原，无覆盖、无残留
		assertEquals("file,http", System.getProperty(DTD),
				"caller-configured DTD value survives concurrent handle() calls");
		assertEquals("", System.getProperty(SCHEMA),
				"caller-configured (empty) schema value survives as-is");
	}

	@Test
	@DisplayName("concurrent conversions leave clean state when caller had no properties")
	void concurrentConversionsLeaveCleanStateWhenNonePreexisting() throws Exception {
		System.clearProperty(DTD);
		System.clearProperty(SCHEMA);

		ExecutorService pool = Executors.newFixedThreadPool(3);
		try {
			List<Future<String>> futures = new ArrayList<Future<String>>();
			for (int i = 0; i < 6; i++) {
				futures.add(pool.submit(conversionTask(i)));
			}
			for (Future<String> future : futures) {
				future.get(60, TimeUnit.SECONDS);
			}
		} finally {
			pool.shutdownNow();
			pool.awaitTermination(30, TimeUnit.SECONDS);
		}

		org.junit.jupiter.api.Assertions.assertNull(System.getProperty(DTD),
				"DTD property must not leak after calls when caller had none");
		org.junit.jupiter.api.Assertions.assertNull(System.getProperty(SCHEMA),
				"schema property must not leak after calls when caller had none");
	}
}
