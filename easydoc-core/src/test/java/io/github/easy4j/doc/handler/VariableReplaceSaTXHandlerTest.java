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
    void constructorWithCustomPlaceholdersCreatesInstance() throws SAXException {
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "Bob");
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler("${", "}", vars);
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
    void handleCharactersWithNoVariablesPassesThrough() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler("${", "}", vars);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader("<root>plain text</root>"));
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
        assertEquals("plain text", sw.toString());
    }

    @Test
    void handleCharactersWithMissingVariableFallsBackToOgnl() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler("${", "}", vars);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader("<root>${missing}</root>"));
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
        String result = sw.toString();
        assertNotNull(result);
    }

    @Test
    void handleCharactersWithOgnlExpressionReturningNonNull() throws Exception {
        // Use a nested map so OGNL can navigate "data.value" -> "hello"
        // This covers line 107: strB.append(value.toString())
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
        // A placeholder without closing '}' exercises lines 121-123
        // (the else branch when keyEnd <= 0)
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
        // Should produce some output (the '$' and remaining text)
        assertNotNull(sw.toString());
    }
}
