package io.github.easy4j.doc.utils;

import java.util.regex.Pattern;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.docx4j.XmlUtils;

/*
* 清扫 docx4j 模板变量字符,通常以${variable}形式
* <p>
* XXX: 主要在上传模板时处理一下, 后续
*
* @author <a href="https://github.com/loong10k">Loong Wan</a>
* @since 2018-11-07
*/
public class DocxVariableClearUtils {

	/**
	 * 去任意XML标签
	 */
	private static final Pattern XML_PATTERN = Pattern.compile("<[^>]*>");

	private DocxVariableClearUtils() {
	}

	/**
	 * start符号
	 */
	private static final char PREFIX = '$';

	/**
	 * 中包含
	 */
	private static final char LEFT_BRACE = '{';

	/**
	 * 结尾
	 */
	private static final char RIGHT_BRACE = '}';

	/**
	 * 未开始
	 */
	private static final int NONE_START = -1;

	/**
	 * 未开始
	 */
	private static final int NONE_START_INDEX = -1;

	/**
	 * 开始
	 */
	private static final int PREFIX_STATUS = 1;

	/**
	 * 左括号
	 */
	private static final int LEFT_BRACE_STATUS = 2;

	/**
	 * 右括号
	 */
	private static final int RIGHT_BRACE_STATUS = 3;

	/*
	 * doCleanDocumentPart
	 *
	 * @param wmlTemplate
	 * 
	 * @param jc
	 * 
	 * @return
	 * 
	 * @throws JAXBException
	 */
	public static Object doCleanDocumentPart(String wmlTemplate, JAXBContext jc) throws JAXBException {
		// 进入变量块位置
		int curStatus = NONE_START;
		// 开始位置
		int keyStartIndex = NONE_START_INDEX;
		// 当前位置
		int curIndex = 0;
		char[] textCharacters = wmlTemplate.toCharArray();
		StringBuilder documentBuilder = new StringBuilder(textCharacters.length);
		documentBuilder.append(textCharacters);
		// 新文档
		StringBuilder newDocumentBuilder = new StringBuilder(textCharacters.length);
		// 最后一次写位置
		int lastWriteIndex = 0;
		for (char c : textCharacters) {
			switch (c) {
			case PREFIX:
				// TODO 不管其何状态直接修改指针,这也意味着变量名称里面不能有PREFIX
				keyStartIndex = curIndex;
				curStatus = PREFIX_STATUS;
				break;
			case LEFT_BRACE:
				if (curStatus == PREFIX_STATUS) {
					curStatus = LEFT_BRACE_STATUS;
				}
				break;
			case RIGHT_BRACE:
				if (curStatus == LEFT_BRACE_STATUS) {
					// 接上之前的字符
					newDocumentBuilder.append(documentBuilder.substring(lastWriteIndex, keyStartIndex));
					// 结束位置
					int keyEndIndex = curIndex + 1;
					// 替换
					String rawKey = documentBuilder.substring(keyStartIndex, keyEndIndex);
					// 干掉多余标签
					String mappingKey = XML_PATTERN.matcher(rawKey).replaceAll("");
					if (!mappingKey.equals(rawKey)) {
						char[] rawKeyChars = rawKey.toCharArray();
						// 保留原格式
						StringBuilder rawStringBuilder = new StringBuilder(rawKey.length());
						// 去掉变量引用字符
						for (char rawChar : rawKeyChars) {
							if (rawChar == PREFIX || rawChar == LEFT_BRACE || rawChar == RIGHT_BRACE) {
								continue;
							}
							rawStringBuilder.append(rawChar);
						}
						// FIXME 要求变量连在一起
						String variable = mappingKey.substring(2, mappingKey.length() - 1);
						int variableStart = rawStringBuilder.indexOf(variable);
						if (variableStart > 0) {
							rawStringBuilder = rawStringBuilder.replace(variableStart, variableStart + variable.length(), mappingKey);
						}
						newDocumentBuilder.append(rawStringBuilder.toString());
					} else {
						newDocumentBuilder.append(mappingKey);
					}
					lastWriteIndex = keyEndIndex;

					curStatus = NONE_START;
					keyStartIndex = NONE_START_INDEX;
				}
			default:
				break;
			}
			curIndex++;
		}
		// 余部
		if (lastWriteIndex < documentBuilder.length()) {
			newDocumentBuilder.append(documentBuilder.substring(lastWriteIndex));
		}
		return XmlUtils.unmarshalString(newDocumentBuilder.toString(), jc);
	}

}