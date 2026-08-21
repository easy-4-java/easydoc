# easydoc 配置参考

easydoc 的引擎适配器通过 docx4j 的全局属性（`org.docx4j.Docx4jProperties`，读取 `docx4j.properties`）读取配置。所有引擎均提供默认值，**无配置文件也可运行**；以下键可按需覆盖。

> 说明：`Docx4jProperties` 是 JVM 级全局静态属性。设置方式：
> - `docx4j.properties` 文件（classpath 根）
> - `System.setProperty("docx4j.xxx", "...")` 启动参数
> - 代码内 `Docx4jProperties.setProperty("docx4j.xxx", "...")`

## 通用（easydoc-core）

| 键 | 默认值 | 说明 |
|---|---|---|
| `docx4j.docx.input.encoding` | `UTF-8` | 模板输入编码 |
| `docx4j.docx.output.encoding` | `UTF-8` | 输出编码 |
| `docx4j.docx.placeholderStart` | `${` | 变量占位符起始 |
| `docx4j.docx.placeholderEnd` | `}` | 变量占位符结束 |
| `docx4j.docx.source.delete` | `false` | 处理完成后是否删除模板 |
| `docx4j.docx.tmpdir` | `unzip_tmpdir` | docx 解压临时目录 |

## easydoc-beetl

| 键 | 默认值 | 说明 |
|---|---|---|
| `docx4j.beetl.charset` | `UTF-8` | 模板字符集 |
| `docx4j.beetl.placeholderStart` | `${` | 占位起始符号 |
| `docx4j.beetl.placeholderEnd` | `}` | 占位结束符号 |
| `docx4j.beetl.statementStart` | `<%` | 控制语句起始符号 |
| `docx4j.beetl.statementEnd` | `%>` | 控制语句结束符号 |
| `docx4j.beetl.htmlTagSupport` | `false` | 是否允许 html tag |
| `docx4j.beetl.htmlTagFlag` | `#` | html tag 标示符号 |
| `docx4j.beetl.htmlTagBindingAttribute` | `var` | html 绑定属性 |
| `docx4j.beetl.nativeCall` | `false` | 是否允许直接调用 class |
| `docx4j.beetl.directByteOutput` | `true` | 字节输出模式 |
| `docx4j.beetl.strict` | `false` | 严格 mvc 模式 |
| `docx4j.beetl.ignoreClientIOError` | `true` | 忽略客户端网络异常 |
| `docx4j.beetl.errorHandlerClass` | `org.beetl.core.ConsoleErrorHandler` | 错误处理类 |
| `docx4j.beetl.resource.root` | `/` | classpath 根路径 |
| `docx4j.beetl.resource.autoCheck` | `true` | 是否检测文件变化 |
| `docx4j.beetl.resource.functionRoot` | `functions` | 自定义脚本方法目录 |
| `docx4j.beetl.resource.functionSuffix` | `html` | 脚本方法文件后缀 |
| `docx4j.beetl.resource.tagRoot` | `htmltag` | 自定义标签目录 |
| `docx4j.beetl.resource.tagSuffix` | `tag` | 标签文件后缀 |

## easydoc-freemarker

引擎配置通过 `docx4j.freemarker.` 前缀的属性注入 FreeMarker `Configuration.setSettings`（仅接受 FreeMarker 已知键）。另有编程式 API：

```java
WordprocessingMLFreemarkerTemplate tpl = new WordprocessingMLFreemarkerTemplate();
tpl.setFreemarkerSettings(props);
tpl.setFreemarkerVariables(map);
tpl.setDefaultEncoding("UTF-8");
tpl.setPreTemplateLoaders(new FileTemplateLoader(new File("/templates")));
tpl.setPostTemplateLoaders(new ClassTemplateLoader(clazz, ""));
```

## easydoc-httl

`docx4j.httl.` 前缀属性，经 `ConfigUtils.filterWithPrefix` 注入 HTTL `Engine`。常用：

| 键 | 说明 |
|---|---|
| `docx4j.httl.template.directory` | 模板目录 |
| `docx4j.httl.template.suffix` | 模板后缀 |
| `docx4j.httl.input.encoding` / `docx4j.httl.output.encoding` | 输入/输出编码 |

## easydoc-jetbrick

`docx4j.jetx.` 前缀属性，经 `ConfigUtils.filterWithPrefix("docx4j.jetx.", "docx4j.", ...)` 注入 Jetbrick `JetConfig`。

## easydoc-rythm

`docx4j.rythm.` 前缀属性，经 `ConfigUtils.filterWithPrefix("docx4j.rythm.", ...)` 注入 Rythm `RythmEngine`。

## easydoc-thymeleaf

| 键 | 默认值 | 说明 |
|---|---|---|
| `docx4j.thymeleaf.templateResolver` | `org.thymeleaf.templateresolver.FileTemplateResolver` | 解析器类：File / ClassLoader / Url / ServletContext |
| `docx4j.thymeleaf.cacheable` | `true` | 是否缓存模板 |
| `docx4j.thymeleaf.cacheTTLMs` | （空=不设） | 缓存 TTL（毫秒）；空字符串按未设置处理 |
| `docx4j.thymeleaf.charset` | `UTF-8` | 字符集 |
| `docx4j.thymeleaf.checkExistence` | `false` | 检查模板存在性 |
| `docx4j.thymeleaf.name` | 解析器类名 | 解析器名称 |
| `docx4j.thymeleaf.order` | `1` | 解析器顺序 |
| `docx4j.thymeleaf.prefix` | （空） | 模板前缀 |
| `docx4j.thymeleaf.suffix` | `.tpl` | 模板后缀 |
| `docx4j.thymeleaf.templateMode` | `XHTML` | 模板模式 |
| `docx4j.thymeleaf.cacheablePatterns` / `nonCacheablePatterns` / `resolvablePatterns` | （空） | 模式集合（逗号分隔） |
| `docx4j.thymeleaf.newHtmlTemplateModePatterns` / `newCSS*` / `newJavaScript*` / `newRaw*` / `newText*` / `newXml*` | （空） | 各模式匹配集合 |
| `docx4j.thymeleaf.useDecoupledLogic` | `false` | 解耦逻辑 |

## easydoc-velocity

| 键 | 默认值 | 说明 |
|---|---|---|
| `docx4j.velocity.resource.loader` | `file` | 资源加载器（file / class / webapp） |
| `docx4j.velocity.file.resource.loader.class` | `org.apache.velocity.runtime.resource.loader.FileResourceLoader` | file 加载器类 |
| `docx4j.velocity.file.resource.loader.path` | `/tpl` | file 加载路径 |
| `docx4j.velocity.file.resource.loader.cache` | `false` | 缓存 |
| `docx4j.velocity.input.encoding` / `output.encoding` | `UTF-8` | 输入/输出编码 |
| `docx4j.velocity.runtime.log` | `velocity.log` | 日志文件 |
| `docx4j.velocity.runtime.log.logsystem.class` | `org.apache.velocity.runtime.log.NullLogChute` | 日志系统 |
| `docx4j.velocity.directive.foreach.counter.name` / `initial.value` | — | foreach 计数器 |
| `docx4j.velocity.webapp.resource.loader.class` | `org.apache.velocity.tools.view.servlet.WebappLoader` | webapp 加载器（javax；需 velocity-tools 2.0） |

## easydoc-webit

| 键 | 默认值 | 说明 |
|---|---|---|
| `docx4j.webit.engine.resourceLoader` | `webit.script.loaders.impl.ClasspathLoader` | 资源加载器 |
| `docx4j.webit.engine.encoding` | `UTF-8` | 输出编码 |
| `docx4j.webit.loader.encoding` | `UTF-8` | 加载编码 |
| `docx4j.webit.loader.root` | （空） | 加载根路径 |
| `docx4j.webit.engine.logger` | `webit.script.loggers.impl.NOPLogger` | 日志器 |
| `docx4j.webit.engine.looseVar` | `false` | 允许未声明变量 |
| `docx4j.webit.engine.suffix` | `.wit` | 模板后缀 |
| `docx4j.webit.engine.textStatementFactory` | `webit.script.core.text.impl.SimpleTextStatementFactory` | 文本语句工厂类 |
| `docx4j.webit.engine.trimCodeBlockBlankLine` | `true` | 修剪代码块空行 |
| `docx4j.webit.engine.appendLostSuffix` | `false` | 补充丢失后缀 |
| `docx4j.webit.engine.shareRootData` | `true` | 共享根数据 |
| `docx4j.webit.engine.vars` | （空） | 预设变量 |

## XHTML / 输出（easydoc-xhtml + core）

| 键 | 默认值 | 说明 |
|---|---|---|
| `docx4j.Convert.Out.HTML.CssIncludeUri` | （空） | 引入外部 CSS（仅允许 `https`/`file` scheme） |
| `docx4j.Convert.Out.HTML.CssIncludePath` | （空） | 引入本地 CSS 文件 |
| `docx4j.Convert.Out.HTML.ImageTargetUri` | `images` | 图片输出目录名 |
| `docx4j.PageSize` | `A4` | 默认纸张 |
| `docx4j.PageOrientationLandscape` | `true` | 默认横向 |
| `docx4j.PageMargins` | `NORMAL` | 页边距（NORMAL/NARROW/MODERATE/WIDE） |
| `docx4j.DPI` | `96` | 图片 DPI |
