/*
 * Copyright (c) 2018, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.easy4j.doc.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * WordprocessingMLPackageWriter has no static entry point that does not
 * require a non-null {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage}
 * or a file path that triggers {@code WordprocessingMLPackage.load(File)}.
 * The simple singletons we can still verify are below.
 */
class WordprocessingMLPackageWriterTest {

    @Test
    void getWMLPackageWriterReturnsInstance() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertNotNull(writer);
    }

    @Test
    void getHyperlinkHandlerReturnsHandler() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertNotNull(writer.getHyperlinkHandler());
    }

    @Test
    void getStyleElementHandlerReturnsHandler() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertNotNull(writer.getStyleElementHandler());
    }

    @Test
    void getScriptElementHandlerReturnsHandler() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        assertNotNull(writer.getScriptElementHandler());
    }

    @Test
    void setHyperlinkHandlerStoresValue() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        org.docx4j.convert.out.ConversionHyperlinkHandler original = writer.getHyperlinkHandler();
        writer.setHyperlinkHandler(original);
        assertNotNull(writer.getHyperlinkHandler());
    }

    @Test
    void setStyleElementHandlerStoresValue() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        org.docx4j.convert.out.ConversionHTMLStyleElementHandler original = writer.getStyleElementHandler();
        writer.setStyleElementHandler(original);
        assertNotNull(writer.getStyleElementHandler());
    }

    @Test
    void setScriptElementHandlerStoresValue() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        org.docx4j.convert.out.ConversionHTMLScriptElementHandler original = writer.getScriptElementHandler();
        writer.setScriptElementHandler(original);
        assertNotNull(writer.getScriptElementHandler());
    }

    @Test
    void writeToDocxRejectsNullPackage() {
        WordprocessingMLPackageWriter writer = WordprocessingMLPackageWriter.getWMLPackageWriter();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            writer.writeToDocx(null, new java.io.File("dummy.docx"));
        });
    }
}
