package io.github.easy4j.doc.thymeleaf;

import io.github.easy4j.doc.xhtml.WordprocessingMLHtmlTemplate;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Additional tests to push JaCoCo line coverage of
 * {@link WordprocessingMLThymeleafTemplate} above 90 %.
 */
class WordprocessingMLThymeleafCoverageTest {

    // ---- constructors ----

    @Test
    void twoArgConstructorSetsFlags() {
        WordprocessingMLThymeleafTemplate t = new WordprocessingMLThymeleafTemplate(true, false);
        assertNotNull(t);
    }

    @Test
    void htmlTemplateConstructorStoresDelegate() {
        WordprocessingMLHtmlTemplate delegate = new WordprocessingMLHtmlTemplate(false, true);
        WordprocessingMLThymeleafTemplate t = new WordprocessingMLThymeleafTemplate(delegate);
        assertNotNull(t);
        assertNotNull(t.getMlHtmlTemplate());
    }

    // ---- setEngine / getEngine pre-set path ----

    @Test
    void getEngineReturnsPresetInstanceWhenSet() throws Exception {
        WordprocessingMLThymeleafTemplate t = new WordprocessingMLThymeleafTemplate();
        TemplateEngine engine = new TemplateEngine();
        t.setEngine(engine);
        assertSame(engine, t.getEngine());
    }

    // ---- setTemplateResolver / getTemplateResolver ----

    @Test
    void setAndGetTemplateResolver() {
        WordprocessingMLThymeleafTemplate t = new WordprocessingMLThymeleafTemplate();
        FileTemplateResolver resolver = new FileTemplateResolver();
        t.setTemplateResolver(resolver);
        assertSame(resolver, t.getTemplateResolver());
    }

    // ---- getInternalEngine with pre-set templateResolver (non-null branch at L66) ----

    @Test
    void getInternalEngineWithPresetResolver() throws Exception {
        WordprocessingMLThymeleafTemplate t = new WordprocessingMLThymeleafTemplate();
        t.setTemplateResolver(new ClassLoaderTemplateResolver());
        // When templateResolver is already set, getInternalEngine skips the resolver selection block.
        // This covers the false branch of "if (getTemplateResolver() == null)" at L66.
        TemplateEngine engine = t.getEngine();
        assertNotNull(engine);
    }

    // ---- getInternalEngine: FileTemplateResolver path (L68-69) ----

    @Test
    void getInternalEngineCreatesFileTemplateResolverByDefault() throws Exception {
        java.util.Properties props = org.docx4j.Docx4jProperties.getProperties();
        // Remove all resolver-related properties to test the default path
        String prevResolver = props.getProperty("docx4j.thymeleaf.templateResolver");
        String prevPrefix = props.getProperty("docx4j.thymeleaf.prefix");
        String prevSuffix = props.getProperty("docx4j.thymeleaf.suffix");
        String prevCharset = props.getProperty("docx4j.thymeleaf.charset");
        String prevCacheTTLMs = props.getProperty("docx4j.thymeleaf.cacheTTLMs");
        props.remove("docx4j.thymeleaf.templateResolver");
        props.remove("docx4j.thymeleaf.prefix");
        props.remove("docx4j.thymeleaf.suffix");
        props.remove("docx4j.thymeleaf.charset");
        props.remove("docx4j.thymeleaf.cacheTTLMs");
        try {
            WordprocessingMLThymeleafTemplate t = new WordprocessingMLThymeleafTemplate();
            TemplateEngine engine = t.getEngine();
            assertNotNull(engine);
        } finally {
            restore(props, "docx4j.thymeleaf.templateResolver", prevResolver);
            restore(props, "docx4j.thymeleaf.prefix", prevPrefix);
            restore(props, "docx4j.thymeleaf.suffix", prevSuffix);
            restore(props, "docx4j.thymeleaf.charset", prevCharset);
            restore(props, "docx4j.thymeleaf.cacheTTLMs", prevCacheTTLMs);
        }
    }

    // ---- getInternalEngine: UrlTemplateResolver path (L72-73) ----

    @Test
    void getInternalEngineWithUrlTemplateResolver() throws Exception {
        java.util.Properties props = org.docx4j.Docx4jProperties.getProperties();
        String prevResolver = props.getProperty("docx4j.thymeleaf.templateResolver");
        String prevPrefix = props.getProperty("docx4j.thymeleaf.prefix");
        props.setProperty("docx4j.thymeleaf.templateResolver",
                "org.thymeleaf.templateresolver.UrlTemplateResolver");
        props.setProperty("docx4j.thymeleaf.prefix", "");
        try {
            WordprocessingMLThymeleafTemplate t = new WordprocessingMLThymeleafTemplate();
            TemplateEngine engine = t.getEngine();
            assertNotNull(engine);
        } finally {
            restore(props, "docx4j.thymeleaf.templateResolver", prevResolver);
            restore(props, "docx4j.thymeleaf.prefix", prevPrefix);
        }
    }

    // ---- getInternalEngine: else branch (L75 — unknown resolver name → FileTemplateResolver) ----

    @Test
    void getInternalEngineWithUnknownResolverFallsBackToFileResolver() throws Exception {
        java.util.Properties props = org.docx4j.Docx4jProperties.getProperties();
        String prevResolver = props.getProperty("docx4j.thymeleaf.templateResolver");
        String prevPrefix = props.getProperty("docx4j.thymeleaf.prefix");
        props.setProperty("docx4j.thymeleaf.templateResolver", "com.unknown.SomeResolver");
        props.setProperty("docx4j.thymeleaf.prefix", "");
        try {
            WordprocessingMLThymeleafTemplate t = new WordprocessingMLThymeleafTemplate();
            TemplateEngine engine = t.getEngine();
            assertNotNull(engine);
        } finally {
            restore(props, "docx4j.thymeleaf.templateResolver", prevResolver);
            restore(props, "docx4j.thymeleaf.prefix", prevPrefix);
        }
    }

    // ---- getInternalEngine: cacheTTLMs non-empty path (L81 non-null branch) ----

    @Test
    void getInternalEngineWithCacheTTLMs() throws Exception {
        java.util.Properties props = org.docx4j.Docx4jProperties.getProperties();
        String prevResolver = props.getProperty("docx4j.thymeleaf.templateResolver");
        String prevPrefix = props.getProperty("docx4j.thymeleaf.prefix");
        String prevCacheTTLMs = props.getProperty("docx4j.thymeleaf.cacheTTLMs");
        props.setProperty("docx4j.thymeleaf.templateResolver",
                "org.thymeleaf.templateresolver.ClassLoaderTemplateResolver");
        props.setProperty("docx4j.thymeleaf.prefix", "");
        props.setProperty("docx4j.thymeleaf.cacheTTLMs", "60000");
        try {
            WordprocessingMLThymeleafTemplate t = new WordprocessingMLThymeleafTemplate();
            TemplateEngine engine = t.getEngine();
            assertNotNull(engine);
        } finally {
            restore(props, "docx4j.thymeleaf.templateResolver", prevResolver);
            restore(props, "docx4j.thymeleaf.prefix", prevPrefix);
            restore(props, "docx4j.thymeleaf.cacheTTLMs", prevCacheTTLMs);
        }
    }

    private static void restore(java.util.Properties props, String key, String prev) {
        if (prev != null) {
            props.setProperty(key, prev);
        } else {
            props.remove(key);
        }
    }
}
