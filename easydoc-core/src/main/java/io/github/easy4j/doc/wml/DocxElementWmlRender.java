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
package io.github.easy4j.doc.wml;

import java.util.List;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.utils.WmlElementUtils;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

/**
 * Implementation of docx element wml render functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
public class DocxElementWmlRender {
	
	protected WordprocessingMLPackage wmlPackage;
	protected ObjectFactory factory;
	
	public DocxElementWmlRender(WordprocessingMLPackage wmlPackage){
		this.wmlPackage = wmlPackage;
		this.factory = Context.getWmlObjectFactory();  
	}
	
	public Tc getCell(Tr row,int cell) {
    	return (Tc)row.getContent().get(cell);
	}
	
	public Tr getRow(Tbl table,int row) {
    	return (Tr)table.getContent().get(row);
	}
	
	public Tbl getTable(String placeholder) throws Docx4JException {
		//从文档中搜索执行类型的对象
		List<Tbl> tables = WmlElementUtils.getTargetElements(wmlPackage.getMainDocumentPart(), Tbl.class);
		//返回包含制定占位符的第一个表格对象
    	return WmlElementUtils.getTable(tables, placeholder);
	}
	
	public Tc newCell(Tbl table,int row,String content) {
        Tc tbCell = factory.createTc();
        tbCell.getContent().add(newParagraph(content));
        getRow(table,row).getContent().add(tbCell);
        return tbCell;
    }
	
	public Tc newCell(Tr tableRow,String content) {
        Tc tbCell = factory.createTc();
        tbCell.getContent().add(newParagraph(content));
        tbCell.getContent().add(tbCell);
        return tbCell;
    }
	
	public Tr newRow(Tbl table,int row) {
		Tr tbRow = factory.createTr();
		table.getContent().set(row, tbRow);
        return tbRow;
    }
	
	public Tbl newTable(int row,int cell) {
		//创建表格对象
        Tbl table = factory.createTbl();
        for (int i = 0; i < row; i++) {
        	Tr tableRow = factory.createTr();
        	for (int j = 0; j < cell; j++) {
        		newCell(tableRow, "");
			}
        	table.getContent().add(tableRow);
		}
        return table;
    }
	
	public Tbl newTable(String placeholder) {
		//创建表格对象
        Tbl table = factory.createTbl();
        
        return table;
	}
	
	public P newParagraph(String content) {
        return wmlPackage.getMainDocumentPart().createParagraphOfText(content);
    }
	
}
