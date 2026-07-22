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

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.STPageOrientation;

/**
 * 创建ooxml文档
 *
 * @素馨花 @version1.0
 */
public class CreateWordprocessingMLDocument {

	public static void main(String[] args) throws Exception {

		// 创建文档处理对象
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

		// 插入文字方法-1(快捷方法,忽略详细属性)
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title", "赛灵通(www.xerllent.cn)工作文档标题");
		wordMLPackage.getMainDocumentPart().addParagraphOfText("赛灵通项目(XerllentProjects)是一项基于j2ee技术的企业信息化系统研发计划!");

		// 插入文字方法-2(对象构造法,可以操作任何属性)
		/**
		 * Togetboldtext,youmustsettherun'srPr@w:b,
		 * soyoucan'tusethecreateParagraphOfTextconveniencemethod
		 * org.docx4j.wml.Pp=wordMLPackage.getMainDocumentPart().
		 * createParagraphOfText("text");//创建无格式文本代码段
		 */

		org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();// 文档子对象工厂

		org.docx4j.wml.P p = factory.createP();// 创建段落P

		// 创建文本段R内容
		org.docx4j.wml.R run = factory.createR();// 创建文本段R
		org.docx4j.wml.Text t = factory.createText();// 创建文本段内容Text
		t.setValue("text");
		run.getRunContent().add(t);// Text添加到R
		// 设置文本段R属性,Optionally,setpPr/rPr@w:b
		org.docx4j.wml.RPr rpr = factory.createRPr();
		org.docx4j.wml.BooleanDefaultTrue b = new org.docx4j.wml.BooleanDefaultTrue();// 创建带缺省值的boolen属性对象
		b.setVal(true);
		rpr.setB(b);
		run.setRPr(rpr);// 设置文本段R属性

		p.getParagraphContent().add(run);// R添加到P

		// 创建默认的段落属性,并加入到段落对象中去
		org.docx4j.wml.PPr ppr = factory.createPPr();
		org.docx4j.wml.ParaRPr paraRpr = factory.createParaRPr();
		ppr.setRPr(paraRpr);
		p.setPPr(ppr);// 段落属性PPr添加到P

		// 将P段落添加到文档里
		wordMLPackage.getMainDocumentPart().addObject(p);

		// 动态插入打印页面及分栏设置,这时一个A3幅面,页面分2栏的设置,试卷页面
		org.docx4j.wml.SectPr sp = factory.createSectPr();
		org.docx4j.wml.SectPr.PgSz pgsz = factory.createSectPrPgSz();// <w:pgSzw:w="23814"w:h="16840"w:orient="landscape"w:code="8"/>
		pgsz.setW(BigInteger.valueOf(23814L));
		pgsz.setH(BigInteger.valueOf(16840L));
		pgsz.setOrient(STPageOrientation.LANDSCAPE);
		pgsz.setCode(BigInteger.valueOf(8L));
		sp.setPgSz(pgsz);

		org.docx4j.wml.SectPr.PgMar pgmar = factory.createSectPrPgMar();// <w:pgMarw:top="1440"w:right="1440"w:bottom="1440"w:left="1440"w:header="720"w:footer="720"w:gutter="0"/>
		pgmar.setTop(BigInteger.valueOf(1440));
		pgmar.setRight(BigInteger.valueOf(1440));
		pgmar.setBottom(BigInteger.valueOf(1440));
		pgmar.setLeft(BigInteger.valueOf(1440));
		pgmar.setHeader(BigInteger.valueOf(720));
		pgmar.setFooter(BigInteger.valueOf(720));
		sp.setPgMar(pgmar);

		org.docx4j.wml.CTColumns cols = factory.createCTColumns();// <w:colsw:num="2"w:space="425"/>
		cols.setNum(BigInteger.valueOf(2));
		cols.setSpace(BigInteger.valueOf(425));
		sp.setCols(cols);

		org.docx4j.wml.CTDocGrid grd = factory.createCTDocGrid();// <w:docGridw:linePitch="360"/>
		grd.setLinePitch(BigInteger.valueOf(360));
		sp.setDocGrid(grd);

		wordMLPackage.getMainDocumentPart().addObject(sp);

		// 插入文字方法-3(更加简便快捷的插入内容方法,可以操作任何属性,但必须熟悉ooxml文档格式)
		// 自定义标签转化的时候,必须加xmlns:w=/"http://schemas.openxmlformats.org/wordprocessingml/2006/main/"语句
		String str = "<w:pxmlns:w=/\"http://schemas.openxmlformats.org/wordprocessingml/2006/main/\"><w:r><w:rPr><w:b/></w:rPr><w:t>Bold,justatw:rlevel</w:t></w:r></w:p>";
		wordMLPackage.getMainDocumentPart().addObject(org.docx4j.XmlUtils.unmarshalString(str));

		// 自定义标签转化的时候,必须加xmlns:w=/"http://schemas.openxmlformats.org/wordprocessingml/2006/main/"语句
		String str1 = null;// "<w:sectPrxmlns:w=/\"http://schemas.openxmlformats.org/wordprocessingml/2006/main/\"w:rsidR=/\"00F10179/\"w:rsidRPr=/\"00CB557A/\"w:rsidSect=/\"001337D5/\"><w:pgSzw:w=/"23814/"w:h=/"16840/"w:orient=/"landscape/"w:code=/"8/"/><w:pgMarw:top=/"1440/"w:right=/"1440/"w:bottom=/"1440/"w:left=/"1440/"w:header=/"720/"w:footer=/"720/"w:gutter=/"0/"/><w:colsw:num=/"2/"w:space=/"425/"/><w:docGridw:linePitch=/"360/"/></w:sectPr>";
		wordMLPackage.getMainDocumentPart().addObject(org.docx4j.XmlUtils.unmarshalString(str1));

		System.out.println("..done!");

		// Nowsaveit
		wordMLPackage.save(new java.io.File(System.getProperty("user.dir") + "/sample-docs/bolds.docx"));

		System.out.println("Done.");

	}
}