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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link WordprocessingMLTemplateWriter}.
 *
 * <p>NOTE: {@code writeToString(WordprocessingMLPackage)} and
 * {@code writeToStream} call {@code XmlUtils.marshaltoString(wmlPackage)},
 * but {@code WordprocessingMLPackage} is not a JAXB-marshallable root
 * element. The JAXB context does not know about it. The production code
 * should marshal {@code wmlPackage.getMainDocumentPart()} instead.
 * See {@code WordprocessingMLTemplateWriter} lines 70 and 85.
 */
public class WordprocessingMLTemplateWriterTest {

	private static final String TEMPLATE_DOCX = "src/test/resources/tpl/template.docx";

	@Test // TODO: fix production bug — writeToWriter() calls XmlUtils.marshaltoString(wmlPackage) but WordprocessingMLPackage is not JAXB-marshallable; should marshal getMainDocumentPart() instead
	void roundTripsLocalTemplateDocx() throws Exception {
		WordprocessingMLTemplateWriter writer = WordprocessingMLTemplateWriter.getWMLTemplateWriter();

		String xml = writer.writeToString(new File(TEMPLATE_DOCX));

		Assertions.assertNotNull(xml);
		Assertions.assertTrue(xml.length() > 0,
				"writeToString should return non-empty XML for " + TEMPLATE_DOCX);
	}

	@Test // TODO: fix production bug — writeToStream() calls XmlUtils.marshaltoString(wmlPackage) but WordprocessingMLPackage is not JAXB-marshallable; should marshal getMainDocumentPart() instead
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