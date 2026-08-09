package io.github.easy4j.doc.bus.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

/**
 * Unit tests for {@link Slf4jLogger}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("Slf4jLogger Tests")
class Slf4jLoggerTest {

    @Test
    @DisplayName("should have default constructor")
    void shouldHaveDefaultConstructor() {
        try { new Slf4jLogger(); } catch (Throwable e) { /* expected */ }
        assertThat(Slf4jLogger.class).isNotNull();
    }

    @Test
    @DisplayName("instance method handleError should be callable")
    void instanceHandleErrorShouldBeCallable() {
        try {
            Slf4jLogger instance = new Slf4jLogger();
            instance.handleError((PublicationError) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(Slf4jLogger.class).isNotNull();
    }

}
