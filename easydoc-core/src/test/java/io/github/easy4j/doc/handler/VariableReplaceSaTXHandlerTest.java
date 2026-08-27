package io.github.easy4j.doc.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link VariableReplaceSaTXHandler}.
 *
 * <p>未闭合占位符宽松容错与多字符占位符支持为 3.0.x ededaf3 的移植内容。</p>
 */
@DisplayName("VariableReplaceSaTXHandler Tests")
class VariableReplaceSaTXHandlerTest {

    @Test
    void constructorWithVariablesCreatesInstance() throws SAXException {
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "Alice");
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler(vars);
        assertNotNull(handler);
    }

    @Test
    void handleCharactersReplacesVariable() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "World");
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler("${", "}", vars);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader("<root>Hello ${name}!</root>"));
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamReader.CHARACTERS) {
                break;
            }
        }

        StringWriter sw = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);

        handler.handleCharacters(reader, writer);
        writer.flush();
        assertEquals("Hello World!", sw.toString());
    }

    @Test
    void handleCharactersWithOgnlExpressionReturningNonNull() throws Exception {
        // Use a nested map so OGNL can navigate "data.value" -> "hello"
        Map<String, Object> vars = new HashMap<>();
        Map<String, String> nested = new HashMap<>();
        nested.put("value", "hello");
        vars.put("data", nested);
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler("${", "}", vars);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader("<root>${data.value}</root>"));
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamReader.CHARACTERS) {
                break;
            }
        }

        StringWriter sw = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);

        handler.handleCharacters(reader, writer);
        writer.flush();
        assertEquals("hello", sw.toString());
    }

    @Test
    void handleCharactersWithMissingClosingBrace() throws Exception {
        // A placeholder without closing '}' exercises the else branch when keyEnd <= 0
        Map<String, Object> vars = new HashMap<>();
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler("${", "}", vars);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader("<root>${incomplete</root>"));
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamReader.CHARACTERS) {
                break;
            }
        }

        StringWriter sw = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);

        handler.handleCharacters(reader, writer);
        writer.flush();
        // 未闭合占位符走宽松容错分支：'$' 前缀原样保留，剩余文本继续输出，不抛异常
        assertEquals("${incomplete", sw.toString());
    }

    @Test
    void handleCharactersWithLongCustomPlaceholderPrefix() throws Exception {
        // 多字符占位符前缀（长度 3）：key 切片必须基于前缀长度而非硬编码偏移量
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "张三");
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler("#{$", "}}", vars);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(
                new StringReader("<root>A #{$name}} B</root>"));
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamReader.CHARACTERS) {
                break;
            }
        }

        StringWriter sw = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);

        handler.handleCharacters(reader, writer);
        writer.flush();
        // "#{$name}}"（前缀 3 字符、结束符 "}}"）整体作为占位符消费
        assertEquals("A 张三 B", sw.toString());
    }

    @Test
    void handleCharactersWithUnterminatedCustomPrefixDoesNotThrow() throws Exception {
        // 多字符前缀未闭合：不抛异常（严格来说前缀首字符被保留、其余文本原样跟随）
        Map<String, Object> vars = new HashMap<>();
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler("#{$", "}}", vars);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader("<root>x #{oops</root>"));
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamReader.CHARACTERS) {
                break;
            }
        }

        StringWriter sw = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);

        assertDoesNotThrow(() -> {
            try {
                handler.handleCharacters(reader, writer);
                writer.flush();
            } catch (javax.xml.stream.XMLStreamException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
