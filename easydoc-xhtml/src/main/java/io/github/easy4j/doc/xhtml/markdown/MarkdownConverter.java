package io.github.easy4j.doc.xhtml.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

/**
 * Markdown ↔ HTML 双向转换（flexmark 驱动）。
 * <ul>
 *   <li>{@link #mdToHtml}：Markdown → HTML，启用 GFM 表格/删除线扩展，供 HTML→docx 管线消费。</li>
 *   <li>{@link #htmlToMarkdown}：HTML → Markdown，AST 驱动（FlexmarkHtmlConverter），
 *       解决旧正则映射对带属性标签/嵌套列表/有序编号/表格/内联代码/锚点的失真问题。</li>
 * </ul>
 */
public final class MarkdownConverter {

	private static final Parser PARSER;
	private static final HtmlRenderer RENDERER;

	/** HTML → Markdown 转换器（线程安全：构建后不可变，与 Parser/Renderer 同模式）。 */
	private static final FlexmarkHtmlConverter HTML_TO_MD;

	static {
		MutableDataSet options = new MutableDataSet();
		options.set(Parser.EXTENSIONS, java.util.Arrays.asList(
				com.vladsch.flexmark.ext.tables.TablesExtension.create(),
				com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension.create(),
				com.vladsch.flexmark.ext.autolink.AutolinkExtension.create()));
		PARSER = Parser.builder(options).build();
		RENDERER = HtmlRenderer.builder(options).build();

		// HTML→Markdown 选项：ATX 样式标题（# 而非 setext 下划线）、
		// 无序列表用 - 分隔（与旧正则行为一致）、关闭 setext headings。
		MutableDataSet html2mdOpts = new MutableDataSet();
		html2mdOpts.set(FlexmarkHtmlConverter.SETEXT_HEADINGS, false);
		html2mdOpts.set(FlexmarkHtmlConverter.UNORDERED_LIST_DELIMITER, '-');
		HTML_TO_MD = FlexmarkHtmlConverter.builder(html2mdOpts).build();
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

	/**
	 * HTML → Markdown（AST 驱动，flexmark-html2md-converter）。
	 * null 输入返回空串。方法签名与 null 语义保持不变，仅内部实现从正则替换
	 * 升级为 FlexmarkHtmlConverter（解决 F1 系列保真度问题）。
	 */
	public static String htmlToMarkdown(String html) {
		if (html == null) {
			return "";
		}
		return HTML_TO_MD.convert(html);
	}
}
