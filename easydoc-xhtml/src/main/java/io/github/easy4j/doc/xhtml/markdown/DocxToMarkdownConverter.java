package io.github.easy4j.doc.xhtml.markdown;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

/**
 * 结构化 DOCX → Markdown 门面聚合器（OOXML 直读路径）。
 *
 * <p>两条 docx → Markdown 路径并存，按需选择：</p>
 * <ul>
 * <li><b>结构化路径</b>（本类与 {@link EasyMarkdown#docxToStructuredMarkdown} 系列）：
 * {@link DocxStructureExtractor} 直接遍历 OOXML（w:p/pStyle、numPr + numbering part、
 * w:tbl、w:drawing 等），基于语义 POJO 树 {@link DocxDocument} 渲染 Markdown，
 * 保真度 95%+ —— 标题层级、有序/无序列表及嵌套缩进、加粗/斜体/下划线、超链接、
 * 表格首行表头约定、图片内联 data URI 均按文档结构还原。</li>
 * <li><b>HTML 路径</b>（{@link EasyMarkdown#docxToMarkdown} 系列）：docx4j HTML 导出 +
 * 简化 HTML→MD 映射，速度快但对复杂结构保真度仅约 70% —— 列表编号语义、深层标题层级、
 * 图片二进制等常被压平或丢失。</li>
 * </ul>
 *
 * <p><b>null 输入策略</b>：新结构化 API 不沿用旧 {@code docxToMarkdown} 系列
 * "null 返回空串" 的宽松约定，改用 {@link Objects#requireNonNull} 边界严格校验
 * （null File/InputStream/pkg 一律抛 {@link NullPointerException}），错误尽早暴露；
 * 该行为由底层 {@link DocxStructureExtractor} 统一实施。文件不存在或包损坏抛
 * {@link IOException}。InputStream 由底层负责关闭（含解析失败场景），调用方无需再关闭。</p>
 */
public final class DocxToMarkdownConverter {

	private DocxToMarkdownConverter() {
	}

	/** 结构化 DOCX 文件 → Markdown（默认渲染选项）。null 抛 NPE；文件不存在抛 IOException。 */
	public static String convert(File docx) throws IOException {
		return convert(docx, MarkdownRenderOptions.DEFAULT);
	}

	/** 结构化 DOCX 文件 → Markdown（指定渲染选项）。null 抛 NPE；文件不存在抛 IOException。 */
	public static String convert(File docx, MarkdownRenderOptions opts) throws IOException {
		return DocxStructureExtractor.extract(docx).fullMarkdown(opts);
	}

	/** 结构化 DOCX 输入流 → Markdown（默认渲染选项，流由本方法负责关闭）。null 抛 NPE；损坏包抛 IOException。 */
	public static String convert(InputStream in) throws IOException {
		return convert(in, MarkdownRenderOptions.DEFAULT);
	}

	/** 结构化 DOCX 输入流 → Markdown（指定渲染选项，流由本方法负责关闭）。null 抛 NPE；损坏包抛 IOException。 */
	public static String convert(InputStream in, MarkdownRenderOptions opts) throws IOException {
		return DocxStructureExtractor.extract(in).fullMarkdown(opts);
	}

	/** 已加载结构化 DOCX 包 → Markdown（默认渲染选项）。null 抛 NPE。 */
	public static String convert(WordprocessingMLPackage pkg) {
		return convert(pkg, MarkdownRenderOptions.DEFAULT);
	}

	/** 已加载结构化 DOCX 包 → Markdown（指定渲染选项）。null 抛 NPE。 */
	public static String convert(WordprocessingMLPackage pkg, MarkdownRenderOptions opts) {
		return DocxStructureExtractor.extract(pkg).fullMarkdown(opts);
	}
}
