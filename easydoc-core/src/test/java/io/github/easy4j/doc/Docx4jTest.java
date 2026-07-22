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

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.*;

import java.io.File;
import java.util.List;

/**
 * @author liuzh
 */
public class Docx4jTest {

    public static void main(String[] args) throws Exception {
        //创建一个word
        //读取可以使用WordprocessingMLPackage.load方法
        WordprocessingMLPackage word = WordprocessingMLPackage.createPackage();
        String imageFilePath = Docx4jTest.class.getResource("/lw.jpg").getPath();

        //创建页眉
        HeaderPart headerPart = createHeader(word);
        //页眉添加图片
        headerPart.getContent().add(newImage(word, headerPart, imageFilePath));

        //创建页脚
        FooterPart footerPart = createFooter(word);
        //添加图片
        footerPart.getContent().add(newImage(word, footerPart, imageFilePath));

        //主内容添加文本和图片
        word.getMainDocumentPart().getContent().add(newText("http://www.mybatis.tk"));
        word.getMainDocumentPart().getContent().add(newText("http://blog.csdn.net/isea533"));

        word.getMainDocumentPart().getContent().add(
                newImage(word, word.getMainDocumentPart(), imageFilePath));

        //保存
        word.save(new File("d:/1.docx"));
    }

    private static final ObjectFactory factory = Context.getWmlObjectFactory();

    /**
     * 创建一段文本
     *
     * @param text
     * @return
     */
    public static P newText(String text){
        P p = factory.createP();
        R r = factory.createR();
        Text t = new Text();
        t.setValue(text);
        r.getContent().add(t);
        p.getContent().add(r);
        return p;
    }

    /**
     * 创建包含图片的内容
     *
     * @param word
     * @param sourcePart
     * @param imageFilePath
     * @return
     * @throws Exception
     */
    public static P newImage(WordprocessingMLPackage word,
                             Part sourcePart,
                             String imageFilePath) throws Exception {
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage
                .createImagePart(word, sourcePart, new File(imageFilePath));
        //随机数ID
        int id = (int) (Math.random() * 10000);
        //这里的id不重复即可
        Inline inline = imagePart.createImageInline("image", "image", id, id * 2, false);

        Drawing drawing = factory.createDrawing();
        drawing.getAnchorOrInline().add(inline);

        R r = factory.createR();
        r.getContent().add(drawing);

        P p = factory.createP();
        p.getContent().add(r);

        return p;
    }

    /**
     * 创建页眉
     *
     * @param word
     * @return
     * @throws Exception
     */
    public static HeaderPart createHeader(
            WordprocessingMLPackage word) throws Exception {
        HeaderPart headerPart = new HeaderPart();
        Relationship rel = word.getMainDocumentPart().addTargetPart(headerPart);
        createHeaderReference(word, rel);
        return headerPart;
    }

    /**
     * 创建页眉引用关系
     *
     * @param word
     * @param relationship
     * @throws InvalidFormatException
     */
    public static void createHeaderReference(
            WordprocessingMLPackage word,
            Relationship relationship )
            throws InvalidFormatException {
        List<SectionWrapper> sections = word.getDocumentModel().getSections();

        SectPr sectPr = sections.get(sections.size() - 1).getSectPr();
        // There is always a section wrapper, but it might not contain a sectPr
        if (sectPr==null ) {
            sectPr = factory.createSectPr();
            word.getMainDocumentPart().addObject(sectPr);
            sections.get(sections.size() - 1).setSectPr(sectPr);
        }
        HeaderReference headerReference = factory.createHeaderReference();
        headerReference.setId(relationship.getId());
        headerReference.setType(HdrFtrRef.DEFAULT);
        sectPr.getEGHdrFtrReferences().add(headerReference);
    }

    /**
     * 创建页脚
     *
     * @param word
     * @return
     * @throws Exception
     */
    public static FooterPart createFooter(WordprocessingMLPackage word) throws Exception {
        FooterPart footerPart = new FooterPart();
        Relationship rel = word.getMainDocumentPart().addTargetPart(footerPart);
        createFooterReference(word, rel);
        return footerPart;
    }

    /**
     * 创建页脚引用关系
     *
     * @param word
     * @param relationship
     * @throws InvalidFormatException
     */
    public static void createFooterReference(
            WordprocessingMLPackage word,
            Relationship relationship )
            throws InvalidFormatException {
        List<SectionWrapper> sections = word.getDocumentModel().getSections();

        SectPr sectPr = sections.get(sections.size() - 1).getSectPr();
        // There is always a section wrapper, but it might not contain a sectPr
        if (sectPr==null ) {
            sectPr = factory.createSectPr();
            word.getMainDocumentPart().addObject(sectPr);
            sections.get(sections.size() - 1).setSectPr(sectPr);
        }
        FooterReference footerReference = factory.createFooterReference();
        footerReference.setId(relationship.getId());
        footerReference.setType(HdrFtrRef.DEFAULT);
        sectPr.getEGHdrFtrReferences().add(footerReference);
    }
}