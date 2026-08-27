package io.github.easy4j.doc.io;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * {@link WordprocessingMLTemplateWriter#writeToFile} 资源修复回归测试（P1，审计项 #14，port from 3.0.x）。
 *
 * <p>修复背景：原实现裸 {@code new FileOutputStream(outFile)} 且从不关闭，
 * writeToFile 返回后流仍持有句柄（Windows 上会阻止文件被删除/重命名）。
 * 现已用 try-with-resources 包裹。</p>
 */
@DisplayName("TemplateWriter.writeToFile closes its stream and produces a valid docx")
class WordprocessingMLTemplateWriterWriteToFileRegressionTest {

    @Test
    void writeToFileProducesLoadableDocxAndAllowsCleanup(@TempDir java.nio.file.Path tempDir) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        pkg.getMainDocumentPart().addParagraphOfText("writeToFile regression");

        File outFile = tempDir.resolve("template-out.docx").toFile();
        WordprocessingMLTemplateWriter.writeToFile(pkg, outFile);

        assertTrue(outFile.isFile(), "输出文件应存在");
        assertTrue(outFile.length() > 0, "输出文件不应为空");

        // 产物必须是可重新加载的合法 docx（ZIP 容器）
        WordprocessingMLPackage reloaded = WordprocessingMLPackage.load(outFile);
        assertTrue(reloaded.getMainDocumentPart().getXML().contains("writeToFile regression"),
                "重载后的文档应包含写入的段落文本");

        // 修复前：流未关闭，Windows 上 delete 会因句柄占用而失败；
        // 修复后：本地 FileOutputStream 已在 try-with-resources 中关闭，
        // 同进程内立即删除必须成功（POSIX 上也能通过 JDK File.delete 校验无锁定）。
        assertTrue(outFile.delete(), "写入完成并关闭流后文件应可立即删除");
    }
}
