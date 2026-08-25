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
import java.io.Writer;

import org.apache.commons.io.output.StringBuilderWriter;
import org.docx4j.TextUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

/**
 * WordprocessingMLPackage text extractor.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLPackageExtractor {

	private static final WordprocessingMLPackageExtractor WML_PACKAGE_EXTRACTOR = new WordprocessingMLPackageExtractor();

	/**
	 * Generate a WordprocessingMLPackageExtractor.
	 * @return the WordprocessingMLPackageExtractor
	 */
	public static WordprocessingMLPackageExtractor getWMLPackageExtractor() {
		return WML_PACKAGE_EXTRACTOR;
	}

	protected WordprocessingMLPackageExtractor() {

	}

	public String extract(String inputfile) throws Exception {
		return this.extract(new File(inputfile));
	}

	public String extract(File inputfile) throws Exception {
		WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.load(inputfile);
		String extracted;
		try (StringBuilderWriter output = new StringBuilderWriter()) {
			this.extract(wmlPackage, output);
			extracted = output.toString();
		}
		return extracted;
	}

	public String extract(WordprocessingMLPackage wmlPackage) throws Exception {
		String extracted;
		try (StringBuilderWriter output = new StringBuilderWriter()) {
			this.extract(wmlPackage, output);
			extracted = output.toString();
		}
		return extracted;
	}

	public void extract(WordprocessingMLPackage wmlPackage, Writer out) throws Exception {
		MainDocumentPart documentPart = wmlPackage.getMainDocumentPart();
		org.docx4j.wml.Document wmlDocumentEl = documentPart.getContents();
		TextUtils.extractText(wmlDocumentEl, out);
	}

}
