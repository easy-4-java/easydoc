package io.github.easy4j.doc.annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

class DocxFieldAnnotationTest {

	static class Model {
		@DocxField("partyName")
		private String name;
		@DocxField(value = "signDate", format = "yyyy-MM-dd")
		private java.util.Date date;
		@DocxIgnore
		private String internal;
	}

	@Test
	void annotationsAreRuntimeVisibleAndCarryValues() throws Exception {
		Field name = Model.class.getDeclaredField("name");
		DocxField df = name.getAnnotation(DocxField.class);
		assertNotNull(df, "@DocxField must be present");
		assertEquals("partyName", df.value());
		assertEquals("", df.format());
		assertEquals(false, df.ignore());

		Field date = Model.class.getDeclaredField("date");
		assertEquals("yyyy-MM-dd", date.getAnnotation(DocxField.class).format());

		Field internal = Model.class.getDeclaredField("internal");
		assertTrue(internal.getAnnotation(DocxIgnore.class) != null,
				"@DocxIgnore must be present");
	}
}
