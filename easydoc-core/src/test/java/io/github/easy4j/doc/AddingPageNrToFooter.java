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
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.Br;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Text;

/**
 * @className	： AddingPageNrToFooter
 * @description	： 在页脚添加页码
	我在本博客要讨论的最后一个特性是怎样在我们刚创建的页脚处添加页码。你可能已经知道，在Word中页码是一个域，因此本例或多或少是前面的两个例子的结合：目录表和页脚。
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @date		： 2017年5月24日 下午10:35:04
 * @version 	V1.0
 */
public class AddingPageNrToFooter {
	
    private static WordprocessingMLPackage wordMLPackage;
    private static ObjectFactory factory;
 
    /**
     *  First we create the package and the factory. Then we create the footer.
     *  Finally we add two pages with text to the document and save it.
     */
    public static void main (String[] args) throws Exception {
        
    	wordMLPackage = WordprocessingMLPackage.createPackage();
        factory = Context.getWmlObjectFactory();
 
        Relationship relationship = createFooterPart();
        createFooterReference(relationship);
 
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
 
        documentPart.addParagraphOfText("Hello World!");
 
        addPageBreak(documentPart);
 
        documentPart.addParagraphOfText("This is page 2!");
        wordMLPackage.save(new File("src/main/files/HelloWord15.docx") );
    }
 
    /**
     *  As in the previous example, this method creates a footer part and adds it to
     *  the main document and then returns the corresponding relationship.
     *
     *  @return
     *  @throws InvalidFormatException
     */
    private static Relationship createFooterPart() throws InvalidFormatException {
        FooterPart footerPart = new FooterPart();
        footerPart.setPackage(wordMLPackage);
 
        footerPart.setJaxbElement(createFooterWithPageNr());
 
        return wordMLPackage.getMainDocumentPart().addTargetPart(footerPart);
    }
 
    /**
     *  As in the previous example, we create a footer and a paragraph object. But
     *  this time, instead of adding text to a run, we add a field. And just as with
     *  the table of content, we have to add a begin and end character around the
     *  actual field with the page number. Finally we add the paragraph to the
     *  content of the footer and then return it.
     *
     * @return
     */
    public static Ftr createFooterWithPageNr() {
        Ftr ftr = factory.createFtr();
        P paragraph = factory.createP();
 
        addFieldBegin(paragraph);
        addPageNumberField(paragraph);
        addFieldEnd(paragraph);
 
        ftr.getContent().add(paragraph);
        return ftr;
    }
 
    /**
     *  Creating the page number field is nearly the same as creating the field in
     *  the TOC example. The only difference is in the value. We use the PAGE
     *  command, which prints the number of the current page, together with the
     *  MERGEFORMAT switch, which indicates that the current formatting should be
     *  preserved when the field is updated.
     *
     * @param paragraph
     */
    private static void addPageNumberField(P paragraph) {
        R run = factory.createR();
        Text txt = new Text();
        txt.setSpace("preserve");
        txt.setValue(" PAGE   \\* MERGEFORMAT ");
        run.getContent().add(factory.createRInstrText(txt));
        paragraph.getContent().add(run);
    }
 
    /**
     * Every fields needs to be delimited by complex field characters. This method
     * adds the delimiter that precedes the actual field to the given paragraph.
     * @param paragraph
     */
    private static void addFieldBegin(P paragraph) {
        R run = factory.createR();
        FldChar fldchar = factory.createFldChar();
        fldchar.setFldCharType(STFldCharType.BEGIN);
        run.getContent().add(fldchar);
        paragraph.getContent().add(run);
    }
 
    /**
     * Every fields needs to be delimited by complex field characters. This method
     * adds the delimiter that follows the actual field to the given paragraph.
     * @param paragraph
     */
    private static void addFieldEnd(P paragraph) {
        FldChar fldcharend = factory.createFldChar();
        fldcharend.setFldCharType(STFldCharType.END);
        R run3 = factory.createR();
        run3.getContent().add(fldcharend);
        paragraph.getContent().add(run3);
    }
 
    /**
     * This method fetches the document final section properties, and adds a newly
     * created footer reference to them.
     *
     * @param relationship
     */
    public static void createFooterReference(Relationship relationship){
 
        List<SectionWrapper> sections =
            wordMLPackage.getDocumentModel().getSections();
 
        SectPr sectPr = sections.get(sections.size() - 1).getSectPr();
        // There is always a section wrapper, but it might not contain a sectPr
        if (sectPr==null ) {
            sectPr = factory.createSectPr();
            wordMLPackage.getMainDocumentPart().addObject(sectPr);
            sections.get(sections.size() - 1).setSectPr(sectPr);
        }
 
        FooterReference footerReference = factory.createFooterReference();
        footerReference.setId(relationship.getId());
        footerReference.setType(HdrFtrRef.DEFAULT);
        sectPr.getEGHdrFtrReferences().add(footerReference);
    }
 
    /**
     * Adds a page break to the document.
     *
     * @param documentPart
     */
    private static void addPageBreak(MainDocumentPart documentPart) {
        Br breakObj = new Br();
        breakObj.setType(STBrType.PAGE);
        P paragraph = factory.createP();
        paragraph.getContent().add(breakObj);
        documentPart.getJaxbElement().getBody().getContent().add(paragraph);
    }
}