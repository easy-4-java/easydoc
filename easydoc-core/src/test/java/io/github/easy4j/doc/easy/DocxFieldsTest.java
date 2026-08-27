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

	/**
	 * 非 static 成员内部类：JVM 规范要求编译器为其合成 this$0 字段（指向外部类
	 * 实例），用于验证反射循环能安全跳过合成字段。
	 */
	class SyntheticHolder {
		// 非 final 静态字段：确保一定以 Field 形式出现在 getDeclaredFields() 中
		static String SHARED = "static-state";
		@DocxField("vis")
		private String visible = "v";
		private String plain = "p";
	}

	@Test
	void fromSkipsStaticAndSyntheticFields() {
		SyntheticHolder bean = new SyntheticHolder();
		Map<String, Object> map = DocxFields.from(bean);
		assertEquals("v", map.get("vis"), "普通实例字段仍需正常提取");
		assertEquals("p", map.get("plain"));
		assertFalse(map.containsKey("this$0"), "编译器合成的 this$0 字段必须被跳过且不得抛异常");
		assertFalse(map.containsKey("SHARED"), "静态字段必须被跳过（防止静态状态写入变量 Map）");
	}
}
