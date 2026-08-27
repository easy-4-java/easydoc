# Changelog

本文件记录 easydoc 各版本线的显著变化。格式参考 [Keep a Changelog](https://keepachangelog.com/)，
版本约定遵循 [Semantic Versioning](https://semver.org/spec/v2.0.0.html)。

## [2.0.x.20260831] — 2026-08-27

2.0.x 是面向 JDK 17 的过渡主线：docx4j 11.5.14（jakarta 时代）、8 个模板引擎 + JSP、
Maven 3.9.16 构建基线、每模块 BUNDLE 覆盖率基线（v20260830 实测起点）。相对 3.0.x（JDK 21）相对 1.0.x（JDK 8）。

### 同步自 3.0.x 的 Sprint 1 修复

- **结构化 Markdown**：列表缩进按 CommonMark 标记几何计算；
  `MarkdownEscaper` 转义层；
  合并单元格 gridSpan / vMerge 规范化；
  header/footer/footnote 内容纳入提取。
- **核心健壮性**：`Assert.notEmpty(Object[])` 条件写反修复；
  `DocxElementWmlRender.newCell(Tr, String)` 自引用修复；
  SAX 变量替换器对未闭合占位符不再崩溃；
  `WmlZipUtils` ZipFile / 入口流 try-with-resources；writer `writeToFile` 流关闭；
  writer 静态状态线程安全（volatile 字段 + synchronized setter + 删除死的 `Docx4jProperties` 突变）。
- **引擎工厂健壮性**：HTTL 默认配置不再 NPE；Velocity 7 个死键 + 修正 loader FQN + 缺目录 guard；
  Rythm `home.tmp` 拼写 + null/invalid config 硬化；Freemarker 双阶段 init 收敛；
  Thymeleaf unknown resolver 降级 WARN；Jetbrick catch 收窄到 `IllegalStateException`。

### 已知边界

- 本周期（20260830）是 2.0.x 过渡线首个纳入 Sprint 1 修复的发布。
- 后续节奏：每两个月一次日期戳发布（`2.0.x.YYYYMMDD`）。
- 2.0.x 已使用 jakarta.servlet（JSP 模块与 3.0.x 一致）；`com.alibaba:fastjson` 已迁移至 2.0.x v1 兼容构建（内核 fastjson2）。
- docx4j 11.5.14 含已修复的 CVE-2026-53752。
