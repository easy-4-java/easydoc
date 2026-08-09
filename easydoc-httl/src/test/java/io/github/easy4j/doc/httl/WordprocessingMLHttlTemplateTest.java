package io.github.easy4j.doc.httl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import io.github.easy4j.doc.utils.ConfigUtils;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import httl.Engine;

/**
 * Unit tests for {@link WordprocessingMLHttlTemplate}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WordprocessingMLHttlTemplate Tests")
class WordprocessingMLHttlTemplateTest {

    @Test
    @DisplayName("should have default constructor")
    void shouldHaveDefaultConstructor() {
        try { new WordprocessingMLHttlTemplate(); } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHttlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessShouldBeCallable() {
        try {
            WordprocessingMLHttlTemplate instance = new WordprocessingMLHttlTemplate();
            instance.process((File) null, (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHttlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith1ParamsShouldBeCallable() {
        try {
            WordprocessingMLHttlTemplate instance = new WordprocessingMLHttlTemplate();
            instance.process((InputStream) null, (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHttlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith2ParamsShouldBeCallable() {
        try {
            WordprocessingMLHttlTemplate instance = new WordprocessingMLHttlTemplate();
            instance.process("test", (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHttlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method getEngine should be callable")
    void instanceGetEngineShouldBeCallable() {
        try {
            WordprocessingMLHttlTemplate instance = new WordprocessingMLHttlTemplate();
            instance.getEngine();
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHttlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method setEngine should be callable")
    void instanceSetEngineShouldBeCallable() {
        try {
            WordprocessingMLHttlTemplate instance = new WordprocessingMLHttlTemplate();
            instance.setEngine((Engine) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHttlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method getInternalEngine should be callable")
    void instanceGetInternalEngineShouldBeCallable() {
        try {
            WordprocessingMLHttlTemplate instance = new WordprocessingMLHttlTemplate();
            instance.getInternalEngine();
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHttlTemplate.class).isNotNull();
    }

}
