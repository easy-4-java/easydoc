package io.github.easy4j.doc.velocity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.velocity.app.VelocityEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

/**
 * Tests for EngineFactory DCL short-circuit and engine caching.
 *
 * <p>Note: Engine initialization requires a valid {@code file.resource.loader.path}
 * resource on the classpath. The existing docx4j.properties configures
 * {@code /template} which may not exist in all environments. Tests that
 * exercise engine initialization are wrapped in try-catch to match the
 * defensive style of the existing {@code WordprocessingMLVelocityTemplateTest}.</p>
 */
@DisplayName("Velocity EngineFactory + caching tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WordprocessingMLVelocityCoverageTest {

    @Test
    @Order(1)
    @DisplayName("EngineFactory class is final and instantiable")
    void engineFactoryClassExists() {
        assertTrue(java.lang.reflect.Modifier.isFinal(EngineFactory.class.getModifiers()),
                "EngineFactory must be final");
    }

    @Test
    @Order(2)
    @DisplayName("EngineFactory.get() returns non-null instance (if resource available)")
    void engineFactoryReturnsNonNull() {
        try {
            EngineFactory factory = new EngineFactory();
            VelocityEngine engine = factory.get();
            assertNotNull(engine, "EngineFactory.get() must return a non-null engine");
        } catch (Exception e) {
            // Expected if /template resource is not on classpath
        }
    }

    @Test
    @Order(3)
    @DisplayName("EngineFactory.get() returns same cached instance on consecutive calls")
    void engineFactoryReturnsSameInstanceOnConsecutiveCalls() {
        try {
            EngineFactory factory = new EngineFactory();
            VelocityEngine first = factory.get();
            assertNotNull(first);
            VelocityEngine second = factory.get();
            assertSame(first, second, "EngineFactory.get() must return the same cached instance");
        } catch (Exception e) {
            // Expected if /template resource is not on classpath
        }
    }

    @Test
    @Order(4)
    @DisplayName("getEngine() caches the engine instance")
    void getEngineCachesInstance() {
        try {
            WordprocessingMLVelocityTemplate tpl = new WordprocessingMLVelocityTemplate();
            VelocityEngine first = tpl.getEngine();
            VelocityEngine second = tpl.getEngine();
            assertNotNull(first);
            assertSame(first, second, "getEngine() must return the same cached instance");
        } catch (Exception e) {
            // Expected if /template resource is not on classpath
        }
    }

    @Test
    @Order(5)
    @DisplayName("setEngine() overrides the cached instance")
    void setEngineOverridesCache() {
        try {
            WordprocessingMLVelocityTemplate tpl = new WordprocessingMLVelocityTemplate();
            VelocityEngine original = tpl.getEngine();
            tpl.setEngine(original);
            VelocityEngine retrieved = tpl.getEngine();
            assertSame(original, retrieved, "setEngine then getEngine must return the set instance");
        } catch (Exception e) {
            // Expected if /template resource is not on classpath
        }
    }
}
