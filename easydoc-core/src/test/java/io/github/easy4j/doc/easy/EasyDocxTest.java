package io.github.easy4j.doc.easy;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import org.junit.jupiter.api.Test;

class EasyDocxTest {

	static class Model {
	}

	@Test
	void writeReturnsWriterBuilder() {
		assertNotNull(EasyDocx.write("src/test/resources/tpl/template.docx", Model.class),
				"write(path, model) must return a builder");
		assertNotNull(EasyDocx.write(new File("src/test/resources/tpl/template.docx"), Model.class),
				"write(file, model) must return a builder");
	}

	@Test
	void readReturnsReaderBuilder() {
		DocxReadListener<Model> l = new DocxReadListener<Model>() {
			@Override
			public void invoke(Model data, java.util.Map<String, String> values) {
			}
		};
		assertNotNull(EasyDocx.read("src/test/resources/tpl/template.docx", Model.class, l),
				"read(path, model, listener) must return a builder");
		assertNotNull(EasyDocx.read(new File("src/test/resources/tpl/template.docx"), Model.class, l),
				"read(file, model, listener) must return a builder");
	}
}
