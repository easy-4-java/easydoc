package io.github.easy4j.doc.thymeleaf;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import org.docx4j.Docx4jProperties;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

/**
 * Tests for EngineFactory DCL short-circuit and engine caching.
 */
@DisplayName("Thymeleaf EngineFactory + caching tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WordprocessingMLThymeleafCoverageTest {

    @Test
    @Order(1)
    @DisplayName("EngineFactory class is final and instantiable")
    void engineFactoryClassExists() {
        assertTrue(java.lang.reflect.Modifier.isFinal(EngineFactory.class.getModifiers()),
                "EngineFactory must be final");
    }

    @Test
    @Order(2)
    @DisplayName("EngineFactory.get() returns non-null instance")
    void engineFactoryReturnsNonNull() throws Exception {
        EngineFactory factory = new EngineFactory(null);
        TemplateEngine engine = factory.get();
        assertNotNull(engine, "EngineFactory.get() must return a non-null engine");
    }

    @Test
    @Order(3)
    @DisplayName("EngineFactory.get() returns same cached instance on consecutive calls")
    void engineFactoryReturnsSameInstanceOnConsecutiveCalls() throws Exception {
        EngineFactory factory = new EngineFactory(null);
        TemplateEngine first = factory.get();
        assertNotNull(first);
        TemplateEngine second = factory.get();
        assertSame(first, second, "EngineFactory.get() must return the same cached instance");
    }

    @Test
    @Order(4)
    @DisplayName("getEngine() caches the engine instance")
    void getEngineCachesInstance() throws Exception {
        WordprocessingMLThymeleafTemplate tpl = new WordprocessingMLThymeleafTemplate();
        TemplateEngine first = tpl.getEngine();
        TemplateEngine second = tpl.getEngine();
        assertNotNull(first);
        assertSame(first, second, "getEngine() must return the same cached instance");
    }

    @Test
    @Order(5)
    @DisplayName("setEngine() overrides the cached instance")
    void setEngineOverridesCache() throws Exception {
        WordprocessingMLThymeleafTemplate tpl = new WordprocessingMLThymeleafTemplate();
        TemplateEngine original = tpl.getEngine();
        tpl.setEngine(original);
        TemplateEngine retrieved = tpl.getEngine();
        assertSame(original, retrieved, "setEngine then getEngine must return the set instance");
    }

    @Test
    @Order(6)
    @DisplayName("unknown templateResolver class degrades to FileTemplateResolver (with WARN log)")
    void unknownResolverDegradesToFileTemplateResolver() throws Exception {
        Properties global = Docx4jProperties.getProperties();
        Properties backup = new Properties();
        backup.putAll(global);
        try {
            // 未知解析器类名：不得抛异常，也不得静默无痕——应记录 WARN 后降级为 FileTemplateResolver
            Docx4jProperties.setProperty("docx4j.thymeleaf.templateResolver", "com.example.UnknownResolver");
            EngineFactory factory = new EngineFactory(null);
            TemplateEngine engine = factory.get();
            assertNotNull(engine, "engine must still initialize for an unknown resolver class");
            // 引擎仅注册工厂构建的解析器，因此集合中应有且仅有一个 FileTemplateResolver
            assertTrue(engine.getConfiguration().getTemplateResolvers().iterator().next() instanceof FileTemplateResolver,
                    "unknown resolver must degrade to FileTemplateResolver");
            assertSame(engine, factory.get(), "factory.get() must return the same cached instance");
        } finally {
            // 恢复全局属性，避免污染同一 JVM 中运行的其他测试
            global.clear();
            global.putAll(backup);
        }
    }

    @Test
    @Order(7)
    @DisplayName("case-insensitive resolver matching: URL resolver FQN in upper case is honored")
    void resolverMatchingIsCaseInsensitive() throws Exception {
        Properties global = Docx4jProperties.getProperties();
        Properties backup = new Properties();
        backup.putAll(global);
        try {
            // 大小写不敏感匹配（aef12ea 语义）：非标准大小写的类名同样生效，而不是降级为 FileTemplateResolver
            Docx4jProperties.setProperty("docx4j.thymeleaf.templateResolver",
                    "org.thymeleaf.templateresolver.URLTEMPLATERESOLVER");
            EngineFactory factory = new EngineFactory(null);
            TemplateEngine engine = factory.get();
            assertNotNull(engine);
            assertTrue(!(engine.getConfiguration().getTemplateResolvers().iterator().next() instanceof FileTemplateResolver),
                    "case-insensitive match must honor the requested resolver type");
        } finally {
            // 恢复全局属性，避免污染同一 JVM 中运行的其他测试
            global.clear();
            global.putAll(backup);
        }
    }
}
