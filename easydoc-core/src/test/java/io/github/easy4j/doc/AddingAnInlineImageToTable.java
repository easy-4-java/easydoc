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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import io.github.easy4j.doc.utils.BorderUtils;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

public class AddingAnInlineImageToTable {
    private static WordprocessingMLPackage  wordMLPackage;
    private static ObjectFactory factory;
 
    /**
     *  首先我们创建包和对象工厂, 因此在类的随处我们都可以使用它们. 然后我们创建一个表格并添加
     *  边框. 接下来我们创建一个表格行并在第一个域添加一些文本.
     *  对于第二个域, 我们用与前面一样的图片创建一个段落并添加进去. 最后把行添加到表格中, 并将
     *  表格添加到包中, 然后保存这个包.
     */
    public static void main (String[] args) throws Exception {
        wordMLPackage = WordprocessingMLPackage.createPackage();
        factory = Context.getWmlObjectFactory();
 
        Tbl table = factory.createTbl();
        addBorders(table);
 
        Tr tr = factory.createTr();
 
        P paragraphOfText = wordMLPackage.getMainDocumentPart().createParagraphOfText("Field 1");
        addTableCell(tr, paragraphOfText);
 
        File file = new File("src/main/resources/iProfsLogo.png");
        P paragraphWithImage = addInlineImageToParagraph(createInlineImage(file));
        addTableCell(tr, paragraphWithImage);
 
        table.getContent().add(tr);
 
        wordMLPackage.getMainDocumentPart().addObject(table);
        wordMLPackage.save(new java.io.File("src/main/files/HelloWord8.docx"));
    }
 
    /**
     * 用给定的段落作为内容向给定的行中添加一个单元格.
     *
     * @param tr
     * @param paragraph
     */
    private static void addTableCell(Tr tr, P paragraph) {
        Tc tc1 = factory.createTc();
        tc1.getContent().add(paragraph);
        tr.getContent().add(tc1);
    }
 
    /**
     *  向新的段落中添加内联图片并返回这个段落.
     *  这个方法与前面例子中的方法没有区别.
     * @param inline
     * @return
     */
    private static P addInlineImageToParagraph(Inline inline) {
        // Now add the in-line image to a paragraph
        ObjectFactory factory = new ObjectFactory();
        P paragraph = factory.createP();
        R run = factory.createR();
        paragraph.getContent().add(run);
        Drawing drawing = factory.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);
        return paragraph;
    }
 
    /**
     * 使用给定的文件创建一个内联图片.
     * 跟前面例子中一样, 我们将文件转换成字节数组, 并用它创建一个内联图片.
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static Inline createInlineImage(File file) throws Exception {
        byte[] bytes = convertImageToByteArray(file);
 
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
 
        int docPrId = 1;
        int cNvPrId = 2;
 
        return imagePart.createImageInline("Filename hint", "Alternative text", docPrId, cNvPrId, false);
    }
 
    /**
     * 将图片从文件转换成字节数组.
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static byte[] convertImageToByteArray(File file) throws FileNotFoundException, IOException {
        InputStream is = new FileInputStream(file );
        long length = file.length();
        // You cannot create an array using a long, it needs to be an int.
        if (length > Integer.MAX_VALUE) {
            System.out.println("File too large!!");
        }
        byte[] bytes = new byte[(int)length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        // Ensure all the bytes have been read
        if (offset < bytes.length) {
            System.out.println("Could not completely read file "+file.getName());
        }
        is.close();
        return bytes;
    }
 
    /**
     * 给表格添加简单的黑色边框.
     * @param table
     */
    private static void addBorders(Tbl table) {
        table.setTblPr(new TblPr());
        CTBorder border = BorderUtils.ctBorder();
        TblBorders borders = BorderUtils.tblBorders(border);
        table.getTblPr().setTblBorders(borders);
    }
    
    
}