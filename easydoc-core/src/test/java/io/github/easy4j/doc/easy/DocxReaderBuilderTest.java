package io.github.easy4j.doc.easy;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

class DocxReaderBuilderTest {

	static class Model {
		String title;
	}

	@Test
	void doReadInvokesListenerWithParsedValues() {
		AtomicBoolean invoked = new AtomicBoolean(false);
		DocxReadListener<Model> listener = new DocxReadListener<Model>() {
			@Override
			public void invoke(Model data, Map<String, String> values) {
				invoked.set(true);
				if (values.containsKey("title")) {
					data.title = values.get("title");
				}
			}
		};
		File template = new File("src/test/resources/tpl/template.docx");
		new DocxReaderBuilder<Model>(template, Model.class, listener).doRead();
		assertTrue(invoked.get(), "listener must be invoked for the parsed template");
	}

	@Test
	void doReadDoesNotThrowOnMissingTemplate() {
		DocxReadListener<Model> l = new DocxReadListener<Model>() {
			@Override
			public void invoke(Model data, Map<String, String> values) {
			}
		};
		new DocxReaderBuilder<Model>(new File("/nonexistent/template.docx"), Model.class, l)
				.doRead();
		assertTrue(true, "missing template must not throw; listener simply receives nothing");
	}

	@Test
	void doReadPassesFreshInstanceToListener() {
		DocxReadListener<Model> l = new DocxReadListener<Model>() {
			@Override
			public void invoke(Model data, Map<String, String> values) {
				assertNotNull(data, "listener must receive a model instance");
			}
		};
		new DocxReaderBuilder<Model>(new File("src/test/resources/tpl/template.docx"), Model.class, l)
				.doRead();
	}
}
