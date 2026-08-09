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
package io.github.easy4j.doc.io;

import java.math.BigInteger;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import io.github.easy4j.doc.utils.WmlElementUtils;
import io.github.easy4j.doc.wml.DocxElementWmlRender;
import io.github.easy4j.doc.wml.ParagraphWmlRender;
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
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.TcPrInner.VMerge;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;

/**
 * TODO
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WordprocessingMLPackageRender {

	protected WordprocessingMLPackage wmlPackage;
	protected ObjectFactory factory;  
	protected ParagraphWmlRender pRender = null;
	protected DocxElementWmlRender tbRender = null;
	
	public WordprocessingMLPackageRender() throws Docx4JException{
		this(WordprocessingMLPackage.createPackage());
	}
	
	public WordprocessingMLPackageRender(WordprocessingMLPackage wmlPackage){
		this.wmlPackage = wmlPackage;
		this.factory = Context.getWmlObjectFactory();  
		this.pRender = new ParagraphWmlRender(this.wmlPackage);
		this.tbRender = new DocxElementWmlRender(this.wmlPackage);  
	}
	
	public void addTitle(String title) {  
		wmlPackage.getMainDocumentPart().addStyledParagraphOfText("Title", title );   
    }
	
	public void addSubtitle(String subtitle) {  
		wmlPackage.getMainDocumentPart().addStyledParagraphOfText("Subtitle", subtitle );   
    }
	
	public void addTable(Tr table) {  
		wmlPackage.getMainDocumentPart().addObject(table);  
    }
	
	public void addTableRow(Tr table,Tr row) { 
		table.getContent().add(row);
    }
	
	public void addTableCell(Tr tableRow, String content) {  
		
		
		
        Tc tableCell = factory.createTc();  
        tableCell.getContent().add(wmlPackage.getMainDocumentPart().createParagraphOfText(content));  
        tableRow.getContent().add(tableCell);  
    }
    
	/*
     *  本方法创建单元格, 添加样式后添加到表格行中 
     */  
    public void addStyledTableCell(Tr tableRow, String content,boolean bold, String fontSize) {  
        Tc tableCell = factory.createTc();  
        addStyling(tableCell, content, bold, fontSize);  
        tableRow.getContent().add(tableCell);  
    }  
   
    /* 
     *  这里我们添加实际的样式信息, 首先创建一个段落, 然后创建以单元格内容作为值的文本对象;  
     *  第三步, 创建一个被称为运行块的对象, 它是一块或多块拥有共同属性的文本的容器, 并将文本对象添加 
     *  到其中. 随后我们将运行块R添加到段落内容中. 
     *  直到现在我们所做的还没有添加任何样式, 为了达到目标, 我们创建运行块属性对象并给它添加各种样式. 
     *  这些运行块的属性随后被添加到运行块. 最后段落被添加到表格的单元格中. 
     */  
    public void addStyling(Tc tableCell, String content, boolean bold, String fontSize) {  
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
   
    /*
     *  本方法为可运行块添加字体大小信息. 首先创建一个"半点"尺码对象, 然后设置fontSize 
     *  参数作为该对象的值, 最后我们分别设置sz和szCs的字体大小. 
     *  Finally we'll set the non-complex and complex script font sizes, sz and szCs respectively. 
     */  
    public void setFontSize(RPr runProperties, String fontSize) {  
        HpsMeasure size = new HpsMeasure();  
        size.setVal(new BigInteger(fontSize));  
        runProperties.setSz(size);  
        runProperties.setSzCs(size);  
    }  
   
    /* 
     *  本方法给可运行块属性添加粗体属性. BooleanDefaultTrue是设置b属性的Docx4j对象, 严格 
     *  来说我们不需要将值设置为true, 因为这是它的默认值. 
     */  
    public void addBoldStyle(RPr runProperties) {  
        BooleanDefaultTrue b = new BooleanDefaultTrue();  
        b.setVal(true);  
        runProperties.setB(b);  
    }  
   
    /* 
     *  本方法给表格添加边框 
     */  
    public void addBorders(Tbl table) {  
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
	
    
    /* 
     *  本方法创建一行, 并向其中添加合并列, 然后添加再两个普通的单元格. 随后将该行添加到表格 
     */  
    public void addTableRowWithMergedCells(String mergedContent,  String field1Content, String field2Content, Tbl table) {  
        Tr tableRow1 = factory.createTr();  
   
        addMergedColumn(tableRow1, mergedContent);  
   
        addTableCell(tableRow1, field1Content);  
        addTableCell(tableRow1, field2Content);  
   
        table.getContent().add(tableRow1);  
    }  
   
    /* 
     *  本方法添加一个合并了其它行单元格的列单元格. 如果传进来的内容是null, 传空字符串和一个为null的合并值. 
     */  
    public void addMergedColumn(Tr row, String content) {  
        if (content == null) {  
            addMergedCell(row, "", null);  
        } else {  
            addMergedCell(row, content, "restart");  
        }  
    }  
   
    /* 
     *  我们创建一个单元格和单元格属性对象. 
     *  也创建了一个纵向合并对象. 如果合并值不为null, 将它设置到合并对象中. 然后将该对象添加到 
     *  单元格属性并将属性添加到单元格中. 最后设置单元格内容并将单元格添加到行中. 
     *  如果合并值为'restart', 表明要开始一个新行. 如果为null, 继续按前面的行处理, 也就是合并单元格. 
     */  
    public void addMergedCell(Tr row, String content, String vMergeVal) {  
        Tc tableCell = factory.createTc();  
        TcPr tableCellProperties = new TcPr();  
   
        VMerge merge = new VMerge();  
        if(vMergeVal != null){  
            merge.setVal(vMergeVal);  
        }  
        tableCellProperties.setVMerge(merge);  
   
        tableCell.setTcPr(tableCellProperties);  
        if(content != null) {  
        	tableCell.getContent().add(wmlPackage.getMainDocumentPart(). createParagraphOfText(content));  
        }  
   
        row.getContent().add(tableCell);  
    }  
   
    /* 
     *  本方法创建一个单元格并将给定的内容添加进去. 
     *  如果给定的宽度大于0, 将这个宽度设置到单元格. 
     *  最后, 将单元格添加到行中. 
     */  
    public void addTableCellWithWidth(Tr row, String content, int width){  
        Tc tableCell = factory.createTc();  
        tableCell.getContent().add(wmlPackage.getMainDocumentPart().createParagraphOfText(content));  
        if (width > 0) {  
            setCellWidth(tableCell, width);  
        }  
        row.getContent().add(tableCell);  
    }
    
    /*
     *  本方法创建一个单元格属性集对象和一个表格宽度对象. 将给定的宽度设置到宽度对象然后将其添加到 属性集对象. 最后将属性集对象设置到单元格中. 
     */  
    public void setCellWidth(Tc tableCell, int width) {  
        TcPr tableCellProperties = new TcPr();  
        TblWidth tableWidth = new TblWidth();  
        tableWidth.setW(BigInteger.valueOf(width));  
        tableCellProperties.setTcW(tableWidth);  
        tableCell.setTcPr(tableCellProperties);  
    }
    
    
    /*
     *  Docx4j拥有一个由字节数组创建图片部件的工具方法, 随后将其添加到给定的包中. 为了能将图片添加 
     *  到一个段落中, 我们需要将图片转换成内联对象. 这也有一个方法, 方法需要文件名提示, 替换文本,  
     *  两个id标识符和一个是嵌入还是链接到的指示作为参数. 
     *  一个id用于文档中绘图对象不可见的属性, 另一个id用于图片本身不可见的绘制属性. 最后我们将内联 
     *  对象添加到段落中并将段落添加到包的主文档部件. 
     * 
     *  @param wordMLPackage 要添加图片的包 
     *  @param bytes         图片对应的字节数组 
     *  @throws Exception    不幸的createImageInline方法抛出一个异常(没有更多具体的异常类型) 
     */  
    public void addImageToPackage(byte[] bytes) throws Exception {  
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wmlPackage, bytes);  
   
        int docPrId = 1;  
        int cNvPrId = 2;  
        Inline inline = imagePart.createImageInline("Filename hint", "Alternative text", docPrId, cNvPrId, false);  
   
        P paragraph = WmlElementUtils.addInlineImageToParagraph(inline);  
   
        wmlPackage.getMainDocumentPart().addObject(paragraph);  
    }
     
	
}
