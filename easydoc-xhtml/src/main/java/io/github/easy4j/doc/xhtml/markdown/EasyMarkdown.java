package io.github.easy4j.doc.xhtml.markdown;

import java.io.ByteArrayOutputStream;
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

	/** docx → Markdown（经 docx4j HTML 导出 + 简化 HTML→MD 映射）。null 输入返回空串。 */
	public static String docxToMarkdown(WordprocessingMLPackage pkg) throws Exception {
		if (pkg == null) {
			return "";
		}
		// 无现成的 writeToHtml(pkg, OutputStream) 封装可用，
		// 故直接使用 docx4j API（HTMLSettings + Docx4J.toHTML）导出到内存流。
		HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
		htmlSettings.setWmlPackage(pkg);
		// 防止含图片文档在 HTML 导出时因未设置 imageDirPath 而失败
		htmlSettings.setImageDirPath(System.getProperty("java.io.tmpdir"));
		htmlSettings.setImageTargetUri("images");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Docx4J.toHTML(htmlSettings, out, Docx4J.FLAG_EXPORT_PREFER_XSL);
		return MarkdownConverter.htmlToMarkdown(out.toString("UTF-8"));
	}
}
