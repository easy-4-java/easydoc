package io.github.easy4j.doc.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OutputConversionHyperlinkHandler Tests")
class OutputConversionHyperlinkHandlerTest {

    @Test
    void getHyperlinkHandlerReturnsSingleton() {
        OutputConversionHyperlinkHandler a = OutputConversionHyperlinkHandler.getHyperlinkHandler();
        OutputConversionHyperlinkHandler b = OutputConversionHyperlinkHandler.getHyperlinkHandler();
        assertNotNull(a);
        assertSame(a, b);
    }

    @Test
    void handleHyperlinkDoesNotThrow() throws Exception {
        OutputConversionHyperlinkHandler handler = OutputConversionHyperlinkHandler.getHyperlinkHandler();
        // handleHyperlink is a no-op; just verify it doesn't throw
        handler.handleHyperlink(null, null, null);
    }
}
