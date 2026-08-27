package io.github.easy4j.doc.xhtml.markdown;

import java.util.ArrayList;
import java.util.List;

/**
 * docx 表格元素（表头 + 数据行），输出标准 GFM 表格。
 *
 * <p>内部存储单元格已升级为 {@link DocxCell}（含可选字体色 / 背景色），
 * 旧调用方可使用 {@link #ofStrings(List, List)} 静态工厂方法构造。</p>
 */
public final class DocxTable extends DocxElement {

	/** 表头单元格（含样式），可空。 */
	private final List<DocxCell> headers;

	/** 数据行（每行为 {@link DocxCell} 列表），可空。 */
	private final List<List<DocxCell>> rows;

	/** 构造器（含单元格样式）。 */
	public DocxTable(List<DocxCell> headers, List<List<DocxCell>> rows) {
		super("table");
		this.headers = headers;
		this.rows = rows;
	}

	/**
	 * 纯文本构造器（兼容已有调用方），颜色字段均为 null。
	 *
	 * @deprecated 请直接使用 {@link #DocxTable(List, List)} 并传入 {@link DocxCell} 列表
	 */
	@Deprecated
	public static DocxTable ofStrings(List<String> headers, List<List<String>> rows) {
		return new DocxTable(toCells(headers), toCellRows(rows));
	}

	/** 表格转 GFM（默认选项，渲染颜色关闭）：表头行 + |---| 分隔行 + 数据行。 */
	@Override
	public String toMarkdown() {
		return toMarkdown(MarkdownRenderOptions.DEFAULT);
	}

	/**
	 * 表格转 GFM（可选颜色渲染）：表头行 + |---| 分隔行 + 数据行；
	 * 无表头时用首行充当表头并保留其数据行；空表返回空串。
	 * 所有行按表头列数归一化（补空格/截断），保证 GFM 网格不破碎。
	 *
	 * @param opts 渲染选项；null 等价于 {@link MarkdownRenderOptions#DEFAULT}
	 */
	@Override
	public String toMarkdown(MarkdownRenderOptions opts) {
		if (opts == null) {
			opts = MarkdownRenderOptions.DEFAULT;
		}
		boolean synthesizeHeader = headers == null || headers.isEmpty();
		if (synthesizeHeader && (rows == null || rows.isEmpty())) {
			return "";
		}
		List<DocxCell> headerRow = synthesizeHeader ? rows.get(0) : headers;
		int expectedColumns = headerRow == null ? 0 : headerRow.size();
		StringBuilder md = new StringBuilder();
		appendRow(md, headerRow, expectedColumns, opts);
		md.append('|');
		for (int i = 0; i < expectedColumns; i++) {
			md.append("---|");
		}
		md.append('\n');
		if (rows != null) {
			for (List<DocxCell> row : rows) {
				appendRow(md, row, expectedColumns, opts);
			}
		}
		return md.substring(0, md.length() - 1);
	}

	/** 返回表头单元格（含样式）。 */
	public List<DocxCell> getHeaders() {
		return headers;
	}

	/**
	 * 返回表头文本（纯文本，兼容旧调用方）。
	 *
	 * @deprecated 请使用 {@link #getHeaders()} 获取含样式的单元格
	 */
	@Deprecated
	public List<String> getHeadersAsText() {
		return toTextList(headers);
	}

	/** 返回数据行（含样式）。 */
	public List<List<DocxCell>> getRows() {
		return rows;
	}

	/**
	 * 返回数据行文本（纯文本，兼容旧调用方）。
	 *
	 * @deprecated 请使用 {@link #getRows()} 获取含样式的单元格
	 */
	@Deprecated
	public List<List<String>> getRowsAsText() {
		if (rows == null) {
			return null;
		}
		List<List<String>> result = new ArrayList<List<String>>();
		for (List<DocxCell> row : rows) {
			result.add(toTextList(row));
		}
		return result;
	}

	/**
	 * 行渲染：单元格文本统一经 {@link MarkdownEscaper#escapeText} 转义；
	 * 当 {@code opts.renderHtmlColor()} 开启且单元格有样式时，用 {@code <span>} 包裹。
	 * 行按 expectedColumns 归一化——不足补空单元格、超出截断。
	 */
	private static void appendRow(StringBuilder md, List<DocxCell> cells,
			int expectedColumns, MarkdownRenderOptions opts) {
		md.append('|');
		int count = cells == null ? 0 : Math.min(cells.size(), Math.max(expectedColumns, 0));
		for (int i = 0; i < count; i++) {
			DocxCell cell = cells.get(i);
			String text = cell == null ? "" : cell.text();
			md.append(' ');
			if (opts.renderHtmlColor() && cell != null && cell.hasStyle()) {
				md.append("<span style=\"");
				if (cell.fontColorHex() != null) {
					md.append("color:#").append(cell.fontColorHex()).append(';');
				}
				if (cell.backgroundColorHex() != null) {
					md.append("background-color:#").append(cell.backgroundColorHex()).append(';');
				}
				md.append("\">");
				md.append(MarkdownEscaper.escapeText(text));
				md.append("</span>");
			} else {
				md.append(MarkdownEscaper.escapeText(text));
			}
			md.append(" |");
		}
		for (int i = count; i < expectedColumns; i++) {
			md.append("  |");
		}
		md.append('\n');
	}

	private static List<String> toTextList(List<DocxCell> cells) {
		if (cells == null) {
			return null;
		}
		List<String> texts = new ArrayList<String>(cells.size());
		for (DocxCell cell : cells) {
			texts.add(cell == null ? "" : cell.text());
		}
		return texts;
	}

	private static List<DocxCell> toCells(List<String> texts) {
		if (texts == null) {
			return null;
		}
		List<DocxCell> cells = new ArrayList<DocxCell>(texts.size());
		for (String t : texts) {
			cells.add(new DocxCell(t == null ? "" : t, null, null));
		}
		return cells;
	}

	private static List<List<DocxCell>> toCellRows(List<List<String>> textRows) {
		if (textRows == null) {
			return null;
		}
		List<List<DocxCell>> result = new ArrayList<List<DocxCell>>(textRows.size());
		for (List<String> row : textRows) {
			result.add(toCells(row));
		}
		return result;
	}

	@Override
	public String toString() {
		return "DocxTable{headers=" + headers + ", rows=" + rows + "}";
	}
}
