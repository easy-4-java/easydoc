package io.github.easy4j.doc.easy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.easy4j.doc.annotation.DocxField;
import io.github.easy4j.doc.annotation.DocxIgnore;

class DocxFieldsTest {

	private static TimeZone original;

	@BeforeAll
	static void fixTimeZone() {
		original = TimeZone.getDefault();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@AfterAll
	static void restoreTimeZone() {
		TimeZone.setDefault(original);
	}

	static class Contract {
		@DocxField("partyName")
		private String name = "ACME";
		@DocxField(value = "signDate", format = "yyyy-MM-dd")
		private Date date = new Date(1700000000000L);
		@DocxField
		private Integer amount = 100;
		@DocxIgnore
		private String internal = "secret";
	}

	@Test
	void fromMapsAnnotatedFieldsToPlaceholders() {
		Map<String, Object> map = DocxFields.from(new Contract());
		assertEquals("ACME", map.get("partyName"));
		assertEquals("2023-11-14", map.get("signDate"), "format must apply to Date");
		assertEquals(100, map.get("amount"), "unannotated value defaults to field name");
		assertFalse(map.containsKey("internal"), "@DocxIgnore fields must be skipped");
		assertFalse(map.containsKey("name"), "raw field name must not appear when @DocxField overrides");
	}

	@Test
	void fromHandlesNullBeanAndNullValues() {
		assertTrue(DocxFields.from(null).isEmpty(), "null bean yields empty map");
		Map<String, Object> m = DocxFields.from(new Object() {
			@DocxField("x")
			private String v = null;
		});
		assertTrue(m.containsKey("x"), "null value still appears with its placeholder key");
		assertEquals(null, m.get("x"));
	}
}
