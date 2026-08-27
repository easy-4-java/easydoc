package io.github.easy4j.doc.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Covers the OGNL-evaluation guard paths of both replace() implementations
 * that the happy-path tests miss:
 *
 * <ul>
 *   <li>unresolved variable + strict mode → IllegalStateException passthrough
 *       (rethrown unwrapped by the catch block)</li>
 *   <li>OGNL evaluation failure + lenient mode → key appended verbatim</li>
 *   <li>OGNL evaluation failure + strict mode → wrapped IllegalStateException</li>
 * </ul>
 *
 * For {@link VariableReplaceSAXHandler} the instance is allocated without its
 * constructor: on JDK 21+ docx4j's SAXHandler superclass cannot be constructed
 * ("Transformer didn't set ContentHandler"), the same constraint the sibling
 * VariableReplaceSAXHandlerTest works around with Unsafe.allocateInstance.
 */
@DisplayName("replace() unresolved/failed evaluation guards")
class VariableReplaceGuardsPathTest {

    private static final String STRICT_FLAG = "easydoc.variable.strict";

    @BeforeEach
    void clearFlag() {
        System.clearProperty(STRICT_FLAG);
    }

    @AfterEach
    void clearFlagAgain() {
        System.clearProperty(STRICT_FLAG);
    }

    // ---------------------------------------------------------------
    // SaTX handler — directly constructible on JDK 21
    // ---------------------------------------------------------------

    @Test
    @DisplayName("SaTX: OGNL eval failure in lenient mode appends key verbatim")
    void satxEvalFailureLenientAppendsKeyVerbatim() throws Exception {
        VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler("${", "}", new HashMap<>());
        String out = runSatx(handler, "<root>x ${null.toString()} y</root>");
        // lenient mode appends the raw key without its placeholder delimiters
        assertEquals("x null.toString() y", out);
    }

    @Test
    @DisplayName("SaTX: OGNL eval failure in strict mode throws wrapped IllegalStateException")
    void satxEvalFailureStrictThrowsWrapped() {
        System.setProperty(STRICT_FLAG, "true");
        assertThrows(IllegalStateException.class, () -> {
            VariableReplaceSaTXHandler handler = new VariableReplaceSaTXHandler("${", "}", new HashMap<>());
            runSatx(handler, "<root>${null.toString()}</root>");
        });
    }

    private static String runSatx(VariableReplaceSaTXHandler handler, String xml) throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamReader.CHARACTERS) {
                break;
            }
        }
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
        handler.handleCharacters(reader, writer);
        writer.flush();
        return sw.toString();
    }

    // ---------------------------------------------------------------
    // SAX handler — constructor-less allocation (JDK 21 constraint)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("SAX: unresolved variable in strict mode propagates as IllegalStateException")
    void saxUnresolvedStrictPropagatesIllegalState() throws Exception {
        System.setProperty(STRICT_FLAG, "true");
        Map<String, Object> vars = new HashMap<>();
        vars.put("known", "value");
        VariableReplaceSAXHandler handler = buildSax(vars);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> driveSaxCharacters(handler, "${missing}"));
        assertTrue(ex.getMessage().contains("Unresolved template variable"),
                "the catch block must rethrow the strict-mode ISE unwrapped, was: " + ex.getMessage());
    }

    @Test
    @DisplayName("SAX: OGNL eval failure in lenient mode appends key verbatim")
    void saxEvalFailureLenientAppendsKeyVerbatim() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        VariableReplaceSAXHandler handler = buildSax(vars);

        RecordingHandler recorder = driveSaxCharacters(handler, "${null.toString()}");
        // lenient mode appends the raw key without its placeholder delimiters
        assertEquals("null.toString()", recorder.collected);
    }

    @Test
    @DisplayName("SAX: OGNL eval failure in strict mode throws wrapped IllegalStateException")
    void saxEvalFailureStrictThrowsWrapped() throws Exception {
        System.setProperty(STRICT_FLAG, "true");
        VariableReplaceSAXHandler handler = buildSax(new HashMap<>());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> driveSaxCharacters(handler, "${null.toString()}"));
        assertTrue(ex.getMessage().contains("Failed to evaluate OGNL expression"),
                "strict mode must wrap eval failures, was: " + ex.getMessage());
    }

    /** Allocates a VariableReplaceSAXHandler bypassing the JDK21-broken superclass ctor. */
    private static VariableReplaceSAXHandler buildSax(Map<String, Object> vars) throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        sun.misc.Unsafe unsafe = (sun.misc.Unsafe) f.get(null);
        VariableReplaceSAXHandler handler =
                (VariableReplaceSAXHandler) unsafe.allocateInstance(VariableReplaceSAXHandler.class);

        setField(handler, "placeholderStart", "${");
        setField(handler, "placeholderEnd", "}");
        setField(handler, "variables", vars);

        Method initContext = VariableReplaceSAXHandler.class.getDeclaredMethod("initContext");
        initContext.setAccessible(true);
        initContext.invoke(handler);

        setField(handler, "ch", new DefaultHandler());
        return handler;
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Class<?> clz = target.getClass();
        while (clz != null) {
            try {
                Field field = clz.getDeclaredField(name);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException e) {
                clz = clz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    /** ContentHandler recorder so we can assert the emitted characters. */
    private static final class RecordingHandler extends DefaultHandler {
        String collected = "";
        @Override
        public void characters(char[] ch, int start, int length) {
            collected += new String(ch, start, length);
        }
    }

    private static RecordingHandler driveSaxCharacters(VariableReplaceSAXHandler handler,
            String payload) throws Exception {
        Field chField = handler.getClass().getSuperclass().getDeclaredField("ch");
        chField.setAccessible(true);
        RecordingHandler recorder = new RecordingHandler();
        chField.set(handler, recorder);
        char[] input = payload.toCharArray();
        handler.characters(input, 0, input.length);
        return recorder;
    }
}
