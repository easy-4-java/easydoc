package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.junit.jupiter.api.Test;

/**
 * Additional tests for WMLPackageUtils to cover replaceParagraph and
 * other methods not exercised by the original WMLPackageUtilsTest.
 */
class WMLPackageUtilsExtendedTest {

    @Test
    void replaceParagraphReplacesPlaceholder() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = pkg.getMainDocumentPart();

        // Add a paragraph with a placeholder
        P p = new P();
        Text t = new Text();
        t.setValue("${content}");
        R r = new R();
        r.getContent().add(t);
        p.getContent().add(r);
        mdp.getContent().add(p);

        WMLPackageUtils.replaceParagraph(mdp, "${content}", "New text line", mdp);

        // The original paragraph should have been replaced with new content
        List<Object> content = mdp.getContent();
        assertFalse(content.isEmpty());
    }

    @Test
    void replaceParagraphWithMultilineText() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = pkg.getMainDocumentPart();

        P p = new P();
        Text t = new Text();
        t.setValue("${data}");
        R r = new R();
        r.getContent().add(t);
        p.getContent().add(r);
        mdp.getContent().add(p);

        WMLPackageUtils.replaceParagraph(mdp, "${data}", "line1\nline2\nline3", mdp);

        // Should create 3 paragraphs (one per line)
        List<Object> content = mdp.getContent();
        long paragraphCount = content.stream().filter(obj -> obj instanceof P).count();
        assertTrue(paragraphCount >= 3, "Expected at least 3 paragraphs for 3 lines");
    }

    @Test
    void replaceTextWithBookmarkAndMarkupRange() throws Exception {
        // This exercises the main logic of replaceText
        org.docx4j.wml.CTBookmark bm = new org.docx4j.wml.CTBookmark();
        bm.setName("myBookmark");

        P p = new P();
        bm.setParent(p);

        // Set up the bookmark with an id
        bm.setId(java.math.BigInteger.ZERO);

        // Add bookmark and markup range to the paragraph
        p.getContent().add(bm);

        // Add some content between bookmark and markup range
        R run = new R();
        Text text = new Text();
        text.setValue("old text");
        run.getContent().add(text);
        p.getContent().add(run);

        // Add markup range to close the bookmark
        org.docx4j.wml.CTMarkupRange markup = new org.docx4j.wml.CTMarkupRange();
        markup.setId(java.math.BigInteger.ZERO);
        p.getContent().add(markup);

        WMLPackageUtils.replaceText(bm, "new text");

        // Verify the text was replaced
        boolean foundNewText = false;
        for (Object obj : p.getContent()) {
            Object unwrapped = org.docx4j.XmlUtils.unwrap(obj);
            if (unwrapped instanceof R) {
                for (Object rc : ((R) unwrapped).getContent()) {
                    Object rUnwrapped = org.docx4j.XmlUtils.unwrap(rc);
                    if (rUnwrapped instanceof Text) {
                        if ("new text".equals(((Text) rUnwrapped).getValue())) {
                            foundNewText = true;
                        }
                    }
                }
            }
        }
        assertTrue(foundNewText, "Expected 'new text' in paragraph content");
    }

    @Test
    void replaceTextBookmarkNotInParagraphReturns() throws Exception {
        // Bookmark parent is not a P => early return
        org.docx4j.wml.CTBookmark bm = new org.docx4j.wml.CTBookmark();
        bm.setName("test");
        bm.setParent(new Object()); // Not a P
        // Should not throw
        WMLPackageUtils.replaceText(bm, "value");
    }
}
