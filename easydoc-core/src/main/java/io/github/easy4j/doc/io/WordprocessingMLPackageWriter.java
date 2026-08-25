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
 * WordprocessingMLPackage writer.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLPackageWriter  {

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
	protected final String PDF_SUFFIX = ".pdf";
	protected final String DOCX_SUFFIX = ".docx";
	protected ConversionHyperlinkHandler hyperlinkHandler = OutputConversionHyperlinkHandler.getHyperlinkHandler();
	protected ConversionHTMLStyleElementHandler styleElementHandler = OutputConversionHTMLStyleElementHandler.getStyleElementHandler();
	protected ConversionHTMLScriptElementHandler scriptElementHandler = OutputConversionHTMLScriptElementHandler.getScriptElementHandler();

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

	public File writeToDocx(WordprocessingMLPackage wmlPackage) throws  IOException, Docx4JException{
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		File outFile = new File( Docx4jUtils.getTempPath() + DOCX_SUFFIX );
		return writeToDocx(wmlPackage, outFile);
	}

	public File writeToDocx(WordprocessingMLPackage wmlPackage, String outPath) throws  IOException, Docx4JException{
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(outPath, " outPath is not specified!");
		return writeToDocx(wmlPackage, new File(outPath));
	}

	public File writeToDocx(WordprocessingMLPackage wmlPackage, File outFile) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		writeToDocx(wmlPackage, new FileOutputStream(outFile));
		return outFile;
	}

	public void writeToDocx(WordprocessingMLPackage wmlPackage, OutputStream output) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(output, " output is not specified!");
        // 显式 close 调用方传入的 stream（不吞掉 close 异常 — 与原 IOUtils.closeQuietly
        // 行为不同，但避免屏蔽调用方真实的 I/O 错误，调用方自行 try-finally 即可）
        try {
        	wmlPackage.save(output , Docx4J.FLAG_SAVE_ZIP_FILE );//保存到 docx 文件
		} finally {
			output.close();
        }
	}

	public File writeToHtml(WordprocessingMLPackage wmlPackage) throws IOException, Docx4JException{
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		File outFile = new File( Docx4jUtils.getTempPath() + PDF_SUFFIX );
		return writeToHtml(wmlPackage, outFile);
	}

	public File writeToHtml(WordprocessingMLPackage wmlPackage, String outPath) throws  IOException, Docx4JException{
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(outPath, " outPath is not specified!");
		return writeToHtml(wmlPackage, new File(outPath));
	}

	public File writeToHtml(WordprocessingMLPackage wmlPackage, File outFile) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		// 方法语义是写入目录：先校验是目录，避免下面 outFile.listFiles(...) 返回 null 导致 NPE
		if (!outFile.isDirectory()) {
			throw new IllegalArgumentException("outFile must be a directory: " + outFile);
		}
		String imageTargetUri = Docx4jProperties.getProperty(Docx4jConstants.DOCX4J_CONVERT_OUT_HTML_IMAGETARGETURI, "images");
		File[] files = outFile.listFiles(new OutputDirFilterHandler(imageTargetUri));
		if(files.length != 1){
			File imageDir = new File(outFile, imageTargetUri);
			imageDir.setWritable(true);
			imageDir.setReadable(true);
			imageDir.mkdir();
		}
		// 本地资源用 try-with-resources 保证 close + 自动 flush
		try (OutputStream output = new FileOutputStream(outFile)) {
			HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
			htmlSettings.setImageDirPath(outFile.getParent());
			htmlSettings.setImageTargetUri(imageTargetUri);
			htmlSettings.setWmlPackage(wmlPackage);

			htmlSettings.setHyperlinkHandler(getHyperlinkHandler());
			htmlSettings.setScriptElementHandler(getScriptElementHandler());
			htmlSettings.setStyleElementHandler(getStyleElementHandler());

			Docx4J.toHTML(htmlSettings, output, Docx4J.FLAG_EXPORT_PREFER_XSL);
		}

		return outFile;
	}

	public File writeToPDF(WordprocessingMLPackage wmlPackage) throws  IOException, Docx4JException{
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		File outFile = new File( Docx4jUtils.getTempPath() + PDF_SUFFIX );
		return writeToPDF(wmlPackage, outFile);
	}

	public File writeToPDF(WordprocessingMLPackage wmlPackage,String outPath) throws  IOException, Docx4JException{
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(outPath, " outPath is not specified!");
		return writeToPDF(wmlPackage, new File(outPath));
	}

	public File writeToPDF(WordprocessingMLPackage wmlPackage,File outFile) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		writeToPDF(wmlPackage, new FileOutputStream(outFile));
		return outFile;
	}

	public void writeToPDF(WordprocessingMLPackage wmlPackage,OutputStream output) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(output, " output is not specified!");
        // 显式 close 调用方传入的 stream（同 writeToDocx 语义）
        try {
			Docx4J.toPDF(wmlPackage, output); //保存到 pdf 文件
			output.flush();
		} finally {
			output.close();
        }
	}

	/**
	 * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 pdf
	 * （使用 FO 转换方式）
	 * @param wmlPackage {@link WordprocessingMLPackage} 对象
	 * @param output 文件输出流
	 * @throws IOException ：IO异常
	 * @throws Docx4JException ： Docx4j异常
	 */
	public void writeToPDFWithFo(WordprocessingMLPackage wmlPackage, OutputStream output) throws IOException, Docx4JException {
		Assert.notNull(wmlPackage, " wmlPackage is not specified!");
		Assert.notNull(output, " output is not specified!");
		try {
			// Refresh the values of DOCPROPERTY fields
			FieldUpdater updater = new FieldUpdater(wmlPackage);
			updater.update(true);

			PhysicalFonts.get("Arial Unicode MS");

			// FO exporter setup (required)
			FOSettings foSettings = Docx4J.createFOSettings();
			foSettings.setWmlPackage(wmlPackage);
			foSettings.setApacheFopMime("application/pdf");

			Docx4J.toFO(foSettings, output, Docx4J.FLAG_EXPORT_PREFER_XSL);
		} finally {
			output.close();
		}
	}

	/**
	 * @deprecated Use {@link #writeToPDFWithFo(WordprocessingMLPackage, OutputStream)} instead (corrected spelling).
	 */
	@Deprecated
	public void writeToPDFWhithFo(WordprocessingMLPackage wmlPackage, OutputStream output) throws IOException, Docx4JException {
		writeToPDFWithFo(wmlPackage, output);
	}

	public ConversionHyperlinkHandler getHyperlinkHandler() {
		return hyperlinkHandler;
	}

	public void setHyperlinkHandler(ConversionHyperlinkHandler hyperlinkHandler) {
		this.hyperlinkHandler = hyperlinkHandler;
	}

	public ConversionHTMLStyleElementHandler getStyleElementHandler() {
		return styleElementHandler;
	}

	public void setStyleElementHandler(ConversionHTMLStyleElementHandler styleElementHandler) {
		this.styleElementHandler = styleElementHandler;
	}

	public ConversionHTMLScriptElementHandler getScriptElementHandler() {
		return scriptElementHandler;
	}

	public void setScriptElementHandler(ConversionHTMLScriptElementHandler scriptElementHandler) {
		this.scriptElementHandler = scriptElementHandler;
	}

}
