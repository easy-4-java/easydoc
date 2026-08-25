package io.github.easy4j.doc.beetl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.beetl.core.GroupTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Coverage tests for EngineFactory DCL short-circuit and Renderer statelessness.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@DisplayName("WordprocessingMLBeetlTemplate Coverage Tests")
class WordprocessingMLBeetlCoverageTest {

    /**
     * Exercises the EngineFactory DCL short-circuit: consecutive calls to
     * {@code factory.get()} must return the same instance without re-entering
     * the synchronized block.
     */
    @Test
    @DisplayName("EngineFactory.get() returns same cached instance on consecutive calls")
    void engineFactoryReturnsSameInstanceOnConsecutiveCalls() throws Exception {
        EngineFactory factory = new EngineFactory();
        GroupTemplate first = factory.get();
        assertNotNull(first);
        GroupTemplate second = factory.get();
        assertSame(first, second, "EngineFactory.get() must return the same cached instance");
    }

    /**
     * Verifies that Renderer is stateless: it has no mutable instance fields.
     */
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
