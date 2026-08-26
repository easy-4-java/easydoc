package io.github.easy4j.doc.xhtml.markdown;

/**
 * docx 标题元素（level 0 为文档标题，1-9 对应 Markdown 井号层级）。
 */
public final class DocxHeading extends DocxElement {

	/** 支持的最大标题层级。 */
	private static final int MAX_LEVEL = 9;

	/** 标题层级（0=文档标题样式，1-9 常规层级）。 */
	private final int level;

	/** 标题文本。 */
	private final String text;

	/** 标题超链接地址，无链接为 null。 */
	private final String hyperlinkUrl;

	public DocxHeading(int level, String text) {
		this(level, text, null);
	}

	public DocxHeading(int level, String text, String hyperlinkUrl) {
		super("heading");
		this.level = level;
		this.text = text;
		this.hyperlinkUrl = hyperlinkUrl;
	}

	/** 转 Markdown：输出 level 个井号（0 与负数按一级、超出 9 按九级），带链接时文本包为 [text](url)。 */
	@Override
	public String toMarkdown() {
		int hashes = level < 1 ? 1 : Math.min(level, MAX_LEVEL);
		StringBuilder md = new StringBuilder();
		for (int i = 0; i < hashes; i++) {
			md.append('#');
		}
		String body = text == null ? "" : text;
		if (hyperlinkUrl != null && !hyperlinkUrl.isEmpty()) {
			body = "[" + body + "](" + hyperlinkUrl + ")";
		}
		return md.append(' ').append(body).toString();
	}

	/** 返回标题层级。 */
	public int getLevel() {
		return level;
	}

	/** 返回标题文本。 */
	public String getText() {
		return text;
	}

	/** 返回标题超链接地址。 */
	public String getHyperlinkUrl() {
		return hyperlinkUrl;
	}

	@Override
	public String toString() {
		return "DocxHeading{level=" + level + ", text='" + text + "', hyperlinkUrl=" + hyperlinkUrl + "}";
	}
}
