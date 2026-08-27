package io.github.easy4j.doc.xhtml.markdown;

/**
 * 单元格文本及其可选样式（字体颜色 / 背景色），供结构化 Markdown 路径的表格渲染使用。
 *
 * <p>颜色字段为 6 位十六进制字符串（大写或小写均可，渲染前统一 {@code toUpperCase()} 归一化），
 * 例如 {@code "FF0000"} 表示红色。{@code null} 表示无该样式——主题色（{@code theme=}）
 * 因解析复杂首版不支持，也返回 {@code null}（不渲染）。</p>
 *
 * @param text              单元格纯文本
 * @param fontColorHex      字体颜色（6 位 hex，可 null）
 * @param backgroundColorHex 背景颜色（6 位 hex，可 null）
 */
public record DocxCell(String text, String fontColorHex, String backgroundColorHex) {

    /** 空单元格常量（无文本、无样式）。 */
    public static final DocxCell EMPTY = new DocxCell("", null, null);

    /**
     * 是否携带任意颜色样式（字体色或背景色至少一个非 null）。
     *
     * @return 有颜色样式时返回 true
     */
    public boolean hasStyle() {
        return fontColorHex != null || backgroundColorHex != null;
    }
}
