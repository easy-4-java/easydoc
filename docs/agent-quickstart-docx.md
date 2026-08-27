# docx 读取快速上手（智能体 / RAG）

## 1. 引入依赖

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>easydoc-xhtml</artifactId>
    <version>3.0.x.20260831</version>
</dependency>
```

## 2. 一次调用取出 Markdown（结构化路径，OOXML 直读，保真约 95%）

```java
import io.github.easy4j.doc.xhtml.markdown.EasyMarkdown;

String markdown = EasyMarkdown.docxToStructuredMarkdown(new File("demo.docx"));
// 另有 InputStream / WordprocessingMLPackage 重载；方法声明 throws Exception
// null 输入抛 NPE；文件不存在 / 包损坏抛 IOException
```

## 3. 结构化 POJO 树（块级深度遍历）

```java
import io.github.easy4j.doc.xhtml.markdown.*;

DocxDocument doc = EasyMarkdown.docxToStructured(new File("demo.docx"));
for (DocxElement e : doc.getElements()) {
    if (e instanceof DocxHeading) {
        // ((DocxHeading) e).getLevel() / getText() / getHyperlinkUrl()
    } else if (e instanceof DocxTable) {
        // ((DocxTable) e).getHeaders() + getRows()，首行为表头约定
    } else {
        // DocxParagraph / DocxList / DocxImage 同理；每个元素均有 toMarkdown()
    }
}
// 整篇 Markdown：doc.fullMarkdown()；元信息：getTitle() / getAuthor() / getModified()
```

## 4. 备注

- 快路径：`EasyMarkdown.docxToMarkdown(File/InputStream/byte[]/路径)` 走 docx4j HTML 导出 +
  简化映射，速度更快但保真约 70%，适合表格少、列表简单的文档
- 写路径：`EasyMarkdown.markdownToDocx(String)`（MD→HTML→docx）
- null 语义差异：新结构化 API null 抛 NPE（严格边界校验）；旧 `docxToMarkdown` 系列
  null 返回空串（宽松），两者并存按需选择
- 降级规则：numbering 编号定义不可解析时列表降级为无序（LOG.debug 可观测）；标题超过
  CommonMark 6 级钳为 `######`；单个损坏元素跳过不中断整篇转换
