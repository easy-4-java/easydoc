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

import java.util.List;

import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.junit.jupiter.api.Test;

class WmlElementTraversalTest {

    @Test
    void getChildrenElementsIncludesSelfWhenMatching() {
        RPr rpr = new RPr();
        List<RPr> result = WmlElementTraversal.getChildrenElements(rpr, RPr.class);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getTargetElementsWalksIntoTr() {
        Tr tr = new Tr();
        Tc tc = new Tc();
        tr.getContent().add(tc);
        List<Tc> result = WmlElementTraversal.getTargetElements(tr, Tc.class);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllElementFromObjectOnEmptyTblReturnsEmpty() {
        Tbl tbl = new Tbl();
        List<Object> result = WmlElementTraversal.getAllElementFromObject(tbl, Tc.class);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void getTargetElementsOnPopulatedP() {
        P p = new P();
        PPr ppr = new PPr();
        p.setPPr(ppr);
        R r = new R();
        p.getContent().add(r);
        List<RPr> result = WmlElementTraversal.getTargetElements(p, RPr.class);
        assertNotNull(result);
        // No RPr in this tree
        assertEquals(0, result.size());
    }

    @Test
    void getTargetElementsReturnsSelfIfMatching() {
        Tbl tbl = new Tbl();
        // Tbl's content is a List - it does not match Tbl.class
        List<Tbl> result = WmlElementTraversal.getTargetElements(tbl, Tbl.class);
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
