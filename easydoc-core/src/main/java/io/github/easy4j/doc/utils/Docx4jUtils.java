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
package io.github.easy4j.doc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.contenttype.ContentTypes;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;

/**
 * Implementation of docx4j utils functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
public class Docx4jUtils {

	protected static String CONTENT_TYPE = "";
	
	/*
	 * 生成临时文件位置
	 */
	public static String getTempPath() {
		return System.getProperty("java.io.tmpdir") + File.separator + System.currentTimeMillis();
	}

    public InputStream mergeDocx(final List<InputStream> streams)  throws Docx4JException, IOException {  
      
        WordprocessingMLPackage target = null;  
        final File generated = File.createTempFile("generated", ".docx");  
      
        int chunkId = 0;  
        Iterator<InputStream> it = streams.iterator();  
        while (it.hasNext()) {  
            InputStream is = it.next();  
            if (is != null) {  
                if (target == null) {  
                    // Copy first (master) document   
                    OutputStream os = new FileOutputStream(generated);  
                    os.write(IOUtils.toByteArray(is));  
                    os.close();  
      
                    target = WordprocessingMLPackage.load(generated);  
                } else {  
                    // Attach the others (Alternative input parts)   
                    insertDocx(target.getMainDocumentPart(),  
                            IOUtils.toByteArray(is), chunkId++);  
                }  
            }  
        }  
      
        if (target != null) {  
            target.save(generated);  
            return new FileInputStream(generated);  
        } else {  
            return null;  
        }  
    }  
      
    // 插入文档   
    private void insertDocx(MainDocumentPart main, byte[] bytes, int chunkId) {  
        try {  
            AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/part" + chunkId + ".docx"));  
            // afiPart.setContentType(new ContentType(CONTENT_TYPE));
            afiPart.setContentType(new ContentType(ContentTypes.APPLICATION_XML));
            afiPart.setBinaryData(bytes);  
            Relationship altChunkRel = main.addTargetPart(afiPart);  
      
            CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();  
            chunk.setId(altChunkRel.getId());  
      
            main.addObject(chunk);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    } 
    
    public static void toP(WordprocessingMLPackage wordMLPackage,String outPath) throws Exception{
        OutputStream os = new FileOutputStream(outPath);
        FOSettings foSettings = Docx4J.createFOSettings();
        foSettings.setWmlPackage(wordMLPackage);
        Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);
    }
    
}
