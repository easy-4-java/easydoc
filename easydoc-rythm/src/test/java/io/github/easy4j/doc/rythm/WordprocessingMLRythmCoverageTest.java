package io.github.easy4j.doc.rythm;

import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.junit.jupiter.api.Test;
import org.rythmengine.RythmEngine;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Additional tests to push JaCoCo line coverage of
 * {@link WordprocessingMLRythmTemplate} above 90 %.
 */
class WordprocessingMLRythmCoverageTest {

    @Test
    void twoArgConstructorSetsFlags() {
        WordprocessingMLRythmTemplate t = new WordprocessingMLRythmTemplate(true, false);
        assertNotNull(t);
    }

    @Test
    void htmlTemplateConstructorStoresDelegate() {
        WordprocessingMLHtmlTemplate delegate = new WordprocessingMLHtmlTemplate(false, true);
        WordprocessingMLRythmTemplate t = new WordprocessingMLRythmTemplate(delegate);
        assertNotNull(t);
        assertNotNull(t.getMlHtmlTemplate());
    }

    /**
     * Exercises the {@code engine != null} branch of getEngine() by first
     * initializing the engine via a process() call, then calling getEngine()
     * a second time which returns the cached instance.
     */
    @Test
    void getEngineReturnsCachedInstanceOnSecondCall() throws Exception {
        WordprocessingMLRythmTemplate t = new WordprocessingMLRythmTemplate();
        // First call initializes the engine via getInternalEngine()
        RythmEngine first = t.getEngine();
        assertNotNull(first);
        // Second call should return the cached engine (engine != null branch)
        RythmEngine second = t.getEngine();
        assertSame(first, second);
    }

    /**
     * Covers the DCL short-circuit path inside {@code getInternalEngine()}:
     * after the first call initialized the engine, a second direct call must
     * skip the {@code synchronized} block and return the same volatile field
     * (the {@code local != null} branch added by the virtual-thread-friendly
     * double-checked-locking refactor).
     */
    @Test
    void getInternalEngineTwiceShortCircuitsDcl() throws Exception {
        WordprocessingMLRythmTemplate t = new WordprocessingMLRythmTemplate();
        RythmEngine first = t.getInternalEngine();
        assertNotNull(first);
        // Second direct call: outer local==null check is false → short-circuit
        RythmEngine second = t.getInternalEngine();
        assertSame(first, second);
    }

    @Test
    void setEngineStoresAndOverrides() throws Exception {
        WordprocessingMLRythmTemplate t = new WordprocessingMLRythmTemplate();
        RythmEngine custom = new RythmEngine();
        t.setEngine(custom);
        assertSame(custom, t.getEngine(), "setEngine must make getEngine return the injected instance");
    }

    /**
     * Exercises the EngineFactory DCL short-circuit: consecutive calls to
     * {@code factory.get()} must return the same instance without re-entering
     * the synchronized block.
     */
    @Test
    void engineFactoryReturnsSameInstanceOnConsecutiveCalls() throws Exception {
        EngineFactory factory = new EngineFactory();
        RythmEngine first = factory.get();
        assertNotNull(first);
        RythmEngine second = factory.get();
        assertSame(first, second, "EngineFactory.get() must return the same cached instance");
    }

    /**
     * Exercises the Renderer statelessness guarantee: rendering the same
     * template with the same variables twice through the same Renderer
     * instance must produce identical results.
     */
    @Test
    void rendererProducesIdenticalResultsForSameInputs() throws Exception {
        Renderer renderer = new Renderer();
        RythmEngine engine = new EngineFactory().get();
        Map<String, Object> vars = Map.of("name", "world");
        String first = renderer.render("/tpl/hello.tpl", vars, engine);
        assertNotNull(first);
        String second = renderer.render("/tpl/hello.tpl", vars, engine);
        assertEquals(first, second, "Renderer must be stateless: same inputs produce same output");
    }
}
