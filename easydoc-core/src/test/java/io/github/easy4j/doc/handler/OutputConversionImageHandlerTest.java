package io.github.easy4j.doc.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OutputConversionImageHandler Tests")
class OutputConversionImageHandlerTest {

    @Test
    void constructorCreatesInstance() {
        OutputConversionImageHandler handler = new OutputConversionImageHandler("/tmp/images", "images", true);
        assertNotNull(handler);
    }

    @Test
    void constructorWithNullArgsDoesNotThrow() {
        // The parent class HTMLConversionImageHandler accepts null args
        OutputConversionImageHandler handler = new OutputConversionImageHandler(null, null, false);
        assertNotNull(handler);
    }

    @Test
    void constructorWithIncludeUUIDFalse() {
        OutputConversionImageHandler handler = new OutputConversionImageHandler("/tmp/img", "img", false);
        assertNotNull(handler);
    }
}
