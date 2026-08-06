# easydoc

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-8-orange)](https://github.com/easy-4-java/easydoc) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](./LICENSE)

基于 [docx4j](https://www.docx4java.org/) 与多种模板引擎实现的 Word（.docx）快速输出组件。支持从模板（Freemarker、Velocity、Thymeleaf、Beetl、Rythm、Jetbrick、HTTL、Webit、JSP）或直接由 XHTML 渲染 WordprocessingML 文档。

## 目录

- [1. 项目概览](#1-项目概览)
- [2. 功能与状态](#2-features--status)
- [3. 环境要求与兼容性](#3-requirements--compatibility)
- [4. 架构与模块](#4-architecture--modules)
- [5. 安装](#5-installation)
- [6. 快速开始](#6-quick-start)
- [7. 配置](#7-configuration)
- [8. 核心用法 / API](#8-core-usage--api)
- [9. 测试与构建](#9-testing--build)
- [10. 版本线与分支](#10-versioning--branches)
- [11. 参与贡献与许可协议](#11-contributing--license)

## 1. 项目概览

`easydoc`（项目描述：*Building doc documents based on xhtml templates using docx4j*）是一个多模块的 Word 文档快速输出组件。核心模块定义 `WordprocessingMLTemplate` 契约（模板 + 变量 -> `WordprocessingMLPackage`），各引擎适配模块与 XHTML 导入路径分别独立成模块。

| 是什么 | 不是什么 |
|:---|:---|
| 基于模板的 .docx 生成（docx4j） | PDF 渲染器（参见姊妹项目 `easypdf`） |
| 可插拔模板引擎（Freemarker、Velocity、Thymeleaf、Beetl、Rythm、Jetbrick、HTTL、Webit、JSP、XHTML） | 文档编辑器或查看器 |
| 直接 XHTML -> WordprocessingML 转换 | 云文档服务 |

典型使用场景：

| 场景 | 模块 |
|:---|:---|
| 用变量 Map 填充 Word 模板 | `easydoc-core`（`WordprocessingMLDocxTemplate`） |
| 使用常用引擎渲染模板 | `easydoc-freemarker` / `-velocity` / `-thymeleaf` / `-beetl` / `-rythm` / `-jetbrick` / `-httl` / `-webit` / `-jsp` |
| 将 XHTML（或 URL / Document）转换为 WordprocessingML 包 | `easydoc-xhtml` |
| 统一管理依赖版本 | `easydoc-bom` |

**项目状态：** 稳定。

<a id="2-features--status"></a>
## 2. 功能与状态

| 能力 | 状态 | 说明 |
|:---|:---|:---|
| `WordprocessingMLTemplate` 契约 | 可用 | `process(File/InputStream, Map<String,Object>)` -> `WordprocessingMLPackage` |
| `WordprocessingMLDocxTemplate` | 可用 | 通过 `Docx4J.load` 加载 `.docx` 模板；未提供模板时创建示例文档 |
| Freemarker 引擎 | 可用 | `WordprocessingMLFreemarkerTemplate`（另有 `process(String, Map)` 重载） |
| Velocity / Thymeleaf / Beetl / Rythm / Jetbrick / HTTL / Webit / JSP 引擎 | 可用 | 每引擎一个模块，`WordprocessingML{引擎}Template` |
| XHTML 导入 | 可用 | `WordprocessingMLHtmlTemplate`（File / InputStream / `Document` / URL）+ `XHTMLImporterUtils` |
| WML 工具 | 可用 | WML 元素/段落/边框工具、变量清理、zip 辅助、字体映射（`ChineseFont`、`FontMapperHolder`） |
| 输出管线 | 可用 | `WordprocessingMLPackageRender` / `-Writer` / `-Extractor` |
| 构建事件 / 错误处理 | 可用 | `bus.event`（构建开始/结束）与 `bus.error.Slf4jLogger` |
| CI 流水线 | 未配置 | 仓库中无 CI 工作流文件 |

<a id="3-requirements--compatibility"></a>
## 3. 环境要求与兼容性

| 依赖项 | 版本 |
|:---|:---|
| JDK | 8 |
| Maven | 3.0+ |
| docx4j | `docx4j-core` + JAXB 变体（Internal / MOXy / ReferenceImpl） |
| OGNL | 核心模块表达式支持 |
| 引擎模块 | Freemarker、Velocity、Thymeleaf、Beetl、Rythm、Jetbrick、HTTL、Webit、JSP（按模块） |

### 版本线矩阵

| 分支 | JDK | 版本号模式 |
|:---|:---|:---|
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

<a id="4-architecture--modules"></a>
## 4. 架构与模块

```text
  模板来源                          easydoc 模块                     输出
  --------                         ------------                    ------
  .docx 模板    ->  easydoc-core  （WordprocessingMLTemplate）
  .ftl / .vm / .tpl ->  easydoc-{freemarker,velocity,beetl,thymeleaf,
                          rythm,jetbrick,httl,webit,jsp}
  .html / .xhtml    ->  easydoc-xhtml（WordprocessingMLHtmlTemplate +
                          XHTMLImporterUtils）
                                |
                                v
                     WordprocessingMLPackage（docx4j）
                                |
                                v
                     渲染 / 写出 / 抽取（easydoc-core io.*）
                                |
                                v
                            输出 .docx
```

| 模块 | 职责 |
|:---|:---|
| `easydoc-core` | 模板契约、docx4j/WML 工具、渲染/写出/抽取管线 |
| `easydoc-xhtml` | XHTML/HTML -> `WordprocessingMLPackage`（基于 docx4j ImportXHTML） |
| `easydoc-freemarker` / `easydoc-velocity` / `easydoc-thymeleaf` / `easydoc-beetl` / `easydoc-rythm` / `easydoc-jetbrick` / `easydoc-httl` / `easydoc-webit` / `easydoc-jsp` | 每模板引擎一个适配模块 |
| `easydoc-bom` | 依赖管理 BOM |

<a id="5-installation"></a>
## 5. 安装

### Maven

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>easydoc-core</artifactId>
    <version>1.0.x.20260630-SNAPSHOT</version>
</dependency>
```

按需引入引擎模块，例如：

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>easydoc-freemarker</artifactId>
    <version>1.0.x.20260630-SNAPSHOT</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.easy4j:easydoc-core:1.0.x.20260630-SNAPSHOT'
implementation 'io.github.easy4j:easydoc-freemarker:1.0.x.20260630-SNAPSHOT'
```

**可用性：** 构件发布至阿里云私有 Maven 仓库，并通过 GitHub Releases 分发；尚未发布到 Maven Central。

<a id="6-quick-start"></a>
## 6. 快速开始

用变量 Map 填充 `.docx` 模板：

```java
import io.github.easy4j.doc.WordprocessingMLDocxTemplate;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

WordprocessingMLTemplate template = new WordprocessingMLDocxTemplate();

Map<String, Object> variables = new HashMap<>();
variables.put("title", "Meeting Minutes");
variables.put("author", "Alice");

WordprocessingMLPackage doc = template.process(new File("template.docx"), variables);
doc.save(new File("output.docx"));
```

预期结果：`output.docx` 为渲染后的文档。未传入模板文件时，会通过 `SampleDocument` 创建示例文档。

<a id="7-configuration"></a>
## 7. 配置

核心库为模板驱动，无需配置文件。引擎适配模块支持以编程方式传入引擎专属设置：

- `WordprocessingMLFreemarkerTemplate.setFreemarkerSettings(Properties)` — FreeMarker 设置
- `WordprocessingMLBeetlTemplate` / `WordprocessingMLFreemarkerTemplate` 构造器支持 `landscape` / `altChunk` 标志，以及基于 XHTML 的变体（`WordprocessingMLHtmlTemplate`）

<a id="8-core-usage--api"></a>
## 8. 核心用法 / API

### 8.1 模板契约

```java
public interface WordprocessingMLTemplate {
    default WordprocessingMLPackage process(File template, Map<String, Object> variables) throws Exception;
    default WordprocessingMLPackage process(InputStream template, Map<String, Object> variables) throws Exception;
    default WordprocessingMLPackage process(String template, Map<String, Object> variables) throws Exception;
    ...
}
```

### 8.2 Freemarker 模板

```java
WordprocessingMLFreemarkerTemplate tpl = new WordprocessingMLFreemarkerTemplate();
WordprocessingMLPackage doc = tpl.process(new File("report.ftl"), variables);
```

### 8.3 XHTML 转 Word

```java
WordprocessingMLHtmlTemplate html = new WordprocessingMLHtmlTemplate();
WordprocessingMLPackage doc = html.process(new File("page.html"));
```

### 8.4 核心包结构

| 包 | 内容 |
|:---|:---|
| `io.github.easy4j.doc` | `WordprocessingMLTemplate`、`WordprocessingMLDocxTemplate`、`Docx4jConstants`、`SampleDocument`、docx4j SAX/StAX 模板变体 |
| `io.github.easy4j.doc.io` | `WordprocessingMLPackageRender` / `-Writer` / `-Extractor` / `WordprocessingMLTemplateWriter` |
| `io.github.easy4j.doc.handler` | 输出转换与变量替换处理器 |
| `io.github.easy4j.doc.utils` | docx4j / WML / zip / 字体 / 段落 / 边框工具 |
| `io.github.easy4j.doc.wml` | WML 元素渲染与 `WMLType` |
| `io.github.easy4j.doc.fonts` | `ChineseFont`、`FontMapperHolder` |
| `io.github.easy4j.doc.bus` | 构建事件与错误日志 |

<a id="9-testing--build"></a>
## 9. 测试与构建

```bash
./mvnw clean verify        # 构建全部模块、运行测试、生成覆盖率报告
./mvnw clean install       # 安装全部模块到本地仓库
```

- 测试位于 `easydoc-core`（docx4j 演示型测试）、`easydoc-velocity` 与 `easydoc-xhtml`（HTML 转换演示）。
- 覆盖率由 JaCoCo Maven 插件度量（目标：90% 行覆盖率，`haltOnFailure=false`）。
- `release` profile 组装 GPG 签名 + 源码 + Javadoc + 部署（`./mvnw -Prelease clean deploy`）。

<a id="10-versioning--branches"></a>
## 10. 版本线与分支

仓库维护三条并行版本线：

| 分支 | JDK | 版本号模式 |
|:---|:---|:---|
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

维护策略：在 JDK 8 作为基线的同时，1.0.x 版本线接收缺陷修复；新功能开发主要面向 2.0.x / 3.0.x 版本线。

<a id="11-contributing--license"></a>
## 11. 参与贡献与许可协议

欢迎参与贡献——请通过 Issue 反馈问题，或向对应版本线分支提交 Pull Request（JDK 8 相关改动提交到 `feature/1.0.x`）。

本项目基于 [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可发布。详见仓库根目录的 `LICENSE` 文件。

参考：
- https://www.docx4java.org/
- https://github.com/easy-4-java/easydoc
