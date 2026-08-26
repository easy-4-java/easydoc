package io.github.easy4j.doc.easy;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import io.github.easy4j.doc.WordprocessingMLTemplate;

/**
 * docx 模板渲染链式 Builder（对齐 easyodf {@code OFDWriterBuilder} /
 * EasyExcel {@code ExcelWriterBuilder}）。docx 无 sheet，中间层用
 * {@link #document(String)}（Document 概念）标识渲染目标；单文档时 document
 * 可省略。process 内部 POJO → Map → 现有模板管线。
 *
 * @param <T> 模型类型
 */
public final class DocxWriterBuilder<T> {

	private final File templateFile;
	private final Class<T> model;

	public DocxWriterBuilder(File templateFile, Class<T> model) {
		this.templateFile = templateFile;
		this.model = model;
	}

	/**
	 * docx 语义中间层（对齐 EasyExcel sheet 的位置）：标识文档/模板实例。
	 * 单文档渲染时不改变管线行为；多文档批量场景由调用方多次 process。
	 */
	public DocxWriterBuilder<T> document(String name) {
		return this;
	}

	/** POJO 模型渲染：@DocxField 注解 → Map → 现有模板管线。 */
	public WordprocessingMLPackage process(T data) throws Exception {
		return process(DocxFields.from(data));
	}

	/** 原始 Map 渲染（兼容现有 API 的变量注入）。 */
	public WordprocessingMLPackage process(Map<String, Object> vars) throws Exception {
		WordprocessingMLTemplate template = new io.github.easy4j.doc.WordprocessingMLDocxTemplate();
		Map<String, Object> effective = new HashMap<String, Object>(vars);
		return template.process(templateFile, effective);
	}
}
