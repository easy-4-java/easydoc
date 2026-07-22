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

/**
 * http://53873039oycg.iteye.com/blog/2123815
 */
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.model.properties.table.tr.TrHeight;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.relationships.Relationship;
import org.docx4j.utils.BufferUtil;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTHeight;
import org.docx4j.wml.CTShd;
import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.Color;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HeaderReference;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.P.Hyperlink;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.Ind;
import org.docx4j.wml.PPrBase.PBdr;
import org.docx4j.wml.PPrBase.Spacing;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.STHint;
import org.docx4j.wml.STLineSpacingRule;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblGrid;
import org.docx4j.wml.TblGridCol;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.TcPrInner.HMerge;
import org.docx4j.wml.TcPrInner.VMerge;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.docx4j.wml.TrPr;
import org.docx4j.wml.U;
import org.docx4j.wml.UnderlineEnumeration;

public class Docx4J_例子2 {
	public static void main(String[] args) throws Exception {
		Docx4J_例子2 t = new Docx4J_例子2();
		WordprocessingMLPackage wordMLPackage = t
				.createWordprocessingMLPackage();
		MainDocumentPart mp = wordMLPackage.getMainDocumentPart();
		ObjectFactory factory = Context.getWmlObjectFactory();

		Relationship relationship = t.createHeaderPart(wordMLPackage, mp,
				factory, false, "3");
		relationship = t.createTextHeaderPart(wordMLPackage, mp, factory,
				"我是页眉,独乐乐不如众乐乐", true, "3", JcEnumeration.CENTER);
		t.addParagraphTest(wordMLPackage, mp, factory);
		t.addPageBreak(wordMLPackage, factory, STBrType.PAGE);
		t.createHeaderReference(wordMLPackage, mp, factory, relationship);
		t.createNormalTableTest(wordMLPackage, mp, factory);
		t.addPageBreak(wordMLPackage, factory, STBrType.TEXT_WRAPPING);
		t.createTableTest(wordMLPackage, mp, factory);
		t.addPageBreak(wordMLPackage, factory, STBrType.TEXT_WRAPPING);
		P paragraph=factory.createP();
		CTBorder topBorder=new CTBorder() ;
		topBorder.setSpace(new BigInteger("1"));
		topBorder.setSz(new BigInteger("2"));
		topBorder.setVal(STBorder.WAVE);
		t.createParagraghLine(wordMLPackage, mp, factory, paragraph, topBorder, topBorder, topBorder, topBorder);
		mp.addObject(paragraph);
		t.createHyperlink(wordMLPackage, mp, factory,paragraph,
				"mailto:1329186624@qq.com?subject=docx4j测试", "联系我","微软雅黑","24",JcEnumeration.CENTER);
		
		// 页脚
		// relationship = t.createFooterPart(wordMLPackage, mp, factory,
		// false,"3");
		// relationship = t.createTextFooterPart(wordMLPackage, mp,
		// factory,"我是页脚", true, "3", JcEnumeration.CENTER);
		relationship = t.createFooterPageNumPart(wordMLPackage, mp, factory,
				false, "3", JcEnumeration.CENTER);
		t.createFooterReference(wordMLPackage, mp, factory, relationship);
		t.saveWordPackage(wordMLPackage, new File(
				"f:/saveFile/temp/s7_simple.docx"));
	}

	public void addParagraphTest(WordprocessingMLPackage wordMLPackage,
			MainDocumentPart t, ObjectFactory factory) throws Exception {
		RPr titleRPr = getRPr(factory, "黑体", "000000", "30", STHint.EAST_ASIA,
				true, false, false, false);
		RPr boldRPr = getRPr(factory, "宋体", "000000", "24", STHint.EAST_ASIA,
				true, false, false, false);
		RPr fontRPr = getRPr(factory, "宋体", "000000", "22", STHint.EAST_ASIA,
				false, false, false, false);
		P paragraph = factory.createP();
		Text txt = factory.createText();
		R run = factory.createR();

		txt.setValue("七年级上册Unit2 This is just a test. sectionA测试卷答题卡");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(titleRPr);
		paragraph.getContent().add(run);
		t.addObject(paragraph);

		paragraph = factory.createP();
		setParagraphSpacing(factory, paragraph, JcEnumeration.CENTER, true,
				"0", "0", null, null, true, "240", STLineSpacingRule.AUTO);
		txt = factory.createText();
		txt.setValue("班级：________    姓名：________");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("一、单选题");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(boldRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", "200", "100", true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("1.下列有关仪器用途的说法错误的是(    )");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("A.烧杯用于较多量试剂的反应容器");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("B.烧杯用于较多量试剂的反应容器");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("C.烧杯用于较多量试剂的反应容器");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("D.烧杯用于较多量试剂的反应容器");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("2.下列实验操作中，正确的是(    ) ");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		// 段前8磅 段后0.5磅
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true,
				"160", "10", null, null, true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		File file = new File("f:/saveFile/temp/image1.png");
		java.io.InputStream is = new java.io.FileInputStream(file);
		createImageParagraph(wordMLPackage, factory, paragraph, "img_1", "A.",
				BufferUtil.getBytesFromInputStream(is), JcEnumeration.LEFT);

		file = new File("f:/saveFile/temp/image2.png");
		is = new java.io.FileInputStream(file);
		createImageParagraph(wordMLPackage, factory, paragraph, "img_2",
				StringUtils.leftPad("B.", 20),
				BufferUtil.getBytesFromInputStream(is), JcEnumeration.LEFT);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		file = new File("f:/saveFile/temp/image3.png");
		is = new java.io.FileInputStream(file);
		createImageParagraph(wordMLPackage, factory, paragraph, "img_3", "C.",
				BufferUtil.getBytesFromInputStream(is), JcEnumeration.LEFT);

		file = new File("f:/saveFile/temp/image4.png");
		is = new java.io.FileInputStream(file);
		createImageParagraph(wordMLPackage, factory, paragraph, "img_4",
				StringUtils.leftPad("D.", 20),
				BufferUtil.getBytesFromInputStream(is), JcEnumeration.LEFT);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		addPageBreak(wordMLPackage, factory, STBrType.PAGE);
		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试首行缩进");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.AUTO);
		setParagraphInd(factory, paragraph, JcEnumeration.LEFT, true, "200",
				false, null);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试悬挂缩进");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.AUTO);
		setParagraphInd(factory, paragraph, JcEnumeration.LEFT, false, null,
				true, "200");
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试段前2行单倍行距");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", "200", null, true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试段后1行单倍行距");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, "100", true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试单倍行距");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试段前2行段后2行单倍行距");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", "200", "200", true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试段前10磅段后10磅段单倍行距");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true,
				"200", "200", null, null, true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试段前10磅段后10磅段前2行段后2行单倍行距");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true,
				"200", "200", "200", "200", true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试段后12磅单倍行距");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"240", null, null, true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试单倍行距");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试行距300[实际为多倍行距1.25]");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "300", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试1.5倍行距");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "360", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试2倍行距");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "480", STLineSpacingRule.AUTO);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试最小值 12磅");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.AT_LEAST);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试固定值12磅");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.EXACT);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试固定值13磅");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "260", STLineSpacingRule.EXACT);
		t.addObject(paragraph);

		paragraph = factory.createP();
		txt = factory.createText();
		txt.setValue("测试多倍行距3倍");
		run = factory.createR();
		run.getContent().add(txt);
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);
		setParagraphSpacing(factory, paragraph, JcEnumeration.LEFT, true, "0",
				"0", null, null, true, "720", STLineSpacingRule.AUTO);
		t.addObject(paragraph);
	}

	public void createNormalTableTest(WordprocessingMLPackage wordMLPackage,
			MainDocumentPart t, ObjectFactory factory) throws Exception {
		RPr titleRpr = getRPr(factory, "宋体", "000000", "22", STHint.EAST_ASIA,
				true, false, false, false);
		RPr contentRpr = getRPr(factory, "宋体", "000000", "22",
				STHint.EAST_ASIA, false, false, false, false);
		Tbl table = factory.createTbl();
		CTBorder topBorder = new CTBorder();
		topBorder.setColor("80C687");
		topBorder.setVal(STBorder.DOUBLE);
		topBorder.setSz(new BigInteger("2"));
		CTBorder leftBorder = new CTBorder();
		leftBorder.setVal(STBorder.NONE);
		leftBorder.setSz(new BigInteger("0"));

		CTBorder hBorder = new CTBorder();
		hBorder.setVal(STBorder.SINGLE);
		hBorder.setSz(new BigInteger("1"));

		addBorders(table, topBorder, topBorder, leftBorder, leftBorder,
				hBorder, null);

		double[] colWidthPercent = new double[] { 15, 20, 20, 20, 25 };// 百分比
		setTableGridCol(wordMLPackage, factory, table, 80, colWidthPercent);

		Tr titleRow = factory.createTr();
		setTableTrHeight(factory, titleRow, "500");
		addTableCell(factory, wordMLPackage, titleRow, "序号", titleRpr,
				JcEnumeration.CENTER, true, "C6D9F1");
		addTableCell(factory, wordMLPackage, titleRow, "姓甚", titleRpr,
				JcEnumeration.CENTER, true, "C6D9F1");
		addTableCell(factory, wordMLPackage, titleRow, "名谁", titleRpr,
				JcEnumeration.CENTER, true, "C6D9F1");
		addTableCell(factory, wordMLPackage, titleRow, "籍贯", titleRpr,
				JcEnumeration.CENTER, true, "C6D9F1");
		addTableCell(factory, wordMLPackage, titleRow, "营生", titleRpr,
				JcEnumeration.CENTER, true, "C6D9F1");
		table.getContent().add(titleRow);

		for (int i = 0; i < 10; i++) {
			Tr contentRow = factory.createTr();
			addTableCell(factory, wordMLPackage, contentRow, i + "",
					contentRpr, JcEnumeration.CENTER, false, null);
			addTableCell(factory, wordMLPackage, contentRow, "无名氏", contentRpr,
					JcEnumeration.CENTER, false, null);
			addTableCell(factory, wordMLPackage, contentRow, "佚名", contentRpr,
					JcEnumeration.CENTER, false, null);
			addTableCell(factory, wordMLPackage, contentRow, "武林", contentRpr,
					JcEnumeration.CENTER, false, null);
			addTableCell(factory, wordMLPackage, contentRow, "吟诗赋曲",
					contentRpr, JcEnumeration.CENTER, false, null);
			table.getContent().add(contentRow);
		}
		setTableAlign(factory, table, JcEnumeration.CENTER);
		t.addObject(table);
	}

	public void createTableTest(WordprocessingMLPackage wordMLPackage,
			MainDocumentPart t, ObjectFactory factory) throws Exception {
		RPr titleRpr = getRPr(factory, "宋体", "000000", "22", STHint.EAST_ASIA,
				true, false, false, false);
		RPr contentRpr = getRPr(factory, "宋体", "000000", "22",
				STHint.EAST_ASIA, false, false, false, false);
		Tbl table = factory.createTbl();
		addBorders(table, "2");

		double[] colWidthPercent = new double[] { 15, 20, 20, 20, 25 };// 百分比
		setTableGridCol(wordMLPackage, factory, table, 100, colWidthPercent);
		List<String> columnList = new ArrayList<String>();
		columnList.add("序号");
		columnList.add("姓名信息|姓甚|名谁");
		columnList.add("名刺信息|籍贯|营生");
		addTableTitleCell(factory, wordMLPackage, table, columnList, titleRpr,
				JcEnumeration.CENTER, true, "C6D9F1");

		for (int i = 0; i < 10; i++) {
			Tr contentRow = factory.createTr();
			addTableCell(factory, wordMLPackage, contentRow, i + "",
					contentRpr, JcEnumeration.CENTER, false, null);
			addTableCell(factory, wordMLPackage, contentRow, "无名氏", contentRpr,
					JcEnumeration.CENTER, false, null);
			addTableCell(factory, wordMLPackage, contentRow, "佚名", contentRpr,
					JcEnumeration.CENTER, false, null);
			addTableCell(factory, wordMLPackage, contentRow, "武林", contentRpr,
					JcEnumeration.CENTER, false, null);
			addTableCell(factory, wordMLPackage, contentRow, "吟诗赋曲",
					contentRpr, JcEnumeration.CENTER, false, null);
			table.getContent().add(contentRow);
		}
		setTableAlign(factory, table, JcEnumeration.CENTER);
		t.addObject(table);
	}

	// 设置段间距-->行距 段前段后距离
	// 段前段后可以设置行和磅 行距只有磅
	// 段前磅值和行值同时设置，只有行值起作用
	// TODO 1磅=20 1行=100 单倍行距=240 为什么是这个值不知道
	/**
	 * @param jcEnumeration
	 *            对齐方式
	 * @param isSpace
	 *            是否设置段前段后值
	 * @param before
	 *            段前磅数
	 * @param after
	 *            段后磅数
	 * @param beforeLines
	 *            段前行数
	 * @param afterLines
	 *            段后行数
	 * @param isLine
	 *            是否设置行距
	 * @param lineValue
	 *            行距值
	 * @param sTLineSpacingRule
	 *            自动auto 固定exact 最小 atLeast
	 */
	public void setParagraphSpacing(ObjectFactory factory, P p,
			JcEnumeration jcEnumeration, boolean isSpace, String before,
			String after, String beforeLines, String afterLines,
			boolean isLine, String lineValue,
			STLineSpacingRule sTLineSpacingRule) {
		PPr pPr = p.getPPr();
		if (pPr == null) {
			pPr = factory.createPPr();
		}
		Jc jc = pPr.getJc();
		if (jc == null) {
			jc = new Jc();
		}
		jc.setVal(jcEnumeration);
		pPr.setJc(jc);

		Spacing spacing = new Spacing();
		if (isSpace) {
			if (before != null) {
				// 段前磅数
				spacing.setBefore(new BigInteger(before));
			}
			if (after != null) {
				// 段后磅数
				spacing.setAfter(new BigInteger(after));
			}
			if (beforeLines != null) {
				// 段前行数
				spacing.setBeforeLines(new BigInteger(beforeLines));
			}
			if (afterLines != null) {
				// 段后行数
				spacing.setAfterLines(new BigInteger(afterLines));
			}
		}
		if (isLine) {
			if (lineValue != null) {
				spacing.setLine(new BigInteger(lineValue));
			}
			spacing.setLineRule(sTLineSpacingRule);
		}
		pPr.setSpacing(spacing);
		p.setPPr(pPr);
	}

	// 设置缩进 同时设置为true,则为悬挂缩进
	public void setParagraphInd(ObjectFactory factory, P p,
			JcEnumeration jcEnumeration, boolean firstLine,
			String firstLineValue, boolean hangLine, String hangValue) {
		PPr pPr = p.getPPr();
		if (pPr == null) {
			pPr = factory.createPPr();
		}
		Jc jc = pPr.getJc();
		if (jc == null) {
			jc = new Jc();
		}
		jc.setVal(jcEnumeration);
		pPr.setJc(jc);

		Ind ind = pPr.getInd();
		if (ind == null) {
			ind = new Ind();
		}
		if (firstLine) {
			if (firstLineValue != null) {
				ind.setFirstLineChars(new BigInteger(firstLineValue));
			}
		}
		if (hangLine) {
			if (hangValue != null) {
				ind.setHangingChars(new BigInteger(hangValue));
			}
		}
		pPr.setInd(ind);
		p.setPPr(pPr);
	}

	/**
	 * 创建字体
	 * 
	 * @param isBlod
	 *            粗体
	 * @param isUnderLine
	 *            下划线
	 * @param isItalic
	 *            斜体
	 * @param isStrike
	 *            删除线
	 */
	public RPr getRPr(ObjectFactory factory, String fontFamily,
			String colorVal, String fontSize, STHint sTHint, boolean isBlod,
			boolean isUnderLine, boolean isItalic, boolean isStrike) {
		RPr rPr = factory.createRPr();
		RFonts rf = new RFonts();
		rf.setHint(sTHint);
		rf.setAscii(fontFamily);
		rf.setHAnsi(fontFamily);
		rPr.setRFonts(rf);

		BooleanDefaultTrue bdt = factory.createBooleanDefaultTrue();
		rPr.setBCs(bdt);
		if (isBlod) {
			rPr.setB(bdt);
		}
		if (isItalic) {
			rPr.setI(bdt);
		}
		if (isStrike) {
			rPr.setStrike(bdt);
		}
		if (isUnderLine) {
			U underline = new U();
			underline.setVal(UnderlineEnumeration.SINGLE);
			rPr.setU(underline);
		}

		Color color = new Color();
		color.setVal(colorVal);
		rPr.setColor(color);

		HpsMeasure sz = new HpsMeasure();
		sz.setVal(new BigInteger(fontSize));
		rPr.setSz(sz);
		rPr.setSzCs(sz);
		return rPr;
	}

	// 文字页面
	public Relationship createTextHeaderPart(
			WordprocessingMLPackage wordprocessingMLPackage,
			MainDocumentPart t, ObjectFactory factory, String content,
			boolean isUnderLine, String underLineSz, JcEnumeration jcEnumeration)
			throws Exception {
		HeaderPart headerPart = new HeaderPart();
		Relationship rel = t.addTargetPart(headerPart);
		headerPart.setJaxbElement(getTextHdr(wordprocessingMLPackage, factory,
				headerPart, content, isUnderLine, underLineSz, jcEnumeration));
		return rel;
	}

	// 文字页脚
	public Relationship createTextFooterPart(
			WordprocessingMLPackage wordprocessingMLPackage,
			MainDocumentPart t, ObjectFactory factory, String content,
			boolean isUnderLine, String underLineSz, JcEnumeration jcEnumeration)
			throws Exception {
		FooterPart footerPart = new FooterPart();
		Relationship rel = t.addTargetPart(footerPart);
		footerPart.setJaxbElement(getTextFtr(wordprocessingMLPackage, factory,
				footerPart, content, isUnderLine, underLineSz, jcEnumeration));
		return rel;
	}

	// 图片页眉
	public Relationship createHeaderPart(
			WordprocessingMLPackage wordprocessingMLPackage,
			MainDocumentPart t, ObjectFactory factory, boolean isUnderLine,
			String underLineSize) throws Exception {
		HeaderPart headerPart = new HeaderPart();
		Relationship rel = t.addTargetPart(headerPart);
		// After addTargetPart, so image can be added properly
		headerPart.setJaxbElement(getHdr(wordprocessingMLPackage, factory,
				headerPart, isUnderLine, underLineSize));
		return rel;
	}

	// 图片页脚
	public Relationship createFooterPart(
			WordprocessingMLPackage wordprocessingMLPackage,
			MainDocumentPart t, ObjectFactory factory, boolean isUnderLine,
			String underLineSize) throws Exception {
		FooterPart footerPart = new FooterPart();
		Relationship rel = t.addTargetPart(footerPart);
		footerPart.setJaxbElement(getFtr(wordprocessingMLPackage, factory,
				footerPart, isUnderLine, underLineSize));
		return rel;
	}

	public Relationship createFooterPageNumPart(
			WordprocessingMLPackage wordprocessingMLPackage,
			MainDocumentPart t, ObjectFactory factory, boolean isUnderLine,
			String underLineSz, JcEnumeration jcEnumeration) throws Exception {
		FooterPart footerPart = new FooterPart();
		footerPart.setPackage(wordprocessingMLPackage);
		footerPart.setJaxbElement(createFooterWithPageNr(
				wordprocessingMLPackage, factory, isUnderLine, underLineSz,
				jcEnumeration));
		return t.addTargetPart(footerPart);
	}

	public Ftr createFooterWithPageNr(
			WordprocessingMLPackage wordprocessingMLPackage,
			ObjectFactory factory, boolean isUnderLine, String underLineSz,
			JcEnumeration jcEnumeration) throws Exception {
		Ftr ftr = factory.createFtr();
		P paragraph = factory.createP();
		RPr fontRPr = getRPr(factory, "宋体", "000000", "20", STHint.EAST_ASIA,
				false, false, false, false);
		R run = factory.createR();
		run.setRPr(fontRPr);
		paragraph.getContent().add(run);

		addPageTextField(factory, paragraph, "第");
		addFieldBegin(factory, paragraph);
		addPageNumberField(factory, paragraph);
		addFieldEnd(factory, paragraph);
		addPageTextField(factory, paragraph, "页");

		addPageTextField(factory, paragraph, " 总共");
		addFieldBegin(factory, paragraph);
		addTotalPageNumberField(factory, paragraph);
		addFieldEnd(factory, paragraph);
		addPageTextField(factory, paragraph, "页");
		setParagraphSpacing(factory, paragraph, jcEnumeration, true, "0", "0",
				null, null, true, "240", STLineSpacingRule.AUTO);
		PPr pPr = paragraph.getPPr();
		if (pPr == null) {
			pPr = factory.createPPr();
		}
		Jc jc = pPr.getJc();
		if (jc == null) {
			jc = new Jc();
		}
		jc.setVal(jcEnumeration);
		pPr.setJc(jc);
		if (isUnderLine) {
			PBdr pBdr = pPr.getPBdr();
			if (pBdr == null) {
				pBdr = factory.createPPrBasePBdr();
			}
			CTBorder value = new CTBorder();
			value.setVal(STBorder.SINGLE);
			value.setColor("000000");
			value.setSpace(new BigInteger("0"));
			value.setSz(new BigInteger(underLineSz));
			pBdr.setBetween(value);
			pPr.setPBdr(pBdr);
			paragraph.setPPr(pPr);
		}
		ftr.getContent().add(paragraph);
		if (isUnderLine) {
			ftr.getContent().add(
					createHeaderBlankP(wordprocessingMLPackage, factory,
							underLineSz, jcEnumeration));
		}
		return ftr;
	}

	public void addFieldBegin(ObjectFactory factory, P paragraph) {
		R run = factory.createR();
		FldChar fldchar = factory.createFldChar();
		fldchar.setFldCharType(STFldCharType.BEGIN);
		run.getContent().add(fldchar);
		paragraph.getContent().add(run);
	}

	public void addFieldEnd(ObjectFactory factory, P paragraph) {
		FldChar fldcharend = factory.createFldChar();
		fldcharend.setFldCharType(STFldCharType.END);
		R run3 = factory.createR();
		run3.getContent().add(fldcharend);
		paragraph.getContent().add(run3);
	}

	public void addPageNumberField(ObjectFactory factory, P paragraph) {
		R run = factory.createR();
		Text txt = new Text();
		txt.setSpace("preserve");
		txt.setValue("PAGE  \\* MERGEFORMAT ");
		run.getContent().add(factory.createRInstrText(txt));
		paragraph.getContent().add(run);
	}

	public void addTotalPageNumberField(ObjectFactory factory, P paragraph) {
		R run = factory.createR();
		Text txt = new Text();
		txt.setSpace("preserve");
		txt.setValue("NUMPAGES  \\* MERGEFORMAT ");
		run.getContent().add(factory.createRInstrText(txt));
		paragraph.getContent().add(run);
	}

	private void addPageTextField(ObjectFactory factory, P paragraph,
			String value) {
		R run = factory.createR();
		Text txt = new Text();
		txt.setSpace("preserve");
		txt.setValue(value);
		run.getContent().add(txt);
		paragraph.getContent().add(run);
	}

	public void createHeaderReference(
			WordprocessingMLPackage wordprocessingMLPackage,
			MainDocumentPart t, ObjectFactory factory, Relationship relationship)
			throws InvalidFormatException {
		List<SectionWrapper> sections = wordprocessingMLPackage
				.getDocumentModel().getSections();
		SectPr sectPr = sections.get(sections.size() - 1).getSectPr();
		// There is always a section wrapper, but it might not contain a sectPr
		if (sectPr == null) {
			sectPr = factory.createSectPr();
			t.addObject(sectPr);
			sections.get(sections.size() - 1).setSectPr(sectPr);
		}
		HeaderReference headerReference = factory.createHeaderReference();
		headerReference.setId(relationship.getId());
		headerReference.setType(HdrFtrRef.DEFAULT);
		sectPr.getEGHdrFtrReferences().add(headerReference);
	}

	public void createFooterReference(
			WordprocessingMLPackage wordprocessingMLPackage,
			MainDocumentPart t, ObjectFactory factory, Relationship relationship)
			throws InvalidFormatException {
		List<SectionWrapper> sections = wordprocessingMLPackage
				.getDocumentModel().getSections();
		SectPr sectPr = sections.get(sections.size() - 1).getSectPr();
		// There is always a section wrapper, but it might not contain a sectPr
		if (sectPr == null) {
			sectPr = factory.createSectPr();
			t.addObject(sectPr);
			sections.get(sections.size() - 1).setSectPr(sectPr);
		}
		FooterReference footerReference = factory.createFooterReference();
		footerReference.setId(relationship.getId());
		footerReference.setType(HdrFtrRef.DEFAULT);
		sectPr.getEGHdrFtrReferences().add(footerReference);
	}

	public Hdr getTextHdr(WordprocessingMLPackage wordprocessingMLPackage,
			ObjectFactory factory, Part sourcePart, String content,
			boolean isUnderLine, String underLineSz, JcEnumeration jcEnumeration)
			throws Exception {
		Hdr hdr = factory.createHdr();
		P headP = factory.createP();
		Text text = factory.createText();
		text.setValue(content);
		R run = factory.createR();
		run.getContent().add(text);
		headP.getContent().add(run);

		PPr pPr = headP.getPPr();
		if (pPr == null) {
			pPr = factory.createPPr();
		}
		Jc jc = pPr.getJc();
		if (jc == null) {
			jc = new Jc();
		}
		jc.setVal(jcEnumeration);
		pPr.setJc(jc);

		if (isUnderLine) {
			PBdr pBdr = pPr.getPBdr();
			if (pBdr == null) {
				pBdr = factory.createPPrBasePBdr();
			}
			CTBorder value = new CTBorder();
			value.setVal(STBorder.SINGLE);
			value.setColor("000000");
			value.setSpace(new BigInteger("0"));
			value.setSz(new BigInteger(underLineSz));
			pBdr.setBetween(value);
			pPr.setPBdr(pBdr);
			headP.setPPr(pPr);
		}
		setParagraphSpacing(factory, headP, jcEnumeration, true, "0", "0",
				null, null, true, "240", STLineSpacingRule.AUTO);
		hdr.getContent().add(headP);
		if (isUnderLine) {
			hdr.getContent().add(
					createHeaderBlankP(wordprocessingMLPackage, factory,
							underLineSz, jcEnumeration));
		}
		return hdr;
	}

	public Ftr getTextFtr(WordprocessingMLPackage wordprocessingMLPackage,
			ObjectFactory factory, Part sourcePart, String content,
			boolean isUnderLine, String underLineSz, JcEnumeration jcEnumeration)
			throws Exception {
		Ftr ftr = factory.createFtr();
		P footerP = factory.createP();
		Text text = factory.createText();
		text.setValue(content);
		R run = factory.createR();
		run.getContent().add(text);
		footerP.getContent().add(run);

		PPr pPr = footerP.getPPr();
		if (pPr == null) {
			pPr = factory.createPPr();
		}
		Jc jc = pPr.getJc();
		if (jc == null) {
			jc = new Jc();
		}
		jc.setVal(jcEnumeration);
		pPr.setJc(jc);
		footerP.setPPr(pPr);
		setParagraphSpacing(factory, footerP, jcEnumeration, true, "0", "0",
				null, null, true, "240", STLineSpacingRule.AUTO);
		if (isUnderLine) {
			PBdr pBdr = pPr.getPBdr();
			if (pBdr == null) {
				pBdr = factory.createPPrBasePBdr();
			}
			CTBorder value = new CTBorder();
			value.setVal(STBorder.SINGLE);
			value.setColor("000000");
			value.setSpace(new BigInteger("0"));
			value.setSz(new BigInteger(underLineSz));
			pBdr.setBetween(value);
			pPr.setPBdr(pBdr);
			footerP.setPPr(pPr);
		}
		ftr.getContent().add(footerP);
		if (isUnderLine) {
			ftr.getContent().add(
					createHeaderBlankP(wordprocessingMLPackage, factory,
							underLineSz, jcEnumeration));
		}
		return ftr;
	}

	public Hdr getHdr(WordprocessingMLPackage wordprocessingMLPackage,
			ObjectFactory factory, Part sourcePart, boolean isUnderLine,
			String underLineSize) throws Exception {
		Hdr hdr = factory.createHdr();
		File file = new File("f:/saveFile/tmp/xxt.jpg");
		java.io.InputStream is = new java.io.FileInputStream(file);
		hdr.getContent().add(
				newImage(wordprocessingMLPackage, factory, sourcePart,
						BufferUtil.getBytesFromInputStream(is), "filename",
						"这是页眉部分", 1, 2, isUnderLine, underLineSize,
						JcEnumeration.CENTER));
		if (isUnderLine) {
			hdr.getContent().add(
					createHeaderBlankP(wordprocessingMLPackage, factory,
							underLineSize, JcEnumeration.CENTER));
		}
		return hdr;
	}

	public Ftr getFtr(WordprocessingMLPackage wordprocessingMLPackage,
			ObjectFactory factory, Part sourcePart, boolean isUnderLine,
			String underLineSz) throws Exception {
		Ftr ftr = factory.createFtr();
		File file = new File("f:/saveFile/tmp/xxt.jpg");
		java.io.InputStream is = new java.io.FileInputStream(file);
		ftr.getContent().add(
				newImage(wordprocessingMLPackage, factory, sourcePart,
						BufferUtil.getBytesFromInputStream(is), "filename",
						"这是页脚", 1, 2, isUnderLine, underLineSz,
						JcEnumeration.CENTER));
		if (isUnderLine) {
			ftr.getContent().add(
					createHeaderBlankP(wordprocessingMLPackage, factory,
							underLineSz, JcEnumeration.CENTER));
		}
		return ftr;
	}

	// 段落中插入文字和图片
	public P createImageParagraph(WordprocessingMLPackage wordMLPackage,
			ObjectFactory factory, P p, String fileName, String content,
			byte[] bytes, JcEnumeration jcEnumeration) throws Exception {
		BinaryPartAbstractImage imagePart = BinaryPartAbstractImage
				.createImagePart(wordMLPackage, bytes);
		Inline inline = imagePart.createImageInline(fileName, "这是图片", 1, 2,
				false);
		Text text = factory.createText();
		text.setValue(content);
		text.setSpace("preserve");
		R run = factory.createR();
		p.getContent().add(run);
		run.getContent().add(text);
		Drawing drawing = factory.createDrawing();
		run.getContent().add(drawing);
		drawing.getAnchorOrInline().add(inline);
		PPr pPr = p.getPPr();
		if (pPr == null) {
			pPr = factory.createPPr();
		}
		Jc jc = pPr.getJc();
		if (jc == null) {
			jc = new Jc();
		}
		jc.setVal(jcEnumeration);
		pPr.setJc(jc);
		p.setPPr(pPr);
		setParagraphSpacing(factory, p, jcEnumeration, true, "0", "0", null,
				null, true, "240", STLineSpacingRule.AUTO);
		return p;
	}

	public P newImage(WordprocessingMLPackage wordMLPackage,
			ObjectFactory factory, Part sourcePart, byte[] bytes,
			String filenameHint, String altText, int id1, int id2,
			boolean isUnderLine, String underLineSize,
			JcEnumeration jcEnumeration) throws Exception {
		BinaryPartAbstractImage imagePart = BinaryPartAbstractImage
				.createImagePart(wordMLPackage, sourcePart, bytes);
		Inline inline = imagePart.createImageInline(filenameHint, altText, id1,
				id2, false);
		P p = factory.createP();
		R run = factory.createR();
		p.getContent().add(run);
		Drawing drawing = factory.createDrawing();
		run.getContent().add(drawing);
		drawing.getAnchorOrInline().add(inline);
		PPr pPr = p.getPPr();
		if (pPr == null) {
			pPr = factory.createPPr();
		}
		Jc jc = pPr.getJc();
		if (jc == null) {
			jc = new Jc();
		}
		jc.setVal(jcEnumeration);
		pPr.setJc(jc);
		p.setPPr(pPr);

		if (isUnderLine) {
			PBdr pBdr = pPr.getPBdr();
			if (pBdr == null) {
				pBdr = factory.createPPrBasePBdr();
			}
			CTBorder value = new CTBorder();
			value.setVal(STBorder.SINGLE);
			value.setColor("000000");
			value.setSpace(new BigInteger("0"));
			value.setSz(new BigInteger(underLineSize));
			pBdr.setBetween(value);
			pPr.setPBdr(pBdr);
		}
		setParagraphSpacing(factory, p, jcEnumeration, true, "0", "0", null,
				null, true, "240", STLineSpacingRule.AUTO);
		return p;
	}

	public P createHeaderBlankP(WordprocessingMLPackage wordMLPackage,
			ObjectFactory factory, String underLineSz,
			JcEnumeration jcEnumeration) throws Exception {
		P p = factory.createP();
		R run = factory.createR();
		p.getContent().add(run);
		PPr pPr = p.getPPr();
		if (pPr == null) {
			pPr = factory.createPPr();
		}
		Jc jc = pPr.getJc();
		if (jc == null) {
			jc = new Jc();
		}
		jc.setVal(jcEnumeration);
		pPr.setJc(jc);

		PBdr pBdr = pPr.getPBdr();
		if (pBdr == null) {
			pBdr = factory.createPPrBasePBdr();
		}
		CTBorder value = new CTBorder();
		value.setVal(STBorder.SINGLE);
		value.setColor("000000");
		value.setSpace(new BigInteger("0"));
		value.setSz(new BigInteger(underLineSz));
		pBdr.setBetween(value);
		pPr.setPBdr(pBdr);
		p.setPPr(pPr);
		setParagraphSpacing(factory, p, jcEnumeration, true, "0", "0", null,
				null, true, "240", STLineSpacingRule.AUTO);
		return p;
	}

	// 分页
	public void addPageBreak(WordprocessingMLPackage wordMLPackage,
			ObjectFactory factory, STBrType sTBrType) {
		MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
		Br breakObj = new Br();
		breakObj.setType(sTBrType);
		P paragraph = factory.createP();
		paragraph.getContent().add(breakObj);
		documentPart.addObject(paragraph);
	}

	// 得到页面宽度
	public int getWritableWidth(WordprocessingMLPackage wordPackage)
			throws Exception {
		return wordPackage.getDocumentModel().getSections().get(0)
				.getPageDimensions().getWritableWidthTwips();
	}

	// 设置整列宽度
	/**
	 * @param tableWidthPercent
	 *            表格占页面宽度百分比
	 * @param widthPercent
	 *            各列百分比
	 */
	public void setTableGridCol(WordprocessingMLPackage wordPackage,
			ObjectFactory factory, Tbl table, double tableWidthPercent,
			double[] widthPercent) throws Exception {
		int width = getWritableWidth(wordPackage);
		int tableWidth = (int) (width * tableWidthPercent / 100);
		TblGrid tblGrid = factory.createTblGrid();
		for (int i = 0; i < widthPercent.length; i++) {
			TblGridCol gridCol = factory.createTblGridCol();
			gridCol.setW(BigInteger.valueOf((long) (tableWidth
					* widthPercent[i] / 100)));
			tblGrid.getGridCol().add(gridCol);
		}
		table.setTblGrid(tblGrid);

		TblPr tblPr = table.getTblPr();
		if (tblPr == null) {
			tblPr = factory.createTblPr();
		}
		TblWidth tblWidth = new TblWidth();
		tblWidth.setType("dxa");// 这一行是必须的,不自己设置宽度默认是auto
		tblWidth.setW(new BigInteger(tableWidth + ""));
		tblPr.setTblW(tblWidth);
		table.setTblPr(tblPr);
	}

	// 表格水平对齐方式
	public void setTableAlign(ObjectFactory factory, Tbl table,
			JcEnumeration jcEnumeration) {
		TblPr tablePr = table.getTblPr();
		if (tablePr == null) {
			tablePr = factory.createTblPr();
		}
		Jc jc = tablePr.getJc();
		if (jc == null) {
			jc = new Jc();
		}
		jc.setVal(jcEnumeration);
		tablePr.setJc(jc);
		table.setTblPr(tablePr);
	}

	// 表格增加边框
	public void addBorders(Tbl table, String borderSize) {
		table.setTblPr(new TblPr());
		CTBorder border = new CTBorder();
		border.setColor("auto");
		border.setSz(new BigInteger(borderSize));
		border.setSpace(new BigInteger("0"));
		border.setVal(STBorder.SINGLE);
		TblBorders borders = new TblBorders();
		borders.setBottom(border);
		borders.setLeft(border);
		borders.setRight(border);
		borders.setTop(border);
		borders.setInsideH(border);
		borders.setInsideV(border);
		table.getTblPr().setTblBorders(borders);
	}

	// 表格增加边框 可以设置上下左右四个边框样式以及横竖水平线样式
	public void addBorders(Tbl table, CTBorder topBorder,
			CTBorder bottomBorder, CTBorder leftBorder, CTBorder rightBorder,
			CTBorder hBorder, CTBorder vBorder) {
		table.setTblPr(new TblPr());
		TblBorders borders = new TblBorders();
		borders.setBottom(bottomBorder);
		borders.setLeft(leftBorder);
		borders.setRight(rightBorder);
		borders.setTop(bottomBorder);
		borders.setInsideH(hBorder);
		borders.setInsideV(vBorder);
		table.getTblPr().setTblBorders(borders);
	}

	// 设置tr高度
	public void setTableTrHeight(ObjectFactory factory, Tr tr, String heigth) {
		TrPr trPr = tr.getTrPr();
		if (trPr == null) {
			trPr = factory.createTrPr();
		}
		CTHeight ctHeight = new CTHeight();
		ctHeight.setVal(new BigInteger(heigth));
		TrHeight trHeight = new TrHeight(ctHeight);
		trHeight.set(trPr);
		tr.setTrPr(trPr);
	}

	// 新增单元格
	public void addTableCell(ObjectFactory factory,
			WordprocessingMLPackage wordMLPackage, Tr tableRow, String content,
			RPr rpr, JcEnumeration jcEnumeration, boolean hasBgColor,
			String backgroudColor) {
		Tc tableCell = factory.createTc();
		P p = factory.createP();
		setParagraphSpacing(factory, p, jcEnumeration, true, "0", "0", null,
				null, true, "240", STLineSpacingRule.AUTO);
		Text t = factory.createText();
		t.setValue(content);
		R run = factory.createR();
		// 设置表格内容字体样式
		run.setRPr(rpr);

		TcPr tcPr = tableCell.getTcPr();
		if (tcPr == null) {
			tcPr = factory.createTcPr();
		}

		CTVerticalJc valign = factory.createCTVerticalJc();
		valign.setVal(STVerticalJc.CENTER);
		tcPr.setVAlign(valign);

		run.getContent().add(t);
		p.getContent().add(run);
		tableCell.getContent().add(p);
		if (hasBgColor) {
			CTShd shd = tcPr.getShd();
			if (shd == null) {
				shd = factory.createCTShd();
			}
			shd.setColor("auto");
			shd.setFill(backgroudColor);
			tcPr.setShd(shd);
		}
		tableCell.setTcPr(tcPr);
		tableRow.getContent().add(tableCell);
	}

	// 新增单元格
	public void addTableTitleCell(ObjectFactory factory,
			WordprocessingMLPackage wordMLPackage, Tbl table,
			List<String> titleList, RPr rpr, JcEnumeration jcEnumeration,
			boolean hasBgColor, String backgroudColor) {
		Tr firstTr = factory.createTr();
		Tr secordTr = factory.createTr();
		setTableTrHeight(factory, firstTr, "200");
		setTableTrHeight(factory, secordTr, "200");
		table.getContent().add(firstTr);
		table.getContent().add(secordTr);
		for (String str : titleList) {
			if (str.indexOf("|") == -1) {
				createNormalCell(factory, firstTr, str, rpr, jcEnumeration,
						hasBgColor, backgroudColor, false, "restart");
				createNormalCell(factory, secordTr, "", rpr, jcEnumeration,
						hasBgColor, backgroudColor, false, null);
			} else {
				String[] cols = str.split("\\|");
				createNormalCell(factory, firstTr, cols[0], rpr, jcEnumeration,
						hasBgColor, backgroudColor, true, "restart");
				for (int i = 1; i < cols.length - 1; i++) {
					createNormalCell(factory, firstTr, "", rpr, jcEnumeration,
							hasBgColor, backgroudColor, true, null);
				}
				for (int i = 1; i < cols.length; i++) {
					createNormalCell(factory, secordTr, cols[i], rpr,
							jcEnumeration, hasBgColor, backgroudColor, true,
							null);
				}
			}
		}
	}

	public void createNormalCell(ObjectFactory factory, Tr tr, String content,
			RPr rpr, JcEnumeration jcEnumeration, boolean hasBgColor,
			String backgroudColor, boolean isHMerger, String mergeVal) {
		Tc tableCell = factory.createTc();
		P p = factory.createP();
		setParagraphSpacing(factory, p, jcEnumeration, true, "0", "0", null,
				null, true, "240", STLineSpacingRule.AUTO);
		Text t = factory.createText();
		t.setValue(content);
		R run = factory.createR();
		// 设置表格内容字体样式
		run.setRPr(rpr);
		TcPr tcPr = tableCell.getTcPr();
		if (tcPr == null) {
			tcPr = factory.createTcPr();
		}
		CTVerticalJc valign = factory.createCTVerticalJc();
		valign.setVal(STVerticalJc.CENTER);
		tcPr.setVAlign(valign);
		if (isHMerger) {
			HMerge merge = new HMerge();
			if (mergeVal != null) {
				merge.setVal(mergeVal);
			}
			tcPr.setHMerge(merge);
		} else {
			VMerge merge = new VMerge();
			if (mergeVal != null) {
				merge.setVal(mergeVal);
			}
			tcPr.setVMerge(merge);
		}
		run.getContent().add(t);
		p.getContent().add(run);
		tableCell.getContent().add(p);
		if (hasBgColor) {
			CTShd shd = tcPr.getShd();
			if (shd == null) {
				shd = factory.createCTShd();
			}
			shd.setColor("auto");
			shd.setFill(backgroudColor);
			tcPr.setShd(shd);
		}
		tableCell.setTcPr(tcPr);
		tr.getContent().add(tableCell);
	}

	  public void createParagraghLine(WordprocessingMLPackage wordMLPackage,
				MainDocumentPart t, ObjectFactory factory,P p,CTBorder topBorder,CTBorder bottomBorder,CTBorder leftBorder,CTBorder rightBorder){
			PPr ppr=new PPr();
			PBdr pBdr=new PBdr();
			pBdr.setTop(topBorder);
			pBdr.setBottom(bottomBorder);
			pBdr.setLeft(leftBorder);
			pBdr.setRight(rightBorder);
			ppr.setPBdr(pBdr);
			p.setPPr(ppr);
		}
	 
	public void createHyperlink(WordprocessingMLPackage wordMLPackage,
			MainDocumentPart t, ObjectFactory factory,P paragraph, String url,
			String value, String fontName, String fontSize,JcEnumeration jcEnumeration) throws Exception {
		org.docx4j.relationships.ObjectFactory reFactory = new org.docx4j.relationships.ObjectFactory();
		org.docx4j.relationships.Relationship rel = reFactory
				.createRelationship();
		rel.setType(Namespaces.HYPERLINK);
		rel.setTarget(url);
		rel.setTargetMode("External");
		t.getRelationshipsPart().addRelationship(rel);
		StringBuffer sb = new StringBuffer();
		// addRelationship sets the rel's @Id
		sb.append("<w:hyperlink r:id=\"");
		sb.append(rel.getId());
		sb.append("\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" ");
		sb.append("xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" >");
		sb.append("<w:r><w:rPr><w:rStyle w:val=\"Hyperlink\" />");
		sb.append("<w:rFonts  w:ascii=\"");
		sb.append(fontName);
		sb.append("\"  w:hAnsi=\"");
		sb.append(fontName);
		sb.append("\"  w:eastAsia=\"");
		sb.append(fontName);
		sb.append("\" w:hint=\"eastAsia\"/>");
		sb.append("<w:sz w:val=\"");
		sb.append(fontSize);
		sb.append("\"/><w:szCs w:val=\"");
		sb.append(fontSize);
		sb.append("\"/></w:rPr><w:t>");
		sb.append(value);
		sb.append("</w:t></w:r></w:hyperlink>");

		Hyperlink link = (Hyperlink) XmlUtils.unmarshalString(sb.toString());
		paragraph.getContent().add(link);
		setParagraphSpacing(factory, paragraph, jcEnumeration, true, "0",
				"0", null, null, true, "240", STLineSpacingRule.AUTO);
		PPr ppr = paragraph.getPPr();
		if (ppr == null) {
			ppr = factory.createPPr();
		}
		RFonts fonts = new RFonts();
		fonts.setAscii("微软雅黑");
		fonts.setHAnsi("微软雅黑");
		fonts.setEastAsia("微软雅黑");
		fonts.setHint(STHint.EAST_ASIA);
		ParaRPr rpr = new ParaRPr();
		rpr.setRFonts(fonts);
		ppr.setRPr(rpr);
		paragraph.setPPr(ppr);
	}

	public WordprocessingMLPackage createWordprocessingMLPackage()
			throws Exception {
		return WordprocessingMLPackage.createPackage();
	}

	public void saveWordPackage(WordprocessingMLPackage wordPackage, File file)
			throws Exception {
		wordPackage.save(file);
	}

}