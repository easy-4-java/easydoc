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

import javax.xml.bind.JAXBException;

import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.SectPr.PgSz;
import org.docx4j.wml.SectPr.Type;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.TextDirection;
import org.docx4j.wml.Tr;

public class Docx4j_Helper {
	
	protected static ObjectFactory factory = Context.getWmlObjectFactory();  
	protected static String inputfilepath = "e:/test_tmp/0904/test_p.docx";  
	protected static String outputfilepath = "e:/test_tmp/0904/test_p2.docx";  
	  
	public static void name() throws Exception {
	  
	    WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File(inputfilepath));  
	    MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();  
	    String titleStr = "测试插入段落";  
	    P p = Docx4j_Helper.factory.createP();  
	    String rprStr = "<w:rPr xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"><w:rFonts w:hint=\"eastAsia\" w:ascii=\"Times New Roman\" w:hAnsi=\"Times New Roman\" w:eastAsia=\"宋体\"/><w:b/><w:color w:val=\"333333\"/><w:sz w:val=\"32\"/><w:szCs w:val=\"32\"/></w:rPr>";  
	    RPr rpr = (RPr) XmlUtils.unmarshalString(rprStr);  
	    setParagraphContent(p, rpr, titleStr);  
	    documentPart.getContent().add(5, p);  
	      
	    String tblPrStr = "<w:tblPr xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"><w:tblW w:w=\"8522\" w:type=\"dxa\"/><w:tblBorders><w:top w:val=\"single\"  w:sz=\"4\" w:space=\"0\"/><w:left w:val=\"single\"  w:sz=\"4\" w:space=\"0\"/><w:bottom w:val=\"single\"  w:sz=\"4\" w:space=\"0\"/><w:right w:val=\"single\"  w:sz=\"4\" w:space=\"0\"/><w:insideH w:val=\"single\"  w:sz=\"4\" w:space=\"0\"/></w:tblBorders></w:tblPr>";  
	    Tbl tbl = Docx4j_Helper.factory.createTbl();  
	    TblPr tblPr = (TblPr) XmlUtils.unmarshalString(tblPrStr);  
	    tbl.setTblPr(tblPr);  
	    Tr tr = Docx4j_Helper.factory.createTr();  
	    Tc tc = Docx4j_Helper.factory.createTc();  
	    tr.getContent().add(tc);  
	      
	    tc = Docx4j_Helper.factory.createTc();  
	    tr.getContent().add(tc);  
	      
	    tc = Docx4j_Helper.factory.createTc();  
	    tr.getContent().add(tc);  
	      
	    tc = Docx4j_Helper.factory.createTc();  
	    tr.getContent().add(tc);  
	      
	    tc = Docx4j_Helper.factory.createTc();  
	    tr.getContent().add(tc);  
	    tbl.getContent().add(tr);  
	      
	    tr = Docx4j_Helper.factory.createTr();  
	    tc = Docx4j_Helper.factory.createTc();  
	    tr.getContent().add(tc);  
	      
	    tc = Docx4j_Helper.factory.createTc();  
	    tr.getContent().add(tc);  
	      
	    tc = Docx4j_Helper.factory.createTc();  
	    tr.getContent().add(tc);  
	      
	    tc = Docx4j_Helper.factory.createTc();  
	    tr.getContent().add(tc);  
	      
	    tc = Docx4j_Helper.factory.createTc();  
	    tr.getContent().add(tc);  
	    tbl.getContent().add(tr);  
	    documentPart.getContent().add(9, tbl);  
	      
	    //Docx4j_Helper.saveWordPackage(wordMLPackage, outputfilepath);  
	}
	
    public void testDocx4jSetPageSize() throws Exception {  
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();  
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();  
  
        String titleStr="静夜思    李白";  
        String str="床前明月光，疑似地上霜。";  
        String str2="举头望明月，低头思故乡。";  
        P p = Docx4j_Helper.factory.createP();  
        String rprStr = "<w:rPr xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"><w:rFonts w:hint=\"eastAsia\" w:ascii=\"Times New Roman\" w:hAnsi=\"Times New Roman\" w:eastAsia=\"宋体\"/><w:b/><w:color w:val=\"333333\"/><w:sz w:val=\"32\"/><w:szCs w:val=\"32\"/></w:rPr>";  
        RPr rpr = (RPr) XmlUtils.unmarshalString(rprStr);  
        setParagraphContent(p, rpr,titleStr);  
        mdp.addObject(p);  
          
        p = Docx4j_Helper.factory.createP();  
        setParagraphContent(p, rpr,str);  
        mdp.addObject(p);  
          
        p = Docx4j_Helper.factory.createP();  
        PPr pPr=Docx4j_Helper.factory.createPPr();  
        //设置文字方向  
        SectPr sectPr = Docx4j_Helper.factory.createSectPr();  
        TextDirection textDirect = Docx4j_Helper.factory.createTextDirection();  
        //文字方向:垂直方向从右往左  
        textDirect.setVal("tbRl");  
        sectPr.setTextDirection(textDirect);  
        Type sectType = Docx4j_Helper.factory.createSectPrType();  
        //下一页  
        sectType.setVal("nextPage");  
        sectPr.setType(sectType);  
        //设置页面大小  
        PgSz pgSz =  Docx4j_Helper.factory.createSectPrPgSz();  
        pgSz.setW(new BigInteger("8335"));  
        pgSz.setH(new BigInteger("11850"));  
        sectPr.setPgSz(pgSz);  
        pPr.setSectPr(sectPr);  
        p.setPPr(pPr);  
        setParagraphContent(p, rpr,str2);  
        mdp.addObject(p);  
          
        p = createParagraphWithHAlign();  
        setParagraphContent(p, rpr,titleStr);  
        mdp.addObject(p);  
          
        p = createParagraphWithHAlign();  
        setParagraphContent(p, rpr,str);  
        mdp.addObject(p);  
          
        p = createParagraphWithHAlign();  
        setParagraphContent(p, rpr,str2);  
        mdp.addObject(p);  
       // Docx4j_Helper.saveWordPackage(wordMLPackage, outputfilepath);  
    }  
  
    /** 
     * 创建段落设置水平对齐方式 
     * @return 
     */  
    private P createParagraphWithHAlign() {  
        P p;  
        PPr pPr;  
        p = Docx4j_Helper.factory.createP();  
        pPr=Docx4j_Helper.factory.createPPr();  
        Jc jc =Docx4j_Helper.factory.createJc();  
        jc.setVal(JcEnumeration.CENTER);  
        pPr.setJc(jc);  
        p.setPPr(pPr);  
        return p;  
    }  
  
    /** 
     * 设置段落内容 
     */  
    private static void setParagraphContent(P p, RPr rpr,String content) {  
        Text t = Docx4j_Helper.factory.createText();  
        t.setSpace("preserve");  
        t.setValue(content);  
        R run = Docx4j_Helper.factory.createR();  
        run.setRPr(rpr);  
        run.getContent().add(t);  
        p.getContent().add(run);  
    } 
    
}
