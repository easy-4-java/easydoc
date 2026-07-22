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
package io.github.easy4j.doc.xhtml;

import org.docx4j.Docx4J;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.RFonts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by noah on 3/10/15.
 */
public class HtmlConverter {

    /**
     * 输出文件名
     */
    public final String OUT_FILENAME = "OUT_ConvertInXHTMLURL";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将页面保存为 docx
     *
     * @param url
     * @return
     * @throws Exception
     */
    public File saveUrlToDocx(String url) throws Exception {
        return saveDocx(url2word(url));
    }

    /**
     * 将页面保存为 pdf
     *
     * @param url
     * @return
     * @throws Exception
     */
    public File saveUrlToPdf(String url) throws Exception {
        return savePdf(url2word(url));
    }

    /**
     * 将页面转为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage}
     *
     * @param url
     * @return
     * @throws Exception
     */
    public WordprocessingMLPackage url2word(String url) throws Exception {
        return xhtml2word(url2xhtml(url));
    }

    /**
     * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 docx
     *
     * @param wordMLPackage
     * @return
     * @throws Exception
     */
    public File saveDocx(WordprocessingMLPackage wordMLPackage) throws Exception {

        File file = new File(genFilePath() + ".docx");
        wordMLPackage.save(file); //保存到 docx 文件

        if (logger.isDebugEnabled()) {
            logger.debug("Save to [.docx]: {}", file.getAbsolutePath());
        }
        return file;
    }

    /**
     * 将 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 存为 pdf
     *
     * @param wordMLPackage
     * @return
     * @throws Exception
     */
    public File savePdf(WordprocessingMLPackage wordMLPackage) throws Exception {

        File file = new File(genFilePath() + ".pdf");

        OutputStream os = new java.io.FileOutputStream(file);

        Docx4J.toPDF(wordMLPackage, os);

        os.flush();
        os.close();

        if (logger.isDebugEnabled()) {
            logger.debug("Save to [.pdf]: {}", file.getAbsolutePath());
        }
        return file;
    }

    /**
     * 将 {@link org.jsoup.nodes.Document} 对象转为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage}
     * xhtml to word
     *
     * @param doc
     * @return
     * @throws Exception
     */
    protected WordprocessingMLPackage xhtml2word(Document doc) throws Exception {

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage(PageSizePaper.valueOf("A4"), true); //A4纸，//横版:true

        configSimSunFont(wordMLPackage); //配置中文字体

        XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);

        wordMLPackage.getMainDocumentPart().getContent().addAll( //导入 xhtml
                xhtmlImporter.convert(doc.html(), doc.baseUri()));


        return wordMLPackage;
    }

    /**
     * 将页面转为{@link org.jsoup.nodes.Document}对象，xhtml 格式
     *
     * @param url
     * @return
     * @throws Exception
     */
    protected Document url2xhtml(String url) throws Exception {
        Document doc = Jsoup.connect(url).get(); //获得

        if (logger.isDebugEnabled()) {
            logger.debug("baseUri: {}", doc.baseUri());
        }

        for (Element script : doc.getElementsByTag("script")) { //除去所有 script
            script.remove();
        }

        for (Element a : doc.getElementsByTag("a")) { //除去 a 的 onclick，href 属性
            a.removeAttr("onclick");
            a.removeAttr("href");
        }

        Elements links = doc.getElementsByTag("link"); //将link中的地址替换为绝对地址
        for (Element element : links) {
            String href = element.absUrl("href");

            if (logger.isDebugEnabled()) {
                logger.debug("href: {} -> {}", element.attr("href"), href);
            }

            element.attr("href", href);
        }

        doc.outputSettings()
                .syntax(Document.OutputSettings.Syntax.xml)
                .escapeMode(Entities.EscapeMode.xhtml);  //转为 xhtml 格式

        if (logger.isDebugEnabled()) {
            String[] split = doc.html().split("\n");
            for (int c = 0; c < split.length; c++) {
                logger.debug("line {}:\t{}", c + 1, split[c]);
            }
        }
        return doc;
    }

    /**
     * 为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 配置中文字体
     *
     * @param wordMLPackage
     * @throws Exception
     */
    protected void configSimSunFont(WordprocessingMLPackage wordMLPackage) throws Exception {
        Mapper fontMapper = new IdentityPlusMapper();
        wordMLPackage.setFontMapper(fontMapper);

        String fontFamily = "SimSun";

        URL simsunUrl = this.getClass().getResource("/org/noahx/html2docx/simsun.ttc"); //加载字体文件（解决linux环境下无中文字体问题）
        // docx4j 8.x 重载为 URI，11.x 重载为 URL；反射调用以保持三线源码一致
        try {
            java.lang.reflect.Method addFonts = PhysicalFonts.class.getMethod("addPhysicalFonts", String.class, java.net.URI.class);
            addFonts.invoke(null, fontFamily, simsunUrl.toURI());
        } catch (NoSuchMethodException ignored) {
            java.lang.reflect.Method addFonts = PhysicalFonts.class.getMethod("addPhysicalFonts", String.class, java.net.URL.class);
            addFonts.invoke(null, fontFamily, simsunUrl);
        }
        PhysicalFont simsunFont = PhysicalFonts.get(fontFamily);
        fontMapper.put(fontFamily, simsunFont);

        RFonts rfonts = Context.getWmlObjectFactory().createRFonts(); //设置文件默认字体
        rfonts.setAsciiTheme(null);
        rfonts.setAscii(fontFamily);
        wordMLPackage.getMainDocumentPart().getPropertyResolver()
                .getDocumentDefaultRPr().setRFonts(rfonts);
    }

    /**
     * 生成文件位置
     *
     * @return
     */
    protected String genFilePath() {
        return System.getProperty("user.dir") + "/" + OUT_FILENAME;
    }
}