package io.github.easy4j.doc.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.utils.Docx4jUtils;

/**
 * 无路径导出重载的临时文件命名与后缀回归测试（P1，审计项 #14，port from 3.0.x）。
 *
 * <p>修复背景：临时文件名基于毫秒时间戳时，同毫秒并发调用会互相覆盖；
 * 现改用 {@code Files.createTempFile} 原子命名。无路径 docx 导出以
 * “easydoc-”前缀 + 唯一名产出（无路径 html 的 .pdf→.html 后缀修复见
 * WordprocessingMLPackageWriter 的 HTML_SUFFIX 提交）。</p>
 */
@DisplayName("Writer no-path overloads: unique temp naming and correct suffixes")
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

    @Test
    @DisplayName("writeToDocx(pkg) produces uniquely named .docx artifacts")
    void writeToDocxNoArgSuffixAndUniqueness() throws Exception {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File first = writer.writeToDocx(pkg);
        try {
            assertNotNull(first);
            assertTrue(first.getName().endsWith(".docx"), "无路径 docx 导出应以 .docx 结尾");
        } finally {
            first.delete();
        }
        File second = writer.writeToDocx(pkg);
        try {
            assertNotEquals(first.getName(), second.getName(), "两次导出文件名必须不同");
        } finally {
            second.delete();
        }
    }

    @Test
    @DisplayName("writeToHtml(pkg) produces an .html artifact (was .pdf before fix #13)")
    void writeToHtmlNoArgUsesHtmlSuffix() throws Exception {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        pkg.getMainDocumentPart().addParagraphOfText("easydoc html suffix regression");
        // 修复前：无路径重载把目标命名为 *.pdf（#13）。修复后必须是 *.html。
        File html = writer.writeToHtml(pkg);
        try {
            assertNotNull(html);
            assertTrue(html.getName().endsWith(".html"),
                    "无路径 html 导出应以 .html 结尾，实际为: " + html.getName());
            assertTrue(html.length() > 0, "html 输出不应为空");
            assertEquals(0, html.getName().indexOf("easydoc-"),
                    "临时输出应由 createTempFile 统一命名");
        } finally {
            html.delete();
        }
    }
}
