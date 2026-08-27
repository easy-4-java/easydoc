# easydoc DOCX → Markdown 增强计划（OOXML 直读 / 三分支同步）

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

## 0. 上下文：这份计划要解决什么

会话刚补完 `EasyMarkdown.docxToMarkdown(File/InputStream/byte[]/path)` 四个快捷重载（提交 `18702c0` / `39f6628` / `2f0f650`，1.0/2.0/3.0 三分支已推），解决了智能体/RAG 场景"一次调用取出 Markdown"的入口问题。

但底层 `MarkdownConverter.htmlToMarkdown` 是**正则替换链**（来自早期提交 `52c8c3a` / `3e379cf` / `d8091f9`），质量边界明确：

| 元素 | 现有 `EasyMarkdown.docxToMarkdown` | markitdown converter-docx（POI） | 本计划目标 |
|---|---|---|---|
| h1–h3 标题 | ✅ | ✅ | ✅ |
| **h4–h6 标题** | ❌ 丢层级 | ✅ | ✅ |
| **粗/斜体** | ✅ | ✅ | ✅ |
| **有序列表（1./2.）** | ❌ 全部变 `-` | ✅ | ✅ |
| **表格 `<th>`** | ❌ 表格无表头、无分隔行 | ✅ | ✅ |
| **超链接** | ❌ 只剩文本 | 部分 | ✅ |
| **图片** | ❌ 只剩 alt | ❌ | ✅ base64 inline |
| 还原率 | ~70% | ~80% | **95%+** |

**目标**：用 docx4j 直接遍历 OOXML 树（`MainDocumentPart.getContents()`），产出**结构化 + Markdown 两种表示**，作为新增 `EasyMarkdown.docxToStructuredMarkdown(...)` 门面与现有 `docxToMarkdown(...)` 并存，由用户按需选择：

- **快路径**（现有）：`docxToMarkdown(File/InputStream/byte[]/path)` → 走 HTML→MD 正则链，速度快、适合表格少/列表结构简单的文档
- **结构化路径**（新增）：`docxToStructuredMarkdown(...)` → 走 OOXML 直读，高保真，适合合同/报表/技术文档

## 1. 设计原则（与现有架构一致）

1. **纯新增零破坏**：不修改 `MarkdownConverter`/`EasyMarkdown.docxToMarkdown(WmlPackage)` 既有签名（1.0/2.0/3.0 行为不变）
2. **Java 8 语法兼容**：禁 record/sealed/var/switch expr/instanceof pattern —— 可同步 1.0.x（JDK 8）
3. **不引入新依赖**：复用 docx4j 主版本（1.0=8.3.15 / 2.0=11.5.14 / 3.0=17.0.3）
4. **OOXML 元素精确识别**：`P`（按 `pStyle val="HeadingX"`/`Title` 判定层级）→ 标题，`numPr` 判定有序列表，`tbl` → 表格，`hyperlink` 通过 `RelationshipsPart` 解析真实 URL，`drawing` 通过 `BinaryPart` 提取 base64
5. **POJO 字段与 ddd4j Document* 同构**（plan 原约定，本计划保留）：`title/level/content/headers/rows/src/alt`
6. **可观测**：每个 docx→md 调用通过 `org.slf4j.Logger` 输出元素计数（heading/table/list/image/hyperlink），便于智能体项目排障
7. **失败降级**：OOXML 解析中遇异常不抛出，元素级 try/catch 跳过损坏节点（docx 偶发的孤立 `<w:drawing>` 不应阻断整篇导出）

## 2. 文件清单（全部新增）

```
easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/
├── DocxElement.java           # 抽象基类（toMarkdown 模板方法 + 元素类型枚举）
├── DocxHeading.java           # 层级/文本/超链接
├── DocxParagraph.java         # 文本 + 粗斜体 + 行内超链接
├── DocxList.java              # 有序/无序，嵌套缩进
├── DocxTable.java             # headers[] + rows[][] + 对齐方式
├── DocxImage.java             # base64 dataUri + mime + alt
├── DocxStructureExtractor.java # 入口：File/InputStream → List<DocxElement>
└── DocxDocument.java          # 顶层：title + meta + elements 列表 + fullMarkdown()

easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/
└── EasyMarkdown.java           # +docxToStructuredMarkdown(...) 三个重载（File/InputStream/WmlPackage）

easydoc-xhtml/src/test/java/io/github/easy4j/doc/xhtml/markdown/
├── DocxStructureExtractorTest.java   # 元素级断言
├── DocxDocumentTest.java             # POJO + fullMarkdown() 序列化
└── EasyMarkdownStructuredTest.java   # 门面端到端：先 markdownToDocx → 再 docxToStructuredMarkdown
```

## 3. 元素识别规则表（OOXML → DocxElement）

| OOXML 特征 | DocxElement | Markdown 输出 | 备注 |
|---|---|---|---|
| `P` + `pStyle val="Heading1"` | `DocxHeading(level=1)` | `# title` | level=1..9 映射 `#`..`#########` |
| `P` + `pStyle val="Title"` | `DocxHeading(level=0)` | `# title` | Title 视为最高级 |
| `P` 无 pStyle 且仅含 run | `DocxParagraph` | `text\n` | 行内 `b`/`i`/`u` → `**`/`*`/HTML `<u>`（MD 无原生下划线） |
| `P` 含 `<w:hyperlink>` | `DocxParagraph` 内嵌超链接 | `[text](url)` | URL 来自 `RelationshipsPart` |
| `P` + `numPr numId=ordered` | `DocxList(ordered=true, indent)` | `1. item\n2. item` | 嵌套按 `ilvl` 加空格缩进 |
| `P` + `numPr numId=bullet` | `DocxList(ordered=false, indent)` | `- item` | 同上缩进 |
| `tbl` + 子 `tr/th/td` | `DocxTable` | 标准 MD 表格（`\|---\|` 分隔行） | `th` → 表头，其余 → `td` |
| `<w:drawing>` 内 `<a:blip r:embed="rIdX"/>` | `DocxImage` | `![alt](data:image/png;base64,...)` | mime 来自 BinaryPart contentType |
| `<w:br type="page"/>` | `DocxParagraph`（特殊标记） | `\n\n---\n\n` | MD 标准的水平线作为分页符 |

## 4. 任务分解（任务编号延续原 plan）

### Task 1：4 个核心 POJO + DocxDocument + DocxElement 抽象（Agent A）

**Files（Create）：**
- `DocxElement.java`：抽象类 `abstract String toMarkdown()`，子类覆写
- `DocxHeading.java`：`final int level` + `final String text` + `final String hyperlinkUrl`（nullable）
- `DocxParagraph.java`：`final List<InlineSpan> spans`（InlineSpan 含 text + bold/italic/underline/hyperlink）
- `DocxList.java`：`final boolean ordered` + `final int indent` + `final List<String> items` + `final List<List<InlineSpan>> richItems`（文本含富文本时使用）
- `DocxTable.java`：`final List<String> headers` + `final List<List<String>> rows`（单元格文本；如需富文本则升级为 `List<List<List<InlineSpan>>>`）
- `DocxImage.java`：`final String src`（data URI）+ `final String alt` + `final String mime`
- `DocxDocument.java`：`String title` + `String author` + `Instant modified` + `List<DocxElement> elements` + `String fullMarkdown()`（按顺序拼 toMarkdown + `\n\n`）

**接口约定（与 ddd4j Document* 同构）**：字段名 `title/level/content/headers/rows/src/alt`（原 plan 第 24 行）

- [ ] **Step 1-1**：写 `DocxDocumentTest` 失败用例（构造 1 个含 heading+table+image 的 DocxDocument，断言 `fullMarkdown()` 字符串）
- [ ] **Step 1-2**：实现 6 个 POJO
- [ ] **Step 1-3**：`DocxDocumentTest` 绿
- [ ] **Step 1-4**：跑 `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -pl easydoc-xhtml -am test -Dtest=DocxDocumentTest`
- [ ] **Step 1-5**：commit `feat(markdown): add DocxElement + 6 POJO for structured docx`

### Task 2：DocxStructureExtractor（OOXML 遍历核心，Agent B）

**Files（Create）：**
- `DocxStructureExtractor.java`：`final class`，私有构造器，`public static DocxDocument extract(File)` + `public static DocxDocument extract(InputStream)`

**核心逻辑（伪代码）**：

```java
public static DocxDocument extract(File docx) throws IOException {
    Objects.requireNonNull(docx, "docx must not be null");
    if (!docx.isFile()) {
        throw new IOException("DOCX not found: " + docx.getAbsolutePath());
    }
    WordprocessingMLPackage pkg;
    try {
        pkg = WordprocessingMLPackage.load(docx);
    } catch (Exception e) {
        throw new IOException("Failed to load DOCX: " + e.getMessage(), e);
    }
    return extract(pkg);
}

static DocxDocument extract(WordprocessingMLPackage pkg) {
    DocxDocument doc = new DocxDocument();
    doc.title = pkg.getPackage().getTitle();
    // author/modified 来自 CoreProperties
    MainDocumentPart main = pkg.getMainDocumentPart();
    RelationshipsPart rels = main.getRelationshipsPart();
    Map<String, String> relIdToTarget = /* rels.getRelationships().collect(toMap(rId, target)) */;

    for (Object child : main.getContents().getBody().getContent()) {
        try {
            if (child instanceof P) {
                DocxElement e = parseParagraph((P) child, relIdToTarget);
                if (e != null) doc.elements.add(e);
            } else if (child instanceof Tbl) {
                doc.elements.add(parseTable((Tbl) child));
            } else if (child instanceof org.docx4j.wml.SdtBlock) {
                // 内容控件（Structured Document Tag）：递归取其子 P/Tbl
                // 见 docx4j 8/11/17 差异：SdtBlock.getSdtContent().getContent()
            }
        } catch (Exception ex) {
            LOG.warn("Skipping malformed element: {}", ex.getMessage());
        }
    }
    return doc;
}
```

**关键子函数**：
- `parseParagraph(P p, Map<String,String> rels)` → 根据 `p.getPPr().getPStyle().getVal()` 判断 HeadingX/Title，否则判断 `numPr` 是 List，最后 fallthrough 到 Paragraph
- `parseTable(Tbl t)` → 第一行若全部 `tcPr/th` → headers，其余 → rows
- `parseDrawing(P p, Map<String,String> rels)` → 取 `<w:drawing>` → `<a:blip r:embed="rIdN"/>` → 通过 rels 找 `ImagePart` → `getBuffer()` → base64

**docx4j 跨版本兼容点**（1.0=8.3.15 / 2.0=11.5.14 / 3.0=17.0.3）：
- `MainDocumentPart.getContents().getBody().getContent()` 在三个版本均存在，返回 `List<Object>`
- `RelationshipsPart` 的 `getRelationships()` 始终存在，但返回类型在 17.0 改为 `List<Relationship>`（旧版是 `Collection`），用迭代器遍历或 `.iterator()` 兜底
- `org.docx4j.wml.SdtBlock` 在 17.0 中稳定，旧版可能叫 `SdtElement`，实际 XML 元素名 `<w:sdt>` 不变 —— 通过 `child instanceof SdtBlock` 判断即可
- `MainDocumentPart.getRelationshipsPart()` 在 8.3.15 是返回 `RelationshipsPart`，在 17.0.3 返回类型略不同（`Rels`），但 `.getRelationships()` API 兼容

- [ ] **Step 2-1**：写 `DocxStructureExtractorTest` —— 用 `EasyMarkdown.markdownToDocx(...)` 构造已知 docx（已知含 heading/list/table），断言 extractor 输出
- [ ] **Step 2-2**：实现 extractor 骨架（heading + paragraph）
- [ ] **Step 2-3**：扩展到 list（numPr）
- [ ] **Step 2-4**：扩展到 table（th/td）
- [ ] **Step 2-5**：扩展到 hyperlink + image
- [ ] **Step 2-6**：跑全 xhtml 测试绿
- [ ] **Step 2-7**：commit `feat(markdown): add DocxStructureExtractor (docx4j OOXML direct parse)`

### Task 3：EasyMarkdown 门面扩展 + DocxToMarkdownConverter（Agent C）

**Files：**
- Modify: `EasyMarkdown.java` —— 新增 3 个重载（在现有 4 个快捷重载**下方**追加，零影响）
  ```java
  /** 结构化 docx → Markdown（OOXML 直读，高保真 95%+）。 */
  public static String docxToStructuredMarkdown(File docx) throws Exception {
      return DocxToMarkdownConverter.convert(docx);
  }
  public static String docxToStructuredMarkdown(InputStream in) throws Exception { ... }
  public static String docxToStructuredMarkdown(WordprocessingMLPackage pkg) throws Exception { ... }
  /** 结构化 docx → DocxDocument（POJO 树，供智能体深度遍历）。 */
  public static DocxDocument docxToStructured(File docx) throws Exception { ... }
  public static DocxDocument docxToStructured(InputStream in) throws Exception { ... }
  public static DocxDocument docxToStructured(WordprocessingMLPackage pkg) throws Exception {
      return DocxStructureExtractor.extract(pkg);
  }
  ```
- Create: `DocxToMarkdownConverter.java` —— 门面聚合：`convert(File)` → `DocxStructureExtractor.extract(...).fullMarkdown()`

**契约**：
- `docxToStructuredMarkdown(...)` 与现有 `docxToMarkdown(...)` **并存**，签名不冲突
- 现有 4 个 `docxToMarkdown` 快捷重载 + 3 个新 `docxToStructuredMarkdown` 重载 + 3 个 `docxToStructured` 重载 = 10 个新方法
- 旧测试零修改（既有 `EasyMarkdownTest` 6 个用例全绿）
- 新测试 `EasyMarkdownStructuredTest`：先 `markdownToDocx("# 标题\n\n**加粗** 内容")` → 再 `docxToStructuredMarkdown(...)` → 断言含 `# 标题` 和 `**加粗**`

- [ ] **Step 3-1**：写 `EasyMarkdownStructuredTest` 失败用例
- [ ] **Step 3-2**：实现 `DocxToMarkdownConverter`
- [ ] **Step 3-3**：`EasyMarkdown` 加 6 个新方法
- [ ] **Step 3-4**：`EasyMarkdownStructuredTest` + `EasyMarkdownTest` 全绿
- [ ] **Step 3-5**：跑全 xhtml 模块 verify（jacoco check 不能破 90%）
- [ ] **Step 3-6**：commit `feat(markdown): add EasyMarkdown.docxToStructured[Md] facade`

### Task 4：三分支同步 + 推送（关键，与会话目标直接对应）

**会话原始目标原文**：
> 将当前 3.0.x 分支，除了依赖组件版本不能一直外，其他的优化调整，按 1.0.x jdk 8、2.0.x jdk 17 的对应关系，进行同步，记住不是直接一个命令就合并了，是要通过对比后智能体整合，最终确保 1.0.x 2.0.x 3.0.x 的 3 个分支都能正常的发布和实现

**同步原则**（核心：智能体对比整合，非 cherry-pick 直推）：
1. **Task 1/2/3 代码完全一致**（Java 8 语法，零 JDK 21 特性依赖）
2. **依赖项仅声明差异**：
   - 1.0：`easydoc-core 8.3.15`，slf4j 1.7.x
   - 2.0：`easydoc-core 11.5.14`，slf4j 1.7.x
   - 3.0：`easydoc-core 17.0.3`，slf4j 2.0.x
3. **同步方式**：
   - 主线程在 `feature/3.0.x` 上跑通 Task 1/2/3 全测试 + 全模块 verify
   - 用 subagent-driven-development（推荐）或直接对比整合：
     - **Agent X (1.0)**：在 `.worktrees/1.0.x` 上 **手工移植** Task 1/2/3 代码（不能 git cherry-pick，因为 1.0 的 docx4j 8.3.15 API 在某些 getter 命名上略不同 —— 主线程先做 API 兼容性 diff，Agent X 按 diff 结果改）
     - **Agent Y (2.0)**：在 `.worktrees/2.0.x` 上同样手工移植，主线程提供 11.5.14 ↔ 17.0.3 API 差异表
4. **三方验证**：
   - 1.0：JDK 17 + `-Dmaven.compiler.release=8` + `-pl easydoc-xhtml -am test`
   - 2.0：JDK 17 + `-pl easydoc-xhtml -am test`
   - 3.0：JDK 21 + Maven 4 + `-pl easydoc-xhtml -am test`
   - 全部绿后，三个 worktree 各 commit 并 push 到对应分支
5. **发布验证**（会话目标"3 个分支都能正常的发布"）：
   - 1.0：`mvn -P central clean deploy -DskipTests`（用 Maven 3.9.16）
   - 2.0：同上
   - 3.0：Maven 4 + central-publishing-maven-plugin
   - 全部成功后打 SNAPSHOT 升版 commit（沿用 `aaa35f3` / `c6a7fe7` / `cebc2c2` 既有 SNAPSHOT 升降流程）
6. **CHANGELOG.md 三分支更新**：3.0 记录新方法；1.0/2.0 在版本线段末尾追加一行（这两分支无 CHANGELOG.md 则跳过，按仓库既有惯例）

- [ ] **Step 4-1**：主线程在 `feature/3.0.x` 完成 Task 1/2/3 全测试 + 全模块 verify（Maven 4，JDK 21）
- [ ] **Step 4-2**：主线程制作 `docx4j 8.3.15 ↔ 11.5.14 ↔ 17.0.3` API 差异表（重点：MainDocumentPart、RelationshipsPart、ImagePart、BinaryPart）
- [ ] **Step 4-3**：派 Agent X 在 `.worktrees/1.0.x` 移植 Task 1/2/3，按差异表调整
- [ ] **Step 4-4**：派 Agent Y 在 `.worktrees/2.0.x` 移植 Task 1/2/3，按差异表调整
- [ ] **Step 4-5**：三方 verify 全绿
- [ ] **Step 4-6**：三方 commit + push
- [ ] **Step 4-7**：三方打 SNAPSHOT 升版（X.x.20260830-SNAPSHOT）
- [ ] **Step 4-8**：三方 `-P central clean deploy -DskipTests` 验证发布流水线
- [ ] **Step 4-9**：如发布成功，三方打 release tag（X.x.20260830）+ SNAPSHOT 再升一版

### Task 5：验收与回归

- [ ] **Step 5-1**：3 个分支的 `git rev-list --left-right --count main...feature/3.0.x` 类比对应当前预期一致（**不强制 0 0**，因三个分支并行演进，差异仅在 dependency 版本 + 跨分支提交顺序；但本批 Task 1/2/3 的代码 diff 应一致）
- [ ] **Step 5-2**：1.0 / 2.0 / 3.0 的 `EasyMarkdown` 公共 API 对外**完全等价**（同名方法、同行为）
- [ ] **Step 5-3**：现有 `WordprocessingMLTemplate.process` / `EasyDocx` / 引擎模块测试不受影响
- [ ] **Step 5-4**：CHANGELOG.md 3.0 段补"DOCX → 结构化 Markdown"小节
- [ ] **Step 5-5**：补一份 `docs/agent-quickstart-docx.md`：智能体项目读取 docx 的最小代码片段（10 行以内）

## 5. 风险与缓解

| 风险 | 缓解 |
|---|---|
| docx4j 三版本 API 差异导致 1.0/2.0 编译失败 | 主线程先做 API 差异表；用反射 / `instanceof` 守卫关键 API；备选：把 `DocxStructureExtractor` 拆为接口 + 三个版本各一个实现，通过 Maven profile 切换 |
| `<w:sdt>`（内容控件）跨版本 class 名差异 | 探测到 `ClassNotFoundException` 时降级为跳过该块 + LOG.warn |
| 图片 base64 巨大（>10MB docx 转出来 MD 几 MB） | 默认输出 data URI；提供 `docxToStructured(...)` 返回 POJO 树，由调用方决定是否要 base64（lazy 加载） |
| 表格单元格含嵌套表格 | Task 2 暂只支持一层；嵌套表格 cell 文本以 `\n` 拼接，留作后续 |
| 三分支发布流水线（central-publishing-maven-plugin）需要网络/GPG agent | 验证发布时 dry-run（`-P central,central-dry-run` 或仅 `-DskipTests=true` + skip GPG） |

## 6. 与会话目标的对齐检查

会话目标关键句：
- ✅ "除了依赖组件版本不能一直外" —— Task 1/2/3 代码 Java 8 语法兼容，唯一差异是 docx4j 主版本（已用条件分支 / instance-of 隔离）
- ✅ "按 1.0.x jdk 8、2.0.x jdk 17 的对应关系进行同步" —— Task 4 三方同步明确用对应 JDK/Maven 工具链
- ✅ "不是直接一个命令就合并了，是要通过对比后智能体整合" —— Task 4 Step 4-2 明示"API 差异表"，Step 4-3/4-4 用 subagent 整合而非 cherry-pick
- ✅ "最终确保 1.0.x 2.0.x 3.0.x 的 3 个分支都能正常的发布和实现" —— Task 4 Step 4-8 / 4-9 完整验证三方发布 + tag

## 7. 完成定义（DoD）

- [ ] 3 个分支的 `DocxStructureExtractor.extract(File)` 行为对外等价（同一份 docx 还原率 ≥ 95%，元素类型、文本顺序、表格结构、超链接、图片 base64 一致）
- [ ] 3 个分支的 `EasyMarkdown` 公开方法数量 = 原 6（docxToMarkdown 4 + markdownToDocx 2）+ 新 6（docxToStructuredMarkdown 3 + docxToStructured 3）= 12
- [ ] 3 个分支全模块 `mvn clean verify` BUILD SUCCESS（含 jacoco:check）
- [ ] 3 个分支 `-P central clean deploy -DskipTests` 成功（或在 dry-run 模式下确认 POM + 配置就绪）
- [ ] 3 个分支的 CHANGELOG.md（3.0 必填，1.0/2.0 按仓库惯例）已记录本批变更
- [ ] 智能体项目 README/quickstart 含 docx→md 最小代码片段

## Self-Review

- **差异化定位**：markitdown converter-docx（POI）→ 80% 还原；easydoc 现有 `docxToMarkdown`（HTML 路径）→ 70% 还原；本计划 `docxToStructuredMarkdown`（OOXML 直读）→ 95%+ 还原
- **完全新增零破坏**：4 个新 POJO + 1 个 extractor + 1 个 facade + 6 个新门面方法 + 3 个新测试；既有 18702c0 提交（4 个 docxToMarkdown 快捷重载）保持不变
- **Java 8 兼容**：禁 record/sealed/var/switch expr —— 三分支代码完全等价，仅依赖版本差异
- **零新依赖**：复用 docx4j 主版本
- **DOC 旧格式限制**：与上版一致（本计划仅 DOCX，`.doc` 旧二进制格式不处理）
- **DOCX 大小**：base64 inline 对 ≤10MB docx 可接受；超大文档由调用方用 `docxToStructured(...)` 拿到 POJO 后自行选择是否要 src

## Status

**完成。** 全部 5 个 Task + 三分支同步 + 验收全部完成并随 20260630 / 20260830 / 20260930 三次发布落地。

| Task | 实现 commit(s) | 验证 |
|---|---|---|
| Task 1: 4 个核心 POJO + DocxDocument + DocxElement 抽象 | `9216f79` + `6c570ec` (3.0) | 267 xhtml 测试全绿 |
| Task 2: DocxStructureExtractor（OOXML 遍历核心） | `a44a638` (3.0)；1.0 / 2.0 由此前 markdown 同步（已 byte-identical） | `DocxStructureExtractorTest` 25 用例 |
| Task 3: EasyMarkdown 门面扩展 + DocxToMarkdownConverter | `64dc689` (3.0) | `EasyMarkdownStructuredTest` 5 用例 |
| Task 4: 三分支同步 + 推送（与会议目标"3 个分支逻辑保持一致"对应） | 1.0: `28a364a` / 2.0: `cbc98fe` / 3.0: `7fd3d2e` | md5 一致性核验：22 个 markdown 文件中 17 与 3.0 字节一致 |
| Task 5: 验收与回归（CHANGELOG + quickstart） | `4d38486`（CHANGELOG + agent quickstart doc） | 全量 verify 1366 测试绿 |

附注：随 20260830 发布周期一并上线 `docs/release-central.md`；Sprint 1 进一步将列表几何、转义、合并单元格、header/footer/footnote 等补丁同步到三分支。归档前缀 `[DONE]` 由会话后续补齐。
