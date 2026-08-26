package io.github.easy4j.doc.xhtml.markdown;

/**
 * docx 图片元素（src 内联时为 data URI，mime 记录原始类型）。
 */
public final class DocxImage extends DocxElement {

	/** 图片地址（data URI 或相对路径）。 */
	private final String src;

	/** 替代文本。 */
	private final String alt;

	/** 原始 MIME 类型（如 image/png），仅作元数据不参与渲染。 */
	private final String mime;

	public DocxImage(String src, String alt, String mime) {
		super("image");
		this.src = src;
		this.alt = alt;
		this.mime = mime;
	}

	/** 图片转 Markdown：输出 ![alt](src)，null 字段按空串处理。 */
	@Override
	public String toMarkdown() {
		return "![" + (alt == null ? "" : alt) + "](" + (src == null ? "" : src) + ")";
	}

	/** 返回图片地址。 */
	public String getSrc() {
		return src;
	}

	/** 返回替代文本。 */
	public String getAlt() {
		return alt;
	}

	/** 返回 MIME 类型。 */
	public String getMime() {
		return mime;
	}

	@Override
	public String toString() {
		return "DocxImage{src='" + src + "', alt='" + alt + "', mime='" + mime + "'}";
	}
}
