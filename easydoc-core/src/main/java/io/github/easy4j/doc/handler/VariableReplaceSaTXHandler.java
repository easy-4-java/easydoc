/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package io.github.easy4j.doc.handler;

import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.docx4j.openpackaging.parts.StAXHandlerAbstract;
import io.github.easy4j.doc.ognl.DefaultMemberAccess;
import org.xml.sax.SAXException;

import ognl.DefaultClassResolver;
import ognl.DefaultTypeConverter;
import ognl.Ognl;
import ognl.OgnlContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VariableReplaceSaTXHandler extends StAXHandlerAbstract {

	private static final Logger LOG = LoggerFactory.getLogger(VariableReplaceSaTXHandler.class);
	
	/**
	 * 变量占位符开始位，默认：${
	 */
	protected String placeholderStart = "${";
	/**
	 * 变量占位符结束位，默认：}
	 */
	protected String placeholderEnd = "}";
	/**
	 * 变量集合
	 */
	protected Map<String, Object> variables;

	/**
	 * 严格模式（-Deasydoc.variable.strict=true）：占位符无法解析或 OGNL 求值失败时
	 * 抛 {@link IllegalStateException}，而不是把 key 原样写进文档。默认宽松模式保持
	 * 历史行为（WARN 日志 + 原样输出）。仅在失败路径读取，故可在测试/运行期切换。
	 */
	protected static boolean strictMode() {
		return Boolean.getBoolean("easydoc.variable.strict");
	}
	/**
	 * Ognl上下文对象
	 */
	protected OgnlContext context;
	
	public VariableReplaceSaTXHandler(Map<String, Object> variables) throws SAXException {
		super();
		this.variables = variables;
		this.initContext();
	}

	public VariableReplaceSaTXHandler(String placeholderStart, String placeholderEnd, Map<String, Object> variables)
			throws SAXException {
		super();
		this.placeholderStart = placeholderStart;
		this.placeholderEnd = placeholderEnd;
		this.variables = variables;
		this.initContext();
	}

	protected void initContext() {
		// 构建一个OgnlContext对象
		// 安全：仅允许访问 public 成员（allowPrivate/Protected/PackageProtected 全 false），
		// 防止模板内容可控时通过 OGNL 反射访问私有成员造成 RCE
		context = (OgnlContext) Ognl.createDefaultContext(this, 
		        new DefaultMemberAccess(false, false, false), 
		        new DefaultClassResolver(),
		        new DefaultTypeConverter());
		// 设置根节点，以及初始化一些实例对象
		context.setRoot(variables);
		context.putAll(variables);
	}
	
	@Override
	public void handleCharacters(XMLStreamReader xmlr, XMLStreamWriter writer) throws XMLStreamException {

		StringBuilder sb = new StringBuilder();
	
		sb.append(xmlr.getTextCharacters(), xmlr.getTextStart(), xmlr.getTextLength());

		String wmlString = replace(sb.toString(), 0, new StringBuilder(), variables).toString();
//		LOG.debug(wmlString);

		char[] charOut = wmlString.toCharArray();
		writer.writeCharacters(charOut, 0, charOut.length);

//		writer.writeCharacters(xmlr.getTextCharacters(),
//				xmlr.getTextStart(), xmlr.getTextLength());

	}

	private StringBuilder replace(String wmlTemplateString, int offset, StringBuilder strB,
			Map<String, Object> mappings) {

		int startKey = wmlTemplateString.indexOf(placeholderStart, offset);
		if (startKey == -1) {
			return strB.append(wmlTemplateString.substring(offset));
		} else {
			strB.append(wmlTemplateString.substring(offset, startKey));
			int keyEnd = wmlTemplateString.indexOf(placeholderEnd, startKey);
			if (keyEnd > 0) {
				// 使用占位符前缀长度而非硬编码 +2，保证自定义多字符前缀（如 "#$("）时 key 切片正确
				String key = wmlTemplateString.substring(startKey + placeholderStart.length(), keyEnd);
				Object val = mappings.get(key);
				if (val == null) {
					try {
						// 先构建一个Ognl表达式，再解析表达式
				        Object ognl = Ognl.parseExpression(key);//构建Ognl表达式
				        Object value = Ognl.getValue(ognl, context, context.getRoot()); //解析表达式
						if(value != null) {
							strB.append(value.toString());
						} else {
							if (strictMode()) {
								throw new IllegalStateException("Unresolved template variable '" + placeholderStart + key + placeholderEnd
										+ "' (strict mode: easydoc.variable.strict=true)");
							}
							LOG.warn("Invalid key '{}' or key not mapped to a value", key);
							strB.append(key);
						}
					} catch (Exception e) {
						// else 分支抛出的 IllegalStateException（未解析变量）原样透传，
						// 不做二次包装
						if (e instanceof IllegalStateException ise) {
							throw ise;
						}
						if (strictMode()) {
							throw new IllegalStateException("Failed to evaluate OGNL expression '" + placeholderStart + key + placeholderEnd
									+ "' (strict mode: easydoc.variable.strict=true)", e);
						}
						LOG.warn("Failed to evaluate expression '" + placeholderStart + key + placeholderEnd + "': {}", e.getMessage());
						strB.append(key);
					}
				} else {
					strB.append(val.toString());
				}
				// 前进量基于结束占位符长度而非硬编码 +1，保证多字符结束符（如 "}}"）时不错位
				return replace(wmlTemplateString, keyEnd + placeholderEnd.length(), strB, mappings);
			} else {
				LOG.warn("Invalid key: could not find '}}' ");
				strB.append("$");
				return replace(wmlTemplateString, offset + 1, strB, mappings);
			}
		}
	}
}