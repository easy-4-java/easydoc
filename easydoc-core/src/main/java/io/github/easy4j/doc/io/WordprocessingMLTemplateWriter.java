/*
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
		StringBuilderWriter output = new StringBuilderWriter();
		try {
			this.writeToWriter(wmlPackage, output);
		} finally {
			IOUtils.closeQuietly(output);
		}
		return output.toString();
	}
	
	public String writeToString(WordprocessingMLPackage wmlPackage) throws IOException, Docx4JException {
		MainDocumentPart document = wmlPackage.getMainDocumentPart();		
		return XmlUtils.marshaltoString(wmlPackage);
	}
	
	public static void writeToFile(WordprocessingMLPackage wmlPackage,File outFile) throws IOException, Docx4JException {
		writeToStream(wmlPackage, new FileOutputStream(outFile));
	}
	
	public static void writeToStream(WordprocessingMLPackage wmlPackage,OutputStream output) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(output, " output is not specified!");
		InputStream input = null;
		try {
			//Document对象
			MainDocumentPart document = wmlPackage.getMainDocumentPart();	
			//Document XML
			String documentXML = XmlUtils.marshaltoString(wmlPackage);
			//转成字节输入流
			input = IOUtils.toBufferedInputStream(new ByteArrayInputStream(documentXML.getBytes()));
			//输出模板
			IOUtils.copy(input, output);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
	
	public void writeToWriter(WordprocessingMLPackage wmlPackage,Writer output) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(output, " output is not specified!");
		InputStream input = null;
		try {
			//Document对象
			MainDocumentPart document = wmlPackage.getMainDocumentPart();	
			//Document XML
			String documentXML = XmlUtils.marshaltoString(wmlPackage.getPackage());
			//转成字节输入流
			input = IOUtils.toBufferedInputStream(new ByteArrayInputStream(documentXML.getBytes()));
			//获取模板输出编码格式
			String charsetName = Docx4jProperties.getProperty(Docx4jConstants.DOCX4J_CONVERT_OUT_WMLTEMPLATE_CHARSETNAME, Docx4jConstants.DEFAULT_CHARSETNAME );
			//输出模板
			IOUtils.copy(input, output, Charset.forName(charsetName));
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
	
}
