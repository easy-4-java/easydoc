package io.github.easy4j.doc.xhtml.markdown;

import java.util.Map;

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

	/** Markdown → docx（支持 ${var} 占位符替换——MD 内容渲染后由 HTML 管线处理）。 */
	public static WordprocessingMLPackage markdownToDocx(String markdown,
			Map<String, Object> vars) throws Exception {
		String html = MarkdownConverter.mdToHtml(markdown);
		WordprocessingMLHtmlTemplate template = new WordprocessingMLHtmlTemplate();
		return template.process(html, vars);
	}
}
