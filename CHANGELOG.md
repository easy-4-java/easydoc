# Changelog

本文件记录 easydoc 各版本线的显著变化。格式参考 [Keep a Changelog](https://keepachangelog.com/)，
版本约定遵循 [Semantic Versioning](https://semver.org/spec/v2.0.0.html)。

## 支持状态声明（2026-08-28）

**1.0.x 进入受限维护（EOL 路径）。**

- **不要用 1.0.x 处理不可信文档**（用户上传、外部来源）。处理不可信输入请升级到
  [2.0.x](https://github.com/easy-4-java/easydoc/tree/feature/2.0.x)（docx4j 11.5.14，
  CVE-2026-53752 已修复）或 [3.0.x](https://github.com/easy-4-java/easydoc/tree/main)
  （docx4j 17.0.3）。
- 1.0.x 仍适用于 JDK 8 环境下基于**可信内部模板**的文档生成。
- 残留安全债（详见下表，均无上游修复路径）：docx4j 8.3.15 的 CVE-2026-53752、
  PDF 路径传递依赖 itext 2.1.7（XXE，CVE-2017-9096）、logback 1.3.x（EOL）。
- 后续仅提供我们自行引入缺陷的修复；新功能与安全加固优先 2.0.x / 3.0.x，
  仅当可干净适配 javax 基线时回移。

## [1.0.x.20260831] — 2026-08-27

1.0.x 是面向 JDK 8 的兼容基线：docx4j 8.3.15（javax 时代终线）、8 个模板引擎 + JSP、
Maven 3.9.16 构建基线、每模块 BUNDLE 覆盖率基线（v20260830 实测起点）。相对 3.0.x（JDK 21）。

### 同步自 3.0.x 的 Sprint 1 修复

- **结构化 Markdown**：列表缩进按 CommonMark 标记几何计算（多级 / 10+ 有序列表不再散架）；
  `MarkdownEscaper` 转义层（`*_[]#\` 等字面字符不再污染下游）；
  合并单元格 gridSpan 展开 / vMerge 续行占位 / 行列宽规范化；
  header/footer/footnote 内容纳入提取。
- **核心健壮性**：`Assert.notEmpty(Object[])` 条件写反修复；
  `DocxElementWmlRender.newCell(Tr, String)` 自引用修复；
  SAX 变量替换器对未闭合占位符不再崩溃（与 StAX 宽松语义一致）；
  `WmlZipUtils` ZipFile / 入口流 try-with-resources；writer `writeToFile` 流关闭；
  writer 静态状态线程安全（volatile 字段 + synchronized setter + 删除死的 `Docx4jProperties` 突变）。
- **引擎工厂健壮性**：HTTL 默认配置不再 NPE；Velocity 7 个分号前缀死键 + 修正 loader FQN + 缺目录 guard；
  Rythm `home.tmp` 拼写 + null/invalid config 硬化；Freemarker 双阶段 init 收敛（与其它 7 模块一致）；
  Thymeleaf unknown resolver 降级 WARN 日志；Jetbrick 异常 catch 收窄到 `IllegalStateException`。

### 已知边界

- 本周期（20260830）是 1.0.x 兼容线首个纳入 Sprint 1 修复的发布。
- 后续节奏：每两个月一次日期戳发布（`1.0.x.YYYYMMDD`）。
- 1.0.x 仍使用 javax.servlet（JSP 模块）；`com.alibaba:fastjson` 已迁移至 2.0.x v1 兼容构建（内核 fastjson2）。
- docx4j 8.3.15 为 javax 时代终线，CVE-2026-53752 DoS 暂无修复；建议对不可信 .docx 输入做限流或升级评估。
