package io.github.easy4j.doc.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.ConversionHTMLScriptElementHandler;
import org.docx4j.convert.out.ConversionHTMLStyleElementHandler;
import org.docx4j.convert.out.ConversionHyperlinkHandler;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.handler.OutputConversionHTMLScriptElementHandler;
import io.github.easy4j.doc.handler.OutputConversionHTMLStyleElementHandler;
import io.github.easy4j.doc.handler.OutputConversionHyperlinkHandler;
import io.github.easy4j.doc.handler.OutputDirFilterHandler;
import io.github.easy4j.doc.utils.Assert;
import io.github.easy4j.doc.utils.Docx4jUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;
import java.util.Set;

/**
 * Unit tests for {@link WordprocessingMLPackageWriter}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WordprocessingMLPackageWriter Tests")
class WordprocessingMLPackageWriterTest {

    @Test
    @DisplayName("static method getWMLPackageWriter should be callable")
    void staticGetWMLPackageWriterShouldBeCallable() {
        try { WordprocessingMLPackageWriter.getWMLPackageWriter(); } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageWriter.class).isNotNull();
    }

    /**
     * 构造带正文段落的包：空包（仅 createPackage）在 Docx4J.toHTML 导出时
     * 会确定性失败（MainDocumentPart 为空），无法验证写入成功路径。
     */
    private static WordprocessingMLPackage packageWithContent()
            throws org.docx4j.openpackaging.exceptions.InvalidFormatException {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        pkg.getMainDocumentPart().addParagraphOfText("hello easydoc");
        return pkg;
    }

    @Test
    @DisplayName("writeToHtml 目标路径为已存在目录时抛出明确 IOException")
    void writeToHtmlFileRejectsExistingDirectoryTarget(@TempDir java.nio.file.Path tempDir) throws Exception {
        // 缺陷修复后的语义：outFile 必须是“目标文件”，传入已存在的目录时
        // 抛出带明确提示的 IOException（旧版要求目录却又对其建 FileOutputStream，
        // 导致该重载的任何调用都必然以 FileNotFoundException 失败）
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = packageWithContent();
        assertThatThrownBy(() -> writer.writeToHtml(pkg, tempDir.toFile()))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("directory");
    }

    @Test
    @DisplayName("writeToHtml 自动创建不存在的多级父目录并写出 html 与图片目录")
    void writeToHtmlCreatesMissingParentDirsAndWritesFile(@TempDir java.nio.file.Path tempDir) throws Exception {
        // File 即目标 html 文件（与 writeToDocx / writeToPDF 的 File 重载语义一致），
        // 多级不存在的父目录会被自动创建（Files.createDirectories）
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = packageWithContent();

        File outFile = tempDir.resolve("level1/level2/report.html").toFile();
        File result = writer.writeToHtml(pkg, outFile);

        assertThat(result).isNotNull();
        assertThat(result.isFile()).as("target html file should be created").isTrue();
        assertThat(result.length()).as("html output should be non-empty").isPositive();
        assertThat(new File(result.getParentFile(), "images").isDirectory())
                .as("images resource dir should be created next to the html file").isTrue();
    }

}
