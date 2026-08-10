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

import java.math.BigInteger;

import org.docx4j.wml.CTBorder;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.TblBorders;

/**
 * Implementation of border utils functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class BorderUtils {
	
	public static CTBorder ctBorder() {
		return ctBorder("auto",new BigInteger("4"));
    }
	
	public static CTBorder ctBorder(String color) {
		return ctBorder(color,new BigInteger("4"));
    }
	
	public static CTBorder ctBorder(String color,BigInteger border_width) {
        return ctBorder(color,border_width,new BigInteger("0"));
    }

	public static CTBorder ctBorder(String color,BigInteger border_width,BigInteger border_space) {
	    CTBorder border = new CTBorder();
	    border.setColor(color);
	    border.setSz(border_width);
	    border.setSpace(border_space);
	    border.setVal(STBorder.SINGLE);
	    return border;
	}
	
	public static TblBorders tblBorders(CTBorder border) {
        TblBorders borders = new TblBorders();
        borders.setBottom(border);
        borders.setLeft(border);
        borders.setRight(border);
        borders.setTop(border);
        borders.setInsideH(border);
        borders.setInsideV(border);
        return borders;
    }

}
