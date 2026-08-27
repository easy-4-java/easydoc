package io.github.easy4j.doc.xhtml.markdown;

import java.time.Instant;
import java.util.List;

/**
 * 结构化文档模型（元信息 + 有序块元素），可渲染整篇 Markdown。
 */
public final class DocxDocument {

	/** 文档标题。 */
	private final String title;

	/** 作者。 */
	private final String author;

	/** 最后修改时间。 */
	private final Instant modified;

	/** 块元素（按文档顺序）。 */
	private final List<DocxElement> elements;

	public DocxDocument(String title, String author, Instant modified, List<DocxElement> elements) {
		this.title = title;
		this.author = author;
		this.modified = modified;
		this.elements = elements;
	}

	/** 渲染整篇 Markdown（默认渲染选项）：按序以空行拼接各块并 trim；跳过空白块；无内容返回空串。 */
	public String fullMarkdown() {
		return fullMarkdown(MarkdownRenderOptions.DEFAULT);
	}

	/**
	 * 渲染整篇 Markdown（指定渲染选项）：按序以空行拼接各块并 trim；跳过空白块；无内容返回空串。
	 *
	 * @param opts 渲染选项；null 等价于 {@link MarkdownRenderOptions#DEFAULT}
	 */
	public String fullMarkdown(MarkdownRenderOptions opts) {
		if (elements == null || elements.isEmpty()) {
			return "";
		}
		StringBuilder md = new StringBuilder();
		for (DocxElement element : elements) {
			if (element == null) {
				continue;
			}
			String part = element.toMarkdown(opts);
			if (part == null || part.trim().isEmpty()) {
				continue;
			}
			if (md.length() > 0) {
				md.append("\n\n");
			}
			md.append(part);
		}
		return md.toString().trim();
	}

	/** 返回文档标题。 */
	public String getTitle() {
		return title;
	}

	/** 返回作者。 */
	public String getAuthor() {
		return author;
	}

	/** 返回最后修改时间。 */
	public Instant getModified() {
		return modified;
	}

	/** 返回块元素列表。 */
	public List<DocxElement> getElements() {
		return elements;
	}

	@Override
	public String toString() {
		int size = elements == null ? 0 : elements.size();
		return "DocxDocument{title='" + title + "', author='" + author
				+ "', modified=" + modified + ", elements=" + size + "}";
	}
}
