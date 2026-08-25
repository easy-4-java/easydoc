package io.github.easy4j.doc.freemarker;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import freemarker.template.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

/**
 * Tests for EngineFactory DCL short-circuit and engine caching.
 */
@DisplayName("Freemarker EngineFactory + caching tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WordprocessingMLFreemarkerCoverageTest {

    @Test
    @Order(1)
    @DisplayName("EngineFactory.get() returns non-null instance")
    void engineFactoryReturnsNonNull() throws Exception {
        EngineFactory factory = new EngineFactory(null, null, null, null, null);
        Configuration engine = factory.get();
        assertNotNull(engine, "EngineFactory.get() must return a non-null engine");
    }

    @Test
    @Order(2)
    @DisplayName("EngineFactory.get() returns same cached instance on consecutive calls")
    void engineFactoryReturnsSameInstanceOnConsecutiveCalls() throws Exception {
        EngineFactory factory = new EngineFactory(null, null, null, null, null);
        Configuration first = factory.get();
        assertNotNull(first);
        Configuration second = factory.get();
        assertSame(first, second, "EngineFactory.get() must return the same cached instance");
    }

    @Test
    @Order(3)
    @DisplayName("getEngine() caches the engine instance")
    void getEngineCachesInstance() throws Exception {
        WordprocessingMLFreemarkerTemplate tpl = new WordprocessingMLFreemarkerTemplate();
        Configuration first = tpl.getEngine();
        Configuration second = tpl.getEngine();
        assertNotNull(first);
        assertSame(first, second, "getEngine() must return the same cached instance");
    }

    @Test
    @Order(4)
    @DisplayName("setEngine() overrides the cached instance")
    void setEngineOverridesCache() throws Exception {
        WordprocessingMLFreemarkerTemplate tpl = new WordprocessingMLFreemarkerTemplate();
        Configuration original = tpl.getEngine();
        tpl.setEngine(original);
        Configuration retrieved = tpl.getEngine();
        assertSame(original, retrieved, "setEngine then getEngine must return the set instance");
    }

}
