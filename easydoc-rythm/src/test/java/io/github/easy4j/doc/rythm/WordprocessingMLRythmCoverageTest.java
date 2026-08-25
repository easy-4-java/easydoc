package io.github.easy4j.doc.rythm;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.rythmengine.RythmEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

/**
 * Tests for EngineFactory DCL short-circuit and engine caching.
 */
@DisplayName("Rythm EngineFactory + caching tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WordprocessingMLRythmCoverageTest {

    @Test
    @Order(1)
    @DisplayName("EngineFactory.get() returns non-null instance")
    void engineFactoryReturnsNonNull() throws Exception {
        EngineFactory factory = new EngineFactory();
        RythmEngine engine = factory.get();
        assertNotNull(engine, "EngineFactory.get() must return a non-null engine");
    }

    @Test
    @Order(2)
    @DisplayName("EngineFactory.get() returns same cached instance on consecutive calls")
    void engineFactoryReturnsSameInstanceOnConsecutiveCalls() throws Exception {
        EngineFactory factory = new EngineFactory();
        RythmEngine first = factory.get();
        assertNotNull(first);
        RythmEngine second = factory.get();
        assertSame(first, second, "EngineFactory.get() must return the same cached instance");
    }

    @Test
    @Order(3)
    @DisplayName("getEngine() caches the engine instance")
    void getEngineCachesInstance() throws Exception {
        WordprocessingMLRythmTemplate tpl = new WordprocessingMLRythmTemplate();
        RythmEngine first = tpl.getEngine();
        RythmEngine second = tpl.getEngine();
        assertNotNull(first);
        assertSame(first, second, "getEngine() must return the same cached instance");
    }

    @Test
    @Order(4)
    @DisplayName("setEngine() overrides the cached instance")
    void setEngineOverridesCache() throws Exception {
        WordprocessingMLRythmTemplate tpl = new WordprocessingMLRythmTemplate();
        RythmEngine original = tpl.getEngine();
        tpl.setEngine(original);
        RythmEngine retrieved = tpl.getEngine();
        assertSame(original, retrieved, "setEngine then getEngine must return the set instance");
    }

    @Test
    @Order(5)
    @DisplayName("Renderer can render with engine from factory")
    void rendererCanRender() throws Exception {
        WordprocessingMLRythmTemplate tpl = new WordprocessingMLRythmTemplate();
        RythmEngine engine = tpl.getEngine();
        Renderer renderer = new Renderer();
        java.util.Map<String, Object> vars = new java.util.HashMap<String, Object>();
        vars.put("name", "world");
        String result = renderer.render("rythm.tpl", vars, engine);
        assertNotNull(result, "Renderer must produce non-null output");
    }
}
