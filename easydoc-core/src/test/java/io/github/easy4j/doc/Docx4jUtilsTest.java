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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.utils.Docx4jUtils;

class Docx4jUtilsTest {

	@Test
	void getTempPathReturnsTempDirPath() {
		assertTrue(Docx4jUtils.getTempPath().startsWith(System.getProperty("java.io.tmpdir")));
	}

	@Test
	void mergeDocxEmptyListReturnsNull() throws Exception {
		Docx4jUtils utils = new Docx4jUtils();
		assertNull(utils.mergeDocx(Collections.emptyList()));
	}

	/**
	 * The single-stream path is the C-4 regression guard: it must hand back a
	 * non-null {@code InputStream} and that stream must round-trip back to a
	 * reopenable docx.
	 *
	 * <p>Disabled because the project's {@code docx4j-JAXB-MOXy 2.7.6} +
	 * {@code jakarta.xml.bind-api 4.0.4} combination cannot initialise
	 * {@code org.docx4j.jaxb.Context.jcContentTypes} at static-init time: the
	 * new SPI lookup expects a JAXB 4.x provider ({@code org.glassfish.jaxb.runtime.v2.ContextFactory})
	 * that is not on the test classpath. The pre-existing
	 * {@code WordprocessingMLTemplateWriterTest} hits the same
	 * {@code NullPointerException} at {@code Context.jcContentTypes.createUnmarshaller()}.
	 * Until the pom is fixed to add a JAXB 4.x runtime on the classpath, this test
	 * cannot be run. The byte-level check below passes whenever the production code
	 * manages to return a stream (i.e. on a fixed classpath); kept here as a guard
	 * for the moment that fix lands.
	 */
	@Test
	@Disabled("requires jakarta.xml.bind-api 4.x runtime on classpath; see Javadoc above")
	void mergeDocxSingleStreamRoundTrips_C4Regression() throws Exception {
		byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/tpl/template.docx"));
		InputStream in = new ByteArrayInputStream(bytes);
		Docx4jUtils utils = new Docx4jUtils();
		InputStream merged = utils.mergeDocx(Arrays.asList(in));
		assertNotNull(merged);
		try {
			ByteArrayOutputStream sink = new ByteArrayOutputStream();
			byte[] chunk = new byte[4096];
			int n;
			while ((n = merged.read(chunk)) > 0) {
				sink.write(chunk, 0, n);
			}
			byte[] mergedBytes = sink.toByteArray();
			assertTrue(mergedBytes.length > 0, "merged docx stream must be non-empty");
			try (ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(mergedBytes))) {
				assertNotNull(zin.getNextEntry(),
						"merged output must be a valid ZIP container (i.e. a docx)");
			}
		} finally {
			merged.close();
		}
	}
}