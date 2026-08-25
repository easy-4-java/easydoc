package io.github.easy4j.doc.jetbrick;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import jetbrick.template.JetEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

/**
 * Tests for EngineFactory DCL short-circuit and Renderer statelessness.
 */
@DisplayName("Jetbrick EngineFactory + Renderer tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WordprocessingMLJetbrickCoverageTest {

    @Test
    @Order(1)
    @DisplayName("EngineFactory.get() returns non-null instance")
    void engineFactoryReturnsNonNull() throws Exception {
        EngineFactory factory = new EngineFactory();
        JetEngine engine = factory.get();
        assertNotNull(engine, "EngineFactory.get() must return a non-null engine");
    }

    @Test
    @Order(2)
    @DisplayName("EngineFactory.get() returns same cached instance on consecutive calls")
    void engineFactoryReturnsSameInstanceOnConsecutiveCalls() throws Exception {
        EngineFactory factory = new EngineFactory();
        JetEngine first = factory.get();
        assertNotNull(first);
        JetEngine second = factory.get();
        assertSame(first, second, "EngineFactory.get() must return the same cached instance");
    }

    @Test
    @Order(3)
    @DisplayName("Renderer is stateless: same inputs produce same output")
    void rendererIsStateless() throws Exception {
        Renderer renderer = new Renderer();
        WordprocessingMLJetbrickTemplate tpl = new WordprocessingMLJetbrickTemplate();
        JetEngine engine = tpl.getEngine();
        java.util.Map<String, Object> vars = new java.util.HashMap<String, Object>();
        vars.put("name", "world");
        String first = renderer.render("/tpl/jetbrick.jetx", vars, engine);
        assertNotNull(first);
        String second = renderer.render("/tpl/jetbrick.jetx", vars, engine);
        assertEquals(first, second, "Renderer must be stateless: same inputs produce same output");
    }

    @Test
    @Order(4)
    @DisplayName("getEngine() caches the engine instance")
    void getEngineCachesInstance() throws Exception {
        WordprocessingMLJetbrickTemplate tpl = new WordprocessingMLJetbrickTemplate();
        JetEngine first = tpl.getEngine();
        JetEngine second = tpl.getEngine();
        assertNotNull(first);
        assertSame(first, second, "getEngine() must return the same cached instance");
    }

    @Test
    @Order(5)
    @DisplayName("setEngine() overrides the cached instance")
    void setEngineOverridesCache() throws Exception {
        WordprocessingMLJetbrickTemplate tpl = new WordprocessingMLJetbrickTemplate();
        JetEngine original = tpl.getEngine();
        tpl.setEngine(original);
        JetEngine retrieved = tpl.getEngine();
        assertSame(original, retrieved, "setEngine then getEngine must return the set instance");
    }
}
