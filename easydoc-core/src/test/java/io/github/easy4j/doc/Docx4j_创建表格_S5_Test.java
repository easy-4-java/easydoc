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

import java.awt.GraphicsEnvironment;  
import java.awt.Toolkit;  
import java.io.File;  
import java.io.FileInputStream;  
import java.math.BigInteger;  
  
import org.apache.commons.io.IOUtils;  
import org.docx4j.dml.wordprocessingDrawing.Inline;  
import org.docx4j.jaxb.Context;  
import org.docx4j.model.structure.PageDimensions;  
import org.docx4j.model.structure.PageSizePaper;  
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;  
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;  
import org.docx4j.wml.Body;  
import org.docx4j.wml.BooleanDefaultTrue;  
import org.docx4j.wml.CTBorder;  
import org.docx4j.wml.CTShd;  
import org.docx4j.wml.CTTblPrBase.TblStyle;  
import org.docx4j.wml.CTVerticalJc;  
import org.docx4j.wml.Color;  
import org.docx4j.wml.Drawing;  
import org.docx4j.wml.HpsMeasure;  
import org.docx4j.wml.Jc;  
import org.docx4j.wml.JcEnumeration;  
import org.docx4j.wml.ObjectFactory;  
import org.docx4j.wml.P;  
import org.docx4j.wml.PPr;  
import org.docx4j.wml.R;  
import org.docx4j.wml.RFonts;  
import org.docx4j.wml.RPr;  
import org.docx4j.wml.STBorder;  
import org.docx4j.wml.STVerticalJc;  
import org.docx4j.wml.SectPr;  
import org.docx4j.wml.SectPr.PgMar;  
import org.docx4j.wml.Tbl;  
import org.docx4j.wml.TblPr;  
import org.docx4j.wml.TblWidth;  
import org.docx4j.wml.Tc;  
import org.docx4j.wml.TcMar;  
import org.docx4j.wml.TcPr;  
import org.docx4j.wml.TcPrInner.GridSpan;  
import org.docx4j.wml.TcPrInner.TcBorders;  
import org.docx4j.wml.TcPrInner.VMerge;  
import org.docx4j.wml.Text;  
import org.docx4j.wml.Tr;  
import org.docx4j.wml.U;  
import org.docx4j.wml.UnderlineEnumeration;  
  
//原文见:http://programmingbb.blogspot.com/2014/08/using-docx4j-to-generate-docx-files.html  
public class Docx4j_创建表格_S5_Test {  
    public static void main(String[] args) throws Exception {  
        Docx4j_创建表格_S5_Test t = new Docx4j_创建表格_S5_Test();  
        t.testDocx4jCreateTable();  
    }  
  
    public void testDocx4jCreateTable() throws Exception {  
        boolean landscape = false;  
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage  
                .createPackage(PageSizePaper.A4, landscape);  
        ObjectFactory factory = Context.getWmlObjectFactory();  
        setPageMargins(wordMLPackage, factory);  
        String imgFilePath = "f:/saveFile/tmp/2sql日志.jpg";  
        Tbl table = createTableWithContent(wordMLPackage, factory, imgFilePath);  
        wordMLPackage.getMainDocumentPart().addObject(table);  
        wordMLPackage.save(new File("f:/saveFile/temp/sys_"  
                + System.currentTimeMillis() + ".docx"));  
    }  
  
    public Tbl createTableWithContent(WordprocessingMLPackage wordMLPackage,  
            ObjectFactory factory, String imgFilePath) throws Exception {  
        Tbl table = factory.createTbl();  
        // for TEST: this adds borders to all cells  
        TblPr tblPr = new TblPr();  
        TblStyle tblStyle = new TblStyle();  
        tblStyle.setVal("TableGrid");  
        tblPr.setTblStyle(tblStyle);  
        table.setTblPr(tblPr);  
        Tr tableRow = factory.createTr();  
        // a default table cell style  
        Docx4jStyle_S3 defStyle = new Docx4jStyle_S3();  
        defStyle.setBold(false);  
        defStyle.setItalic(false);  
        defStyle.setUnderline(false);  
        defStyle.setHorizAlignment(JcEnumeration.CENTER);  
        // a specific table cell style  
        Docx4jStyle_S3 style = new Docx4jStyle_S3();  
        style.setBold(true);  
        style.setItalic(true);  
        style.setUnderline(true);  
        style.setFontSize("40");  
        style.setFontColor("FF0000");  
        style.setCnFontFamily("微软雅黑");  
        style.setEnFontFamily("Times New Roman");  
        style.setTop(300);  
        style.setBackground("CCFFCC");  
        style.setVerticalAlignment(STVerticalJc.CENTER);  
        style.setHorizAlignment(JcEnumeration.CENTER);  
        style.setBorderTop(true);  
        style.setBorderBottom(true);  
        style.setNoWrap(true);  
        addTableCell(factory, tableRow, "测试Field 1", 3500, style, 1, null);  
        // start vertical merge for Filed 2 and Field 3 on 3 rows  
        addTableCell(factory, tableRow, "测试Field 2", 3500, defStyle, 1,  
                "restart");  
        addTableCell(factory, tableRow, "测试Field 3", 1500, defStyle, 1,  
                "restart");  
        table.getContent().add(tableRow);  
        tableRow = factory.createTr();  
        addTableCell(factory, tableRow, "Text", 3500, defStyle, 1, null);  
        addTableCell(factory, tableRow, "", 3500, defStyle, 1, "");  
        addTableCell(factory, tableRow, "", 1500, defStyle, 1, "");  
        table.getContent().add(tableRow);  
        tableRow = factory.createTr();  
        addTableCell(factory, tableRow, "Interval", 3500, defStyle, 1, null);  
        addTableCell(factory, tableRow, "", 3500, defStyle, 1, "close");  
        addTableCell(factory, tableRow, "", 1500, defStyle, 1, "close");  
        table.getContent().add(tableRow);  
        // add an image horizontally merged on 3 cells  
        String filenameHint = null;  
        String altText = null;  
        int id1 = 0;  
        int id2 = 1;  
        byte[] bytes = getImageBytes(imgFilePath);  
        P pImage;  
        try {  
            pImage = newImage(wordMLPackage, factory, bytes, filenameHint,  
                    altText, id1, id2, 8500);  
            tableRow = factory.createTr();  
            addTableCell(factory, tableRow, pImage, 8500, defStyle, 3, null);  
            table.getContent().add(tableRow);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return table;  
    }  
  
    public byte[] getImageBytes(String imgFilePath) throws Exception {  
        return IOUtils.toByteArray(new FileInputStream(imgFilePath));  
    }  
  
    public void addTableCell(ObjectFactory factory, Tr tableRow, P image,  
            int width, Docx4jStyle_S3 style, int horizontalMergedCells,  
            String verticalMergedVal) {  
        Tc tableCell = factory.createTc();  
        addImageCellStyle(tableCell, image, style);  
        setCellWidth(tableCell, width);  
        setCellVMerge(tableCell, verticalMergedVal);  
        setCellHMerge(tableCell, horizontalMergedCells);  
        tableRow.getContent().add(tableCell);  
    }  
  
    public void addTableCell(ObjectFactory factory, Tr tableRow,  
            String content, int width, Docx4jStyle_S3 style,  
            int horizontalMergedCells, String verticalMergedVal) {  
        Tc tableCell = factory.createTc();  
        addCellStyle(factory, tableCell, content, style);  
        setCellWidth(tableCell, width);  
        setCellVMerge(tableCell, verticalMergedVal);  
        setCellHMerge(tableCell, horizontalMergedCells);  
        if (style.isNoWrap()) {  
            setCellNoWrap(tableCell);  
        }  
        tableRow.getContent().add(tableCell);  
    }  
  
    public void addCellStyle(ObjectFactory factory, Tc tableCell,  
            String content, Docx4jStyle_S3 style) {  
        if (style != null) {  
            P paragraph = factory.createP();  
            Text text = factory.createText();  
            text.setValue(content);  
            R run = factory.createR();  
            run.getContent().add(text);  
            paragraph.getContent().add(run);  
            setHorizontalAlignment(paragraph, style.getHorizAlignment());  
            RPr runProperties = factory.createRPr();  
            if (style.isBold()) {  
                addBoldStyle(runProperties);  
            }  
            if (style.isItalic()) {  
                addItalicStyle(runProperties);  
            }  
            if (style.isUnderline()) {  
                addUnderlineStyle(runProperties);  
            }  
            setFontSize(runProperties, style.getFontSize());  
            setFontColor(runProperties, style.getFontColor());  
            setFontFamily(runProperties, style.getCnFontFamily(),style.getEnFontFamily());  
            setCellMargins(tableCell, style.getTop(), style.getRight(),  
                    style.getBottom(), style.getLeft());  
            setCellColor(tableCell, style.getBackground());  
            setVerticalAlignment(tableCell, style.getVerticalAlignment());  
            setCellBorders(tableCell, style.isBorderTop(),  
                    style.isBorderRight(), style.isBorderBottom(),  
                    style.isBorderLeft());  
            run.setRPr(runProperties);  
            tableCell.getContent().add(paragraph);  
        }  
    }  
  
    public void addImageCellStyle(Tc tableCell, P image, Docx4jStyle_S3 style) {  
        setCellMargins(tableCell, style.getTop(), style.getRight(),  
                style.getBottom(), style.getLeft());  
        setCellColor(tableCell, style.getBackground());  
        setVerticalAlignment(tableCell, style.getVerticalAlignment());  
        setHorizontalAlignment(image, style.getHorizAlignment());  
        setCellBorders(tableCell, style.isBorderTop(), style.isBorderRight(),  
                style.isBorderBottom(), style.isBorderLeft());  
        tableCell.getContent().add(image);  
    }  
  
    public P newImage(WordprocessingMLPackage wordMLPackage,  
            ObjectFactory factory, byte[] bytes, String filenameHint,  
            String altText, int id1, int id2, long cx) throws Exception {  
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage  
                .createImagePart(wordMLPackage, bytes);  
        Inline inline = imagePart.createImageInline(filenameHint, altText, id1,  
                id2, cx, false);  
        // Now add the inline in w:p/w:r/w:drawing  
        P p = factory.createP();  
        R run = factory.createR();  
        p.getContent().add(run);  
        Drawing drawing = factory.createDrawing();  
        run.getContent().add(drawing);  
        drawing.getAnchorOrInline().add(inline);  
        return p;  
    }  
  
    public void setCellBorders(Tc tableCell, boolean borderTop,  
            boolean borderRight, boolean borderBottom, boolean borderLeft) {  
        TcPr tableCellProperties = tableCell.getTcPr();  
        if (tableCellProperties == null) {  
            tableCellProperties = new TcPr();  
            tableCell.setTcPr(tableCellProperties);  
        }  
        CTBorder border = new CTBorder();  
        // border.setColor("auto");  
        border.setColor("0000FF");  
        border.setSz(new BigInteger("20"));  
        border.setSpace(new BigInteger("0"));  
        border.setVal(STBorder.SINGLE);  
        TcBorders borders = new TcBorders();  
        if (borderBottom) {  
            borders.setBottom(border);  
        }  
        if (borderTop) {  
            borders.setTop(border);  
        }  
        if (borderLeft) {  
            borders.setLeft(border);  
        }  
        if (borderRight) {  
            borders.setRight(border);  
        }  
        tableCellProperties.setTcBorders(borders);  
    }  
  
    public void setCellWidth(Tc tableCell, int width) {  
        if (width > 0) {  
            TcPr tableCellProperties = tableCell.getTcPr();  
            if (tableCellProperties == null) {  
                tableCellProperties = new TcPr();  
                tableCell.setTcPr(tableCellProperties);  
            }  
            TblWidth tableWidth = new TblWidth();  
            tableWidth.setType("dxa");  
            tableWidth.setW(BigInteger.valueOf(width));  
            tableCellProperties.setTcW(tableWidth);  
        }  
    }  
  
    public void setCellNoWrap(Tc tableCell) {  
        TcPr tableCellProperties = tableCell.getTcPr();  
        if (tableCellProperties == null) {  
            tableCellProperties = new TcPr();  
            tableCell.setTcPr(tableCellProperties);  
        }  
        BooleanDefaultTrue b = new BooleanDefaultTrue();  
        b.setVal(true);  
        tableCellProperties.setNoWrap(b);  
    }  
  
    public void setCellVMerge(Tc tableCell, String mergeVal) {  
        if (mergeVal != null) {  
            TcPr tableCellProperties = tableCell.getTcPr();  
            if (tableCellProperties == null) {  
                tableCellProperties = new TcPr();  
                tableCell.setTcPr(tableCellProperties);  
            }  
            VMerge merge = new VMerge();  
            if (!"close".equals(mergeVal)) {  
                merge.setVal(mergeVal);  
            }  
            tableCellProperties.setVMerge(merge);  
        }  
    }  
  
    public void setCellHMerge(Tc tableCell, int horizontalMergedCells) {  
        if (horizontalMergedCells > 1) {  
            TcPr tableCellProperties = tableCell.getTcPr();  
            if (tableCellProperties == null) {  
                tableCellProperties = new TcPr();  
                tableCell.setTcPr(tableCellProperties);  
            }  
            GridSpan gridSpan = new GridSpan();  
            gridSpan.setVal(new BigInteger(String  
                    .valueOf(horizontalMergedCells)));  
            tableCellProperties.setGridSpan(gridSpan);  
            tableCell.setTcPr(tableCellProperties);  
        }  
    }  
  
    public void setCellColor(Tc tableCell, String color) {  
        if (color != null) {  
            TcPr tableCellProperties = tableCell.getTcPr();  
            if (tableCellProperties == null) {  
                tableCellProperties = new TcPr();  
                tableCell.setTcPr(tableCellProperties);  
            }  
            CTShd shd = new CTShd();  
            shd.setFill(color);  
            tableCellProperties.setShd(shd);  
        }  
    }  
  
    public void setCellMargins(Tc tableCell, int top, int right, int bottom,  
            int left) {  
        TcPr tableCellProperties = tableCell.getTcPr();  
        if (tableCellProperties == null) {  
            tableCellProperties = new TcPr();  
            tableCell.setTcPr(tableCellProperties);  
        }  
        TcMar margins = new TcMar();  
        if (bottom > 0) {  
            TblWidth bW = new TblWidth();  
            bW.setType("dxa");  
            bW.setW(BigInteger.valueOf(bottom));  
            margins.setBottom(bW);  
        }  
        if (top > 0) {  
            TblWidth tW = new TblWidth();  
            tW.setType("dxa");  
            tW.setW(BigInteger.valueOf(top));  
            margins.setTop(tW);  
        }  
        if (left > 0) {  
            TblWidth lW = new TblWidth();  
            lW.setType("dxa");  
            lW.setW(BigInteger.valueOf(left));  
            margins.setLeft(lW);  
        }  
        if (right > 0) {  
            TblWidth rW = new TblWidth();  
            rW.setType("dxa");  
            rW.setW(BigInteger.valueOf(right));  
            margins.setRight(rW);  
        }  
        tableCellProperties.setTcMar(margins);  
    }  
  
    public void setVerticalAlignment(Tc tableCell, STVerticalJc align) {  
        if (align != null) {  
            TcPr tableCellProperties = tableCell.getTcPr();  
            if (tableCellProperties == null) {  
                tableCellProperties = new TcPr();  
                tableCell.setTcPr(tableCellProperties);  
            }  
            CTVerticalJc valign = new CTVerticalJc();  
            valign.setVal(align);  
            tableCellProperties.setVAlign(valign);  
        }  
    }  
  
    public void setFontSize(RPr runProperties, String fontSize) {  
        if (fontSize != null && !fontSize.isEmpty()) {  
            HpsMeasure size = new HpsMeasure();  
            size.setVal(new BigInteger(fontSize));  
            runProperties.setSz(size);  
            runProperties.setSzCs(size);  
        }  
    }  
  
    public void setFontFamily(RPr runProperties, String cnFontFamily,String enFontFamily) {  
        if (cnFontFamily != null||enFontFamily!=null) {  
            RFonts rf = runProperties.getRFonts();  
            if (rf == null) {  
                rf = new RFonts();  
                runProperties.setRFonts(rf);  
            }  
            if(cnFontFamily!=null){  
                rf.setEastAsia(cnFontFamily);  
            }  
            if(enFontFamily!=null){  
                rf.setAscii(enFontFamily);  
            }  
        }  
    }  
  
    public void setFontColor(RPr runProperties, String color) {  
        if (color != null) {  
            Color c = new Color();  
            c.setVal(color);  
            runProperties.setColor(c);  
        }  
    }  
  
    public void setHorizontalAlignment(P paragraph, JcEnumeration hAlign) {  
        if (hAlign != null) {  
            PPr pprop = new PPr();  
            Jc align = new Jc();  
            align.setVal(hAlign);  
            pprop.setJc(align);  
            paragraph.setPPr(pprop);  
        }  
    }  
  
    public void addBoldStyle(RPr runProperties) {  
        BooleanDefaultTrue b = new BooleanDefaultTrue();  
        b.setVal(true);  
        runProperties.setB(b);  
    }  
  
    public void addItalicStyle(RPr runProperties) {  
        BooleanDefaultTrue b = new BooleanDefaultTrue();  
        b.setVal(true);  
        runProperties.setI(b);  
    }  
  
    public void addUnderlineStyle(RPr runProperties) {  
        U val = new U();  
        val.setVal(UnderlineEnumeration.SINGLE);  
        runProperties.setU(val);  
    }  
  
    public void setPageMargins(WordprocessingMLPackage wordMLPackage,  
            ObjectFactory factory) {  
        try {  
            Body body = wordMLPackage.getMainDocumentPart().getContents()  
                    .getBody();  
            PageDimensions page = new PageDimensions();  
            PgMar pgMar = page.getPgMar();  
            pgMar.setBottom(BigInteger.valueOf(pixelsToDxa(50)));  
            pgMar.setTop(BigInteger.valueOf(pixelsToDxa(50)));  
            pgMar.setLeft(BigInteger.valueOf(pixelsToDxa(50)));  
            pgMar.setRight(BigInteger.valueOf(pixelsToDxa(50)));  
            SectPr sectPr = factory.createSectPr();  
            body.setSectPr(sectPr);  
            sectPr.setPgMar(pgMar);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    // get dots per inch  
    public static int getDPI() {  
        return GraphicsEnvironment.isHeadless() ? 96 : Toolkit  
                .getDefaultToolkit().getScreenResolution();  
    }  
  
    public int pixelsToDxa(int pixels) {  
        return (1440 * pixels / getDPI());  
    }  
}  
  
class Docx4jStyle_S3 {  
    private boolean bold;  
    private boolean italic;  
    private boolean underline;  
    private String fontSize;  
    private String fontColor;  
    private String cnFontFamily;  
    private String enFontFamily;  
    // cell margins  
    private int left;  
    private int bottom;  
    private int top;  
    private int right;  
    private String background;  
    private STVerticalJc verticalAlignment;  
    private JcEnumeration horizAlignment;  
    private boolean borderLeft;  
    private boolean borderRight;  
    private boolean borderTop;  
    private boolean borderBottom;  
    private boolean noWrap;  
  
    public boolean isBold() {  
        return bold;  
    }  
  
    public void setBold(boolean bold) {  
        this.bold = bold;  
    }  
  
    public boolean isItalic() {  
        return italic;  
    }  
  
    public void setItalic(boolean italic) {  
        this.italic = italic;  
    }  
  
    public boolean isUnderline() {  
        return underline;  
    }  
  
    public void setUnderline(boolean underline) {  
        this.underline = underline;  
    }  
  
    public String getFontSize() {  
        return fontSize;  
    }  
  
    public void setFontSize(String fontSize) {  
        this.fontSize = fontSize;  
    }  
  
    public String getFontColor() {  
        return fontColor;  
    }  
  
    public void setFontColor(String fontColor) {  
        this.fontColor = fontColor;  
    }  
  
    public String getCnFontFamily() {  
        return cnFontFamily;  
    }  
  
    public void setCnFontFamily(String cnFontFamily) {  
        this.cnFontFamily = cnFontFamily;  
    }  
  
    public String getEnFontFamily() {  
        return enFontFamily;  
    }  
  
    public void setEnFontFamily(String enFontFamily) {  
        this.enFontFamily = enFontFamily;  
    }  
  
    public int getLeft() {  
        return left;  
    }  
  
    public void setLeft(int left) {  
        this.left = left;  
    }  
  
    public int getBottom() {  
        return bottom;  
    }  
  
    public void setBottom(int bottom) {  
        this.bottom = bottom;  
    }  
  
    public int getTop() {  
        return top;  
    }  
  
    public void setTop(int top) {  
        this.top = top;  
    }  
  
    public int getRight() {  
        return right;  
    }  
  
    public void setRight(int right) {  
        this.right = right;  
    }  
  
    public String getBackground() {  
        return background;  
    }  
  
    public void setBackground(String background) {  
        this.background = background;  
    }  
  
    public STVerticalJc getVerticalAlignment() {  
        return verticalAlignment;  
    }  
  
    public void setVerticalAlignment(STVerticalJc verticalAlignment) {  
        this.verticalAlignment = verticalAlignment;  
    }  
  
    public JcEnumeration getHorizAlignment() {  
        return horizAlignment;  
    }  
  
    public void setHorizAlignment(JcEnumeration horizAlignment) {  
        this.horizAlignment = horizAlignment;  
    }  
  
    public boolean isBorderLeft() {  
        return borderLeft;  
    }  
  
    public void setBorderLeft(boolean borderLeft) {  
        this.borderLeft = borderLeft;  
    }  
  
    public boolean isBorderRight() {  
        return borderRight;  
    }  
  
    public void setBorderRight(boolean borderRight) {  
        this.borderRight = borderRight;  
    }  
  
    public boolean isBorderTop() {  
        return borderTop;  
    }  
  
    public void setBorderTop(boolean borderTop) {  
        this.borderTop = borderTop;  
    }  
  
    public boolean isBorderBottom() {  
        return borderBottom;  
    }  
  
    public void setBorderBottom(boolean borderBottom) {  
        this.borderBottom = borderBottom;  
    }  
  
    public boolean isNoWrap() {  
        return noWrap;  
    }  
  
    public void setNoWrap(boolean noWrap) {  
        this.noWrap = noWrap;  
    }  
}  
