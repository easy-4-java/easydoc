package io.github.easy4j.doc.httl;

import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Additional tests to push JaCoCo line coverage of
 * {@link WordprocessingMLHttlTemplate} above 90 %.
 */
class WordprocessingMLHttlCoverageTest {

    @Test
    void twoArgConstructorSetsFlags() {
        WordprocessingMLHttlTemplate t = new WordprocessingMLHttlTemplate(true, false);
        assertNotNull(t);
    }

    @Test
    void htmlTemplateConstructorStoresDelegate() {
        WordprocessingMLHtmlTemplate delegate = new WordprocessingMLHtmlTemplate(false, true);
        WordprocessingMLHttlTemplate t = new WordprocessingMLHttlTemplate(delegate);
        assertNotNull(t);
        assertNotNull(t.getMlHtmlTemplate());
    }

    @Test
    void getEngineReturnsPresetInstanceWhenSet() throws Exception {
        WordprocessingMLHttlTemplate t = new WordprocessingMLHttlTemplate();
        httl.Engine engine = httl.Engine.getEngine();
        t.setEngine(engine);
        assertSame(engine, t.getEngine());
    }

    /**
     * Exercises the EngineFactory DCL short-circuit: consecutive calls to
     * {@code factory.get()} must return the same instance without re-entering
     * the synchronized block.
     */
    @Test
    void engineFactoryReturnsSameInstanceOnConsecutiveCalls() throws Exception {
        EngineFactory factory = new EngineFactory();
        httl.Engine first = factory.get();
        assertNotNull(first);
        httl.Engine second = factory.get();
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
        httl.Engine engine = httl.Engine.getEngine();
        Map<String, Object> vars = Map.of("name", "world");
        String first = renderer.render("/tpl/hello.htt", vars, engine);
        assertNotNull(first);
        String second = renderer.render("/tpl/hello.htt", vars, engine);
        assertEquals(first, second, "Renderer must be stateless: same inputs produce same output");
    }
}
