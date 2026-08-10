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
import java.util.List;

import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.relationships.Relationship;
import io.github.easy4j.doc.io.WordprocessingMLPackageRender;
import io.github.easy4j.doc.utils.WmlElementUtils;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.SectPr;
/**
 * 
 * @className	： AddingAFooter
 * @description	： 添加页脚或页眉
 *	添加页脚或页眉（或二者兼具）是一个很多客户都想要的特性。幸运的是，做这个并不是很难。这个例子是docx4j示例的一部分，但是我删掉了添加图像的部分只展示最基本的添加页脚部分。添加页眉，你需要做的仅仅是用header来替换footer（或者hdr替换ftr）。
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @date		： 2017年5月24日 下午10:32:22
 * @version 	V1.0
 */
public class AddingAFooter {
	
    private static WordprocessingMLPackage wordMLPackage;
    private static ObjectFactory factory;
 
    /**
     *  First we create the package and the factory. Then we create the footer part,
     *  which returns a relationship. This relationship is then used to create
     *  a reference. Finally we add some text to the document and save it.
     */
    public static void main (String[] args) throws Docx4JException {
    	
    	 WordprocessingMLPackageRender render = new WordprocessingMLPackageRender(wordMLPackage);
         
    	 
    	 
        wordMLPackage = WordprocessingMLPackage.createPackage();
        factory = Context.getWmlObjectFactory();
 
        Relationship relationship = createFooterPart();
        createFooterReference(relationship);
 
        wordMLPackage.getMainDocumentPart().addParagraphOfText("Hello Word!");
 
        wordMLPackage.save(new File("d://HelloWord14.docx") );
    }
 
    /**
     *  This method creates a footer part and set the package on it. Then we add some
     *  text and add the footer part to the package. Finally we return the
     *  corresponding relationship.
     *
     *  @return
     *  @throws InvalidFormatException
     */
    private static Relationship createFooterPart() throws InvalidFormatException {
        FooterPart footerPart = new FooterPart();
        footerPart.setPackage(wordMLPackage);
        footerPart.setJaxbElement(WmlElementUtils.createFooter("Text"));
 
        return wordMLPackage.getMainDocumentPart().addTargetPart(footerPart);
    }
   
 
    /**
     *  First we retrieve the document sections from the package. As we want to add
     *  a footer, we get the last section and take the section properties from it.
     *  The section is always present, but it might not have properties, so we check
     *  if they exist to see if we should create them. If they need to be created,
     *  we do and add them to the main document part and the section.
     *  Then we create a reference to the footer, give it the id of the relationship,
     *  set the type to header/footer reference and add it to the collection of
     *  references to headers and footers in the section properties.
     *
     * @param relationship
     */
    private static void createFooterReference(Relationship relationship) {
        List<SectionWrapper> sections = wordMLPackage.getDocumentModel().getSections();
 
        SectPr sectionProperties = sections.get(sections.size() - 1).getSectPr();
        // There is always a section wrapper, but it might not contain a sectPr
        if (sectionProperties==null ) {
            sectionProperties = factory.createSectPr();
            wordMLPackage.getMainDocumentPart().addObject(sectionProperties);
            sections.get(sections.size() - 1).setSectPr(sectionProperties);
        }
 
        FooterReference footerReference = factory.createFooterReference();
        footerReference.setId(relationship.getId());
        footerReference.setType(HdrFtrRef.DEFAULT);
        sectionProperties.getEGHdrFtrReferences().add(footerReference);
    }
}