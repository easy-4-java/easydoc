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
package io.github.easy4j.doc.handler;

import org.docx4j.convert.out.ConversionHyperlinkHandler;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.parts.Part;

/**
 * Implementation of output conversion hyperlink handler functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class OutputConversionHyperlinkHandler implements ConversionHyperlinkHandler {

	private static final OutputConversionHyperlinkHandler OUTPUT_CONVERSION_HYPERLINK_HANDLER = new OutputConversionHyperlinkHandler();

	/**
	 * Generate a OutputConversionHyperlinkHandler.
	 * @return the OutputConversionHyperlinkHandler
	 */
	public static OutputConversionHyperlinkHandler getHyperlinkHandler() {
		return OUTPUT_CONVERSION_HYPERLINK_HANDLER;
	}
	
	protected OutputConversionHyperlinkHandler() {
		
	}
	
	@Override
	public void handleHyperlink(Model hyperlinkModel, OpcPackage opcPackage, Part currentPart) throws Docx4JException {
		//do nothing
	}

}
