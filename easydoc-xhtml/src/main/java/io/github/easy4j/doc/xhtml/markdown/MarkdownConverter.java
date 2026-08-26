package io.github.easy4j.doc.xhtml.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

/**
 * Markdown 文本 → HTML 字符串转换（flexmark 驱动，启用 GFM 表格/删除线扩展）。
 * 输出 HTML 供 easydoc 现有 HTML→docx 管线消费。
 */
public final class MarkdownConverter {

	private static final Parser PARSER;
	private static final HtmlRenderer RENDERER;

	static {
		MutableDataSet options = new MutableDataSet();
		options.set(Parser.EXTENSIONS, java.util.Arrays.asList(
				com.vladsch.flexmark.ext.tables.TablesExtension.create(),
				com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension.create(),
				com.vladsch.flexmark.ext.autolink.AutolinkExtension.create()));
		PARSER = Parser.builder(options).build();
		RENDERER = HtmlRenderer.builder(options).build();
	}

	private MarkdownConverter() {
	}

	/** Markdown → HTML。null 输入返回空串。 */
	public static String mdToHtml(String markdown) {
		if (markdown == null) {
			return "";
		}
		Node document = PARSER.parse(markdown);
		return RENDERER.render(document);
	}

	/** HTML → Markdown（简化映射：标题/段落/粗斜体/列表/表格/代码块）。 */
	public static String htmlToMarkdown(String html) {
		if (html == null) {
			return "";
		}
		String out = html
				.replaceAll("(?i)<h1[^>]*>", "\n# ")
				.replaceAll("(?i)</h1>", "\n")
				.replaceAll("(?i)<h2[^>]*>", "\n## ")
				.replaceAll("(?i)</h2>", "\n")
				.replaceAll("(?i)<h3[^>]*>", "\n### ")
				.replaceAll("(?i)</h3>", "\n")
				.replaceAll("(?i)<strong>", "**").replaceAll("(?i)</strong>", "**")
				.replaceAll("(?i)<em>", "*").replaceAll("(?i)</em>", "*")
				.replaceAll("(?i)<li>", "- ").replaceAll("(?i)</li>", "\n")
				.replaceAll("(?i)<p[^>]*>", "\n").replaceAll("(?i)</p>", "\n")
				.replaceAll("(?i)<td[^>]*>", " | ").replaceAll("(?i)</td>", "")
				.replaceAll("(?i)</tr>", "\n")
				.replaceAll("(?i)<pre[^>]*>", "\n```\n").replaceAll("(?i)</pre>", "\n```\n")
				.replaceAll("(?i)</?table[^>]*>", "\n")
				.replaceAll("(?i)</?thead[^>]*>|</?tbody[^>]*>|</?tr[^>]*>", "\n")
				.replaceAll("(?i)<[^>]+>", ""); // 残余标签
		return out.replaceAll("\\n{3,}", "\n\n").trim();
	}
}
