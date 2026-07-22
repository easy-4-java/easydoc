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

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.Text;

public class AddingTableOfContent {
	
    private static ObjectFactory factory;
 
    /**
     *  首先我们创建对象工厂和包并从包中抽出文档部件. 然后我们添加目录表, 后面跟着一些带有分类
     *  标题样式的段落. 最后我们保存包.
     */
    public static void main(String[] args) throws Docx4JException {
        factory = Context.getWmlObjectFactory();
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
 
        addTableOfContent(documentPart);
 
        documentPart.addStyledParagraphOfText("Heading1", "Hello 1");
        documentPart.addStyledParagraphOfText("Heading2", "Hello 2");
        documentPart.addStyledParagraphOfText("Heading3", "Hello 3");
        documentPart.addStyledParagraphOfText("Heading1", "Hello 1");
 
        wordMLPackage.save(new File("src/main/files/HelloWord10.docx"));
    }
 
    /**
     *  将目录表添加到文档.
     *  
     *  首先我们创建段落. 然后添加标记域开始的指示符, 然后添加域内容(真正的目录表), 接着添加域
     *  结束的指示符. 最后将段落添加到给定文档的JAXB元素中.
     *
     *  @param documentPart
     */
    private static void addTableOfContent(MainDocumentPart documentPart) {
        P paragraph = factory.createP();
 
        addFieldBegin(paragraph);
        addTableOfContentField(paragraph);
        addFieldEnd(paragraph);
 
        documentPart.getJaxbElement().getBody().getContent().add(paragraph);
    }
 
    /**
     * (不知道该怎么翻译, 因此将英文原注释保留)
     *  Adds the field that Word uses to create a table of content to the paragraph.
     *
     *  First we create a run and a text. Then we indicate that all spaces in the
     *  text are to be preserved and set the value to that of the TOC field.
     *  This field definition takes some arguments. The exact definition can be
     *  found in §17.16.5.58 of the Office Open XML standard. In this case we
     *  specify that we want to include all paragrapsh formatted with headings of
     *  levels 1-3 (\0 “1-3”). We also specify that we want all entries to be
     *  hyperlinks (\h), that we want to hide tab leader and page numbers in Web
     *  layout view (\z), and that we want to use the applied paragraph outline
     *  level (\\u).
     *  Finally we take the text and use it to create a JAXB element containing text
     *  and add this to the run, which we then add to the given paragraph.
     *  
     *  将Word用于创建目录表的域添加到段落中.
     *  
     *  首先创建一个可运行块和一个文本. 然后指出文本中所有的空格都被保护并给TOC域设置值. 这个域定义
     *  需要一些参数, 确切定义可以在Office Open XML标准的§17.16.5.58找到, 这种情况我们指定所有
     *  段落使用1-3级别的标题来格式化(\0 "1-3"). 我们同时指定所有的实体作为超链接(\h), 而且在Web
     *  视图中隐藏标签和页码(\z), 我们要使用段落大纲级别应用(\\u).
     *  最后使用文本对象创建了一个JAXB元素包含文本并添加到随后被添加到段落中的可运行块中. 
     *  
     *  @param paragraph
     */
    private static void addTableOfContentField(P paragraph) {
        R run = factory.createR();
        Text txt = new Text();
        txt.setSpace("preserve");
        txt.setValue("TOC \\o \"1-3\" \\h \\z \\u");
        run.getContent().add(factory.createRInstrText(txt));
        paragraph.getContent().add(run);
    }
 
    /**
     *  每个域都需要用复杂的域字符来确定界限. 本方法向给定段落添加在真正域之前的界定符.
     *  
     *  再一次以创建一个可运行块开始, 然后创建一个域字符来标记域的起始并标记域是'脏的'因为我们想要
     *  在整个文档生成之后进行内容更新.
     *  最后将域字符转换成JAXB元素并将其添加到可运行块, 然后将可运行块添加到段落中.
     *
     *  @param paragraph
     */
    private static void addFieldBegin(P paragraph) {
        R run = factory.createR();
        FldChar fldchar = factory.createFldChar();
        fldchar.setFldCharType(STFldCharType.BEGIN);
        fldchar.setDirty(true);
        run.getContent().add(getWrappedFldChar(fldchar));
        paragraph.getContent().add(run);
    }
 
    /**
     *  每个域都需要用复杂的域字符来确定界限. 本方法向给定段落添加在真正域之后的界定符.
     *  
     *  跟前面一样, 从创建可运行块开始, 然后创建域字符标记域的结束, 最后将域字符转换成JAXB元素并
     *  将其添加到可运行块, 可运行块再添加到段落中.
     *
     *  @param paragraph
     */
    private static void addFieldEnd(P paragraph) {
        R run = factory.createR();
        FldChar fldcharend = factory.createFldChar();
        fldcharend.setFldCharType(STFldCharType.END);
        run.getContent().add(getWrappedFldChar(fldcharend));
        paragraph.getContent().add(run);
    }
 
    /**
     *  创建包含给定复杂域字符的JAXBElement的便利方法.
     *
     *  @param fldchar
     *  @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static JAXBElement getWrappedFldChar(FldChar fldchar) {
        return new JAXBElement(new QName(Namespaces.NS_WORD12, "fldChar"), FldChar.class, fldchar);
    }
}