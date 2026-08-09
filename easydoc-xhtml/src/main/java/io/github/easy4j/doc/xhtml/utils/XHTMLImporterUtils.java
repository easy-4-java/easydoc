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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.docx4j.Docx4jProperties;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import io.github.easy4j.doc.Docx4jConstants;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

/**
 * Class for XHTMLImporterUtils.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class XHTMLImporterUtils {

	public static WordprocessingMLPackage handle(WordprocessingMLPackage wmlPackage, Document doc,boolean fragment,boolean altChunk) throws IOException, Docx4JException {
		//设置转换模式
		doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml).escapeMode(Entities.EscapeMode.xhtml);  //转为 xhtml 格式
		
		if(altChunk){
			//Document对象
			MainDocumentPart document = wmlPackage.getMainDocumentPart();
			//获取Jsoup参数
			String charsetName = Docx4jProperties.getProperty(Docx4jConstants.DOCX4J_JSOUP_PARSE_CHARSETNAME, Docx4jConstants.DEFAULT_CHARSETNAME );
			//设置转换模式
			doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml).escapeMode(Entities.EscapeMode.xhtml);  //转为 xhtml 格式
			//创建html导入对象
			//XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
			document.addAltChunk(AltChunkType.Xhtml, (fragment ? doc.body().html() : doc.html()) .getBytes(Charset.forName(charsetName)));
			//document.addAltChunk(type, bytes, attachmentPoint)
			//document.addAltChunk(type, is)
			//document.addAltChunk(type, is, attachmentPoint)
			document.convertAltChunks();
				//返回处理后的WordprocessingMLPackage对象
				return wmlPackage;
		}
		
		//创建html导入对象
		XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wmlPackage);
		//将xhtml转换为wmlPackage可用的对象
		List<Object> list = xhtmlImporter.convert((fragment ? doc.body().html() : doc.html()), doc.baseUri());
		//导入转换后的内容对象
		wmlPackage.getMainDocumentPart().getContent().addAll(list);
		//返回原WordprocessingMLPackage对象
		return wmlPackage;
	}
	
}
