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

import java.util.List;

import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STLineSpacingRule;
import org.junit.jupiter.api.Test;

class WmlElementUtilsTest {

    @Test
    void getRPrCreatesRPrWhenMissing() {
        R r = new R();
        RPr rpr = WmlElementUtils.getRPr(r);
        assertNotNull(rpr);
        assertSame(rpr, r.getRPr());
    }

    @Test
    void getPPrCreatesPPrWhenMissing() {
        org.docx4j.wml.P p = new org.docx4j.wml.P();
        PPr ppr = WmlElementUtils.getPPr(p);
        assertNotNull(ppr);
        assertSame(ppr, p.getPPr());
    }

    @Test
    void getAllElementFromObjectReturnsEmptyForLeaf() {
        List<Object> result = WmlElementUtils.getAllElementFromObject("string", RPr.class);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void getTargetElementsReturnsEmptyForLeaf() {
        List<RPr> result = WmlElementUtils.getTargetElements(new RPr(), RPr.class);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getChildrenElementsIncludesSelf() {
        List<RPr> result = WmlElementUtils.getChildrenElements(new RPr(), RPr.class);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void setFontFamilyAppliesToExistingRPr() {
        RPr rpr = new RPr();
        WmlElementUtils.setFontFamily(rpr, "宋体", "Times New Roman");
        assertNotNull(rpr.getRFonts());
        assertEquals("宋体", rpr.getRFonts().getEastAsia());
        assertEquals("Times New Roman", rpr.getRFonts().getAscii());
    }

    @SuppressWarnings("unused")
    private static final JcEnumeration JC = JcEnumeration.LEFT;
    @SuppressWarnings("unused")
    private static final STLineSpacingRule RULE = STLineSpacingRule.AUTO;
}
