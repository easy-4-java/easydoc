package io.github.easy4j.doc.xhtml.markdown;

import java.util.List;

/**
 * docx 段落元素，由行内富文本片段顺序拼接而成。
 */
public final class DocxParagraph extends DocxElement {

	/** 行内片段列表（可空）。 */
	private final List<InlineSpan> spans;

	public DocxParagraph(List<InlineSpan> spans) {
		super("paragraph");
		this.spans = spans;
	}

	/** 段落转 Markdown：顺序拼接片段；无有效内容返回空串（fullMarkdown 会跳过空块）。 */
	@Override
	public String toMarkdown() {
		return renderSpans(spans);
	}

	/** 顺序拼接片段列表并跳过 null 片段，供段落与富列表项复用。 */
	static String renderSpans(List<InlineSpan> spans) {
		if (spans == null || spans.isEmpty()) {
			return "";
		}
		StringBuilder md = new StringBuilder();
		for (InlineSpan span : spans) {
			if (span == null) {
				continue;
			}
			md.append(span.toMarkdown());
		}
		return md.toString();
	}

	/** 返回行内片段列表。 */
	public List<InlineSpan> getSpans() {
		return spans;
	}

	@Override
	public String toString() {
		return "DocxParagraph{spans=" + spans + "}";
	}
}
