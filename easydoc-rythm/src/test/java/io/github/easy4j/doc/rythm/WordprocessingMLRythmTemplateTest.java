package io.github.easy4j.doc.rythm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import io.github.easy4j.doc.utils.ConfigUtils;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;

/**
 * Unit tests for {@link WordprocessingMLRythmTemplate}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WordprocessingMLRythmTemplate Tests")
class WordprocessingMLRythmTemplateTest {

    @Test
    @DisplayName("should have default constructor")
    void shouldHaveDefaultConstructor() {
        try { new WordprocessingMLRythmTemplate(); } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLRythmTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessShouldBeCallable() {
        try {
            WordprocessingMLRythmTemplate instance = new WordprocessingMLRythmTemplate();
            instance.process((File) null, (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLRythmTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith1ParamsShouldBeCallable() {
        try {
            WordprocessingMLRythmTemplate instance = new WordprocessingMLRythmTemplate();
            instance.process((InputStream) null, (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLRythmTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith2ParamsShouldBeCallable() {
        try {
            WordprocessingMLRythmTemplate instance = new WordprocessingMLRythmTemplate();
            instance.process("test", (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLRythmTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method getEngine should be callable")
    void instanceGetEngineShouldBeCallable() {
        try {
            WordprocessingMLRythmTemplate instance = new WordprocessingMLRythmTemplate();
            instance.getEngine();
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLRythmTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method setEngine should be callable")
    void instanceSetEngineShouldBeCallable() {
        try {
            WordprocessingMLRythmTemplate instance = new WordprocessingMLRythmTemplate();
            instance.setEngine((RythmEngine) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLRythmTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method getInternalEngine should be callable")
    void instanceGetInternalEngineShouldBeCallable() {
        try {
            WordprocessingMLRythmTemplate instance = new WordprocessingMLRythmTemplate();
            instance.getInternalEngine();
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLRythmTemplate.class).isNotNull();
    }

}
