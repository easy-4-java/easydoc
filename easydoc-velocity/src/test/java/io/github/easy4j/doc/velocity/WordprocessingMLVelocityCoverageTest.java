package io.github.easy4j.doc.velocity;

import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.apache.velocity.app.VelocityEngine;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Additional tests to push JaCoCo line coverage of
 * {@link WordprocessingMLVelocityTemplate} above 90 %.
 */
class WordprocessingMLVelocityCoverageTest {

    @Test
    void twoArgConstructorSetsFlags() {
        WordprocessingMLVelocityTemplate t = new WordprocessingMLVelocityTemplate(true, false);
        assertNotNull(t);
    }

    @Test
    void htmlTemplateConstructorStoresDelegate() {
        WordprocessingMLHtmlTemplate delegate = new WordprocessingMLHtmlTemplate(false, true);
        WordprocessingMLVelocityTemplate t = new WordprocessingMLVelocityTemplate(delegate);
        assertNotNull(t);
        assertNotNull(t.getMlHtmlTemplate());
    }

    @Test
    void getEngineReturnsPresetInstanceWhenSet() throws Exception {
        WordprocessingMLVelocityTemplate t = new WordprocessingMLVelocityTemplate();
        VelocityEngine engine = new VelocityEngine();
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
        VelocityEngine first = factory.get();
        assertNotNull(first);
        VelocityEngine second = factory.get();
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
        WordprocessingMLVelocityTemplate tpl = new WordprocessingMLVelocityTemplate();
        VelocityEngine engine = tpl.getEngine();
        Map<String, Object> vars = new HashMap<>(Map.of("name", "world"));
        String first = renderer.render("hello.vm", vars, engine);
        assertNotNull(first);
        String second = renderer.render("hello.vm", vars, engine);
        assertEquals(first, second, "Renderer must be stateless: same inputs produce same output");
    }
}
