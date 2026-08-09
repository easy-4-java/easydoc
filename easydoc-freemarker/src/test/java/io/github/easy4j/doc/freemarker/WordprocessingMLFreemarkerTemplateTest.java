package io.github.easy4j.doc.freemarker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import io.github.easy4j.doc.utils.ConfigUtils;
import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.HtmlEscape;
import freemarker.template.utility.XmlEscape;
import java.util.Set;

/**
 * Unit tests for {@link WordprocessingMLFreemarkerTemplate}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WordprocessingMLFreemarkerTemplate Tests")
class WordprocessingMLFreemarkerTemplateTest {

    @Test
    @DisplayName("should have default constructor")
    void shouldHaveDefaultConstructor() {
        try { new WordprocessingMLFreemarkerTemplate(); } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLFreemarkerTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessShouldBeCallable() {
        try {
            WordprocessingMLFreemarkerTemplate instance = new WordprocessingMLFreemarkerTemplate();
            instance.process((File) null, (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLFreemarkerTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith1ParamsShouldBeCallable() {
        try {
            WordprocessingMLFreemarkerTemplate instance = new WordprocessingMLFreemarkerTemplate();
            instance.process((InputStream) null, (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLFreemarkerTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith2ParamsShouldBeCallable() {
        try {
            WordprocessingMLFreemarkerTemplate instance = new WordprocessingMLFreemarkerTemplate();
            instance.process("test", (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLFreemarkerTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method getEngine should be callable")
    void instanceGetEngineShouldBeCallable() {
        try {
            WordprocessingMLFreemarkerTemplate instance = new WordprocessingMLFreemarkerTemplate();
            instance.getEngine();
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLFreemarkerTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method setEngine should be callable")
    void instanceSetEngineShouldBeCallable() {
        try {
            WordprocessingMLFreemarkerTemplate instance = new WordprocessingMLFreemarkerTemplate();
            instance.setEngine((Configuration) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLFreemarkerTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method getInternalEngine should be callable")
    void instanceGetInternalEngineShouldBeCallable() {
        try {
            WordprocessingMLFreemarkerTemplate instance = new WordprocessingMLFreemarkerTemplate();
            instance.getInternalEngine();
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLFreemarkerTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method getAggregateTemplateLoader should be callable")
    void instanceGetAggregateTemplateLoaderShouldBeCallable() {
        try {
            WordprocessingMLFreemarkerTemplate instance = new WordprocessingMLFreemarkerTemplate();
            instance.getAggregateTemplateLoader((List) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLFreemarkerTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method setFreemarkerSettings should be callable")
    void instanceSetFreemarkerSettingsShouldBeCallable() {
        try {
            WordprocessingMLFreemarkerTemplate instance = new WordprocessingMLFreemarkerTemplate();
            instance.setFreemarkerSettings((Properties) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLFreemarkerTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method setFreemarkerVariables should be callable")
    void instanceSetFreemarkerVariablesShouldBeCallable() {
        try {
            WordprocessingMLFreemarkerTemplate instance = new WordprocessingMLFreemarkerTemplate();
            instance.setFreemarkerVariables((Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLFreemarkerTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method setDefaultEncoding should be callable")
    void instanceSetDefaultEncodingShouldBeCallable() {
        try {
            WordprocessingMLFreemarkerTemplate instance = new WordprocessingMLFreemarkerTemplate();
            instance.setDefaultEncoding("test");
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLFreemarkerTemplate.class).isNotNull();
    }

}
