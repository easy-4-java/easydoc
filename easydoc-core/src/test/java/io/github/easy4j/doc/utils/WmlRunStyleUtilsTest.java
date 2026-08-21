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

import java.math.BigInteger;

import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.STEm;
import org.docx4j.wml.STShd;
import org.docx4j.wml.STVerticalAlignRun;
import org.docx4j.wml.UnderlineEnumeration;
import org.junit.jupiter.api.Test;

class WmlRunStyleUtilsTest {

    @Test
    void getRPrCreatesRPrWhenMissing() {
        R r = new R();
        RPr rpr = WmlRunStyleUtils.getRPr(r);
        assertNotNull(rpr);
        assertSame(rpr, r.getRPr());
    }

    @Test
    void setFontSizeSetsSzAndSzCs() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.setFontSize(rpr, "24");
        assertNotNull(rpr.getSz());
        assertEquals(new BigInteger("24"), rpr.getSz().getVal());
        assertSame(rpr.getSz(), rpr.getSzCs());
    }

    @Test
    void setFontSizeIgnoresBlank() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.setFontSize(rpr, "");
        assertEquals(null, rpr.getSz());
    }

    @Test
    void setFontFamilySetsRFonts() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.setFontFamily(rpr, "宋体", "Times New Roman");
        assertNotNull(rpr.getRFonts());
        assertEquals("宋体", rpr.getRFonts().getEastAsia());
        assertEquals("Times New Roman", rpr.getRFonts().getAscii());
    }

    @Test
    void setFontColorSetsColor() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.setFontColor(rpr, "FF0000");
        assertNotNull(rpr.getColor());
        assertEquals("FF0000", rpr.getColor().getVal());
    }

    @Test
    void setFontColorAllowsNull() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.setFontColor(rpr, null);
        // Accepts null, sets Color to null
        assertEquals(null, rpr.getColor());
    }

    @Test
    void addRPrBoldStyleSetsBTrue() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrBoldStyle(rpr);
        assertNotNull(rpr.getB());
        assertEquals(Boolean.TRUE, rpr.getB().isVal());
    }

    @Test
    void addRPrItalicStyleSetsITrue() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrItalicStyle(rpr);
        assertNotNull(rpr.getI());
        assertEquals(Boolean.TRUE, rpr.getI().isVal());
    }

    @Test
    void addRPrUnderlineStyleSetsU() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrUnderlineStyle(rpr, UnderlineEnumeration.SINGLE);
        assertNotNull(rpr.getU());
        assertEquals(UnderlineEnumeration.SINGLE, rpr.getU().getVal());
    }

    @Test
    void setRPrVanishStyleSetsVanishTrue() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.setRPrVanishStyle(rpr, true);
        assertNotNull(rpr.getVanish());
        assertEquals(Boolean.TRUE, rpr.getVanish().isVal());
    }

    @Test
    void addRPrEmStyleSetsEm() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrEmStyle(rpr, STEm.DOT);
        assertNotNull(rpr.getEm());
        assertEquals(STEm.DOT, rpr.getEm().getVal());
    }

    @Test
    void addRPrcaleStyleSetsVertAlign() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrcaleStyle(rpr, STVerticalAlignRun.SUPERSCRIPT);
        assertNotNull(rpr.getVertAlign());
        assertEquals(STVerticalAlignRun.SUPERSCRIPT, rpr.getVertAlign().getVal());
    }

    @Test
    void addRPrScaleStyleSetsW() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrScaleStyle(rpr, 100);
        assertNotNull(rpr.getW());
        assertEquals(100, rpr.getW().getVal());
    }

    @Test
    void addRPrtSpacingStyleSetsSpacing() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrtSpacingStyle(rpr, 50);
        assertNotNull(rpr.getSpacing());
        assertEquals(50, rpr.getSpacing().getVal().intValue());
    }

    @Test
    void addRPrtPositionStyleSetsPosition() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrtPositionStyle(rpr, 10);
        assertNotNull(rpr.getPosition());
        assertEquals(10, rpr.getPosition().getVal().intValue());
    }

    @Test
    void addRPrShadowStyleSetsShadow() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrShadowStyle(rpr);
        assertNotNull(rpr.getShadow());
        assertEquals(Boolean.TRUE, rpr.getShadow().isVal());
    }

    @Test
    void addRPrShdStyleSetsShd() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrShdStyle(rpr, STShd.CLEAR);
        assertNotNull(rpr.getShd());
        assertEquals(STShd.CLEAR, rpr.getShd().getVal());
    }

    @Test
    void addRPrImprintStyleSetsImprint() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrImprintStyle(rpr);
        assertNotNull(rpr.getImprint());
        assertEquals(Boolean.TRUE, rpr.getImprint().isVal());
    }

    @Test
    void addRPrEmbossStyleSetsEmboss() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrEmbossStyle(rpr);
        assertNotNull(rpr.getEmboss());
        assertEquals(Boolean.TRUE, rpr.getEmboss().isVal());
    }

    @Test
    void addRPrOutlineStyleSetsOutline() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrOutlineStyle(rpr);
        assertNotNull(rpr.getOutline());
        assertEquals(Boolean.TRUE, rpr.getOutline().isVal());
    }

    @Test
    void addRPrStrikeStyleSetsStrike() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrStrikeStyle(rpr, true, false);
        assertNotNull(rpr.getStrike());
        assertEquals(Boolean.TRUE, rpr.getStrike().isVal());
        assertEquals(null, rpr.getDstrike());
    }

    @Test
    void addRPrStrikeStyleSetsDstrike() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrStrikeStyle(rpr, false, true);
        assertEquals(null, rpr.getStrike());
        assertNotNull(rpr.getDstrike());
    }

    @Test
    void addRPrBorderStyleSetsBdr() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrBorderStyle(rpr, "4", STBorder.SINGLE, "0", "auto");
        assertNotNull(rpr.getBdr());
        assertEquals("auto", rpr.getBdr().getColor());
        assertEquals(new BigInteger("4"), rpr.getBdr().getSz());
    }

    @Test
    void addRPrHightLightStyleSetsHighlight() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.addRPrHightLightStyle(rpr, "yellow");
        assertNotNull(rpr.getHighlight());
        assertEquals("yellow", rpr.getHighlight().getVal());
    }

    @Test
    void setFontStyleInvokesAllThree() {
        RPr rpr = new RPr();
        WmlRunStyleUtils.setFontStyle(rpr, "宋体", "Times", "22", "0000FF");
        assertNotNull(rpr.getRFonts());
        assertNotNull(rpr.getSz());
        assertNotNull(rpr.getColor());
    }
}
