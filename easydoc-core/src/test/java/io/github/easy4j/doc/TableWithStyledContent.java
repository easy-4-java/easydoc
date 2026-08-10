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
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;

/**
 * @className	： TableWithStyledContent
 * @description	：给表格添加样式
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @date		： 2017年5月24日 下午10:40:51
 * @version 	V1.0
 */
public class TableWithStyledContent {
    private static WordprocessingMLPackage  wordMLPackage;
    private static ObjectFactory factory;
 
    /**
      *  跟前面的做的一样, 我们再一次创建了一个表格, 并添加了三个单元格, 其中有两个
      *  单元带有样式. 在新方法中我们传进表格行, 单元格内容, 是否为粗体及字体大小作
      *  为参数. 你需要注意, 因为the Office Open specification规范定义这个属性是半个
      *  点(half-point)大小, 因此字体大小需要是你想在Word中显示大小的两倍, 
     */
    public static void main (String[] args) throws Docx4JException {
        wordMLPackage = WordprocessingMLPackage.createPackage();
        factory = Context.getWmlObjectFactory();
 
        Tbl table = factory.createTbl();
        Tr tableRow = factory.createTr();
 
        addRegularTableCell(tableRow, "Normal text");
        addStyledTableCell(tableRow, "Bold text", true, null);
        addStyledTableCell(tableRow, "Bold large text", true, "40");
 
        table.getContent().add(tableRow);
        addBorders(table);
 
        wordMLPackage.getMainDocumentPart().addObject(table);
        wordMLPackage.save(new java.io.File("src/main/files/HelloWord6.docx") );
    }
 
    /**
     *  本方法创建单元格, 添加样式后添加到表格行中
     */
    private static void addStyledTableCell(Tr tableRow, String content,
                        boolean bold, String fontSize) {
        Tc tableCell = factory.createTc();
        addStyling(tableCell, content, bold, fontSize);
        tableRow.getContent().add(tableCell);
    }
 
    /**
     *  这里我们添加实际的样式信息, 首先创建一个段落, 然后创建以单元格内容作为值的文本对象; 
     *  第三步, 创建一个被称为运行块的对象, 它是一块或多块拥有共同属性的文本的容器, 并将文本对象添加
     *  到其中. 随后我们将运行块R添加到段落内容中.
     *  直到现在我们所做的还没有添加任何样式, 为了达到目标, 我们创建运行块属性对象并给它添加各种样式.
     *  这些运行块的属性随后被添加到运行块. 最后段落被添加到表格的单元格中.
     */
    private static void addStyling(Tc tableCell, String content, boolean bold, String fontSize) {
        P paragraph = factory.createP();
 
        Text text = factory.createText();
        text.setValue(content);
 
        R run = factory.createR();
        run.getContent().add(text);
 
        paragraph.getContent().add(run);
 
        RPr runProperties = factory.createRPr();
        if (bold) {
            addBoldStyle(runProperties);
        }
 
        if (fontSize != null && !fontSize.isEmpty()) {
            setFontSize(runProperties, fontSize);
        }
 
        run.setRPr(runProperties);
 
        tableCell.getContent().add(paragraph);
    }
 
    /**
     *  本方法为可运行块添加字体大小信息. 首先创建一个"半点"尺码对象, 然后设置fontSize
     *  参数作为该对象的值, 最后我们分别设置sz和szCs的字体大小.
     *  Finally we'll set the non-complex and complex script font sizes, sz and szCs respectively.
     */
    private static void setFontSize(RPr runProperties, String fontSize) {
        HpsMeasure size = new HpsMeasure();
        size.setVal(new BigInteger(fontSize));
        runProperties.setSz(size);
        runProperties.setSzCs(size);
    }
 
    /**
     *  本方法给可运行块属性添加粗体属性. BooleanDefaultTrue是设置b属性的Docx4j对象, 严格
     *  来说我们不需要将值设置为true, 因为这是它的默认值.
     */
    private static void addBoldStyle(RPr runProperties) {
        BooleanDefaultTrue b = new BooleanDefaultTrue();
        b.setVal(true);
        runProperties.setB(b);
    }
 
    /**
     *  本方法像前面例子中一样再一次创建了普通的单元格
     */
    private static void addRegularTableCell(Tr tableRow, String content) {
        Tc tableCell = factory.createTc();
        tableCell.getContent().add(
            wordMLPackage.getMainDocumentPart().createParagraphOfText(
                content));
        tableRow.getContent().add(tableCell);
    }
 
    /**
     *  本方法给表格添加边框
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