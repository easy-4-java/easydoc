package io.github.easy4j.doc.xhtml.markdown;

/**
 * 结构化 docx 块元素抽象基类（标题/段落/列表/表格/图片），子类覆写 toMarkdown。
 */
public abstract class DocxElement {

	/** 元素类型标签（日志定位用）。 */
	private final String elementType;

	protected DocxElement(String elementType) {
		this.elementType = elementType;
	}

	/** 转为 Markdown 片段（默认渲染选项）。 */
	public abstract String toMarkdown();

	/**
	 * 转为 Markdown 片段（指定渲染选项）。
	 *
	 * <p>默认实现委托给 {@link #toMarkdown()}；仅 {@link DocxTable} 覆写以支持
	 * 可选颜色渲染。其它子类无需覆写，除非需要响应 {@link MarkdownRenderOptions} 中的开关。</p>
	 *
	 * @param opts 渲染选项；null 等价于 {@link MarkdownRenderOptions#DEFAULT}
	 * @return Markdown 片段
	 */
	public String toMarkdown(MarkdownRenderOptions opts) {
		return toMarkdown();
	}

	/** 返回元素类型标签。 */
	public String getElementType() {
		return elementType;
	}
}
