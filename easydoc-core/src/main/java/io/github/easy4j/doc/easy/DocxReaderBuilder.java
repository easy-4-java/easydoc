package io.github.easy4j.doc.easy;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.Map;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * docx 模板读取 Builder（对齐 easyodf {@code OFDReaderBuilder} /
 * EasyExcel {@code ExcelReaderBuilder}）：解析模板占位符，回调
 * {@link DocxReadListener}。
 *
 * @param <T> 模型类型
 */
public final class DocxReaderBuilder<T> {

	private static final Logger LOG = LoggerFactory.getLogger(DocxReaderBuilder.class);

	private final File templateFile;
	private final Class<T> model;
	private final DocxReadListener<T> listener;

	public DocxReaderBuilder(File templateFile, Class<T> model, DocxReadListener<T> listener) {
		this.templateFile = templateFile;
		this.model = Objects.requireNonNull(model, "model must not be null");
		this.listener = Objects.requireNonNull(listener, "listener must not be null");
	}

	/**
	 * 解析模板（占位符 → 值），回调 listener。模板缺失时静默返回（薄封装语义）。
	 */
	public void doRead() {
		if (templateFile == null || !templateFile.exists()) {
			return;
		}
		try {
			WordprocessingMLPackage pkg = new io.github.easy4j.doc.WordprocessingMLDocxTemplate()
					.process(templateFile, new HashMap<String, Object>());
			String xml = pkg.getMainDocumentPart().getXML();
			Map<String, String> values = extractPlaceholders(xml);
			T data = newInstance();
			if (listener != null && data != null) {
				listener.invoke(data, values);
				listener.doAfterAllAnalysed();
			}
		} catch (Exception e) {
			// 读取失败不抛出（薄封装语义），但不再静默吞掉异常（port from 3.0.x）：
			// 记录 WARN 便于排查模板解析/监听器回调失败
			LOG.warn("Failed to load DocxReader listener for template {}", templateFile, e);
		}
	}

	private T newInstance() {
		try {
			return model.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			return null;
		}
	}

	/** 提取文档 XML 中所有 ${name} 占位符（值为空串，供监听器识别键集合）。 */
	private Map<String, String> extractPlaceholders(String xml) {
		Map<String, String> values = new HashMap<String, String>();
		String start = "${";
		String end = "}";
		int from = 0;
		int i = xml.indexOf(start, from);
		while (i >= 0) {
			int j = xml.indexOf(end, i + start.length());
			if (j > i) {
				String key = xml.substring(i + start.length(), j);
				if (!key.isEmpty()) {
					values.put(key, "");
				}
				from = j + end.length();
			} else {
				from = i + start.length();
			}
			i = xml.indexOf(start, from);
		}
		return values;
	}
}
