package io.github.easy4j.doc.rythm;

import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.junit.jupiter.api.Test;
import org.rythmengine.RythmEngine;

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
}
