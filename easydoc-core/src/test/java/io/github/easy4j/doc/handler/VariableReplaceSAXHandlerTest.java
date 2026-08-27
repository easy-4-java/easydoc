package io.github.easy4j.doc.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Unit tests for {@link VariableReplaceSAXHandler}.
 *
 * <p>重点覆盖占位符替换的容错契约（#15）：
 * 未闭合占位符不抛异常（保留字面文本），多字符自定义前缀/结束符切片正确。</p>
 *
 * <p>实现说明：测试类路径携带 docx4j 自带的 xalan-metainf（META-INF/services 指向
 * 重打包的 org.docx4j.org.apache.xalan 工厂），该工厂的 identity transform
 * 不回调 SAXSource reader 的 {@code setContentHandler}，导致 docx4j SAXHandler
 * 构造器必然抛出 “Transformer didn't set ContentHandler”（3.0.x 在 JDK 21+
 * 遇到的是同一失败模式）。与 3.0.x 的处理一致：实例化时绕过构造器，
 * 再以反射补齐字段并调用 initContext。区别在于本分支按 release=8 编译约束，
 * 通过反射调用 {@code sun.misc.Unsafe#allocateInstance}（仅字符串/反射，无编译期依赖）。
 * 注意 @AfterAll 中恢复属性——不需要了。</p>
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("VariableReplaceSAXHandler Tests")
class VariableReplaceSAXHandlerTest {

    /**
     * 捕获 characters() 输出的 ContentHandler，用于断言替换结果。
     */
    private static class CapturingContentHandler extends DefaultHandler {
        final StringBuilder collected = new StringBuilder();

        @Override
        public void characters(char[] ch, int start, int length) {
            collected.append(ch, start, length);
        }
    }

    /** 绕过构造器分配实例（对齐 3.0.x 做法；sun.misc.Unsafe 经反射调用）。 */
    private static VariableReplaceSAXHandler allocateHandler() throws Exception {
        Field f = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Object unsafe = f.get(null);
        Method allocate = unsafe.getClass().getMethod("allocateInstance", Class.class);
        return (VariableReplaceSAXHandler) allocate.invoke(unsafe, VariableReplaceSAXHandler.class);
    }

    /** 在目标类或其父类上查找声明字段（供设置继承私有字段使用）。 */
    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = null;
        Class<?> clz = target.getClass();
        while (clz != null && field == null) {
            try {
                field = clz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clz = clz.getSuperclass();
            }
        }
        if (field == null) {
            throw new NoSuchFieldException(fieldName);
        }
        field.setAccessible(true);
        field.set(target, value);
    }

    private static VariableReplaceSAXHandler buildHandler(Map<String, Object> vars,
            String placeholderStart, String placeholderEnd) throws Exception {
        VariableReplaceSAXHandler handler = allocateHandler();
        setField(handler, "placeholderStart", placeholderStart);
        setField(handler, "placeholderEnd", placeholderEnd);
        setField(handler, "variables", vars);

        Method initContext = VariableReplaceSAXHandler.class.getDeclaredMethod("initContext");
        initContext.setAccessible(true);
        initContext.invoke(handler);

        // 私有 ch（ContentHandler）字段换为捕获器，便于断言替换后的最终输出
        setField(handler, "ch", new CapturingContentHandler());
        return handler;
    }

    private static VariableReplaceSAXHandler buildHandler(Map<String, Object> vars) throws Exception {
        return buildHandler(vars, "${", "}");
    }

    @Test
    void charactersWithPlainTextNoPlaceholders() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "World");
        VariableReplaceSAXHandler handler = buildHandler(vars);
        CapturingContentHandler capture = new CapturingContentHandler();
        setField(handler, "ch", capture);

        char[] input = "plain text".toCharArray();
        handler.characters(input, 0, input.length);

        assertEquals("plain text", capture.collected.toString());
    }

    @Test
    void charactersWithSimpleVariableReplacement() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "World");
        VariableReplaceSAXHandler handler = buildHandler(vars);
        CapturingContentHandler capture = new CapturingContentHandler();
        setField(handler, "ch", capture);

        char[] input = "Hello ${name}!".toCharArray();
        handler.characters(input, 0, input.length);

        assertEquals("Hello World!", capture.collected.toString());
    }

    @Test
    void charactersWithUnterminatedPlaceholderDoesNotThrow() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        VariableReplaceSAXHandler handler = buildHandler(vars);

        // 未闭合占位符（无结束 '}'）：修复前抛 StringIndexOutOfBoundsException，
        // 修复后保留字面文本并继续处理（宽松容错语义）
        char[] input = "text ${incomplete".toCharArray();
        assertDoesNotThrow(() -> handler.characters(input, 0, input.length));
    }

    @Test
    void charactersWithUnterminatedPlaceholderKeepsLiteralText() throws Exception {
        // 决策行为（#15）：未闭合占位符不抛异常，占位符前缀原样保留输出 + WARN 日志后继续
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "World");
        VariableReplaceSAXHandler handler = buildHandler(vars);
        CapturingContentHandler capture = new CapturingContentHandler();
        setField(handler, "ch", capture);

        char[] input = "A ${name} B ${foo".toCharArray();
        handler.characters(input, 0, input.length);

        assertEquals("A World B ${foo", capture.collected.toString());
    }

    @Test
    void charactersWithOnlyUnterminatedPlaceholderDoesNotThrow() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        VariableReplaceSAXHandler handler = buildHandler(vars);
        CapturingContentHandler capture = new CapturingContentHandler();
        setField(handler, "ch", capture);

        handler.characters("${foo".toCharArray(), 0, "${foo".length());

        assertEquals("${foo", capture.collected.toString());
    }

    @Test
    void charactersWithLongCustomPlaceholderPrefix() throws Exception {
        // 占位符前缀长度 > 2：key 切片必须基于前缀长度而非硬编码 +2
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "张三");
        VariableReplaceSAXHandler handler = buildHandler(vars, "{{!", "}}");
        CapturingContentHandler capture = new CapturingContentHandler();
        setField(handler, "ch", capture);

        char[] input = "你好 {{!name}} ~ {{!missing}} !".toCharArray();
        handler.characters(input, 0, input.length);

        // 已闭合部分正常替换；缺失变量回退 OGNL 为空 → 原样保留 key
        assertEquals("你好 张三 ~ missing !", capture.collected.toString());
    }

    @Test
    void charactersWithLongPrefixAndUnterminatedPlaceholder() throws Exception {
        // 多字符前缀 + 未闭合占位符：前缀需完整保留，且不能死循环
        Map<String, Object> vars = new HashMap<>();
        VariableReplaceSAXHandler handler = buildHandler(vars, "{{!", "}}");
        CapturingContentHandler capture = new CapturingContentHandler();
        setField(handler, "ch", capture);

        handler.characters("x {{!oops".toCharArray(), 0, "x {{!oops".length());

        assertEquals("x {{!oops", capture.collected.toString());
    }
}
