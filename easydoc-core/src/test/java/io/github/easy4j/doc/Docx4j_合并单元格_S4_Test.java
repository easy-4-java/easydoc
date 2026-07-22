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
import java.util.ArrayList;  
import java.util.List;  
import javax.xml.bind.JAXBElement;  
import org.docx4j.wml.ContentAccessor;  
import org.docx4j.wml.Tbl;  
import org.docx4j.wml.Tc;  
import org.docx4j.wml.TcPr;  
import org.docx4j.wml.TcPrInner.GridSpan;  
import org.docx4j.wml.TcPrInner.HMerge;  
import org.docx4j.wml.TcPrInner.VMerge;  
import org.docx4j.wml.Tr;  
  
public class Docx4j_合并单元格_S4_Test {
	
    public void mergeCellsHorizontalByGridSpan(Tbl tbl, int row, int fromCell,  
            int toCell) {  
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
                gridSpan.setVal(BigInteger.valueOf(Math.min(tcList.size() - 1,  
                        toCell) - fromCell + 1));  
            } else {  
                tr.getContent().remove(cellIndex);  
            }  
        }  
    }  
  
    /** 
     * @Description: 跨列合并 
     */  
    public void mergeCellsHorizontal(Tbl tbl, int row, int fromCell, int toCell) {  
        if (row < 0 || fromCell < 0 || toCell < 0) {  
            return;  
        }  
        List<Tr> trList = getTblAllTr(tbl);  
        if (row > trList.size()) {  
            return;  
        }  
        Tr tr = trList.get(row);  
        List<Tc> tcList = getTrAllCell(tr);  
        for (int cellIndex = fromCell, len = Math  
                .min(tcList.size() - 1, toCell); cellIndex <= len; cellIndex++) {  
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
    public void mergeCellsVertically(Tbl tbl, int col, int fromRow, int toRow) {  
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
     * @Description:得到指定位置的表格 
     */  
    public Tc getTc(Tbl tbl, int row, int cell) {  
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
     * @Description: 获取所有的单元格 
     */  
    public List<Tc> getTrAllCell(Tr tr) {  
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
  
    public TcPr getTcPr(Tc tc) {  
        TcPr tcPr = tc.getTcPr();  
        if (tcPr == null) {  
            tcPr = new TcPr();  
            tc.setTcPr(tcPr);  
        }  
        return tcPr;  
    }  
  
    /** 
     * @Description: 得到表格所有的行 
     */  
    public List<Tr> getTblAllTr(Tbl tbl) {  
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
     * @Description:得到指定类型的元素 
     */  
    public static List<Object> getAllElementFromObject(Object obj,  
            Class<?> toSearch) {  
        List<Object> result = new ArrayList<Object>();  
        if (obj instanceof JAXBElement)  
            obj = ((JAXBElement<?>) obj).getValue();  
        if (obj.getClass().equals(toSearch))  
            result.add(obj);  
        else if (obj instanceof ContentAccessor) {  
            List<?> children = ((ContentAccessor) obj).getContent();  
            for (Object child : children) {  
                result.addAll(getAllElementFromObject(child, toSearch));  
            }  
        }  
        return result;  
    }  
}  