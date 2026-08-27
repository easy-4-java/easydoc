package io.github.easy4j.doc.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for VariableReplaceSAXHandler.
 *
 * The SAXHandler superclass constructor can fail depending on the JDK/JAXP
 * implementation because the built-in Transformer does not call setContentHandler
 * on the provided XMLReader. We use {@code Unsafe.allocateInstance} to bypass the
 * constructor and then set fields / call methods via reflection so that the
 * replace logic and characters() paths are exercised.
 *
 * <p>未闭合占位符守护与多字符占位符支持为 3.0.x ededaf3 的移植内容。</p>
 */
@DisplayName("VariableReplaceSAXHandler Tests")
class VariableReplaceSAXHandlerTest {

    // ---------------------------------------------------------------
    // Helper: allocate instance without calling any constructor
    // ---------------------------------------------------------------
    private static VariableReplaceSAXHandler allocateHandler() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        sun.misc.Unsafe unsafe = (sun.misc.Unsafe) f.get(null);
        return (VariableReplaceSAXHandler) unsafe.allocateInstance(VariableReplaceSAXHandler.class);
    }

    /**
     * Set a declared field on the handler or any superclass.
     */
    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = null;
        Class<?> clz = target.getClass();
        while (clz != null) {
            try {
                field = clz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                clz = clz.getSuperclass();
            }
        }
        if (field == null) throw new NoSuchFieldException(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * Build a fully initialised handler (fields set + initContext called +
     * SAXHandler.ch set to a no-op ContentHandler).
     */
    private static VariableReplaceSAXHandler buildHandler(Map<String, Object> vars) throws Exception {
        return buildHandler(vars, "${", "}");
    }

    private static VariableReplaceSAXHandler buildHandler(Map<String, Object> vars,
            String placeholderStart, String placeholderEnd) throws Exception {
        VariableReplaceSAXHandler handler = allocateHandler();

        // Set instance fields that would normally be set by the constructor
        setField(handler, "placeholderStart", placeholderStart);
        setField(handler, "placeholderEnd", placeholderEnd);
        setField(handler, "variables", vars);

        // Call initContext() to set up the OGNL context
        Method initContext = VariableReplaceSAXHandler.class.getDeclaredMethod("initContext");
        initContext.setAccessible(true);
        initContext.invoke(handler);

        // Set the private 'ch' (ContentHandler) field on SAXHandler so that
        // getContentHandler() returns a non-null handler.
        setField(handler, "ch", new DefaultHandler());

        return handler;
    }

    // ---------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------

    @Test
    void charactersWithPlainTextNoPlaceholders() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "World");
        VariableReplaceSAXHandler handler = buildHandler(vars);

        char[] input = "plain text".toCharArray();
        // Should not throw; exercises the replace() base case (no placeholder found)
        handler.characters(input, 0, input.length);
    }

    @Test
    void charactersWithSimpleVariableReplacement() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "World");
        VariableReplaceSAXHandler handler = buildHandler(vars);

        char[] input = "Hello ${name}!".toCharArray();
        handler.characters(input, 0, input.length);
        // Exercises: characters() -> replace() with val != null branch
    }

    @Test
    void charactersWithMultiplePlaceholders() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        vars.put("firstName", "John");
        vars.put("lastName", "Doe");
        VariableReplaceSAXHandler handler = buildHandler(vars);

        char[] input = "${firstName} ${lastName}".toCharArray();
        handler.characters(input, 0, input.length);
        // Exercises recursive replace with multiple placeholders
    }

    @Test
    void charactersWithPartialPlaceholderText() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        VariableReplaceSAXHandler handler = buildHandler(vars);

        // 未闭合占位符（无结束 '}'）：修复后不应抛 StringIndexOutOfBoundsException，
        // 而是保留字面文本并继续处理（宽松容错语义）
        char[] input = "text ${incomplete".toCharArray();
        assertDoesNotThrow(() -> handler.characters(input, 0, input.length));
    }

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
        VariableReplaceSAXHandler handler =
                buildHandler(vars, "{{!", "}}");
        CapturingContentHandler capture = new CapturingContentHandler();
        setField(handler, "ch", capture);

        char[] input = "你好 {{!name}} ~ {{!missing}} !".toCharArray();
        handler.characters(input, 0, input.length);

        // 已闭合部分正常替换；未闭合场景不触发；缺失变量回退 OGNL 为空 → 原样保留 key
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
