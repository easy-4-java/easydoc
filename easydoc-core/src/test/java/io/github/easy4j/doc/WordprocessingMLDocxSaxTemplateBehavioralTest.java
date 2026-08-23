package io.github.easy4j.doc;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Behavioral tests for {@link WordprocessingMLDocxSaxTemplate}.
 *
 * On JDK 21+, docx4j's {@code SAXHandler} cannot be used ("Transformer didn't set
 * ContentHandler"; unchanged in docx4j 17.0.3). Instead of failing fast, the template
 * logs a one-time WARN and transparently delegates to {@link WordprocessingMLDocxStAXTemplate}.
 * We test all branches: null template, real template, null/empty/non-empty variables.
 */
@DisplayName("WordprocessingMLDocxSaxTemplate Behavioral Tests")
class WordprocessingMLDocxSaxTemplateBehavioralTest {

    // ---------------------------------------------------------------
    // Constructor and getters/setters
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Default placeholder start is ${ and end is }")
    void defaultPlaceholders() {
        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        assertEquals("${", tpl.getPlaceholderStart());
        assertEquals("}", tpl.getPlaceholderEnd());
    }

    @Test
    @DisplayName("setPlaceholderStart stores custom start")
    void setPlaceholderStartCustom() {
        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        tpl.setPlaceholderStart("<<");
        assertEquals("<<", tpl.getPlaceholderStart());
    }

    @Test
    @DisplayName("setPlaceholderEnd stores custom end")
    void setPlaceholderEndCustom() {
        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        tpl.setPlaceholderEnd(">>");
        assertEquals(">>", tpl.getPlaceholderEnd());
    }

    // ---------------------------------------------------------------
    // process(File, Map) overloads
    // ---------------------------------------------------------------

    @Test
    @DisplayName("process(File, Map) with null file creates dummy document, null vars skip fallback")
    void processFileWithNullTemplateAndNullVars() throws Exception {
        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        WordprocessingMLPackage result = tpl.process((File) null, null);
        assertNotNull(result);
        assertNotNull(result.getMainDocumentPart());
    }

    @Test
    @DisplayName("process(File, Map) with null file and empty vars skips fallback")
    void processFileWithNullTemplateAndEmptyVars() throws Exception {
        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        Map<String, Object> vars = new HashMap<>();
        WordprocessingMLPackage result = tpl.process((File) null, vars);
        assertNotNull(result);
    }

    @Test
    @DisplayName("process(File, Map) with null file and non-empty vars transparently falls back on JDK 21+")
    void processFileWithNullTemplateAndVarsJdk21() throws Exception {
        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "value");

        // JDK 21+ => transparent StAX fallback; JDK < 21 => native SAX path; both succeed
        WordprocessingMLPackage result = tpl.process((File) null, vars);
        assertNotNull(result);
    }

    @Test
    @DisplayName("process(File, Map) with non-existent file treats it as null template")
    void processFileWithNonExistentFile() throws Exception {
        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        File nonExistent = new File("/nonexistent/path/template.docx");
        WordprocessingMLPackage result = tpl.process(nonExistent, null);
        assertNotNull(result);
    }

    @Test
    @DisplayName("process(File, Map) with directory (not a file) treats it as null template")
    void processFileWithDirectory(@TempDir java.nio.file.Path tempDir) throws Exception {
        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        WordprocessingMLPackage result = tpl.process(tempDir.toFile(), null);
        assertNotNull(result);
    }

    @Test
    @DisplayName("process(File, Map) with real template and null vars loads template")
    void processFileWithRealTemplateAndNullVars(@TempDir java.nio.file.Path tempDir) throws Exception {
        // Create a minimal docx file
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File templateFile = tempDir.resolve("template.docx").toFile();
        pkg.save(templateFile);

        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        WordprocessingMLPackage result = tpl.process(templateFile, null);
        assertNotNull(result);
        assertNotNull(result.getMainDocumentPart());
    }

    @Test
    @DisplayName("process(File, Map) with real template and empty vars loads template")
    void processFileWithRealTemplateAndEmptyVars(@TempDir java.nio.file.Path tempDir) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File templateFile = tempDir.resolve("template2.docx").toFile();
        pkg.save(templateFile);

        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        Map<String, Object> vars = new HashMap<>();
        WordprocessingMLPackage result = tpl.process(templateFile, vars);
        assertNotNull(result);
    }

    @Test
    @DisplayName("process(File, Map) with real template and non-empty vars transparently falls back on JDK 21+")
    void processFileWithRealTemplateAndVars(@TempDir java.nio.file.Path tempDir) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File templateFile = tempDir.resolve("template3.docx").toFile();
        pkg.save(templateFile);

        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        Map<String, Object> vars = new HashMap<>();
        vars.put("title", "Test");

        WordprocessingMLPackage result = tpl.process(templateFile, vars);
        assertNotNull(result);
    }

    // ---------------------------------------------------------------
    // process(InputStream, Map) overloads
    // ---------------------------------------------------------------

    @Test
    @DisplayName("process(InputStream, Map) with null stream creates dummy document")
    void processInputStreamWithNullStreamAndNullVars() throws Exception {
        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        WordprocessingMLPackage result = tpl.process((InputStream) null, null);
        assertNotNull(result);
    }

    @Test
    @DisplayName("process(InputStream, Map) with null stream and empty vars")
    void processInputStreamWithNullStreamAndEmptyVars() throws Exception {
        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        Map<String, Object> vars = new HashMap<>();
        WordprocessingMLPackage result = tpl.process((InputStream) null, vars);
        assertNotNull(result);
    }

    @Test
    @DisplayName("process(InputStream, Map) with null stream and non-empty vars transparently falls back on JDK 21+")
    void processInputStreamWithNullStreamAndVars() throws Exception {
        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        Map<String, Object> vars = new HashMap<>();
        vars.put("key", "val");

        WordprocessingMLPackage result = tpl.process((InputStream) null, vars);
        assertNotNull(result);
    }

    @Test
    @DisplayName("process(InputStream, Map) with real stream loads from InputStream")
    void processInputStreamWithRealStream(@TempDir java.nio.file.Path tempDir) throws Exception {
        // Create a docx, read it as InputStream
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File f = tempDir.resolve("stream-test.docx").toFile();
        pkg.save(f);
        byte[] bytes = java.nio.file.Files.readAllBytes(f.toPath());

        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        WordprocessingMLPackage result = tpl.process(new ByteArrayInputStream(bytes), null);
        assertNotNull(result);
    }

    @Test
    @DisplayName("process(InputStream, Map) with real stream and vars transparently falls back on JDK 21+")
    void processInputStreamWithRealStreamAndVars(@TempDir java.nio.file.Path tempDir) throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        File f = tempDir.resolve("stream-test2.docx").toFile();
        pkg.save(f);
        byte[] bytes = java.nio.file.Files.readAllBytes(f.toPath());

        WordprocessingMLDocxSaxTemplate tpl = new WordprocessingMLDocxSaxTemplate();
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "World");

        WordprocessingMLPackage result = tpl.process(new ByteArrayInputStream(bytes), vars);
        assertNotNull(result);
    }
}
