# easydoc 结构化 Markdown 颜色渲染（开关版，默认 OFF）

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking. Each step ends with `commit:` and `verify:` lines.

## Goal

为 easydoc 的 **结构化 Markdown 路径**（`EasyMarkdown.docxToStructuredMarkdown(File)`）增加 **可选的 cell 级字体颜色 / 背景色渲染**，**默认 OFF**——保证不破坏既有"输出纯 GFM"承诺。

输出格式（开启时）：
```
| header | header |
| --- | --- |
| <span style="color:#FF0000;background-color:#FFFF00">cell text</span> | cell |
```

## Background facts (from this session's audit)

- `DocxStructureExtractor.extractRowCells(Tr, Set<Integer>)` 当前返回 `List<String>`（每格仅文本）
- `DocxTable` 字段：`headers: List<String>` + `rows: List<List<String>>`
- `DocxTable.appendRow(...)` 当前直接调 `MarkdownEscaper.escapeText(cell)` 然后拼接
- `MarkdownEscaper.escapeText(String)` 已存在，做 `*_[]#\`` 等转义
- `EasyMarkdown` 当前 6 个结构化门面都是 `docxToStructuredMarkdown(File/InputStream/WordprocessingMLPackage)` 单参数版本
- Sprint 1 audit 标记的 P2 项 #19（`throws Exception → IOException`）和 F2 转义层**已经实现**

## Scope boundaries (do NOT do)

- **PDF 路径不动**——颜色已由 docx4j XSL-FO 内置保留
- **快路径 Markdown 不动**（`EasyMarkdown.docxToMarkdown(...)` 用 `MarkdownConverter.htmlToMarkdown` 正则替换）——这条改用 flexmark HTML parser 是独立大工作
- **per-run 颜色不采**（只采整格 cell 级；rPr 内逐 run 颜色复杂度太高）
- **主题色解析**（`<w:color w:theme="1"/>` 复杂，theme→rgb 映射要查 styles.xml 第一版先不支持，返回 null 即不渲染）
- **跨分支同步**——本 plan 只在 3.0.x 分支上落地；1.0/2.0 同步由 controller 在后续 sprint 处理

## Design

### 数据模型变更

`DocxTable` 增加 cell 维度的样式，**不破坏 API**（保留旧构造器为 deprecated / 委托到新构造器，或保留旧字段 + 增加并行结构）。

**推荐**：把 `DocxTable` 的 `headers: List<String>` 改为 `List<DocxCell>`，`rows: List<List<String>>` 改为 `List<List<DocxCell>>`，新增 record：

```java
public record DocxCell(String text, String fontColorHex, String backgroundColorHex) {
    public static final DocxCell EMPTY = new DocxCell("", null, null);
    public boolean hasStyle() { return fontColorHex != null || backgroundColorHex != null; }
}
```

`DocxTable.appendRow` 改为接受 `List<DocxCell>`，渲染逻辑读 `cell.hasStyle()` 决定是否包 `<span>`。

### 渲染选项

新增 public sealed interface `MarkdownRenderOptions`：

```java
public final class MarkdownRenderOptions {
    private final boolean renderHtmlColor;

    private MarkdownRenderOptions(boolean renderHtmlColor) {
        this.renderHtmlColor = renderHtmlColor;
    }
    public boolean renderHtmlColor() { return renderHtmlColor; }

    public static final MarkdownRenderOptions DEFAULT = new MarkdownRenderOptions(false);
    public static MarkdownRenderOptions of(boolean renderHtmlColor) {
        return new MarkdownRenderOptions(renderHtmlColor);
    }
}
```

### 入口签名

在 `EasyMarkdown` 增加 3 个重载（仅 `docxToStructured(File/InputStream/WordprocessingMLPackage)`，3 个参数；不增加 6 个——`docxToStructured` 已经返回结构化 `DocxDocument`，POJO 调用方自然可以迭代 cell 自己决定渲染）。即：

```java
public static String docxToStructuredMarkdown(File docx, MarkdownRenderOptions opts) throws IOException;
public static String docxToStructuredMarkdown(InputStream in, MarkdownRenderOptions opts) throws IOException;
public static String docxToStructuredMarkdown(WordprocessingMLPackage pkg, MarkdownRenderOptions opts) throws IOException;
```

旧 3 个无 opts 重载委托到 `opts = DEFAULT`（保持字节级兼容）。

## Tasks

### Task 1: DocxCell record + MarkdownRenderOptions（基础类型）
**Files (Create):**
- `easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/DocxCell.java`
- `easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/MarkdownRenderOptions.java`

**`DocxCell`:**
- record，字段：`text`、`fontColorHex`、`backgroundColorHex`
- 静态 `EMPTY` 常量
- 方法 `hasStyle()` 当任一颜色非 null
- Javadoc 中文，说明 hex 格式（6 位大写或小写均可，渲染前 `.toUpperCase()` 归一化）

**`MarkdownRenderOptions`:**
- 私有构造器 + 静态工厂 `of(boolean)` + `DEFAULT = of(false)`
- 方法 `renderHtmlColor()`
- Javadoc 中文

**`verify:`** 编译通过，单元测试（同一文件内嵌 `@Test`）覆盖：构造、工厂、`hasStyle`、`EMPTY`。

**`commit:`** `feat(xhtml): add DocxCell record + MarkdownRenderOptions for cell-level color rendering`

### Task 2: DocxStructureExtractor 采集颜色
**Files (Modify):**
- `easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/DocxStructureExtractor.java`

**改动：**
1. `extractRowCells` 返回类型从 `List<String>` 改为 `List<DocxCell>`
2. 每个 cell 调用新私有方法 `extractCell(tc)` 返回 `DocxCell`
3. `extractCell(tc)` 从 `tc.getTcPr().getShd()` 取 `<w:shd w:fill="...">` 的 backgroundColorHex（只接受显式 `w:fill` 6 位 hex；`theme`/`auto` 返回 null）
4. `extractCell(tc)` 从 cell 第一个 run 的 `rPr.getColor()` 取 fontColorHex（同上：只接受显式 `w:val` 6 位 hex）
5. cell 文本通过现有 `flatText()` 拿
6. `parseTable` 内的 `headers` 和 `dataRows` 类型同步改为 `List<DocxCell>`
7. `flattenedCell` 同步返回 `List<DocxCell>`（gridSpan 展开时颜色重复）

**Helper:**
```java
private static String hex6FromString(String raw) {
    if (raw == null || raw.isEmpty()) return null;
    String s = raw.startsWith("#") ? raw.substring(1) : raw;
    if (s.length() != 6) return null;
    for (int i = 0; i < 6; i++) {
        char c = s.charAt(i);
        boolean ok = (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
        if (!ok) return null;
    }
    return s.toUpperCase();
}
```

**Tests:** 在 `DocxStructureExtractorExtendedTest`（3.0 已有）追加 3 个用例：
- 整格 `<w:color w:val="FF0000">` 出现在 cell 第一个 run → `DocxCell.fontColorHex == "FF0000"`
- `<w:shd w:fill="FFFF00">` → `DocxCell.backgroundColorHex == "FFFF00"`
- 主题色 `<w:color w:theme="1"/>` → null

**`verify:`** `mvn -Denforcer.skip=true -pl easydoc-xhtml -am test -Dtest='DocxStructureExtractorExtendedTest'` 全绿。

**`commit:`** `feat(xhtml): extract cell-level fontColor/backgroundColor in DocxStructureExtractor`

### Task 3: DocxTable 升级 + 渲染
**Files (Modify):**
- `easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/DocxTable.java`
- `easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/DocxToMarkdownConverter.java`

**`DocxTable` 改动：**
1. 字段：`headers: List<DocxCell>` + `rows: List<List<DocxCell>>`
2. 构造器签名变更；保留旧 `List<String>` 构造器为 **deprecated**（委托：`headers.stream().map(s -> new DocxCell(s, null, null)).toList()`）
3. `getHeaders()` 改为 `List<DocxCell>`；**新增** `getHeadersAsText()` 返回 `List<String>`（为旧外部调用方兜底，标记 deprecated）
4. 同理 `getRows()`
5. `appendRow` 重载接受 `List<DocxCell>`
6. 新增 `toMarkdown(MarkdownRenderOptions opts)` 重载（默认 `toMarkdown()` 委托 `toMarkdown(DEFAULT)`）
7. 渲染逻辑：
```java
for (DocxCell cell : cells) {
    md.append(' ');
    if (opts.renderHtmlColor() && cell.hasStyle()) {
        md.append("<span style=\"");
        if (cell.fontColorHex() != null) md.append("color:#").append(cell.fontColorHex()).append(';');
        if (cell.backgroundColorHex() != null) md.append("background-color:#").append(cell.backgroundColorHex()).append(';');
        md.append("\">");
        md.append(MarkdownEscaper.escapeText(cell.text()));
        md.append("</span>");
    } else {
        md.append(MarkdownEscaper.escapeText(cell.text()));
    }
    md.append(" |");
}
```

**`DocxToMarkdownConverter` 改动：**
- 调用 `DocxTable.toMarkdown(opts)` 而非 `toMarkdown()`

**Tests:** 在 `DocxTableTest` / `MarkdownEscaperTest` 旁新建或扩展 `MarkdownColorRenderingTest`：
- 默认 OFF：`cell = new DocxCell("bold", "FF0000", null)` → 渲染纯文本 `bold`，无 span
- 开启 + 字体色：`renderHtmlColor(true)` + 字体色 → `<span style="color:#FF0000">bold</span>`
- 开启 + 背景色：→ `<span style="background-color:#FFFF00">x</span>`
- 开启 + 双色：顺序 `color:;background-color:;`，逗号分隔
- 值含 `<>&` → 转义后 span 包裹完整（`<` → `&lt;`）
- 默认 OFF 时的输出与上一版本**字节级一致**（snapshot 测试）

**`verify:`** `mvn -Denforcer.skip=true -pl easydoc-xhtml -am test` 全绿，jacoco 0 违规。

**`commit:`** `feat(xhtml): render cell-level color via MarkdownRenderOptions (default OFF)`

### Task 4: EasyMarkdown 门面重载
**Files (Modify):**
- `easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/EasyMarkdown.java`

**改动：** 增加 3 个 `docxToStructuredMarkdown(X, MarkdownRenderOptions opts)` 重载。旧 3 个无 opts 委托 `opts = DEFAULT`。

**Tests:** 在 `EasyMarkdownStructuredTest`（3.0 已存在）追加 2 个用例：
- 开启选项 + 含颜色 docx → 输出含 `<span style="color:#XXX">`
- 默认（不传 opts）→ 输出与现状字节一致

**`verify:`** `mvn -Denforcer.skip=true -pl easydoc-xhtml -am test` 全绿。

**`commit:`** `feat(xhtml): expose docxToStructuredMarkdown(..., MarkdownRenderOptions)`

### Task 5: 文档与 CHANGELOG
**Files (Modify):**
- `CHANGELOG.md`
- `docs/agent-quickstart-docx.md`

**CHANGELOG:** 在 3.0.x 的 `## [3.0.x.20260831]` 段内追加：
```
### 颜色渲染（结构化 Markdown 路径）

新增 `MarkdownRenderOptions`（默认 OFF 保持"纯 GFM"承诺），开启 `renderHtmlColor(true)` 后
单元格级字体颜色（`<w:color w:val="FF0000"/>`）和背景色（`<w:shd w:fill="FFFF00"/>`）通过
`<span style="...">` 输出。主题色（`theme=`）解析复杂，首版不支持（返回 null 不渲染）。
PDF 路径颜色已由 docx4j XSL-FO 内置保留；快路径 Markdown 不在本 plan 范围。
```

**`docs/agent-quickstart-docx.md`:** 在第 3 节"结构化 POJO 树"下方追加：
```
### 颜色渲染（默认 OFF）

要开启单元格颜色输出，传入 `MarkdownRenderOptions.renderHtmlColor(true)`：

\```java
String md = EasyMarkdown.docxToStructuredMarkdown(new File("demo.docx"),
    MarkdownRenderOptions.of(true));
\```

输出形如 `<span style="color:#FF0000;background-color:#FFFF00">cell</span>`，
GitHub 上完整渲染；其它渲染器可能降级为源代码。不开启时输出与之前字节级一致。
```

**`verify:`** grep 验证两个文件包含新关键词。

**`commit:`** `docs(xhtml): document cell-level color rendering (MarkdownRenderOptions)`

## Definition of Done

ALL must be true:

1. `git log --oneline feature/3.0.x..HEAD` 包含 5 个 commit（Task 1-5），无合并提交
2. `mvn -Denforcer.skip=true -pl easydoc-xhtml -am clean verify` BUILD SUCCESS，jacoco 0 违规
3. 新增/扩展测试全绿；**默认 OFF 时输出与未改动版本字节级一致**（snapshot 测试）
4. `MarkdownRenderOptions.DEFAULT.renderHtmlColor() == false`
5. CHANGELOG 和 agent-quickstart 同步更新
6. 不修改任何 1.0/2.0 分支（controller 在后续 sprint 同步）
7. 不修改 PDF 路径、不修改快路径 Markdown、不修改 `MarkdownConverter.htmlToMarkdown`
8. 不引入新依赖
