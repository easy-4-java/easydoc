/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package io.github.easy4j.doc;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jakarta.xml.bind.JAXBException;

import org.docx4j.XmlUtils;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class generates a sample document in case no
 *  document has been passed.
 */
public class SampleDocument {

	private static final Logger LOG = LoggerFactory.getLogger(SampleDocument.class);
    
	public static void createContent(MainDocumentPart wordDocumentPart ) {
		/*
		 * NB, this currently works nicely with
		 * viaIText, and viaXSLFO (provided
		 * you view with Acrobat Reader .. it 
		 * seems to overwhelm pdfviewer, which
		 * is weird, since viaIText works in both).
		 */
		
		try {
			// Do this explicitly, since we need
			// it in order to create our content
			PhysicalFonts.discoverPhysicalFonts(); 						
															
			Map<String, PhysicalFont> physicalFontMap = PhysicalFonts.getPhysicalFonts();			
			Iterator physicalFontMapIterator = physicalFontMap.entrySet().iterator();
			while (physicalFontMapIterator.hasNext()) {
			    Map.Entry pairs = (Map.Entry)physicalFontMapIterator.next();
			    if(pairs.getKey()==null) {
			    	pairs = (Map.Entry)physicalFontMapIterator.next();
			    }
			    String fontName = (String)pairs.getKey();
			    PhysicalFont pf = (PhysicalFont)pairs.getValue();
			    
			    LOG.debug("Added paragraph for " + fontName);
			    addObject(wordDocumentPart, sampleText, fontName );
	
			    // bold, italic etc
			    PhysicalFont pfVariation = PhysicalFonts.getBoldForm(pf);
			    if (pfVariation!=null) {
				    addObject(wordDocumentPart, sampleTextBold, pfVariation.getName() );
			    }
			    pfVariation = PhysicalFonts.getBoldItalicForm(pf);
			    if (pfVariation!=null) {
				    addObject(wordDocumentPart, sampleTextBoldItalic, pfVariation.getName() );
			    }
			    pfVariation = PhysicalFonts.getItalicForm(pf);
			    if (pfVariation!=null) {
				    addObject(wordDocumentPart, sampleTextItalic, pfVariation.getName() );
			    }
			    
			}
		} catch (Throwable e) {
			// 捕获 Throwable（含 AssertionError）：macOS 上 FOP 字体解析
			// 会对特定系统字体抛 AssertionError，不能让其中断文档生成。
			// 日志消息须与实际失败场景一致：这里是字体枚举/示例段落生成失败
			LOG.warn("Failed to create sample document content for available fonts", e);
		}    		    
		
	}
	
	static void addObject(MainDocumentPart wordDocumentPart, String template, String fontName ) throws JAXBException {
		
	    HashMap substitution = new HashMap();
	    substitution.put("fontname", fontName);
	    Object o = XmlUtils.unmarshallFromTemplate(template, substitution);
	    wordDocumentPart.addObject(o);    		    
		
	}
	
	final static String sampleText = "<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
		+"<w:r>"
		+"<w:rPr>"
			+"<w:rFonts w:ascii=\"${fontname}\" w:eastAsia=\"${fontname}\" w:hAnsi=\"${fontname}\" w:cs=\"${fontname}\" />"
		+"</w:rPr>"
		+"<w:t xml:space=\"preserve\">${fontname}</w:t>"
	+"</w:r>"
	+"</w:p>";
	final static String sampleTextBold =	"<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"	+"<w:r>"
		+"<w:rPr>"
			+"<w:rFonts w:ascii=\"${fontname}\" w:eastAsia=\"${fontname}\" w:hAnsi=\"${fontname}\" w:cs=\"${fontname}\" />"
			+"<w:b />"
		+"</w:rPr>"
		+"<w:t>${fontname} bold;</w:t>"
	+"</w:r>"
	+"</w:p>";
	final static String sampleTextItalic =	"<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"	+"<w:r>"
		+"<w:rPr>"
			+"<w:rFonts w:ascii=\"${fontname}\" w:eastAsia=\"${fontname}\" w:hAnsi=\"${fontname}\" w:cs=\"${fontname}\" />"
			+"<w:i />"
		+"</w:rPr>"
		+"<w:t>${fontname} italic; </w:t>"
	+"</w:r>"
	+"</w:p>";
	final static String sampleTextBoldItalic ="<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
		+"<w:r>"
		+"<w:rPr>"
			+"<w:rFonts w:ascii=\"${fontname}\" w:eastAsia=\"${fontname}\" w:hAnsi=\"${fontname}\" w:cs=\"${fontname}\" />"
			+"<w:b />"
			+"<w:i />"
		+"</w:rPr>"
		+"<w:t>${fontname} bold italic</w:t>"
	+"</w:r>"
	+"</w:p>";

}