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

import java.math.BigInteger;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.Tr;

/**
 * 
 * @className	： SettingColumnWidthForTable
 * @description	： 为表格设置列宽
 * @author 		： <a href="https://github.com/easy-4-java">hiwepy</a>
 * @date		： 2017年5月24日 下午10:40:04
 * @version 	V1.0
 */
public class SettingColumnWidthForTable {
	
    private static WordprocessingMLPackage  wordMLPackage;
    private static ObjectFactory factory;
 
    /**
     *  创建一个带边框的表格并添加一行. 然后添加两个带内容的单元格并给定宽度.
     */
    public static void main (String[] args) throws Docx4JException {
        wordMLPackage = WordprocessingMLPackage.createPackage();
        factory = Context.getWmlObjectFactory();
 
        Tbl table = factory.createTbl();
        addBorders(table);
 
        Tr tr = factory.createTr();
 
        addTableCellWithWidth(tr, "Field 1", 2500);
        addTableCellWithWidth(tr, "Field 2", 0);
 
        table.getContent().add(tr);
 
        wordMLPackage.getMainDocumentPart().addObject(table);
        wordMLPackage.save(new java.io.File("src/main/HelloWord133.docx") );
    }
 
    /**
     *  本方法创建一个单元格并将给定的内容添加进去.
     *  如果给定的宽度大于0, 将这个宽度设置到单元格.
     *  最后, 将单元格添加到行中.
     */
    private static void addTableCellWithWidth(Tr row, String content, int width){
        Tc tableCell = factory.createTc();
        tableCell.getContent().add(
            wordMLPackage.getMainDocumentPart().createParagraphOfText(
                content));
 
        if (width > 0) {
            setCellWidth(tableCell, width);
        }
        row.getContent().add(tableCell);
    }
 
    /**
     *  本方法创建一个单元格属性集对象和一个表格宽度对象. 将给定的宽度设置到宽度对象然后将其添加到
     *  属性集对象. 最后将属性集对象设置到单元格中.
     */
    private static void setCellWidth(Tc tableCell, int width) {
        TcPr tableCellProperties = new TcPr();
        TblWidth tableWidth = new TblWidth();
        tableWidth.setW(BigInteger.valueOf(width));
        tableCellProperties.setTcW(tableWidth);
        tableCell.setTcPr(tableCellProperties);
    }
 
    /**
     *  本方法为表格添加边框
     */
    private static void addBorders(Tbl table) {
        table.setTblPr(new TblPr());
        CTBorder border = new CTBorder();
        border.setColor("auto");
        border.setSz(new BigInteger("4"));
        border.setSpace(new BigInteger("0"));
        border.setVal(STBorder.SINGLE);
 
        TblBorders borders = new TblBorders();
        borders.setBottom(border);
        borders.setLeft(border);
        borders.setRight(border);
        borders.setTop(border);
        borders.setInsideH(border);
        borders.setInsideV(border);
        table.getTblPr().setTblBorders(borders);
    }
}