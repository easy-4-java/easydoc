package io.github.easy4j.doc.easy;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easy4j.doc.annotation.DocxField;
import io.github.easy4j.doc.annotation.DocxIgnore;

/**
 * 将 POJO 按 {@link DocxField}/{@link DocxIgnore} 注解转换为文档变量 Map
 * （对齐 easyodf 的 OFDReflectionUtils）。字段值映射到占位符名，默认占位符
 * 为字段名（渲染时由模板包装成 ${name}），支持 format 日期格式化。
 */
public final class DocxFields {

	private static final Logger LOG = LoggerFactory.getLogger(DocxFields.class);

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
			// 跳过静态字段与编译器合成字段（如内部类的 this$0、lambda 捕获的 arg$1、
			// switch-on-string 生成的 $SwitchMap$xxx 等）：它们不属于业务渲染数据，
			// 静态字段的值被写入变量 Map 还可能造成跨请求数据串扰
			if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
				continue;
			}
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
			} catch (ReflectiveOperationException | InaccessibleObjectException e) {
				// 反射不可达：IllegalAccessException / NoSuch* 等反射操作异常，
				// 以及 JDK 16+ 模块封闭抛出的 InaccessibleObjectException（RuntimeException 子类）。
				// 一律跳过该字段并记录 WARN，不阻断整体转换
				LOG.warn("Skip inaccessible field {}.{} while extracting docx variables",
						bean.getClass().getName(), field.getName(), e);
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
