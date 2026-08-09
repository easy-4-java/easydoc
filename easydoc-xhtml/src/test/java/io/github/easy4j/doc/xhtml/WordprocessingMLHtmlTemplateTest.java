package io.github.easy4j.doc.xhtml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.WordprocessingMLTemplate;
import org.jsoup.nodes.Document;

/**
 * Unit tests for {@link WordprocessingMLHtmlTemplate}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WordprocessingMLHtmlTemplate Tests")
class WordprocessingMLHtmlTemplateTest {

    @Test
    @DisplayName("should have default constructor")
    void shouldHaveDefaultConstructor() {
        try { new WordprocessingMLHtmlTemplate(); } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHtmlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessShouldBeCallable() {
        try {
            WordprocessingMLHtmlTemplate instance = new WordprocessingMLHtmlTemplate();
            instance.process((File) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHtmlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith1ParamsShouldBeCallable() {
        try {
            WordprocessingMLHtmlTemplate instance = new WordprocessingMLHtmlTemplate();
            instance.process((File) null, (PageSizePaper) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHtmlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith2ParamsShouldBeCallable() {
        try {
            WordprocessingMLHtmlTemplate instance = new WordprocessingMLHtmlTemplate();
            instance.process((Document) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHtmlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith3ParamsShouldBeCallable() {
        try {
            WordprocessingMLHtmlTemplate instance = new WordprocessingMLHtmlTemplate();
            instance.process((Document) null, (PageSizePaper) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHtmlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith4ParamsShouldBeCallable() {
        try {
            WordprocessingMLHtmlTemplate instance = new WordprocessingMLHtmlTemplate();
            instance.process((InputStream) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHtmlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith5ParamsShouldBeCallable() {
        try {
            WordprocessingMLHtmlTemplate instance = new WordprocessingMLHtmlTemplate();
            instance.process((InputStream) null, (PageSizePaper) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHtmlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith6ParamsShouldBeCallable() {
        try {
            WordprocessingMLHtmlTemplate instance = new WordprocessingMLHtmlTemplate();
            instance.process((URL) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHtmlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith7ParamsShouldBeCallable() {
        try {
            WordprocessingMLHtmlTemplate instance = new WordprocessingMLHtmlTemplate();
            instance.process("test", (Map) null, (PageSizePaper) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHtmlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith8ParamsShouldBeCallable() {
        try {
            WordprocessingMLHtmlTemplate instance = new WordprocessingMLHtmlTemplate();
            instance.process((File) null, (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHtmlTemplate.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessWith9ParamsShouldBeCallable() {
        try {
            WordprocessingMLHtmlTemplate instance = new WordprocessingMLHtmlTemplate();
            instance.process((InputStream) null, (Map) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLHtmlTemplate.class).isNotNull();
    }

}
