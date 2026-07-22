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
package io.github.easy4j.doc;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

public class AddingATable {

	private static WordprocessingMLPackage wordMLPackage;
	private static ObjectFactory factory;

	public static void main(String[] args) throws Docx4JException {
		wordMLPackage = WordprocessingMLPackage.createPackage();
		factory = Context.getWmlObjectFactory();

		Tbl table = factory.createTbl();
		Tr tableRow = factory.createTr();

		addTableCell(tableRow, "Field 1");
		addTableCell(tableRow, "Field 2");

		table.getContent().add(tableRow);

		wordMLPackage.getMainDocumentPart().addObject(table);

		wordMLPackage.save(new java.io.File("src/main/files/HelloWord4.docx"));
	}

	private static void addTableCell(Tr tableRow, String content) {
		Tc tableCell = factory.createTc();
		tableCell.getContent().add(
				wordMLPackage.getMainDocumentPart().createParagraphOfText(
						content));
		tableRow.getContent().add(tableCell);
	}
}