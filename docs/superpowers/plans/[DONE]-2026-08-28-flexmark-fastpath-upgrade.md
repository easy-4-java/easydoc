# 快速路径 docx→MD 改用 flexmark-html2md + 评估 docx-converter 反向

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development. Steps use checkbox (`- [ ]`) syntax for tracking.

## Goal

1. **快速路径 docx→MD 保真度升级**：把 `MarkdownConverter.htmlToMarkdown`（正则替换，~70% 保真，Sprint 1 审计 F1 系列问题根源）替换为 `FlexmarkHtmlConverter.convert(String html)`（flexmark-html2md-converter，AST 驱动），解决标题带属性/嵌套列表/表格/代码块等正则失效场景。
2. **评估 flexmark-docx-converter 反向（MD→docx 原生渲染）**：验证 `com.vladsch.flexmark:flexmark-docx-converter:0.64.8` 与 easydoc 三分支的兼容性，输出评估结论（POC + 报告），**默认不引入**——如果评估通过且价值明确，可作为独立 plan 落地。

## Background facts (verified this session)

- `MarkdownConverter.htmlToMarkdown` 用 12 个 `.replaceAll` 正则映射（`MarkdownConverter.java:40-62`），对带属性标签/嵌套结构/有序列表编号/锚点/内联代码全部失真——F1 系列审计项的根源
- 唯一调用方：`EasyMarkdown.docxToMarkdown(WordprocessingMLPackage)` 快路径（`EasyMarkdown.java:90`），即 docx4j `Docx4J.toHTML` 导出 HTML 后走 `htmlToMarkdown`
- flexmark 版本 `0.64.8`（根 pom `flexmark.version`），`flexmark-html2md-converter` 和 `flexmark-docx-converter` 两个构件在 Central 和本地 .m2 均存在 0.64.8
- `FlexmarkHtmlConverter` 公开 API：`public String convert(String html)` + 一系列 DataKey 选项（`MAX_BLANK_LINES`/`LIST_CONTENT_INDENT`/`SETEXT_HEADINGS`/`OUTPUT_UNKNOWN_TAGS` 等），内部用 jsoup 解析 HTML（构件传递依赖已含 jsoup）
- easydoc-xhtml 当前依赖 flexmark 4 个构件（flexmark + ext-tables + ext-gfm-strikethrough + ext-autolink，Sprint 1 #24 最小集）

## Scope

- 仅 easydoc-xhtml 模块（3.0.x 分支）
- 替换 `MarkdownConverter.htmlToMarkdown` 的**内部实现**（保持方法签名与 null 语义，API 不变）
- 评估 flexmark-docx-converter 兼容性（只读 POC，不提交依赖）
- **不做**：结构化路径（DocxStructureExtractor）改造、PDF、1.0/2.0 分支（controller 后续同步）

## Design

### 改造 1：htmlToMarkdown 用 FlexmarkHtmlConverter 重写

**原则**：`MarkdownConverter.htmlToMarkdown(String html)` 签名、null→空串语义、返回类型完全不变，仅内部实现替换。

```java
// 静态构建一次（线程安全：FlexmarkHtmlConverter 构建后不可变，与现有 Parser/Renderer 同模式）
private static final FlexmarkHtmlConverter HTML_TO_MD = FlexmarkHtmlConverter.builder(options).build();

public static String htmlToMarkdown(String html) {
    if (html == null) {
        return "";
    }
    return HTML_TO_MD.convert(html);
}
```

**选项配置**（需用测试实证后定稿）：
- `FlexmarkHtmlConverter.OUTPUT_UNKNOWN_TAGS` — docx4j HTML 导出会产出 `<span style="...">` 等未知标签；保真优先时应设为 true（输出为内联 HTML），纯 GFM 承诺优先时 false。**测试后定**
- `FlexmarkHtmlConverter.MAX_BLANK_LINES` / `MAX_TRAILING_BLANK_LINES` — 控制空行压缩
- `FlexmarkHtmlConverter.LIST_CONTENT_INDENT` — 列表缩进

**行为差异点（需测试覆盖）**：
| 场景 | 正则旧实现 | html2md 新实现 |
|---|---|---|
| 标题带属性 `<h1 style="...">` | 正则丢属性但保留文本 | AST 正确解析 |
| 嵌套列表 `<ul><li><ul>...` | 正则拉平 | 正确嵌套 |
| 有序列表 `<ol><li>1. x` | 正则全部 `- ` | 保留有序编号 |
| 表格 `<table><thead>...` | 无 GFM 分隔行（F1 旧病） | 产出 GFM 表格 |
| 内联代码 `<code>foo</code>` | 正则剥掉 code | 保留 `` `foo` `` |
| 锚点 `<a href="...">` | 正则剥掉 href | 保留 `[text](href)` |

### 改造 2：评估 flexmark-docx-converter（只读 POC，不提交）

在临时目录（非 easydoc 仓库）写一个 30 行 POC：
1. `mvn dependency:get -Dartifact=com.vladsch.flexmark:flexmark-docx-converter:0.64.8`
2. 用 `com.vladsch.flexmark.docx.converter.DocxRenderer` 把一段 markdown 转成 WordprocessingMLPackage → 输出临时 docx
3. 检查：是否需要 docx4j 特定版本（easydoc 3.0 用 17.0.3、2.0 用 11.5.14、1.0 用 8.3.15——flexmark-docx-converter 的 docx4j 依赖版本若高于某分支会冲突）
4. 输出评估表：每个分支的 docx4j 版本 vs flexmark-docx-converter 的 docx4j 依赖版本 → 兼容性结论

**评估通过标准**（满足才在独立 plan 落地）：
- 三个分支的 docx4j 版本 >= flexmark-docx-converter 编译所需的 docx4j 版本
- POC 能产出有效 docx
- 样式保真（标题/列表/表格）优于当前 HTML 中间层

## Tasks

### Task 1: 验证 FlexmarkHtmlConverter 行为（读 API + 小实验）
- [ ] 读 `FlexmarkHtmlConverter.builder()` / `convert(String)` / 关键 DataKey 文档（javap + 源码注释）
- [ ] 用 3 个代表性 HTML 输入（带属性标题、嵌套列表、表格）在临时测试里验证新实现输出，与旧正则输出对比
- [ ] 确定 `OUTPUT_UNKNOWN_TAGS` 等选项的最终取值
- [ ] 在 `MarkdownConverterTest` 追加失败用例（pin 新实现行为）
- `commit:` `test(xhtml): pin html2md behavior for attribute-heading/nested-list/table inputs`

### Task 2: 替换 htmlToMarkdown 实现
- [ ] `MarkdownConverter.java` 增加 `flexmark-html2md-converter` 依赖（easydoc-xhtml/pom.xml）
- [ ] `htmlToMarkdown` 内部替换为 `FlexmarkHtmlConverter`（保留签名/null 语义）
- [ ] 新增依赖构件 `flexmark-html2md-converter`（及传递依赖 `flexmark-ext-emoji` 等）到 easydoc-xhtml/pom.xml
- [ ] 全量测试绿（重点：`MarkdownConverterTest`、`EasyMarkdownTest` 快路径用例）
- `commit:` `feat(xhtml): replace regex htmlToMarkdown with flexmark-html2md (F1 fidelity fix)`

### Task 3: 兼容性验证
- [ ] 确认新依赖在 Maven 4 下 13 模块 reactor 全绿（3.0.x）
- [ ] 确认 `flexmark-html2md-converter` 的传递依赖（jsoup 版本）与 easydoc 现有 jsoup 无冲突
- [ ] CHANGELOG 追加条目（3.0.x Unreleased 段）
- `commit:` `docs(xhtml): CHANGELOG entry for html2md fast-path fidelity upgrade`

### Task 4: 评估 flexmark-docx-converter（只读 POC）
- [ ] `mvn dependency:get -Dartifact=com.vladsch.flexmark:flexmark-docx-converter:0.64.8` 成功
- [ ] 用 DocxRenderer 把一段 markdown 转 WordprocessingMLPackage → 临时 docx（在 /tmp，不进仓库）
- [ ] 查该构件 pom 的 docx4j 依赖版本，与三个分支的 docx4j 版本（17.0.3 / 11.5.14 / 8.3.15）对比
- [ ] 输出评估报告（写进 commit message + 汇报）：兼容性结论 + 样式保真对比 + 是否建议引入
- `commit:` `docs(plans): flexmark-docx-converter compatibility assessment`

## Definition of Done

1. `MarkdownConverter.htmlToMarkdown` 内部用 FlexmarkHtmlConverter，签名/null 语义不变
2. 全量 `mvn -Denforcer.skip=true -pl easydoc-xhtml -am clean verify` BUILD SUCCESS，jacoco 0 违规
3. 新增测试覆盖：带属性标题、嵌套列表、有序列表、表格、内联代码、锚点——6 场景至少各 1 断言
4. **向后兼容**：`MarkdownConverterTest` 现有用例全绿（若输出格式有合理变化，更新断言并注明理由）
5. flexmark-docx-converter 评估报告产出（兼容性结论明确，无论是否建议引入）
6. 不触碰结构化路径 / PDF / 1.0-2.0 分支
7. 不引入新依赖以外的意外变更
