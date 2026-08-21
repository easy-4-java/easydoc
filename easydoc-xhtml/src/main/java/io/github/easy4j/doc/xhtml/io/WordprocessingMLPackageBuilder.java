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
package io.github.easy4j.doc.xhtml.io;

import java.io.File;
import java.net.URL;

import org.docx4j.events.EventFinished;
import org.docx4j.events.StartEvent;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.bus.event.BuildJobTypes;
import io.github.easy4j.doc.fonts.ChineseFont;
import io.github.easy4j.doc.fonts.FontMapperHolder;
import io.github.easy4j.doc.utils.PhysicalFontUtils;
import io.github.easy4j.doc.xhtml.DataMap;
import io.github.easy4j.doc.xhtml.handler.DocumentHandler;
import io.github.easy4j.doc.xhtml.handler.def.XHTMLDocumentHandler;
import io.github.easy4j.doc.xhtml.utils.XHTMLImporterUtils;
import org.jsoup.nodes.Document;

public class WordprocessingMLPackageBuilder {

	protected DocumentHandler docHandler = XHTMLDocumentHandler.getDocumentHandler();

	private static final WordprocessingMLPackageBuilder WML_PACKAGE_BUILDER = new WordprocessingMLPackageBuilder();

	/**
	 * Generate a WordprocessingMLPackageBuilder.
	 * @return the WordprocessingMLPackageBuilder
	 */
	public static WordprocessingMLPackageBuilder getWMLPackageBuilder() {
		return WML_PACKAGE_BUILDER;
	}

	protected WordprocessingMLPackageBuilder() {

	}

	/*
	 * 为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 配置中文字体;解决中文乱码问题
	 */
	public WordprocessingMLPackageBuilder configChineseFonts(WordprocessingMLPackage wmlPackage) throws Exception {
		//初始化中文字体
		PhysicalFontUtils.setWmlPackageFonts(wmlPackage);
        //返回WordprocessingMLPackage对象
      	return this;
    }

	/*
	 * 为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 配置默认字体
	 */
	public WordprocessingMLPackageBuilder configDefaultFont(WordprocessingMLPackage wmlPackage,String fontName) throws Exception {
		//设置文件默认字体
		try {
			PhysicalFontUtils.setDefaultFont(wmlPackage, fontName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //返回WordprocessingMLPackage对象
      	return this;
    }

	/*
	 * 为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 配置中文字体
	 */
	public WordprocessingMLPackageBuilder configSimSunFont(WordprocessingMLPackage wmlPackage) throws Exception {
		//初始化中文字体，解决中文乱码问题
		configChineseFonts(wmlPackage);
        //设置文件默认字体
		configDefaultFont(wmlPackage,ChineseFont.SIMSUM.getFontName());
		//返回WordprocessingMLPackage对象
		return this;
    }

	/*
	 * 获取初始化后的 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage}对象
	 * @param wmlPackage
	 * @return
	 */
	public WordprocessingMLPackage initialize(WordprocessingMLPackage wmlPackage) {

		/*MBassador<Docx4jEvent> bus = new MBassador<Docx4jEvent>();
			Docx4jEvent.setEventNotifier(bus);
		*/


		return wmlPackage;
	}

	/**
	 * Internal helper that encapsulates the lifecycle common to every build path:
	 * publish a {@link StartEvent}, run {@link XHTMLImporterUtils#handle}, then
	 * publish an {@link EventFinished} and apply the configured font mapper.
	 *
	 * <p>Keeps the four canonical buildWith*() methods thin and ensures the
	 * start/finish event pair stays in sync.</p>
	 */
	private WordprocessingMLPackage execute(BuildRequest req) throws Exception {
		StartEvent jobStartEvent = new StartEvent(req.jobType, req.wmlPackage);
		jobStartEvent.publish();
		//配置中文字体
		WordprocessingMLPackage wmlPackage = initialize(req.wmlPackage);
		//渲染WordprocessingMLPackage对象
		XHTMLImporterUtils.handle(wmlPackage, req.document, req.fragment, req.altChunk);
		//构建任务结束
		new EventFinished(jobStartEvent).publish();
		//返回WordprocessingMLPackage对象
		return FontMapperHolder.useFontMapper(wmlPackage);
	}

	/** Tiny value object capturing the inputs required by {@link #execute}. */
	private static final class BuildRequest {
		final BuildJobTypes jobType;
		final WordprocessingMLPackage wmlPackage;
		final Document document;
		final boolean fragment;
		final boolean altChunk;

		BuildRequest(BuildJobTypes jobType, WordprocessingMLPackage wmlPackage,
				Document document, boolean fragment, boolean altChunk) {
			this.jobType = jobType;
			this.wmlPackage = wmlPackage;
			this.document = document;
			this.fragment = fragment;
			this.altChunk = altChunk;
		}
	}

	public WordprocessingMLPackage buildWithDoc(Document doc, boolean altChunk) throws Exception {
		/*
		 * 	根据docx4j.properties配置文件中:
		 * 	docx4j.PageSize = A4
		 * 	docx4j.PageOrientationLandscape = true
		 * 	创建默认的WordProcessingML package
		 */
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
        //返回WordprocessingMLPackage对象
        return buildWithDoc(wmlPackage , doc, altChunk);
    }

	public WordprocessingMLPackage buildWithDoc(Document doc, boolean landscape, boolean altChunk) throws Exception {
        //返回WordprocessingMLPackage对象
        return buildWithDoc(doc, PageSizePaper.A4, landscape, altChunk);
    }

	public WordprocessingMLPackage buildWithDoc(Document doc,PageSizePaper pageSize,boolean landscape,boolean altChunk) throws Exception {
		//创建指定纸张大小和方向的WordprocessingMLPackage对象
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage(pageSize, landscape); //A4纸，
        //返回WordprocessingMLPackage对象
        return buildWithDoc(wmlPackage , doc , altChunk);
    }

	public WordprocessingMLPackage buildWithDoc(WordprocessingMLPackage wmlPackage, Document doc,boolean altChunk) throws Exception {
		return execute(new BuildRequest(BuildJobTypes.DOC, wmlPackage, doc, false, altChunk));
    }

	/*
	 * 将 {@link org.jsoup.nodes.Document} 对象转为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage}
	 */
	public WordprocessingMLPackage buildWithXhtml(File htmlFile, boolean altChunk) throws Exception {
		/*
		 * 	根据docx4j.properties配置文件中:
		 * 	docx4j.PageSize = A4
		 * 	docx4j.PageOrientationLandscape = true
		 * 	创建默认的WordProcessingML package
		 */
		WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
		//返回WordprocessingMLPackage对象
		return buildWithXhtml(wmlPackage,htmlFile, altChunk);
    }

	public WordprocessingMLPackage buildWithXhtml(File htmlFile, boolean landscape, boolean altChunk) throws Exception {
        //返回WordprocessingMLPackage对象
        return buildWithXhtml(htmlFile, PageSizePaper.A4, landscape, altChunk);
    }

	public WordprocessingMLPackage buildWithXhtml(File htmlFile, PageSizePaper pageSize, boolean landscape ,boolean altChunk) throws Exception {
		//创建指定纸张大小和方向的WordprocessingMLPackage对象
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage(pageSize, landscape); //A4纸，
        //返回WordprocessingMLPackage对象
        return buildWithXhtml(wmlPackage, htmlFile , altChunk);
    }

	public WordprocessingMLPackage buildWithXhtml(WordprocessingMLPackage wmlPackage,File htmlFile, boolean altChunk) throws Exception{
		return execute(new BuildRequest(BuildJobTypes.HTML, wmlPackage, docHandler.handle(htmlFile), false, altChunk));
    }

	/*
	 * 将 {@link org.jsoup.nodes.Document} 对象转为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage}
	 */
	public WordprocessingMLPackage buildWithXhtml(String html, boolean altChunk) throws Exception {
		/*
		 * 	根据docx4j.properties配置文件中:
		 * 	docx4j.PageSize = A4
		 * 	docx4j.PageOrientationLandscape = true
		 * 	创建默认的WordProcessingML package
		 */
  		WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
		//返回WordprocessingMLPackage对象
		return buildWithXhtml(wmlPackage,html, altChunk);
    }

	public WordprocessingMLPackage buildWithXhtml(String html, boolean landscape,boolean altChunk) throws Exception {
        //返回WordprocessingMLPackage对象
        return buildWithXhtml(html, PageSizePaper.A4, landscape, altChunk);
    }

	public WordprocessingMLPackage buildWithXhtml(String html, PageSizePaper pageSize, boolean landscape, boolean altChunk) throws Exception {
		//创建指定纸张大小和方向的WordprocessingMLPackage对象
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage(pageSize, landscape); //A4纸，
        //返回WordprocessingMLPackage对象
        return buildWithXhtml(wmlPackage, html, altChunk);
    }

	public WordprocessingMLPackage buildWithXhtml(WordprocessingMLPackage wmlPackage, String html, boolean altChunk) throws Exception {
		return execute(new BuildRequest(BuildJobTypes.HTML, wmlPackage, docHandler.handle(html , false), false, altChunk));
    }

	/*
	 * 将 {@link org.jsoup.nodes.Document} 对象转为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage}
	 */
	public WordprocessingMLPackage buildWithXhtmlFragment(String xhtml,boolean altChunk) throws Exception {
		/*
		 * 	根据docx4j.properties配置文件中:
		 * 	docx4j.PageSize = A4
		 * 	docx4j.PageOrientationLandscape = true
		 * 	创建默认的WordProcessingML package
		 */
  		WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
		//返回WordprocessingMLPackage对象
		return buildWithXhtmlFragment(wmlPackage, xhtml, altChunk);
    }

	public WordprocessingMLPackage buildWithXhtmlFragment(String html,boolean landscape, boolean altChunk) throws Exception {
        //返回WordprocessingMLPackage对象
        return buildWithXhtmlFragment(html, PageSizePaper.A4, landscape, altChunk);
    }

	public WordprocessingMLPackage buildWithXhtmlFragment(String xhtml,PageSizePaper pageSize,boolean landscape,boolean altChunk) throws Exception {
		//创建指定纸张大小和方向的WordprocessingMLPackage对象
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage(pageSize, landscape); //A4纸，
        //返回WordprocessingMLPackage对象
        return buildWithXhtmlFragment(wmlPackage, xhtml, altChunk);
    }

	public WordprocessingMLPackage buildWithXhtmlFragment(WordprocessingMLPackage wmlPackage,String xhtml, boolean altChunk) throws Exception {
		return execute(new BuildRequest(BuildJobTypes.HTML, wmlPackage, docHandler.handle(xhtml , true), true, altChunk));
    }

	/*
	 * 将 {@link org.jsoup.nodes.Document} 对象转为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage}
	 */
	public WordprocessingMLPackage buildWithURL(URL url, boolean altChunk) throws Exception {
		/*
		 * 	根据docx4j.properties配置文件中:
		 * 	docx4j.PageSize = A4
		 * 	docx4j.PageOrientationLandscape = true
		 * 	创建默认的WordProcessingML package
		 */
  		WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
		//返回WordprocessingMLPackage对象
		return buildWithURL(wmlPackage,url, altChunk);
    }

	public WordprocessingMLPackage buildWithURL(URL url, boolean landscape, boolean altChunk) throws Exception {
        //返回WordprocessingMLPackage对象
        return buildWithURL(url, PageSizePaper.A4, landscape, altChunk);
    }

	public WordprocessingMLPackage buildWithURL(URL url,PageSizePaper pageSize,boolean landscape, boolean altChunk) throws Exception {
		//创建指定纸张大小和方向的WordprocessingMLPackage对象
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage(pageSize, landscape); //A4纸，
        //返回WordprocessingMLPackage对象
        return buildWithURL(wmlPackage, url, altChunk);
    }

	public WordprocessingMLPackage buildWithURL(WordprocessingMLPackage wmlPackage, URL url, boolean altChunk) throws Exception {
		return execute(new BuildRequest(BuildJobTypes.URL, wmlPackage, docHandler.handle(url), false, altChunk));
    }

	public WordprocessingMLPackage buildWithURL(String url, DataMap dataMap, boolean altChunk) throws Exception {
		/*
		 * 	根据docx4j.properties配置文件中:
		 * 	docx4j.PageSize = A4
		 * 	docx4j.PageOrientationLandscape = true
		 * 	创建默认的WordProcessingML package
		 */
  		WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage();
		//返回WordprocessingMLPackage对象
		return buildWithURL(wmlPackage, url, dataMap, altChunk);
    }

	public WordprocessingMLPackage buildWithURL(String url, DataMap dataMap, boolean landscape, boolean altChunk) throws Exception {
        //返回WordprocessingMLPackage对象
        return buildWithURL(url, dataMap, PageSizePaper.A4, landscape, altChunk);
    }

	public WordprocessingMLPackage buildWithURL(String url, DataMap dataMap, PageSizePaper pageSize, boolean landscape, boolean altChunk) throws Exception {
		//创建指定纸张大小和方向的WordprocessingMLPackage对象
        WordprocessingMLPackage wmlPackage = WordprocessingMLPackage.createPackage(pageSize, landscape); //A4纸，
        //返回WordprocessingMLPackage对象
        return buildWithURL(wmlPackage, url, dataMap, altChunk);
    }

	public WordprocessingMLPackage buildWithURL(WordprocessingMLPackage wmlPackage, String url, DataMap dataMap, boolean altChunk) throws Exception {
		return execute(new BuildRequest(BuildJobTypes.URL, wmlPackage, docHandler.handle(url, dataMap), false, altChunk));
    }

	// -------------------------------------------------------------------------
	// Deprecated forwarders. Each old buildWhith* method delegates to its
	// buildWith* counterpart and is scheduled for removal in a future release.
	// -------------------------------------------------------------------------

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithDoc(Document doc, boolean altChunk) throws Exception {
		return buildWithDoc(doc, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithDoc(Document doc, boolean landscape, boolean altChunk) throws Exception {
		return buildWithDoc(doc, landscape, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithDoc(Document doc, PageSizePaper pageSize, boolean landscape, boolean altChunk) throws Exception {
		return buildWithDoc(doc, pageSize, landscape, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithDoc(WordprocessingMLPackage wmlPackage, Document doc, boolean altChunk) throws Exception {
		return buildWithDoc(wmlPackage, doc, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithXhtml(File htmlFile, boolean altChunk) throws Exception {
		return buildWithXhtml(htmlFile, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithXhtml(File htmlFile, boolean landscape, boolean altChunk) throws Exception {
		return buildWithXhtml(htmlFile, landscape, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithXhtml(File htmlFile, PageSizePaper pageSize, boolean landscape, boolean altChunk) throws Exception {
		return buildWithXhtml(htmlFile, pageSize, landscape, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithXhtml(WordprocessingMLPackage wmlPackage, File htmlFile, boolean altChunk) throws Exception {
		return buildWithXhtml(wmlPackage, htmlFile, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithXhtml(String html, boolean altChunk) throws Exception {
		return buildWithXhtml(html, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithXhtml(String html, boolean landscape, boolean altChunk) throws Exception {
		return buildWithXhtml(html, landscape, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithXhtml(String html, PageSizePaper pageSize, boolean landscape, boolean altChunk) throws Exception {
		return buildWithXhtml(html, pageSize, landscape, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithXhtml(WordprocessingMLPackage wmlPackage, String html, boolean altChunk) throws Exception {
		return buildWithXhtml(wmlPackage, html, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithXhtmlFragment(String xhtml, boolean altChunk) throws Exception {
		return buildWithXhtmlFragment(xhtml, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithXhtmlFragment(String html, boolean landscape, boolean altChunk) throws Exception {
		return buildWithXhtmlFragment(html, landscape, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithXhtmlFragment(String xhtml, PageSizePaper pageSize, boolean landscape, boolean altChunk) throws Exception {
		return buildWithXhtmlFragment(xhtml, pageSize, landscape, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithXhtmlFragment(WordprocessingMLPackage wmlPackage, String xhtml, boolean altChunk) throws Exception {
		return buildWithXhtmlFragment(wmlPackage, xhtml, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithURL(URL url, boolean altChunk) throws Exception {
		return buildWithURL(url, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithURL(URL url, boolean landscape, boolean altChunk) throws Exception {
		return buildWithURL(url, landscape, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithURL(URL url, PageSizePaper pageSize, boolean landscape, boolean altChunk) throws Exception {
		return buildWithURL(url, pageSize, landscape, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithURL(WordprocessingMLPackage wmlPackage, URL url, boolean altChunk) throws Exception {
		return buildWithURL(wmlPackage, url, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithURL(String url, DataMap dataMap, boolean altChunk) throws Exception {
		return buildWithURL(url, dataMap, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithURL(String url, DataMap dataMap, boolean landscape, boolean altChunk) throws Exception {
		return buildWithURL(url, dataMap, landscape, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithURL(String url, DataMap dataMap, PageSizePaper pageSize, boolean landscape, boolean altChunk) throws Exception {
		return buildWithURL(url, dataMap, pageSize, landscape, altChunk);
	}

	@Deprecated(since = "3.0.x", forRemoval = true)
	public WordprocessingMLPackage buildWhithURL(WordprocessingMLPackage wmlPackage, String url, DataMap dataMap, boolean altChunk) throws Exception {
		return buildWithURL(wmlPackage, url, dataMap, altChunk);
	}

}