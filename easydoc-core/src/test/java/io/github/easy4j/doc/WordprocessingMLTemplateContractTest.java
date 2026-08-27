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
package io.github.easy4j.doc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for the default methods of the {@link WordprocessingMLTemplate} interface.
 *
 * <p>Covers all three {@code process()} overloads for both null-input (create dummy
 * document) and non-null-input (load existing document) branches.
 *
 * <p>Historical note: null-template paths call {@code SampleDocument.createContent()}
 * which invokes {@code PhysicalFonts.discoverPhysicalFonts()}; on some macOS
 * environments that used to throw {@code AssertionError} past the production
 * {@code catch (Exception)} guard. Production now guards with
 * {@code catch (Throwable)}, so these tests assert unconditionally again —
 * the former catch(AssertionError) vacuous passes are removed (audit #17).</p>
 */
@DisplayName("WordprocessingMLTemplate interface default methods")
class WordprocessingMLTemplateContractTest {

    private static final String TEMPLATE_DOCX = "src/test/resources/tpl/template.docx";

    /** Minimal concrete implementation to exercise the interface defaults. */
    private final WordprocessingMLTemplate template = new WordprocessingMLTemplate() {};

    private final Map<String, Object> emptyVars = new HashMap<>();

    // --- process(File, Map) ---

    @Test
    @DisplayName("process(File,null template) creates a non-null package")
    void processFile_nullTemplate_createsPackage() throws Exception {
        WordprocessingMLPackage result = template.process((File) null, emptyVars);
        assertThat(result).isNotNull();
        assertThat(result.getMainDocumentPart()).isNotNull();
    }

    @Test
    @DisplayName("process(File,non-existent file) creates a dummy document")
    void processFile_nonExistentFile_createsDummy() throws Exception {
        File ghost = new File("/tmp/does_not_exist_42.docx");
        WordprocessingMLPackage result = template.process(ghost, emptyVars);
        assertThat(result).isNotNull();
        assertThat(result.getMainDocumentPart()).isNotNull();
    }

    @Test
    @DisplayName("process(File,existing file) loads the docx")
    void processFile_existingFile_loadsSuccessfully() throws Exception {
        File source = new File(TEMPLATE_DOCX);
        WordprocessingMLPackage result = template.process(source, emptyVars);
        assertThat(result).isNotNull();
        assertThat(result.getMainDocumentPart()).isNotNull();
    }

    // --- process(InputStream, Map) ---

    @Test
    @DisplayName("process(InputStream,null stream) creates a non-null package")
    void processStream_nullStream_createsPackage() throws Exception {
        WordprocessingMLPackage result = template.process((InputStream) null, emptyVars);
        assertThat(result).isNotNull();
        assertThat(result.getMainDocumentPart()).isNotNull();
    }

    @Test
    @DisplayName("process(InputStream,real stream) loads the docx")
    void processStream_realStream_loadsSuccessfully() throws Exception {
        try (InputStream is = new FileInputStream(TEMPLATE_DOCX)) {
            WordprocessingMLPackage result = template.process(is, emptyVars);
            assertThat(result).isNotNull();
            assertThat(result.getMainDocumentPart()).isNotNull();
        }
    }

    // --- process(String, Map) ---

    @Test
    @DisplayName("process(String,valid path) loads the docx via InputStream")
    void processString_validPath_loadsSuccessfully() throws Exception {
        WordprocessingMLPackage result = template.process(TEMPLATE_DOCX, emptyVars);
        assertThat(result).isNotNull();
        assertThat(result.getMainDocumentPart()).isNotNull();
    }

    @Test
    @DisplayName("process(File, non-null variables) also returns a package")
    void processFile_withVariables_returnsPackage() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        vars.put("key", "value");
        WordprocessingMLPackage result = template.process((File) null, vars);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("process(InputStream, non-null variables) also returns a package")
    void processStream_withVariables_returnsPackage() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        vars.put("key", "value");
        WordprocessingMLPackage result = template.process((InputStream) null, vars);
        assertThat(result).isNotNull();
    }

}
