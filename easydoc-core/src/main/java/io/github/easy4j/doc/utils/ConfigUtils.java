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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Implementation of config utils functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
public class ConfigUtils {

	private static final String PLUS = "+";

	private static final String MINUS = "-";
	
	public static Properties filterWithPrefix(String filter,String prefix,Properties input, boolean escape) {
		Properties ret = new Properties();
		for (Object entry : input.keySet()) {
			String key = entry.toString();
			if (escape) {
				key = key.replace('_', '.');
			}
			if(key.startsWith(filter)) {
				String value = input.getProperty(key);
				if (escape) {
					if (value.startsWith(PLUS)) {
						key += PLUS;
						value = value.substring(PLUS.length());
					} else if (value.startsWith(MINUS)) {
						key += MINUS;
						value = value.substring(MINUS.length());
					}
				}
				key = key.substring(prefix.length());
				ret.setProperty(key, value);
			}
		}
		return ret;
	}
	
	public static Map<String, String> filterWithPrefix(String prefix, Map<String, String> input, boolean escape) {
		Map<String, String> ret = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : input.entrySet()) {
			String key = entry.getKey();
			if (escape) {
				key = key.replace('_', '.');
			}
			if(key.startsWith(prefix)) {
				key = key.substring(prefix.length());
				String value = entry.getValue();
				if (escape) {
					if (value.startsWith(PLUS)) {
						key += PLUS;
						value = value.substring(PLUS.length());
					} else if (value.startsWith(MINUS)) {
						key += MINUS;
						value = value.substring(MINUS.length());
					}
				}
				ret.put(key, value);
			}
		}
		return ret;
	}
	
}
