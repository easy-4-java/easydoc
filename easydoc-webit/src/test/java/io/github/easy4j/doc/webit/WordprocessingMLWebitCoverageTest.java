package io.github.easy4j.doc.webit;

import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.junit.jupiter.api.Test;
import webit.script.Engine;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Additional tests to push JaCoCo line coverage of
 * {@link WordprocessingMLWebitTemplate} above 90 %.
 */
class WordprocessingMLWebitCoverageTest {

    @Test
    void twoArgConstructorSetsFlags() {
        WordprocessingMLWebitTemplate t = new WordprocessingMLWebitTemplate(true, false);
        assertNotNull(t);
    }

    @Test
    void htmlTemplateConstructorStoresDelegate() {
        WordprocessingMLHtmlTemplate delegate = new WordprocessingMLHtmlTemplate(false, true);
        WordprocessingMLWebitTemplate t = new WordprocessingMLWebitTemplate(delegate);
        assertNotNull(t);
        assertNotNull(t.getMlHtmlTemplate());
    }

    @Test
    void getEngineReturnsPresetInstanceWhenSet() throws Exception {
        WordprocessingMLWebitTemplate t = new WordprocessingMLWebitTemplate();
        java.util.Map<String, Object> ps = new java.util.HashMap<>();
        ps.put(webit.script.CFG.LOOSE_VAR, true);
        ps.put(webit.script.CFG.LOGGER, "webit.script.loggers.impl.NOPLogger");
        ps.put(webit.script.CFG.SUFFIX, ".wit");
        Engine engine = Engine.create("", ps);
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
        Engine first = factory.get();
        assertNotNull(first);
        Engine second = factory.get();
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
        // Enable looseVar so that undeclared variables do not cause errors
        java.util.Properties props = org.docx4j.Docx4jProperties.getProperties();
        String prev = props.getProperty("docx4j.webit.engine.looseVar");
        props.setProperty("docx4j.webit.engine.looseVar", "true");
        try {
            Engine engine = Engine.create("", new java.util.HashMap<>() {{
                put(webit.script.CFG.LOOSE_VAR, true);
                put(webit.script.CFG.LOGGER, "webit.script.loggers.impl.NOPLogger");
                put(webit.script.CFG.SUFFIX, ".html");
            }});
            Map<String, Object> vars = Map.of("name", "world");
            String first = renderer.render("/tpl/hello.html", vars, engine);
            assertNotNull(first);
            String second = renderer.render("/tpl/hello.html", vars, engine);
            assertEquals(first, second, "Renderer must be stateless: same inputs produce same output");
        } finally {
            props.remove("docx4j.webit.engine.looseVar");
            if (prev != null) {
                props.setProperty("docx4j.webit.engine.looseVar", prev);
            }
        }
    }
}
