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

/**
 * http://53873039oycg.iteye.com/blog/2158829
 */
import java.io.File;  
import java.math.BigInteger;  
import java.util.Calendar;  
import java.util.Date;  
import java.util.GregorianCalendar;  
  
import javax.xml.datatype.DatatypeFactory;  
import javax.xml.datatype.XMLGregorianCalendar;  
  
import org.docx4j.jaxb.Context;  
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;  
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart;  
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;  
import org.docx4j.wml.BooleanDefaultTrue;  
import org.docx4j.wml.CTShd;  
import org.docx4j.wml.CTVerticalAlignRun;  
import org.docx4j.wml.Color;  
import org.docx4j.wml.CommentRangeEnd;  
import org.docx4j.wml.CommentRangeStart;  
import org.docx4j.wml.Comments;  
import org.docx4j.wml.Comments.Comment;  
import org.docx4j.wml.Highlight;  
import org.docx4j.wml.HpsMeasure;  
import org.docx4j.wml.Jc;  
import org.docx4j.wml.JcEnumeration;  
import org.docx4j.wml.ObjectFactory;  
import org.docx4j.wml.P;  
import org.docx4j.wml.PPr;  
import org.docx4j.wml.PPrBase.Spacing;  
import org.docx4j.wml.PPrBase.TextAlignment;  
import org.docx4j.wml.R;  
import org.docx4j.wml.RFonts;  
import org.docx4j.wml.RPr;  
import org.docx4j.wml.STHint;  
import org.docx4j.wml.STLineSpacingRule;  
import org.docx4j.wml.STShd;  
import org.docx4j.wml.Text;  
import org.docx4j.wml.U;  
import org.docx4j.wml.UnderlineEnumeration;  
  
public class Docx4j_创建批注_S3_Test {  
    public static void main(String[] args) throws Exception {  
        Docx4j_创建批注_S3_Test t = new Docx4j_创建批注_S3_Test();  
        WordprocessingMLPackage wordMLPackage = t  
                .createWordprocessingMLPackage();  
        MainDocumentPart mp = wordMLPackage.getMainDocumentPart();  
        ObjectFactory factory = Context.getWmlObjectFactory();  
        t.testCreateComment(wordMLPackage, mp, factory);  
        t.saveWordPackage(wordMLPackage, new File("f:/saveFile/temp/sys_"  
                + System.currentTimeMillis() + ".docx"));  
    }  
  
    public void testCreateComment(WordprocessingMLPackage wordMLPackage,  
            MainDocumentPart t, ObjectFactory factory) throws Exception {  
        P p = factory.createP();  
        setParagraphSpacing(factory, p, true, "0",  
                "0",true, null, "100", true, "240", STLineSpacingRule.AUTO);  
        t.addObject(p);  
        RPr fontRPr = getRPrStyle(factory, "微软雅黑", "000000", "20",  
                STHint.EAST_ASIA, false, false, false, true, UnderlineEnumeration.SINGLE,  "B61CD2",  
                true, "darkYellow", false, null, null, null);  
        RPr commentRPr = getRPrStyle(factory, "微软雅黑", "41A62D", "18",  
                STHint.EAST_ASIA, true, true, false, false, null, null, false,  
                null, false, null, null, null);  
        Comments comments = addDocumentCommentsPart(wordMLPackage, factory);  
        BigInteger commentId = BigInteger.valueOf(1);  
          
        createCommentEnd(factory, p, "测试", "这是官网Demo", fontRPr, commentRPr, commentId, comments);  
        commentId = commentId.add(BigInteger.ONE);  
  
        createCommentRound(factory, p, "批注", "这是批注comment", fontRPr, commentRPr, commentId, comments);  
        commentId = commentId.add(BigInteger.ONE);  
          
        p = factory.createP();  
        setParagraphSpacing(factory, p, true, "0",  
                "0",true, null, "100", true, "240", STLineSpacingRule.AUTO);  
        t.addObject(p);  
        createCommentRound(factory, p, "批注2", "这是批注comment2", fontRPr, commentRPr, commentId, comments);  
        commentId = commentId.add(BigInteger.ONE);  
          
        createCommentEnd(factory, p, "测试2", "这是官网Demo", fontRPr, commentRPr, commentId, comments);  
        commentId = commentId.add(BigInteger.ONE);  
    }  
  
    public void createCommentEnd(ObjectFactory factory, P p, String pContent,  
            String commentContent, RPr fontRPr, RPr commentRPr,  
            BigInteger commentId, Comments comments) throws Exception{  
        Text txt = factory.createText();  
        txt.setValue(pContent);  
        R run = factory.createR();  
        run.getContent().add(txt);  
        run.setRPr(fontRPr);  
        p.getContent().add(run);  
        Comment commentOne = createComment(factory, commentId, "系统管理员",new Date(), commentContent, commentRPr);  
        comments.getComment().add(commentOne);  
        p.getContent().add(createRunCommentReference(factory, commentId));  
    }  
      
    //创建批注(选定范围)  
    public void createCommentRound(ObjectFactory factory, P p, String pContent,  
            String commentContent, RPr fontRPr, RPr commentRPr,  
            BigInteger commentId, Comments comments) throws Exception {  
        CommentRangeStart startComment = factory.createCommentRangeStart();  
        startComment.setId(commentId);  
        p.getContent().add(startComment);  
        R run = factory.createR();  
        Text txt = factory.createText();  
        txt.setValue(pContent);  
        run.getContent().add(txt);  
        run.setRPr(fontRPr);  
        p.getContent().add(run);  
        CommentRangeEnd endComment = factory.createCommentRangeEnd();  
        endComment.setId(commentId);  
        p.getContent().add(endComment);  
        Comment commentOne = createComment(factory, commentId, "系统管理员",  
                new Date(), commentContent, commentRPr);  
        comments.getComment().add(commentOne);  
        p.getContent().add(createRunCommentReference(factory, commentId));  
    }  
  
    public Comments addDocumentCommentsPart(  
            WordprocessingMLPackage wordMLPackage, ObjectFactory factory)  
            throws Exception {  
        CommentsPart cp = new CommentsPart();  
        wordMLPackage.getMainDocumentPart().addTargetPart(cp);  
        Comments comments = factory.createComments();  
        cp.setJaxbElement(comments);  
        return comments;  
    }  
  
    public Comments.Comment createComment(ObjectFactory factory,  
            BigInteger commentId, String author, Date date,  
            String commentContent, RPr commentRPr) throws Exception {  
        Comments.Comment comment = factory.createCommentsComment();  
        comment.setId(commentId);  
        if (author != null) {  
            comment.setAuthor(author);  
        }  
        if (date != null) {  
            comment.setDate(toXMLCalendar(date));  
        }  
        P commentP = factory.createP();  
        comment.getEGBlockLevelElts().add(commentP);  
        R commentR = factory.createR();  
        commentP.getContent().add(commentR);  
        Text commentText = factory.createText();  
        commentR.getContent().add(commentText);  
        commentR.setRPr(commentRPr);  
        commentText.setValue(commentContent);  
        return comment;  
    }  
  
    public R createRunCommentReference(ObjectFactory factory,  
            BigInteger commentId) {  
        R run = factory.createR();  
        R.CommentReference commentRef = factory.createRCommentReference();  
        run.getContent().add(commentRef);  
        commentRef.setId(commentId);  
        return run;  
    }  
  
    public XMLGregorianCalendar toXMLCalendar(Date d) throws Exception {  
        GregorianCalendar gc = new GregorianCalendar();  
        gc.setTime(d);  
        XMLGregorianCalendar xml = DatatypeFactory.newInstance()  
                .newXMLGregorianCalendar();  
        xml.setYear(gc.get(Calendar.YEAR));  
        xml.setMonth(gc.get(Calendar.MONTH) + 1);  
        xml.setDay(gc.get(Calendar.DAY_OF_MONTH));  
        xml.setHour(gc.get(Calendar.HOUR_OF_DAY));  
        xml.setMinute(gc.get(Calendar.MINUTE));  
        xml.setSecond(gc.get(Calendar.SECOND));  
        return xml;  
    }  
  
    // 字体样式  
    public RPr getRPrStyle(ObjectFactory factory, String fontFamily,  
            String colorVal, String fontSize, STHint sTHint, boolean isBlod,  
            boolean isItalic, boolean isStrike, boolean isUnderLine,  
            UnderlineEnumeration underLineStyle, String underLineColor,  
            boolean isHightLight, String hightLightValue, boolean isShd,  
            STShd shdValue, String shdColor, CTVerticalAlignRun stRunEnum) {  
        RPr rPr = factory.createRPr();  
        RFonts rf = new RFonts();  
        if (sTHint != null) {  
            rf.setHint(sTHint);  
        }  
        if (fontFamily != null) {  
            rf.setAscii(fontFamily);  
            rf.setEastAsia(fontFamily);  
            rf.setHAnsi(fontFamily);  
        }  
        rPr.setRFonts(rf);  
        if (colorVal != null) {  
            Color color = new Color();  
            color.setVal(colorVal);  
            rPr.setColor(color);  
        }  
        if (fontSize != null) {  
            HpsMeasure sz = new HpsMeasure();  
            sz.setVal(new BigInteger(fontSize));  
            rPr.setSz(sz);  
            rPr.setSzCs(sz);  
        }  
  
        BooleanDefaultTrue bdt = factory.createBooleanDefaultTrue();  
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
            if (underLineStyle != null) {  
                underline.setVal(underLineStyle);  
            }  
            if (underLineColor != null) {  
                underline.setColor(underLineColor);  
            }  
            rPr.setU(underline);  
        }  
        if (isHightLight) {  
            Highlight hight = new Highlight();  
            hight.setVal(hightLightValue);  
            rPr.setHighlight(hight);  
        }  
        if (isShd) {  
            CTShd shd = new CTShd();  
            if (shdColor != null) {  
                shd.setColor(shdColor);  
            }  
            if (shdValue != null) {  
                shd.setVal(shdValue);  
            }  
            rPr.setShd(shd);  
        }  
        if (stRunEnum != null) {  
            rPr.setVertAlign(stRunEnum);  
        }  
        return rPr;  
    }  
  
    // 段落底纹  
    public void setParagraphShdStyle(ObjectFactory factory, P p, boolean isShd,  
            STShd shdValue, String shdColor) {  
        if (isShd) {  
            PPr ppr = factory.createPPr();  
            CTShd shd = new CTShd();  
            if (shdColor != null) {  
                shd.setColor(shdColor);  
            }  
            if (shdValue != null) {  
                shd.setVal(shdValue);  
            }  
            ppr.setShd(shd);  
            p.setPPr(ppr);  
        }  
    }  
  
    // 段落间距  
    public void setParagraphSpacing(ObjectFactory factory, P p,  
            boolean isSpace, String before, String after, boolean isLines,  
            String beforeLines, String afterLines, boolean isLineRule,  
            String lineValue, STLineSpacingRule sTLineSpacingRule) {  
        PPr pPr = p.getPPr();  
        if (pPr == null) {  
            pPr = factory.createPPr();  
        }  
        Spacing spacing = new Spacing();  
        if (isSpace) {  
            if (before != null) {  
                // 段前磅数  
                spacing.setBefore(new BigInteger(before));  
            }  
            if (after != null) {  
                // 段后磅数  
                spacing.setAfter(new BigInteger(after));  
            }  
        }  
        if (isLines) {  
            if (beforeLines != null) {  
                // 段前行数  
                spacing.setBeforeLines(new BigInteger(beforeLines));  
            }  
            if (afterLines != null) {  
                // 段后行数  
                spacing.setAfterLines(new BigInteger(afterLines));  
            }  
        }  
        if (isLineRule) {  
            if (lineValue != null) {  
                spacing.setLine(new BigInteger(lineValue));  
            }  
            spacing.setLineRule(sTLineSpacingRule);  
        }  
        pPr.setSpacing(spacing);  
        p.setPPr(pPr);  
    }  
  
    // 段落对齐方式  
    public void setParagraphAlign(ObjectFactory factory, P p,  
            JcEnumeration jcEnumeration, TextAlignment textAlign) {  
        PPr pPr = p.getPPr();  
        if (pPr == null) {  
            pPr = factory.createPPr();  
        }  
        if (jcEnumeration != null) {  
            Jc jc = pPr.getJc();  
            if (jc == null) {  
                jc = new Jc();  
            }  
            jc.setVal(jcEnumeration);  
            pPr.setJc(jc);  
        }  
        if (textAlign != null) {  
            pPr.setTextAlignment(textAlign);  
        }  
        p.setPPr(pPr);  
    }  
  
    public WordprocessingMLPackage createWordprocessingMLPackage()  
            throws Exception {  
        return WordprocessingMLPackage.createPackage();  
    }  
  
    public void saveWordPackage(WordprocessingMLPackage wordPackage, File file)  
            throws Exception {  
        wordPackage.save(file);  
    }  
  
}  
