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

import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STLineSpacingRule;
import org.docx4j.wml.STShd;
import org.junit.jupiter.api.Test;

class WmlParagraphUtilsTest {

    @Test
    void getPPrCreatesPPrWhenMissing() {
        P p = new P();
        PPr ppr = WmlParagraphUtils.getPPr(p);
        assertNotNull(ppr);
        assertSame(ppr, p.getPPr());
    }

    @Test
    void setParaJcAlignSetsAlignment() {
        P p = new P();
        WmlParagraphUtils.setParaJcAlign(p, JcEnumeration.CENTER);
        PPr ppr = p.getPPr();
        assertNotNull(ppr);
        assertNotNull(ppr.getJc());
        assertEquals(JcEnumeration.CENTER, ppr.getJc().getVal());
    }

    @Test
    void setParaJcAlignIgnoresNull() {
        P p = new P();
        WmlParagraphUtils.setParaJcAlign(p, null);
        // No PPr created
        assertEquals(null, p.getPPr());
    }

    @Test
    void setParagraphSpacingSetsSpacing() {
        P p = new P();
        WmlParagraphUtils.setParagraphSpacing(p, true, "100", "200", null, null, true, "240", STLineSpacingRule.AUTO);
        PPr ppr = WmlParagraphUtils.getPPr(p);
        assertNotNull(ppr.getSpacing());
        assertEquals(100, ppr.getSpacing().getBefore().intValue());
        assertEquals(200, ppr.getSpacing().getAfter().intValue());
        assertEquals(240, ppr.getSpacing().getLine().intValue());
        assertEquals(STLineSpacingRule.AUTO, ppr.getSpacing().getLineRule());
    }

    @Test
    void setParagraphSpacingWithEmptyArgsDoesNotThrow() {
        P p = new P();
        // The method always calls getPPr which creates PPr, then Spacing is created
        // but no fields are set. Just verify it doesn't throw.
        WmlParagraphUtils.setParagraphSpacing(p, false, "", "", "", "", false, "", null);
        assertNotNull(p.getPPr());
        assertNotNull(p.getPPr().getSpacing());
    }

    @Test
    void setParagraphSuppressLineNumSetsSuppressTrue() {
        P p = new P();
        WmlParagraphUtils.setParagraphSuppressLineNum(p);
        assertNotNull(p.getPPr());
        assertNotNull(p.getPPr().getSuppressLineNumbers());
        assertEquals(Boolean.TRUE, p.getPPr().getSuppressLineNumbers().isVal());
    }

    @Test
    void setParagraphShdStyleSetsShd() {
        P p = new P();
        WmlParagraphUtils.setParagraphShdStyle(p, STShd.CLEAR, "FF0000");
        PPr ppr = p.getPPr();
        assertNotNull(ppr.getShd());
        assertEquals("FF0000", ppr.getShd().getColor());
        assertEquals(STShd.CLEAR, ppr.getShd().getVal());
    }

    @Test
    void addPageBreakAddsBrToParagraph() {
        P p = new P();
        int before = p.getContent().size();
        WmlParagraphUtils.addPageBreak(p, STBrType.PAGE);
        assertEquals(before + 1, p.getContent().size());
    }

    @Test
    void setParaRContentAddsRun() {
        P p = new P();
        WmlParagraphUtils.setParaRContent(p, null, "hello world");
        assertEquals(1, p.getContent().size());
    }

    @Test
    void appendParaRContentAppends() {
        P p = new P();
        WmlParagraphUtils.appendParaRContent(p, null, "a");
        WmlParagraphUtils.appendParaRContent(p, null, "b");
        assertEquals(2, p.getContent().size());
    }

    @Test
    void setParagraphIndInfoSetsFirstLine() {
        P p = new P();
        WmlParagraphUtils.setParagraphIndInfo(p, "480", null, null, null, null, null, null, null);
        PPr ppr = p.getPPr();
        assertNotNull(ppr.getInd());
        assertEquals(480, ppr.getInd().getFirstLine().intValue());
    }

    @Test
    void setParaVanishSetsVanishTrue() {
        P p = new P();
        WmlParagraphUtils.setParaVanish(WmlParagraphUtils.getPPr(p), true);
        PPr ppr = p.getPPr();
        assertNotNull(ppr.getRPr());
        assertNotNull(ppr.getRPr().getVanish());
        assertEquals(Boolean.TRUE, ppr.getRPr().getVanish().isVal());
    }

    @Test
    void getParaRPrCreatesWhenNull() {
        PPr ppr = new PPr();
        org.docx4j.wml.ParaRPr paraRpr = WmlParagraphUtils.getParaRPr(ppr);
        assertNotNull(paraRpr);
        assertSame(paraRpr, ppr.getRPr());
    }
}
