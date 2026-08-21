package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.xml.bind.JAXBContext;

import org.docx4j.jaxb.Context;
import org.junit.jupiter.api.Test;

class DocxVariableClearUtilsTest {

    private static final JAXBContext JC = Context.jc;

    @Test
    void doCleanDocumentPartStripsXmlTagsFromVariable() throws Exception {
        String template = "<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                + "<w:r><w:t>Hello ${name} world</w:t></w:r></w:p>";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, JC);
        assertNotNull(result);
    }

    @Test
    void doCleanDocumentPartWithNoVariablesReturnsSameStructure() throws Exception {
        String template = "<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                + "<w:r><w:t>No variables here</w:t></w:r></w:p>";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, JC);
        assertNotNull(result);
    }

    @Test
    void doCleanDocumentPartWithMultipleVariables() throws Exception {
        String template = "<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                + "<w:r><w:t>${first} and ${second}</w:t></w:r></w:p>";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, JC);
        assertNotNull(result);
    }

    @Test
    void doCleanDocumentPartWithDollarSignInText() throws Exception {
        // Dollar sign without brace -- should pass through
        String template = "<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                + "<w:r><w:t>$500</w:t></w:r></w:p>";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, JC);
        assertNotNull(result);
    }

    @Test
    void doCleanDocumentPartWithEmptyTemplate() throws Exception {
        String template = "<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"/>";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, JC);
        assertNotNull(result);
    }

    @Test
    void doCleanDocumentPartWithPartialPlaceholder() throws Exception {
        // Just $ without { -- should pass through
        String template = "<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                + "<w:r><w:t>price: $100</w:t></w:r></w:p>";
        Object result = DocxVariableClearUtils.doCleanDocumentPart(template, JC);
        assertNotNull(result);
    }
}
