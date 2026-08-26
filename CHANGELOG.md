# Changelog

本文件记录 easydoc 各版本线的显著变化。格式参考 [Keep a Changelog](https://keepachangelog.com/)，
版本约定遵循 [Semantic Versioning](https://semver.org/spec/v2.0.0.html)。

## [3.0.x] — Unreleased（feature/3.0.x，面向 JDK 21）

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

### 安全

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
- easydoc-jsp 仍是 javax.servlet + Tomcat 9 Jasper（Jakarta EE 用户不可用，其余模块不受影响）。
- HTML→PDF 的 openhtmltopdf（docx4j-xhtmlrenderer 3.0.0）会打印 XXE 相关 SEVERE 警告；
  处理不可信 HTML 时建议 JVM 参数 `javax.xml.accessExternalDTD=""`。

## [1.0.x] — 1.0.1.RELEASE（JDK 8 基线）

首个发布线：docx4j 8.x、8 模板引擎 + JSP、基础变量替换管线。
