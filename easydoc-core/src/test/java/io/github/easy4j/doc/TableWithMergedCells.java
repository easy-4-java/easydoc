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
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.TcPrInner.VMerge;
import org.docx4j.wml.Tr;

/**
 * @className	： TableWithMergedCells
 * @description	： 纵向合并单元格
 * @author 		： <a href="https://github.com/easy-4-java">hiwepy</a>
 * @date		： 2017年5月24日 下午10:40:35
 * @version 	V1.0
 */
public class TableWithMergedCells {
    private static WordprocessingMLPackage  wordMLPackage;
    private static ObjectFactory factory;
 
    /**
     *  创建一个带边框的表格并添加四个带内容的行, 然后将表格添加到文档并保存
     */
    public static void main (String[] args) throws Docx4JException {
        wordMLPackage = WordprocessingMLPackage.createPackage();
        factory = Context.getWmlObjectFactory();
 
        Tbl table = factory.createTbl();
        addBorders(table);
 
        addTableRowWithMergedCells("Heading 1", "Heading 1.1",
            "Field 1", table);
        addTableRowWithMergedCells(null, "Heading 1.2", "Field 2", table);
 
        addTableRowWithMergedCells("Heading 2", "Heading 2.1",
            "Field 3", table);
        addTableRowWithMergedCells(null, "Heading 2.2", "Field 4", table);
 
        wordMLPackage.getMainDocumentPart().addObject(table);
        wordMLPackage.save(new java.io.File(
            "src/main/files/HelloWord9.docx") );
    }
 
    /**
     *  本方法创建一行, 并向其中添加合并列, 然后添加再两个普通的单元格. 随后将该行添加到表格
     */
    private static void addTableRowWithMergedCells(String mergedContent,
            String field1Content, String field2Content, Tbl table) {
        Tr tableRow1 = factory.createTr();
 
        addMergedColumn(tableRow1, mergedContent);
 
        addTableCell(tableRow1, field1Content);
        addTableCell(tableRow1, field2Content);
 
        table.getContent().add(tableRow1);
    }
 
    /**
     *  本方法添加一个合并了其它行单元格的列单元格. 如果传进来的内容是null, 传空字符串和一个为null的合并值.
     */
    private static void addMergedColumn(Tr row, String content) {
        if (content == null) {
            addMergedCell(row, "", null);
        } else {
            addMergedCell(row, content, "restart");
        }
    }
 
    /**
     *  我们创建一个单元格和单元格属性对象.
     *  也创建了一个纵向合并对象. 如果合并值不为null, 将它设置到合并对象中. 然后将该对象添加到
     *  单元格属性并将属性添加到单元格中. 最后设置单元格内容并将单元格添加到行中.
     *  
     *  如果合并值为'restart', 表明要开始一个新行. 如果为null, 继续按前面的行处理, 也就是合并单元格.
     */
    private static void addMergedCell(Tr row, String content, String vMergeVal) {
        Tc tableCell = factory.createTc();
        TcPr tableCellProperties = new TcPr();
 
        VMerge merge = new VMerge();
        if(vMergeVal != null){
            merge.setVal(vMergeVal);
        }
        tableCellProperties.setVMerge(merge);
 
        tableCell.setTcPr(tableCellProperties);
        if(content != null) {
                tableCell.getContent().add(
                wordMLPackage.getMainDocumentPart().
                    createParagraphOfText(content));
        }
 
        row.getContent().add(tableCell);
    }
 
    /**
     *  本方法为给定的行添加一个单元格, 并以给定的段落作为内容
     */
    private static void addTableCell(Tr tr, String content) {
        Tc tc1 = factory.createTc();
        tc1.getContent().add(
            wordMLPackage.getMainDocumentPart().createParagraphOfText(content));
        tr.getContent().add(tc1);
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
