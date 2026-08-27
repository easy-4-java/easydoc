package io.github.easy4j.doc.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.utils.Docx4jUtils;

/**
 * 无路径导出重载的临时文件命名与后缀回归测试（P1，审计项 #14）。
 *
 * <p>修复背景：临时文件名原基于毫秒时间戳（{@code currentTimeMillis()}），
 * 同毫秒并发调用会生成相同路径前缀导致互相覆盖。</p>
 */
@DisplayName("Writer temp output: collision-free naming")
class WriterTempOutputRegressionTest {

    @Test
    @DisplayName("newTempOutputFile creates an existing unique file with the requested suffix")
    void newTempOutputFileCreatesUniqueFile() throws Exception {
        Set<String> names = new HashSet<>();
        for (String suffix : new String[] { ".docx", ".html", ".pdf" }) {
            File f = Docx4jUtils.newTempOutputFile(suffix);
            assertNotNull(f);
            assertTrue(f.isFile(), "createTempFile 产出的应是真实文件");
            assertTrue(f.getName().endsWith(suffix), "文件名应以请求的后缀结尾");
            assertTrue(names.add(f.getName()), "多次创建不得重名: " + f.getName());
            assertTrue(f.delete(), "文件应可删除");
        }
    }

    @Test
    @DisplayName("getTempPath stays collision-free under rapid successive calls")
    void getTempPathUniqueUnderTightLoop() {
        Set<String> paths = new HashSet<>();
        // 旧实现基于 currentTimeMillis，紧循环内极易重复（同毫秒）
        for (int i = 0; i < 50; i++) {
            String path = Docx4jUtils.getTempPath();
            assertTrue(path.startsWith(System.getProperty("java.io.tmpdir")),
                    "仍须位于系统临时目录内");
            assertTrue(paths.add(path), "getTempPath 紧循环内不得产生重复前缀");
        }
    }
}
