package io.github.easy4j.doc.xhtml.markdown;

import java.util.Arrays;
import java.util.List;

/**
 * docx 列表元素（有序/无序 + 嵌套层级），纯文本项与富文本项并存。
 */
public final class DocxList extends DocxElement {

	/** 是否有序列表。 */
	private final boolean ordered;

	/** 嵌套层级（OOXML ilvl，0 起始，每级缩进 2 空格）。 */
	private final int indent;

	/** 纯文本列表项。 */
	private final List<String> items;

	/** 富文本列表项（与 items 按下标对应，优先使用）。 */
	private final List<List<InlineSpan>> richItems;

	public DocxList(boolean ordered, int indent, List<String> items) {
		this(ordered, indent, items, null);
	}

	public DocxList(boolean ordered, int indent, List<String> items, List<List<InlineSpan>> richItems) {
		super("list");
		this.ordered = ordered;
		this.indent = indent;
		this.items = items;
		this.richItems = richItems;
	}

	/** 列表转 Markdown：有序输出 1./2./…，无序输出 -，缩进每级 2 空格；同下标富文本项优先于纯文本项。 */
	@Override
	public String toMarkdown() {
		int count = Math.max(items == null ? 0 : items.size(), richItems == null ? 0 : richItems.size());
		if (count == 0) {
			return "";
		}
		String pad = spaces(indent <= 0 ? 0 : indent * 2);
		StringBuilder md = new StringBuilder();
		for (int i = 0; i < count; i++) {
			if (md.length() > 0) {
				md.append('\n');
			}
			md.append(pad).append(ordered ? (i + 1) + ". " : "- ");
			List<InlineSpan> rich = richItems == null || i >= richItems.size() ? null : richItems.get(i);
			if (rich != null && !rich.isEmpty()) {
				md.append(DocxParagraph.renderSpans(rich));
			} else if (items != null && i < items.size() && items.get(i) != null) {
				md.append(items.get(i));
			}
		}
		return md.toString();
	}

	/** 是否有序列表。 */
	public boolean isOrdered() {
		return ordered;
	}

	/** 返回嵌套层级。 */
	public int getIndent() {
		return indent;
	}

	/** 返回纯文本列表项。 */
	public List<String> getItems() {
		return items;
	}

	/** 返回富文本列表项。 */
	public List<List<InlineSpan>> getRichItems() {
		return richItems;
	}

	private static String spaces(int count) {
		char[] chars = new char[count];
		Arrays.fill(chars, ' ');
		return new String(chars);
	}

	@Override
	public String toString() {
		return "DocxList{ordered=" + ordered + ", indent=" + indent
				+ ", items=" + items + ", richItems=" + richItems + "}";
	}
}
