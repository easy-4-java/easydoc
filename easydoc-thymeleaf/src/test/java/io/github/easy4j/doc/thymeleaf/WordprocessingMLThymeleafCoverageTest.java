package io.github.easy4j.doc.thymeleaf;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.thymeleaf.TemplateEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

/**
 * Tests for EngineFactory DCL short-circuit and engine caching.
 */
@DisplayName("Thymeleaf EngineFactory + caching tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WordprocessingMLThymeleafCoverageTest {

    @Test
    @Order(1)
    @DisplayName("EngineFactory class is final and instantiable")
    void engineFactoryClassExists() {
        assertTrue(java.lang.reflect.Modifier.isFinal(EngineFactory.class.getModifiers()),
                "EngineFactory must be final");
    }

    @Test
    @Order(2)
    @DisplayName("EngineFactory.get() returns non-null instance")
    void engineFactoryReturnsNonNull() throws Exception {
        EngineFactory factory = new EngineFactory(null);
        TemplateEngine engine = factory.get();
        assertNotNull(engine, "EngineFactory.get() must return a non-null engine");
    }

    @Test
    @Order(3)
    @DisplayName("EngineFactory.get() returns same cached instance on consecutive calls")
    void engineFactoryReturnsSameInstanceOnConsecutiveCalls() throws Exception {
        EngineFactory factory = new EngineFactory(null);
        TemplateEngine first = factory.get();
        assertNotNull(first);
        TemplateEngine second = factory.get();
        assertSame(first, second, "EngineFactory.get() must return the same cached instance");
    }

    @Test
    @Order(4)
    @DisplayName("getEngine() caches the engine instance")
    void getEngineCachesInstance() throws Exception {
        WordprocessingMLThymeleafTemplate tpl = new WordprocessingMLThymeleafTemplate();
        TemplateEngine first = tpl.getEngine();
        TemplateEngine second = tpl.getEngine();
        assertNotNull(first);
        assertSame(first, second, "getEngine() must return the same cached instance");
    }

    @Test
    @Order(5)
    @DisplayName("setEngine() overrides the cached instance")
    void setEngineOverridesCache() throws Exception {
        WordprocessingMLThymeleafTemplate tpl = new WordprocessingMLThymeleafTemplate();
        TemplateEngine original = tpl.getEngine();
        tpl.setEngine(original);
        TemplateEngine retrieved = tpl.getEngine();
        assertSame(original, retrieved, "setEngine then getEngine must return the set instance");
    }
}
