package io.github.easy4j.doc.xhtml.markdown;

/**
 * 行内富文本片段（文本 + 粗体/斜体/下划线/超链接标记）。
 */
public final class InlineSpan {

	/** 片段文本（可空）。 */
	private final String text;

	/** 是否加粗。 */
	private final boolean bold;

	/** 是否斜体。 */
	private final boolean italic;

	/** 是否下划线。 */
	private final boolean underline;

	/** 超链接地址，无链接为 null。 */
	private final String hyperlinkUrl;

	public InlineSpan(String text) {
		this(text, false, false, false, null);
	}

	public InlineSpan(String text, String hyperlinkUrl) {
		this(text, false, false, false, hyperlinkUrl);
	}

	public InlineSpan(String text, boolean bold, boolean italic, boolean underline) {
		this(text, bold, italic, underline, null);
	}

	public InlineSpan(String text, boolean bold, boolean italic, boolean underline, String hyperlinkUrl) {
		this.text = text;
		this.bold = bold;
		this.italic = italic;
		this.underline = underline;
		this.hyperlinkUrl = hyperlinkUrl;
	}

	/** 转 Markdown：按下划线/粗/斜包裹文本；有链接输出 [文本](url)；空文本返回空串。 */
	public String toMarkdown() {
		if (text == null || text.isEmpty()) {
			return "";
		}
		String rendered = text;
		if (italic) {
			rendered = "*" + rendered + "*";
		}
		if (bold) {
			rendered = "**" + rendered + "**";
		}
		if (underline) {
			rendered = "<u>" + rendered + "</u>";
		}
		if (hyperlinkUrl != null && !hyperlinkUrl.isEmpty()) {
			return "[" + rendered + "](" + hyperlinkUrl + ")";
		}
		return rendered;
	}

	/** 返回片段文本。 */
	public String getText() {
		return text;
	}

	/** 是否加粗。 */
	public boolean isBold() {
		return bold;
	}

	/** 是否斜体。 */
	public boolean isItalic() {
		return italic;
	}

	/** 是否下划线。 */
	public boolean isUnderline() {
		return underline;
	}

	/** 返回超链接地址。 */
	public String getHyperlinkUrl() {
		return hyperlinkUrl;
	}

	@Override
	public String toString() {
		return "InlineSpan{text='" + text + "', bold=" + bold + ", italic=" + italic
				+ ", underline=" + underline + ", hyperlinkUrl=" + hyperlinkUrl + "}";
	}
}
