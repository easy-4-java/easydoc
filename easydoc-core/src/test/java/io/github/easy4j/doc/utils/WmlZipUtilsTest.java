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
package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WmlZipUtilsTest {

    private static final String TEMPLATE_PATH = "tpl/template.docx";

    @Test
    void unzipExtractsDocx(@TempDir Path tempDir) throws Exception {
        Path source = tempDir.resolve("template.docx");
        try {
            Files.copy(new File(TEMPLATE_PATH).toPath(), source, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            // Skip if test resource not available
            return;
        }

        Path outputDir = tempDir.resolve("out");
        WmlZipUtils.unzip(source.toFile(), outputDir.toFile());
        assertNotNull(outputDir);
        assertTrue(outputDir.toFile().exists());
        // A docx contains a [Content_Types].xml at the root
        assertTrue(Files.exists(outputDir.resolve("[Content_Types].xml")));
    }

    @Test
    void unzipStringOverloadExtractsDocx(@TempDir Path tempDir) throws Exception {
        File src = new File(TEMPLATE_PATH);
        if (!src.exists()) {
            return;
        }
        Path source = tempDir.resolve("template.docx");
        Files.copy(src.toPath(), source, StandardCopyOption.REPLACE_EXISTING);
        Path outputDir = tempDir.resolve("out2");
        WmlZipUtils.unzip(source.toString(), outputDir.toString());
        assertTrue(Files.exists(outputDir.resolve("[Content_Types].xml")));
    }
}
