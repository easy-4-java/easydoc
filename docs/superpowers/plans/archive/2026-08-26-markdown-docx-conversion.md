# [DONE] Markdown ↔ docx 转换实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 给 easydoc 增加 Markdown ↔ docx 快速转换：`EasyMarkdown.markdownToDocx(...)`（MD→HTML→现有 docx 管线）与 `EasyMarkdown.docxToMarkdown(...)`（docx→HTML→简化 MD）。

**Architecture:** 复用现有 HTML↔docx 管线，只新增 Markdown 转换层。MD→docx 用 flexmark（`com.vladsch.flexmark:flexmark-all:0.64.8`，Java 8 兼容）将 Markdown 渲染为 HTML，再走现有 `WordprocessingMLHtmlTemplate.process(html, ...)`（docx4j-ImportXHTML）；docx→MD 用现有 `WordprocessingMLPackageWriter.writeToHtml` 输出 HTML，再以简化标签映射转回 Markdown（标题/段落/表格/列表/强调）。新门面 `EasyMarkdown`（对齐 EasyDocx 风格），薄封装不替代引擎。

**Tech Stack:** Java 21（3.0.x；代码保持 Java 8 兼容供 1.0.x/2.0.x 同步）、flexmark-all 0.64.8、JUnit 5、docx4j 17.0.3、Maven 4 + POM 4.1.0 + `${revision}`。

## Global Constraints

- 新代码放 `easydoc-xhtml`（复用 HTML 管线），包 `io.github.easy4j.doc.xhtml.markdown`
- **纯新增，零破坏**：不改现有 `WordprocessingMLHtmlTemplate`/`WordprocessingMLPackageWriter` 签名；现有测试全绿
- **Java 8 语法兼容**（禁 record/sealed/var/switch 表达式/instanceof pattern）——可同步 1.0.x/2.0.x
- 每个 Task 末尾跑 `mvn -Denforcer.skip=true -pl easydoc-xhtml -am clean verify`（Maven 4：`~/tools/apache-maven-4.0.0-rc-6/bin/mvn`；1.0/2.0 同步时用各自 JDK/Maven）必须 BUILD SUCCESS
- flexmark 依赖：`com.vladsch.flexmark:flexmark-all:0.64.8`（根 pom dependencyManagement 新增属性 `flexmark.version=0.64.8`；easydoc-xhtml 声明）
- 测试用 JUnit 5，命名 `*Test.java`
- 提交信息遵循现有风格（`feat(markdown): ...`）

---

### Task 1: flexmark 依赖 + MarkdownConverter.mdToHtml

**Files:**
- Modify: `pom.xml`（根，加 `flexmark.version` 属性 + dependencyManagement 条目）
- Modify: `easydoc-xhtml/pom.xml`（声明 flexmark-all 依赖）
- Create: `easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/MarkdownConverter.java`
- Test: `easydoc-xhtml/src/test/java/io/github/easy4j/doc/xhtml/markdown/MarkdownConverterTest.java`

**Interfaces:**
- Produces: `public static String mdToHtml(String markdown)` —— Markdown → HTML 字符串，供 Task 2 的 `markdownToDocx` 使用

- [ ] **Step 1: 写失败测试**

`MarkdownConverterTest.java`：
```java
package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MarkdownConverterTest {

	@Test
	void mdToHtmlConvertsHeadingsAndParagraphs() {
		String html = MarkdownConverter.mdToHtml("# Title\n\nHello **world**.");
		assertTrue(html.contains("<h1>"), "heading must render to <h1>");
		assertTrue(html.contains("<strong>world</strong>"), "bold must render to <strong>");
		assertTrue(html.contains("<p>"), "paragraph must render to <p>");
	}

	@Test
	void mdToHtmlConvertsTable() {
		String html = MarkdownConverter.mdToHtml("| A | B |\n|---|---|\n| 1 | 2 |");
		assertTrue(html.contains("<table>"), "GFM table must render to <table>");
		assertTrue(html.contains("<td>1</td>"), "table cell must render");
	}

	@Test
	void mdToHtmlConvertsCodeBlockAndList() {
		String html = MarkdownConverter.mdToHtml("```java\nint x=1;\n```\n\n- item1\n- item2");
		assertTrue(html.contains("<pre>"), "code block must render to <pre>");
		assertTrue(html.contains("<li>item1</li>"), "list item must render");
	}
}
```

- [ ] **Step 2: 跑测试确认失败**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-xhtml -am test -Dtest=MarkdownConverterTest`
Expected: 编译失败（找不到 `MarkdownConverter`）

- [ ] **Step 3: 加依赖 + 实现**

根 `pom.xml` properties 加：
```xml
<!-- Markdown 解析（MD→HTML 渲染，Java 8 兼容） -->
<flexmark.version>0.64.8</flexmark.version>
```
根 `pom.xml` dependencyManagement 加：
```xml
<dependency>
    <groupId>com.vladsch.flexmark</groupId>
    <artifactId>flexmark-all</artifactId>
    <version>${flexmark.version}</version>
</dependency>
```
`easydoc-xhtml/pom.xml` dependencies 加：
```xml
<!-- flexmark：Markdown → HTML（GFM 表格/代码块扩展） -->
<dependency>
    <groupId>com.vladsch.flexmark</groupId>
    <artifactId>flexmark-all</artifactId>
</dependency>
```

`markdown/MarkdownConverter.java`：
```java
package io.github.easy4j.doc.xhtml.markdown;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

/**
 * Markdown 文本 → HTML 字符串转换（flexmark 驱动，启用 GFM 表格/删除线扩展）。
 * 输出 HTML 供 easydoc 现有 HTML→docx 管线消费。
 */
public final class MarkdownConverter {

	private static final Parser PARSER;
	private static final HtmlRenderer RENDERER;

	static {
		MutableDataSet options = new MutableDataSet();
		options.set(Parser.EXTENSIONS, java.util.Arrays.asList(
				com.vladsch.flexmark.ext.tables.TablesExtension.create(),
				com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension.create(),
				com.vladsch.flexmark.ext.autolink.AutolinkExtension.create()));
		PARSER = Parser.builder(options).build();
		RENDERER = HtmlRenderer.builder(options).build();
	}

	private MarkdownConverter() {
	}

	/** Markdown → HTML。null 输入返回空串。 */
	public static String mdToHtml(String markdown) {
		if (markdown == null) {
			return "";
		}
		Node document = PARSER.parse(markdown);
		return RENDERER.render(document);
	}
}
```

- [ ] **Step 4: 跑测试确认通过**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-xhtml -am test -Dtest=MarkdownConverterTest`
Expected: PASS

- [ ] **Step 5: 提交**

```bash
git add pom.xml easydoc-xhtml/pom.xml easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/ easydoc-xhtml/src/test/java/io/github/easy4j/doc/xhtml/markdown/
git commit -m "feat(markdown): add flexmark dep + MarkdownConverter.mdToHtml"
```

---

### Task 2: EasyMarkdown.markdownToDocx

**Files:**
- Create: `easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/EasyMarkdown.java`
- Test: `easydoc-xhtml/src/test/java/io/github/easy4j/doc/xhtml/markdown/EasyMarkdownTest.java`

**Interfaces:**
- Consumes: `MarkdownConverter.mdToHtml`（Task 1）、现有 `WordprocessingMLHtmlTemplate`
- Produces: `public static WordprocessingMLPackage markdownToDocx(String markdown)`、`public static WordprocessingMLPackage markdownToDocx(String markdown, Map<String,Object> vars)`

- [ ] **Step 1: 写失败测试**

`EasyMarkdownTest.java`：
```java
package io.github.easy4j.doc.xhtml.markdown;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

class EasyMarkdownTest {

	@Test
	void markdownToDocxProducesPackage() throws Exception {
		WordprocessingMLPackage pkg = EasyMarkdown.markdownToDocx(
				"# 标题\n\n这是 **加粗** 内容。\n\n- 列表一\n- 列表二");
		assertNotNull(pkg);
		String xml = pkg.getMainDocumentPart().getXML();
		assertTrue(xml.contains("标题"), "heading text must appear in the docx");
		assertTrue(xml.contains("加粗"), "bold text must appear in the docx");
	}

	@Test
	void markdownToDocxHandlesNullAndEmpty() throws Exception {
		assertNotNull(EasyMarkdown.markdownToDocx(null), "null markdown yields a package");
		assertNotNull(EasyMarkdown.markdownToDocx(""), "empty markdown yields a package");
	}
}
```

- [ ] **Step 2: 跑测试确认失败**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-xhtml -am test -Dtest=EasyMarkdownTest`
Expected: 编译失败（找不到 `EasyMarkdown`）

- [ ] **Step 3: 实现门面**

`markdown/EasyMarkdown.java`：
```java
package io.github.easy4j.doc.xhtml.markdown;

import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;

/**
 * Markdown ↔ docx 转换门面（对齐 EasyDocx 风格）。MD→docx 走
 * MarkdownConverter.mdToHtml + 现有 WordprocessingMLHtmlTemplate（HTML→docx）；
 * 薄封装，不替代引擎。
 */
public final class EasyMarkdown {

	private EasyMarkdown() {
	}

	/** Markdown → docx（无变量替换）。 */
	public static WordprocessingMLPackage markdownToDocx(String markdown) throws Exception {
		return markdownToDocx(markdown, null);
	}

	/** Markdown → docx（支持 ${var} 占位符替换——MD 内容渲染后由 HTML 管线处理）。 */
	public static WordprocessingMLPackage markdownToDocx(String markdown,
			Map<String, Object> vars) throws Exception {
		String html = MarkdownConverter.mdToHtml(markdown);
		WordprocessingMLHtmlTemplate template = new WordprocessingMLHtmlTemplate();
		return template.process(html, vars);
	}
}
```

- [ ] **Step 4: 跑测试确认通过**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-xhtml -am test -Dtest=EasyMarkdownTest`
Expected: PASS（若 docx4j-ImportXHTML 对 flexmark 输出（如 `<table>`/`<pre>`）解析有兼容问题，调整 flexmark 扩展或 HTML 预处理——报告具体失败）

- [ ] **Step 5: 提交**

```bash
git add easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/EasyMarkdown.java easydoc-xhtml/src/test/java/io/github/easy4j/doc/xhtml/markdown/EasyMarkdownTest.java
git commit -m "feat(markdown): add EasyMarkdown.markdownToDocx"
```

---

### Task 3: EasyMarkdown.docxToMarkdown

**Files:**
- Modify: `easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/EasyMarkdown.java`
- Modify: `easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/MarkdownConverter.java`（加 `docxToMarkdown` 辅助）
- Test: `easydoc-xhtml/src/test/java/io/github/easy4j/doc/xhtml/markdown/EasyMarkdownTest.java`（追加）

**Interfaces:**
- Consumes: `WordprocessingMLPackageWriter.writeToHtml`（docx→HTML）、`MarkdownConverter.docxToMarkdown(String html)`（HTML→MD）
- Produces: `public static String docxToMarkdown(WordprocessingMLPackage pkg)`

- [ ] **Step 1: 追加失败测试**

`EasyMarkdownTest.java` 追加：
```java
	@Test
	void docxToMarkdownConvertsHeadingAndBold() throws Exception {
		WordprocessingMLPackage pkg = EasyMarkdown.markdownToDocx("# 标题\n\n**加粗** 内容");
		String md = EasyMarkdown.docxToMarkdown(pkg);
		assertTrue(md.contains("标题"), "heading text must appear in markdown output");
		assertTrue(md.contains("加粗"), "text must appear in markdown output");
	}
```

- [ ] **Step 2: 跑测试确认失败**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-xhtml -am test -Dtest=EasyMarkdownTest`
Expected: FAIL（`docxToMarkdown` 不存在）

- [ ] **Step 3: 实现 docx→Markdown（docx→HTML→简化 MD）**

`MarkdownConverter.java` 追加（HTML→MD 简化映射）：
```java
	/** HTML → Markdown（简化映射：标题/段落/粗斜体/列表/表格/代码块）。 */
	public static String htmlToMarkdown(String html) {
		if (html == null) {
			return "";
		}
		String out = html
				.replaceAll("(?i)<h1[^>]*>", "\n# ")
				.replaceAll("(?i)</h1>", "\n")
				.replaceAll("(?i)<h2[^>]*>", "\n## ")
				.replaceAll("(?i)</h2>", "\n")
				.replaceAll("(?i)<h3[^>]*>", "\n### ")
				.replaceAll("(?i)</h3>", "\n")
				.replaceAll("(?i)<strong>", "**").replaceAll("(?i)</strong>", "**")
				.replaceAll("(?i)<em>", "*").replaceAll("(?i)</em>", "*")
				.replaceAll("(?i)<li>", "- ").replaceAll("(?i)</li>", "\n")
				.replaceAll("(?i)<p[^>]*>", "\n").replaceAll("(?i)</p>", "\n")
				.replaceAll("(?i)<td[^>]*>", " | ").replaceAll("(?i)</td>", "")
				.replaceAll("(?i)</tr>", "\n")
				.replaceAll("(?i)<pre[^>]*>", "\n```\n").replaceAll("(?i)</pre>", "\n```\n")
				.replaceAll("(?i)</?table[^>]*>", "\n")
				.replaceAll("(?i)</?thead[^>]*>|</?tbody[^>]*>|</?tr[^>]*>", "\n")
				.replaceAll("(?i)<[^>]+>", ""); // 残余标签
		return out.replaceAll("\\n{3,}", "\n\n").trim();
	}
```

`EasyMarkdown.java` 追加：
```java
	/** docx → Markdown（经 docx4j HTML 导出 + 简化 HTML→MD 映射）。 */
	public static String docxToMarkdown(org.docx4j.openpackaging.packages.WordprocessingMLPackage pkg)
			throws Exception {
		java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		new io.github.easy4j.doc.io.WordprocessingMLPackageWriter().writeToHtml(pkg, out);
		return MarkdownConverter.htmlToMarkdown(out.toString("UTF-8"));
	}
```

- [ ] **Step 4: 跑测试确认通过**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-xhtml -am test -Dtest=EasyMarkdownTest`
Expected: PASS（docx4j HTML 导出的标签与映射匹配；若 `writeToHtml(OutputStream)` 签名/行为差异，按 easydoc-xhtml 实际 API 适配）

- [ ] **Step 5: 提交**

```bash
git add easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/markdown/ easydoc-xhtml/src/test/java/io/github/easy4j/doc/xhtml/markdown/
git commit -m "feat(markdown): add EasyMarkdown.docxToMarkdown"
```

---

### Task 4: 全量验证 + README/CHANGELOG + 推送

**Files:**
- Modify: `README.md`（Markdown 转换快速上手段落）
- Modify: `CHANGELOG.md`

- [ ] **Step 1: README 增加 Markdown 转换**

在 README.md 的 EasyDocx 段后追加：
```markdown
### Markdown ↔ docx

```java
// Markdown → docx（快速转换，复用 HTML 管线）
org.docx4j.openpackaging.packages.WordprocessingMLPackage doc =
        EasyMarkdown.markdownToDocx("# 标题\n\n**加粗** 内容");

// docx → Markdown
String md = EasyMarkdown.docxToMarkdown(doc);
```
```

- [ ] **Step 2: 全量验证**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn clean verify`（Maven 4 无 skip）
Expected: BUILD SUCCESS（13 模块 + JaCoCo 90%）

- [ ] **Step 3: CHANGELOG 记录**

`CHANGELOG.md` 3.0.x 段追加：
```markdown
- **Markdown ↔ docx**：`EasyMarkdown.markdownToDocx`（MD→HTML→docx，flexmark 驱动）、
  `EasyMarkdown.docxToMarkdown`（docx→HTML→MD 简化映射）
```

- [ ] **Step 4: 提交 + 推送**

```bash
git add README.md CHANGELOG.md
git commit -m "docs(markdown): document Markdown conversion + changelog"
git push origin feature/3.0.x
git checkout main && git merge --ff-only feature/3.0.x && git push origin main
```

---

## 执行后同步（1.0.x / 2.0.x）

全部代码 Java 8 兼容 + flexmark 0.64.8 是 Java 8 库；依赖 API（`WordprocessingMLHtmlTemplate.process(html, vars)`、`WordprocessingMLPackageWriter.writeToHtml(pkg, OutputStream)`）在 1.0/2.0 存在（同步时核对签名：1.0/2.0 的 `writeToHtml` 是否接受 `OutputStream`——若无，用 `File` 重载 + 读回字符串适配）。

同步步骤（对应 worktree）：
1. 根 pom 加 `flexmark.version=0.64.8` + dependencyManagement；`easydoc-xhtml/pom.xml` 加 flexmark-all
2. 复制 `xhtml/.../markdown/` 源码与测试到 1.0.x/2.0.x
3. 适配 `docxToMarkdown` 的 `writeToHtml` 调用（核对 1.0/2.0 签名）
4. 各分支 `mvn -Denforcer.skip=true -pl easydoc-xhtml -am clean verify` 绿
5. commit + push feature/1.0.x / feature/2.0.x

## Status

**完成。** 全部 4 个 Task 已落地并通过三分支验证。

| Task | 实现 commit(s) | 验证 |
|---|---|---|
| Task 1: flexmark 依赖 + MarkdownConverter.mdToHtml | `3.0.x` 早期提交（已在 `7fd3d2e` 前存在） | `MarkdownConverterTest` |
| Task 2: EasyMarkdown.markdownToDocx | 同上 | xhtml 测试覆盖 |
| Task 3: EasyMarkdown.docxToMarkdown（含快路径 4 重载） | 同上；Sprint 1 期间 `18702c0` 增加便捷重载、`64dc689` 增加 6 结构化门面 | `EasyMarkdownTest` / `EasyMarkdownStructuredTest` |
| Task 4: 全量验证 + README/CHANGELOG + 推送 | `4d38486`（CHANGELOG + quickstart） | xhtml 230 测试全绿 |

附注：原会话期间已三次发版（20260630 / 20260830 / 20260930），本计划所有交付已随版本发布。归档前缀 `[DONE]` 由会话后续补齐。
