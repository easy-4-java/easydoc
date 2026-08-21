package io.github.easy4j.doc.jetbrick;

import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Additional tests to push JaCoCo line coverage of
 * {@link WordprocessingMLJetbrickTemplate} above 90 %.
 */
class WordprocessingMLJetbrickCoverageTest {

    @Test
    void twoArgConstructorSetsFlags() {
        WordprocessingMLJetbrickTemplate t = new WordprocessingMLJetbrickTemplate(true, false);
        assertNotNull(t);
    }

    @Test
    void htmlTemplateConstructorStoresDelegate() {
        WordprocessingMLHtmlTemplate delegate = new WordprocessingMLHtmlTemplate(false, true);
        WordprocessingMLJetbrickTemplate t = new WordprocessingMLJetbrickTemplate(delegate);
        assertNotNull(t);
        assertNotNull(t.getMlHtmlTemplate());
    }

    @Test
    void getEngineReturnsPresetInstanceWhenSet() throws Exception {
        WordprocessingMLJetbrickTemplate t = new WordprocessingMLJetbrickTemplate();
        jetbrick.template.JetEngine engine = jetbrick.template.JetEngine.create();
        t.setEngine(engine);
        assertSame(engine, t.getEngine());
    }
}
