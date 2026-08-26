package io.github.easy4j.doc.easy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

class DocxReadListenerTest {

	static class Model {
		String name;
	}

	@Test
	void listenerDefaultMethodsDoNotThrow() {
		DocxReadListener<Model> listener = new DocxReadListener<Model>() {
			@Override
			public void invoke(Model data, Map<String, String> values) {
				data.name = values.get("name");
			}
		};
		Model m = new Model();
		listener.invoke(m, Collections.singletonMap("name", "ACME"));
		assertEquals("ACME", m.name);
		listener.doAfterAllAnalysed(); // 默认空实现不抛异常
		assertTrue(true);
	}
}
