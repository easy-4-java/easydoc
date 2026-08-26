package io.github.easy4j.doc.easy;

import java.io.File;
import java.util.Objects;

/**
 * 类 EasyExcel / easyodf 的 easydoc 静态门面：链式构建 docx 模板渲染与读取。
 * 薄封装：内部委托 docx 模板管线（WordprocessingMLTemplate），不替代引擎；
 * 高级场景（SAX 细节、XHTML 导入）仍用现有 API。
 */
public final class EasyDocx {

	private EasyDocx() {
	}

	public static <T> DocxWriterBuilder<T> write(String templatePath, Class<T> model) {
		Objects.requireNonNull(model, "model must not be null");
		return new DocxWriterBuilder<T>(new File(templatePath), model);
	}

	public static <T> DocxWriterBuilder<T> write(File templateFile, Class<T> model) {
		Objects.requireNonNull(model, "model must not be null");
		return new DocxWriterBuilder<T>(templateFile, model);
	}

	public static <T> DocxReaderBuilder<T> read(String templatePath, Class<T> model,
			DocxReadListener<T> listener) {
		Objects.requireNonNull(model, "model must not be null");
		Objects.requireNonNull(listener, "listener must not be null");
		return new DocxReaderBuilder<T>(new File(templatePath), model, listener);
	}

	public static <T> DocxReaderBuilder<T> read(File templateFile, Class<T> model,
			DocxReadListener<T> listener) {
		Objects.requireNonNull(model, "model must not be null");
		Objects.requireNonNull(listener, "listener must not be null");
		return new DocxReaderBuilder<T>(templateFile, model, listener);
	}
}
