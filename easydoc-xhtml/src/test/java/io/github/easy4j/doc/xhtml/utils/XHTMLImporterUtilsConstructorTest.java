package io.github.easy4j.doc.xhtml.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Cover line 32 of {@link XHTMLImporterUtils}: the implicit default constructor.
 * JaCoCo tracks the constructor separately from the static {@code handle()} method;
 * creating an instance ensures the class-declaration line is marked covered.
 */
class XHTMLImporterUtilsConstructorTest {

    @Test
    void defaultConstructorCreatesInstance() {
        XHTMLImporterUtils instance = new XHTMLImporterUtils();
        assertNotNull(instance, "XHTMLImporterUtils default constructor must succeed");
    }
}
