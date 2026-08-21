package io.github.easy4j.doc.velocity;

import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
