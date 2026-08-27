package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * {@link WmlZipUtils} 并发与资源安全回归测试（P1，审计项 #14）。
 *
 * <p>修复背景：WmlZipUtils 持有共享静态 {@code ZipFolderHelper}，
 * 其 includeInitialFolder 为可变字段。旧实现“set 标志 → process”两步之间
 * 无任何同步，两个线程并发调用 zipDir 时标志位可能互相覆盖，
 * 导致某一方压缩包的目录结构错误。</p>
 */
@DisplayName("WmlZipUtils concurrency regression tests")
class WmlZipUtilsConcurrencyRegressionTest {

    @TempDir
    Path tempDir;

    private Path createSourceTree(String top) throws Exception {
        Path src = tempDir.resolve(top);
        Files.createDirectories(src.resolve("sub"));
        Files.writeString(src.resolve("a.txt"), "content-a");
        Files.writeString(src.resolve("sub").resolve("b.txt"), "content-b");
        return src;
    }

    private static Set<String> readEntryNames(Path zip) throws Exception {
        Set<String> names = new HashSet<>();
        try (ZipFile zf = new ZipFile(zip.toFile())) {
            for (Object e : Collections.list(zf.entries())) {
                names.add(((ZipEntry) e).getName());
            }
        }
        return names;
    }

    @Test
    @DisplayName("concurrent zipDir with different flags must not cross-contaminate output layout")
    void concurrentZipDirFlagsDoNotInterfere() throws Exception {
        int rounds = 10;
        for (int round = 0; round < rounds; round++) {
            Path src = createSourceTree("top" + round);
            Path keep = tempDir.resolve("keep-" + round + ".zip");
            Path strip = tempDir.resolve("strip-" + round + ".zip");

            CyclicBarrier barrier = new CyclicBarrier(2);
            Thread t1 = new Thread(() -> zipAt(barrier, src, keep, true), "zip-keep-" + round);
            Thread t2 = new Thread(() -> zipAt(barrier, src, strip, false), "zip-strip-" + round);
            t1.start();
            t2.start();
            t1.join();
            t2.join();

            // includeInitialFolder=true：条目应全部带顶层目录前缀
            Set<String> keepNames = readEntryNames(keep);
            assertFalse(keepNames.isEmpty(), "keep 压缩包不应为空");
            for (String n : keepNames) {
                assertTrue(n.startsWith("top" + round),
                        "includeInitialFolder=true 时条目应带顶层前缀, 得到: " + n);
            }
            // includeInitialFolder=false：条目应剥掉顶层目录
            Set<String> stripNames = readEntryNames(strip);
            assertFalse(stripNames.isEmpty(), "strip 压缩包不应为空");
            for (String n : stripNames) {
                assertFalse(n.startsWith("top" + round),
                        "includeInitialFolder=false 时条目不应带顶层前缀, 得到: " + n);
            }
            assertTrue(stripNames.contains("a.txt") && stripNames.contains("sub/b.txt"),
                    "剥掉顶层后应直接包含 a.txt 与 sub/b.txt, 实际: " + stripNames);
        }
    }

    @Test
    @DisplayName("zip-then-unzip roundtrip remains intact after concurrent producers")
    void zipUnzipRoundtripAfterConcurrentZips() throws Exception {
        Path src = createSourceTree("roundtrip");
        Path zip = tempDir.resolve("roundtrip.zip");
        Path ignored = tempDir.resolve("ignored.zip");

        // 先并发各写一次（历史竞态高发路径），再单线程校验结果完整性
        CyclicBarrier barrier = new CyclicBarrier(2);
        Thread t1 = new Thread(() -> zipAt(barrier, src, zip, true), "zip-roundtrip");
        Thread t2 = new Thread(() -> zipAt(barrier, src, ignored, false), "zip-ignored");
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        Path out = tempDir.resolve("roundtrip-out");
        WmlZipUtils.unzip(zip.toFile(), out.toFile());
        assertTrue(Files.exists(out.resolve("roundtrip").resolve("a.txt")));
        Path b = out.resolve("roundtrip").resolve("sub").resolve("b.txt");
        assertTrue(Files.exists(b));
        assertEquals("content-b", Files.readString(b));
    }

    private static void zipAt(CyclicBarrier barrier, Path src, Path dest, boolean flag) {
        try {
            barrier.await();
            WmlZipUtils.zipDir(src.toFile(), dest.toFile(), flag);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
