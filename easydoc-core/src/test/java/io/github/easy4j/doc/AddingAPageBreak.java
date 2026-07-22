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

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Br;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.STBrType;

/**
 * 
 * @className	： AddingAPageBreak
 * @description	： 添加换页符
	添加换页符相当地简单。Docx4j拥有一个叫作Br的break对象，这个对象有一个type属性，这种情况下我们需要将其设置为page，type其它可选的值为column和textWrapping。这个break可以很简单地添加到段落中。
 * @author 		： <a href="https://github.com/easy-4-java">hiwepy</a>
 * @date		： 2017年5月24日 下午10:34:01
 * @version 	V1.0
 */
public class AddingAPageBreak {
    private static ObjectFactory factory;
    private static WordprocessingMLPackage  wordMLPackage;
 
    public static void main (String[] args) throws Docx4JException {
        wordMLPackage = WordprocessingMLPackage.createPackage();
        factory = Context.getWmlObjectFactory();
 
        wordMLPackage.getMainDocumentPart().addParagraphOfText("Hello Word!");
 
        addPageBreak();
 
        wordMLPackage.getMainDocumentPart().addParagraphOfText("This is page 2!");
        wordMLPackage.save(new java.io.File("src/main/files/HelloWord11.docx") );
    }
 
    /**
     * 向文档添加一个换行符
     */
    private static void addPageBreak() {
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
 
        Br breakObj = new Br();
        breakObj.setType(STBrType.PAGE);
 
        P paragraph = factory.createP();
        paragraph.getContent().add(breakObj);
        documentPart.getJaxbElement().getBody().getContent().add(paragraph);
    }
}
