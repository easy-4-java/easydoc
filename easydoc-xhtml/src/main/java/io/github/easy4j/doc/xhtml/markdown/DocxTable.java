package io.github.easy4j.doc.xhtml.markdown;

import java.util.List;

/**
 * docx 表格元素（表头 + 数据行），输出标准 GFM 表格。
 */
public final class DocxTable extends DocxElement {

	/** 表头单元格文本，可空。 */
	private final List<String> headers;

	/** 数据行（每行为单元格文本列表），可空。 */
	private final List<List<String>> rows;

	public DocxTable(List<String> headers, List<List<String>> rows) {
		super("table");
		this.headers = headers;
		this.rows = rows;
	}

	/** 表格转 GFM：表头行 + |---| 分隔行 + 数据行；无表头时用首行充当表头并保留其数据行；空表返回空串。所有行按表头列数归一化（补空格/截断），保证 GFM 网格不破碎。 */
	@Override
	public String toMarkdown() {
		boolean synthesizeHeader = headers == null || headers.isEmpty();
		if (synthesizeHeader && (rows == null || rows.isEmpty())) {
			return "";
		}
		List<String> headerRow = synthesizeHeader ? rows.get(0) : headers;
		int expectedColumns = headerRow == null ? 0 : headerRow.size();
		StringBuilder md = new StringBuilder();
		appendRow(md, headerRow, expectedColumns);
		md.append('|');
		for (int i = 0; i < expectedColumns; i++) {
			md.append("---|");
		}
		md.append('\n');
		if (rows != null) {
			for (List<String> row : rows) {
				appendRow(md, row, expectedColumns);
			}
		}
		return md.substring(0, md.length() - 1);
	}

	/** 返回表头单元格文本。 */
	public List<String> getHeaders() {
		return headers;
	}

	/** 返回数据行。 */
	public List<List<String>> getRows() {
		return rows;
	}

	/**
	 * 行渲染：单元格文本统一经 {@link MarkdownEscaper#escapeText} 转义（含竖线 → "\\|"，
	 * 及强调/标题等结构字符），规避表格语法破坏；行按 expectedColumns 归一化 ——
	 * 不足补空单元格、超出截断，杜绝合并单元格带来的锯齿行。
	 */
	private static void appendRow(StringBuilder md, List<String> cells, int expectedColumns) {
		md.append('|');
		int count = cells == null ? 0 : Math.min(cells.size(), Math.max(expectedColumns, 0));
		for (int i = 0; i < count; i++) {
			String cell = cells.get(i);
			md.append(' ').append(cell == null ? "" : MarkdownEscaper.escapeText(cell)).append(" |");
		}
		for (int i = count; i < expectedColumns; i++) {
			md.append("  |");
		}
		md.append('\n');
	}

	@Override
	public String toString() {
		return "DocxTable{headers=" + headers + ", rows=" + rows + "}";
	}
}
