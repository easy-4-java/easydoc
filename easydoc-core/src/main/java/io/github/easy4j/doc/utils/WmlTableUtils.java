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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.model.properties.table.tr.TrHeight;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTHeight;
import org.docx4j.wml.CTTblCellMar;
import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblGrid;
import org.docx4j.wml.TblGridCol;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.TcPrInner.GridSpan;
import org.docx4j.wml.TcPrInner.HMerge;
import org.docx4j.wml.TcPrInner.VMerge;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.docx4j.wml.TrPr;
import org.docx4j.wml.STVerticalJc;

/**
 * Table helpers ({@code Tbl}, {@code Tr}, {@code Tc}) for docx4j.
 * <p>
 * Extracted from {@code WmlElementUtils} (chunk-6 refactor).
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public final class WmlTableUtils {

    private static final ObjectFactory FACTORY = Context.getWmlObjectFactory();

    private WmlTableUtils() {
    }

    /*
     * 该方法找到表格，获取第一行并且遍历提供的map向表格添加新行，在将其返回之前删除模版行。这个方法用到了两个助手方法：addRowToTable 和 getTemplateTable。我们首先看一下后面的那个：
     */
    public static Tbl getTable(List<Tbl> tables, String placeholder) throws Docx4JException {
        for (Iterator<Tbl> iterator = tables.iterator(); iterator.hasNext();) {
            Tbl tbl = iterator.next();
            //查找当前table下面的text对象
            List<Text> textElements = WmlElementTraversal.getTargetElements(tbl, Text.class);
            for (Text text : textElements) {
                Text textElement = (Text) text;
                //
                if (textElement.getValue() != null && textElement.getValue().equals(placeholder))  {
                    return (Tbl) tbl;
                }
            }
        }
        return null;
    }

    public static void replaceTable(String[] placeholders, List<Map<String, String>> textToAdd,
            WordprocessingMLPackage template) throws Docx4JException, Exception {
        List<Tbl> tables = WmlElementTraversal.getTargetElements(template.getMainDocumentPart(), Tbl.class);

        // 1. find the table
        Tbl tempTable = getTable(tables, placeholders[0]);
        List<Tr> rows = WmlElementTraversal.getTargetElements(tempTable, Tr.class);

        // first row is header, second row is content
        if (rows.size() == 2) {

            // this is our template row
            Tr templateRow = (Tr) rows.get(1);

            for (Map<String, String> replacements : textToAdd) {
                // 2 and 3 are done in this method
                addRowToTable(tempTable, templateRow, replacements);
            }

            // 4. remove the template row
            tempTable.getContent().remove(templateRow);
        }
    }

    public static void addRowToTable(Tbl reviewtable, Tr templateRow, Map<String, String> replacements) {
        Tr workingRow = (Tr) XmlUtils.deepCopy(templateRow);
        List<?> textElements = WmlElementTraversal.getTargetElements(workingRow, Text.class);
        for (Object object : textElements) {
            Text text = (Text) object;
            String replacementValue = (String) replacements.get(text.getValue());
            if (replacementValue != null)
                text.setValue(replacementValue);
        }

        reviewtable.getContent().add(workingRow);
    }

    /**
     * @Description:保存WordprocessingMLPackage
     */
    public static void saveWordPackage(WordprocessingMLPackage wordPackage, File file) throws Exception {
        wordPackage.save(file);
    }

    /**
     * @Description: 新增超链接
     */
    public static void createHyperlink(WordprocessingMLPackage wordMLPackage, MainDocumentPart mainPart, ObjectFactory factory,
            P paragraph, String url, String value, String cnFontName, String enFontName, String fontSize)
            throws Exception {
        if (StringUtils.isBlank(enFontName)) {
            enFontName = "Times New Roman";
        }
        if (StringUtils.isBlank(cnFontName)) {
            cnFontName = "微软雅黑";
        }
        if (StringUtils.isBlank(fontSize)) {
            fontSize = "22";
        }
        org.docx4j.relationships.ObjectFactory reFactory = new org.docx4j.relationships.ObjectFactory();
        org.docx4j.relationships.Relationship rel = reFactory.createRelationship();
        rel.setType(Namespaces.HYPERLINK);
        rel.setTarget(url);
        rel.setTargetMode("External");
        mainPart.getRelationshipsPart().addRelationship(rel);
        StringBuilder sb = new StringBuilder();
        // addRelationship sets the rel's @Id
        // 安全：所有用户可控值先做 XML 转义，防止属性/文本注入（H-2）
        sb.append("<w:hyperlink r:id=\"");
        sb.append(escapeXml(rel.getId()));
        sb.append("\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" ");
        sb.append("xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" >");
        sb.append("<w:r><w:rPr><w:rStyle w:val=\"Hyperlink\" />");
        sb.append("<w:rFonts w:ascii=\"");
        sb.append(escapeXml(enFontName));
        sb.append("\" w:hAnsi=\"");
        sb.append(escapeXml(enFontName));
        sb.append("\" w:eastAsia=\"");
        sb.append(escapeXml(cnFontName));
        sb.append("\" w:hint=\"eastAsia\"/>");
        sb.append("<w:sz w:val=\"");
        sb.append(escapeXml(fontSize));
        sb.append("\"/><w:szCs w:val=\"");
        sb.append(escapeXml(fontSize));
        sb.append("\"/></w:rPr><w:t>");
        sb.append(escapeXml(value));
        sb.append("</w:t></w:r></w:hyperlink>");

        P.Hyperlink link = (P.Hyperlink) XmlUtils.unmarshalString(sb.toString());
        paragraph.getContent().add(link);
    }

    private static String escapeXml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    /*
     * http://53873039oycg.iteye.com/blog/2194849
     *  实现思路:
        主要分在当前行上方插入行和在当前行下方插入行。对首尾2行特殊处理，在有跨行合并情况时，在第一行上面或者在最后一行下面插入是不会跨行的但是可能会跨列。
       对于中间的行，主要参照当前行，如果当前行跨行，则新增行也跨行，如果当前行单元格结束跨行，则新增的上方插入行跨行，下方插入行不跨行，如果当前行单元格开始跨行，则新增的上方插入行不跨行，下发插入行跨行。
       主要思路就是这样，插入的时候需要得到真实位置的单元格，代码如下:

     */
    // 按位置得到单元格(考虑跨列合并情况)
    public static Tc getTcByPosition(List<Tc> tcList, int position) {
        int k = 0;
        for (int i = 0, len = tcList.size(); i < len; i++) {
            Tc tc = tcList.get(i);
            TcPr trPr = tc.getTcPr();
            if (trPr != null) {
                GridSpan gridSpan = trPr.getGridSpan();
                if (gridSpan != null) {
                    k += gridSpan.getVal().intValue() - 1;
                }
            }
            if (k >= position) {
                return tcList.get(i);
            }
            k++;
        }
        if (position < tcList.size()) {
            return tcList.get(position);
        }
        return null;
    }

    /**
     * @Description: 跨列合并
     */
    public static void mergeCellsHorizontalByGridSpan(Tbl tbl, int row, int fromCell, int toCell) {
        if (row < 0 || fromCell < 0 || toCell < 0) {
            return;
        }
        List<Tr> trList = getTblAllTr(tbl);
        if (row > trList.size()) {
            return;
        }
        Tr tr = trList.get(row);
        List<Tc> tcList = getTrAllCell(tr);
        for (int cellIndex = Math.min(tcList.size() - 1, toCell); cellIndex >= fromCell; cellIndex--) {
            Tc tc = tcList.get(cellIndex);
            TcPr tcPr = getTcPr(tc);
            if (cellIndex == fromCell) {
                GridSpan gridSpan = tcPr.getGridSpan();
                if (gridSpan == null) {
                    gridSpan = new GridSpan();
                    tcPr.setGridSpan(gridSpan);
                }
                gridSpan.setVal(BigInteger.valueOf(Math.min(tcList.size() - 1, toCell) - fromCell + 1));
            } else {
                tr.getContent().remove(cellIndex);
            }
        }
    }

    /**
     * @Description: 跨列合并
     */
    public static void mergeCellsHorizontal(Tbl tbl, int row, int fromCell, int toCell) {
        if (row < 0 || fromCell < 0 || toCell < 0) {
            return;
        }
        List<Tr> trList = getTblAllTr(tbl);
        if (row > trList.size()) {
            return;
        }
        Tr tr = trList.get(row);
        List<Tc> tcList = getTrAllCell(tr);
        for (int cellIndex = fromCell, len = Math.min(tcList.size() - 1, toCell); cellIndex <= len; cellIndex++) {
            Tc tc = tcList.get(cellIndex);
            TcPr tcPr = getTcPr(tc);
            HMerge hMerge = tcPr.getHMerge();
            if (hMerge == null) {
                hMerge = new HMerge();
                tcPr.setHMerge(hMerge);
            }
            if (cellIndex == fromCell) {
                hMerge.setVal("restart");
            } else {
                hMerge.setVal("continue");
            }
        }
    }

    /**
     * @Description: 跨行合并
     */
    public static void mergeCellsVertically(Tbl tbl, int col, int fromRow, int toRow) {
        if (col < 0 || fromRow < 0 || toRow < 0) {
            return;
        }
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            Tc tc = getTc(tbl, rowIndex, col);
            if (tc == null) {
                break;
            }
            TcPr tcPr = getTcPr(tc);
            VMerge vMerge = tcPr.getVMerge();
            if (vMerge == null) {
                vMerge = new VMerge();
                tcPr.setVMerge(vMerge);
            }
            if (rowIndex == fromRow) {
                vMerge.setVal("restart");
            } else {
                vMerge.setVal("continue");
            }
        }
    }

    /**
     * @Description:得到指定位置的单元格
     */
    public static Tc getTc(Tbl tbl, int row, int cell) {
        if (row < 0 || cell < 0) {
            return null;
        }
        List<Tr> trList = getTblAllTr(tbl);
        if (row >= trList.size()) {
            return null;
        }
        List<Tc> tcList = getTrAllCell(trList.get(row));
        if (cell >= tcList.size()) {
            return null;
        }
        return tcList.get(cell);
    }

    /**
     * @Description:得到所有表格
     */
    public static List<Tbl> getAllTbl(WordprocessingMLPackage wordMLPackage) {
        MainDocumentPart mainDocPart = wordMLPackage.getMainDocumentPart();
        List<Object> objList = WmlElementTraversal.getAllElementFromObject(mainDocPart, Tbl.class);
        if (objList == null) {
            return null;
        }
        List<Tbl> tblList = new ArrayList<Tbl>();
        for (Object obj : objList) {
            if (obj instanceof Tbl) {
                Tbl tbl = (Tbl) obj;
                tblList.add(tbl);
            }
        }
        return tblList;
    }

    /**
     * @Description:删除指定位置的表格,删除后表格数量减一
     */
    public static boolean removeTableByIndex(WordprocessingMLPackage wordMLPackage, int index) throws Exception {
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
            Object obj = XmlUtils.unwrap(objList.get(i));
            if (obj instanceof Tbl) {
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
     * @Description: 获取单元格内容,无分割符
     */
    public static String getTblContentStr(Tbl tbl) throws Exception {
        return WmlElementTraversal.getElementContent(tbl);
    }

    /**
     * @Description: 获取表格内容
     */
    public static List<String> getTblContentList(Tbl tbl) throws Exception {
        List<String> resultList = new ArrayList<String>();
        List<Tr> trList = getTblAllTr(tbl);
        for (Tr tr : trList) {
            StringBuilder sb = new StringBuilder();
            List<Tc> tcList = getTrAllCell(tr);
            for (Tc tc : tcList) {
                sb.append(WmlElementTraversal.getElementContent(tc)).append(",");
            }
            resultList.add(sb.toString());
        }
        return resultList;
    }

    public static TblPr getTblPr(Tbl tbl) {
        TblPr tblPr = tbl.getTblPr();
        if (tblPr == null) {
            tblPr = new TblPr();
            tbl.setTblPr(tblPr);
        }
        return tblPr;
    }

    /**
     * @Description: 设置表格总宽度
     */
    public static void setTableWidth(Tbl tbl, String width) {
        if (StringUtils.isNotBlank(width)) {
            TblPr tblPr = getTblPr(tbl);
            TblWidth tblW = tblPr.getTblW();
            if (tblW == null) {
                tblW = new TblWidth();
                tblPr.setTblW(tblW);
            }
            tblW.setW(new BigInteger(width));
            tblW.setType("dxa");
        }
    }

    /**
     * @Description:创建表格(默认水平居中,垂直居中)
     */
    public static Tbl createTable(WordprocessingMLPackage wordPackage, int rowNum, int colsNum) throws Exception {
        colsNum = Math.max(1, colsNum);
        rowNum = Math.max(1, rowNum);
        int widthTwips = WmlSectionUtils.getWritableWidth(wordPackage);
        int colWidth = widthTwips / colsNum;
        int[] widthArr = new int[colsNum];
        for (int i = 0; i < colsNum; i++) {
            widthArr[i] = colWidth;
        }
        return createTable(rowNum, colsNum, widthArr);
    }

    /**
     * @Description:创建表格(默认水平居中,垂直居中)
     */
    public static Tbl createTable(int rowNum, int colsNum, int[] widthArr) throws Exception {
        colsNum = Math.max(1, Math.min(colsNum, widthArr.length));
        rowNum = Math.max(1, rowNum);
        Tbl tbl = new Tbl();
        StringBuilder tblSb = new StringBuilder();
        tblSb.append("<w:tblPr ").append(Namespaces.W_NAMESPACE_DECLARATION).append(">");
        tblSb.append("<w:tblStyle w:val=\"TableGrid\"/>");
        tblSb.append("<w:tblW w:w=\"0\" w:type=\"auto\"/>");
        // 上边框
        tblSb.append("<w:tblBorders>");
        tblSb.append("<w:top w:val=\"single\" w:sz=\"1\" w:space=\"0\" w:color=\"auto\"/>");
        // 左边框
        tblSb.append("<w:left w:val=\"single\" w:sz=\"1\" w:space=\"0\" w:color=\"auto\"/>");
        // 下边框
        tblSb.append("<w:bottom w:val=\"single\" w:sz=\"1\" w:space=\"0\" w:color=\"auto\"/>");
        // 右边框
        tblSb.append("<w:right w:val=\"single\" w:sz=\"1\" w:space=\"0\" w:color=\"auto\"/>");
        tblSb.append("<w:insideH w:val=\"single\" w:sz=\"1\" w:space=\"0\" w:color=\"auto\"/>");
        tblSb.append("<w:insideV w:val=\"single\" w:sz=\"1\" w:space=\"0\" w:color=\"auto\"/>");
        tblSb.append("</w:tblBorders>");
        tblSb.append("</w:tblPr>");
        TblPr tblPr = null;
        tblPr = (TblPr) XmlUtils.unmarshalString(tblSb.toString());
        Jc jc = new Jc();
        // 单元格居中对齐
        jc.setVal(JcEnumeration.CENTER);
        tblPr.setJc(jc);

        tbl.setTblPr(tblPr);

        // 设定各单元格宽度
        TblGrid tblGrid = new TblGrid();
        tbl.setTblGrid(tblGrid);
        for (int i = 0; i < colsNum; i++) {
            TblGridCol gridCol = new TblGridCol();
            gridCol.setW(BigInteger.valueOf(widthArr[i]));
            tblGrid.getGridCol().add(gridCol);
        }
        // 新增行
        for (int j = 0; j < rowNum; j++) {
            Tr tr = new Tr();
            tbl.getContent().add(tr);
            // 列
            for (int i = 0; i < colsNum; i++) {
                Tc tc = new Tc();
                tr.getContent().add(tc);

                TcPr tcPr = new TcPr();
                TblWidth cellWidth = new TblWidth();
                cellWidth.setType("dxa");
                cellWidth.setW(BigInteger.valueOf(widthArr[i]));
                tcPr.setTcW(cellWidth);
                tc.setTcPr(tcPr);

                // 垂直居中
                setTcVAlign(tc, STVerticalJc.CENTER);
                P p = new P();
                PPr pPr = new PPr();
                pPr.setJc(jc);
                p.setPPr(pPr);
                R run = new R();
                p.getContent().add(run);
                tc.getContent().add(p);
            }
        }
        return tbl;
    }

    /**
     * @Description:表格增加边框 可以设置上下左右四个边框样式以及横竖水平线样式
     */
    public static void setTblBorders(TblPr tblPr, CTBorder topBorder, CTBorder rightBorder, CTBorder bottomBorder,
            CTBorder leftBorder, CTBorder hBorder, CTBorder vBorder) {
        TblBorders borders = tblPr.getTblBorders();
        if (borders == null) {
            borders = new TblBorders();
            tblPr.setTblBorders(borders);
        }
        if (topBorder != null) {
            borders.setTop(topBorder);
        }
        if (rightBorder != null) {
            borders.setRight(rightBorder);
        }
        if (bottomBorder != null) {
            borders.setBottom(bottomBorder);
        }
        if (leftBorder != null) {
            borders.setLeft(leftBorder);
        }
        if (hBorder != null) {
            borders.setInsideH(hBorder);
        }
        if (vBorder != null) {
            borders.setInsideV(vBorder);
        }
    }

    /**
     * @Description: 设置表格水平对齐方式(仅对表格起作用,单元格不一定水平对齐)
     */
    public static void setTblJcAlign(Tbl tbl, JcEnumeration jcType) {
        if (jcType != null) {
            TblPr tblPr = getTblPr(tbl);
            Jc jc = tblPr.getJc();
            if (jc == null) {
                jc = new Jc();
                tblPr.setJc(jc);
            }
            jc.setVal(jcType);
        }
    }

    /**
     * @Description: 设置表格水平对齐方式(包括单元格),只对该方法前面产生的单元格起作用
     */
    public static void setTblAllJcAlign(Tbl tbl, JcEnumeration jcType) {
        if (jcType != null) {
            setTblJcAlign(tbl, jcType);
            List<Tr> trList = getTblAllTr(tbl);
            for (Tr tr : trList) {
                List<Tc> tcList = getTrAllCell(tr);
                for (Tc tc : tcList) {
                    setTcJcAlign(tc, jcType);
                }
            }
        }
    }

    /**
     * @Description: 设置表格垂直对齐方式(包括单元格),只对该方法前面产生的单元格起作用
     */
    public static void setTblAllVAlign(Tbl tbl, STVerticalJc vAlignType) {
        if (vAlignType != null) {
            List<Tr> trList = getTblAllTr(tbl);
            for (Tr tr : trList) {
                List<Tc> tcList = getTrAllCell(tr);
                for (Tc tc : tcList) {
                    setTcVAlign(tc, vAlignType);
                }
            }
        }
    }

    /**
     * @Description: 设置单元格Margin
     */
    public static void setTableCellMargin(Tbl tbl, String top, String right, String bottom, String left) {
        TblPr tblPr = getTblPr(tbl);
        CTTblCellMar cellMar = tblPr.getTblCellMar();
        if (cellMar == null) {
            cellMar = new CTTblCellMar();
            tblPr.setTblCellMar(cellMar);
        }
        if (StringUtils.isNotBlank(top)) {
            TblWidth topW = new TblWidth();
            topW.setW(new BigInteger(top));
            topW.setType("dxa");
            cellMar.setTop(topW);
        }
        if (StringUtils.isNotBlank(right)) {
            TblWidth rightW = new TblWidth();
            rightW.setW(new BigInteger(right));
            rightW.setType("dxa");
            cellMar.setRight(rightW);
        }
        if (StringUtils.isNotBlank(bottom)) {
            TblWidth btW = new TblWidth();
            btW.setW(new BigInteger(bottom));
            btW.setType("dxa");
            cellMar.setBottom(btW);
        }
        if (StringUtils.isNotBlank(left)) {
            TblWidth leftW = new TblWidth();
            leftW.setW(new BigInteger(left));
            leftW.setType("dxa");
            cellMar.setLeft(leftW);
        }
    }

    /**
     * @Description: 得到表格所有的行
     */
    public static List<Tr> getTblAllTr(Tbl tbl) {
        List<Object> objList = WmlElementTraversal.getAllElementFromObject(tbl, Tr.class);
        List<Tr> trList = new ArrayList<Tr>();
        if (objList == null) {
            return trList;
        }
        for (Object obj : objList) {
            if (obj instanceof Tr) {
                Tr tr = (Tr) obj;
                trList.add(tr);
            }
        }
        return trList;

    }

    /**
     * @Description:设置tr高度
     */
    public static void setTrHeight(Tr tr, String heigth) {
        TrPr trPr = getTrPr(tr);
        CTHeight ctHeight = new CTHeight();
        ctHeight.setVal(new BigInteger(heigth));
        TrHeight trHeight = new TrHeight(ctHeight);
        trHeight.set(trPr);
    }

    /**
     * @Description: 在表格指定位置新增一行,默认居中
     */
    public static void addTrByIndex(Tbl tbl, int index) {
        addTrByIndex(tbl, index, STVerticalJc.CENTER, JcEnumeration.CENTER);
    }

    /**
     * @Description: 在表格指定位置新增一行(默认按表格定义的列数添加)
     */
    public static void addTrByIndex(Tbl tbl, int index, STVerticalJc vAlign, JcEnumeration hAlign) {
        TblGrid tblGrid = tbl.getTblGrid();
        Tr tr = new Tr();
        if (tblGrid != null) {
            List<TblGridCol> gridList = tblGrid.getGridCol();
            for (TblGridCol tblGridCol : gridList) {
                Tc tc = new Tc();
                setTcWidth(tc, tblGridCol.getW().toString());
                if (vAlign != null) {
                    // 垂直居中
                    setTcVAlign(tc, vAlign);
                }
                P p = new P();
                if (hAlign != null) {
                    PPr pPr = new PPr();
                    Jc jc = new Jc();
                    // 单元格居中对齐
                    jc.setVal(hAlign);
                    pPr.setJc(jc);
                    p.setPPr(pPr);
                }
                R run = new R();
                p.getContent().add(run);
                tc.getContent().add(p);
                tr.getContent().add(tc);
            }
        } else {
            // 大部分情况都不会走到这一步
            Tr firstTr = getTblAllTr(tbl).get(0);
            int cellSize = getTcCellSizeWithMergeNum(firstTr);
            for (int i = 0; i < cellSize; i++) {
                Tc tc = new Tc();
                if (vAlign != null) {
                    // 垂直居中
                    setTcVAlign(tc, vAlign);
                }
                P p = new P();
                if (hAlign != null) {
                    PPr pPr = new PPr();
                    Jc jc = new Jc();
                    // 单元格居中对齐
                    jc.setVal(hAlign);
                    pPr.setJc(jc);
                    p.setPPr(pPr);
                }
                R run = new R();
                p.getContent().add(run);
                tc.getContent().add(p);
                tr.getContent().add(tc);
            }
        }
        if (index >= 0 && index < tbl.getContent().size()) {
            tbl.getContent().add(index, tr);
        } else {
            tbl.getContent().add(tr);
        }
    }

    /**
     * @Description: 得到行的列数
     */
    public static int getTcCellSizeWithMergeNum(Tr tr) {
        int cellSize = 1;
        List<Tc> tcList = getTrAllCell(tr);
        if (tcList == null || tcList.size() == 0) {
            return cellSize;
        }
        cellSize = tcList.size();
        for (Tc tc : tcList) {
            TcPr tcPr = getTcPr(tc);
            GridSpan gridSpan = tcPr.getGridSpan();
            if (gridSpan != null) {
                cellSize += gridSpan.getVal().intValue() - 1;
            }
        }
        return cellSize;
    }

    /**
     * @Description: 删除指定行 删除后行数减一
     */
    public static boolean removeTrByIndex(Tbl tbl, int index) {
        boolean flag = false;
        if (index < 0) {
            return flag;
        }
        List<Object> objList = tbl.getContent();
        if (objList == null) {
            return flag;
        }
        int k = -1;
        for (int i = 0, len = objList.size(); i < len; i++) {
            Object obj = XmlUtils.unwrap(objList.get(i));
            if (obj instanceof Tr) {
                k++;
                if (k == index) {
                    tbl.getContent().remove(i);
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    public static TrPr getTrPr(Tr tr) {
        TrPr trPr = tr.getTrPr();
        if (trPr == null) {
            trPr = new TrPr();
            tr.setTrPr(trPr);
        }
        return trPr;
    }

    /**
     * @Description:隐藏行(只对表格中间的部分起作用,不包括首尾行)
     */
    public static void setTrHidden(Tr tr, boolean hidden) {
        List<Tc> tcList = getTrAllCell(tr);
        for (Tc tc : tcList) {
            setTcHidden(tc, hidden);
        }
    }

    /**
     * @Description: 设置单元格宽度
     */
    public static void setTcWidth(Tc tc, String width) {
        if (StringUtils.isNotBlank(width)) {
            TcPr tcPr = getTcPr(tc);
            TblWidth tcW = tcPr.getTcW();
            if (tcW == null) {
                tcW = new TblWidth();
                tcPr.setTcW(tcW);
            }
            tcW.setW(new BigInteger(width));
            tcW.setType("dxa");
        }
    }

    /**
     * @Description: 隐藏单元格内容
     */
    public static void setTcHidden(Tc tc, boolean hidden) {
        List<P> pList = getTcAllP(tc);
        for (P p : pList) {
            PPr ppr = WmlParagraphUtils.getPPr(p);
            List<Object> objRList = WmlElementTraversal.getAllElementFromObject(p, R.class);
            if (objRList == null) {
                continue;
            }
            for (Object objR : objRList) {
                if (objR instanceof R) {
                    R r = (R) objR;
                    RPr rpr = WmlRunStyleUtils.getRPr(r);
                    WmlRunStyleUtils.setRPrVanishStyle(rpr, hidden);
                }
            }
            WmlParagraphUtils.setParaVanish(ppr, hidden);
        }
    }

    public static List<P> getTcAllP(Tc tc) {
        List<Object> objList = WmlElementTraversal.getAllElementFromObject(tc, P.class);
        List<P> pList = new ArrayList<P>();
        if (objList == null) {
            return pList;
        }
        for (Object obj : objList) {
            if (obj instanceof P) {
                P p = (P) obj;
                pList.add(p);
            }
        }
        return pList;
    }

    public static TcPr getTcPr(Tc tc) {
        TcPr tcPr = tc.getTcPr();
        if (tcPr == null) {
            tcPr = new TcPr();
            tc.setTcPr(tcPr);
        }
        return tcPr;
    }

    /**
     * @Description: 设置单元格垂直对齐方式
     */
    public static void setTcVAlign(Tc tc, STVerticalJc vAlignType) {
        if (vAlignType != null) {
            TcPr tcPr = getTcPr(tc);
            CTVerticalJc vAlign = new CTVerticalJc();
            vAlign.setVal(vAlignType);
            tcPr.setVAlign(vAlign);
        }
    }

    /**
     * @Description: 设置单元格水平对齐方式
     */
    public static void setTcJcAlign(Tc tc, JcEnumeration jcType) {
        if (jcType != null) {
            List<P> pList = getTcAllP(tc);
            for (P p : pList) {
                WmlParagraphUtils.setParaJcAlign(p, jcType);
            }
        }
    }

    /**
     * @Description: 获取所有的单元格
     */
    public static List<Tc> getTrAllCell(Tr tr) {
        List<Object> objList = WmlElementTraversal.getAllElementFromObject(tr, Tc.class);
        List<Tc> tcList = new ArrayList<Tc>();
        if (objList == null) {
            return tcList;
        }
        for (Object tcObj : objList) {
            if (tcObj instanceof Tc) {
                Tc objTc = (Tc) tcObj;
                tcList.add(objTc);
            }
        }
        return tcList;
    }

    /**
     * @Description: 获取单元格内容
     */
    public static String getTcContent(Tc tc) throws Exception {
        return WmlElementTraversal.getElementContent(tc);
    }

    /**
     * @Description:设置单元格内容,content为null则清除单元格内容
     */
    public static void setTcContent(Tc tc, RPr rpr, String content) {
        List<Object> pList = tc.getContent();
        P p = null;
        if (pList != null && pList.size() > 0) {
            if (pList.get(0) instanceof P) {
                p = (P) pList.get(0);
            }
        } else {
            p = new P();
            tc.getContent().add(p);
        }
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
            run.setRPr(rpr);
            run.getContent().add(text);

            for (int i = 1, len = contentArr.length; i < len; i++) {
                Br br = new Br();
                run.getContent().add(br);// 换行
                text = new Text();
                text.setSpace("preserve");
                text.setValue(contentArr[i]);
                run.setRPr(rpr);
                run.getContent().add(text);
            }
        }
    }

    /**
     * @Description:设置单元格内容,content为null则清除单元格内容
     */
    public static void removeTcContent(Tc tc) {
        List<Object> pList = tc.getContent();
        P p = null;
        if (pList != null && pList.size() > 0) {
            if (pList.get(0) instanceof P) {
                p = (P) pList.get(0);
            }
        } else {
            return;
        }
        List<Object> rList = p.getContent();
        if (rList != null && rList.size() > 0) {
            for (int i = 0, len = rList.size(); i < len; i++) {
                // 清除内容(所有的r
                p.getContent().remove(0);
            }
        }
    }
}
