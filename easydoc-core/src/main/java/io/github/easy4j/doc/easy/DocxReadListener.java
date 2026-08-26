package io.github.easy4j.doc.easy;

import java.util.Map;

/**
 * 模板读取监听器（对齐 EasyExcel {@code ReadListener} / easyodf
 * {@code OFDReadListener}）。每次解析出一个数据单元时回调 {@link #invoke}，
 * 全部解析完回调 {@link #doAfterAllAnalysed}。
 *
 * @param <T> 数据模型类型
 */
public interface DocxReadListener<T> {

	/**
	 * 解析到一条数据（占位符名 → 值）时回调。
	 * @param data 当前数据实例（由模型无参构造创建）
	 * @param values 解析出的占位符名 → 值映射
	 */
	void invoke(T data, Map<String, String> values);

	/** 全部解析完成后回调；默认空实现。 */
	default void doAfterAllAnalysed() {
	}
}
