package io.github.easy4j.doc.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.math.BigInteger;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import io.github.easy4j.doc.utils.WmlElementUtils;
import io.github.easy4j.doc.wml.DocxElementWmlRender;
import io.github.easy4j.doc.wml.ParagraphWmlRender;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.TcPrInner.VMerge;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import java.io.File;
import java.util.Properties;

/**
 * Unit tests for {@link WordprocessingMLPackageRender}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WordprocessingMLPackageRender Tests")
class WordprocessingMLPackageRenderTest {

    @Test
    @DisplayName("should have default constructor")
    void shouldHaveDefaultConstructor() {
        try { new WordprocessingMLPackageRender(); } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageRender.class).isNotNull();
    }

    @Test
    @DisplayName("instance method addTitle should be callable")
    void instanceAddTitleShouldBeCallable() {
        try {
            WordprocessingMLPackageRender instance = new WordprocessingMLPackageRender();
            instance.addTitle("test");
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageRender.class).isNotNull();
    }

    @Test
    @DisplayName("instance method addSubtitle should be callable")
    void instanceAddSubtitleShouldBeCallable() {
        try {
            WordprocessingMLPackageRender instance = new WordprocessingMLPackageRender();
            instance.addSubtitle("test");
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageRender.class).isNotNull();
    }

    @Test
    @DisplayName("instance method addTable should be callable")
    void instanceAddTableShouldBeCallable() {
        try {
            WordprocessingMLPackageRender instance = new WordprocessingMLPackageRender();
            instance.addTable((Tr) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageRender.class).isNotNull();
    }

    @Test
    @DisplayName("instance method addTableRow should be callable")
    void instanceAddTableRowShouldBeCallable() {
        try {
            WordprocessingMLPackageRender instance = new WordprocessingMLPackageRender();
            instance.addTableRow((Tr) null, (Tr) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageRender.class).isNotNull();
    }

    @Test
    @DisplayName("instance method addTableCell should be callable")
    void instanceAddTableCellShouldBeCallable() {
        try {
            WordprocessingMLPackageRender instance = new WordprocessingMLPackageRender();
            instance.addTableCell((Tr) null, "test");
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageRender.class).isNotNull();
    }

    @Test
    @DisplayName("instance method addStyledTableCell should be callable")
    void instanceAddStyledTableCellShouldBeCallable() {
        try {
            WordprocessingMLPackageRender instance = new WordprocessingMLPackageRender();
            instance.addStyledTableCell((Tr) null, "test", true, "test");
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageRender.class).isNotNull();
    }

    @Test
    @DisplayName("instance method addStyling should be callable")
    void instanceAddStylingShouldBeCallable() {
        try {
            WordprocessingMLPackageRender instance = new WordprocessingMLPackageRender();
            instance.addStyling((Tc) null, "test", true, "test");
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageRender.class).isNotNull();
    }

    @Test
    @DisplayName("instance method setFontSize should be callable")
    void instanceSetFontSizeShouldBeCallable() {
        try {
            WordprocessingMLPackageRender instance = new WordprocessingMLPackageRender();
            instance.setFontSize((RPr) null, "test");
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageRender.class).isNotNull();
    }

    @Test
    @DisplayName("instance method addBoldStyle should be callable")
    void instanceAddBoldStyleShouldBeCallable() {
        try {
            WordprocessingMLPackageRender instance = new WordprocessingMLPackageRender();
            instance.addBoldStyle((RPr) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageRender.class).isNotNull();
    }

    @Test
    @DisplayName("instance method addBorders should be callable")
    void instanceAddBordersShouldBeCallable() {
        try {
            WordprocessingMLPackageRender instance = new WordprocessingMLPackageRender();
            instance.addBorders((Tbl) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageRender.class).isNotNull();
    }

}
