# easydoc

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-17-orange)](https://github.com/easy-4-java/easydoc) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](./LICENSE)

A Word (.docx) generation component built on [docx4j](https://www.docx4java.org/) and a variety of template engines. Render WordprocessingML documents from templates (Freemarker, Velocity, Thymeleaf, Beetl, Rythm [deprecated], Jetbrick, HTTL [deprecated], Webit [deprecated], JSP) or directly from XHTML.

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Features & Status](#2-features--status)
- [3. Requirements & Compatibility](#3-requirements--compatibility)
- [4. Architecture & Modules](#4-architecture--modules)
- [5. Installation](#5-installation)
- [6. Quick Start](#6-quick-start)
- [7. Configuration](#7-configuration)
- [8. Core Usage / API](#8-core-usage--api)
- [9. Testing & Build](#9-testing--build)
- [10. Versioning & Branches](#10-versioning--branches)
- [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

`easydoc` (project description: *Building doc documents based on xhtml templates using docx4j*) is a multi-module component for fast Word document output. A core module defines the `WordprocessingMLTemplate` contract (template + variables -> `WordprocessingMLPackage`), while separate modules adapt each template engine and an XHTML import path.

| What it is | What it is not |
|:---|:---|
| Template-driven .docx generation (docx4j) | A PDF renderer (see the sibling project `easypdf`) |
| Pluggable template engines (Freemarker, Velocity, Thymeleaf, Beetl, Rythm, Jetbrick, HTTL, Webit, JSP, XHTML) | A document editor or viewer |
| Direct XHTML -> WordprocessingML conversion | A cloud document service |

Typical use cases:

| Use case | Module |
|:---|:---|
| Fill a Word template with a variable map | `easydoc-core` (`WordprocessingMLDocxTemplate`) |
| Render templates with your favorite engine | `easydoc-freemarker` / `-velocity` / `-thymeleaf` / `-beetl` / `-rythm` / `-jetbrick` / `-httl` / `-webit` / `-jsp` |
| Convert XHTML (or a URL / document) to a WordprocessingML package | `easydoc-xhtml` |
| Manage shared dependency versions | `easydoc-bom` |

**Project status:** stable.

## 2. Features & Status

| Feature | Status | Notes |
|:---|:---|:---|
| `WordprocessingMLTemplate` contract | Available | `process(File/InputStream, Map<String,Object>)` -> `WordprocessingMLPackage` |
| `WordprocessingMLDocxTemplate` | Available | Loads a `.docx` template via `Docx4J.load`, creates a dummy document when no template is given |
| Freemarker engine | Available | `WordprocessingMLFreemarkerTemplate` (also `process(String, Map)` overload) |
| Velocity / Thymeleaf / Beetl / JSP engines | Available | One module per engine, `WordprocessingML{Engine}Template` |
| Rythm engine | Deprecated | Upstream unmaintained since 2015; functional, but new projects should use Freemarker / Thymeleaf / Velocity |
| HTTL engine | Deprecated | Upstream unmaintained since 2014; functional, but new projects should use Freemarker / Thymeleaf / Velocity |
| Webit engine | Deprecated | Upstream unmaintained since 2016; functional, but new projects should use Freemarker / Thymeleaf / Velocity |
| XHTML import | Available | `WordprocessingMLHtmlTemplate` (File / InputStream / `Document` / URL) + `XHTMLImporterUtils` |
| WML utilities | Available | WML element / paragraph / border utilities, variable clearing, zip helpers, font mapping (`ChineseFont`, `FontMapperHolder`) |
| Output pipeline | Available | `WordprocessingMLPackageRender` / `-Writer` / `-Extractor` |
| Build events / error handling | Available | `bus.event` (build start/finish) and `bus.error.Slf4jLogger` |
| CI pipeline | Not configured | No CI workflow files in the repository |

## 3. Requirements & Compatibility

| Requirement | Version |
|:---|:---|
| JDK | 21+ |
| Maven | 4.0.0-rc-6 or newer (enforced by `requireMavenVersion`; note that in Maven version ordering `4.0.0-rc-N` is lower than `4.0.0`) |
| docx4j | 17.0.3 (`docx4j-core` + `docx4j-JAXB-ReferenceImpl`); `docx4j-ImportXHTML-core` 17.0.2; legacy `docx4j-openxml-objects(-pml)` pinned at 11.5.14 for compatibility |
| OGNL | Expression support in the core module |
| Engine modules | Freemarker, Velocity, Thymeleaf, Beetl, Rythm, Jetbrick, HTTL, Webit, JSP (per-module) |

> **Maven 3 note:** Maven 3.9.x users must pass `-Denforcer.skip=true` (the enforcer
> gate, including the `docx4j-openxml-objects` ban, will not run). Building the
> regular way — `mvn clean verify` with no skips — requires Maven 4.

### Version lines

| Branch | JDK | Version pattern |
|:---|:---|:---|
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

## 4. Architecture & Modules

```text
  Template sources                       easydoc modules                 output
  ----------------                       --------------                 ------
  .docx template    ->  easydoc-core  (WordprocessingMLTemplate)
  .ftl / .vm / .tpl ->  easydoc-{freemarker,velocity,beetl,thymeleaf,
                         rythm,jetbrick,httl,webit,jsp}
  .html / .xhtml    ->  easydoc-xhtml (WordprocessingMLHtmlTemplate +
                         XHTMLImporterUtils)
                                |
                                v
                     WordprocessingMLPackage (docx4j)
                                |
                                v
                         render / write / extract (easydoc-core io.*)
                                |
                                v
                             output .docx
```

| Module | Responsibility |
|:---|:---|
| `easydoc-core` | Template contract, docx4j/WML utilities, render/write/extract pipeline |
| `easydoc-xhtml` | XHTML/HTML -> `WordprocessingMLPackage` (docx4j ImportXHTML based) |
| `easydoc-freemarker` / `easydoc-velocity` / `easydoc-thymeleaf` / `easydoc-beetl` / `easydoc-rythm` (deprecated) / `easydoc-jetbrick` / `easydoc-httl` (deprecated) / `easydoc-webit` (deprecated) / `easydoc-jsp` | One adapter per template engine |
| `easydoc-bom` | Dependency management BOM |

## 5. Installation

### Maven

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>easydoc-core</artifactId>
    <version>2.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

Add the engine module(s) you need, e.g.:

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>easydoc-freemarker</artifactId>
    <version>2.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.easy4j:easydoc-core:2.0.x.x.20260630-SNAPSHOT'
implementation 'io.github.easy4j:easydoc-freemarker:2.0.x.x.20260630-SNAPSHOT'
```

**Availability:** the artifacts are published to the Aliyun private Maven repository and distributed through GitHub Releases; they have not yet been published to Maven Central.

## 6. Quick Start

Fill a `.docx` template with a variable map:

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

Expected result: `output.docx` contains the rendered document. When no template file is passed, a dummy sample document is created (via `SampleDocument`).

## 6.1 EasyDocx（类 EasyExcel 体验）

POJO 模型 + 注解 + 链式门面（docx 语义：`document` 而非 sheet）：

```java
import io.github.easy4j.doc.easy.EasyDocx;
import io.github.easy4j.doc.annotation.DocxField;
import io.github.easy4j.doc.annotation.DocxIgnore;

public class Contract {
    @DocxField("partyName") private String partyName;
    @DocxField(value = "signDate", format = "yyyy-MM-dd") private java.util.Date signDate;
    @DocxIgnore private String internalId;
    // getters/setters...
}

// 写：门面 + 链式 + 注解模型（POJO → 占位符）
org.docx4j.openpackaging.packages.WordprocessingMLPackage doc =
        EasyDocx.write("template.docx", Contract.class)
                .document("合同")      // docx 语义中间层（Document 概念），单文档可省略
                .process(contract);    // 返回 WordprocessingMLPackage

// 读：监听器
EasyDocx.read("template.docx", Contract.class, (data, values) -> {
        // values: 占位符名 → 值
    }).doRead();

// 原始 Map 仍兼容
EasyDocx.write("template.docx", Contract.class)
        .process(java.util.Map.of("partyName", "ACME"));
```

### Markdown ↔ docx

```java
import io.github.easy4j.doc.xhtml.markdown.EasyMarkdown;

// Markdown → docx（快速转换，复用 HTML 管线）
org.docx4j.openpackaging.packages.WordprocessingMLPackage doc =
        EasyMarkdown.markdownToDocx("# 标题\n\n**加粗** 内容");

// docx → Markdown
String md = EasyMarkdown.docxToMarkdown(doc);
```

## 7. Configuration

The core library is template-driven and requires no configuration file. Engine adapters may accept engine-specific settings programmatically:

- `WordprocessingMLFreemarkerTemplate.setFreemarkerSettings(Properties)` — FreeMarker settings
- `WordprocessingMLBeetlTemplate` / `WordprocessingMLFreemarkerTemplate` constructors support `landscape` / `altChunk` flags and an XHTML-based variant (`WordprocessingMLHtmlTemplate` based)

## 8. Core Usage / API

### 8.1 Template contract

```java
public interface WordprocessingMLTemplate {
    default WordprocessingMLPackage process(File template, Map<String, Object> variables) throws Exception;
    default WordprocessingMLPackage process(InputStream template, Map<String, Object> variables) throws Exception;
    default WordprocessingMLPackage process(String template, Map<String, Object> variables) throws Exception;
    ...
}
```

### 8.2 Freemarker template

```java
WordprocessingMLFreemarkerTemplate tpl = new WordprocessingMLFreemarkerTemplate();
WordprocessingMLPackage doc = tpl.process(new File("report.ftl"), variables);
```

### 8.3 XHTML to Word

```java
WordprocessingMLHtmlTemplate html = new WordprocessingMLHtmlTemplate();
WordprocessingMLPackage doc = html.process(new File("page.html"));
```

### 8.4 Core packages

| Package | Contents |
|:---|:---|
| `io.github.easy4j.doc` | `WordprocessingMLTemplate`, `WordprocessingMLDocxTemplate`, `Docx4jConstants`, `SampleDocument`, docx4j SAX/StAX template variants |
| `io.github.easy4j.doc.io` | `WordprocessingMLPackageRender` / `-Writer` / `-Extractor` / `WordprocessingMLTemplateWriter` |
| `io.github.easy4j.doc.handler` | Output-conversion and variable-replacement handlers |
| `io.github.easy4j.doc.utils` | docx4j / WML / zip / font / paragraph / border utilities |
| `io.github.easy4j.doc.wml` | WML element rendering and `WMLType` |
| `io.github.easy4j.doc.fonts` | `ChineseFont`, `FontMapperHolder` |
| `io.github.easy4j.doc.bus` | Build events and error logging |

## 9. Testing & Build

```bash
./mvnw clean verify        # build all modules, run tests, generate coverage report
./mvnw clean install       # install all modules into the local repository
```

- Tests exist across all modules (1000+ tests, JUnit 5).
- Coverage is measured with the JaCoCo Maven plugin: **90% line coverage per module, `haltOnFailure=true`** — the gate fails the build below 90%.
- The `release` profile assembles GPG signing + sources + Javadoc + deployment (`./mvnw -Prelease clean deploy`).

## Known limitations (3.0.x)

| Limitation | Details | Workaround |
|:---|:---|:---|
| SAX template on JDK 21 | `WordprocessingMLDocxSaxTemplate` is incompatible with JDK 21 (docx4j 17.0.3 `SAXHandler` limitation). The `DocxTemplates` factory short-circuits `SAX` → `STAX` on JDK 21+ automatically. | Use `DocxMode.STAX` explicitly, or rely on the automatic short-circuit. |
| httl / rythm / webit engines | Upstream libraries are unmaintained (last releases: 2014–2016). Functional and tested, but `@Deprecated` since 3.0. | New projects should use Freemarker, Thymeleaf, or Velocity. |
| JSP module requires a servlet container | `easydoc-jsp` now delegates to `RequestDispatcher.include` (Tomcat 9.x, javax). | Run inside a servlet container; see `MIGRATION.md` §2.3. |
| `IdentityPlusMapper` on JVM 21 | Font-mapping config helpers (`configChineseFonts` etc.) fail during docx4j font discovery on JVM 21. | Avoid these helpers on JVM 21; see `MIGRATION.md` §4. |

See [MIGRATION.md](./MIGRATION.md) for 2.x → 3.x breaking changes and [CONFIGURATION.md](./CONFIGURATION.md) for the full engine configuration reference.

## 10. Versioning & Branches

Three parallel version lines are maintained:

| Branch | JDK | Version pattern |
|:---|:---|:---|
| `feature/1.0.x` | JDK 8 | `1.0.x.*` |
| `feature/2.0.x` | JDK 17 | `2.0.x.*` |
| `feature/3.0.x` | JDK 21 | `3.0.x.*` |

Maintenance strategy: the 1.0.x line receives bug fixes while JDK 8 remains the baseline; feature development primarily targets the 2.0.x / 3.0.x lines.

## 11. Contributing & License

Contributions are welcome — open an issue or submit a pull request against the matching version-line branch (`feature/2.0.x` for JDK 17 changes).

This project is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0). See the `LICENSE` file in the repository root for details.
