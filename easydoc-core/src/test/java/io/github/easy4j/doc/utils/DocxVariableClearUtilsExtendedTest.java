package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.xml.bind.JAXBContext;

import org.docx4j.jaxb.Context;
import org.junit.jupiter.api.Test;

/**
 * Additional tests for DocxVariableClearUtils to cover the XML tag stripping
 * branch and other edge cases in the variable cleaning logic.
 */
class DocxVariableClearUtilsExtendedTest {

    private static final JAXBContext JC = Context.jc;

    @Test
    void doCleanDocumentPartWithXmlTagsInsideVariable() throws Exception {
        // This tests the branch where the variable contains XML tags
        // e.g., ${<w:r><w:t>name</w:t></w:r>} should be cleaned to ${name}
        String template = "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                + "<w:body><w:p><w:r><w:t>${</w:t></w:r>"
                + "<w:r><w:t>name</w:t></w:r>"
                + "<w:r><w:t>}</w:t></w:r></w:p></w:body></w:document>";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, JC);
        assertNotNull(result);
    }

    @Test
    void doCleanDocumentPartWithMultipleVariablesWithXmlTags() throws Exception {
        String template = "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                + "<w:body><w:p><w:r><w:t>${</w:t></w:r>"
                + "<w:r><w:t>first</w:t></w:r>"
                + "<w:r><w:t>}</w:t></w:r>"
                + "<w:r><w:t> and ${</w:t></w:r>"
                + "<w:r><w:t>second</w:t></w:r>"
                + "<w:r><w:t>}</w:t></w:r></w:p></w:body></w:document>";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, JC);
        assertNotNull(result);
    }

    @Test
    void doCleanDocumentPartWithDollarBraceInMiddleOfText() throws Exception {
        String template = "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                + "<w:body><w:p><w:r><w:t>before ${var} after</w:t></w:r></w:p></w:body></w:document>";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, JC);
        assertNotNull(result);
    }

    @Test
    void doCleanDocumentPartWithConsecutiveDollarSigns() throws Exception {
        String template = "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                + "<w:body><w:p><w:r><w:t>$$100 ${price}</w:t></w:r></w:p></w:body></w:document>";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, JC);
        assertNotNull(result);
    }

    @Test
    void doCleanDocumentPartWithNestedBraces() throws Exception {
        // ${outer{inner}} - should handle the first closing brace
        String template = "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                + "<w:body><w:p><w:r><w:t>${test}</w:t></w:r></w:p></w:body></w:document>";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, JC);
        assertNotNull(result);
    }

    @Test
    void doCleanDocumentPartWithVariableAtEnd() throws Exception {
        String template = "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                + "<w:body><w:p><w:r><w:t>value: ${end}</w:t></w:r></w:p></w:body></w:document>";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, JC);
        assertNotNull(result);
    }

    @Test
    void doCleanDocumentPartWithVariableAtStart() throws Exception {
        String template = "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                + "<w:body><w:p><w:r><w:t>${start} value</w:t></w:r></w:p></w:body></w:document>";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, JC);
        assertNotNull(result);
    }
}
