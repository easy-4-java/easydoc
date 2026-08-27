# Sprint 2: 三分支同步 + 工程债清理（9 项）

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development. Steps use checkbox (`- [ ]`) syntax for tracking.

## Goal

把 3.0.x 已验证的 3 个新功能（颜色渲染 / XXE 硬化 / flexmark-html2md 快路径）同步到 1.0.x 与 2.0.x，并清理 6 项工程债。最终三分支逻辑一致、测试覆盖诚实、发布就绪。

## 已确认事实（controller 验证）

| 项 | 1.0.x | 2.0.x | 3.0.x |
|---|---|---|---|
| 死 JUnit4 类（无 vintage engine，静默不跑） | 15 个 | 15 个 | 0（已删） |
| XHTMLImporterUtils XXE 防护 | 无 | 无 | 有（SecureDocumentBuilderFactory） |
| flexmark-html2md 快路径 | 无（正则） | 无（正则） | 有 |
| MarkdownRenderOptions 颜色渲染 | 无 | 无 | 有 |
| flexmark 版本 | 0.62.2 | 0.64.8 | 0.64.8 |

## Scope / 分组

- **Agent A → 1.0.x**：同步 3 功能（javax/ JDK8 适配）+ JUnit4 死类清理 + argLine 修正
- **Agent B → 2.0.x**：同步 3 功能 + JUnit4 死类清理
- **Agent C → 3.0.x 工程债**：antlr4 评估、CVE 文档、central deploy 脚本固化、jacoco ratchet 脚本、RELEASING.md、html2md 选项验证

## Task 组

### 组 1：1.0.x 同步（Agent A）

**1.1 移植颜色渲染**（参考 3.0.x 提交：`cdd1bc3` + `a51a951` + `e85cc71`）
- [ ] DocxCell.java + MarkdownRenderOptions.java（JDK8 兼容：**不能用 record**，改普通 class + equals/hashCode）
- [ ] DocxStructureExtractor 采集 cell 颜色（javax 适配：`org.docx4j.wml.Color` 等 import 不变）
- [ ] DocxTable 升级 + 渲染管道（DocxElement/DocxDocument/DocxToMarkdownConverter 传递 opts）
- [ ] EasyMarkdown 3 个 opts 重载
- [ ] 测试移植（DocxCellAndRenderOptionsTest 等，JDK8 断言风格）
- `verify:` `JAVA_HOME=/opt/homebrew/opt/openjdk@17 mvn -Dmaven.compiler.release=8 -Denforcer.skip=true -pl easydoc-xhtml -am clean verify`
- `commit:` `feat(xhtml): port MarkdownRenderOptions color rendering to 1.0.x (from 3.0.x)`

**1.2 移植 XXE 硬化**（参考 3.0.x：`8c19b95` + `cb75089` + `a76af55`）
- [ ] SecureDocumentBuilderFactory.java（JDK8 兼容）
- [ ] XHTMLImporterUtils 集成（系统属性窗口 + 恢复）
- [ ] 测试移植（DOCTYPE 拒绝 + 属性恢复）
- `verify:` 同上 + DOCTYPE 拒绝用例必须抛异常
- `commit:` `fix(xhtml): port SecureDocumentBuilderFactory XXE hardening to 1.0.x`

**1.3 移植 flexmark-html2md 快路径**（参考 3.0.x：`d7ce245`）
- [ ] easydoc-xhtml/pom.xml 加 `flexmark-html2md-converter`（1.0.x 的 flexmark 是 **0.62.2**——需确认 0.62.2 是否有该构件；若无则评估是否升级 flexmark 或跳过并在报告说明）
- [ ] MarkdownConverter.htmlToMarkdown 内部替换
- [ ] 测试移植（6 场景）
- `verify:` 同上
- `commit:` `feat(xhtml): port flexmark-html2md fast-path to 1.0.x`

**1.4 JUnit4 死类清理**
- [ ] 列出 15 个 `import org.junit.Test` 类，逐个确认 Jupiter 等价覆盖
- [ ] 有等价 → 删除；无等价 → 迁移为 JUnit 5
- [ ] 移除 pom 里 junit:junit 依赖（若仅死类使用）
- `verify:` 全 reactor 绿 + `grep -rl 'import org.junit.Test'` 为 0
- `commit:` `test(1.0): remove silently-dead JUnit4 classes (no vintage engine)`

**1.5 argLine 修正**
- [ ] 1.0.x 根 pom 的 `-da:org.docx4j...` 过宽 → 收窄为 `-da:org.docx4j.fonts.fop...`（对齐 2.0.x）
- [ ] 确认现有 font 相关测试仍绿
- `verify:` 全 reactor 绿
- `commit:` `build(1.0): narrow -da scope to org.docx4j.fonts.fop (align with 2.0.x)`

### 组 2：2.0.x 同步（Agent B）

**2.1 移植颜色渲染**（同 1.1，但 JDK17 可用 record）
**2.2 移植 XXE 硬化**（同 1.2，jakarta 已一致）
**2.3 移植 flexmark-html2md**（同 1.3，flexmark 0.64.8 直接可用）
**2.4 JUnit4 死类清理**（同 1.4）

- 每组一个 commit，参考 3.0.x 提交
- `verify:` `JAVA_HOME=/opt/homebrew/opt/openjdk@17 mvn -Denforcer.skip=true -pl easydoc-xhtml -am clean verify`（每组）+ 全 reactor 收尾

### 组 3：3.0.x 工程债（Agent C）

**3.1 antlr4-runtime pin 评估**
- [ ] 查 1.0/2.0/3.0 根 pom 的 antlr4-runtime 版本（4.7.1？）与 beetl 3.21.2 的 ANTLR 4.9 支持包
- [ ] 运行时兼容验证（beetl 测试全绿即视为兼容）
- [ ] 输出评估：升级 antlr4-runtime 到 4.9.3 是否安全（不破坏 jetbrick 2.1.10 的 antlr 4.7.1 排除约束）
- [ ] 若安全 → 改根 pom 版本属性 + verify；若风险 → 文档化
- `commit:` `build: antlr4-runtime pin assessment (beetl 3.21.2 antlr4.9 compat)`

**3.2 1.0.x CVE 残留文档化**
- [ ] CHANGELOG 1.0.x 段追加：docx4j 8.3.15 CVE-2026-53752（无修复，限不可信输入）、itext 2.1.7 XXE（死构件）、logback 1.3.15
- `commit:` `docs(changelog): document 1.0.x residual CVEs (docx4j 8.3.15 / itext / logback)`

**3.3 central deploy 脚本固化**
- [ ] `scripts/central-bundle-fix.sh`（按 docs/release-central.md 的手工流程：unzip → 删 consumer.pom → sed revision → 重算校验和 → gpg 重签 → rezip）
- [ ] 参数化版本号，输出 fixed bundle 路径
- [ ] docs/release-central.md 引用该脚本
- `verify:` 脚本 `bash -n` 语法检查 + 在 /tmp 用已有 bundle 试跑（不实际上传）
- `commit:` `build(release): script central-bundle-fix.sh from release-central.md procedure`

**3.4 jacoco ratchet 脚本**
- [ ] `scripts/coverage-ratchet-check.sh`：读各模块 jacoco CSV，输出"当前覆盖率 vs 门禁 vs 目标 0.90"差距表
- [ ] 文档化 ratchet 流程（每周期收紧 ~10pt）
- `commit:` `build: add coverage-ratchet-check.sh (per-release ratchet helper)`

**3.5 RELEASING.md 更新**
- [ ] 与日期戳发布模型对齐（X.x.YYYYMMDD），注明 GA 模型（3.0.0.RELEASE）作为历史参考
- `commit:` `docs(releasing): align with date-stamp release model`

**3.6 html2md 选项验证（快路径颜色）**
- [ ] 验证 `OUTPUT_UNKNOWN_TAGS=true` 时 docx4j 的 `<span style="color:...">` 是否保留
- [ ] 若保留 → 加测试 + 文档；若丢失 → 记录为已知边界
- [ ] 在 CHANGELOG 注明快路径与结构化路径的颜色能力差异
- `commit:` `docs(xhtml): html2md OUTPUT_UNKNOWN_TAGS behavior with docx4j span colors`

## Definition of Done（全组）

1. 三分支都含：颜色渲染 / XXE 硬化 / html2md 快路径（1.0.x 若因 flexmark 0.62.2 缺构件而跳过，需在 commit message 记录理由）
2. 三分支 `grep -rl 'import org.junit.Test'` 为 0（死 JUnit4 类清零）
3. 三分支全 reactor verify 绿 + jacoco 门禁过
4. 工程债 6 项全部产出（评估/脚本/文档）
5. 所有新 plan 归档 `[DONE]`
6. 推送三分支 + main FF
