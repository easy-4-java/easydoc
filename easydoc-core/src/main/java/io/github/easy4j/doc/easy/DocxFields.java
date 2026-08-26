package io.github.easy4j.doc.easy;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.github.easy4j.doc.annotation.DocxField;
import io.github.easy4j.doc.annotation.DocxIgnore;

/**
 * 将 POJO 按 {@link DocxField}/{@link DocxIgnore} 注解转换为文档变量 Map
 * （对齐 easyodf 的 OFDReflectionUtils）。字段值映射到占位符名，默认占位符
 * 为字段名（渲染时由模板包装成 ${name}），支持 format 日期格式化。
 */
public final class DocxFields {

	private DocxFields() {
	}

	/**
	 * 提取 bean 中所有可渲染字段为 占位符名 → 值 的 Map。
	 * @param bean 模型对象；null 返回空 Map
	 * @return 占位符名 → 值
	 */
	public static Map<String, Object> from(Object bean) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (bean == null) {
			return map;
		}
		for (Field field : bean.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(DocxIgnore.class)) {
				continue;
			}
			DocxField df = field.getAnnotation(DocxField.class);
			if (df != null && df.ignore()) {
				continue;
			}
			try {
				field.setAccessible(true);
				Object value = field.get(bean);
				String placeholder = (df != null && !df.value().isEmpty())
						? df.value()
						: field.getName();
				map.put(placeholder, formatValue(value, df != null ? df.format() : ""));
			} catch (IllegalAccessException e) {
				// 反射不可达：跳过该字段，不阻断整体转换
			}
		}
		return map;
	}

	private static Object formatValue(Object value, String format) {
		if (value instanceof Date && format != null && !format.isEmpty()) {
			return new SimpleDateFormat(format).format((Date) value);
		}
		return value;
	}
}
