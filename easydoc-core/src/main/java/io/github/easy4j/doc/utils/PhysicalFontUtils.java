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

import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import io.github.easy4j.doc.fonts.ChineseFont;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Physical font utilities.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class PhysicalFontUtils {

	private static final Logger LOG = LoggerFactory.getLogger(PhysicalFontUtils.class);

	/**
	 * 仅当目标物理字体在当前系统上可用时才建立映射。docx4j 的
	 * {@link Mapper#put} 内部是 ConcurrentHashMap，value 为 null 会直接
	 * NPE——在没有安装微软字体库的 macOS/Linux 上 PhysicalFonts.get 会
	 * 返回 null。跳过的映射由 IdentityPlusMapper 的 Panose 匹配自动回退。
	 */
	private static void putIfAvailable(Mapper fontMapper, String documentFontName, String physicalFontName) {
		PhysicalFont font = PhysicalFonts.get(physicalFontName);
		if (font != null) {
			fontMapper.put(documentFontName, font);
		} else {
			LOG.debug("Physical font '{}' is not installed on this system; "
					+ "skipping mapping for '{}' (IdentityPlusMapper Panose fallback applies)",
					physicalFontName, documentFontName);
		}
	}

	private static Mapper newFontMapper() throws Exception {

		// Set up font mapper (optional)
		PhysicalFonts.get("Arial Unicode MS");

		Mapper fontMapper = new IdentityPlusMapper();
		//进行中文字体兼容处理（物理字体不存在时跳过，见 putIfAvailable 的说明）
        putIfAvailable(fontMapper, "微软雅黑", "Microsoft Yahei");
        putIfAvailable(fontMapper, "黑体", "SimHei");
        putIfAvailable(fontMapper, "楷体", "KaiTi");
        putIfAvailable(fontMapper, "隶书", "LiSu");
        putIfAvailable(fontMapper, "宋体", "SimSun");
        putIfAvailable(fontMapper, "宋体扩展", "simsun-extB");
        putIfAvailable(fontMapper, "新宋体", "NSimSun");
        putIfAvailable(fontMapper, "仿宋", "FangSong");
        putIfAvailable(fontMapper, "仿宋_GB2312", "FangSong_GB2312");
        putIfAvailable(fontMapper, "幼圆", "YouYuan");
        putIfAvailable(fontMapper, "华文宋体", "STSong");
        putIfAvailable(fontMapper, "华文仿宋", "STFangsong");
        putIfAvailable(fontMapper, "华文中宋", "STZhongsong");
        putIfAvailable(fontMapper, "华文行楷", "STXingkai");

        return fontMapper;
	}

	/*
	 * 为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 配置中文字体;解决中文乱码问题
	 */
	public static void setWmlPackageFonts(WordprocessingMLPackage wmlPackage) throws Docx4JException {
		try {
			//字体映射;
			Mapper fontMapper = newFontMapper();
			//设置文档字体库
			wmlPackage.setFontMapper(fontMapper, true);
		} catch (Exception e) {
			// e 作为 cause 传入：原实现 (e.getMessage(), e.getCause()) 在 NPE 等
			// 无消息异常时会产生既无消息也无 cause 的 Docx4JException，把根因
			// 全部吞掉，用户只能看到空白堆栈
			throw new Docx4JException(e.getMessage(), e);
		}
    }

	/*
	 * 为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 配置默认字体
	 */
	public static void setDefaultFont(WordprocessingMLPackage wmlPackage,String fontName) throws Docx4JException {
        //设置文件默认字体
        RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
        rfonts.setAsciiTheme(null);
        rfonts.setAscii(fontName);
        rfonts.setHAnsi(fontName);
        rfonts.setEastAsia(fontName);
        RPr rpr = wmlPackage.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr();
        rpr.setRFonts(rfonts);
    }

	/*
	 * 为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 配置中文字体
	 */
	public static void setSimSunFont(WordprocessingMLPackage wmlPackage) throws Docx4JException {
        //设置文件默认字体
		setDefaultFont(wmlPackage, ChineseFont.SIMSUM.getFontName());
    }

	/*
	 * 为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 增加新的字体
	 */
	public static void setPhysicalFont(WordprocessingMLPackage wmlPackage,PhysicalFont physicalFont) throws Exception {
		Mapper fontMapper = wmlPackage.getFontMapper() == null ? new IdentityPlusMapper() : wmlPackage.getFontMapper();
		//分别设置字体名和别名对应的字体库
		fontMapper.put(physicalFont.getName(), physicalFont );
		//设置文档字体库
		wmlPackage.setFontMapper(fontMapper, true);
    }

	/*
	 * 为 {@link org.docx4j.openpackaging.packages.WordprocessingMLPackage} 增加新的字体
	 */
	public static void setPhysicalFont(WordprocessingMLPackage wmlPackage,String fontName) throws Exception {
		Mapper fontMapper = wmlPackage.getFontMapper() == null ? new IdentityPlusMapper() : wmlPackage.getFontMapper();
		//获取字体库
		PhysicalFont physicalFont = PhysicalFonts.get(fontName);
		//分别设置字体名和别名对应的字体库
		fontMapper.put(fontName, physicalFont );
		//设置文档字体库
		wmlPackage.setFontMapper(fontMapper, true);
    }
}
