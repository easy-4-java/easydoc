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
import java.util.List;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Style;
import org.docx4j.wml.Styles;
import org.docx4j.wml.U;
import org.docx4j.wml.UnderlineEnumeration;

/**
 * @className	： ChangingTheStyleSheet
 * @description	： 更改默认样式
	几乎所有的客户都想要他们自己的风格。Word提供了一些默认的样式但它们不够好（注意一下我没有更改两个图像之间的文本，因此图像中的文本并不反映实际的风格）：
 * @author 		： <a href="https://github.com/easy-4-java">hiwepy</a>
 * @date		： 2017年5月24日 下午10:35:52
 * @version 	V1.0
 */
public class ChangingTheStyleSheet {
    private static WordprocessingMLPackage  wordMLPackage;
 
    /**
     *  First we create the package, then we alter the style sheet and add some
     *  styled paragraphs. Finally we save the package.
     */
    public static void main (String[] args) throws Docx4JException {
        wordMLPackage = WordprocessingMLPackage.createPackage();
        alterStyleSheet();
 
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title",
            "Hello World! This title is now in Arial.");
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Subtitle",
            "Subtitle, this subtitle is now Arial too");
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading1",
            "As is Heading1");
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2",
            "Heading2 is now Arial, no longer bold and has an underline " +
            "and fontsize 12");
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
            "Heading3 is now Arial");
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Normal",
            "And normal text has changed to Arial and fontsize 10");
 
        wordMLPackage.save(new java.io.File("src/main/files/HelloWord12.docx") );
    }
 
    /**
     *  This method alters the default style sheet that is part of each document.
     *
     *  To do this, we first retrieve the style sheet from the package and then
     *  get the Styles object from it. From this object, we get the list of actual
     *  styles and iterate over them.
     *  We check against all styles we want to alter and apply the alterations if
     *  applicable.
     *
     *  @param wordMLPackage
     */
    public static void alterStyleSheet() {
        StyleDefinitionsPart styleDefinitionsPart =
            wordMLPackage.getMainDocumentPart().getStyleDefinitionsPart();
        Styles styles = styleDefinitionsPart.getJaxbElement();
 
        List<Style>  stylesList = styles.getStyle();
        for (Style style : stylesList) {
            if (style.getStyleId().equals("Normal")) {
                alterNormalStyle(style);
            } else if (style.getStyleId().equals("Heading2")) {
                alterHeading2Style(style);
            } else if (style.getStyleId().equals("Heading1") ||
                    style.getStyleId().equals("Heading3") ||
                    style.getStyleId().equals("Title") ||
                    style.getStyleId().equals("Subtitle")) {
                getRunPropertiesAndRemoveThemeInfo(style);
            }
        }
    }
 
    /**
     *  First we create a run properties object as we want to remove nearly all of
     *  the existing styling. Then we change the font and font size and set the
     *  run properties on the given style. As in previous examples, the font size
     *  is defined to be in half-point size.
     */
    private static void alterNormalStyle(Style style) {
        // we want to change (or remove) almost all the run properties of the
        // normal style, so we create a new one.
        RPr rpr = new RPr();
        changeFontToArial(rpr);
        changeFontSize(rpr, 20);
        style.setRPr(rpr);
    }
 
    /**
     *  For this style, we get the existing run properties from the style and
     *  remove the theme font information from them. Then we also remove the bold
     *  styling, change the font size (half-points) and add an underline.
     */
    private static void alterHeading2Style(Style style) {
        RPr rpr = getRunPropertiesAndRemoveThemeInfo(style);
        removeBoldStyle(rpr);
        changeFontSize(rpr, 24);
        addUnderline(rpr);
    }
 
    private static RPr getRunPropertiesAndRemoveThemeInfo(Style style) {
        // We only want to change some settings, so we get the existing run
        // properties from the style.
        RPr rpr = style.getRPr();
        removeThemeFontInformation(rpr);
        return rpr;
    }
 
    /**
     *  Change the font of the given run properties to Arial.
     *
     *  A run font specifies the fonts which shall be used to display the contents
     *  of the run. Of the four possible types of content, we change the styling of
     *  two of them: ASCII and High ANSI.
     *  Finally we add the run font to the run properties.
     *
     *  @param runProperties
     */
    private static void changeFontToArial(RPr runProperties) {
        RFonts runFont = new RFonts();
        runFont.setAscii("Arial");
        runFont.setHAnsi("Arial");
        runProperties.setRFonts(runFont);
    }
 
    /**
     * Change the font size of the given run properties to the given value.
     *
     * @param runProperties
     * @param fontSize  Twice the size needed, as it is specified as half-point value
     */
    private static void changeFontSize(RPr runProperties, int fontSize) {
        HpsMeasure size = new HpsMeasure();
        size.setVal(BigInteger.valueOf(fontSize));
        runProperties.setSz(size);
    }
 
    /**
     * Removes the theme font information from the run properties.
     * If this is not removed then the styles based on the normal style won't
     * inherit the Arial font from the normal style.
     *
     * @param runProperties
     */
    private static void removeThemeFontInformation(RPr runProperties) {
        runProperties.getRFonts().setAsciiTheme(null);
        runProperties.getRFonts().setHAnsiTheme(null);
    }
 
    /**
     * Removes the Bold styling from the run properties.
     *
     * @param runProperties
     */
    private static void removeBoldStyle(RPr runProperties) {
        runProperties.getB().setVal(false);
    }
 
    /**
     * Adds a single underline to the run properties.
     *
     * @param runProperties
     */
    private static void addUnderline(RPr runProperties) {
        U underline = new U();
        underline.setVal(UnderlineEnumeration.SINGLE);
        runProperties.setU(underline );
    }
}
