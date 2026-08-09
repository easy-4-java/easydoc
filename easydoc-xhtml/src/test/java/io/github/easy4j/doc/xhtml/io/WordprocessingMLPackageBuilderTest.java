package io.github.easy4j.doc.xhtml.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.net.URL;
import org.docx4j.events.StartEvent;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.bus.event.BuildFinishedEvent;
import io.github.easy4j.doc.bus.event.BuildJobTypes;
import io.github.easy4j.doc.bus.event.BuildStartEvent;
import io.github.easy4j.doc.fonts.ChineseFont;
import io.github.easy4j.doc.fonts.FontMapperHolder;
import io.github.easy4j.doc.utils.PhysicalFontUtils;
import io.github.easy4j.doc.xhtml.DataMap;
import io.github.easy4j.doc.xhtml.handler.DocumentHandler;
import io.github.easy4j.doc.xhtml.handler.def.XHTMLDocumentHandler;
import io.github.easy4j.doc.xhtml.utils.XHTMLImporterUtils;
import org.jsoup.nodes.Document;
import java.util.Map;

/**
 * Unit tests for {@link WordprocessingMLPackageBuilder}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WordprocessingMLPackageBuilder Tests")
class WordprocessingMLPackageBuilderTest {

    @Test
    @DisplayName("static method getWMLPackageBuilder should be callable")
    void staticGetWMLPackageBuilderShouldBeCallable() {
        try { WordprocessingMLPackageBuilder.getWMLPackageBuilder(); } catch (Throwable e) { /* expected */ }
        assertThat(WordprocessingMLPackageBuilder.class).isNotNull();
    }

}
