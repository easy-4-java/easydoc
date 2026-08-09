/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package io.github.easy4j.doc.handler;

import java.util.Map;

import org.docx4j.openpackaging.parts.SAXHandler;
import io.github.easy4j.doc.ognl.DefaultMemberAccess;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import ognl.DefaultClassResolver;
import ognl.DefaultTypeConverter;
import ognl.Ognl;
import ognl.OgnlContext;

/**
 * Implementation of variable replace s a x handler extending SAXHandler.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
public class VariableReplaceSAXHandler extends SAXHandler implements ContentHandler {
	
	/**
	 * 变量占位符开始位，默认：${
	 */
	protected String placeholderStart = "${";
	/**
	 * 变量占位符结束位，默认：}
	 */
	protected String placeholderEnd = "}";
	/**
	 * SPEL表达式占位符开始位，默认：#{
	 */
	protected String spelExpressionStart = "#{";
	/**
	 * SPEL表达式占位符结束位，默认：}
	 */
	protected String spelExpressionEnd = "}";
	/**
	 * 变量集合
	 */
	protected Map<String, Object> variables;
	/**
	 * Ognl上下文对象
	 */
	protected OgnlContext context;

	public VariableReplaceSAXHandler(Map<String, Object> variables) throws SAXException {
		super();
		this.initContext();
	}
	
	public VariableReplaceSAXHandler(String placeholderStart, String placeholderEnd ,Map<String, Object> variables) throws SAXException {
		super();
		this.placeholderStart = placeholderStart;
		this.placeholderEnd = placeholderEnd;
		this.variables = variables;
		this.initContext();
	}
	
	protected void initContext() {
		// 构建一个OgnlContext对象
		context = (OgnlContext) Ognl.createDefaultContext(this, 
		        new DefaultMemberAccess(true), 
		        new DefaultClassResolver(),
		        new DefaultTypeConverter());
		// 设置根节点，以及初始化一些实例对象
		context.setRoot(variables);
		context.putAll(variables);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		StringBuilder sb = new StringBuilder();
		sb.append(ch, start, length);

		String wmlString = replace(sb.toString(), 0, new StringBuilder(), variables).toString();
//		System.out.println(wmlString);

		char[] charOut = wmlString.toCharArray();
		
		this.getContentHandler().characters(charOut, 0, charOut.length);

	}

	private StringBuilder replace(String wmlTemplateString, int offset, StringBuilder strB,
			Map<String, Object> mappings) {

		int startKey = wmlTemplateString.indexOf(placeholderStart, offset);
		if (startKey == -1) {
			return strB.append(wmlTemplateString.substring(offset));
		} else {
			strB.append(wmlTemplateString.substring(offset, startKey));
			int keyEnd = wmlTemplateString.indexOf(placeholderEnd, startKey);
			String key = wmlTemplateString.substring(startKey + 2, keyEnd);
			Object val = mappings.get(key);
			if (val == null) {
				try {
					// 先构建一个Ognl表达式，再解析表达式
			        Object ognl = Ognl.parseExpression(key);//构建Ognl表达式
			        Object value = Ognl.getValue(ognl, context, context.getRoot()); //解析表达式
					if(value != null) {
						strB.append(value.toString());
					} else {
						System.out.println("Invalid key '" + key + "' or key not mapped to a value");
						strB.append(key);
					}
				} catch (Exception e) {
					e.printStackTrace();
					strB.append(key);
				}
			} else {
				strB.append(val.toString());
			}
			return replace(wmlTemplateString, keyEnd + 1, strB, mappings);
		}
	}
}