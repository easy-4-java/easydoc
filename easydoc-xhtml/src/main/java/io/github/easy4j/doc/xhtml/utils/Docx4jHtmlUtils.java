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
package io.github.easy4j.doc.xhtml.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.io.WordprocessingMLPackageWriter;
import io.github.easy4j.doc.xhtml.io.WordprocessingMLPackageBuilder;

/**
 * TODO
 * @author <a href="https://github.com/easy-4-java">hiwepy</a>
 */
public class Docx4jHtmlUtils {

	protected static WordprocessingMLPackageBuilder WMLPACKAGE_BUILDER = WordprocessingMLPackageBuilder.getWMLPackageBuilder();
	protected static WordprocessingMLPackageWriter WMLPACKAGE_WRITER = WordprocessingMLPackageWriter.getWMLPackageWriter();

	/*
	 * docx文档转换为PDF
	 * 
	 * @param docx 		：docx文档
	 * @param pdfPath	：PDF文档存储路径
	 * @throws Exception： 可能为Docx4JException, FileNotFoundException, IOException等
	 */
	public static void docxToPdf(String docxPath, String pdfPath) throws Exception {
		OutputStream output = null;
		try {
			output = new FileOutputStream(pdfPath);
			
			WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.load(new File(docxPath));

			WMLPACKAGE_BUILDER.configChineseFonts(wmlPackage).configSimSunFont(wmlPackage);
			WMLPACKAGE_WRITER.writeToPDF(wmlPackage, output);

		} finally {
			IOUtils.closeQuietly(output);
		}
	}

	/*
	 * 把docx转成html
	 * @param docxFilePath	：docx文档路径
	 * @param htmlPath		：html输出路径
	 * @throws Exceptionn： 可能为Docx4JException, FileNotFoundException, IOException等
	 */
	public static void docxToHtml(String docxFilePath, String htmlPath) throws Exception {
		OutputStream output = null;
		try {
			//
			WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.load(new File(docxFilePath));

			WMLPACKAGE_BUILDER.configChineseFonts(wmlPackage).configSimSunFont(wmlPackage);
			
			WMLPACKAGE_WRITER.writeToHtml(wmlPackage, htmlPath);

		} finally {
			IOUtils.closeQuietly(output);
		}

	}

}
