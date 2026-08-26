package io.github.easy4j.doc.easy;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import io.github.easy4j.doc.DocxMode;
import io.github.easy4j.doc.DocxTemplates;
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
	private DocxMode mode = DocxMode.DEFAULT;

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

	public DocxWriterBuilder<T> mode(DocxMode mode) {
		this.mode = mode;
		return this;
	}

	/** POJO 模型渲染：@DocxField 注解 → Map → 现有模板管线。 */
	public WordprocessingMLPackage process(T data) throws Exception {
		return process(DocxFields.from(data));
	}

	/** 原始 Map 渲染（兼容现有 API 的变量注入）；vars 为 null 时按空变量处理。 */
	public WordprocessingMLPackage process(Map<String, Object> vars) throws Exception {
		WordprocessingMLTemplate template = DocxTemplates.create(mode);
		Map<String, Object> effective = vars != null
				? new HashMap<String, Object>(vars)
				: new HashMap<String, Object>();
		return template.process(templateFile, effective);
	}
}
