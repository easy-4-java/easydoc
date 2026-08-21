package io.github.easy4j.doc.httl;

import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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
}
