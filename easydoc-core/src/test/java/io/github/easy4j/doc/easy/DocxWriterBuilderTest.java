package io.github.easy4j.doc.easy;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.DocxMode;
import io.github.easy4j.doc.annotation.DocxField;

class DocxWriterBuilderTest {

	static class Contract {
		@DocxField("title")
		private String title = "easy-docx-contract";
		@DocxField("content")
		private String content = "rendered via builder";
	}

	private static File tempTemplate() throws Exception {
		byte[] templateBytes = Files.readAllBytes(Path.of("src/test/resources/tpl/template.docx"));
		File tmp = File.createTempFile("easydocx", ".docx");
		Files.write(tmp.toPath(), templateBytes);
		return tmp;
	}

	@Test
	void processRendersModelIntoPackage() throws Exception {
		File tmp = tempTemplate();
		WordprocessingMLPackage pkg = new DocxWriterBuilder<Contract>(tmp, Contract.class)
				.document("contract")
				.mode(DocxMode.STAX)
				.process(new Contract());
		assertNotNull(pkg);
		String xml = pkg.getMainDocumentPart().getXML();
		assertTrue(xml.contains("easy-docx-contract"),
				"POJO field must be substituted into the rendered document");
		tmp.delete();
	}

	@Test
	void processAcceptsRawMap() throws Exception {
		File tmp = tempTemplate();
		WordprocessingMLPackage pkg = new DocxWriterBuilder<Contract>(tmp, Contract.class)
				.process(java.util.Collections.singletonMap("title", "map-title"));
		assertNotNull(pkg);
		assertTrue(pkg.getMainDocumentPart().getXML().contains("map-title"),
				"raw map values must be substituted");
		tmp.delete();
	}

	@Test
	void processHandlesNullVarsAsEmpty() throws Exception {
		File tmp = tempTemplate();
		WordprocessingMLPackage pkg = new DocxWriterBuilder<Contract>(tmp, Contract.class)
				.process((java.util.Map<String, Object>) null);
		assertNotNull(pkg, "null vars must render as empty variables");
		tmp.delete();
	}

	@Test
	void documentAndModeAreChainable() throws Exception {
		File tmp = tempTemplate();
		DocxWriterBuilder<Contract> b = new DocxWriterBuilder<Contract>(tmp, Contract.class)
				.document("d")
				.mode(DocxMode.STAX);
		assertNotNull(b.process(new Contract()));
		tmp.delete();
	}
}
