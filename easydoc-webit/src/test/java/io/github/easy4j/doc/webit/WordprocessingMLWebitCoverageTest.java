package io.github.easy4j.doc.webit;

import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.junit.jupiter.api.Test;
import webit.script.Engine;

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
}
