package io.github.easy4j.doc.xhtml;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.Docx4jProperties;
import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import io.github.easy4j.doc.Docx4jConstants;
import io.github.easy4j.doc.utils.PhysicalFontUtils;
import org.docx4j.wml.Body;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.Text;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jvnet.jaxb2_commons.ppp.Child;


public class HtmlToDOCDemo {
	
	private static void findRichTextNode(Map<String, List<Object>> richTextMap,Object object, ContentAccessor accessor) {
		Object textObj = XmlUtils.unwrap(object);
		if (textObj instanceof Text) {
			String text = ((Text) textObj).getValue();
			if (StringUtils.isNotEmpty(text)) {
				text = text.trim();
				if (text.startsWith("$RH{") && text.endsWith("}")) {
					String textTag = text.substring("$RH{".length(),
							text.length() - 1);
					if (StringUtils.isNotEmpty(textTag) && (accessor != null)) {
						if (richTextMap.containsKey(textTag)) {
							richTextMap.get(textTag).add(accessor);
						} else {
							List<Object> objList = new ArrayList<Object>();
							objList.add(accessor);
							richTextMap.put(textTag, objList);

						}
					}
				}
			}
		} else if (object instanceof ContentAccessor) {
			List<Object> objList = ((ContentAccessor) object).getContent();
			for (int i = 0, iSize = objList.size(); i < iSize; i++) {
				findRichTextNode(richTextMap, objList.get(i), (ContentAccessor) object);
			}
		}
	}

	private static List<Object> convertToWmlObject(
			WordprocessingMLPackage wordMLPackage, String content)
			throws Docx4JException, JAXBException {
		MainDocumentPart document = wordMLPackage.getMainDocumentPart();
		//获取Jsoup参数
		String charsetName = Docx4jProperties.getProperty(Docx4jConstants.DOCX4J_CONVERT_OUT_WMLTEMPLATE_CHARSETNAME, Docx4jConstants.DEFAULT_CHARSETNAME );
		
		List<Object> wmlObjList = null;
		String templateString = XmlUtils.marshaltoString(document.getContents().getBody());
		System.out.println(templateString);
		Body templateBody = document.getContents().getBody();
		try {
			document.getContents().setBody(XmlUtils.deepCopy(templateBody));
			document.getContent().clear();
			Document doc = Jsoup.parse(content);
			doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml).escapeMode(Entities.EscapeMode.xhtml);
			//XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
			
			AlternativeFormatInputPart  part = document.addAltChunk(AltChunkType.Xhtml,doc.html().getBytes(Charset.forName(charsetName)));
			
			// convertAltChunks: docx4j 8 返回 void，docx4j 11+ 返回 Package；统一就地转换
			document.convertAltChunks();
			File file = new File("d://temp.docx");
			wordMLPackage.save(file);
			wmlObjList = document.getContent();
			//part.getOwningRelationshipPart().getSourceP().get
			//wmlObjList = xhtmlImporter.convert(doc.html(), doc.baseUri());
		} finally {
			document.getContents().setBody(templateBody);
		}
		return wmlObjList;
	}

	public static PPr getWmlObjectPpr(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof P) {
			return ((P) object).getPPr();
		} else if (object instanceof Child) {
			return getWmlObjectPpr(((Child) object).getParent());
		} else {
			return null;
		}
		//XmlUtils.marshaltoString(object)
		//Context.getWmlObjectFactory().
		//((CTAltChunk)object).getAltChunkPr().g
	}

	public static void setWmlPprSetting(Object source, List<Object> targetList) {
		PPr sourcePpr = getWmlObjectPpr(source);
		if (sourcePpr != null) {
			for (int i = 0, iSize = targetList.size(); i < iSize; i++) {
				PPr ppr = getWmlObjectPpr(targetList.get(i));
				if (ppr != null) {
					ppr.setInd(sourcePpr.getInd());
					ppr.setSpacing(sourcePpr.getSpacing());
				}
			}
		}
		//sourcePpr.getSpacing()
	}

	public static void replaceRichText(WordprocessingMLPackage wordMLPackage, Map<String, String> richTextMap) throws Docx4JException, JAXBException {
		MainDocumentPart document = wordMLPackage.getMainDocumentPart();
		Map<String, List<Object>> textNodeMap = new HashMap<String, List<Object>>();
		findRichTextNode(textNodeMap, document.getContents().getBody(), null);
		Iterator<String> iterator = richTextMap.keySet().iterator();
		while (iterator.hasNext()) {
			String textTag = iterator.next();
			List<Object> textNodeList = textNodeMap.get(textTag);
			if (textNodeList != null && richTextMap.containsKey(textTag)) {
				List<Object> textObjList = convertToWmlObject(wordMLPackage, richTextMap.get(textTag));
				for (int i = 0, iSize = textNodeList.size(); i < iSize; i++) {
					Object nodeObject = textNodeList.get(i);
					if (nodeObject != null) {
						//setWmlPprSetting(textNodeList.get(i), textObjList);
						TraversalUtil.replaceChildren(nodeObject , textObjList);
					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		
		
		// 加载模板
		InputStream template = HtmlToDOCDemo.class.getResourceAsStream("kcjj.xml");
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(template);
		PhysicalFontUtils.setSimSunFont(wordMLPackage);
		// 加载Html数据
		String jxdg = IOUtils.toString(HtmlToDOCDemo.class.getResourceAsStream("data.txt"));
		Map<String, String> richTextMap = new HashMap<String, String>();
		richTextMap.put("jxdg", jxdg);

		replaceRichText(wordMLPackage, richTextMap);
		
		File file = new File("d://result4.docx");
		wordMLPackage.save(file);
		System.out.println("success");

	}
}
