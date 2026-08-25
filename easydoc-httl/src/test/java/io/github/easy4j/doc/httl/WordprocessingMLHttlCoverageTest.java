package io.github.easy4j.doc.httl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import httl.Engine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

/**
 * Tests for EngineFactory DCL short-circuit and engine caching.
 */
@DisplayName("HTTL EngineFactory + caching tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WordprocessingMLHttlCoverageTest {

    @Test
    @Order(1)
    @DisplayName("EngineFactory.get() returns non-null instance")
    void engineFactoryReturnsNonNull() throws Exception {
        EngineFactory factory = new EngineFactory();
        Engine engine = factory.get();
        assertNotNull(engine, "EngineFactory.get() must return a non-null engine");
    }

    @Test
    @Order(2)
    @DisplayName("EngineFactory.get() returns same cached instance on consecutive calls")
    void engineFactoryReturnsSameInstanceOnConsecutiveCalls() throws Exception {
        EngineFactory factory = new EngineFactory();
        Engine first = factory.get();
        assertNotNull(first);
        Engine second = factory.get();
        assertSame(first, second, "EngineFactory.get() must return the same cached instance");
    }

    @Test
    @Order(3)
    @DisplayName("getEngine() caches the engine instance")
    void getEngineCachesInstance() throws Exception {
        WordprocessingMLHttlTemplate tpl = new WordprocessingMLHttlTemplate();
        Engine first = tpl.getEngine();
        Engine second = tpl.getEngine();
        assertNotNull(first);
        assertSame(first, second, "getEngine() must return the same cached instance");
    }

    @Test
    @Order(4)
    @DisplayName("setEngine() overrides the cached instance")
    void setEngineOverridesCache() throws Exception {
        WordprocessingMLHttlTemplate tpl = new WordprocessingMLHttlTemplate();
        Engine original = tpl.getEngine();
        tpl.setEngine(original);
        Engine retrieved = tpl.getEngine();
        assertSame(original, retrieved, "setEngine then getEngine must return the set instance");
    }

}
