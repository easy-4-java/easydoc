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
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.STPageOrientation;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.SectPr.PgMar;
import org.docx4j.wml.SectPr.PgSz;
import org.docx4j.wml.SectPr.Type;
import org.docx4j.wml.Text;

/**
 * Section-level helpers ({@code SectPr}, footer, page size/margin).
 * <p>
 * Extracted from {@code WmlElementUtils} (chunk-6 refactor).
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class WmlSectionUtils {

    private static final ObjectFactory FACTORY = Context.getWmlObjectFactory();

    private WmlSectionUtils() {
    }

    /*
     *  First we create a footer, a paragraph, a run and a text. We add the given
     *  given content to the text and add that to the run. The run is then added to
     *  the paragraph, which is in turn added to the footer. Finally we return the
     *  footer.
     *  @param content
     *  @return
     */
    public static Ftr createFooter(String content) {
        Ftr footer = FACTORY.createFtr();
        P paragraph = FACTORY.createP();
        R run = FACTORY.createR();
        Text text = new Text();
        text.setValue(content);
        run.getContent().add(text);
        paragraph.getContent().add(run);
        footer.getContent().add(paragraph);
        return footer;
    }

    public static SectPr getDocSectPr(WordprocessingMLPackage wordPackage) {
        SectPr sectPr = wordPackage.getDocumentModel().getSections().get(0).getSectPr();
        return sectPr;
    }

    /**
     * @Description: 设置分节符 nextPage:下一页 continuous:连续 evenPage:偶数页 oddPage:奇数页
     */
    public static void setDocSectionBreak(WordprocessingMLPackage wordPackage, String sectValType) {
        if (StringUtils.isNotBlank(sectValType)) {
            SectPr sectPr = getDocSectPr(wordPackage);
            Type sectType = sectPr.getType();
            if (sectType == null) {
                sectType = new Type();
                sectPr.setType(sectType);
            }
            sectType.setVal(sectValType);
        }
    }

    /**
     * @Description：设置页边距
     */
    public static void setDocMarginSpace(WordprocessingMLPackage wordPackage, ObjectFactory factory, String top, String left,
            String bottom, String right) {
        SectPr sectPr = getDocSectPr(wordPackage);
        PgMar pg = sectPr.getPgMar();
        if (pg == null) {
            pg = factory.createSectPrPgMar();
            sectPr.setPgMar(pg);
        }
        if (StringUtils.isNotBlank(top)) {
            pg.setTop(new BigInteger(top));
        }
        if (StringUtils.isNotBlank(bottom)) {
            pg.setBottom(new BigInteger(bottom));
        }
        if (StringUtils.isNotBlank(left)) {
            pg.setLeft(new BigInteger(left));
        }
        if (StringUtils.isNotBlank(right)) {
            pg.setRight(new BigInteger(right));
        }
    }

    /**
     * @Description: 设置页面大小及纸张方向 landscape横向
     */
    public static void setDocumentSize(WordprocessingMLPackage wordPackage, ObjectFactory factory, String width, String height,
            STPageOrientation stValue) {
        SectPr sectPr = getDocSectPr(wordPackage);
        PgSz pgSz = sectPr.getPgSz();
        if (pgSz == null) {
            pgSz = factory.createSectPrPgSz();
            sectPr.setPgSz(pgSz);
        }
        if (StringUtils.isNotBlank(width)) {
            pgSz.setW(new BigInteger(width));
        }
        if (StringUtils.isNotBlank(height)) {
            pgSz.setH(new BigInteger(height));
        }
        if (stValue != null) {
            pgSz.setOrient(stValue);
        }
    }

    /**
     * @Description：获取文档的可用宽度
     */
    public static int getWritableWidth(WordprocessingMLPackage wordPackage) throws Exception {
        return wordPackage.getDocumentModel().getSections().get(0).getPageDimensions().getWritableWidthTwips();
    }
}
