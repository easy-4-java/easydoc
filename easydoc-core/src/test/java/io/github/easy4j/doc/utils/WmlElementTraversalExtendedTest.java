package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.junit.jupiter.api.Test;

/**
 * Additional tests for WmlElementTraversal to cover getElementContent
 * and getChildrenElements with ContentAccessor source.
 */
class WmlElementTraversalExtendedTest {

    @Test
    void getElementContentExtractsTextFromParagraph() throws Exception {
        P p = new P();
        R r = new R();
        Text t = new Text();
        t.setValue("hello world");
        r.getContent().add(t);
        p.getContent().add(r);

        String content = WmlElementTraversal.getElementContent(p);
        assertNotNull(content);
        assertTrue(content.contains("hello world"));
    }

    @Test
    void getElementContentOnEmptyParagraph() throws Exception {
        P p = new P();
        String content = WmlElementTraversal.getElementContent(p);
        assertNotNull(content);
    }

    @Test
    void getChildrenElementsWithContentAccessorSource() {
        // A Tbl containing a Tr - when we ask for children of type Tr,
        // the ContentAccessor path returns the content list
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        tbl.getContent().add(tr);

        // Tbl is a ContentAccessor, its content is List<Object>
        List<Tr> result = WmlElementTraversal.getChildrenElements(tbl, Tr.class);
        assertNotNull(result);
    }

    @Test
    void getTargetElementsWalksDeeply() {
        // Nested structure: Tbl -> Tr -> Tc -> P -> R -> Text
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc = new Tc();
        P p = new P();
        R r = new R();
        Text t = new Text();
        t.setValue("deep");
        r.getContent().add(t);
        p.getContent().add(r);
        tc.getContent().add(p);
        tr.getContent().add(tc);
        tbl.getContent().add(tr);

        List<Text> texts = WmlElementTraversal.getTargetElements(tbl, Text.class);
        assertNotNull(texts);
        assertEquals(1, texts.size());
        assertEquals("deep", texts.get(0).getValue());
    }

    @Test
    void getAllElementFromObjectWalksIntoContentAccessor() {
        Tbl tbl = new Tbl();
        Tr tr = new Tr();
        Tc tc = new Tc();
        P p = new P();
        tr.getContent().add(tc);
        tc.getContent().add(p);
        tbl.getContent().add(tr);

        List<Object> result = WmlElementTraversal.getAllElementFromObject(tbl, P.class);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllElementFromObjectWithJAXBElement() {
        // Test the JAXBElement unwrap path
        jakarta.xml.bind.JAXBElement<RPr> jaxbElement =
                new jakarta.xml.bind.JAXBElement<>(
                        new javax.xml.namespace.QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPr"),
                        RPr.class,
                        new RPr());
        List<Object> result = WmlElementTraversal.getAllElementFromObject(jaxbElement, RPr.class);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getTargetElementsReturnsMultipleMatches() {
        Tbl tbl = new Tbl();
        Tr tr1 = new Tr();
        Tr tr2 = new Tr();
        tbl.getContent().add(tr1);
        tbl.getContent().add(tr2);

        List<Tr> result = WmlElementTraversal.getTargetElements(tbl, Tr.class);
        assertEquals(2, result.size());
    }
}
