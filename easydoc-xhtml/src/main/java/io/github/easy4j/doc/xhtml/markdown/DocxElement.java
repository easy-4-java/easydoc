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

	/** 转为 Markdown 片段。 */
	public abstract String toMarkdown();

	/** 返回元素类型标签。 */
	public String getElementType() {
		return elementType;
	}
}
