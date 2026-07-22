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

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
/**
 * 
 * @className	： AddingAnInlineImage
 * @description	： 添加图片
 * @author 		： <a href="https://github.com/easy-4-java">hiwepy</a>
 * @date		： 2017年5月24日 下午10:33:21
 * @version 	V1.0
 */
public class AddingAnInlineImage {
    /**
     *  像往常一样, 我们创建了一个包(package)来容纳文档.
     *  然后我们创建了一个指向将要添加到文档的图片的文件对象.为了能够对图片做一些操作, 我们将它转换
     *  为字节数组. 最后我们将图片添加到包中并保存这个包(package).
     */
    public static void main (String[] args) throws Exception {
        WordprocessingMLPackage  wordMLPackage = WordprocessingMLPackage.createPackage();
 
        File file = new File("src/main/resources/iProfsLogo.png");
        byte[] bytes = convertImageToByteArray(file);
        addImageToPackage(wordMLPackage, bytes);
 
        wordMLPackage.save(new java.io.File("src/main/files/HelloWord7.docx"));
    }
 
    /**
     *  Docx4j拥有一个由字节数组创建图片部件的工具方法, 随后将其添加到给定的包中. 为了能将图片添加
     *  到一个段落中, 我们需要将图片转换成内联对象. 这也有一个方法, 方法需要文件名提示, 替换文本, 
     *  两个id标识符和一个是嵌入还是链接到的指示作为参数.
     *  一个id用于文档中绘图对象不可见的属性, 另一个id用于图片本身不可见的绘制属性. 最后我们将内联
     *  对象添加到段落中并将段落添加到包的主文档部件.
     *
     *  @param wordMLPackage 要添加图片的包
     *  @param bytes         图片对应的字节数组
     *  @throws Exception    不幸的createImageInline方法抛出一个异常(没有更多具体的异常类型)
     */
    private static void addImageToPackage(WordprocessingMLPackage wordMLPackage,
                            byte[] bytes) throws Exception {
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
 
        int docPrId = 1;
        int cNvPrId = 2;
        Inline inline = imagePart.createImageInline("Filename hint","Alternative text", docPrId, cNvPrId, false);
 
        P paragraph = addInlineImageToParagraph(inline);
 
        wordMLPackage.getMainDocumentPart().addObject(paragraph);
    }
 
    /**
     *  创建一个对象工厂并用它创建一个段落和一个可运行块R.
     *  然后将可运行块添加到段落中. 接下来创建一个图画并将其添加到可运行块R中. 最后我们将内联
     *  对象添加到图画中并返回段落对象.
     *
     * @param   inline 包含图片的内联对象.
     * @return  包含图片的段落
     */
    private static P addInlineImageToParagraph(Inline inline) {
        // 添加内联对象到一个段落中
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
     * 将图片从文件对象转换成字节数组.
     * 
     * @param file  将要转换的文件
     * @return      包含图片字节数据的字节数组
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static byte[] convertImageToByteArray(File file)
            throws FileNotFoundException, IOException {
        InputStream is = new FileInputStream(file );
        long length = file.length();
        // 不能使用long类型创建数组, 需要用int类型.
        if (length > Integer.MAX_VALUE) {
            System.out.println("File too large!!");
        }
        byte[] bytes = new byte[(int)length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        // 确认所有的字节都没读取
        if (offset < bytes.length) {
            System.out.println("Could not completely read file " +file.getName());
        }
        is.close();
        return bytes;
    }
}