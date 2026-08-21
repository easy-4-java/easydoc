# easydoc 3.0.x 迁移指南

本文件记录从 2.x（或 3.0.x 早期快照）升级到当前 3.0.x 的破坏性变更与迁移路径。

## 1. 依赖变更

| 变更 | 说明 |
|---|---|
| **docx4j-JAXB-MOXy → docx4j-JAXB-ReferenceImpl** | MOXy 2.7.6 编译自 `javax.xml.bind`（JAXB 2.x），在 JDK 21 + `jakarta.xml.bind-api` 4.x 下不可用（SPI 不匹配 + 命名空间冲突），导致 `WordprocessingMLPackage.load()` 直接 NPE。3.0.x 使用 ReferenceImpl + `org.glassfish.jaxb:jaxb-runtime` 4.0.x。 |
| **ognl 3.2.13 → 3.3.4** | Thymeleaf 3.1.x 需要 `ognl.AbstractMemberAccess`（3.3.4+ 才提供）。`MemberAccess` 接口签名不变，`DefaultMemberAccess` 无需改动。 |
| **antlr4-runtime 4.13.2 → 4.9.3** | Beetl 3.21.2 官方适配 `antlr4.9-support`；4.13.2 会抛 "不支持的antlr版本"。 |
| **CVE 升级** | thymeleaf 3.1.3→3.1.5.RELEASE（3 个 CRITICAL）、jsoup 1.22.2→1.23.1（MEDIUM XSS）、assertj 3.27.3→3.27.7（HIGH XXE）。 |
| **velocity-tools 保持 2.0** | 3.x 是 jakarta-only，会破坏 `javax.servlet` 的 `WebappLoader` 默认值。已知 MEDIUM XSS（仅影响 HTML 视图渲染工具，本库只用 `DateTool`，docx 生成不受影响）。计划在后续 major 迁移。 |

## 2. API 变更

### 2.1 `WmlElementUtils` 已拆分（@Deprecated 门面）

`WmlElementUtils` 拆分为 6 个职责类，原类保留为 `@Deprecated(since="3.0.x", forRemoval=true)` 门面，**3.x 全周期可用，计划 4.0 移除**：

| 原方法（WmlElementUtils.*） | 新位置 |
|---|---|
| `getChildrenElements` / `getTargetElements` / `getAllElementFromObject` / `getElementContent` | `WmlElementTraversal` |
| 表格/行/单元格相关（`getTable`/`mergeCellsHorizontal*`/`addTrByIndex` 等） | `WmlTableUtils` |
| 字符格式（`getRPr`/`addRPr*Style`/`setRPrVanishStyle`） | `WmlRunStyleUtils` |
| 段落相关（`setPara*`/`addInlineImageToParagraph`/`getPPr` 等） | `WmlParagraphUtils` |
| 节级（`createFooter`/`setDocumentSize`/`setDocMarginSpace` 等） | `WmlSectionUtils` |
| 文档级（`createWordprocessingMLPackage`/`loadWordprocessingMLPackage*`/`addImage` 等） | `WmlDocumentUtils` |

迁移示例：
```java
// 2.x
WmlElementUtils.setTblBorders(tbl, top, bottom, left, right, insideH, insideV);
// 3.x
WmlTableUtils.setTblBorders(tbl, top, bottom, left, right, insideH, insideV);
```

### 2.2 `WordprocessingMLPackageBuilder.buildWhith*` → `buildWith*`

24 个方法名拼写修正（`buildWhith` → `buildWith`，缺 't'）。旧名保留为 `@Deprecated(forRemoval=true)` 转发，**3.x 全周期可用，4.0 移除**。

```java
// 2.x / 3.0 早期
builder.buildWhithXhtml(html, landscape, altChunk);
// 3.x
builder.buildWithXhtml(html, landscape, altChunk);
```

### 2.3 JSP 模块：独立引擎 → Servlet 容器 shim

`easydoc-jsp` 的手写 JSP 引擎（~15 个类）已替换为基于 `RequestDispatcher.include` 的 shim。

**破坏性变更**：`WordprocessingMLJspTemplate` 现在**必须运行在 Servlet 容器内**（需要有效的 `HttpServletRequest`/`HttpServletResponse`），且已删除 `getEngine()/setEngine()/getInternalEngine()` 方法。

```java
// 3.x 用法（Servlet 环境内）
WordprocessingMLJspTemplate tpl =
    new WordprocessingMLJspTemplate(request, response, "/WEB-INF/views/foo.jsp", "/foo.jsp");
WordprocessingMLPackage pkg = tpl.process("ignored", variables);
```

依赖：`org.apache.tomcat:tomcat-jasper` 9.0.x（javax 命名空间；Tomcat 10+ 是 jakarta 不兼容）。

## 3. 行为变更

| 变更 | 说明 |
|---|---|
| **FreeMarker 不再污染调用方 Map** | `render()` 不再向调用方的 `variables` 写入 `"String"` 键，改为防御性拷贝。依赖该副作用的代码需改为显式传参。 |
| **Beetl 定界符默认值修正** | `placeholderEnd` 默认值从 `<%` 修正为 `}`；`statementStart` 默认值从 `%>` 修正为 `<%`（与 Beetl 官方默认一致）。配置了自定义定界符的用户不受影响。 |
| **Zip 输出完整** | `ZipFolderHelper` 现在正确关闭 `ZipOutputStream`，产出含 END 记录的合法 zip（此前产出损坏 zip）。 |
| **`unzip` 拒绝路径穿越条目** | 恶意 zip 条目（如 `../../evil.txt`）现在抛 `IOException` 而非写出目标目录。 |

## 4. 已知限制（3.0.x）

| 限制 | 说明 | 缓解 |
|---|---|---|
| **SAX 模板在 JDK 21 不可用** | docx4j 11.5.14 的 `SAXHandler` 在 JDK 21 下无法触发 `setContentHandler` 回调（"Transformer didn't set ContentHandler"）。`WordprocessingMLDocxSaxTemplate.process()` 现在会 **fail-fast** 抛 `UnsupportedOperationException` 并提示替代方案。 | 用 `DocxTemplates.create(DocxMode.DEFAULT)` 或 `DocxMode.STAX`；或 JDK 17 运行。 |
| **`IdentityPlusMapper` 在 JVM 21 初始化失败** | `WordprocessingMLPackageBuilder.configChineseFonts/configDefaultFont/configSimSunFont` 触发 docx4j 字体映射初始化失败（FOP 字体读取断言）。 | 3 个方法的相关测试已 @Disabled 并记录；运行时若触发会在日志中体现。 |
| **velocity-tools 2.0（javax）** | 与 Jakarta 生态的长期不一致。 | 见 §1。 |
