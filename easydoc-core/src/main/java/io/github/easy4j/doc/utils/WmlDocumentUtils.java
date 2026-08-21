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

import java.math.BigInteger;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.wml.CTBackground;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTLineNumber;
import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.SectPr.PgBorders;
import org.docx4j.wml.TextDirection;
import org.docx4j.wml.STLineNumberRestart;

/**
 * Document-level helpers: load/save/create WordprocessingMLPackage, page
 * background/borders, line numbering, text direction, vertical alignment.
 * <p>
 * Extracted from {@code WmlElementUtils} (chunk-6 refactor).
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class WmlDocumentUtils {

    private static final ObjectFactory FACTORY = Context.getWmlObjectFactory();

    private WmlDocumentUtils() {
    }

    /**
     * @Description: 新建WordprocessingMLPackage
     */
    public static WordprocessingMLPackage createWordprocessingMLPackage() throws Exception {
        return WordprocessingMLPackage.createPackage();
    }

    /**
     * @Description: 加载带密码WordprocessingMLPackage
     */
    public static WordprocessingMLPackage loadWordprocessingMLPackageWithPwd(String filePath, String password)
            throws Exception {
        OpcPackage opcPackage = WordprocessingMLPackage.load(new java.io.File(filePath), password);
        WordprocessingMLPackage wordMLPackage = (WordprocessingMLPackage) opcPackage;
        return wordMLPackage;
    }

    /**
     * @Description: 加载WordprocessingMLPackage
     */
    public static WordprocessingMLPackage loadWordprocessingMLPackage(String filePath) throws Exception {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File(filePath));
        return wordMLPackage;
    }

    /**
     * @Description: 插入图片
     * @param wPackage
     * @param bm
     * @param file
     * @throws Exception
     */
    public static void addImage(WordprocessingMLPackage wPackage, org.docx4j.wml.CTBookmark bm, String file) throws Exception {
        // 这儿可以对单个书签进行操作，也可以用一个map对所有的书签进行处理
        // 获取该书签的父级段落
        P p = (P) (bm.getParent());
        // R对象是匿名的复杂类型，然而我并不知道具体啥意思，估计这个要好好去看看ooxml才知道
        R run = FACTORY.createR();
        // 读入图片并转化为字节数组，因为docx4j只能字节数组的方式插入图片
        byte[] bytes = null;//FileUtil.getByteFormBase64DataByImage(file);

        //	InputStream is = new FileInputStream;
        //	byte[] bytes = IOUtils.toByteArray(inputStream);
        //  byte[] bytes = FileUtil.getByteFormBase64DataByImage("");
        // 开始创建一个行内图片
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wPackage, bytes);
        // createImageInline函数的前四个参数我都没有找到具体啥意思，，，，
        // 最有一个是限制图片的宽度，缩放的依据
        Inline inline = imagePart.createImageInline(null, null, 0, 1, false, 0);
        // 获取该书签的父级段落
        // drawing理解为画布？
        Drawing drawing = FACTORY.createDrawing();
        drawing.getAnchorOrInline().add(inline);
        run.getContent().add(drawing);
        p.getContent().add(run);
    }

    /**
     * @Description: 设置页面背景色
     */
    public static void setDocumentBackGround(WordprocessingMLPackage wordPackage, ObjectFactory factory, String color)
            throws Exception {
        MainDocumentPart mdp = wordPackage.getMainDocumentPart();
        CTBackground bkground = mdp.getContents().getBackground();
        if (StringUtils.isNotBlank(color)) {
            if (bkground == null) {
                bkground = factory.createCTBackground();
                bkground.setColor(color);
            }
            mdp.getContents().setBackground(bkground);
        }
    }

    /**
     * @Description: 设置页面边框
     */
    public static void setDocumentBorders(WordprocessingMLPackage wordPackage, ObjectFactory factory, CTBorder top,
            CTBorder right, CTBorder bottom, CTBorder left) {
        SectPr sectPr = WmlSectionUtils.getDocSectPr(wordPackage);
        PgBorders pgBorders = sectPr.getPgBorders();
        if (pgBorders == null) {
            pgBorders = factory.createSectPrPgBorders();
            sectPr.setPgBorders(pgBorders);
        }
        if (top != null) {
            pgBorders.setTop(top);
        }
        if (right != null) {
            pgBorders.setRight(right);
        }
        if (bottom != null) {
            pgBorders.setBottom(bottom);
        }
        if (left != null) {
            pgBorders.setLeft(left);
        }
    }

    /**
     * @Description: 设置行号
     * @param distance
     *            :距正文距离 1厘米=567
     * @param start
     *            :起始编号(0开始)
     * @param countBy
     *            :行号间隔
     * @param restartType
     *            :STLineNumberRestart.CONTINUOUS(continuous连续编号)<br/>
     *            STLineNumberRestart.NEW_PAGE(每页重新编号)<br/>
     *            STLineNumberRestart.NEW_SECTION(每节重新编号)
     */
    public static void setDocInNumType(WordprocessingMLPackage wordPackage, String countBy, String distance, String start,
            STLineNumberRestart restartType) {
        SectPr sectPr = WmlSectionUtils.getDocSectPr(wordPackage);
        CTLineNumber lnNumType = sectPr.getLnNumType();
        if (lnNumType == null) {
            lnNumType = new CTLineNumber();
            sectPr.setLnNumType(lnNumType);
        }
        if (StringUtils.isNotBlank(countBy)) {
            lnNumType.setCountBy(new BigInteger(countBy));
        }
        if (StringUtils.isNotBlank(distance)) {
            lnNumType.setDistance(new BigInteger(distance));
        }
        if (StringUtils.isNotBlank(start)) {
            lnNumType.setStart(new BigInteger(start));
        }
        if (restartType != null) {
            lnNumType.setRestart(restartType);
        }
    }

    /**
     * @Description：设置文字方向 tbRl 垂直
     */
    public static void setDocTextDirection(WordprocessingMLPackage wordPackage, String textDirection) {
        if (StringUtils.isNotBlank(textDirection)) {
            SectPr sectPr = WmlSectionUtils.getDocSectPr(wordPackage);
            TextDirection textDir = sectPr.getTextDirection();
            if (textDir == null) {
                textDir = new TextDirection();
                sectPr.setTextDirection(textDir);
            }
            textDir.setVal(textDirection);
        }
    }

    /**
     * @Description：设置word 垂直对齐方式(Word默认方式都是"顶端对齐")
     */
    public static void setDocVAlign(WordprocessingMLPackage wordPackage, STVerticalJc valignType) {
        if (valignType != null) {
            SectPr sectPr = WmlSectionUtils.getDocSectPr(wordPackage);
            CTVerticalJc valign = sectPr.getVAlign();
            if (valign == null) {
                valign = new CTVerticalJc();
                sectPr.setVAlign(valign);
            }
            valign.setVal(valignType);
        }
    }
}
