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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.ConversionHTMLScriptElementHandler;
import org.docx4j.convert.out.ConversionHTMLStyleElementHandler;
import org.docx4j.convert.out.ConversionHyperlinkHandler;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.handler.OutputConversionHTMLScriptElementHandler;
import io.github.easy4j.doc.handler.OutputConversionHTMLStyleElementHandler;
import io.github.easy4j.doc.handler.OutputConversionHyperlinkHandler;
import io.github.easy4j.doc.handler.OutputDirFilterHandler;
import io.github.easy4j.doc.utils.Assert;
import io.github.easy4j.doc.utils.Docx4jUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link WordprocessingMLPackage} 导出器（docx / html / pdf）。
 *
 * <p><b>线程契约（#10/#12 修复说明，port from 3.0.x）</b>：本类默认以单例提供
 * （{@link #getWMLPackageWriter()}），无参导出重载仅使用局部状态，
 * 不再修改任何 docx4j 全局静态属性（见 {@link #writeToHtml(WordprocessingMLPackage, File)}
 * 内注释）。三个可替换处理器字段（hyperlink / styleElement / scriptElement）
 * 属共享可变状态：字段声明为 {@code volatile} 保证跨线程可见，写入口
 * （setter）以类内 {@code handlerLock} 监视器同步互斥。需注意“读取处理器组合→
 * 执行转换”并非原子事务：并发替换处理器与转换过程重叠时，不保证某次转换使用
 * 完全一致的三个处理器组合。为保证既有 API 兼容而保留可变设计而非改为不可变
 * 工厂；需要严格隔离的多线程调用方可自建实例。getter 返回的是外部传入的
 * 处理器实例，其线程安全性由实现方自行保证。</p>
 */
public class WordprocessingMLPackageWriter  {

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
	protected final String PDF_SUFFIX = ".pdf";
	protected final String DOCX_SUFFIX = ".docx";
	// P1 缺陷修复（#13）：原 writeToHtml(pkg) 无路径重载误用 PDF_SUFFIX，
	// 导致生成的是 .pdf 扩展名的 html 文件；补充专用后缀常量。
	protected final String HTML_SUFFIX = ".html";
	// 处理器字段的写锁（#10）：并发 setter 互斥由 synchronized 保证；
	// 字段本身的 volatile 保证读端（转换路径）的可见性。
	private final Object handlerLock = new Object();
	protected volatile ConversionHyperlinkHandler hyperlinkHandler = OutputConversionHyperlinkHandler.getHyperlinkHandler();
	protected volatile ConversionHTMLStyleElementHandler styleElementHandler = OutputConversionHTMLStyleElementHandler.getStyleElementHandler();
	protected volatile ConversionHTMLScriptElementHandler scriptElementHandler = OutputConversionHTMLScriptElementHandler.getScriptElementHandler();
	
	private static final WordprocessingMLPackageWriter WML_PACKAGE_WRITER = new WordprocessingMLPackageWriter();

	/**
	 * Generate a WordprocessingMLPackageWriter.
	 * @return the WordprocessingMLPackageWriter
	 */
	public static WordprocessingMLPackageWriter getWMLPackageWriter() {
		return WML_PACKAGE_WRITER;
	}
	
	protected WordprocessingMLPackageWriter() {
		
	}
	
	/**
	 * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 docx
	 * @param wmlPackage {@link WordprocessingMLPackage} 对象
	 * @return {@link File} docx 文档
	 * @throws IOException ：IO异常
	 * @throws Docx4JException ： Docx4j异常
	 */
	public File writeToDocx(WordprocessingMLPackage wmlPackage) throws  IOException, Docx4JException{
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		// P1 资源修复（#14）：改用 createTempFile 原子命名，消除毫秒级路径碰撞
		File outFile = Docx4jUtils.newTempOutputFile(DOCX_SUFFIX);
		return writeToDocx(wmlPackage, outFile);
	}
	
	/**
	 * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 docx
	 * @param wmlPackage {@link WordprocessingMLPackage} 对象
	 * @param outPath 文件输出路径
	 * @return {@link File} docx 文档
	 * @throws IOException ：IO异常
	 * @throws Docx4JException ： Docx4j异常
	 */
	public File writeToDocx(WordprocessingMLPackage wmlPackage, String outPath) throws  IOException, Docx4JException{
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(outPath, " outPath is not specified!");
		return writeToDocx(wmlPackage, new File(outPath));
	}
	
	/**
	 * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 docx
	 * @param wmlPackage {@link WordprocessingMLPackage} 对象
	 * @param outFile 文件输出路径
	 * @return {@link File} docx 文档
	 * @throws IOException ：IO异常
	 * @throws Docx4JException ： Docx4j异常
	 */
	public File writeToDocx(WordprocessingMLPackage wmlPackage, File outFile) throws IOException, Docx4JException {
		writeToDocx(wmlPackage, new FileOutputStream(outFile));
		return outFile;
	}
	
	/**
	 * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 docx
	 * @param wmlPackage {@link WordprocessingMLPackage} 对象
	 * @param output 文件输出流
	 * @throws IOException ：IO异常
	 * @throws Docx4JException ： Docx4j异常
	 */
	public void writeToDocx(WordprocessingMLPackage wmlPackage,OutputStream output) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(output, " output is not specified!");
        try {
        	wmlPackage.save(output , Docx4J.FLAG_SAVE_ZIP_FILE );//保存到 docx 文件
		} finally {
			output.close();
        }
	}
	
	/**
	 * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 html
	 * @param wmlPackage {@link WordprocessingMLPackage} 对象
	 * @return {@link File} docx 文档
	 * @throws IOException ：IO异常
	 * @throws Docx4JException ： Docx4j异常
	 */
	public File writeToHtml(WordprocessingMLPackage wmlPackage) throws IOException, Docx4JException{
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		// P1 缺陷修复（#13）：原实现误用 PDF_SUFFIX（生成 .pdf 后缀的 html 文件）；
		// P1 资源修复（#14）：同时改用 createTempFile 原子命名避免碰撞
		File outFile = Docx4jUtils.newTempOutputFile(HTML_SUFFIX);
		return writeToHtml(wmlPackage, outFile);
	}
	
	/**
	 * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 html
	 * @param wmlPackage {@link WordprocessingMLPackage} 对象
	 * @param outPath 文件输出路径
	 * @return {@link File} docx 文档
	 * @throws IOException ：IO异常
	 * @throws Docx4JException ： Docx4j异常
	 */
	public File writeToHtml(WordprocessingMLPackage wmlPackage, String outPath) throws  IOException, Docx4JException{
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(outPath, " outPath is not specified!");
		return writeToHtml(wmlPackage, new File(outPath));
	}
	
	/**
	 * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 html
	 * <p>缺陷修复后语义已统一：{@code outFile} 即待写入的目标 html 文件，
	 * 与 {@link #writeToDocx(WordprocessingMLPackage, File)}、
	 * {@link #writeToPDF(WordprocessingMLPackage, File)} 保持一致，不再要求其为目录。
	 * 父目录不存在时自动创建（{@link Files#createDirectories}）；
	 * 若 {@code outFile} 为已存在的目录则抛出 {@link IOException}。</p>
	 * @param wmlPackage {@link WordprocessingMLPackage} 对象
	 * @param outFile 目标 html 文件（父目录自动创建；不可为已存在的目录）
	 * @return {@link File} html 文档
	 * @throws IOException ：IO异常
	 * @throws Docx4JException ： Docx4j异常
	 */
	public File writeToHtml(WordprocessingMLPackage wmlPackage,File outFile) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(outFile, " outFile is not specified!");
		// 缺陷修复：File 参数统一语义为“目标文件”，不可为已存在的目录（目录无法作为文件写出）
		if (outFile.isDirectory()) {
			throw new IOException("outFile must be a file, but is a directory: " + outFile);
		}
		// 图片资源目录以目标 html 文件所在目录为基准；父目录不存在时自动创建而不是失败
		File baseDir = outFile.getAbsoluteFile().getParentFile();
		Assert.notNull(baseDir, " outFile has no parent directory: " + outFile);
		Files.createDirectories(baseDir.toPath());
		String imageTargetUri = Docx4jProperties.getProperty(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_IMAGETARGETURI, "images");
		// 在基准目录下查找名为 imageTargetUri 的子目录，缺失时补建
		File[] files = baseDir.listFiles(new OutputDirFilterHandler(imageTargetUri));
		if (files == null || files.length != 1) {
			File imageDir = new File(baseDir, imageTargetUri);
			imageDir.setWritable(true);
			imageDir.setReadable(true);
			imageDir.mkdir();
		}
		// 本地资源用 try-with-resources 保证 close + 自动 flush（与 1.0.x/3.0.x 对齐）
		try (OutputStream output = new FileOutputStream(outFile)) {
			//创建Html输出设置
			HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
			htmlSettings.setImageDirPath(baseDir.getPath());
			htmlSettings.setImageTargetUri(imageTargetUri);
			htmlSettings.setWmlPackage(wmlPackage);

			htmlSettings.setHyperlinkHandler(getHyperlinkHandler());
			htmlSettings.setScriptElementHandler(getScriptElementHandler());
			htmlSettings.setStyleElementHandler(getStyleElementHandler());

			// 线程安全修复（#12，port from 3.0.x）：原实现对全局静态 Docx4jProperties 写入
			// DOCX4J_PARAM_04（"docx4j.Convert.Out.HTML.OutputMethodXM"，注意末尾缺少
			// 结尾字母 L），形成并发下的全局竞态窗口。已核实 docx4j 11.5.14
			// （docx4j-core-11.5.14-sources.jar 的 HTMLExporterXslt 及其二进制字节码）：
			// 运行期实际读取的键是 "docx4j.Convert.Out.HTML.OutputMethodXML"（带 L），
			// 且默认值为 true；原写入因拼写错位从未被 docx4j 读到，属无效操作。
			// 据此删除该全局变更可保持行为完全不变（对 docx4j 而言该键等价于恒未设置），
			// 同时彻底消除线程敌性窗口。若将来确需启用 OutputMethodXML 相关配置，
			// 应由应用启动时显式配置 Docx4jProperties，而非在单次转换中临时改写全局态。
			//Docx4J.toHTML(settings, outputStream, flags);
			//Docx4J.toHTML(wmlPackage, imageDirPath, imageTargetUri, outputStream);
			Docx4J.toHTML(htmlSettings, output, Docx4J.FLAG_EXPORT_PREFER_XSL);
		}

		return outFile;
	}
	
	/**
	 * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 pdf
	 * @param wmlPackage {@link WordprocessingMLPackage} 对象
	 * @return {@link File} docx 文档
	 * @throws IOException ：IO异常
	 * @throws Docx4JException ： Docx4j异常
	 */
	public File writeToPDF(WordprocessingMLPackage wmlPackage) throws  IOException, Docx4JException{
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		// P1 资源修复（#14）：改用 createTempFile 原子命名，消除毫秒级路径碰撞
		File outFile = Docx4jUtils.newTempOutputFile(PDF_SUFFIX);
		return writeToPDF(wmlPackage, outFile);
	}
	
	/**
	 * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 pdf
	 * @param wmlPackage {@link WordprocessingMLPackage} 对象
	 * @param outPath 文件输出路径
	 * @return {@link File} docx 文档
	 * @throws IOException ：IO异常
	 * @throws Docx4JException ： Docx4j异常
	 */
	public File writeToPDF(WordprocessingMLPackage wmlPackage,String outPath) throws  IOException, Docx4JException{
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(outPath, " outPath is not specified!");
		return writeToPDF(wmlPackage, new File(outPath));
	}
	
	/**
	 * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 pdf
	 * @param wmlPackage {@link WordprocessingMLPackage} 对象
	 * @param outFile 文件输出路径
	 * @return {@link File} docx 文档
	 * @throws IOException ：IO异常
	 * @throws Docx4JException ： Docx4j异常
	 */
	public File writeToPDF(WordprocessingMLPackage wmlPackage,File outFile) throws IOException, Docx4JException {
		writeToPDF(wmlPackage, new FileOutputStream(outFile));
		return outFile;
	}
	
	/**
	 * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 pdf
	 * @param wmlPackage {@link WordprocessingMLPackage} 对象
	 * @param output 文件输出流
	 * @throws IOException ：IO异常
	 * @throws Docx4JException ： Docx4j异常
	 */
	public void writeToPDF(WordprocessingMLPackage wmlPackage,OutputStream output) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(output, " output is not specified!");
        try {
			Docx4J.toPDF(wmlPackage, output); //保存到 pdf 文件
			output.flush();
		} finally {
			output.close();
        }
	}
	
	/**
	 * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 pdf
	 * @param wmlPackage {@link WordprocessingMLPackage} 对象
	 * @param output 文件输出流
	 * @throws IOException ：IO异常
	 * @throws Docx4JException ： Docx4j异常
	 */
	public void writeToPDFWhithFo(WordprocessingMLPackage wmlPackage, OutputStream output) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(output, " output is not specified!");
        try {
        	
			// Font regex (optional)
			// Set regex if you want to restrict to some defined subset of fonts
			// Here we have to do this before calling createContent,
			// since that discovers fonts
			//String regex = null;
			
			// Refresh the values of DOCPROPERTY fields 
			FieldUpdater updater = new FieldUpdater(wmlPackage);
			updater.update(true);
			
			// .. example of mapping font Times New Roman which doesn't have certain Arabic glyphs
			// eg Glyph "ي" (0x64a, afii57450) not available in font "TimesNewRomanPS-ItalicMT".
			// eg Glyph "ج" (0x62c, afii57420) not available in font "TimesNewRomanPS-ItalicMT".
			// to a font which does
			PhysicalFonts.get("Arial Unicode MS"); 
	
			// FO exporter setup (required)
			// .. the FOSettings object
		    FOSettings foSettings = Docx4J.createFOSettings();
		    
			foSettings.setWmlPackage(wmlPackage);
	        foSettings.setApacheFopMime("application/pdf");
	            
			// Document format: 
			// The default implementation of the FORenderer that uses Apache Fop will output
			// a PDF document if nothing is passed via 
			// foSettings.setApacheFopMime(apacheFopMime)
			// apacheFopMime can be any of the output formats defined in org.apache.fop.apps.MimeConstants eg org.apache.fop.apps.MimeConstants.MIME_FOP_IF or
			// FOSettings.INTERNAL_FO_MIME if you want the fo document as the result.
			//foSettings.setApacheFopMime(FOSettings.INTERNAL_FO_MIME);
			
			// Specify whether PDF export uses XSLT or not to create the FO
			// (XSLT takes longer, but is more complete).
			
			// Don't care what type of exporter you use
			Docx4J.toFO(foSettings, output, Docx4J.FLAG_EXPORT_PREFER_XSL);
			
			// Prefer the exporter, that uses a xsl transformation
			// Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);
			
			// Prefer the exporter, that doesn't use a xsl transformation (= uses a visitor)
			// faster, but not yet at feature parity
			// Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_NONXSL);
			   
			// Clean up, so any ObfuscatedFontPart temp files can be deleted 
			// if (wordMLPackage.getMainDocumentPart().getFontTablePart()!=null) {
			// 	wordMLPackage.getMainDocumentPart().getFontTablePart().deleteEmbeddedFontTempFiles();
			// } 
			// This would also do it, via finalize() methods
			updater = null;
			foSettings = null;
			wmlPackage = null;
		} finally {
			output.close();
        }
	}

	public ConversionHyperlinkHandler getHyperlinkHandler() {
		return hyperlinkHandler;
	}

	/** 线程契约：以 handlerLock 同步写入；字段为 volatile，读端立即可见（见类注释）。 */
	public void setHyperlinkHandler(ConversionHyperlinkHandler hyperlinkHandler) {
		synchronized (handlerLock) {
			this.hyperlinkHandler = hyperlinkHandler;
		}
	}

	public ConversionHTMLStyleElementHandler getStyleElementHandler() {
		return styleElementHandler;
	}

	/** 线程契约：以 handlerLock 同步写入；字段为 volatile，读端立即可见（见类注释）。 */
	public void setStyleElementHandler(ConversionHTMLStyleElementHandler styleElementHandler) {
		synchronized (handlerLock) {
			this.styleElementHandler = styleElementHandler;
		}
	}

	public ConversionHTMLScriptElementHandler getScriptElementHandler() {
		return scriptElementHandler;
	}

	/** 线程契约：以 handlerLock 同步写入；字段为 volatile，读端立即可见（见类注释）。 */
	public void setScriptElementHandler(ConversionHTMLScriptElementHandler scriptElementHandler) {
		synchronized (handlerLock) {
			this.scriptElementHandler = scriptElementHandler;
		}
	}
	
}
