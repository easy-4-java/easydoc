package io.github.easy4j.doc.easy;

import java.io.File;

/**
 * 类 EasyExcel / easyodf 的 easydoc 静态门面：链式构建 docx 模板渲染与读取。
 * 薄封装：内部委托 {@code DocxTemplates} + {@code WordprocessingMLTemplate}
 * 管线，不替代引擎；高级场景（SAX 细节、XHTML 导入）仍用现有 API。
 */
public final class EasyDocx {

	private EasyDocx() {
	}

	public static <T> DocxWriterBuilder<T> write(String templatePath, Class<T> model) {
		return new DocxWriterBuilder<T>(new File(templatePath), model);
	}

	public static <T> DocxWriterBuilder<T> write(File templateFile, Class<T> model) {
		return new DocxWriterBuilder<T>(templateFile, model);
	}

	public static <T> DocxReaderBuilder<T> read(String templatePath, Class<T> model,
			DocxReadListener<T> listener) {
		return new DocxReaderBuilder<T>(new File(templatePath), model, listener);
	}

	public static <T> DocxReaderBuilder<T> read(File templateFile, Class<T> model,
			DocxReadListener<T> listener) {
		return new DocxReaderBuilder<T>(templateFile, model, listener);
	}
}
