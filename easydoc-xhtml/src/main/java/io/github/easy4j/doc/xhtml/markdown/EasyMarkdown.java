package io.github.easy4j.doc.xhtml.markdown;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;

/**
 * Markdown ↔ docx 转换门面（对齐 EasyDocx 风格）。MD→docx 走
 * MarkdownConverter.mdToHtml + 现有 WordprocessingMLHtmlTemplate（HTML→docx）；
 * 薄封装，不替代引擎。
 */
public final class EasyMarkdown {

	private EasyMarkdown() {
	}

	/** Markdown → docx（无变量替换）。 */
	public static WordprocessingMLPackage markdownToDocx(String markdown) throws Exception {
		return markdownToDocx(markdown, null);
	}

	/** Markdown → docx。variables 参数当前未生效（WordprocessingMLHtmlTemplate 暂不支持 HTML 变量替换）；传入 null 即可。 */
	public static WordprocessingMLPackage markdownToDocx(String markdown,
			Map<String, Object> vars) throws Exception {
		String html = MarkdownConverter.mdToHtml(markdown);
		WordprocessingMLHtmlTemplate template = new WordprocessingMLHtmlTemplate();
		return template.process(html, vars);
	}

	/** docx 文件 → Markdown。null 输入返回空串。 */
	public static String docxToMarkdown(File file) throws Exception {
		if (file == null) {
			return "";
		}
		return docxToMarkdown(WordprocessingMLPackage.load(file));
	}

	/** docx 输入流 → Markdown（流由本方法负责关闭）。null 输入返回空串。 */
	public static String docxToMarkdown(InputStream in) throws Exception {
		if (in == null) {
			return "";
		}
		try (InputStream closeable = in) {
			return docxToMarkdown(WordprocessingMLPackage.load(closeable));
		}
	}

	/** docx 字节数组 → Markdown。null 输入返回空串。 */
	public static String docxToMarkdown(byte[] bytes) throws Exception {
		if (bytes == null) {
			return "";
		}
		return docxToMarkdown(WordprocessingMLPackage.load(new ByteArrayInputStream(bytes)));
	}

	/** docx 文件路径 → Markdown。null/空白路径返回空串。 */
	public static String docxToMarkdown(String path) throws Exception {
		if (path == null || path.trim().isEmpty()) {
			return "";
		}
		return docxToMarkdown(new File(path));
	}

	/** docx → Markdown（经 docx4j HTML 导出 + 简化 HTML→MD 映射）。null 输入返回空串。 */
	public static String docxToMarkdown(WordprocessingMLPackage pkg) throws Exception {
		if (pkg == null) {
			return "";
		}
		// 3.0.x WordprocessingMLPackageWriter 未提供 writeToHtml(pkg, OutputStream)
		// 重载，为避免落盘临时文件，此处直接使用同款 docx4j API
		// （HTMLSettings + Docx4J.toHTML）导出到内存流。
		// （历史上的 File 重载“要求目录却又对其建流”缺陷已在
		// WordprocessingMLPackageWriter 中修复：File 现为统一的目标文件语义。）
		HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
		htmlSettings.setWmlPackage(pkg);
		// 防止含图片文档在 HTML 导出时因未设置 imageDirPath 而失败
		htmlSettings.setImageDirPath(System.getProperty("java.io.tmpdir"));
		htmlSettings.setImageTargetUri("images");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Docx4J.toHTML(htmlSettings, out, Docx4J.FLAG_EXPORT_PREFER_XSL);
		return MarkdownConverter.htmlToMarkdown(out.toString("UTF-8"));
	}

	// ==================== 结构化路径（OOXML 直读，高保真 95%+） ====================
	// 与上方 HTML 路径（约 70% 保真）并存：本组重载经 DocxToMarkdownConverter /
	// DocxStructureExtractor 直接解析 OOXML 语义（标题层级、有序/无序列表、内联格式、
	// 表格、图片）。null 策略差异有意为之：旧 docxToMarkdown 系列 null 返回空串（宽松），
	// 新结构化系列 null 抛 NPE（requireNonNull 边界严格校验，尽早暴露调用方缺陷）。
	// 受检异常统一收紧为 IOException：底层仅可能因文件不存在/包损坏抛出，
	// 不再泄漏 Exception 这种"究极捕获"签名（包加载 pkg 重载实际不会抛 IO，保留
	// 同组一致签名并预留未来降级空间）。

	/**
	 * 结构化 docx 文件 → Markdown（OOXML 直读，高保真；默认渲染选项，纯 GFM 输出）。
	 * null 输入抛 NPE；文件不存在或包损坏抛 IOException。
	 */
	public static String docxToStructuredMarkdown(File docx) throws IOException {
		return docxToStructuredMarkdown(docx, MarkdownRenderOptions.DEFAULT);
	}

	/**
	 * 结构化 docx 文件 → Markdown（OOXML 直读，指定渲染选项）。
	 * null 输入抛 NPE；文件不存在或包损坏抛 IOException。
	 *
	 * @param docx docx 文件
	 * @param opts 渲染选项；null 等价于 {@link MarkdownRenderOptions#DEFAULT}
	 */
	public static String docxToStructuredMarkdown(File docx, MarkdownRenderOptions opts) throws IOException {
		return DocxToMarkdownConverter.convert(docx, opts);
	}

	/**
	 * 结构化 docx 输入流 → Markdown（默认渲染选项，流由底层负责关闭，勿重复关闭）。
	 * null 输入抛 NPE；损坏包抛 IOException。
	 */
	public static String docxToStructuredMarkdown(InputStream in) throws IOException {
		return docxToStructuredMarkdown(in, MarkdownRenderOptions.DEFAULT);
	}

	/**
	 * 结构化 docx 输入流 → Markdown（指定渲染选项，流由底层负责关闭，勿重复关闭）。
	 * null 输入抛 NPE；损坏包抛 IOException。
	 *
	 * @param in   docx 输入流
	 * @param opts 渲染选项；null 等价于 {@link MarkdownRenderOptions#DEFAULT}
	 */
	public static String docxToStructuredMarkdown(InputStream in, MarkdownRenderOptions opts) throws IOException {
		return DocxToMarkdownConverter.convert(in, opts);
	}

	/**
	 * 已加载结构化 docx 包 → Markdown（默认渲染选项，OOXML 直读，高保真）。
	 * null 输入抛 NPE。
	 */
	public static String docxToStructuredMarkdown(WordprocessingMLPackage pkg) throws IOException {
		return docxToStructuredMarkdown(pkg, MarkdownRenderOptions.DEFAULT);
	}

	/**
	 * 已加载结构化 docx 包 → Markdown（指定渲染选项，OOXML 直读，高保真）。
	 * null 输入抛 NPE。
	 *
	 * @param pkg  已加载的 docx 包
	 * @param opts 渲染选项；null 等价于 {@link MarkdownRenderOptions#DEFAULT}
	 */
	public static String docxToStructuredMarkdown(WordprocessingMLPackage pkg, MarkdownRenderOptions opts) throws IOException {
		return DocxToMarkdownConverter.convert(pkg, opts);
	}

	/**
	 * 结构化 docx 文件 → {@link DocxDocument} POJO 树（供智能体/调用方深度遍历块元素与行内片段）。
	 * null 输入抛 NPE；文件不存在或包损坏抛 IOException。
	 */
	public static DocxDocument docxToStructured(File docx) throws IOException {
		return DocxStructureExtractor.extract(docx);
	}

	/**
	 * 结构化 docx 输入流 → {@link DocxDocument} POJO 树（流由底层负责关闭，勿重复关闭）。
	 * null 输入抛 NPE；损坏包抛 IOException。
	 */
	public static DocxDocument docxToStructured(InputStream in) throws IOException {
		return DocxStructureExtractor.extract(in);
	}

	/**
	 * 已加载结构化 docx 包 → {@link DocxDocument} POJO 树。
	 * null 输入抛 NPE。
	 */
	public static DocxDocument docxToStructured(WordprocessingMLPackage pkg) throws IOException {
		return DocxStructureExtractor.extract(pkg);
	}
}
