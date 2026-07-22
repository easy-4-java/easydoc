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
import java.io.FileOutputStream;  
import java.util.Map.Entry;  
  
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;  
import org.docx4j.openpackaging.parts.Part;  
import org.docx4j.openpackaging.parts.PartName;  
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPart;  
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;  
 
/**
 * http://53873039oycg.iteye.com/blog/2191406
 */
public class Docx4j_SaveDocxImg_S3_Test {  
    public static void main(String[] args) throws Exception {  
        Docx4j_SaveDocxImg_S3_Test t = new Docx4j_SaveDocxImg_S3_Test();  
        t.saveDocxImg("f:/saveFile/temp/word_docx4j_img_125.docx",  
                "f:/saveFile/temp/docx4j_");  
    }  
  
    /** 
     * @Description: 提取word图片 
     */  
    public void saveDocxImg(String filePath, String savePath) throws Exception {  
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage  
                .load(new File(filePath));  
        for (Entry<PartName, Part> entry : wordMLPackage.getParts().getParts()  
                .entrySet()) {  
            if (entry.getValue() instanceof BinaryPartAbstractImage) {  
                BinaryPartAbstractImage binImg = (BinaryPartAbstractImage) entry  
                        .getValue();  
                // 图片minetype  
                String imgContentType = binImg.getContentType();  
                PartName pt = binImg.getPartName();  
                String fileName = null;  
                if (pt.getName().indexOf("word/media/") != -1) {  
                    fileName = pt.getName().substring(  
                            pt.getName().indexOf("word/media/")  
                                    + "word/media/".length());  
                }  
                System.out.println(String.format("mimetype=%s,filePath=%s",  
                        imgContentType, pt.getName()));  
                FileOutputStream fos = new FileOutputStream(savePath + fileName);  
                ((BinaryPart) entry.getValue()).writeDataToOutputStream(fos);  
                fos.close();  
            }  
        }  
    }  
}  
