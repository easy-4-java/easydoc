package io.github.easy4j.doc.xhtml.markdown;

/**
 * docx 文本 → Markdown 的转义层（包私有工具，audit2 F2）。
 *
 * <p>DOCX 正文对用户输入不做任何标记假设，但 Markdown 里的
 * {@code * _ ` [ ] # ! | < > ~ \\} 等字符具有结构语义：不转义会把正文
 * 误渲染为强调/链接/标题/表格分隔线，甚至让 GFM 解析器把文本中的
 * {@code <script>} 当作原始 HTML 注入。故所有面向 Markdown 渲染的文本出口
 * （行内片段、标题、表格单元格、图片 alt）与 URL 目标都必须经过本类。</p>
 *
 * <p>转义集合说明（对照 CommonMark 可转义字符集与 flexmark 的 GFM 约定）：</p>
 * <ul>
 * <li>基础结构字符：{@code * _ ` [ ] # ! \\} —— 强调、代码、链接、ATX 标题、图片。</li>
 * <li>GFM 扩展字符：{@code | ~} —— 表格分隔线与删除线；{@code < >} 同时阻断
 * autolink 误判与原始 HTML 注入。</li>
 * <li>反斜杠最先转义，保证后续替换产生的是字面量而非新的转义序列。</li>
 * </ul>
 */
final class MarkdownEscaper {

	private MarkdownEscaper() {
	}

	/** 单字符 → 反斜杠转义映射（先处理 {@code \\} 本身，再遍历其余）。 */
	private static final String ESCAPED_CHARS = "\\`_*[]#!|<>~";

	/**
	 * 转义行内/块级文本中的 Markdown 结构字符。
	 * 另外中和首字符的块级起始序列（{@code - + > =} 及 {@code "1."/"1)" 序号}），
	 * 避免段落开头被解析为列表/引用/Setext 标题。
	 */
	static String escapeText(String raw) {
		if (raw == null || raw.isEmpty()) {
			return raw;
		}
		StringBuilder sb = new StringBuilder(raw.length() + 8);
		for (int i = 0; i < raw.length(); i++) {
			char c = raw.charAt(i);
			if (ESCAPED_CHARS.indexOf(c) >= 0) {
				sb.append('\\').append(c);
			} else {
				sb.append(c);
			}
		}
		disarmBlockStart(sb);
		return sb.toString();
	}

	/**
	 * 折叠标题文本内的换行（w:br、软换行、制表符均视为一个空格），并去除首尾空白：
	 * 标题必须是单行 ATX；若标题首个子元素是换行，trim 后自然归零。
	 */
	static String collapseLineBreaks(String raw) {
		if (raw == null || raw.isEmpty()) {
			return raw;
		}
		StringBuilder sb = new StringBuilder(raw.length());
		boolean pendingSpace = false;
		for (int i = 0; i < raw.length(); i++) {
			char c = raw.charAt(i);
			if (c == '\n' || c == '\r' || c == '\t') {
				pendingSpace = true;
				continue;
			}
			if (c == ' ') {
				pendingSpace = true;
				continue;
			}
			if (pendingSpace && sb.length() > 0) {
				sb.append(' ');
			}
			pendingSpace = false;
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 转义链接/图片的 URL 目标：含空白、圆括号或尖括号的目标按百分号编码
	 * （空格→%20 等），规避 {@code (...)} 目标串提前闭合；其余（含 data URI 的
	 * {@code + / =} 等 base64 字符）原样保留。null 返回 null。
	 */
	static String escapeUrl(String raw) {
		if (raw == null || raw.isEmpty()) {
			return raw;
		}
		boolean needsEncoding = false;
		for (int i = 0; i < raw.length(); i++) {
			char c = raw.charAt(i);
			if (c <= ' ' || c == '(' || c == ')' || c == '<' || c == '>') {
				needsEncoding = true;
				break;
			}
		}
		if (!needsEncoding) {
			return raw;
		}
		StringBuilder sb = new StringBuilder(raw.length() + 8);
		for (int i = 0; i < raw.length(); i++) {
			char c = raw.charAt(i);
			switch (c) {
				case ' ':
					sb.append("%20");
					break;
				case '\t':
					sb.append("%09");
					break;
				case '\n':
					sb.append("%0A");
					break;
				case '\r':
					sb.append("%0D");
					break;
				case '(':
					sb.append("%28");
					break;
				case ')':
					sb.append("%29");
					break;
				case '<':
					sb.append("%3C");
					break;
				case '>':
					sb.append("%3E");
					break;
				default:
					sb.append(c);
			}
		}
		return sb.toString();
	}

	/** 中和块级起始序列：仅当结果以敏感字符开头时补转义，避免全局替换带来的噪音。 */
	private static void disarmBlockStart(StringBuilder sb) {
		if (sb.length() == 0) {
			return;
		}
		char first = sb.charAt(0);
		if (first == '-' || first == '+' || first == '>' || first == '=') {
			sb.insert(0, '\\');
			return;
		}
		// "1." / "12)" 形式的序号起始：紧贴数字后补反斜杠使其不再是列表标记
		int digits = 0;
		while (digits < sb.length() && Character.isDigit(sb.charAt(digits))) {
			digits++;
		}
		if (digits > 0 && digits < sb.length()
				&& (sb.charAt(digits) == '.' || sb.charAt(digits) == ')')) {
			sb.insert(digits, '\\');
		}
	}
}
