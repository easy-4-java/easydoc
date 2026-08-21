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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.List;

import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.docx4j.wml.TrPr;
import org.junit.jupiter.api.Test;

class WmlTableUtilsTest {

    @Test
    void createTableReturnsPopulatedTable() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(2, 3, new int[]{1000, 2000, 3000});
        assertNotNull(tbl);
        assertNotNull(tbl.getTblPr());
        assertNotNull(tbl.getTblGrid());
        assertEquals(3, tbl.getTblGrid().getGridCol().size());
        List<Tr> rows = WmlTableUtils.getTblAllTr(tbl);
        assertEquals(2, rows.size());
        List<Tc> cells = WmlTableUtils.getTrAllCell(rows.get(0));
        assertEquals(3, cells.size());
    }

    @Test
    void getTblPrCreatesTblPrWhenMissing() {
        Tbl tbl = new Tbl();
        TblPr tblPr = WmlTableUtils.getTblPr(tbl);
        assertNotNull(tblPr);
        assertSame(tblPr, tbl.getTblPr());
    }

    @Test
    void getTrPrCreatesTrPrWhenMissing() {
        Tr tr = new Tr();
        TrPr trPr = WmlTableUtils.getTrPr(tr);
        assertNotNull(trPr);
        assertSame(trPr, tr.getTrPr());
    }

    @Test
    void getTcPrCreatesTcPrWhenMissing() {
        Tc tc = new Tc();
        org.docx4j.wml.TcPr tcPr = WmlTableUtils.getTcPr(tc);
        assertNotNull(tcPr);
        assertSame(tcPr, tc.getTcPr());
    }

    @Test
    void setTableWidthSetsTblW() {
        Tbl tbl = new Tbl();
        WmlTableUtils.setTableWidth(tbl, "5000");
        TblPr tblPr = tbl.getTblPr();
        assertNotNull(tblPr);
        assertNotNull(tblPr.getTblW());
        assertEquals(new BigInteger("5000"), tblPr.getTblW().getW());
        assertEquals("dxa", tblPr.getTblW().getType());
    }

    @Test
    void setTableWidthIgnoresBlank() {
        Tbl tbl = new Tbl();
        WmlTableUtils.setTableWidth(tbl, "");
        // TblPr is not created when width is blank
        assertEquals(null, tbl.getTblPr());
    }

    @Test
    void setTcWidthSetsTcW() {
        Tc tc = new Tc();
        WmlTableUtils.setTcWidth(tc, "2500");
        org.docx4j.wml.TcPr tcPr = tc.getTcPr();
        assertNotNull(tcPr);
        assertNotNull(tcPr.getTcW());
        assertEquals(new BigInteger("2500"), tcPr.getTcW().getW());
        assertEquals("dxa", tcPr.getTcW().getType());
    }

    @Test
    void setTrHeightSetsTrPr() {
        Tr tr = new Tr();
        WmlTableUtils.setTrHeight(tr, "500");
        TrPr trPr = tr.getTrPr();
        assertNotNull(trPr);
        // TrHeight is set internally; just verify TrPr is non-null
    }

    @Test
    void addTrByIndexWithGridAddsRow() throws Exception {
        Tbl tbl = WmlTableUtils.createTable(1, 2, new int[]{1000, 2000});
        int before = WmlTableUtils.getTblAllTr(tbl).size();
        WmlTableUtils.addTrByIndex(tbl, 0);
        int after = WmlTableUtils.getTblAllTr(tbl).size();
        assertEquals(before + 1, after);
    }

    @Test
    void setTblJcAlignSetsAlignment() {
        Tbl tbl = new Tbl();
        WmlTableUtils.setTblJcAlign(tbl, JcEnumeration.RIGHT);
        TblPr tblPr = WmlTableUtils.getTblPr(tbl);
        assertNotNull(tblPr.getJc());
        assertEquals(JcEnumeration.RIGHT, tblPr.getJc().getVal());
    }

    @Test
    void setTcVAlignSetsVerticalAlignment() {
        Tc tc = new Tc();
        WmlTableUtils.setTcVAlign(tc, STVerticalJc.CENTER);
        org.docx4j.wml.TcPr tcPr = tc.getTcPr();
        assertNotNull(tcPr.getVAlign());
        assertEquals(STVerticalJc.CENTER, tcPr.getVAlign().getVal());
    }

    @Test
    void getTcAllPEmptyForFreshCell() {
        Tc tc = new Tc();
        List<P> pList = WmlTableUtils.getTcAllP(tc);
        assertNotNull(pList);
        assertEquals(0, pList.size());
    }

    @Test
    void getTrAllCellEmptyForFreshRow() {
        Tr tr = new Tr();
        List<Tc> tcList = WmlTableUtils.getTrAllCell(tr);
        assertNotNull(tcList);
        assertEquals(0, tcList.size());
    }

    @Test
    void getTcByPositionReturnsNullForNegative() {
        assertEquals(null, WmlTableUtils.getTc(new Tbl(), -1, 0));
    }

    @Test
    void getTblAllTrReturnsEmptyForEmpty() {
        Tbl tbl = new Tbl();
        List<Tr> rows = WmlTableUtils.getTblAllTr(tbl);
        assertNotNull(rows);
        assertEquals(0, rows.size());
    }

    @Test
    void removeTrByIndexReturnsFalseForNegative() {
        Tbl tbl = new Tbl();
        assertEquals(false, WmlTableUtils.removeTrByIndex(tbl, -1));
    }
}
