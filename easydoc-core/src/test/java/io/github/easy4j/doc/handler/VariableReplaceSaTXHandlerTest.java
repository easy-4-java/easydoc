package io.github.easy4j.doc.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link VariableReplaceSaTXHandler}.
 *
 * <p>重点覆盖占位符替换的容错契约（#15）：
 * 未闭合占位符不抛异常（宽松容错），多字符自定义前缀/结束符切片正确。</p>
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("VariableReplaceSaTXHandler Tests")
class VariableReplaceSaTXHandlerTest {

    private static XMLStreamReader firstCharactersReader(String xml) throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamReader.CHARACTERS) {
                break;
            }
        }
        return reader;
    }

    private static String handleCharacters(String xml, VariableReplaceSaTXHandler handler) throws Exception {
        XMLStreamReader reader = firstCharactersReader(xml);
        StringWriter sw = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);
        handler.handleCharacters(reader, writer);
        writer.flush();
        return sw.toString();
    }

    @Test
    void handleCharactersWithSimpleVariableReplacement() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "World");
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler(vars);

        assertEquals("Hello World!", handleCharacters("<root>Hello ${name}!</root>", handler));
    }

    @Test
    void handleCharactersWithUnterminatedPlaceholderKeepsLiteralText() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler(vars);

        // 未闭合占位符走宽松容错分支：'$' 前缀原样保留，剩余文本继续输出，不抛异常
        assertEquals("${incomplete", handleCharacters("<root>${incomplete</root>", handler));
    }

    @Test
    void handleCharactersWithLongCustomPlaceholderPrefix() throws Exception {
        // 多字符占位符前缀（长度 3）：key 切片必须基于前缀长度而非硬编码偏移量
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "张三");
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler("#{$", "}}", vars);

        // "#{$name}}"（前缀 3 字符、结束符 "}}"）整体作为占位符消费
        assertEquals("A 张三 B", handleCharacters("<root>A #{$name}} B</root>", handler));
    }

    @Test
    void handleCharactersWithUnterminatedCustomPrefixDoesNotThrow() throws Exception {
        // 多字符前缀未闭合：不抛异常（宽松容错分支保留前缀首字符并继续扫描）
        Map<String, Object> vars = new HashMap<>();
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler("#{$", "}}", vars);

        assertDoesNotThrow(() -> {
            try {
                handleCharacters("<root>x #{oops</root>", handler);
            } catch (javax.xml.stream.XMLStreamException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
