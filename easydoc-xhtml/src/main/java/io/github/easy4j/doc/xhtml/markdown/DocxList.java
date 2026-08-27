package io.github.easy4j.doc.xhtml.markdown;

import java.util.Arrays;
import java.util.List;

/**
 * docx 列表元素（有序/无序 + 嵌套层级），纯文本项与富文本项并存。
 *
 * <p>嵌套缩进遵循 CommonMark 规则：子块缩进列数必须 ≥ 父项内容起始列
 * （如 {@code "1. "} 占 3 列、{@code "10. "} 占 4 列、{@code "- "} 占 2 列）。
 * 抽取路径（{@code DocxStructureExtractor}）按祖先各级的“最宽标记宽度”累计出精确
 * 缩进；直接构造（未传精确缩进）时回退为每级 2 空格的启发式 —— 对无序列表与浅层
 * 有序列表恰好合法，深层有序列表仅作近似展示用途。</p>
 */
public final class DocxList extends DocxElement {

	/** 是否有序列表。 */
	private final boolean ordered;

	/** 嵌套层级（OOXML ilvl，0 起始）。 */
	private final int indent;

	/** 精确缩进列数：由抽取器按祖先级标记宽度和累计计算；null 表示未提供，渲染时回退 indent*2 启发式。 */
	private final Integer indentColumns;

	/** 纯文本列表项。 */
	private final List<String> items;

	/** 富文本列表项（与 items 按下标对应，优先使用）。 */
	private final List<List<InlineSpan>> richItems;

	public DocxList(boolean ordered, int indent, List<String> items) {
		this(ordered, indent, items, null);
	}

	public DocxList(boolean ordered, int indent, List<String> items, List<List<InlineSpan>> richItems) {
		this(ordered, indent, items, richItems, null);
	}

	/**
	 * 包私有构造器：携带抽取器按 CommonMark 几何规则算出的精确缩进列数。
	 * indentColumns 为 null 时行为与公开双参构造器完全一致。
	 */
	DocxList(boolean ordered, int indent, List<String> items,
			List<List<InlineSpan>> richItems, Integer indentColumns) {
		super("list");
		this.ordered = ordered;
		this.indent = indent;
		this.indentColumns = indentColumns;
		this.items = items;
		this.richItems = richItems;
	}

	/** 列表转 Markdown：有序输出 1./2./…，无序输出 -；同下标富文本项优先于纯文本项。 */
	@Override
	public String toMarkdown() {
		int count = Math.max(items == null ? 0 : items.size(), richItems == null ? 0 : richItems.size());
		if (count == 0) {
			return "";
		}
		String pad = spaces(padColumns());
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

	/**
	 * 渲染前的缩进列数：优先使用抽取器按祖先级标记宽度累计出的精确值；
	 * 未提供时回退到每级 2 空格的启发式（历史约定，保证既有调用方行为不变）。
	 */
	private int padColumns() {
		if (indentColumns != null) {
			return Math.max(indentColumns.intValue(), 0);
		}
		return indent <= 0 ? 0 : indent * 2;
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
				+ ", indentColumns=" + indentColumns
				+ ", items=" + items + ", richItems=" + richItems + "}";
	}
}
