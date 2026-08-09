package io.github.easy4j.doc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

/**
 * Unit tests for {@link WordprocessingMLTemplate}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WordprocessingMLTemplate Tests")
class WordprocessingMLTemplateTest {

    @Test
    @DisplayName("should be an interface")
    void shouldBeAnInterface() {
        assertThat(WordprocessingMLTemplate.class).isInterface();
    }

    @Test
    @DisplayName("class should be loadable")
    void classShouldBeLoadable() {
        assertThat(WordprocessingMLTemplate.class.getName()).isEqualTo("io.github.easy4j.doc.WordprocessingMLTemplate");
    }

}
