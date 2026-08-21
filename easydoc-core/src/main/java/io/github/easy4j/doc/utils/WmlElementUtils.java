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

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STEm;
import org.docx4j.wml.STLineNumberRestart;
import org.docx4j.wml.STLineSpacingRule;
import org.docx4j.wml.STPageOrientation;
import org.docx4j.wml.STShd;
import org.docx4j.wml.STVerticalAlignRun;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.docx4j.wml.TrPr;
import org.docx4j.wml.UnderlineEnumeration;

/**
 * Compatibility facade for the legacy {@code WmlElementUtils} god-class.
 * <p>
 * The original helpers were split (chunk-6 refactor) into six focused classes:
 * <ul>
 *   <li>{@link WmlElementTraversal} - XML/element traversal</li>
 *   <li>{@link WmlTableUtils} - table helpers ({@code Tbl}, {@code Tr}, {@code Tc})</li>
 *   <li>{@link WmlRunStyleUtils} - run-property ({@code RPr}) helpers</li>
 *   <li>{@link WmlParagraphUtils} - paragraph ({@code P}, {@code PPr}) helpers</li>
 *   <li>{@link WmlSectionUtils} - section ({@code SectPr}) helpers</li>
 *   <li>{@link WmlDocumentUtils} - document/package helpers</li>
 * </ul>
 * Every method below is a one-line delegator marked {@code @Deprecated} and is
 * scheduled for removal. New code should call the new classes directly.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
@SuppressWarnings("unchecked")
public class WmlElementUtils {

    protected static ObjectFactory factory = Context.getWmlObjectFactory();

    // ---- Traversal ----------------------------------------------------------

    /** @deprecated use {@link WmlElementTraversal#getChildrenElements}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static <T> List<T> getChildrenElements(Object source, Class<T> targetClass) {
        return WmlElementTraversal.getChildrenElements(source, targetClass);
    }

    /** @deprecated use {@link WmlElementTraversal#getTargetElements}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static <T> List<T> getTargetElements(Object source, Class<T> targetClass) {
        return WmlElementTraversal.getTargetElements(source, targetClass);
    }

    /** @deprecated use {@link WmlElementTraversal#getAllElementFromObject}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
        return WmlElementTraversal.getAllElementFromObject(obj, toSearch);
    }

    /** @deprecated use {@link WmlElementTraversal#getElementContent}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static String getElementContent(Object obj) throws Exception {
        return WmlElementTraversal.getElementContent(obj);
    }

    // ---- Table --------------------------------------------------------------

    /** @deprecated use {@link WmlTableUtils#getTable}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static Tbl getTable(List<Tbl> tables, String placeholder) throws Docx4JException {
        return WmlTableUtils.getTable(tables, placeholder);
    }

    /** @deprecated use {@link WmlTableUtils#replaceTable}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void replaceTable(String[] placeholders, List<Map<String, String>> textToAdd,
            WordprocessingMLPackage template) throws Exception {
        WmlTableUtils.replaceTable(placeholders, textToAdd, template);
    }

    /** @deprecated use {@link WmlTableUtils#addRowToTable}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRowToTable(Tbl reviewtable, Tr templateRow, Map<String, String> replacements) {
        WmlTableUtils.addRowToTable(reviewtable, templateRow, replacements);
    }

    /** @deprecated use {@link WmlTableUtils#saveWordPackage}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public void saveWordPackage(WordprocessingMLPackage wordPackage, File file) throws Exception {
        WmlTableUtils.saveWordPackage(wordPackage, file);
    }

    /** @deprecated use {@link WmlTableUtils#createHyperlink}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public void createHyperlink(WordprocessingMLPackage wordMLPackage, MainDocumentPart mainPart, ObjectFactory factory,
            P paragraph, String url, String value, String cnFontName, String enFontName, String fontSize)
            throws Exception {
        WmlTableUtils.createHyperlink(wordMLPackage, mainPart, factory, paragraph, url, value, cnFontName, enFontName, fontSize);
    }

    /** @deprecated use {@link WmlTableUtils#getTcByPosition}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public Tc getTcByPosition(List<Tc> tcList, int position) {
        return WmlTableUtils.getTcByPosition(tcList, position);
    }

    /** @deprecated use {@link WmlTableUtils#mergeCellsHorizontalByGridSpan}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void mergeCellsHorizontalByGridSpan(Tbl tbl, int row, int fromCell, int toCell) {
        WmlTableUtils.mergeCellsHorizontalByGridSpan(tbl, row, fromCell, toCell);
    }

    /** @deprecated use {@link WmlTableUtils#mergeCellsHorizontal}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void mergeCellsHorizontal(Tbl tbl, int row, int fromCell, int toCell) {
        WmlTableUtils.mergeCellsHorizontal(tbl, row, fromCell, toCell);
    }

    /** @deprecated use {@link WmlTableUtils#mergeCellsVertically}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void mergeCellsVertically(Tbl tbl, int col, int fromRow, int toRow) {
        WmlTableUtils.mergeCellsVertically(tbl, col, fromRow, toRow);
    }

    /** @deprecated use {@link WmlTableUtils#getTc}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static Tc getTc(Tbl tbl, int row, int cell) {
        return WmlTableUtils.getTc(tbl, row, cell);
    }

    /** @deprecated use {@link WmlTableUtils#getAllTbl}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static List<Tbl> getAllTbl(WordprocessingMLPackage wordMLPackage) {
        return WmlTableUtils.getAllTbl(wordMLPackage);
    }

    /** @deprecated use {@link WmlTableUtils#removeTableByIndex}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static boolean removeTableByIndex(WordprocessingMLPackage wordMLPackage, int index) throws Exception {
        return WmlTableUtils.removeTableByIndex(wordMLPackage, index);
    }

    /** @deprecated use {@link WmlTableUtils#getTblContentStr}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static String getTblContentStr(Tbl tbl) throws Exception {
        return WmlTableUtils.getTblContentStr(tbl);
    }

    /** @deprecated use {@link WmlTableUtils#getTblContentList}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static List<String> getTblContentList(Tbl tbl) throws Exception {
        return WmlTableUtils.getTblContentList(tbl);
    }

    /** @deprecated use {@link WmlTableUtils#getTblPr}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static TblPr getTblPr(Tbl tbl) {
        return WmlTableUtils.getTblPr(tbl);
    }

    /** @deprecated use {@link WmlTableUtils#setTableWidth}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setTableWidth(Tbl tbl, String width) {
        WmlTableUtils.setTableWidth(tbl, width);
    }

    /** @deprecated use {@link WmlTableUtils#createTable(WordprocessingMLPackage, int, int)}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static Tbl createTable(WordprocessingMLPackage wordPackage, int rowNum, int colsNum) throws Exception {
        return WmlTableUtils.createTable(wordPackage, rowNum, colsNum);
    }

    /** @deprecated use {@link WmlTableUtils#createTable(int, int, int[])}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static Tbl createTable(int rowNum, int colsNum, int[] widthArr) throws Exception {
        return WmlTableUtils.createTable(rowNum, colsNum, widthArr);
    }

    /** @deprecated use {@link WmlTableUtils#setTblBorders}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public void setTblBorders(TblPr tblPr, CTBorder topBorder, CTBorder rightBorder, CTBorder bottomBorder,
            CTBorder leftBorder, CTBorder hBorder, CTBorder vBorder) {
        WmlTableUtils.setTblBorders(tblPr, topBorder, rightBorder, bottomBorder, leftBorder, hBorder, vBorder);
    }

    /** @deprecated use {@link WmlTableUtils#setTblJcAlign}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public void setTblJcAlign(Tbl tbl, JcEnumeration jcType) {
        WmlTableUtils.setTblJcAlign(tbl, jcType);
    }

    /** @deprecated use {@link WmlTableUtils#setTblAllJcAlign}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public void setTblAllJcAlign(Tbl tbl, JcEnumeration jcType) {
        WmlTableUtils.setTblAllJcAlign(tbl, jcType);
    }

    /** @deprecated use {@link WmlTableUtils#setTblAllVAlign}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public void setTblAllVAlign(Tbl tbl, STVerticalJc vAlignType) {
        WmlTableUtils.setTblAllVAlign(tbl, vAlignType);
    }

    /** @deprecated use {@link WmlTableUtils#setTableCellMargin}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public void setTableCellMargin(Tbl tbl, String top, String right, String bottom, String left) {
        WmlTableUtils.setTableCellMargin(tbl, top, right, bottom, left);
    }

    /** @deprecated use {@link WmlTableUtils#getTblAllTr}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static List<Tr> getTblAllTr(Tbl tbl) {
        return WmlTableUtils.getTblAllTr(tbl);
    }

    /** @deprecated use {@link WmlTableUtils#setTrHeight}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setTrHeight(Tr tr, String heigth) {
        WmlTableUtils.setTrHeight(tr, heigth);
    }

    /** @deprecated use {@link WmlTableUtils#addTrByIndex(Tbl, int)}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addTrByIndex(Tbl tbl, int index) {
        WmlTableUtils.addTrByIndex(tbl, index);
    }

    /** @deprecated use {@link WmlTableUtils#addTrByIndex(Tbl, int, STVerticalJc, JcEnumeration)}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addTrByIndex(Tbl tbl, int index, STVerticalJc vAlign, JcEnumeration hAlign) {
        WmlTableUtils.addTrByIndex(tbl, index, vAlign, hAlign);
    }

    /** @deprecated use {@link WmlTableUtils#getTcCellSizeWithMergeNum}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static int getTcCellSizeWithMergeNum(Tr tr) {
        return WmlTableUtils.getTcCellSizeWithMergeNum(tr);
    }

    /** @deprecated use {@link WmlTableUtils#removeTrByIndex}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static boolean removeTrByIndex(Tbl tbl, int index) {
        return WmlTableUtils.removeTrByIndex(tbl, index);
    }

    /** @deprecated use {@link WmlTableUtils#getTrPr}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static TrPr getTrPr(Tr tr) {
        return WmlTableUtils.getTrPr(tr);
    }

    /** @deprecated use {@link WmlTableUtils#setTrHidden}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setTrHidden(Tr tr, boolean hidden) {
        WmlTableUtils.setTrHidden(tr, hidden);
    }

    /** @deprecated use {@link WmlTableUtils#setTcWidth}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setTcWidth(Tc tc, String width) {
        WmlTableUtils.setTcWidth(tc, width);
    }

    /** @deprecated use {@link WmlTableUtils#setTcHidden}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setTcHidden(Tc tc, boolean hidden) {
        WmlTableUtils.setTcHidden(tc, hidden);
    }

    /** @deprecated use {@link WmlTableUtils#getTcAllP}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static List<P> getTcAllP(Tc tc) {
        return WmlTableUtils.getTcAllP(tc);
    }

    /** @deprecated use {@link WmlTableUtils#getTcPr}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static org.docx4j.wml.TcPr getTcPr(Tc tc) {
        return WmlTableUtils.getTcPr(tc);
    }

    /** @deprecated use {@link WmlTableUtils#setTcVAlign}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setTcVAlign(Tc tc, STVerticalJc vAlignType) {
        WmlTableUtils.setTcVAlign(tc, vAlignType);
    }

    /** @deprecated use {@link WmlTableUtils#setTcJcAlign}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setTcJcAlign(Tc tc, JcEnumeration jcType) {
        WmlTableUtils.setTcJcAlign(tc, jcType);
    }

    /** @deprecated use {@link WmlRunStyleUtils#getRPr}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static RPr getRPr(R r) {
        return WmlRunStyleUtils.getRPr(r);
    }

    /** @deprecated use {@link WmlTableUtils#getTrAllCell}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static List<Tc> getTrAllCell(Tr tr) {
        return WmlTableUtils.getTrAllCell(tr);
    }

    /** @deprecated use {@link WmlTableUtils#getTcContent}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static String getTcContent(Tc tc) throws Exception {
        return WmlTableUtils.getTcContent(tc);
    }

    /** @deprecated use {@link WmlTableUtils#setTcContent}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public void setTcContent(Tc tc, RPr rpr, String content) {
        WmlTableUtils.setTcContent(tc, rpr, content);
    }

    /** @deprecated use {@link WmlTableUtils#removeTcContent}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public void removeTcContent(Tc tc) {
        WmlTableUtils.removeTcContent(tc);
    }

    // ---- Run style (RPr) ----------------------------------------------------

    /** @deprecated use {@link WmlRunStyleUtils#setFontStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setFontStyle(RPr runProperties, String cnFontFamily, String enFontFamily, String fontSize,
            String color) {
        WmlRunStyleUtils.setFontStyle(runProperties, cnFontFamily, enFontFamily, fontSize, color);
    }

    /** @deprecated use {@link WmlRunStyleUtils#setFontSize}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setFontSize(RPr runProperties, String fontSize) {
        WmlRunStyleUtils.setFontSize(runProperties, fontSize);
    }

    /** @deprecated use {@link WmlRunStyleUtils#setFontFamily}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setFontFamily(RPr runProperties, String cnFontFamily, String enFontFamily) {
        WmlRunStyleUtils.setFontFamily(runProperties, cnFontFamily, enFontFamily);
    }

    /** @deprecated use {@link WmlRunStyleUtils#setFontColor}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setFontColor(RPr runProperties, String color) {
        WmlRunStyleUtils.setFontColor(runProperties, color);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrBorderStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrBorderStyle(RPr runProperties, String size, STBorder bordType, String space, String color) {
        WmlRunStyleUtils.addRPrBorderStyle(runProperties, size, bordType, space, color);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrEmStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrEmStyle(RPr runProperties, STEm emType) {
        WmlRunStyleUtils.addRPrEmStyle(runProperties, emType);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrOutlineStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrOutlineStyle(RPr runProperties) {
        WmlRunStyleUtils.addRPrOutlineStyle(runProperties);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrcaleStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrcaleStyle(RPr runProperties, STVerticalAlignRun vAlign) {
        WmlRunStyleUtils.addRPrcaleStyle(runProperties, vAlign);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrScaleStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrScaleStyle(RPr runProperties, int indent) {
        WmlRunStyleUtils.addRPrScaleStyle(runProperties, indent);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrtSpacingStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrtSpacingStyle(RPr runProperties, int spacing) {
        WmlRunStyleUtils.addRPrtSpacingStyle(runProperties, spacing);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrtPositionStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrtPositionStyle(RPr runProperties, int position) {
        WmlRunStyleUtils.addRPrtPositionStyle(runProperties, position);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrImprintStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrImprintStyle(RPr runProperties) {
        WmlRunStyleUtils.addRPrImprintStyle(runProperties);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrEmbossStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrEmbossStyle(RPr runProperties) {
        WmlRunStyleUtils.addRPrEmbossStyle(runProperties);
    }

    /** @deprecated use {@link WmlRunStyleUtils#setRPrVanishStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setRPrVanishStyle(RPr runProperties, boolean isVanish) {
        WmlRunStyleUtils.setRPrVanishStyle(runProperties, isVanish);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrShadowStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrShadowStyle(RPr runProperties) {
        WmlRunStyleUtils.addRPrShadowStyle(runProperties);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrShdStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrShdStyle(RPr runProperties, STShd shdtype) {
        WmlRunStyleUtils.addRPrShdStyle(runProperties, shdtype);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrHightLightStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrHightLightStyle(RPr runProperties, String hightlight) {
        WmlRunStyleUtils.addRPrHightLightStyle(runProperties, hightlight);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrStrikeStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrStrikeStyle(RPr runProperties, boolean isStrike, boolean isDStrike) {
        WmlRunStyleUtils.addRPrStrikeStyle(runProperties, isStrike, isDStrike);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrBoldStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrBoldStyle(RPr runProperties) {
        WmlRunStyleUtils.addRPrBoldStyle(runProperties);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrItalicStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrItalicStyle(RPr runProperties) {
        WmlRunStyleUtils.addRPrItalicStyle(runProperties);
    }

    /** @deprecated use {@link WmlRunStyleUtils#addRPrUnderlineStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addRPrUnderlineStyle(RPr runProperties, UnderlineEnumeration enumType) {
        WmlRunStyleUtils.addRPrUnderlineStyle(runProperties, enumType);
    }

    // ---- Paragraph ----------------------------------------------------------

    /** @deprecated use {@link WmlParagraphUtils#addInlineImageToParagraph}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static P addInlineImageToParagraph(org.docx4j.dml.wordprocessingDrawing.Inline inline) {
        return WmlParagraphUtils.addInlineImageToParagraph(inline);
    }

    /** @deprecated use {@link WmlParagraphUtils#removeParaByIndex}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public boolean removeParaByIndex(WordprocessingMLPackage wordMLPackage, int index) {
        return WmlParagraphUtils.removeParaByIndex(wordMLPackage, index);
    }

    /** @deprecated use {@link WmlParagraphUtils#setParaJcAlign}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setParaJcAlign(P paragraph, JcEnumeration hAlign) {
        WmlParagraphUtils.setParaJcAlign(paragraph, hAlign);
    }

    /** @deprecated use {@link WmlParagraphUtils#setParaRContent}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setParaRContent(P p, RPr runProperties, String content) {
        WmlParagraphUtils.setParaRContent(p, runProperties, content);
    }

    /** @deprecated use {@link WmlParagraphUtils#appendParaRContent}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void appendParaRContent(P p, RPr runProperties, String content) {
        WmlParagraphUtils.appendParaRContent(p, runProperties, content);
    }

    /** @deprecated use {@link WmlParagraphUtils#addImageToPara}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addImageToPara(WordprocessingMLPackage wordMLPackage, ObjectFactory factory, P paragraph,
            String filePath, String content, RPr rpr, String altText, int id1, int id2) throws Exception {
        WmlParagraphUtils.addImageToPara(wordMLPackage, factory, paragraph, filePath, content, rpr, altText, id1, id2);
    }

    /** @deprecated use {@link WmlParagraphUtils#addPageBreak}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addPageBreak(P para, STBrType sTBrType) {
        WmlParagraphUtils.addPageBreak(para, sTBrType);
    }

    /** @deprecated use {@link WmlParagraphUtils#setParagraphSuppressLineNum}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setParagraphSuppressLineNum(P p) {
        WmlParagraphUtils.setParagraphSuppressLineNum(p);
    }

    /** @deprecated use {@link WmlParagraphUtils#setParagraphShdStyle}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setParagraphShdStyle(P p, STShd shdType, String shdColor) {
        WmlParagraphUtils.setParagraphShdStyle(p, shdType, shdColor);
    }

    /** @deprecated use {@link WmlParagraphUtils#setParagraphSpacing}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public void setParagraphSpacing(P p, boolean isSpace, String before, String after, String beforeLines,
            String afterLines, boolean isLine, String lineValue, STLineSpacingRule sTLineSpacingRule) {
        WmlParagraphUtils.setParagraphSpacing(p, isSpace, before, after, beforeLines, afterLines, isLine, lineValue, sTLineSpacingRule);
    }

    /** @deprecated use {@link WmlParagraphUtils#setParagraphIndInfo}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public void setParagraphIndInfo(P p, String firstLine, String firstLineChar, String hanging, String hangingChar,
            String right, String rigthChar, String left, String leftChar) {
        WmlParagraphUtils.setParagraphIndInfo(p, firstLine, firstLineChar, hanging, hangingChar, right, rigthChar, left, leftChar);
    }

    /** @deprecated use {@link WmlParagraphUtils#getPPr}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static PPr getPPr(P p) {
        return WmlParagraphUtils.getPPr(p);
    }

    /** @deprecated use {@link WmlParagraphUtils#getParaRPr}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static org.docx4j.wml.ParaRPr getParaRPr(PPr ppr) {
        return WmlParagraphUtils.getParaRPr(ppr);
    }

    /** @deprecated use {@link WmlParagraphUtils#setParaVanish}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setParaVanish(PPr ppr, boolean isVanish) {
        WmlParagraphUtils.setParaVanish(ppr, isVanish);
    }

    /** @deprecated use {@link WmlParagraphUtils#setParagraghBorders}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setParagraghBorders(P p, CTBorder topBorder, CTBorder bottomBorder, CTBorder leftBorder,
            CTBorder rightBorder) {
        WmlParagraphUtils.setParagraghBorders(p, topBorder, bottomBorder, leftBorder, rightBorder);
    }

    // ---- Section ------------------------------------------------------------

    /** @deprecated use {@link WmlSectionUtils#createFooter}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static Ftr createFooter(String content) {
        return WmlSectionUtils.createFooter(content);
    }

    /** @deprecated use {@link WmlSectionUtils#getDocSectPr}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static SectPr getDocSectPr(WordprocessingMLPackage wordPackage) {
        return WmlSectionUtils.getDocSectPr(wordPackage);
    }

    /** @deprecated use {@link WmlSectionUtils#setDocSectionBreak}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setDocSectionBreak(WordprocessingMLPackage wordPackage, String sectValType) {
        WmlSectionUtils.setDocSectionBreak(wordPackage, sectValType);
    }

    /** @deprecated use {@link WmlSectionUtils#setDocMarginSpace}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setDocMarginSpace(WordprocessingMLPackage wordPackage, ObjectFactory factory, String top,
            String left, String bottom, String right) {
        WmlSectionUtils.setDocMarginSpace(wordPackage, factory, top, left, bottom, right);
    }

    /** @deprecated use {@link WmlSectionUtils#setDocumentSize}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setDocumentSize(WordprocessingMLPackage wordPackage, ObjectFactory factory, String width,
            String height, STPageOrientation stValue) {
        WmlSectionUtils.setDocumentSize(wordPackage, factory, width, height, stValue);
    }

    /** @deprecated use {@link WmlSectionUtils#getWritableWidth}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static int getWritableWidth(WordprocessingMLPackage wordPackage) throws Exception {
        return WmlSectionUtils.getWritableWidth(wordPackage);
    }

    // ---- Document -----------------------------------------------------------

    /** @deprecated use {@link WmlDocumentUtils#addImage}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void addImage(WordprocessingMLPackage wPackage, org.docx4j.wml.CTBookmark bm, String file) throws Exception {
        WmlDocumentUtils.addImage(wPackage, bm, file);
    }

    /** @deprecated use {@link WmlDocumentUtils#createWordprocessingMLPackage}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static WordprocessingMLPackage createWordprocessingMLPackage() throws Exception {
        return WmlDocumentUtils.createWordprocessingMLPackage();
    }

    /** @deprecated use {@link WmlDocumentUtils#loadWordprocessingMLPackageWithPwd}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static WordprocessingMLPackage loadWordprocessingMLPackageWithPwd(String filePath, String password)
            throws Exception {
        return WmlDocumentUtils.loadWordprocessingMLPackageWithPwd(filePath, password);
    }

    /** @deprecated use {@link WmlDocumentUtils#loadWordprocessingMLPackage}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static WordprocessingMLPackage loadWordprocessingMLPackage(String filePath) throws Exception {
        return WmlDocumentUtils.loadWordprocessingMLPackage(filePath);
    }

    /** @deprecated use {@link WmlDocumentUtils#setDocumentBackGround}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setDocumentBackGround(WordprocessingMLPackage wordPackage, ObjectFactory factory, String color)
            throws Exception {
        WmlDocumentUtils.setDocumentBackGround(wordPackage, factory, color);
    }

    /** @deprecated use {@link WmlDocumentUtils#setDocumentBorders}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setDocumentBorders(WordprocessingMLPackage wordPackage, ObjectFactory factory, CTBorder top,
            CTBorder right, CTBorder bottom, CTBorder left) {
        WmlDocumentUtils.setDocumentBorders(wordPackage, factory, top, right, bottom, left);
    }

    /** @deprecated use {@link WmlDocumentUtils#setDocInNumType}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setDocInNumType(WordprocessingMLPackage wordPackage, String countBy, String distance, String start,
            STLineNumberRestart restartType) {
        WmlDocumentUtils.setDocInNumType(wordPackage, countBy, distance, start, restartType);
    }

    /** @deprecated use {@link WmlDocumentUtils#setDocTextDirection}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setDocTextDirection(WordprocessingMLPackage wordPackage, String textDirection) {
        WmlDocumentUtils.setDocTextDirection(wordPackage, textDirection);
    }

    /** @deprecated use {@link WmlDocumentUtils#setDocVAlign}. */
    @Deprecated(since = "3.0.x", forRemoval = true)
    public static void setDocVAlign(WordprocessingMLPackage wordPackage, STVerticalJc valignType) {
        WmlDocumentUtils.setDocVAlign(wordPackage, valignType);
    }
}
