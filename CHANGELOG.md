# Changelog

本文件记录 easydoc 各版本线的显著变化。格式参考 [Keep a Changelog](https://keepachangelog.com/)，
版本约定遵循 [Semantic Versioning](https://semver.org/spec/v2.0.0.html)。

## [3.0.x] — Unreleased（feature/3.0.x，面向 JDK 21）

### fast-path docx→Markdown 保真度升级（F1 系列修复）

- **htmlToMarkdown 内部实现替换**：`MarkdownConverter.htmlToMarkdown` 从 12 个 `.replaceAll` 正则映射
  升级为 `FlexmarkHtmlConverter`（flexmark-html2md-converter 0.64.8，AST 驱动），解决 F1 系列审计项根源。
- **修复场景**：标题带属性正确解析、嵌套列表保留层级缩进、有序列表保留编号（1./2./3.）、
  GFM 表格含分隔行（`|---|---|`）、内联代码保留反引号、锚点保留 `[text](href)`。
- **方法签名不变**：`htmlToMarkdown(String)` 返回类型、null→空串语义完全保持，API 兼容。
- **新增依赖**：`com.vladsch.flexmark:flexmark-html2md-converter:0.64.8`（easydoc-xhtml 模块）。
- **测试覆盖**：新增 8 个 `MarkdownConverterTest` 用例（属性标题/嵌套列表/有序列表/表格分隔行/内联代码/锚点/null/空串）。

> **已知版本事故：** Maven Central 上同时存在 `3.0.x.20260730`（版本号低于前序发布 `3.0.x.20260830`，属误操作发布）与本版本 `3.0.x.20260831`。两者携带相同内容（Sprint 1 修复 + fastjson2 迁移）；请使用 `3.0.x.20260831` 作为最新版本，`3.0.x.20260730` 应视为已废弃。

## [3.0.x.20260831] — 2026-08-27

3.0.x 是面向 JDK 21 的现代化主线：docx4j 升级到 17.0.3 全家桶、JDK 21 语言与并发特性落地、
Maven 4 构建基线、安全加固与一系列正确性修复。相对 1.0.x（JDK 8）/ 2.0.x（JDK 17）。

### 依赖矩阵（JDK 21 时代版本）

| 构件 | 版本 |
|---|---|
| docx4j-core / docx4j-export-fo / docx4j-JAXB-ReferenceImpl / docx4j-JAXB-MOXy / docx4j-export-fo-fonts-{symbol,croscore,crosextra} | 17.0.3 |
| docx4j-ImportXHTML-core | 17.0.2 |
| docx4j-openxml-objects / -pml（legacy，仅 dependencyManagement 锁定） | 11.5.14 |
| jakarta.xml.bind-api | 4.x（随 docx4j 17） |

- JAXB 桥接从 MOXy 切换到 **docx4j-JAXB-ReferenceImpl**（MOXy 在 jakarta 4.x 下 SPI/编译目标双重失配）。
- 旧 `docx4j-openxml-objects(-pml)` 与 17.0.3 的 `docx4j-generated-objects` 同 FQN，
  禁止直接依赖（enforcer `bannedDependencies` 护栏，误用时构建即失败并给出迁移说明）。
- `javax.servlet-api` / `tomcat-jasper` 移出根 dependencyManagement，由使用模块自持版本。

### 架构演进（EngineFactory + Renderer 分离 / VariableReplacer SPI / JSP Jakarta）

- **easydoc-jsp 迁移 Jakarta EE**：javax.servlet → jakarta.servlet（jakarta.servlet-api 6.0.0），
  Tomcat 9 Jasper → 10.1.54（Jakarta EE 10 命名空间）。Spring Boot 3 / Jakarta EE 用户可直接使用。
- **VariableReplacer 开放为公共 SPI**：`io.github.easy4j.doc.VariableReplacer`（非密封接口）替代原
  AbstractWmlTemplate 内嵌 sealed 类型；`AbstractWmlTemplate.setReplacer(VariableReplacer)` 允许注入
  自定义替换策略（MVEL / SpEL 等）；内置 Default / Sax / StAX 三策略保留为接口嵌套实现。
- **引擎生命周期与渲染分离（8 引擎）**：每模块新增不可变 `EngineFactory`（volatile + DCL 懒加载引擎）
  与无状态 `Renderer`（渲染逻辑与状态解耦）；模板类保留全部 public API（3 构造器 /
  getEngine / setEngine / getInternalEngine / render）作为委托门面。freemarker / thymeleaf 的配置
  setter 保留为 @Deprecated（设值后使 factory 失效重建）。虚拟线程下无 synchronized 方法 / 无
  carrier-thread pinning。

### EasyDocx API（类 EasyExcel / easyodf 体验）

- **注解模型**：`@DocxField`（value=占位符, format 日期格式化, ignore）、`@DocxIgnore`
- **门面 + 链式**：`EasyDocx.write(template, Model).document("名").process(pojo|map)`、
  `EasyDocx.read(template, Model, listener).doRead()`（`DocxWriterBuilder`/`DocxReaderBuilder`）
- **监听器**：`DocxReadListener<T>`（invoke / doAfterAllAnalysed）
- **POJO→Map**：`DocxFields.from(bean)`（反射提取注解字段）
- docx 语义：中间层用 `document`（Document 概念）而非 EasyExcel 的 sheet；单文档可省略
- 薄封装：内部委托 DocxTemplates + WordprocessingMLTemplate 管线，Java 8 兼容（可同步 1.0.x/2.0.x）

### Markdown ↔ docx

- **Markdown ↔ docx**：`EasyMarkdown.markdownToDocx`（MD→HTML→docx，flexmark 驱动）、
  `EasyMarkdown.docxToMarkdown`（docx→HTML→MD 简化映射；提供 File/InputStream/byte[]/路径
  四种快捷重载，智能体/文档读取场景一次调用直接产出 Markdown）
- **结构化 docx → Markdown（新增，OOXML 直读，保真 ≈95%，对比 HTML 路径 ~70%）**：
  `EasyMarkdown.docxToStructuredMarkdown`（File/InputStream/WordprocessingMLPackage 三种重载）
  —— 绕过 HTML 导出直接解析 OOXML 语义：标题层级映射、输出按 CommonMark ATX 上限钳为 6 级；
  有序/无序列表由 numbering 定义驱动，定义不可解析时优雅降级为无序；GFM 表格（首行作表头约定）；
  超链接解析真实 URL；图片内联 base64 data URI；全部按文档顺序输出
- **结构化 POJO 树（新增）**：`EasyMarkdown.docxToStructured(...)` 返回 `DocxDocument`
  （title/author/modified 元信息 + 按文档顺序的 `DocxElement` 列表：`DocxHeading` /
  `DocxParagraph` / `DocxList` / `DocxTable` / `DocxImage`，逐元素 `toMarkdown()`），供智能体深度遍历
- 新增 `DocxStructureExtractor`（docx4j OOXML 遍历核心）与 `DocxToMarkdownConverter`（聚合渲染）；
  Java 8 语法编写（可同步 1.0.x/2.0.x），零新增依赖；元素级容错——单个损坏元素 `LOG.warn` 跳过，
  不中断整篇转换
- **null 语义差异（有意为之）**：旧 `docxToMarkdown` 系列 null 返回空串（宽松）；
  新结构化系列 null 抛 NPE（`requireNonNull` 边界严格校验，尽早暴露调用方缺陷）

### 颜色渲染（结构化 Markdown 路径）

新增 `MarkdownRenderOptions`（默认 OFF 保持"纯 GFM"承诺），开启 `renderHtmlColor(true)` 后
单元格级字体颜色（`<w:color w:val="FF0000"/>`）和背景色（`<w:shd w:fill="FFFF00"/>`）通过
`<span style="...">` 输出。主题色（`theme=`）解析复杂，首版不支持（返回 null 不渲染）。
PDF 路径颜色已由 docx4j XSL-FO 内置保留；快路径 Markdown 不在本 plan 范围。

### JDK 21 特性

- **sealed + record**：三模板（DEFAULT/SAX/STAX）收敛到 `AbstractWmlTemplate` 骨架 +
  sealed `VariableReplacer`（Default/StAX 为 record，Sax 为带 volatile 单飞降级标志的 final 类）。
- **switch 表达式**：Thymeleaf resolver 选择器等 if/else 阶梯改造（含 `when` guard）。
- **虚拟线程就绪**：256 并发渲染/解压合约测试（`Jdk21VirtualThreadTest`）；
  批量协调测试（allOf / CompletionException 传播 / interrupt 传播 / timeout 边界）；
  SAX→StAX JDK 21 透明降级为 volatile + 单飞初始化。
- **并发卫生**：mutable static 清零（WMLPackageUtils、FontMapperHolder volatile）。

### 修复（正确性）

- **字体映射 NPE**（根因修复）：系统未安装 CJK 字体时 `PhysicalFonts.get` 返回 null，
  `Mapper.put` 内部 ConcurrentHashMap 拒绝 null → NPE。现在 `putIfAvailable` 仅在字体存在时
  建立映射，缺失字体交给 IdentityPlusMapper 的 Panose 回退；顺带修复 `setWmlPackageFonts`
  吞异常的黑洞（`Docx4JException(e.getMessage(), e.getCause())` 会丢失 NPE 的消息与 cause）。
- **mergeDocx 临时文件泄漏**：返回值改为 close 时自删除的流，不再依赖 `deleteOnExit`
  （长生命周期服务/虚拟线程场景下曾无限累积）。
- **热路径 IO** 全面 try-with-resources / 显式 close，移除 `IOUtils.closeQuietly` 对
  close 异常的吞没。
- **变量严格模式**（`-Deasydoc.variable.strict=true`）：占位符无法解析或 OGNL 求值失败时
  抛出含占位符名的 `IllegalStateException`；宽松模式（默认）行为不变，但 WARN 日志带上
  占位符文本，模板拼写错误不再静默。
- SAX 模板在 JDK 21 下首次使用记录一次 WARN 并透明降级到 StAX（docx4j 17.0.3 的
  SAXHandler 与 JDK 21 Transformer 不兼容）。`DocxTemplates` 工厂在 JDK 21+ 上对
  `DocxMode.SAX` 静态短路直接返回 StAX 模板（info 日志一次），消除"名不副实"问题。
- httl / rythm / webit 引擎标记 `@Deprecated(since = "3.0")`（上游停更，httl 2014 年、
  rythm 2015 年、webit 2016 年最后发布；建议新项目选用 freemarker / thymeleaf / velocity）。

### 审计修复（Sprint 1，30 项，多智能体并行交付）

**正确性（P0）**

- `Assert.notEmpty(Object[])` 条件写反（非空抛、空放行）修复为契约语义；同步修正
  `CoverageBoostTest` 中三条锁定旧缺陷的断言。
- `DocxElementWmlRender.newCell(Tr, String)` 自引用修复（原把单元格加进自己，
  `newTable(row, cell)` 产出的表格行内容为空且存在编组递归风险）。
- 结构化 Markdown：**列表缩进按 CommonMark 标记几何计算**（多级/10+ 有序列表不再散架）；
  新增 `MarkdownEscaper` 转义层（`*_[]#\` 等字面字符不再污染下游）；
  **合并单元格** gridSpan 展开 / vMerge 续行占位 / 行列宽规范化；
  **header/footer/footnote 内容纳入提取**（body → headers → footers → footnotes）。
- 删除 14 个在 JUnit Platform 下**静默从未执行**的 JUnit 4 测试类（无 vintage-engine），
  由 Jupiter 等价套件承接覆盖；移除根 pom 的 junit4 依赖。

**健壮性（P1）**

- SAX 变量替换器对未闭合占位符（`${foo`）不再崩溃（对齐 StAX 宽松语义：WARN + 字面保留），
  占位符前后缀长度不再硬编码 2。
- writer 家族：`writeToHtml(pkg)` 不再写入 `.pdf` 后缀文件；临时文件改
  `Files.createTempFile`（同毫秒不再互踩）；`WmlZipUtils` 的 ZipFile/entry 流全部
  try-with-resources；`writeToFile` 流关闭；删除经字节码验证为**死代码**的全局
  `Docx4jProperties` 突变（key 拼写错误从未生效），handler 字段 volatile + 同步 setter。
- 引擎工厂：HTTL 默认配置不再 NPE；Velocity 清理 7 个分号前缀死键 + 修正 loader FQN；
  Rythm 修复 `home.tmp` 拼写与空配置 NPE；Freemarker 双阶段初始化收敛为与其它 7 模块一致的
  `factory.get()` 单入口（修复每次 `getInternalEngine()` 重建 Renderer、首个 render 丢失
  静态助手、过期 setter 不重置缓存三处缺陷）；8 模块统一 IOException 异常边界。
- 静默异常吞噬点（DocxReaderBuilder / WMLPackageUtils / insertDocx / SampleDocument /
  双 handler）全部改为带完整 cause 链的 WARN 日志。

**测试与门禁**

- JaCoCo 门禁诚实化：BUNDLE 0.90→**0.92**，新增 PACKAGE 级 0.85 规则
  （handler 84%→91.6%、markdown 89%→91.0% 已先补测试达标）；
- 新增 `FontDiscoveryTestBase`（字体发现失败从"静默弱通过"改为显式 Assumption 跳过）；
  14 个 perf 测试统一 `@Tag("perf-absolute")`，8 个 e2e 改为同 JVM 比值界限（负载免疫）；
  surefire argLine 统一晚绑定 `@{argLine}`；清理 src/test/resources/output 残留产物。
- 结构化门面 6 个方法 `throws Exception` 收窄为 `IOException`（源码兼容）。

**安全（依赖）**

- **fastjson 迁移至 2.x v1 兼容构建**：rythm-engine 硬编码 fastjson v1 API，原传递依赖
  1.2.83 处于已终止维护且存在无修复 RCE（CVE-2026-16723）的 1.2.x 线；现全分支锁定
  `com.alibaba:fastjson:2.0.57`——同一 GAV、v1 包名/API 面不变（JSONWrapper /
  parseArray/parseObject / JSONArray），内部实现切换为 fastjson2 内核，
  `RythmFastjsonCompatTest` 锁定兼容面。该 CRITICAL 已从全部依赖树清除。
- 完整 CVE 扫描结论见 [docs/release-central.md](docs/release-central.md)。

### 安全

- **XXE 防护硬化（XHTMLImporterUtils）**：之前依赖 System.setProperty 序列化窗口
  （accessExternalDTD/SCHEMA）作为间接缓解，未实际禁用 DOCTYPE 解析本身。新增
  `SecureDocumentBuilderFactory`（继承 DocumentBuilderFactory，构造器内统一应用
  disallow-doctype-decl / FEATURE_SECURE_PROCESSING / 关闭外部实体与 XInclude），通过
  javax.xml.parsers.DocumentBuilderFactory 系统属性指向该类，作为 docx4j 内部工厂
  实例化的拦截器。恶意 XHTML 输入中的 DOCTYPE 与外部实体引用将被解析器直接拒绝。
  新增 3 个回归测试覆盖 DOCTYPE 拒绝与系统属性恢复。
- OGNL 注入：`DefaultMemberAccess(false, false, false)` 仅允许 public 成员。
- Zip Slip：解压目标 canonical path 校验。
- XML 注入：输出转义。

### 构建与流程

- **Maven 4 基线**：`requireMavenVersion [4.0.0-rc-6,)`（注意 Maven 版本序中 rc 低于 GA）。
  常规 `mvn clean verify`（无 skip）下 enforcer 护栏与 JaCoCo 90% 覆盖率门禁真正生效。
  Maven 3.9.x 用户可用 `-Denforcer.skip=true` 绕过（护栏不执行）。
- 覆盖率：全模块 90% LINE（haltOnFailure），easydoc-core 930+ 测试。
- 性能测试：14 个 JUnit 时序基准类（无 JMH 依赖）。

### 已知边界

- `DocxMode.SAX` 在 JDK 21 实际执行 StAX 路径——`DocxTemplates` 工厂在 JDK 21+ 上静态短路
  直接返回 StAX 模板（行为一致，运行时降级保留防御直接 new 实例化）。
- httl / rythm / webit 引擎标记 `@Deprecated`（上游停更 2014–2016），可用但建议新项目选用
  freemarker / thymeleaf / velocity。
- easydoc-jsp 已迁移 jakarta.servlet 6.0.0 + Tomcat 10.1 Jasper（见架构演进节）。
- HTML→PDF 的 openhtmltopdf（docx4j-xhtmlrenderer 3.0.0）会打印 XXE 相关 SEVERE 警告；
  处理不可信 HTML 时建议 JVM 参数 `javax.xml.accessExternalDTD=""`。

## [1.0.x] — 1.0.1.RELEASE（JDK 8 基线）

首个发布线：docx4j 8.x、8 模板引擎 + JSP、基础变量替换管线。
