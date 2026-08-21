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
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTEm;
import org.docx4j.wml.CTShd;
import org.docx4j.wml.CTSignedHpsMeasure;
import org.docx4j.wml.CTSignedTwipsMeasure;
import org.docx4j.wml.CTTextScale;
import org.docx4j.wml.CTVerticalAlignRun;
import org.docx4j.wml.Color;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Highlight;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.STEm;
import org.docx4j.wml.STShd;
import org.docx4j.wml.STVerticalAlignRun;
import org.docx4j.wml.U;
import org.docx4j.wml.UnderlineEnumeration;

/**
 * Run-property ({@code RPr}) helpers for docx4j: font family/size/color, bold,
 * italic, underline, strike, vanish, shadow, shd, highlight, position, scale,
 * spacing, emboss, imprint, outline, em, border, vertAlign.
 * <p>
 * Extracted from {@code WmlElementUtils} (chunk-6 refactor).
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class WmlRunStyleUtils {

    private WmlRunStyleUtils() {
    }

    public static RPr getRPr(R r) {
        RPr rpr = r.getRPr();
        if (rpr == null) {
            rpr = new RPr();
            r.setRPr(rpr);
        }
        return rpr;
    }

    /**
     * @Description: 设置字体信息
     */
    public static void setFontStyle(RPr runProperties, String cnFontFamily, String enFontFamily, String fontSize,
            String color) {
        setFontFamily(runProperties, cnFontFamily, enFontFamily);
        setFontSize(runProperties, fontSize);
        setFontColor(runProperties, color);
    }

    /**
     * @Description: 设置字体大小
     */
    public static void setFontSize(RPr runProperties, String fontSize) {
        if (StringUtils.isNotBlank(fontSize)) {
            HpsMeasure size = new HpsMeasure();
            size.setVal(new BigInteger(fontSize));
            runProperties.setSz(size);
            runProperties.setSzCs(size);
        }
    }

    /**
     * @Description: 设置字体
     */
    public static void setFontFamily(RPr runProperties, String cnFontFamily, String enFontFamily) {
        if (StringUtils.isNotBlank(cnFontFamily) || StringUtils.isNotBlank(enFontFamily)) {
            RFonts rf = runProperties.getRFonts();
            if (rf == null) {
                rf = new RFonts();
                runProperties.setRFonts(rf);
            }
            if (cnFontFamily != null) {
                rf.setEastAsia(cnFontFamily);
            }
            if (enFontFamily != null) {
                rf.setAscii(enFontFamily);
            }
        }
    }

    /**
     * @Description: 设置字体颜色
     */
    public static void setFontColor(RPr runProperties, String color) {
        if (color != null) {
            Color c = new Color();
            c.setVal(color);
            runProperties.setColor(c);
        }
    }

    /**
     * @Description: 设置字符边框
     */
    public static void addRPrBorderStyle(RPr runProperties, String size, STBorder bordType, String space, String color) {
        CTBorder value = new CTBorder();
        if (StringUtils.isNotBlank(color)) {
            value.setColor(color);
        }
        if (StringUtils.isNotBlank(size)) {
            value.setSz(new BigInteger(size));
        }
        if (StringUtils.isNotBlank(space)) {
            value.setSpace(new BigInteger(space));
        }
        if (bordType != null) {
            value.setVal(bordType);
        }
        runProperties.setBdr(value);
    }

    /**
     * @Description:着重号
     */
    public static void addRPrEmStyle(RPr runProperties, STEm emType) {
        if (emType != null) {
            CTEm em = new CTEm();
            em.setVal(emType);
            runProperties.setEm(em);
        }
    }

    /**
     * @Description: 空心
     */
    public static void addRPrOutlineStyle(RPr runProperties) {
        BooleanDefaultTrue outline = new BooleanDefaultTrue();
        outline.setVal(true);
        runProperties.setOutline(outline);
    }

    /**
     * @Description: 设置上标下标
     */
    public static void addRPrcaleStyle(RPr runProperties, STVerticalAlignRun vAlign) {
        if (vAlign != null) {
            CTVerticalAlignRun value = new CTVerticalAlignRun();
            value.setVal(vAlign);
            runProperties.setVertAlign(value);
        }
    }

    /**
     * @Description: 设置字符间距缩进
     */
    public static void addRPrScaleStyle(RPr runProperties, int indent) {
        CTTextScale value = new CTTextScale();
        value.setVal(indent);
        runProperties.setW(value);
    }

    /**
     * @Description: 设置字符间距信息
     */
    public static void addRPrtSpacingStyle(RPr runProperties, int spacing) {
        CTSignedTwipsMeasure value = new CTSignedTwipsMeasure();
        value.setVal(BigInteger.valueOf(spacing));
        runProperties.setSpacing(value);
    }

    /**
     * @Description: 设置文本位置
     */
    public static void addRPrtPositionStyle(RPr runProperties, int position) {
        CTSignedHpsMeasure ctPosition = new CTSignedHpsMeasure();
        ctPosition.setVal(BigInteger.valueOf(position));
        runProperties.setPosition(ctPosition);
    }

    /**
     * @Description: 阴文
     */
    public static void addRPrImprintStyle(RPr runProperties) {
        BooleanDefaultTrue imprint = new BooleanDefaultTrue();
        imprint.setVal(true);
        runProperties.setImprint(imprint);
    }

    /**
     * @Description: 阳文
     */
    public static void addRPrEmbossStyle(RPr runProperties) {
        BooleanDefaultTrue emboss = new BooleanDefaultTrue();
        emboss.setVal(true);
        runProperties.setEmboss(emboss);
    }

    /**
     * @Description: 设置隐藏
     */
    public static void setRPrVanishStyle(RPr runProperties, boolean isVanish) {
        BooleanDefaultTrue vanish = runProperties.getVanish();
        if (vanish != null) {
            vanish.setVal(isVanish);
        } else {
            vanish = new BooleanDefaultTrue();
            vanish.setVal(isVanish);
            runProperties.setVanish(vanish);
        }
    }

    /**
     * @Description: 设置阴影
     */
    public static void addRPrShadowStyle(RPr runProperties) {
        BooleanDefaultTrue shadow = new BooleanDefaultTrue();
        shadow.setVal(true);
        runProperties.setShadow(shadow);
    }

    /**
     * @Description: 设置底纹
     */
    public static void addRPrShdStyle(RPr runProperties, STShd shdtype) {
        if (shdtype != null) {
            CTShd shd = new CTShd();
            shd.setVal(shdtype);
            runProperties.setShd(shd);
        }
    }

    /**
     * @Description: 设置突出显示文本
     */
    public static void addRPrHightLightStyle(RPr runProperties, String hightlight) {
        if (StringUtils.isNotBlank(hightlight)) {
            Highlight highlight = new Highlight();
            highlight.setVal(hightlight);
            runProperties.setHighlight(highlight);
        }
    }

    /**
     * @Description: 设置删除线样式
     */
    public static void addRPrStrikeStyle(RPr runProperties, boolean isStrike, boolean isDStrike) {
        // 删除线
        if (isStrike) {
            BooleanDefaultTrue strike = new BooleanDefaultTrue();
            strike.setVal(true);
            runProperties.setStrike(strike);
        }
        // 双删除线
        if (isDStrike) {
            BooleanDefaultTrue dStrike = new BooleanDefaultTrue();
            dStrike.setVal(true);
            runProperties.setDstrike(dStrike);
        }
    }

    /**
     * @Description: 加粗
     */
    public static void addRPrBoldStyle(RPr runProperties) {
        BooleanDefaultTrue b = new BooleanDefaultTrue();
        b.setVal(true);
        runProperties.setB(b);
    }

    /**
     * @Description: 倾斜
     */
    public static void addRPrItalicStyle(RPr runProperties) {
        BooleanDefaultTrue b = new BooleanDefaultTrue();
        b.setVal(true);
        runProperties.setI(b);
    }

    /**
     * @Description: 添加下划线
     */
    public static void addRPrUnderlineStyle(RPr runProperties, UnderlineEnumeration enumType) {
        U val = new U();
        val.setVal(enumType);
        runProperties.setU(val);
    }
}
