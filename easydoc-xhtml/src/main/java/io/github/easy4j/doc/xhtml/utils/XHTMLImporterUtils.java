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
 * Implementation of x h t m l importer utils functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class XHTMLImporterUtils {

	/**
	 * 串行化锁：JAXP 系统属性是 JVM 全局状态，并发调用会互相覆盖/恢复彼此的
	 * "原值"（读-改-恢复非原子）。docx4j ImportXHTML 内部自行创建 XMLReader，
	 * 无法按调用注入 EntityResolver，系统属性兜底不可回避，故以类级锁串行化
	 * 属性窗口；吞吐换正确性。
	 */
	private static final Object JAXP_PROPERTY_LOCK = new Object();

	private static final String DBF_PROPERTY = "javax.xml.parsers.DocumentBuilderFactory";

	public static WordprocessingMLPackage handle(WordprocessingMLPackage wmlPackage, Document doc,boolean fragment,boolean altChunk) throws IOException, Docx4JException {
		// XXE 防护（第二层）：临时将 DocumentBuilderFactory 系统属性指向
		// SecureDocumentBuilderFactory，使 docx4j 内部 DocumentBuilderFactory.newInstance()
		// 加载我们的安全工厂——在构造器内统一应用 disallow-doctype-decl /
		// FEATURE_SECURE_PROCESSING / 关闭外部实体与 XInclude。
		// 此属性窗口在 accessExternalDTD/SCHEMA 序列化窗口外层，避免持有
		// JAXP_PROPERTY_LOCK 期间扩大锁范围。
		String oldDbf = System.getProperty(DBF_PROPERTY);
		System.setProperty(DBF_PROPERTY, SecureDocumentBuilderFactory.class.getName());
		try {
			// XXE 防护（第一层）：docx4j-ImportXHTML 内部的 openhtmltopdf 尝试在 XMLReader 上
			// setProperty(ACCESS_EXTERNAL_DTD) 时被 JDK 解析器拒绝（"不支持"），打
			// "Unable to disable XML External Entities" SEVERE 警告。这里通过 JAXP
			// 系统属性兜底：解析器创建时读取系统属性作为默认，外部 DTD/Schema 被
			// 真正禁止（空串 = 不允许任何外部访问），finally 恢复调用方原值。
			//
			// ⚠ 线程敌对契约：System.setProperty 本身就是 JVM 全局副作用。即便有
			// try/finally 恢复 + 类级锁保护本方法内部的属性窗口，以下情况仍无法防御：
			// （1）用户代码在 handle 执行期间读写同名属性；（2）解析器在窗口外延迟读取
			// 属性。若业务方直接依赖这两个属性的精确取值，应避开与本方法并发执行，
			// 或改为在容器层面统一配置解析器工厂。
			synchronized (JAXP_PROPERTY_LOCK) {
				String oldDtd = System.getProperty("javax.xml.accessExternalDTD");
				String oldSchema = System.getProperty("javax.xml.accessExternalSchema");
				System.setProperty("javax.xml.accessExternalDTD", "");
				System.setProperty("javax.xml.accessExternalSchema", "");
				try {
					return handleInternal(wmlPackage, doc, fragment, altChunk);
				} finally {
					restore("javax.xml.accessExternalDTD", oldDtd);
					restore("javax.xml.accessExternalSchema", oldSchema);
				}
			}
		} finally {
			// 恢复 DocumentBuilderFactory 系统属性（caller 原值优先）
			restore(DBF_PROPERTY, oldDbf);
		}
	}

	private static void restore(String key, String value) {
		if (value == null) {
			System.clearProperty(key);
		} else {
			System.setProperty(key, value);
		}
	}

	private static WordprocessingMLPackage handleInternal(WordprocessingMLPackage wmlPackage, Document doc,boolean fragment,boolean altChunk) throws IOException, Docx4JException {
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
