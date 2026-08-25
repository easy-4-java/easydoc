package io.github.easy4j.doc.beetl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Regression tests for the Beetl delimiter fix in
 * {@link WordprocessingMLBeetlTemplate#getInternalEngine()}.
 *
 * <p>The fix sets {@code placeholderEnd="}"} and {@code statementStart="<%"}
 * so that Beetl does not collide with docx4j's XML syntax. These tests verify
 * that the defaults are applied correctly and that custom overrides via
 * {@code Docx4jProperties} are honoured.</p>
 *
 * <p>Tests are ordered so that default-verification runs first (before any
 * property overrides), and custom-override tests run last. Each test creates
 * a fresh {@link WordprocessingMLBeetlTemplate} to avoid engine caching issues.</p>
 */
@DisplayName("Beetl delimiter regression tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WordprocessingMLBeetlCoverageTest {

    // ------------------------------------------------------------------
    // Default delimiter verification (run first, before any overrides)
    // ------------------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("Default placeholderEnd is '}'")
    void defaultPlaceholderEndIsClosingBrace() throws Exception {
        WordprocessingMLBeetlTemplate tpl = new WordprocessingMLBeetlTemplate();
        Configuration conf = tpl.getEngine().getConf();
        assertEquals("}", conf.getPlaceholderEnd(),
                "placeholderEnd must default to '}' to avoid XML collision");
    }

    @Test
    @Order(2)
    @DisplayName("Default statementStart is '<%'")
    void defaultStatementStartIsPercentAngle() throws Exception {
        WordprocessingMLBeetlTemplate tpl = new WordprocessingMLBeetlTemplate();
        Configuration conf = tpl.getEngine().getConf();
        assertEquals("<%", conf.getStatementStart(),
                "statementStart must default to '<%' to avoid XML collision");
    }

    @Test
    @Order(3)
    @DisplayName("Default placeholderStart is '${'")
    void defaultPlaceholderStartIsDollarBrace() throws Exception {
        WordprocessingMLBeetlTemplate tpl = new WordprocessingMLBeetlTemplate();
        Configuration conf = tpl.getEngine().getConf();
        assertEquals("${", conf.getPlaceholderStart());
    }

    @Test
    @Order(4)
    @DisplayName("Default statementEnd is '%>'")
    void defaultStatementEndIsPercentAngle() throws Exception {
        WordprocessingMLBeetlTemplate tpl = new WordprocessingMLBeetlTemplate();
        Configuration conf = tpl.getEngine().getConf();
        assertEquals("%>", conf.getStatementEnd());
    }

    // ------------------------------------------------------------------
    // End-to-end: ${name} syntax renders correctly with defaults
    // ------------------------------------------------------------------

    @Test
    @Order(5)
    @DisplayName("Template with ${name} placeholder renders correctly")
    void dollarBracePlaceholderRendersCorrectly() throws Exception {
        WordprocessingMLBeetlTemplate tpl = new WordprocessingMLBeetlTemplate();
        Map<String, Object> vars = Map.of("name", "BeetlDelimiterTest");
        WordprocessingMLPackage pkg = tpl.process("/tpl/hello.btl", vars);
        assertNotNull(pkg, "rendering a template with ${name} must produce a package");
        String xml = pkg.getMainDocumentPart().getXML();
        assertTrue(xml.contains("Hello BeetlDelimiterTest"),
                "rendered docx must contain the substituted value");
    }

    // ------------------------------------------------------------------
    // getEngine() caching: second call returns cached instance
    // ------------------------------------------------------------------

    @Test
    @Order(6)
    @DisplayName("getEngine() caches the GroupTemplate instance")
    void getEngineReturnsCachedInstance() throws Exception {
        WordprocessingMLBeetlTemplate tpl = new WordprocessingMLBeetlTemplate();
        GroupTemplate first = tpl.getEngine();
        GroupTemplate second = tpl.getEngine();
        assertNotNull(first);
        assertNotNull(second);
        assertTrue(first == second, "getEngine() must return the same cached instance");
    }

    @Test
    @Order(7)
    @DisplayName("setEngine() overrides the cached instance")
    void setEngineOverridesCache() throws Exception {
        WordprocessingMLBeetlTemplate tpl = new WordprocessingMLBeetlTemplate();
        GroupTemplate original = tpl.getEngine();
        tpl.setEngine(original);
        GroupTemplate retrieved = tpl.getEngine();
        assertTrue(original == retrieved, "setEngine then getEngine must return the set instance");
    }

    // ------------------------------------------------------------------
    // Custom delimiter override via Docx4jProperties (run last)
    // ------------------------------------------------------------------

    @Test
    @Order(8)
    @DisplayName("Custom placeholderStart/End override defaults")
    void customPlaceholderDelimitersOverrideDefaults() throws Exception {
        try {
            Docx4jProperties.setProperty("docx4j.beetl.placeholderStart", "<<");
            Docx4jProperties.setProperty("docx4j.beetl.placeholderEnd", ">>");
            // Fresh template so getInternalEngine reads the new properties
            WordprocessingMLBeetlTemplate tpl = new WordprocessingMLBeetlTemplate();
            Configuration conf = tpl.getEngine().getConf();
            assertEquals("<<", conf.getPlaceholderStart(),
                    "custom placeholderStart must be honoured");
            assertEquals(">>", conf.getPlaceholderEnd(),
                    "custom placeholderEnd must be honoured");
        } finally {
            // Reset to defaults (no remove API, so re-set to defaults)
            Docx4jProperties.setProperty("docx4j.beetl.placeholderStart", "${");
            Docx4jProperties.setProperty("docx4j.beetl.placeholderEnd", "}");
        }
    }

    @Test
    @Order(9)
    @DisplayName("Custom statementStart/End override defaults")
    void customStatementDelimitersOverrideDefaults() throws Exception {
        try {
            Docx4jProperties.setProperty("docx4j.beetl.statementStart", "<!--");
            Docx4jProperties.setProperty("docx4j.beetl.statementEnd", "-->");
            WordprocessingMLBeetlTemplate tpl = new WordprocessingMLBeetlTemplate();
            Configuration conf = tpl.getEngine().getConf();
            assertEquals("<!--", conf.getStatementStart(),
                    "custom statementStart must be honoured");
            assertEquals("-->", conf.getStatementEnd(),
                    "custom statementEnd must be honoured");
        } finally {
            Docx4jProperties.setProperty("docx4j.beetl.statementStart", "<%");
            Docx4jProperties.setProperty("docx4j.beetl.statementEnd", "%>");
        }
    }

    /**
     * Exercises the EngineFactory DCL short-circuit: consecutive calls to
     * {@code factory.get()} must return the same instance without re-entering
     * the synchronized block.
     */
    @Test
    @Order(10)
    @DisplayName("EngineFactory.get() returns same cached instance on consecutive calls")
    void engineFactoryReturnsSameInstanceOnConsecutiveCalls() throws Exception {
        EngineFactory factory = new EngineFactory();
        GroupTemplate first = factory.get();
        assertNotNull(first);
        GroupTemplate second = factory.get();
        assertSame(first, second, "EngineFactory.get() must return the same cached instance");
    }

    /**
     * Exercises the Renderer statelessness guarantee: rendering the same
     * template with the same variables twice through the same Renderer
     * instance must produce identical results.
     */
    @Test
    @Order(11)
    @DisplayName("Renderer produces identical results for same inputs")
    void rendererProducesIdenticalResultsForSameInputs() throws Exception {
        Renderer renderer = new Renderer();
        WordprocessingMLBeetlTemplate tpl = new WordprocessingMLBeetlTemplate();
        GroupTemplate engine = tpl.getEngine();
        Map<String, Object> vars = Map.of("name", "world");
        String first = renderer.render("/tpl/hello.btl", vars, engine);
        assertNotNull(first);
        String second = renderer.render("/tpl/hello.btl", vars, engine);
        assertEquals(first, second, "Renderer must be stateless: same inputs produce same output");
    }
}
