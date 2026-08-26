# EasyDocx API 封装实现计划（P0 → P1）

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 给 easydoc 补齐 EasyExcel 式使用体验——`EasyDocx` 静态门面 + `@DocxField` 注解模型 + 链式 Builder + 监听器，用 docx 语义（document 替代 sheet），POJO 驱动替代 Map 驱动。

**Architecture:** 薄封装层（对齐 easyodf 的定位：不替代 docx4j/easydoc 引擎，简化 80% 常见场景）。新增 `@DocxField`/`@DocxIgnore` 注解 + `DocxFields`（POJO→Map 反射转换，对齐 easyodf 的 OFDReflectionUtils），新增 `EasyDocx` 门面 + `DocxWriterBuilder`/`DocxReaderBuilder`（链式，内部委托现有 `DocxTemplates.create(DocxMode)` + `WordprocessingMLTemplate.process(Map)` 管线，零重写）。docx 无 sheet，中间层用 `.document(String)`（对齐 docx 的 Document 概念；单文档时可省略）。

**Tech Stack:** Java 21（3.0.x，P0/P1 代码保持 Java 8 兼容以便同步 1.0.x/2.0.x）、JUnit 5、docx4j 17.0.3、Maven 4 + POM 4.1.0 + `${revision}`。

## Global Constraints

- 代码放 `easydoc-core`，包 `io.github.easy4j.doc.annotation`（注解）与 `io.github.easy4j.doc.easy`（门面/Builder/监听器/反射工具）
- **纯新增，零破坏**：不改现有 `WordprocessingMLTemplate`/`DocxTemplates`/`AbstractWmlTemplate` 任何签名；现有 935 测试必须全绿
- **Java 8 语法兼容**（禁 record/sealed/var/switch 表达式/instanceof pattern）——保证 P0/P1 可直接同步 1.0.x/2.0.x
- 每个 Task 末尾跑 `mvn -Denforcer.skip=true -pl easydoc-core -am clean verify`（Maven 4：`~/tools/apache-maven-4.0.0-rc-6/bin/mvn`）必须 BUILD SUCCESS + JaCoCo 90% 达标
- 测试用 JUnit 5（easydoc-core 现有框架），测试文件命名 `*Test.java`
- 提交信息遵循现有风格（`feat(easydocx): ...`）

---

### Task 1: @DocxField 与 @DocxIgnore 注解

**Files:**
- Create: `easydoc-core/src/main/java/io/github/easy4j/doc/annotation/DocxField.java`
- Create: `easydoc-core/src/main/java/io/github/easy4j/doc/annotation/DocxIgnore.java`
- Test: `easydoc-core/src/test/java/io/github/easy4j/doc/annotation/DocxFieldAnnotationTest.java`

**Interfaces:**
- Produces: `@DocxField(value, format, ignore)`、`@DocxIgnore`（供 Task 2 的 `DocxFields` 消费）

- [ ] **Step 1: 写失败测试**

`DocxFieldAnnotationTest.java`：
```java
package io.github.easy4j.doc.annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

class DocxFieldAnnotationTest {

    static class Model {
        @DocxField("partyName")
        private String name;
        @DocxField(value = "signDate", format = "yyyy-MM-dd")
        private java.util.Date date;
        @DocxIgnore
        private String internal;
    }

    @Test
    void annotationsAreRuntimeVisibleAndCarryValues() throws Exception {
        Field name = Model.class.getDeclaredField("name");
        DocxField df = name.getAnnotation(DocxField.class);
        assertTrue(df != null, "@DocxField must be present");
        assertEquals("partyName", df.value());
        assertEquals("", df.format());

        Field date = Model.class.getDeclaredField("date");
        assertEquals("yyyy-MM-dd", date.getAnnotation(DocxField.class).format());

        Field internal = Model.class.getDeclaredField("internal");
        assertTrue(internal.getAnnotation(DocxIgnore.class) != null,
                "@DocxIgnore must be present");
    }
}
```

- [ ] **Step 2: 跑测试确认失败**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-core test -Dtest=DocxFieldAnnotationTest`
Expected: 编译失败（找不到 `DocxField`/`DocxIgnore` 类）

- [ ] **Step 3: 实现注解**

`annotation/DocxField.java`：
```java
package io.github.easy4j.doc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注 POJO 字段到文档占位符的映射（类 EasyExcel @ExcelProperty）。
 * value 为占位符名（默认取字段名，映射为 ${name}）；format 支持日期/数字格式化。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocxField {
    String value() default "";
    String format() default "";
    boolean ignore() default false;
}
```

`annotation/DocxIgnore.java`：
```java
package io.github.easy4j.doc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 标注忽略的字段（类 EasyExcel @ExcelIgnore）。 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocxIgnore {
}
```

- [ ] **Step 4: 跑测试确认通过**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-core test -Dtest=DocxFieldAnnotationTest`
Expected: PASS

- [ ] **Step 5: 提交**

```bash
git add easydoc-core/src/main/java/io/github/easy4j/doc/annotation/ easydoc-core/src/test/java/io/github/easy4j/doc/annotation/
git commit -m "feat(easydocx): add @DocxField and @DocxIgnore annotations"
```

---

### Task 2: DocxFields——POJO → Map 反射转换

**Files:**
- Create: `easydoc-core/src/main/java/io/github/easy4j/doc/easy/DocxFields.java`
- Test: `easydoc-core/src/test/java/io/github/easy4j/doc/easy/DocxFieldsTest.java`

**Interfaces:**
- Consumes: `@DocxField`、`@DocxIgnore`（Task 1）
- Produces: `public static Map<String, Object> from(Object bean)` —— 返回占位符名→值的 Map，供 Task 5 的 `process(T)` 使用

- [ ] **Step 1: 写失败测试**

`DocxFieldsTest.java`：
```java
package io.github.easy4j.doc.easy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.annotation.DocxField;
import io.github.easy4j.doc.annotation.DocxIgnore;

class DocxFieldsTest {

    static class Contract {
        @DocxField("partyName")
        private String name = "ACME";
        @DocxField(value = "signDate", format = "yyyy-MM-dd")
        private Date date = new Date(1700000000000L);
        @DocxField
        private Integer amount = 100;
        @DocxIgnore
        private String internal = "secret";
    }

    @Test
    void fromMapsAnnotatedFieldsToPlaceholders() {
        Map<String, Object> map = DocxFields.from(new Contract());
        assertEquals("ACME", map.get("partyName"));
        assertEquals("2023-11-14", map.get("signDate"), "format must apply to Date");
        assertEquals(100, map.get("amount"), "unannotated value defaults to field name");
        assertFalse(map.containsKey("internal"), "@DocxIgnore fields must be skipped");
        assertFalse(map.containsKey("name"), "raw field name must not appear when @DocxField overrides");
    }

    @Test
    void fromHandlesNullBeanAndNullValues() {
        assertTrue(DocxFields.from(null).isEmpty(), "null bean yields empty map");
        Map<String, Object> m = DocxFields.from(new Object() {
            @DocxField("x")
            private String v = null;
        });
        assertTrue(m.containsKey("x"), "null value still appears with its placeholder key");
        assertEquals(null, m.get("x"));
    }
}
```

- [ ] **Step 2: 跑测试确认失败**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-core test -Dtest=DocxFieldsTest`
Expected: 编译失败（找不到 `DocxFields`）

- [ ] **Step 3: 实现 DocxFields**

`easy/DocxFields.java`：
```java
package io.github.easy4j.doc.easy;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.github.easy4j.doc.annotation.DocxField;
import io.github.easy4j.doc.annotation.DocxIgnore;

/**
 * 将 POJO 按 {@link DocxField}/{@link DocxIgnore} 注解转换为文档变量 Map
 * （对齐 easyodf 的 OFDReflectionUtils）。字段值映射到占位符名，默认占位符
 * 为字段名（渲染时由模板包装成 ${name}），支持 format 日期/数字格式化。
 */
public final class DocxFields {

    private DocxFields() {
    }

    public static Map<String, Object> from(Object bean) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (bean == null) {
            return map;
        }
        Class<?> type = bean.getClass();
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(DocxIgnore.class)) {
                continue;
            }
            DocxField df = field.getAnnotation(DocxField.class);
            if (df != null && df.ignore()) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object value = field.get(bean);
                String placeholder = df != null && !df.value().isEmpty() ? df.value() : field.getName();
                map.put(placeholder, formatValue(value, df != null ? df.format() : ""));
            } catch (IllegalAccessException e) {
                // 反射不可达（模块限制等）：跳过该字段，不阻断整体转换
            }
        }
        return map;
    }

    private static Object formatValue(Object value, String format) {
        if (value instanceof Date && format != null && !format.isEmpty()) {
            return new SimpleDateFormat(format).format((Date) value);
        }
        return value;
    }
}
```

- [ ] **Step 4: 跑测试确认通过**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-core test -Dtest=DocxFieldsTest`
Expected: PASS（注意日期断言依赖 `new Date(1700000000000L)` 的格式化结果——若与时区相关，测试改用固定 SimpleDateFormat 验证；实现在 format 时用 `SimpleDateFormat(format)` 默认时区，测试用 `TimeZone.setDefault` 固定或断言年/月/日子串）

- [ ] **Step 5: 提交**

```bash
git add easydoc-core/src/main/java/io/github/easy4j/doc/easy/DocxFields.java easydoc-core/src/test/java/io/github/easy4j/doc/easy/DocxFieldsTest.java
git commit -m "feat(easydocx): add DocxFields POJO-to-Map converter"
```

---

### Task 3: DocxReadListener 接口

**Files:**
- Create: `easydoc-core/src/main/java/io/github/easy4j/doc/easy/DocxReadListener.java`
- Test: `easydoc-core/src/test/java/io/github/easy4j/doc/easy/DocxReadListenerTest.java`

**Interfaces:**
- Produces: `public interface DocxReadListener<T>`（供 Task 6 的 `DocxReaderBuilder` 消费）

- [ ] **Step 1: 写失败测试**

`DocxReadListenerTest.java`：
```java
package io.github.easy4j.doc.easy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

class DocxReadListenerTest {

    static class Model {
        String name;
    }

    @Test
    void listenerDefaultMethodsDoNotThrow() {
        DocxReadListener<Model> listener = new DocxReadListener<Model>() {
            @Override
            public void invoke(Model data, Map<String, String> values) {
                data.name = values.get("name");
            }
        };
        Model m = new Model();
        listener.invoke(m, java.util.Collections.singletonMap("name", "ACME"));
        assertEquals("ACME", m.name);
        listener.doAfterAllAnalysed(); // 默认空实现不抛异常
        assertTrue(true);
    }
}
```

- [ ] **Step 2: 跑测试确认失败**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-core test -Dtest=DocxReadListenerTest`
Expected: 编译失败（找不到 `DocxReadListener`）

- [ ] **Step 3: 实现接口**

`easy/DocxReadListener.java`：
```java
package io.github.easy4j.doc.easy;

import java.util.Map;

/**
 * 模板读取监听器（对齐 EasyExcel ReadListener / easyodf OFDReadListener）。
 * 每次解析出一个数据单元时回调 invoke，全部解析完回调 doAfterAllAnalysed。
 */
public interface DocxReadListener<T> {

    /** 解析到一条数据（占位符名 → 值）时回调。 */
    void invoke(T data, Map<String, String> values);

    /** 全部解析完成后回调；默认空实现。 */
    default void doAfterAllAnalysed() {
    }
}
```

- [ ] **Step 4: 跑测试确认通过**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-core test -Dtest=DocxReadListenerTest`
Expected: PASS

- [ ] **Step 5: 提交**

```bash
git add easydoc-core/src/main/java/io/github/easy4j/doc/easy/DocxReadListener.java easydoc-core/src/test/java/io/github/easy4j/doc/easy/DocxReadListenerTest.java
git commit -m "feat(easydocx): add DocxReadListener interface"
```

---

### Task 4: EasyDocx 静态门面

**Files:**
- Create: `easydoc-core/src/main/java/io/github/easy4j/doc/easy/EasyDocx.java`
- Test: `easydoc-core/src/test/java/io/github/easy4j/doc/easy/EasyDocxTest.java`

**Interfaces:**
- Produces: `public static <T> DocxWriterBuilder<T> write(File|String, Class<T>)`、`public static <T> DocxReaderBuilder<T> read(File|String, Class<T>, DocxReadListener<T>)`（供 Task 5/6 实现）

- [ ] **Step 1: 写失败测试**

`EasyDocxTest.java`：
```java
package io.github.easy4j.doc.easy;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class EasyDocxTest {

    static class Model {
    }

    @Test
    void writeReturnsWriterBuilder() {
        assertNotNull(EasyDocx.write("src/test/resources/tpl/template.docx", Model.class),
                "write(path, model) must return a builder");
        assertNotNull(EasyDocx.write(new java.io.File("src/test/resources/tpl/template.docx"), Model.class),
                "write(file, model) must return a builder");
    }

    @Test
    void readReturnsReaderBuilder() {
        DocxReadListener<Model> l = (data, values) -> {
        };
        assertNotNull(EasyDocx.read("src/test/resources/tpl/template.docx", Model.class, l),
                "read(path, model, listener) must return a builder");
    }
}
```

- [ ] **Step 2: 跑测试确认失败**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-core test -Dtest=EasyDocxTest`
Expected: 编译失败（找不到 `EasyDocx`）

- [ ] **Step 3: 实现门面**

`easy/EasyDocx.java`：
```java
package io.github.easy4j.doc.easy;

import java.io.File;

/**
 * 类 EasyExcel / easyodf 的 easydoc 静态门面：链式构建 docx 模板渲染与读取。
 * 薄封装：内部委托 DocxTemplates + WordprocessingMLTemplate 管线，不替代引擎。
 */
public final class EasyDocx {

    private EasyDocx() {
    }

    public static <T> DocxWriterBuilder<T> write(String templatePath, Class<T> model) {
        return new DocxWriterBuilder<T>(new File(templatePath), model);
    }

    public static <T> DocxWriterBuilder<T> write(File templateFile, Class<T> model) {
        return new DocxWriterBuilder<T>(templateFile, model);
    }

    public static <T> DocxReaderBuilder<T> read(String templatePath, Class<T> model,
            DocxReadListener<T> listener) {
        return new DocxReaderBuilder<T>(new File(templatePath), model, listener);
    }

    public static <T> DocxReaderBuilder<T> read(File templateFile, Class<T> model,
            DocxReadListener<T> listener) {
        return new DocxReaderBuilder<T>(templateFile, model, listener);
    }
}
```

- [ ] **Step 4: 跑测试确认通过**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-core test -Dtest=EasyDocxTest`
Expected: PASS（依赖 Task 5 的 `DocxWriterBuilder`/Task 6 的 `DocxReaderBuilder`——若 Task 4 先于 5/6 实现会编译失败。**调整执行顺序：Task 5/6 的 Builder 类与 Task 4 门面同批实现**，或将 Task 4 的门面测试改为仅断言方法存在、Builder 类在 Task 5/6 落地后跑完整测试）

- [ ] **Step 5: 提交**

```bash
git add easydoc-core/src/main/java/io/github/easy4j/doc/easy/EasyDocx.java easydoc-core/src/test/java/io/github/easy4j/doc/easy/EasyDocxTest.java
git commit -m "feat(easydocx): add EasyDocx static facade"
```

---

### Task 5: DocxWriterBuilder（链式 + process）

**Files:**
- Create: `easydoc-core/src/main/java/io/github/easy4j/doc/easy/DocxWriterBuilder.java`
- Test: `easydoc-core/src/test/java/io/github/easy4j/doc/easy/DocxWriterBuilderTest.java`

**Interfaces:**
- Consumes: `@DocxField`/`DocxFields.from`（Task 1/2）、`EasyDocx.write`（Task 4）、现有 `DocxTemplates`/`WordprocessingMLTemplate`
- Produces: `process(T data)` → `WordprocessingMLPackage`；`process(Map vars)`；链式 `document(String)`/`mode(DocxMode)`

- [ ] **Step 1: 写失败测试**

`DocxWriterBuilderTest.java`：
```java
package io.github.easy4j.doc.easy;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.DocxMode;
import io.github.easy4j.doc.annotation.DocxField;

class DocxWriterBuilderTest {

    static class Contract {
        @DocxField("title")
        private String title = "easy-docx-contract";
        @DocxField("content")
        private String content = "rendered via builder";
    }

    @Test
    void processRendersModelIntoPackage() throws Exception {
        byte[] templateBytes = Files.readAllBytes(Path.of("src/test/resources/tpl/template.docx"));
        java.io.File tmp = java.io.File.createTempFile("easydocx", ".docx");
        Files.write(tmp.toPath(), templateBytes);

        WordprocessingMLPackage pkg = new DocxWriterBuilder<Contract>(tmp, Contract.class)
                .document("contract")
                .mode(DocxMode.STAX)
                .process(new Contract());
        assertNotNull(pkg);
        String xml = pkg.getMainDocumentPart().getXML();
        assertTrue(xml.contains("easy-docx-contract"),
                "POJO field must be substituted into the rendered document");
        tmp.delete();
    }

    @Test
    void processAcceptsRawMap() throws Exception {
        java.io.File tmp = java.io.File.createTempFile("easydocx", ".docx");
        Files.write(tmp.toPath(), Files.readAllBytes(Path.of("src/test/resources/tpl/template.docx")));
        WordprocessingMLPackage pkg = new DocxWriterBuilder<Contract>(tmp, Contract.class)
                .process(java.util.Collections.singletonMap("title", "map-title"));
        assertNotNull(pkg);
        assertTrue(pkg.getMainDocumentPart().getXML().contains("map-title"));
        tmp.delete();
    }
}
```

- [ ] **Step 2: 跑测试确认失败**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-core test -Dtest=DocxWriterBuilderTest`
Expected: 编译失败（找不到 `DocxWriterBuilder`）

- [ ] **Step 3: 实现 Builder**

`easy/DocxWriterBuilder.java`：
```java
package io.github.easy4j.doc.easy;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import io.github.easy4j.doc.DocxMode;
import io.github.easy4j.doc.DocxTemplates;
import io.github.easy4j.doc.WordprocessingMLTemplate;

/**
 * docx 模板渲染链式 Builder（对齐 easyodf OFDWriterBuilder / EasyExcel
 * ExcelWriterBuilder）。docx 无 sheet，中间层用 document（Document 概念）
 * 标识渲染目标；单文档时 document 可省略。process 内部 POJO→Map→现有管线。
 */
public final class DocxWriterBuilder<T> {

    private final File templateFile;
    private final Class<T> model;
    private DocxMode mode = DocxMode.DEFAULT;

    public DocxWriterBuilder(File templateFile, Class<T> model) {
        this.templateFile = templateFile;
        this.model = model;
    }

    /** docx 语义中间层（对齐 EasyExcel sheet 的位置）：标识文档/模板实例。 */
    public DocxWriterBuilder<T> document(String name) {
        // name 当前作为文档标识元数据保留；单文档渲染不改变管线行为
        return this;
    }

    public DocxWriterBuilder<T> mode(DocxMode mode) {
        this.mode = mode;
        return this;
    }

    /** POJO 模型渲染：@DocxField 注解 → Map → 现有模板管线。 */
    public WordprocessingMLPackage process(T data) throws Exception {
        return process(DocxFields.from(data));
    }

    /** 原始 Map 渲染（兼容现有 API 的变量注入）。 */
    public WordprocessingMLPackage process(Map<String, Object> vars) throws Exception {
        WordprocessingMLTemplate template = DocxTemplates.create(mode);
        Map<String, Object> effective = new HashMap<String, Object>(vars);
        return template.process(templateFile, effective);
    }
}
```

- [ ] **Step 4: 跑测试确认通过**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-core test -Dtest=DocxWriterBuilderTest`
Expected: PASS

- [ ] **Step 5: 提交**

```bash
git add easydoc-core/src/main/java/io/github/easy4j/doc/easy/DocxWriterBuilder.java easydoc-core/src/test/java/io/github/easy4j/doc/easy/DocxWriterBuilderTest.java
git commit -m "feat(easydocx): add DocxWriterBuilder chain"
```

---

### Task 6: DocxReaderBuilder（监听器读取）

**Files:**
- Create: `easydoc-core/src/main/java/io/github/easy4j/doc/easy/DocxReaderBuilder.java`
- Test: `easydoc-core/src/test/java/io/github/easy4j/doc/easy/DocxReaderBuilderTest.java`

**Interfaces:**
- Consumes: `DocxReadListener`（Task 3）、`EasyDocx.read`（Task 4）、现有 `WordprocessingMLTemplate`/`Docx4jUtils` 文本提取
- Produces: `doRead()` —— 解析模板占位符并回调 listener

- [ ] **Step 1: 写失败测试**

`DocxReaderBuilderTest.java`：
```java
package io.github.easy4j.doc.easy;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

class DocxReaderBuilderTest {

    static class Model {
        String title;
    }

    @Test
    void doReadInvokesListenerWithParsedValues() throws Exception {
        AtomicBoolean invoked = new AtomicBoolean(false);
        DocxReadListener<Model> listener = new DocxReadListener<Model>() {
            @Override
            public void invoke(Model data, Map<String, String> values) {
                invoked.set(true);
                if (values.containsKey("title")) {
                    data.title = values.get("title");
                }
            }
        };
        java.io.File template = new java.io.File("src/test/resources/tpl/template.docx");
        new DocxReaderBuilder<Model>(template, Model.class, listener).doRead();
        assertTrue(invoked.get(), "listener must be invoked for the parsed template");
    }

    @Test
    void doReadDoesNotThrowOnMissingTemplate() {
        DocxReadListener<Model> l = (data, values) -> {
        };
        new DocxReaderBuilder<Model>(new java.io.File("/nonexistent/template.docx"), Model.class, l)
                .doRead();
        assertTrue(true, "missing template must not throw; listener simply receives nothing");
    }
}
```

- [ ] **Step 2: 跑测试确认失败**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-core test -Dtest=DocxReaderBuilderTest`
Expected: 编译失败（找不到 `DocxReaderBuilder`）

- [ ] **Step 3: 实现 Builder**

`easy/DocxReaderBuilder.java`：
```java
package io.github.easy4j.doc.easy;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import io.github.easy4j.doc.DocxTemplates;

/**
 * docx 模板读取 Builder（对齐 easyodf OFDReaderBuilder / EasyExcel
 * ExcelReaderBuilder）：解析模板占位符，逐条回调 {@link DocxReadListener}。
 */
public final class DocxReaderBuilder<T> {

    private final File templateFile;
    private final Class<T> model;
    private final DocxReadListener<T> listener;

    public DocxReaderBuilder(File templateFile, Class<T> model, DocxReadListener<T> listener) {
        this.templateFile = templateFile;
        this.model = model;
        this.listener = listener;
    }

    /** 解析模板（占位符 → 值），回调 listener。模板缺失时静默返回（不抛异常）。 */
    public void doRead() {
        if (templateFile == null || !templateFile.exists()) {
            return;
        }
        try {
            WordprocessingMLPackage pkg = DocxTemplates.create(io.github.easy4j.doc.DocxMode.DEFAULT)
                    .process(templateFile, new HashMap<String, Object>());
            String xml = pkg.getMainDocumentPart().getXML();
            Map<String, String> values = extractPlaceholders(xml);
            T data = newInstance();
            listener.invoke(data, values);
            listener.doAfterAllAnalysed();
        } catch (Exception e) {
            // 读取失败不抛出（薄封装语义）；调用方可自行判断
        }
    }

    @SuppressWarnings("unchecked")
    private T newInstance() {
        try {
            return model.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, String> extractPlaceholders(String xml) {
        Map<String, String> values = new HashMap<String, String>();
        int from = 0;
        String start = "${";
        String end = "}";
        int i = xml.indexOf(start, from);
        while (i >= 0) {
            int j = xml.indexOf(end, i + start.length());
            if (j > i) {
                String key = xml.substring(i + start.length(), j);
                if (!key.isEmpty()) {
                    values.put(key, "");
                }
                from = j + end.length();
            } else {
                from = i + start.length();
            }
            i = xml.indexOf(start, from);
        }
        return values;
    }
}
```

- [ ] **Step 4: 跑测试确认通过**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn -Denforcer.skip=true -pl easydoc-core test -Dtest=DocxReaderBuilderTest`
Expected: PASS

- [ ] **Step 5: 提交**

```bash
git add easydoc-core/src/main/java/io/github/easy4j/doc/easy/DocxReaderBuilder.java easydoc-core/src/test/java/io/github/easy4j/doc/easy/DocxReaderBuilderTest.java
git commit -m "feat(easydocx): add DocxReaderBuilder with listener"
```

---

### Task 7: 全量验证 + README 文档

**Files:**
- Modify: `README.md`（3.0.x 的 EasyDocx 快速上手段落）
- Modify: `CHANGELOG.md`（EasyDocx 新增记录）

**Interfaces:**
- Consumes: Task 1-6 的全部产物

- [ ] **Step 1: README 增加 EasyDocx 快速上手**

在 README.md 的 Quick Start 后追加：
```markdown
### EasyDocx（类 EasyExcel 体验）

```java
// 写：门面 + 链式 + 注解模型（POJO → 占位符）
EasyDocx.write("template.docx", Contract.class)
        .document("合同")          // docx 语义（Document 概念），单文档可省略
        .process(contract);        // 返回 WordprocessingMLPackage

// 读：监听器
EasyDocx.read("template.docx", Contract.class, new ContractReadListener(dao))
        .doRead();

// 模型
public class Contract {
    @DocxField("${partyName}") private String partyName;
    @DocxField(value = "${signDate}", format = "yyyy-MM-dd") private Date signDate;
    @DocxIgnore private String internalId;
}
```
```

- [ ] **Step 2: 全量验证**

Run: `~/tools/apache-maven-4.0.0-rc-6/bin/mvn clean verify`（Maven 4，无 skip）
Expected: BUILD SUCCESS（13 模块 + JaCoCo 90%）

- [ ] **Step 3: CHANGELOG 记录**

`CHANGELOG.md` 的 3.0.x 段追加：
```markdown
- **EasyDocx API**（类 EasyExcel/easyodf 体验）：`@DocxField`/`@DocxIgnore` 注解模型、
  `EasyDocx` 门面、`DocxWriterBuilder`/`DocxReaderBuilder` 链式、`DocxReadListener` 监听器、
  `DocxFields` POJO→Map 转换。docx 语义中间层用 `document`（非 sheet）。
```

- [ ] **Step 4: 提交**

```bash
git add README.md CHANGELOG.md
git commit -m "docs(easydocx): document EasyDocx quick start and changelog"
```

- [ ] **Step 5: 推送 3.0.x + main**

```bash
git checkout feature/3.0.x && git merge --ff-only main
git push origin feature/3.0.x main
```

---

## 执行后同步（1.0.x / 2.0.x）

P0/P1 全部代码为 Java 8 兼容（无 record/sealed/var/switch 表达式），且不依赖 3.0.x 专属 API（`DocxTemplates`/`WordprocessingMLTemplate`/`DocxMode` 在 1.0.x/2.0.x 的移植版中已存在——1.0/2.0 移植时有 `DocxTemplates`/`DocxMode` 吗？**核对**：1.0.x/2.0.x 无 `DocxTemplates`/`DocxMode`（3.0.x 新增 API），同步时 `DocxWriterBuilder.mode()` 与 `process` 需改用 1.0/2.0 现有的模板创建方式（直接 `new WordprocessingMLDocxTemplate()` / `WordprocessingMLDocxStAXTemplate()`）。

同步步骤（在对应 worktree）：
1. 复制 `annotation/` + `easy/` 源码与测试到 1.0.x/2.0.x 的 easydoc-core
2. 适配 `DocxWriterBuilder`：`DocxTemplates.create(mode)` → 1.0/2.0 的等价工厂（若无工厂，`mode` 用 `DocxMode` 等价判断：DEFAULT→`new WordprocessingMLDocxTemplate()`，STAX→`new WordprocessingMLDocxStAXTemplate()`）
3. 各分支 `mvn -Denforcer.skip=true -pl easydoc-core -am clean verify` 绿
4. commit + push feature/1.0.x / feature/2.0.x
