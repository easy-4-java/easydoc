package io.github.easy4j.doc.xhtml.markdown;

/**
 * 结构化 Markdown 渲染选项（不可变），控制 HTML 颜色输出等行为。
 *
 * <p>默认 {@link #DEFAULT} 关闭所有 HTML 扩展——输出纯 GFM，与未引入本类前的
 * 行为字节级一致。开启 {@code renderHtmlColor} 后，表格单元格的字体色 / 背景色
 * 通过 {@code <span style="...">} 内联输出。</p>
 */
public final class MarkdownRenderOptions {

    /** 默认选项：全部关闭，输出纯 GFM。 */
    public static final MarkdownRenderOptions DEFAULT = of(false);

    private final boolean renderHtmlColor;

    private MarkdownRenderOptions(boolean renderHtmlColor) {
        this.renderHtmlColor = renderHtmlColor;
    }

    /**
     * 创建指定选项实例。
     *
     * @param renderHtmlColor 是否在单元格中渲染 {@code <span style="color:...">} 颜色
     * @return 新的选项实例
     */
    public static MarkdownRenderOptions of(boolean renderHtmlColor) {
        return new MarkdownRenderOptions(renderHtmlColor);
    }

    /**
     * 是否开启单元格级 HTML 颜色渲染。
     *
     * @return true 时表格单元格输出含 {@code <span>} 颜色标签
     */
    public boolean renderHtmlColor() {
        return renderHtmlColor;
    }
}
