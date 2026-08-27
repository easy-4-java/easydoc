# XHTMLImporterUtils XXE 防护硬化（Sprint 2）

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development. Steps use checkbox (`- [ ]`) syntax for tracking.

## Goal

硬化 `easydoc-xhtml` 的 XHTML 解析路径（`XHTMLImporterUtils` + docx4j 底层 `DocumentBuilderFactory`），使 DOCTYPE 声明与外部实体**直接被解析器拒绝**——而不只是依赖"SET 前后系统属性"的间接缓解。完成后**新增一个最小化的恶意输入回归测试**确认拒绝行为。

## Background facts

- 当前测试 `XHTMLImporterUtilsXxeProtectionTest` **只覆盖系统属性序列化窗口的恢复**，未验证 DOCTYPE 是否被解析器**实际拒绝**
- 生产代码路径：`XHTMLImporterUtils.handle()` 通过 docx4j 的 `XHTMLImporterImpl` 解析 XHTML，底层用 `javax.xml.parsers.DocumentBuilderFactory`
- 攻击场景：用户提供包含 `<!DOCTYPE foo [<!ENTITY x SYSTEM "file:///etc/passwd">]>` 的 XHTML 输入，当前仅依赖系统属性 `javax.xml.accessExternalDTD=""` 缓解，**未禁 DOCTYPE 本身**

## Scope

- 仅 `easydoc-xhtml` 模块
- 仅 `XHTMLImporterUtils` 路径的入站 XHTML 解析
- 不修改 docx4j 自身
- 不触碰 PDF 路径、快路径 Markdown、结构化 Markdown（颜色 plan 已在另一工作树进行）
- 不引入新依赖

## Design

### 工厂配置（最小侵入）

定位 docx4j 在解析 XHTML 时构造 `DocumentBuilderFactory` 的位置——通常在 `org.docx4j.convert.in.xhtml.XHTMLImporterImpl` 内部，通过 `DocumentBuilderFactory.newInstance()`。我们无法直接修改 docx4j 源码，但有两个可行路径：

**Option A（推荐，零代码侵入）**：通过 `System.setProperty` 在 `XHTMLImporterUtils.handle()` 入口设置 `_jdk.xml.elementAttributeLimit` 等全局属性 + 在调用前临时替换 `DocumentBuilderFactory.newInstance()` 返回的实例**做不到**，因为 factory 是 docx4j 内部 new 的。

**Option B（实际方案）**：在 `XHTMLImporterUtils.handle()` 入口**先主动触发一次** `DocumentBuilderFactory.newInstance()` 并配置好 XXE 安全特性。这个实例化动作的目的是"预热/审计"——把当前 JVM 内的默认 factory 设好安全配置（如果后续代码用相同 classloader 拿到的实例沿用同一配置）。同时为这个 factory 设置的 `setFeature` 不影响 docx4j 自己的实例化。

**Option C（最稳）**：在 `XHTMLImporterUtils.handle()` 入口设置 `javax.xml.parsers.DocumentBuilderFactory` 系统属性，指向我们自己的工厂包装类（通过反射或 `ThreadLocal`）。这与现有 accessExternalDTD/SCHEMA 的 `System.setProperty` 模式一致，但需要重新审视 docx4j 内部是否会读取该系统属性。

**简化决策**：采用 **Option C 配合 docx4j 已知的钩子**——

1. 在 `XHTMLImporterUtils.handle()` 入口，除现有 `accessExternalDTD/SCHEMA` 序列化窗口外，**额外**设置 `javax.xml.parsers.DocumentBuilderFactory` 系统属性为项目内一个**安全的包装工厂类** `io.github.easy4j.doc.xhtml.utils.SecureDocumentBuilderFactory`，它继承 `DocumentBuilderFactory` 并在构造器中应用全部 XXE 防护 + `FEATURE_SECURE_PROCESSING` + `setXIncludeAware(false)` + `setExpandEntityReferences(false)`
2. docx4j `DocumentBuilderFactory.newInstance()` 走系统属性查找时，会加载我们的工厂
3. 在 `handle()` 退出前恢复原系统属性值（与现有 accessExternalDTD/SCHEMA 模式一致——"caller 原值优先"语义）

### 安全工厂类

新建 `easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/utils/SecureDocumentBuilderFactory.java`：

```java
package io.github.easy4j.doc.xhtml.utils;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * XXE 防护强化的 DocumentBuilderFactory：构造函数内统一应用 SAX 解析器的安全配置。
 *
 * <p>由 {@link XHTMLImporterUtils} 通过 javax.xml.parsers.DocumentBuilderFactory 系统属性
 * 指向本类，作为 docx4j 内部 DocumentBuilderFactory.newInstance() 的拦截器。</p>
 *
 * <p>防护特性：</p>
 * <ul>
 *   <li>禁用 DOCTYPE 声明（disallow-doctype-decl）</li>
 *   <li>禁用外部通用实体（external-general-entities）</li>
 *   <li>禁用外部参数实体（external-parameter-entities）</li>
 *   <li>启用 XMLConstants.FEATURE_SECURE_PROCESSING</li>
 *   <li>禁用 XInclude</li>
 *   <li>禁用实体引用展开</li>
 * </ul>
 *
 * <p>继承 DocumentBuilderFactory 而非包装，保证 docx4j 调用 {@code newInstance().newDocumentBuilder()}
 * 链路不被打断。</p>
 */
public class SecureDocumentBuilderFactory extends DocumentBuilderFactory {

    public SecureDocumentBuilderFactory() {
        super();
        try {
            // 禁用 DOCTYPE 声明（最高优先级）
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            // 禁用外部实体
            setFeature("http://xml.org/sax/features/external-general-entities", false);
            setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // 启用 JDK 通用安全处理
            setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // 禁用 XInclude（避免包含外部 XML）
            setXIncludeAware(false);
            // 禁用实体引用展开（防止内部实体递归炸弹）
            setExpandEntityReferences(false);
        } catch (ParserConfigurationException e) {
            // 配置失败即抛错——绝不做静默降级（降级等于无防护）
            throw new IllegalStateException(
                "Unable to configure XXE protection for DocumentBuilderFactory: " + e.getMessage(), e);
        }
    }
}
```

### 入口集成

修改 `XHTMLImporterUtils.handle()`：在现有 `accessExternalDTD/SCHEMA` 序列化窗口**外层**，再添加一层 `javax.xml.parsers.DocumentBuilderFactory` 系统属性的临时覆盖：

```java
private static final ThreadLocal<String> SAVED_DBF = new ThreadLocal<>();

static {
    // class init 时不做覆盖（避免污染其他调用方）；在 handle() 入口临时覆盖
}

public static WordprocessingMLPackage handle(...) {
    String savedDbf = System.getProperty("javax.xml.parsers.DocumentBuilderFactory");
    SAVED_DBF.set(savedDbf);
    System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
        SecureDocumentBuilderFactory.class.getName());
    try {
        // 原有 accessExternalDTD/SCHEMA 序列化窗口 + 解析逻辑（保持不变）
        ...
    } finally {
        // 恢复两个系统属性
        String dbf = SAVED_DBF.get();
        SAVED_DBF.remove();
        if (dbf == null) {
            System.clearProperty("javax.xml.parsers.DocumentBuilderFactory");
        } else {
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", dbf);
        }
        // 原有 accessExternalDTD/SCHEMA 恢复（保持不变）
    }
}
```

## Tasks

### Task 1: SecureDocumentBuilderFactory
**File (Create):** `easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/utils/SecureDocumentBuilderFactory.java`

按上面的代码块实现。Chinese Javadoc。

**Test (Create):** `SecureDocumentBuilderFactoryTest.java`（同包 test 目录）
- `new SecureDocumentBuilderFactory()` 不抛
- `newDocumentBuilder()` 能 new 一个 Document
- `getFeature("http://apache.org/xml/features/disallow-doctype-decl") == true`
- `getFeature("http://xml.org/sax/features/external-general-entities") == false`
- 解析含 `<!DOCTYPE foo [...]><foo/>` 的输入应抛 `SAXParseException`（或 `ParserConfigurationException`）而不是返回 Document

**`verify:`** `mvn -Denforcer.skip=true -pl easydoc-xhtml -am test -Dtest=SecureDocumentBuilderFactoryTest` 全绿。

**`commit:`** `feat(xhtml): add SecureDocumentBuilderFactory with XXE protection (disallow-doctype-decl + FEATURE_SECURE_PROCESSING)`

### Task 2: XHTMLImporterUtils 集成
**File (Modify):** `easydoc-xhtml/src/main/java/io/github/easy4j/doc/xhtml/utils/XHTMLImporterUtils.java`

按上面的入口集成代码，在现有 accessExternalDTD/SCHEMA 序列化窗口**外层**添加 DocumentBuilderFactory 系统属性的临时覆盖与恢复。**保持**现有所有行为（序列化窗口、try/finally、并发测试不破）。

**Test (Modify):** `XHTMLImporterUtilsXxeProtectionTest.java`
**新增用例：**
- `handleRejectsDoctypeDeclaration()`：传入 `Jsoup.parse("<!DOCTYPE foo [<!ENTITY x SYSTEM 'file:///etc/passwd'>]><html><body><p>hi</p></body></html>")`，调用 `handle()`，期望**抛** `SAXParseException`（docx4j 包装）或 `XHTMLImportException`，**不能**返回含 `&x;` 的 docx
- `handleRestoresDocumentBuilderFactoryProperty()`：预先 `System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.example.OtherFactory")`，调用 `handle()`，结束后 `System.getProperty(...)` 恢复为 caller 原值
- `handleClearsDocumentBuilderFactoryProperty()`：caller 未设置该属性时，调用 `handle()` 后属性被清除

**`verify:`** `mvn -Denforcer.skip=true -pl easydoc-xhtml -am test` 全绿，jacoco 0 违规。

**`commit:`** `fix(xhtml): apply SecureDocumentBuilderFactory via system property in XHTMLImporterUtils (XXE hardening)`

### Task 3: 文档与 CHANGELOG
**Files (Modify):**
- `CHANGELOG.md`（3.0.x 段追加）
- `docs/release-central.md`（如有提及 XXE 风险点则更新）

**CHANGELOG:** 在 `## [3.0.x.20260831]` 段内追加：
```
### 安全修复

- **XXE 防护硬化（XHTMLImporterUtils）**：之前依赖 System.setProperty 序列化窗口
  （accessExternalDTD/SCHEMA）作为间接缓解，未实际禁用 DOCTYPE 解析本身。新增
  `SecureDocumentBuilderFactory`（继承 DocumentBuilderFactory，构造器内统一应用
  disallow-doctype-decl / FEATURE_SECURE_PROCESSING / 关闭外部实体与 XInclude），通过
  javax.xml.parsers.DocumentBuilderFactory 系统属性指向该类，作为 docx4j 内部工厂
  实例化的拦截器。恶意 XHTML 输入中的 DOCTYPE 与外部实体引用将被解析器直接拒绝。
  新增 3 个回归测试覆盖 DOCTYPE 拒绝与系统属性恢复。
```

**`verify:`** grep 验证两个文件包含新关键词。

**`commit:`** `docs(xhtml): CHANGELOG entry for XXE hardening in XHTMLImporterUtils`

## Definition of Done

1. `git log --oneline feature/3.0.x..HEAD` 包含 3 个 commit（Task 1-3），无合并提交
2. `mvn -Denforcer.skip=true -pl easydoc-xhtml -am clean verify` BUILD SUCCESS，jacoco 0 违规
3. **DOCTYPE 拒绝用例**断言：传入含 DOCTYPE 的输入，`handle()` 抛异常（**不能**返回 docx 或返回含 `&x;` 内容的 docx）
4. **属性恢复用例**：caller 预先设置过 `javax.xml.parsers.DocumentBuilderFactory`，`handle()` 结束后恢复 caller 原值
5. **属性清除用例**：caller 未设置该属性，`handle()` 后属性被清除
6. **不触碰 1.0/2.0 分支**（由 controller 在 Sprint 后续同步）
7. **不引入新依赖**
8. CHANGELOG 已更新

## Risk note

- 如果 docx4j 在某版本中绕过系统属性、直接 hard-code `DocumentBuilderFactory.newInstance()` 调用，则 `SecureDocumentBuilderFactory` 拦截会失效——这是一个需要后续验证的点（Task 2 完成后做一次集成测试：传入含 `<!ENTITY x SYSTEM 'file:///etc/passwd'>` 的真实 XHTML，确认抛异常而非返回含 `&x;` 的 docx）
- 如果发现 docx4j 不读系统属性，可升级到 Option B/C 中的更激进方案（反射替换工厂实例）——但作为 v1 方案先按系统属性路径走，docx4j 6.x/7.x 都尊重此属性
