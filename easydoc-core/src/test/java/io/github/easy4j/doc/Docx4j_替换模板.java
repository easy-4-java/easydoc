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
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
  

import javax.xml.bind.JAXBElement;  
  

import org.apache.commons.lang3.RandomStringUtils;  
import org.apache.commons.lang3.RandomUtils;  
import org.docx4j.TraversalUtil;  
import org.docx4j.XmlUtils;  
import org.docx4j.dml.chart.CTChartSpace;  
import org.docx4j.dml.chart.CTNumDataSource;  
import org.docx4j.dml.chart.CTNumVal;  
import org.docx4j.dml.chart.CTPieChart;  
import org.docx4j.dml.chart.CTPieSer;  
import org.docx4j.jaxb.Context;  
import org.docx4j.openpackaging.exceptions.Docx4JException;  
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;  
import org.docx4j.openpackaging.parts.DrawingML.Chart;  
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;  
import org.docx4j.openpackaging.parts.relationships.Namespaces;  
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;  
import org.docx4j.relationships.Relationship;
import io.github.easy4j.doc.wml.MyTblFinder;
import org.docx4j.wml.Body;  
import org.docx4j.wml.Br;  
import org.docx4j.wml.ContentAccessor;  
import org.docx4j.wml.Document;  
import org.docx4j.wml.P;  
import org.docx4j.wml.R;  
import org.docx4j.wml.Tbl;  
import org.docx4j.wml.Tc;  
import org.docx4j.wml.TcPr;  
import org.docx4j.wml.TcPrInner.VMerge;  
import org.docx4j.wml.Text;  
import org.docx4j.wml.Tr;  
import org.docx4j.wml.TrPr;  
import org.junit.Test;
  
public class Docx4j_替换模板 {  
    private String inputfilepath = "E:/test_tmp/0904/test_docx.docx";  
    private static final String outputfilepath = "e:/test_tmp/0904/sys_" + System.currentTimeMillis() + ".docx";  
    public static org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();  
  
    @Test  
    public void testReplaceTemplateDocx() throws Exception {  
        replaceTemplateDocx();  
    }  
  
    public void replaceTemplateDocx() throws Exception {  
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File(inputfilepath));  
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();  
        HashMap<String, String> staticMap = getStaticData();  
        // 替换普通变量  
        documentPart.variableReplace(staticMap);  
  
        Document document = (Document) documentPart.getContents();  
        Body body = document.getBody();  
        MyTblFinder tblFinder = new MyTblFinder();  
        new TraversalUtil(body, tblFinder);  
        // 替换表格模板第三行数据  
        Tbl firstTbl = tblFinder.getTbls().get(0);  
        List<Object> trObjList = firstTbl.getContent();  
        Tr tr = (Tr) trObjList.get(2);  
        replaceTrTotalData(tr, getTotalData());  
  
        int lvlIndex = 3;  
        // 替换表格模板第四行数据  
        int lvlTotalSize = 4;  
        tr = (Tr) trObjList.get(lvlIndex);  
        List<String[]> lvDataList = getLvDataList(lvlTotalSize);  
        replaceTrData(firstTbl, tr, lvDataList, lvlIndex);  
  
        // 重新获取表格数据  
        trObjList = firstTbl.getContent();  
        int sexTotalSize = 2;  
        // 替换表格模板第五行数据  
        tr = (Tr) trObjList.get(lvlIndex + lvlTotalSize);  
        List<String[]> sexDataList = getSexDataList(2);  
        replaceTrSexData(tr, sexDataList.get(0));  
        // 替换表格模板第六行数据  
        tr = (Tr) trObjList.get(4 + lvlTotalSize);  
        replaceTrSexData(tr, sexDataList.get(1));  
  
        // 替换表格模板第七行数据  
        tr = (Tr) trObjList.get(5 + lvlTotalSize);  
        int nationTotalSize = 56;  
        List<String[]> nationDataList = getNationDataList(nationTotalSize);  
        replaceTrData(firstTbl, tr, nationDataList, lvlIndex+lvlTotalSize+sexTotalSize);  
  
        // 合并层次单元格 层次位于第3行  
        mergeCellsVertically(firstTbl, 0, lvlIndex, lvlIndex + lvlTotalSize - 1);  
        // 合并民族单元格 层次+性别2行  
        mergeCellsVertically(firstTbl, 0, lvlIndex + lvlTotalSize + sexTotalSize,  
                lvlIndex + lvlTotalSize + sexTotalSize + nationTotalSize - 1);  
  
        // 替换图表数据  
        String[] chartArr = getChartData();  
        replacePieChartData(wordMLPackage, chartArr);  
        // 保存结果  
        saveWordPackage(wordMLPackage, outputfilepath);  
    }  
  
    /** 
     * 替换图表数据 
     */  
    private void replacePieChartData(WordprocessingMLPackage wordMLPackage, String[] chartArr) throws Docx4JException {  
        RelationshipsPart rp = wordMLPackage.getMainDocumentPart().getRelationshipsPart();  
        Relationship rel = rp.getRelationshipByType(Namespaces.SPREADSHEETML_CHART);  
        Chart chart = (Chart) rp.getPart(rel);  
        CTChartSpace chartSpace = chart.getContents();  
        List<Object> charObjList = chartSpace.getChart().getPlotArea().getAreaChartOrArea3DChartOrLineChart();  
        CTPieChart pieChart = (CTPieChart) charObjList.get(0);  
        List<CTPieSer> serList = pieChart.getSer();  
        CTNumDataSource serVal = serList.get(0).getVal();  
        List<CTNumVal> ptList = serVal.getNumRef().getNumCache().getPt();  
        ptList.get(0).setV(chartArr[0]);  
        ptList.get(1).setV(chartArr[1]);  
    }  
  
    /** 
     * 替换tr数据 
     */  
    private void replaceTrSexData(Tr tr, String[] dataArr) throws Exception {  
        List<Tc> tcList = getTrAllCell(tr);  
        Tc tc = null;  
        for (int i = 2, iLen = tcList.size(); i < iLen; i++) {  
            tc = tcList.get(i);  
            replaceTcContent(tc, dataArr[i - 2]);  
        }  
    }  
  
    /** 
     * 替换tr数据,其他插入 
     */  
    private void replaceTrData(Tbl tbl, Tr tr, List<String[]> dataList, int trIndex) throws Exception {  
        TrPr trPr = XmlUtils.deepCopy(tr.getTrPr());  
        String tcContent = null;  
        String[] tcMarshaArr = getTcMarshalStr(tr);  
        String[] dataArr = null;  
        for (int i = 0, iLen = dataList.size(); i < iLen; i++) {  
            dataArr = dataList.get(i);  
            Tr newTr = null;  
            Tc newTc = null;  
            if (i == 0) {  
                newTr = tr;  
            } else {  
                newTr = factory.createTr();  
                if (trPr != null) {  
                    newTr.setTrPr(trPr);  
                }  
                newTc = factory.createTc();  
                newTr.getContent().add(newTc);  
            }  
            for (int j = 0, jLen = dataArr.length; j < jLen; j++) {  
                tcContent = tcMarshaArr[j];  
                if (tcContent != null) {  
                    tcContent = tcContent.replaceAll("(<w:t>)(.*?)(</w:t>)", "<w:t>" + dataArr[j] + "</w:t>");  
                    newTc = (Tc) XmlUtils.unmarshalString(tcContent);  
                } else {  
                    newTc = factory.createTc();  
                    setNewTcContent(newTc, dataArr[j]);  
                }  
                // 新增tr  
                if (i != 0) {  
                    newTr.getContent().add(newTc);  
                } else {  
                    // 替换  
                    newTr.getContent().set(j + 1, newTc);  
                }  
            }  
            if(i!=0){  
                tbl.getContent().add(trIndex+i, newTr);  
            }  
        }  
    }  
  
    /** 
     * 获取单元格字符串 
     */  
    private String[] getTcMarshalStr(Tr tr) {  
        List<Object> tcObjList = tr.getContent();  
        String[] marshaArr = new String[7];  
        // 跳过层次  
        for (int i = 1, len = tcObjList.size(); i < len; i++) {  
            marshaArr[i - 1] = XmlUtils.marshaltoString(tcObjList.get(i), true, false);  
        }  
        return marshaArr;  
    }  
  
    /** 
     * 替换表格合计数据 
     */  
    private void replaceTrTotalData(Tr tr, String[] dataArr) {  
        List<Object> tcObjList = tr.getContent();  
        Tc tc = null;  
        // 跳过合计  
        for (int i = 2, len = tcObjList.size(); i < len; i++) {  
            tc = (Tc) XmlUtils.unwrap(tcObjList.get(i));  
            replaceTcContent(tc, dataArr[i - 2]);  
        }  
    }  
  
    /** 
     * 替换单元格内容 
     */  
    private void replaceTcContent(Tc tc, String value) {  
        List<Object> rtnList = getAllElementFromObject(tc, Text.class);  
        if (rtnList == null || rtnList.size() == 0) {  
            return;  
        }  
        Text textElement = (Text) rtnList.get(0);  
        textElement.setValue(value);  
    }  
  
    /** 
     * 设置单元格内容 
     *  
     * @param tc 
     * @param content 
     */  
    public static void setNewTcContent(Tc tc, String content) {  
        P p = factory.createP();  
        tc.getContent().add(p);  
        R run = factory.createR();  
        p.getContent().add(run);  
        if (content != null) {  
            String[] contentArr = content.split("\n");  
            Text text = factory.createText();  
            text.setSpace("preserve");  
            text.setValue(contentArr[0]);  
            run.getContent().add(text);  
  
            for (int i = 1, len = contentArr.length; i < len; i++) {  
                Br br = factory.createBr();  
                run.getContent().add(br);// 换行  
                text = factory.createText();  
                text.setSpace("preserve");  
                text.setValue(contentArr[i]);  
                run.getContent().add(text);  
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
                vMerge = factory.createTcPrInnerVMerge();  
                tcPr.setVMerge(vMerge);  
            }  
            if (rowIndex == fromRow) {  
                vMerge.setVal("restart");  
            } else {  
                vMerge.setVal("continue");  
            }  
        }  
    }  
  
    public static TcPr getTcPr(Tc tc) {  
        TcPr tcPr = tc.getTcPr();  
        if (tcPr == null) {  
            tcPr = new TcPr();  
            tc.setTcPr(tcPr);  
        }  
        return tcPr;  
    }  
  
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
  
    public static List<Tc> getTrAllCell(Tr tr) {  
        List<Object> objList = getAllElementFromObject(tr, Tc.class);  
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
  
    public static List<Tr> getTblAllTr(Tbl tbl) {  
        List<Object> objList = getAllElementFromObject(tbl, Tr.class);  
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
     * 按class获取内容 
     */  
    public static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {  
        List<Object> result = new ArrayList<Object>();  
        if (obj instanceof JAXBElement) {  
            obj = ((JAXBElement<?>) obj).getValue();  
        }  
        if (obj.getClass().equals(toSearch)) {  
            result.add(obj);  
        } else if (obj instanceof ContentAccessor) {  
            List<?> children = ((ContentAccessor) obj).getContent();  
            for (Object child : children) {  
                result.addAll(getAllElementFromObject(child, toSearch));  
            }  
        }  
        return result;  
    }  
  
    /** 
     * 保存文档 
     */  
    public static void saveWordPackage(WordprocessingMLPackage wordPackage, String filePath) throws Exception {  
        wordPackage.save(new File(filePath));  
    }  
  
    /** 
     * 获取图表数据 
     */  
    private String[] getChartData() {  
        int[] intArr = new int[] { RandomUtils.nextInt(1, 99), 0 };  
        intArr[1] = 100 - intArr[0];  
        return new String[] { Integer.toString(intArr[0]), Integer.toString(intArr[1]) };  
    }  
  
    /** 
     * 获取民族数据 
     */  
    private List<String[]> getNationDataList(int total) {  
        List<String[]> dataList = new ArrayList<String[]>();  
        for (int i = 0; i < total; i++) {  
            dataList.add(  
                    new String[] { "民族_" + i, RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2),  
                            RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2),  
                            RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2) });  
        }  
        return dataList;  
    }  
  
    /** 
     * 获取性别数据 
     */  
    private List<String[]> getSexDataList(int total) {  
        List<String[]> dataList = new ArrayList<String[]>();  
        // 男  
        dataList.add(new String[] { RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2),  
                RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2),  
                RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2) });  
        // 女  
        dataList.add(new String[] { RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2),  
                RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2),  
                RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2) });  
        return dataList;  
    }  
  
    /** 
     * 获取层次数据 
     */  
    private List<String[]> getLvDataList(int total) {  
        List<String[]> dataList = new ArrayList<String[]>();  
        for (int i = 0; i < total; i++) {  
            dataList.add(  
                    new String[] { "本科_" + i, RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2),  
                            RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2),  
                            RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2) });  
        }  
        return dataList;  
    }  
  
    /** 
     * 获取合计数据 
     */  
    private String[] getTotalData() {  
        return new String[] { "2000", "44.4", "2500", "55.5", "500", "11.11" };  
    }  
  
    /** 
     * 获取静态数据 
     */  
    private HashMap<String, String> getStaticData() {  
        HashMap<String, String> dataMap = new HashMap<String, String>();  
        dataMap.put("Pg1-1", "2016");  
        dataMap.put("Pg1-2", "11");  
        dataMap.put("Pg1-02", "18");  
        dataMap.put("Pg1-3", "2000");  
        dataMap.put("Pg1-4", "500");  
        dataMap.put("Pg1-5", "1500");  
        dataMap.put("Pg1-6", "900");  
        dataMap.put("Pg1-7", "600");  
        dataMap.put("Pg1-8", "2015");  
        dataMap.put("Pg1-9", "1000");  
        dataMap.put("Pg1-10", "10");  
        dataMap.put("Pg1-11", "1");  
        dataMap.put("Pg1-12", "5");  
        dataMap.put("Pg1-13", "3");  
        dataMap.put("Pg1-14", "4");  
        dataMap.put("Tb1-24", "25");  
        dataMap.put("Tb1-44", "45");  
        dataMap.put("Tb1-54", "30");  
        dataMap.put("Pg2-1", "1200");  
        dataMap.put("Tb1-64", "60");  
        dataMap.put("Pg2-2", "800");  
        dataMap.put("Tb1-74", "40");  
        dataMap.put("Pg2-3", "0.66");  
        return dataMap;  
    }  
}  