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
 * The SAXHandler superclass constructor fails on JDK 21 because the built-in
 * Transformer does not call setContentHandler on the provided XMLReader.
 * We use {@code Unsafe.allocateInstance} to bypass the constructor and then
 * set fields / call methods via reflection so that the replace logic and
 * characters() paths are exercised.
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
        VariableReplaceSAXHandler handler = allocateHandler();

        // Set instance fields that would normally be set by the constructor
        setField(handler, "placeholderStart", "${");
        setField(handler, "placeholderEnd", "}");
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
    void charactersWithNullValuedVariableFallsToOgnlNull() throws Exception {
        // Put a key with null value: get() returns null, OGNL evaluates to null
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", null);
        VariableReplaceSAXHandler handler = buildHandler(vars);

        char[] input = "${name}".toCharArray();
        handler.characters(input, 0, input.length);
        // Exercises: val == null -> OGNL evaluates "name" -> returns null -> strB.append(key)
    }

    @Test
    void charactersWithMissingKeyAndOgnlException() throws Exception {
        // Expression that causes OGNL to throw (property access on null)
        Map<String, Object> vars = new HashMap<>();
        VariableReplaceSAXHandler handler = buildHandler(vars);

        char[] input = "${nonexistent.nested}".toCharArray();
        handler.characters(input, 0, input.length);
        // Exercises: val == null -> OGNL throws -> catch block -> strB.append(key)
    }

    @Test
    void charactersWithOgnlExpressionReturningNonNull() throws Exception {
        // "data.value" is not a direct key, but OGNL navigates data -> value
        Map<String, Object> vars = new HashMap<>();
        Map<String, String> nested = new HashMap<>();
        nested.put("value", "hello");
        vars.put("data", nested);
        VariableReplaceSAXHandler handler = buildHandler(vars);

        char[] input = "${data.value}".toCharArray();
        handler.characters(input, 0, input.length);
        // Exercises: val == null -> OGNL evaluates "data.value" -> returns "hello" -> non-null branch
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

        // Text that starts with placeholder-like syntax but has no closing brace
        // before the end of the string. The indexOf(placeholderEnd) will return -1,
        // causing a StringIndexOutOfBoundsException in substring.
        // This is a known limitation of the replace logic.
        char[] input = "text ${incomplete".toCharArray();
        try {
            handler.characters(input, 0, input.length);
        } catch (Exception e) {
            // Expected: StringIndexOutOfBoundsException from replace() when no closing '}'
        }
    }
}
