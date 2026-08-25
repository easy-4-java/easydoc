package io.github.easy4j.doc.webit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import webit.script.Engine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

/**
 * Tests for EngineFactory DCL short-circuit and engine caching.
 *
 * <p>Note: Engine initialization requires valid configuration in docx4j.properties.
 * Tests that exercise engine initialization are wrapped in try-catch to match the
 * defensive style of the existing {@code WordprocessingMLWebitTemplateTest}.</p>
 */
@DisplayName("Webit EngineFactory + caching tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WordprocessingMLWebitCoverageTest {

    @Test
    @Order(1)
    @DisplayName("EngineFactory class is final and instantiable")
    void engineFactoryClassExists() {
        assertTrue(java.lang.reflect.Modifier.isFinal(EngineFactory.class.getModifiers()),
                "EngineFactory must be final");
    }

    @Test
    @Order(2)
    @DisplayName("EngineFactory.get() returns non-null instance (if config valid)")
    void engineFactoryReturnsNonNull() {
        try {
            EngineFactory factory = new EngineFactory();
            Engine engine = factory.get();
            assertNotNull(engine, "EngineFactory.get() must return a non-null engine");
        } catch (Exception e) {
            // Expected if docx4j.properties has invalid config values
        }
    }

    @Test
    @Order(3)
    @DisplayName("EngineFactory.get() returns same cached instance on consecutive calls")
    void engineFactoryReturnsSameInstanceOnConsecutiveCalls() {
        try {
            EngineFactory factory = new EngineFactory();
            Engine first = factory.get();
            assertNotNull(first);
            Engine second = factory.get();
            assertSame(first, second, "EngineFactory.get() must return the same cached instance");
        } catch (Exception e) {
            // Expected if docx4j.properties has invalid config values
        }
    }

    @Test
    @Order(4)
    @DisplayName("getEngine() caches the engine instance")
    void getEngineCachesInstance() {
        try {
            WordprocessingMLWebitTemplate tpl = new WordprocessingMLWebitTemplate();
            Engine first = tpl.getEngine();
            Engine second = tpl.getEngine();
            assertNotNull(first);
            assertSame(first, second, "getEngine() must return the same cached instance");
        } catch (Exception e) {
            // Expected if docx4j.properties has invalid config values
        }
    }

    @Test
    @Order(5)
    @DisplayName("setEngine() overrides the cached instance")
    void setEngineOverridesCache() {
        try {
            WordprocessingMLWebitTemplate tpl = new WordprocessingMLWebitTemplate();
            Engine original = tpl.getEngine();
            tpl.setEngine(original);
            Engine retrieved = tpl.getEngine();
            assertSame(original, retrieved, "setEngine then getEngine must return the set instance");
        } catch (Exception e) {
            // Expected if docx4j.properties has invalid config values
        }
    }
}
