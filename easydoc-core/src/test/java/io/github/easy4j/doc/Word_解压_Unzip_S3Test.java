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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io3.Load3;
import org.docx4j.openpackaging.io3.Save;
import org.docx4j.openpackaging.io3.stores.UnzippedPartStore;
import org.docx4j.openpackaging.io3.stores.ZipPartStore;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;   
  
public class Word_解压_Unzip_S3Test {   
    public static void main(String[] args) throws Exception {   
        Word_解压_Unzip_S3Test t = new Word_解压_Unzip_S3Test();   
        //t.unzipWord("f:/saveFile/temp/test_t.docx","f:/saveFile/temp/Unzip_3");   
        t.unzipWord("f:/saveFile/temp/img_word.docx","f:/saveFile/temp/Unzip_8");   
        //t.zipXml("f:/saveFile/temp/Unzip_2", "f:/saveFile/temp/test_t2.docx");   
    }   
  
    public void unzipWord(String fileName,String outFilePath) throws Exception {   
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File(fileName));   
        File baseDir = new File(outFilePath);   
        baseDir.mkdir();   
        UnzippedPartStore ups = new UnzippedPartStore(baseDir);   
        ups.setSourcePartStore(wordMLPackage.getSourcePartStore());   
        Save saver = new Save(wordMLPackage, ups);   
        saver.save(null);   
    }   
  
    public void zipXml(String inputfilepath, String outFilePath)   
            throws Exception {   
        System.out.println(inputfilepath);   
  
        // Load the docx   
        File baseDir = new File(inputfilepath);   
        UnzippedPartStore partLoader = new UnzippedPartStore(baseDir);   
        final Load3 loader = new Load3(partLoader);   
        OpcPackage opc = loader.get();   
  
        // Save it zipped   
        ZipPartStore zps = new ZipPartStore();   
        zps.setSourcePartStore(opc.getSourcePartStore());   
        Save saver = new Save(opc, zps);   
        FileOutputStream fos = null;   
        try {   
            fos = new FileOutputStream(new File(outFilePath));   
            saver.save(fos);   
        } catch (FileNotFoundException e) {   
            throw new Docx4JException("Couldn't save " + outFilePath, e);   
        } finally {   
            IOUtils.closeQuietly(fos);   
        }   
    }   
} 