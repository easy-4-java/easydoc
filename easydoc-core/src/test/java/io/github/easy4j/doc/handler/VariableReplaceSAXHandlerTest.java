package io.github.easy4j.doc.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
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

    /**
     * 将（继承自 SAXHandler 的）私有 ContentHandler 字段替换为捕获器，
     * 以便断言替换后的最终输出。
     */
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
        // 本分支运行环境（docx4j 8 + JDK17 测试运行时）下 SAXHandler 构造器
        // 可正常完成（Transformer 会回设 ContentHandler），无需绕过构造器；
        // 构造后仅替换私有 ch 字段为捕获器。
        VariableReplaceSAXHandler handler =
                new VariableReplaceSAXHandler(placeholderStart, placeholderEnd, vars);
        setField(handler, "ch", new CapturingContentHandler());
        return handler;
    }

    private static VariableReplaceSAXHandler buildHandler(Map<String, Object> vars) throws Exception {
        return buildHandler(vars, "${", "}");
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
