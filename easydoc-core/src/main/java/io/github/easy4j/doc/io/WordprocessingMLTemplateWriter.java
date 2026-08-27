/**
 * Copyright (c) 2018, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.easy4j.doc.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.docx4j.Docx4jProperties;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.utils.Assert;

/**
 * WordprocessingML template writer.
 *
 * <p>已知遗留问题（不在本次修复范围，需接口层协同）：
 * {@link io.github.easy4j.doc.WordprocessingMLTemplate#process(String, java.util.Map)}
 * 的默认实现直接 {@code new FileInputStream(template)} 传入 {@code process(InputStream, Map)}，
 * 该流从未显式关闭 —— 存在文件句柄泄漏。修复需要调整接口/抽象类约定
 * （如约定实现方关闭入参流并在此处使用 try-with-resources 包装），涉及
 * 模板处理链各实现方，待统一规划后处理。</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLTemplateWriter {

	private static final WordprocessingMLTemplateWriter WML_TEMPLATE_WRITER = new WordprocessingMLTemplateWriter();

	/**
	 * Generate a WordprocessingMLTemplateWriter.
	 * @return the WordprocessingMLTemplateWriter
	 */
	public static WordprocessingMLTemplateWriter getWMLTemplateWriter() {
		return WML_TEMPLATE_WRITER;
	}

	protected WordprocessingMLTemplateWriter() {

	}

	public String writeToString(String docFile) throws Exception {
		return this.writeToString(new File(docFile));
	}

	public String writeToString(File docFile) throws IOException, Docx4JException {
		WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.load(docFile);
		String extracted;
		try (StringBuilderWriter output = new StringBuilderWriter()) {
			this.writeToWriter(wmlPackage, output);
			extracted = output.toString();
		}
		return extracted;
	}

	public String writeToString(WordprocessingMLPackage wmlPackage) throws IOException, Docx4JException {
		MainDocumentPart document = wmlPackage.getMainDocumentPart();
		return document.getXML();
	}

	public static void writeToFile(WordprocessingMLPackage wmlPackage, File outFile) throws IOException, Docx4JException {
		// P1 资源修复（#14）：原实现裸 new FileOutputStream 不关闭；本地流改为
		// try-with-resources，异常路径同样保证释放。writeToStream 对入参流的
		// close 语义保持不变。
		try (OutputStream output = new FileOutputStream(outFile)) {
			writeToStream(wmlPackage, output);
		}
	}

	public static void writeToStream(WordprocessingMLPackage wmlPackage, OutputStream output) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(output, " output is not specified!");
		// .docx 是 ZIP 容器：保存整个包而不是把 XML 文本写入文件
		wmlPackage.save(output);
	}

	public void writeToWriter(WordprocessingMLPackage wmlPackage, Writer output) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(output, " output is not specified!");
		//Document对象
		MainDocumentPart document = wmlPackage.getMainDocumentPart();
		//Document XML
		String documentXML = document.getXML();
		//转成字节输入流（input 是本地资源，用 try-with-resources；output 是入参不变）
		try (InputStream input = IOUtils.toBufferedInputStream(new ByteArrayInputStream(documentXML.getBytes()))) {
			//获取模板输出编码格式
			String charsetName = Docx4jProperties.getProperty(Docx4jConstants.DOCX4J_CONVERT_OUT_WMLTEMPLATE_CHARSETNAME, Docx4jConstants.DEFAULT_CHARSETNAME);
			//输出模板
			IOUtils.copy(input, output, Charset.forName(charsetName));
		}
	}

}
