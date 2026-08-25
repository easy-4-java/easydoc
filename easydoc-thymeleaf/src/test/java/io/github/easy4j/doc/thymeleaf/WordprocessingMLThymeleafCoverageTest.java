package io.github.easy4j.doc.thymeleaf;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.thymeleaf.TemplateEngine;

/**
 * Coverage tests for EngineFactory DCL short-circuit and Renderer statelessness.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("WordprocessingMLThymeleafTemplate Coverage Tests")
class WordprocessingMLThymeleafCoverageTest {

    @Test
    @DisplayName("EngineFactory.get() returns same cached instance on consecutive calls")
    void engineFactoryReturnsSameInstanceOnConsecutiveCalls() throws Exception {
        EngineFactory factory = new EngineFactory(null);
        TemplateEngine first = factory.get();
        assertNotNull(first);
        TemplateEngine second = factory.get();
        assertSame(first, second, "EngineFactory.get() must return the same cached instance");
    }

    @Test
    @DisplayName("Renderer has no mutable instance fields (stateless)")
    void rendererIsStateless() {
        Field[] fields = Renderer.class.getDeclaredFields();
        for (Field f : fields) {
            if (!Modifier.isStatic(f.getModifiers())) {
                assertTrue(Modifier.isFinal(f.getModifiers()),
                        "Renderer field '" + f.getName() + "' must be final to guarantee statelessness");
            }
        }
    }
}
