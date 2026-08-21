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
package io.github.easy4j.doc.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTShd;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.Ind;
import org.docx4j.wml.PPrBase.PBdr;
import org.docx4j.wml.PPrBase.Spacing;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STLineSpacingRule;
import org.docx4j.wml.STShd;
import org.docx4j.wml.Text;

/**
 * Paragraph-level helpers ({@code P}, {@code PPr}, run attachment, images).
 * <p>
 * Extracted from {@code WmlElementUtils} (chunk-6 refactor).
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class WmlParagraphUtils {

    private static final ObjectFactory FACTORY = Context.getWmlObjectFactory();

    private WmlParagraphUtils() {
    }

    /*
     *  创建一个对象工厂并用它创建一个段落和一个可运行块R.
     *  然后将可运行块添加到段落中. 接下来创建一个图画并将其添加到可运行块R中. 最后我们将内联
     *  对象添加到图画中并返回段落对象.
     *
     * @param   inline 包含图片的内联对象.
     * @return  包含图片的段落
     */
    public static P addInlineImageToParagraph(Inline inline) {
        // 添加内联对象到一个段落中
        P paragraph = FACTORY.createP();
        R run = FACTORY.createR();
        paragraph.getContent().add(run);
        Drawing drawing = FACTORY.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);
        return paragraph;
    }

    /**
     * @Description: 只删除单独的段落，不包括表格内或其他内的段落
     */
    public static boolean removeParaByIndex(WordprocessingMLPackage wordMLPackage, int index) {
        boolean flag = false;
        if (index < 0) {
            return flag;
        }
        List<Object> objList = wordMLPackage.getMainDocumentPart().getContent();
        if (objList == null) {
            return flag;
        }
        int k = -1;
        for (int i = 0, len = objList.size(); i < len; i++) {
            if (objList.get(i) instanceof P) {
                k++;
                if (k == index) {
                    wordMLPackage.getMainDocumentPart().getContent().remove(i);
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * @Description: 设置段落水平对齐方式
     */
    public static void setParaJcAlign(P paragraph, JcEnumeration hAlign) {
        if (hAlign != null) {
            PPr pprop = paragraph.getPPr();
            if (pprop == null) {
                pprop = new PPr();
                paragraph.setPPr(pprop);
            }
            Jc align = new Jc();
            align.setVal(hAlign);
            pprop.setJc(align);
        }
    }

    /**
     * @Description: 设置段落内容
     */
    public static void setParaRContent(P p, RPr runProperties, String content) {
        R run = null;
        List<Object> rList = p.getContent();
        if (rList != null && rList.size() > 0) {
            for (int i = 0, len = rList.size(); i < len; i++) {
                // 清除内容(所有的r
                p.getContent().remove(0);
            }
        }
        run = new R();
        p.getContent().add(run);
        if (content != null) {
            String[] contentArr = content.split("\n");
            Text text = new Text();
            text.setSpace("preserve");
            text.setValue(contentArr[0]);
            run.setRPr(runProperties);
            run.getContent().add(text);

            for (int i = 1, len = contentArr.length; i < len; i++) {
                Br br = new Br();
                run.getContent().add(br);// 换行
                text = new Text();
                text.setSpace("preserve");
                text.setValue(contentArr[i]);
                run.setRPr(runProperties);
                run.getContent().add(text);
            }
        }
    }

    /**
     * @Description: 添加段落内容
     */
    public static void appendParaRContent(P p, RPr runProperties, String content) {
        if (content != null) {
            R run = new R();
            p.getContent().add(run);
            String[] contentArr = content.split("\n");
            Text text = new Text();
            text.setSpace("preserve");
            text.setValue(contentArr[0]);
            run.setRPr(runProperties);
            run.getContent().add(text);

            for (int i = 1, len = contentArr.length; i < len; i++) {
                Br br = new Br();
                run.getContent().add(br);// 换行
                text = new Text();
                text.setSpace("preserve");
                text.setValue(contentArr[i]);
                run.setRPr(runProperties);
                run.getContent().add(text);
            }
        }
    }

    /**
     * @Description: 添加图片到段落
     */
    public static void addImageToPara(WordprocessingMLPackage wordMLPackage, ObjectFactory factory, P paragraph,
            String filePath, String content, RPr rpr, String altText, int id1, int id2) throws Exception {
        R run = factory.createR();
        if (content != null) {
            Text text = factory.createText();
            text.setValue(content);
            text.setSpace("preserve");
            run.setRPr(rpr);
            run.getContent().add(text);
        }

        InputStream is = new FileInputStream(filePath);
        byte[] bytes = IOUtils.toByteArray(is);
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
        Inline inline = imagePart.createImageInline(filePath, altText, id1, id2, false);
        Drawing drawing = factory.createDrawing();
        drawing.getAnchorOrInline().add(inline);
        run.getContent().add(drawing);
        paragraph.getContent().add(run);
    }

    /**
     * @Description: 段落添加Br 页面Break(分页符)
     */
    public static void addPageBreak(P para, STBrType sTBrType) {
        Br breakObj = new Br();
        breakObj.setType(sTBrType);
        para.getContent().add(breakObj);
    }

    /**
     * @Description: 设置段落是否禁止行号(禁止用于当前行号)
     */
    public static void setParagraphSuppressLineNum(P p) {
        PPr ppr = getPPr(p);
        BooleanDefaultTrue line = ppr.getSuppressLineNumbers();
        if (line == null) {
            line = new BooleanDefaultTrue();
        }
        line.setVal(true);
        ppr.setSuppressLineNumbers(line);
    }

    /**
     * @Description: 设置段落底纹(对整段文字起作用)
     */
    public static void setParagraphShdStyle(P p, STShd shdType, String shdColor) {
        PPr ppr = getPPr(p);
        CTShd ctShd = ppr.getShd();
        if (ctShd == null) {
            ctShd = new CTShd();
        }
        if (StringUtils.isNotBlank(shdColor)) {
            ctShd.setColor(shdColor);
        }
        if (shdType != null) {
            ctShd.setVal(shdType);
        }
        ppr.setShd(ctShd);
    }

    /**
     * @param isSpace
     *            是否设置段前段后值
     * @param before
     *            段前磅数
     * @param after
     *            段后磅数
     * @param beforeLines
     *            段前行数
     * @param afterLines
     *            段后行数
     * @param isLine
     *            是否设置行距
     * @param lineValue
     *            行距值
     * @param sTLineSpacingRule
     *            自动auto 固定exact 最小 atLeast 1磅=20 1行=100 单倍行距=240
     */
    public static void setParagraphSpacing(P p, boolean isSpace, String before, String after, String beforeLines,
            String afterLines, boolean isLine, String lineValue, STLineSpacingRule sTLineSpacingRule) {
        PPr pPr = getPPr(p);
        Spacing spacing = pPr.getSpacing();
        if (spacing == null) {
            spacing = new Spacing();
            pPr.setSpacing(spacing);
        }
        if (isSpace) {
            if (StringUtils.isNotBlank(before)) {
                // 段前磅数
                spacing.setBefore(new BigInteger(before));
            }
            if (StringUtils.isNotBlank(after)) {
                // 段后磅数
                spacing.setAfter(new BigInteger(after));
            }
            if (StringUtils.isNotBlank(beforeLines)) {
                // 段前行数
                spacing.setBeforeLines(new BigInteger(beforeLines));
            }
            if (StringUtils.isNotBlank(afterLines)) {
                // 段后行数
                spacing.setAfterLines(new BigInteger(afterLines));
            }
        }
        if (isLine) {
            if (StringUtils.isNotBlank(lineValue)) {
                spacing.setLine(new BigInteger(lineValue));
            }
            if (sTLineSpacingRule != null) {
                spacing.setLineRule(sTLineSpacingRule);
            }
        }
    }

    /**
     * @Description: 设置段落缩进信息 1厘米≈567
     */
    public static void setParagraphIndInfo(P p, String firstLine, String firstLineChar, String hanging, String hangingChar,
            String right, String rigthChar, String left, String leftChar) {
        PPr ppr = getPPr(p);
        Ind ind = ppr.getInd();
        if (ind == null) {
            ind = new Ind();
            ppr.setInd(ind);
        }
        if (StringUtils.isNotBlank(firstLine)) {
            ind.setFirstLine(new BigInteger(firstLine));
        }
        if (StringUtils.isNotBlank(firstLineChar)) {
            ind.setFirstLineChars(new BigInteger(firstLineChar));
        }
        if (StringUtils.isNotBlank(hanging)) {
            ind.setHanging(new BigInteger(hanging));
        }
        if (StringUtils.isNotBlank(hangingChar)) {
            ind.setHangingChars(new BigInteger(hangingChar));
        }
        if (StringUtils.isNotBlank(left)) {
            ind.setLeft(new BigInteger(left));
        }
        if (StringUtils.isNotBlank(leftChar)) {
            ind.setLeftChars(new BigInteger(leftChar));
        }
        if (StringUtils.isNotBlank(right)) {
            ind.setRight(new BigInteger(right));
        }
        if (StringUtils.isNotBlank(rigthChar)) {
            ind.setRightChars(new BigInteger(rigthChar));
        }
    }

    public static PPr getPPr(P p) {
        PPr ppr = p.getPPr();
        if (ppr == null) {
            ppr = new PPr();
            p.setPPr(ppr);
        }
        return ppr;
    }

    public static ParaRPr getParaRPr(PPr ppr) {
        ParaRPr parRpr = ppr.getRPr();
        if (parRpr == null) {
            parRpr = new ParaRPr();
            ppr.setRPr(parRpr);
        }
        return parRpr;

    }

    public static void setParaVanish(PPr ppr, boolean isVanish) {
        ParaRPr parRpr = getParaRPr(ppr);
        BooleanDefaultTrue vanish = parRpr.getVanish();
        if (vanish != null) {
            vanish.setVal(isVanish);
        } else {
            vanish = new BooleanDefaultTrue();
            parRpr.setVanish(vanish);
            vanish.setVal(isVanish);
        }
    }

    /**
     * @Description: 设置段落边框样式
     */
    public static void setParagraghBorders(P p, CTBorder topBorder, CTBorder bottomBorder, CTBorder leftBorder,
            CTBorder rightBorder) {
        PPr ppr = getPPr(p);
        PBdr pBdr = new PBdr();
        if (topBorder != null) {
            pBdr.setTop(topBorder);
        }
        if (bottomBorder != null) {
            pBdr.setBottom(bottomBorder);
        }
        if (leftBorder != null) {
            pBdr.setLeft(leftBorder);
        }
        if (rightBorder != null) {
            pBdr.setRight(rightBorder);
        }
        ppr.setPBdr(pBdr);
    }
}
