/**
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.CTMarkupRange;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Document;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WML package utilities.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WMLPackageUtils {

	private static final Logger LOG = LoggerFactory.getLogger(WMLPackageUtils.class);

	private static final ObjectFactory FACTORY = Context.getWmlObjectFactory();

    /**
     * cleanDocumentPart
     * @param documentPart
     */
    public static boolean cleanDocumentPart(MainDocumentPart documentPart) throws Exception {
        if (documentPart == null) {
            return false;
        }
        Document document = documentPart.getContents();
        String wmlTemplate = XmlUtils.marshaltoString(document, true, false, Context.jc);
        document = (Document) XmlUtils.unwrap(DocxVariableClearUtils.doCleanDocumentPart(wmlTemplate, Context.jc));
        documentPart.setContents(document);
        return true;
    }

	/*
	 * 例如你动态设置一个文档的标题。首先，在前面创建的模版文档中添加一个自定义占位符，我使用SJ_EX1作为占位符，我们将要用name参数来替换这个值。
	 * 在docx4j中基本的文本元素用org.docx4j.wml.Text类来表示，替换这个简单的占位符我们需要做的就是调用这个方法：
	 */
	public static void replacePlaceholder(MainDocumentPart documentPart, String placeholder, String content ) {
		// 获取文档对象中所有的Text类型对象
	    List<Text> texts = WmlElementUtils.getTargetElements(documentPart, Text.class);
	   // 循环Text类型对象集合
	    for (Text text : texts) {
	        Text textElement = (Text) text;
	        if (textElement.getValue().equals(placeholder)) {
	            textElement.setValue(content);
	        }
	    }
	}

	/*
	 *  向模版文档添加段落
		你可能想知道为什么我们需要添加段落？我们已经可以添加文本，难道段落不就是一大段的文本吗？好吧，既是也不是，一个段落确实看起来像是一大段文本，但你需要考虑的是换行符，如果你像前面一样添加一个Text元素并且在文本中添加换行符，它们并不会出现，当你想要换行符时，你就需要创建一个新的段落。然而，幸运的是这对于Docx4j来说也非常地容易。
		做这个需要下面的几步：

		    从模版中找到要替换的段落
		    将输入文本拆分成单独的行
		    每一行基于模版中的段落创建一个新的段落
		    移除原来的段落

	 * ******************************************************************
	 */
	public static void replaceParagraph(MainDocumentPart documentPart, String placeholder, String textToAdd, ContentAccessor addTo) {
        // 1. get the paragraph
        List<P> paragraphs = WmlElementUtils.getTargetElements(documentPart, P.class);
        P toReplace = null;
        for (P p : paragraphs) {
            List<Text> texts = WmlElementUtils.getTargetElements(p, Text.class);
            for (Text t : texts) {
                Text content = (Text) t;
                if (content.getValue().equals(placeholder)) {
                    toReplace = (P) p;
                    break;
                }
            }
        }

        // we now have the paragraph that contains our placeholder: toReplace
        // 2. split into seperate lines
        String as[] = StringUtils.splitPreserveAllTokens(textToAdd, '\n');

        for (int i = 0; i < as.length; i++) {
            String ptext = as[i];

            // 3. copy the found paragraph to keep styling correct
            P copy = (P) XmlUtils.deepCopy(toReplace);

            // replace the text elements from the copy
            List<?> texts = WmlElementUtils.getTargetElements(copy, Text.class);
            if (texts.size() > 0) {
                Text textToReplace = (Text) texts.get(0);
                textToReplace.setValue(ptext);
            }

            // add the paragraph to the document
            addTo.getContent().add(copy);
        }

        // 4. remove the original one
        ((ContentAccessor)toReplace.getParent()).getContent().remove(toReplace);

    }

	/**
	 * 在标签处插入内容
	 *
	 * @param bm
	 * @param object
	 * @throws Exception
	 */
	public static void replaceText(CTBookmark bm, Object object) throws Exception {
		if (object == null) {
			return;
		}
		// do we have data for this one?
		if (bm.getName() == null)
			return;
		String value = object.toString();
		try {
			// Can't just remove the object from the parent,
			// since in the parent, it may be wrapped in a JAXBElement
			List<Object> theList = null;
			ParaRPr rpr = null;
			if (bm.getParent() instanceof P) {
				PPr pprTemp = ((P) (bm.getParent())).getPPr();
				if (pprTemp == null) {
					rpr = null;
				} else {
					rpr = ((P) (bm.getParent())).getPPr().getRPr();
				}
				theList = ((ContentAccessor) (bm.getParent())).getContent();
			} else {
				return;
			}
			int rangeStart = -1;
			int rangeEnd = -1;
			int i = 0;
			for (Object ox : theList) {
				Object listEntry = XmlUtils.unwrap(ox);
				if (listEntry.equals(bm)) {

					if (((CTBookmark) listEntry).getName() != null) {

						rangeStart = i + 1;

					}
				} else if (listEntry instanceof CTMarkupRange) {
					if (((CTMarkupRange) listEntry).getId().equals(bm.getId())) {
						rangeEnd = i - 1;

						break;
					}
				}
				i++;
			}
			int x = i - 1;
			// Delete the bookmark range
			for (int j = x; j >= rangeStart; j--) {
				theList.remove(j);
			}
			// now add a run
			org.docx4j.wml.R run = FACTORY.createR();
			org.docx4j.wml.Text t = FACTORY.createText();
			t.setValue(value);
			run.getContent().add(t);

			theList.add(rangeStart, run);
		} catch (ClassCastException cce) {
			// 不再静默吞掉类型转换失败：此处期望书签父节点为 P/ContentAccessor 结构，
			// 记录 WARN 并带上具体类型信息，便于排查结构异常的模板文档
			LOG.warn("Unexpected class while replacing bookmark '{}' text: expected P/ContentAccessor structure; details: {}",
					bm.getName(), cce.getMessage(), cce);
		}
	}

	/**
     * 将图片从文件对象转换成字节数组.
     * @param file  将要转换的文件
     * @return      包含图片字节数据的字节数组
     * @throws FileNotFoundException
     * @throws IOException
     */
	public static byte[] imageToByteArray(File file) throws FileNotFoundException, IOException {
        long length = file.length();
        // 不能使用long类型创建数组, 需要用int类型.
        if (length > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("File too large: " + length);
        }
        byte[] bytes = new byte[(int)length];
        // try-with-resources：读取抛异常时也能保证流被关闭
        try (InputStream is = new FileInputStream(file)) {
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确认所有的字节都没读取
            if (offset < bytes.length) {
                LOG.debug("Could not completely read file " + file.getName());
            }
        }
        return bytes;
    }

}
