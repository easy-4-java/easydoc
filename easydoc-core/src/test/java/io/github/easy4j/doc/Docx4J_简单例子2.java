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

import java.io.File;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.model.properties.table.tr.TrHeight;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.utils.BufferUtil;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTHeight;
import org.docx4j.wml.CTShd;
import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.Color;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HeaderReference;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.PBdr;
import org.docx4j.wml.PPrBase.Spacing;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.STHint;
import org.docx4j.wml.STLineSpacingRule;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblGrid;
import org.docx4j.wml.TblGridCol;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.docx4j.wml.TrPr;
import org.docx4j.wml.U;
import org.docx4j.wml.UnderlineEnumeration;  
  
  
public class Docx4J_简单例子2 {  
    public static void main(String[] args) throws Exception {  
        Docx4J_简单例子 t = new Docx4J_简单例子();  
        WordprocessingMLPackage wordMLPackage = t  
                .createWordprocessingMLPackage();  
        MainDocumentPart mp = wordMLPackage.getMainDocumentPart();  
        ObjectFactory factory = Context.getWmlObjectFactory();  
        //图片页眉  
        //Relationship relationship =t.createHeaderPart(wordMLPackage, mp, factory);  
        //文字页眉  
        Relationship relationship =t.createTextHeaderPart(wordMLPackage, mp, factory, "我是页眉,多创造,少抄袭", JcEnumeration.CENTER);  
        t.createHeaderReference(wordMLPackage, mp, factory, relationship);  
          
        t.addParagraphTest(wordMLPackage, mp, factory);  
        t.addPageBreak(wordMLPackage, factory);  
          
        t.createNormalTableTest(wordMLPackage, mp, factory);  
        //页脚  
        relationship =t.createFooterPageNumPart(wordMLPackage, mp, factory);  
        t.createFooterReference(wordMLPackage, mp, factory, relationship);  
          
        t.saveWordPackage(wordMLPackage, new File(  
                "f:/saveFile/temp/s5_simple.docx"));  
    }  
  
    public void addParagraphTest(WordprocessingMLPackage wordMLPackage,  
            MainDocumentPart t, ObjectFactory factory) throws Exception {  
        RPr titleRPr = getRPr(factory, "黑体", "000000", "30", STHint.EAST_ASIA,  
                true, false, false, false);  
        RPr boldRPr = getRPr(factory, "宋体", "000000", "24", STHint.EAST_ASIA,  
                true, false, false, false);  
        RPr fontRPr = getRPr(factory, "宋体", "000000", "22", STHint.EAST_ASIA,  
                false, false, false, false);  
        P paragraph = factory.createP();  
        setParagraphAlign(factory, paragraph, JcEnumeration.CENTER);  
        Text txt = factory.createText();  
        txt.setValue("七年级上册Unit2 This is just a test. sectionA测试卷答题卡");  
        R run = factory.createR();  
        run.getContent().add(txt);  
        run.setRPr(titleRPr);  
        paragraph.getContent().add(run);  
        t.addObject(paragraph);  
  
        paragraph = factory.createP();  
        setParagraphAlign(factory, paragraph, JcEnumeration.CENTER);  
        txt = factory.createText();  
        txt.setValue("班级：________    姓名：________");  
        run = factory.createR();  
        run.getContent().add(txt);  
        run.setRPr(fontRPr);  
        paragraph.getContent().add(run);  
        t.addObject(paragraph);  
  
        paragraph = factory.createP();  
        txt = factory.createText();  
        txt.setValue("一、单选题");  
        run = factory.createR();  
        run.getContent().add(txt);  
        run.setRPr(boldRPr);  
        paragraph.getContent().add(run);  
        setParagraphSpacing(factory, paragraph,JcEnumeration.LEFT, "0",  "3");  
        t.addObject(paragraph);  
  
        paragraph = factory.createP();  
        txt = factory.createText();  
        txt.setValue("1.下列有关仪器用途的说法错误的是(    )");  
        run = factory.createR();  
        run.getContent().add(txt);  
        run.setRPr(fontRPr);  
        paragraph.getContent().add(run);  
        setParagraphSpacing(factory, paragraph,JcEnumeration.LEFT, "0",  "3");  
        t.addObject(paragraph);  
  
        paragraph = factory.createP();  
        txt = factory.createText();  
        txt.setValue("A.烧杯用于较多量试剂的反应容器");  
        run = factory.createR();  
        run.getContent().add(txt);  
        run.setRPr(fontRPr);  
        paragraph.getContent().add(run);  
        setParagraphSpacing(factory, paragraph,JcEnumeration.LEFT, "0",  "3");  
        t.addObject(paragraph);  
  
        paragraph = factory.createP();  
        txt = factory.createText();  
        txt.setValue("B.烧杯用于较多量试剂的反应容器");  
        run = factory.createR();  
        run.getContent().add(txt);  
        run.setRPr(fontRPr);  
        paragraph.getContent().add(run);  
        setParagraphSpacing(factory, paragraph,JcEnumeration.LEFT, "0",  "3");  
        t.addObject(paragraph);  
  
        paragraph = factory.createP();  
        txt = factory.createText();  
        txt.setValue("C.烧杯用于较多量试剂的反应容器");  
        run = factory.createR();  
        run.getContent().add(txt);  
        run.setRPr(fontRPr);  
        paragraph.getContent().add(run);  
        setParagraphSpacing(factory, paragraph,JcEnumeration.LEFT, "0",  "3");  
        t.addObject(paragraph);  
  
        paragraph = factory.createP();  
        txt = factory.createText();  
        txt.setValue("D.烧杯用于较多量试剂的反应容器");  
        run = factory.createR();  
        run.getContent().add(txt);  
        run.setRPr(fontRPr);  
        paragraph.getContent().add(run);  
        setParagraphSpacing(factory, paragraph,JcEnumeration.LEFT, "0",  "3");  
        t.addObject(paragraph);  
          
        paragraph = factory.createP();  
        txt = factory.createText();  
        txt.setValue("2.下列实验操作中，正确的是(    ) ");  
        run = factory.createR();  
        run.getContent().add(txt);  
        run.setRPr(fontRPr);  
        paragraph.getContent().add(run);  
        //段前8磅 段后0.5磅  
        setParagraphSpacing(factory, paragraph,JcEnumeration.LEFT, "160",  "10");  
        t.addObject(paragraph);  
          
        paragraph = factory.createP();  
        File file = new File("f:/saveFile/temp/image1.png");  
        java.io.InputStream is = new java.io.FileInputStream(file);  
        createImageParagraph(wordMLPackage, factory, paragraph, "img_1", "A.", BufferUtil.getBytesFromInputStream(is), JcEnumeration.LEFT);  
          
        file = new File("f:/saveFile/temp/image2.png");  
        is = new java.io.FileInputStream(file);  
        createImageParagraph(wordMLPackage, factory, paragraph, "img_2",StringUtils.leftPad("B.", 20) , BufferUtil.getBytesFromInputStream(is), JcEnumeration.LEFT);  
        setParagraphSpacing(factory, paragraph,JcEnumeration.LEFT, "1",  "3");  
        t.addObject(paragraph);  
          
        paragraph = factory.createP();  
        file = new File("f:/saveFile/temp/image3.png");  
        is = new java.io.FileInputStream(file);  
        createImageParagraph(wordMLPackage, factory, paragraph, "img_3", "C.", BufferUtil.getBytesFromInputStream(is), JcEnumeration.LEFT);  
          
        file = new File("f:/saveFile/temp/image4.png");  
        is = new java.io.FileInputStream(file);  
        createImageParagraph(wordMLPackage, factory, paragraph, "img_4", StringUtils.leftPad("D.", 20), BufferUtil.getBytesFromInputStream(is), JcEnumeration.LEFT);  
        setParagraphSpacing(factory, paragraph,JcEnumeration.LEFT, "1",  "3");  
        t.addObject(paragraph);  
          
    }  
      
    public void createNormalTableTest(WordprocessingMLPackage wordMLPackage,  
            MainDocumentPart t, ObjectFactory factory) throws Exception {  
        RPr titleRpr = getRPr(factory, "宋体", "000000", "22", STHint.EAST_ASIA,  
                true, false, false, false);  
        RPr contentRpr = getRPr(factory, "宋体", "000000", "22",  
                STHint.EAST_ASIA, false, false, false, false);  
        Tbl table = factory.createTbl();  
        addBorders(table, "2");  
          
        double[]widthPercent=new double[]{15,20,20,20,25};//百分比  
        setTableGridCol(wordMLPackage, factory, table, widthPercent);  
          
        Tr titleRow = factory.createTr();  
        setTableTrHeight(factory, titleRow, "500");  
        addTableCell(factory, wordMLPackage, titleRow, "序号", titleRpr,  
                JcEnumeration.CENTER, true, "C6D9F1");  
        addTableCell(factory, wordMLPackage, titleRow, "姓甚", titleRpr,  
                JcEnumeration.CENTER, true, "C6D9F1");  
        addTableCell(factory, wordMLPackage, titleRow, "名谁", titleRpr,  
                JcEnumeration.CENTER, true, "C6D9F1");  
        addTableCell(factory, wordMLPackage, titleRow, "籍贯", titleRpr,  
                JcEnumeration.CENTER, true, "C6D9F1");  
        addTableCell(factory, wordMLPackage, titleRow, "营生", titleRpr,  
                JcEnumeration.CENTER, true, "C6D9F1");  
        table.getContent().add(titleRow);  
          
        for (int i = 0; i < 10; i++) {  
            Tr contentRow = factory.createTr();  
            addTableCell(factory, wordMLPackage, contentRow, i + "",  
                    contentRpr, JcEnumeration.CENTER, false, null);  
            addTableCell(factory, wordMLPackage, contentRow, "无名氏", contentRpr,  
                    JcEnumeration.CENTER, false, null);  
            addTableCell(factory, wordMLPackage, contentRow, "佚名", contentRpr,  
                    JcEnumeration.CENTER, false, null);  
            addTableCell(factory, wordMLPackage, contentRow, "武林", contentRpr,  
                    JcEnumeration.CENTER, false, null);  
            addTableCell(factory, wordMLPackage, contentRow, "吟诗赋曲",  
                    contentRpr, JcEnumeration.CENTER, false, null);  
            table.getContent().add(contentRow);  
        }  
        setTableAlign(factory, table, JcEnumeration.CENTER);  
        t.addObject(table);  
    }  
  
    public WordprocessingMLPackage createWordprocessingMLPackage()  
            throws Exception {  
        return WordprocessingMLPackage.createPackage();  
    }  
  
    public void saveWordPackage(WordprocessingMLPackage wordPackage, File file)  
            throws Exception {  
        wordPackage.save(file);  
    }  
  
    // 分页  
    public void addPageBreak(WordprocessingMLPackage wordMLPackage,  
            ObjectFactory factory) {  
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();  
        Br breakObj = new Br();  
        breakObj.setType(STBrType.PAGE);  
        P paragraph = factory.createP();  
        paragraph.getContent().add(breakObj);  
        documentPart.addObject(paragraph);  
    }  
  
    /** 
     * 创建字体 
     *  
     * @param isBlod 
     *            粗体 
     * @param isUnderLine 
     *            下划线 
     * @param isItalic 
     *            斜体 
     * @param isStrike 
     *            删除线 
     */  
    public RPr getRPr(ObjectFactory factory, String fontFamily,  
            String colorVal, String fontSize, STHint sTHint, boolean isBlod,  
            boolean isUnderLine, boolean isItalic, boolean isStrike) {  
        RPr rPr = factory.createRPr();  
        RFonts rf = new RFonts();  
        rf.setHint(sTHint);  
        rf.setAscii(fontFamily);  
        rf.setHAnsi(fontFamily);  
        rPr.setRFonts(rf);  
  
        BooleanDefaultTrue bdt = factory.createBooleanDefaultTrue();  
        rPr.setBCs(bdt);  
        if (isBlod) {  
            rPr.setB(bdt);  
        }  
        if (isItalic) {  
            rPr.setI(bdt);  
        }  
        if (isStrike) {  
            rPr.setStrike(bdt);  
        }  
        if (isUnderLine) {  
            U underline = new U();  
            underline.setVal(UnderlineEnumeration.SINGLE);  
            rPr.setU(underline);  
        }  
  
        Color color = new Color();  
        color.setVal(colorVal);  
        rPr.setColor(color);  
  
        HpsMeasure sz = new HpsMeasure();  
        sz.setVal(new BigInteger(fontSize));  
        rPr.setSz(sz);  
        rPr.setSzCs(sz);  
        return rPr;  
    }  
  
    // 水平对齐方式  
    // TODO 垂直对齐没写  
    public void setParagraphAlign(ObjectFactory factory, P p,  
            JcEnumeration jcEnumeration) {  
        PPr pPr = p.getPPr();  
        if (pPr == null) {  
            pPr = factory.createPPr();  
        }  
        Jc jc = pPr.getJc();  
        if (jc == null) {  
            jc = new Jc();  
        }  
        jc.setVal(jcEnumeration);  
        pPr.setJc(jc);  
        p.setPPr(pPr);  
    }  
      
    //设置段落间距  
    public void setParagraphSpacing(ObjectFactory factory, P p,  
            JcEnumeration jcEnumeration,String before,String after) {  
        PPr pPr = p.getPPr();  
        if (pPr == null) {  
            pPr = factory.createPPr();  
        }  
        Jc jc = pPr.getJc();  
        if (jc == null) {  
            jc = new Jc();  
        }  
        jc.setVal(jcEnumeration);  
        pPr.setJc(jc);  
          
        Spacing spacing=new Spacing();  
        spacing.setBefore(new BigInteger(before));  
        spacing.setAfter(new BigInteger(after));  
        spacing.setLineRule(STLineSpacingRule.AUTO);  
        pPr.setSpacing(spacing);  
        p.setPPr(pPr);  
    }  
  
    // 表格水平对齐方式  
    public void setTableAlign(ObjectFactory factory, Tbl table,  
            JcEnumeration jcEnumeration) {  
        TblPr tablePr = table.getTblPr();  
        if (tablePr == null) {  
            tablePr = factory.createTblPr();  
        }  
        Jc jc = tablePr.getJc();  
        if (jc == null) {  
            jc = new Jc();  
        }  
        jc.setVal(jcEnumeration);  
        tablePr.setJc(jc);  
        table.setTblPr(tablePr);  
    }  
  
    //得到页面宽度  
    public  int getWritableWidth(WordprocessingMLPackage wordPackage)  
            throws Exception {  
        return wordPackage.getDocumentModel().getSections().get(0)  
                .getPageDimensions().getWritableWidthTwips();  
    }  
      
    //设置整列宽度  
    public void setTableGridCol(WordprocessingMLPackage wordPackage,ObjectFactory factory,Tbl table,double[] widthPercent) throws Exception{  
        int width=getWritableWidth(wordPackage);  
        TblGrid tblGrid = factory.createTblGrid();  
        for (int i = 0; i <widthPercent.length; i++) {  
            TblGridCol gridCol = factory.createTblGridCol();  
            gridCol.setW(BigInteger.valueOf((long) (width*widthPercent[i]/100)));  
            tblGrid.getGridCol().add(gridCol);  
        }  
        table.setTblGrid(tblGrid);  
          
        TblPr tblPr=table.getTblPr();  
        if(tblPr==null){  
            tblPr=factory.createTblPr();  
        }  
        TblWidth tblWidth=new TblWidth();  
        tblWidth.setType("dxa");//这一行是必须的,不自己设置宽度默认是auto  
        tblWidth.setW(new BigInteger(""+width));  
        tblPr.setTblW(tblWidth);  
        table.setTblPr(tblPr);  
    }  
      
    // 表格增加边框  
    public void addBorders(Tbl table, String borderSize) {  
        table.setTblPr(new TblPr());  
        CTBorder border = new CTBorder();  
        border.setColor("auto");  
        border.setSz(new BigInteger(borderSize));  
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
  
    //设置tr高度  
    public void setTableTrHeight(ObjectFactory factory,Tr tr,String heigth){  
        TrPr trPr=tr.getTrPr();  
        if(trPr==null){  
            trPr=factory.createTrPr();  
        }  
        CTHeight ctHeight=new CTHeight();  
        ctHeight.setVal(new BigInteger(heigth));  
        TrHeight trHeight=new TrHeight(ctHeight);  
        trHeight.set(trPr);  
        tr.setTrPr(trPr);  
    }  
      
    // 新增单元格  
    public void addTableCell(ObjectFactory factory,  
            WordprocessingMLPackage wordMLPackage, Tr tableRow, String content,  
            RPr rpr, JcEnumeration jcEnumeration, boolean hasBgColor,  
            String backgroudColor) {  
        Tc tableCell = factory.createTc();  
        P p = factory.createP();  
        setParagraphAlign(factory, p, jcEnumeration);  
        Text t = factory.createText();  
        t.setValue(content);  
        R run = factory.createR();  
        // 设置表格内容字体样式  
        run.setRPr(rpr);  
          
        TcPr tcPr = tableCell.getTcPr();  
        if (tcPr == null) {  
            tcPr = factory.createTcPr();  
        }  
          
        CTVerticalJc valign = factory.createCTVerticalJc();  
        valign.setVal(STVerticalJc.CENTER);  
        tcPr.setVAlign(valign);  
          
        run.getContent().add(t);  
        p.getContent().add(run);  
          
        PPr ppr=p.getPPr();  
        if(ppr==null){  
            ppr=factory.createPPr();  
        }  
        //设置段后距离  
        Spacing spacing=new Spacing();  
        spacing.setAfter(new BigInteger("0"));  
        spacing.setLineRule(STLineSpacingRule.AUTO);  
        ppr.setSpacing(spacing);  
        p.setPPr(ppr);  
          
        tableCell.getContent().add(p);  
        if (hasBgColor) {  
            CTShd shd = tcPr.getShd();  
            if (shd == null) {  
                shd = factory.createCTShd();  
            }  
            shd.setColor("auto");  
            shd.setFill(backgroudColor);  
            tcPr.setShd(shd);  
            tableCell.setTcPr(tcPr);  
        }  
        tableRow.getContent().add(tableCell);  
    }  
  
    // 文字页面  
    public Relationship createTextHeaderPart(  
            WordprocessingMLPackage wordprocessingMLPackage,  
            MainDocumentPart t, ObjectFactory factory, String content,  
            JcEnumeration jcEnumeration) throws Exception {  
        HeaderPart headerPart = new HeaderPart();  
        Relationship rel = t.addTargetPart(headerPart);  
        headerPart.setJaxbElement(getTextHdr(wordprocessingMLPackage, factory,  
                headerPart, content, jcEnumeration));  
        return rel;  
    }  
  
    // 文字页脚  
    public Relationship createTextFooterPart(  
            WordprocessingMLPackage wordprocessingMLPackage,  
            MainDocumentPart t, ObjectFactory factory, String content,  
            JcEnumeration jcEnumeration) throws Exception {  
        FooterPart footerPart = new FooterPart();  
        Relationship rel = t.addTargetPart(footerPart);  
        footerPart.setJaxbElement(getTextFtr(wordprocessingMLPackage, factory,  
                footerPart, content, jcEnumeration));  
        return rel;  
    }  
  
    // 图片页眉  
    public Relationship createHeaderPart(  
            WordprocessingMLPackage wordprocessingMLPackage,  
            MainDocumentPart t, ObjectFactory factory) throws Exception {  
        HeaderPart headerPart = new HeaderPart();  
        Relationship rel = t.addTargetPart(headerPart);  
        // After addTargetPart, so image can be added properly  
        headerPart.setJaxbElement(getHdr(wordprocessingMLPackage, factory,  
                headerPart));  
        return rel;  
    }  
  
    // 图片页脚  
    public Relationship createFooterPart(  
            WordprocessingMLPackage wordprocessingMLPackage,  
            MainDocumentPart t, ObjectFactory factory) throws Exception {  
        FooterPart footerPart = new FooterPart();  
        Relationship rel = t.addTargetPart(footerPart);  
        footerPart.setJaxbElement(getFtr(wordprocessingMLPackage, factory,  
                footerPart));  
        return rel;  
    }  
  
    public Relationship createFooterPageNumPart(  
            WordprocessingMLPackage wordprocessingMLPackage,  
            MainDocumentPart t, ObjectFactory factory) throws Exception {  
        FooterPart footerPart = new FooterPart();  
        footerPart.setPackage(wordprocessingMLPackage);  
        footerPart.setJaxbElement(createFooterWithPageNr(factory));  
        return t.addTargetPart(footerPart);  
    }  
  
    public Ftr createFooterWithPageNr(ObjectFactory factory) {  
        Ftr ftr = factory.createFtr();  
        P paragraph = factory.createP();  
        RPr fontRPr = getRPr(factory, "宋体", "000000", "20", STHint.EAST_ASIA,  
                false, false, false, false);  
        R run = factory.createR();  
        run.setRPr(fontRPr);  
        paragraph.getContent().add(run);  
  
        addPageTextField(factory, paragraph, "第");  
        addFieldBegin(factory, paragraph);  
        addPageNumberField(factory, paragraph);  
        addFieldEnd(factory, paragraph);  
        addPageTextField(factory, paragraph, "页");  
  
        addPageTextField(factory, paragraph, " 总共");  
        addFieldBegin(factory, paragraph);  
        addTotalPageNumberField(factory, paragraph);  
        addFieldEnd(factory, paragraph);  
        addPageTextField(factory, paragraph, "页");  
        setParagraphAlign(factory, paragraph, JcEnumeration.CENTER);  
        ftr.getContent().add(paragraph);  
        return ftr;  
    }  
  
    public void addFieldBegin(ObjectFactory factory, P paragraph) {  
        R run = factory.createR();  
        FldChar fldchar = factory.createFldChar();  
        fldchar.setFldCharType(STFldCharType.BEGIN);  
        run.getContent().add(fldchar);  
        paragraph.getContent().add(run);  
    }  
  
    public void addFieldEnd(ObjectFactory factory, P paragraph) {  
        FldChar fldcharend = factory.createFldChar();  
        fldcharend.setFldCharType(STFldCharType.END);  
        R run3 = factory.createR();  
        run3.getContent().add(fldcharend);  
        paragraph.getContent().add(run3);  
    }  
  
    public void addPageNumberField(ObjectFactory factory, P paragraph) {  
        R run = factory.createR();  
        Text txt = new Text();  
        txt.setSpace("preserve");  
        txt.setValue("PAGE  \\* MERGEFORMAT ");  
        run.getContent().add(factory.createRInstrText(txt));  
        paragraph.getContent().add(run);  
    }  
  
    public void addTotalPageNumberField(ObjectFactory factory, P paragraph) {  
        R run = factory.createR();  
        Text txt = new Text();  
        txt.setSpace("preserve");  
        txt.setValue("NUMPAGES  \\* MERGEFORMAT ");  
        run.getContent().add(factory.createRInstrText(txt));  
        paragraph.getContent().add(run);  
    }  
  
    private void addPageTextField(ObjectFactory factory, P paragraph,  
            String value) {  
        R run = factory.createR();  
        Text txt = new Text();  
        txt.setSpace("preserve");  
        txt.setValue(value);  
        run.getContent().add(txt);  
        paragraph.getContent().add(run);  
    }  
  
    public void createHeaderReference(  
            WordprocessingMLPackage wordprocessingMLPackage,  
            MainDocumentPart t, ObjectFactory factory, Relationship relationship)  
            throws InvalidFormatException {  
        List<SectionWrapper> sections = wordprocessingMLPackage  
                .getDocumentModel().getSections();  
        SectPr sectPr = sections.get(sections.size() - 1).getSectPr();  
        // There is always a section wrapper, but it might not contain a sectPr  
        if (sectPr == null) {  
            sectPr = factory.createSectPr();  
            t.addObject(sectPr);  
            sections.get(sections.size() - 1).setSectPr(sectPr);  
        }  
        HeaderReference headerReference = factory.createHeaderReference();  
        headerReference.setId(relationship.getId());  
        headerReference.setType(HdrFtrRef.DEFAULT);  
        sectPr.getEGHdrFtrReferences().add(headerReference);  
    }  
  
    public void createFooterReference(  
            WordprocessingMLPackage wordprocessingMLPackage,  
            MainDocumentPart t, ObjectFactory factory, Relationship relationship)  
            throws InvalidFormatException {  
        List<SectionWrapper> sections = wordprocessingMLPackage  
                .getDocumentModel().getSections();  
        SectPr sectPr = sections.get(sections.size() - 1).getSectPr();  
        // There is always a section wrapper, but it might not contain a sectPr  
        if (sectPr == null) {  
            sectPr = factory.createSectPr();  
            t.addObject(sectPr);  
            sections.get(sections.size() - 1).setSectPr(sectPr);  
        }  
        FooterReference footerReference = factory.createFooterReference();  
        footerReference.setId(relationship.getId());  
        footerReference.setType(HdrFtrRef.DEFAULT);  
        sectPr.getEGHdrFtrReferences().add(footerReference);  
    }  
  
    public Hdr getTextHdr(WordprocessingMLPackage wordprocessingMLPackage,  
            ObjectFactory factory, Part sourcePart, String content,  
            JcEnumeration jcEnumeration) throws Exception {  
        Hdr hdr = factory.createHdr();  
        P headP = factory.createP();  
        Text text = factory.createText();  
        text.setValue(content);  
        R run = factory.createR();  
        run.getContent().add(text);  
        headP.getContent().add(run);  
  
        PPr pPr = headP.getPPr();  
        if (pPr == null) {  
            pPr = factory.createPPr();  
        }  
        Jc jc = pPr.getJc();  
        if (jc == null) {  
            jc = new Jc();  
        }  
        jc.setVal(jcEnumeration);  
        pPr.setJc(jc);  
          
        PBdr pBdr=pPr.getPBdr();  
        if(pBdr==null){  
            pBdr=factory.createPPrBasePBdr();  
        }  
          
        CTBorder value=new CTBorder();  
        value.setVal(STBorder.SINGLE);  
        value.setColor("000000");  
        value.setSpace(new BigInteger("0"));  
        value.setSz(new BigInteger("3"));  
        pBdr.setBetween(value);  
        pPr.setPBdr(pBdr);  
        headP.setPPr(pPr);  
        setParagraphSpacing(factory, headP, jcEnumeration, "0", "0");  
        hdr.getContent().add(headP);  
        hdr.getContent().add(createHeaderBlankP(wordprocessingMLPackage, factory, jcEnumeration));  
        return hdr;  
    }  
  
    public Ftr getTextFtr(WordprocessingMLPackage wordprocessingMLPackage,  
            ObjectFactory factory, Part sourcePart, String content,  
            JcEnumeration jcEnumeration) throws Exception {  
        Ftr ftr = factory.createFtr();  
        P footerP = factory.createP();  
        Text text = factory.createText();  
        text.setValue(content);  
        R run = factory.createR();  
        run.getContent().add(text);  
        footerP.getContent().add(run);  
  
        PPr pPr = footerP.getPPr();  
        if (pPr == null) {  
            pPr = factory.createPPr();  
        }  
        Jc jc = pPr.getJc();  
        if (jc == null) {  
            jc = new Jc();  
        }  
        jc.setVal(jcEnumeration);  
        pPr.setJc(jc);  
        footerP.setPPr(pPr);  
        ftr.getContent().add(footerP);  
        return ftr;  
    }  
  
    public Hdr getHdr(WordprocessingMLPackage wordprocessingMLPackage,  
            ObjectFactory factory, Part sourcePart) throws Exception {  
        Hdr hdr = factory.createHdr();  
        File file = new File("f:/saveFile/tmp/xxt.jpg");  
        java.io.InputStream is = new java.io.FileInputStream(file);  
        hdr.getContent().add(  
                newImage(wordprocessingMLPackage, factory, sourcePart,  
                        BufferUtil.getBytesFromInputStream(is), "filename",  
                        "这是页眉部分", 1, 2, JcEnumeration.CENTER));  
        hdr.getContent().add(createHeaderBlankP(wordprocessingMLPackage, factory,JcEnumeration.CENTER));  
        return hdr;  
    }  
  
    public Ftr getFtr(WordprocessingMLPackage wordprocessingMLPackage,  
            ObjectFactory factory, Part sourcePart) throws Exception {  
        Ftr ftr = factory.createFtr();  
        File file = new File("f:/saveFile/tmp/xxt.jpg");  
        java.io.InputStream is = new java.io.FileInputStream(file);  
        ftr.getContent().add(  
                newImage(wordprocessingMLPackage, factory, sourcePart,  
                        BufferUtil.getBytesFromInputStream(is), "filename",  
                        "这是页脚", 1, 2, JcEnumeration.CENTER));  
        return ftr;  
    }  
  
    //段落中插入文字和图片  
    public P createImageParagraph(WordprocessingMLPackage wordMLPackage,  
            ObjectFactory factory,P p,String fileName, String content,byte[] bytes,  
            JcEnumeration jcEnumeration) throws Exception {  
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage  
                .createImagePart(wordMLPackage, bytes);  
        Inline inline = imagePart.createImageInline(fileName, "这是图片", 1,  
                2, false);  
        Text text = factory.createText();  
        text.setValue(content);  
        text.setSpace("preserve");  
        R run = factory.createR();  
        p.getContent().add(run);  
        run.getContent().add(text);  
        Drawing drawing = factory.createDrawing();  
        run.getContent().add(drawing);  
        drawing.getAnchorOrInline().add(inline);  
        PPr pPr = p.getPPr();  
        if (pPr == null) {  
            pPr = factory.createPPr();  
        }  
        Jc jc = pPr.getJc();  
        if (jc == null) {  
            jc = new Jc();  
        }  
        jc.setVal(jcEnumeration);  
        pPr.setJc(jc);  
        p.setPPr(pPr);  
        setParagraphSpacing(factory, p, jcEnumeration, "0", "0");  
        return p;  
    }  
      
    public P newImage(WordprocessingMLPackage wordMLPackage,  
            ObjectFactory factory, Part sourcePart, byte[] bytes,  
            String filenameHint, String altText, int id1, int id2,  
            JcEnumeration jcEnumeration) throws Exception {  
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage  
                .createImagePart(wordMLPackage, sourcePart, bytes);  
        Inline inline = imagePart.createImageInline(filenameHint, altText, id1,  
                id2, false);  
        P p = factory.createP();  
        R run = factory.createR();  
        p.getContent().add(run);  
        Drawing drawing = factory.createDrawing();  
        run.getContent().add(drawing);  
        drawing.getAnchorOrInline().add(inline);  
        PPr pPr = p.getPPr();  
        if (pPr == null) {  
            pPr = factory.createPPr();  
        }  
        Jc jc = pPr.getJc();  
        if (jc == null) {  
            jc = new Jc();  
        }  
        jc.setVal(jcEnumeration);  
        pPr.setJc(jc);  
        p.setPPr(pPr);  
          
        PBdr pBdr=pPr.getPBdr();  
        if(pBdr==null){  
            pBdr=factory.createPPrBasePBdr();  
        }  
        CTBorder value=new CTBorder();  
        value.setVal(STBorder.SINGLE);  
        value.setColor("000000");  
        value.setSpace(new BigInteger("0"));  
        value.setSz(new BigInteger("3"));  
        pBdr.setBetween(value);  
        pPr.setPBdr(pBdr);  
        setParagraphSpacing(factory, p, jcEnumeration, "0", "0");  
        return p;  
    }  
      
    public P createHeaderBlankP(WordprocessingMLPackage wordMLPackage,  
            ObjectFactory factory,  
            JcEnumeration jcEnumeration) throws Exception{  
        P p = factory.createP();  
        R run = factory.createR();  
        p.getContent().add(run);  
        PPr pPr = p.getPPr();  
        if (pPr == null) {  
            pPr = factory.createPPr();  
        }  
        Jc jc = pPr.getJc();  
        if (jc == null) {  
            jc = new Jc();  
        }  
        jc.setVal(jcEnumeration);  
        pPr.setJc(jc);  
          
        PBdr pBdr=pPr.getPBdr();  
        if(pBdr==null){  
            pBdr=factory.createPPrBasePBdr();  
        }  
        CTBorder value=new CTBorder();  
        value.setVal(STBorder.SINGLE);  
        value.setColor("000000");  
        value.setSpace(new BigInteger("0"));  
        value.setSz(new BigInteger("3"));  
        pBdr.setBetween(value);  
        pPr.setPBdr(pBdr);  
        p.setPPr(pPr);  
        setParagraphSpacing(factory, p, jcEnumeration, "0", "0");  
        return p;  
    }  
  
}  
