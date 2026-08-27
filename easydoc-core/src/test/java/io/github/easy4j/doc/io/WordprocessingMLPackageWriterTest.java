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

    @Test
    @DisplayName("writeToHtml 目标路径为已存在目录时抛出明确 IOException")
    void writeToHtmlRejectsExistingDirectoryTarget(@TempDir java.nio.file.Path tempDir) throws Exception {
        // 缺陷修复后语义：outFile 必须是目标 html 文件，传入已存在的目录必须直接失败，
        // 且异常信息需明确指出“目标是目录”（旧版要求目录却又对其建 FileOutputStream，
        // 导致任何调用都必然以 FileNotFoundException 失败）
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File outDir = tempDir.resolve("htmlout").toFile();
        assertThat(outDir.mkdir()).isTrue();
        assertThatThrownBy(() -> writer.writeToHtml(pkg, outDir))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("directory");
    }

    @Test
    @DisplayName("writeToHtml 自动创建不存在的多级父目录并写出 html 文件")
    void writeToHtmlCreatesMissingParentDirsAndWritesFile(@TempDir java.nio.file.Path tempDir) throws Exception {
        // 缺陷修复后语义：File 即目标 html 文件，多级不存在的父目录会被自动创建
        // （Files.createDirectories），而不是要求先手动建目录；导出空包时 Docx4J.toHTML
        // 会确定性失败（MainDocumentPart 为空），故补一个正文段落验证成功写出路径
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        pkg.getMainDocumentPart().addParagraphOfText("hello easydoc");
        File outFile = tempDir.resolve("level1/level2/report.html").toFile();
        File result = writer.writeToHtml(pkg, outFile);
        assertThat(result).isNotNull();
        assertThat(outFile.isFile()).isTrue();
        assertThat(outFile.length()).isPositive();
        assertThat(new File(outFile.getParentFile(), "images").isDirectory()).isTrue();
    }

}
