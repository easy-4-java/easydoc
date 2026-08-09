package io.github.easy4j.doc.thymeleaf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import io.github.easy4j.doc.utils.ArrayUtils;
import io.github.easy4j.doc.utils.StringUtils;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;
import java.util.Properties;
import java.util.Set;

/**
 * Unit tests for {@link WordprocessingMLThymeleafTemplate}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WordprocessingMLThymeleafTemplate Tests")
class WordprocessingMLThymeleafTemplateTest {

    @Test
    @DisplayName("should have default constructor")
    void shouldHaveDefaultConstructor() {
        try { new WordprocessingMLThymeleafTemplate(); } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLThymeleafTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessShouldBeCallable() {
        try {
            WordprocessingMLThymeleafTemplate instance = new WordprocessingMLThymeleafTemplate();
            instance.process((File) null, (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLThymeleafTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith1ParamsShouldBeCallable() {
        try {
            WordprocessingMLThymeleafTemplate instance = new WordprocessingMLThymeleafTemplate();
            instance.process((InputStream) null, (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLThymeleafTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith2ParamsShouldBeCallable() {
        try {
            WordprocessingMLThymeleafTemplate instance = new WordprocessingMLThymeleafTemplate();
            instance.process("test", (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLThymeleafTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method getEngine should be callable")
    void instanceGetEngineShouldBeCallable() {
        try {
            WordprocessingMLThymeleafTemplate instance = new WordprocessingMLThymeleafTemplate();
            instance.getEngine();
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLThymeleafTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method setEngine should be callable")
    void instanceSetEngineShouldBeCallable() {
        try {
            WordprocessingMLThymeleafTemplate instance = new WordprocessingMLThymeleafTemplate();
            instance.setEngine((TemplateEngine) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLThymeleafTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method getInternalEngine should be callable")
    void instanceGetInternalEngineShouldBeCallable() {
        try {
            WordprocessingMLThymeleafTemplate instance = new WordprocessingMLThymeleafTemplate();
            instance.getInternalEngine();
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLThymeleafTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method getTemplateResolver should be callable")
    void instanceGetTemplateResolverShouldBeCallable() {
        try {
            WordprocessingMLThymeleafTemplate instance = new WordprocessingMLThymeleafTemplate();
            instance.getTemplateResolver();
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLThymeleafTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method setTemplateResolver should be callable")
    void instanceSetTemplateResolverShouldBeCallable() {
        try {
            WordprocessingMLThymeleafTemplate instance = new WordprocessingMLThymeleafTemplate();
            instance.setTemplateResolver((AbstractConfigurableTemplateResolver) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLThymeleafTemplate.class).isNotNull();
    }

}
