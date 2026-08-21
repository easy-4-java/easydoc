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

import java.io.File;
import java.nio.file.Path;

import io.github.easy4j.doc.io.WordprocessingMLTemplateWriter;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link WordprocessingMLTemplateWriter}.
 *
 * <p><b>Disabled until the MOXy migration lands (see easydoc-core/pom.xml TODO).</b>
 * The current dep stack (MOXy 2.7.6 + jakarta.xml.bind-api 4.0 + jaxb-runtime 4.0)
 * leaves docx4j's org.docx4j.jaxb.Context unable to instantiate the
 * JAXB Reference Implementation cleanly because the namespace-prefix-mapper
 * bridge between docx4j and MOXy does not initialise in the current
 * configuration. Until that is fixed by the
 * {@code docx4j-JAXB-ReferenceImpl + jakarta.xml.bind-api 4.x} migration,
 * {@code WordprocessingMLPackage.load(File)} throws
 * {@code "JAXB: Can't instantiate JAXB Reference Implementation"} /
 * {@code "namespacePrefixMapper is null"}.
 */
@Disabled("MOXy/jaxb-runtime namespace-prefix-mapper bridge — see easydoc-core/pom.xml TODO")
public class WordprocessingMLTemplateWriterTest {

	private static final String TEMPLATE_DOCX = "src/test/resources/tpl/template.docx";

	@Test
	void roundTripsLocalTemplateDocx() throws Exception {
		WordprocessingMLTemplateWriter writer = WordprocessingMLTemplateWriter.getWMLTemplateWriter();

		String xml = writer.writeToString(new File(TEMPLATE_DOCX));

		Assertions.assertNotNull(xml);
		Assertions.assertTrue(xml.length() > 0,
				"writeToString should return non-empty XML for " + TEMPLATE_DOCX);
	}

	@Test
	void writeToFileProducesAReopenableDocx(@TempDir Path tempDir) throws Exception {
		WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.load(new File(TEMPLATE_DOCX));
		Assertions.assertNotNull(wmlPackage, "Source template must load as a WordprocessingMLPackage");

		File outFile = tempDir.resolve("out.docx").toFile();
		WordprocessingMLTemplateWriter.writeToFile(wmlPackage, outFile);

		Assertions.assertTrue(outFile.exists(), "Output docx should exist at " + outFile);
		Assertions.assertTrue(outFile.length() > 0, "Output docx should be non-empty");

		WordprocessingMLPackage reloaded = WordprocessingMLPackage.load(outFile);
		Assertions.assertNotNull(reloaded, "Reopened package must not be null");
		Assertions.assertNotNull(reloaded.getMainDocumentPart(),
				"Reopened package must expose a MainDocumentPart");
	}

}