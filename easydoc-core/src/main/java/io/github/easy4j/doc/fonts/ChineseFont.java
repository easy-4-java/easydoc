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
package io.github.easy4j.doc.fonts;

import java.net.URL;

public enum ChineseFont {
	
	//仿宋体
	SIMFANG("仿宋体","SimFang","SIMFANG.ttf"),
	//黑体
	SIMHEI("黑体","SimHei","SIMHEI.ttf"),
	//楷体
	SIMKAI("楷体","SimKai","SIMKAI.ttf"),
	//宋体&新宋体
	SIMSUM("宋体","SimSun","simsun.ttc"),
	//华文仿宋
	STFANGSO("华文仿宋","StFangSo","STFANGSO.ttf");
	
	private String fontName;
	private String fontAlias;
	private String fontFileName;
	
	public URL getFontURL() {
		return ChineseFont.class.getResource(this.fontFileName);
	}
	public String getFontName() {
		return fontName;
	}
	
	public String getFontAlias() {
		return fontAlias;
	}
	 
	private ChineseFont(String fontName,String fontAlias,String fontFileName) {
		this.fontName = fontName;
		this.fontAlias = fontAlias;
		this.fontFileName = fontFileName;
    }

}
