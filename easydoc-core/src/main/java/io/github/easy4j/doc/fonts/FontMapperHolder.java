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

import org.docx4j.fonts.Mapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

/**
 * Class for FontMapperHolder.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class FontMapperHolder {

	private static Mapper fontMapper;

	public static Mapper getFontMapper() {
		return fontMapper;
	}

	public static void setFontMapper(Mapper fontMapper) {
		FontMapperHolder.fontMapper = fontMapper;
	}
	
	public static WordprocessingMLPackage useFontMapper(WordprocessingMLPackage wmlPackage) throws Exception {
		if(fontMapper != null) {
			wmlPackage.setFontMapper(fontMapper);
		}
		return wmlPackage;
	}
	
}
