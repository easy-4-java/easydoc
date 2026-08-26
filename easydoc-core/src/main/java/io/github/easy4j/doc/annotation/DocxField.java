package io.github.easy4j.doc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注 POJO 字段到文档占位符的映射（类 EasyExcel {@code @ExcelProperty} /
 * easyodf {@code @OFDProperty}）。value 为占位符名（默认取字段名，渲染时由
 * 模板包装成 {@code ${name}}）；format 支持 Date/Number 格式化。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocxField {

	/** 占位符名（默认取字段名）。 */
	String value() default "";

	/** 日期/数字格式化模式（如 "yyyy-MM-dd"）。 */
	String format() default "";

	/** 是否忽略该字段（等价 {@link DocxIgnore}）。 */
	boolean ignore() default false;
}
