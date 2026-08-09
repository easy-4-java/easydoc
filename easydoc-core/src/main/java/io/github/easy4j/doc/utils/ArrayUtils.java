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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of array utils extending org.apache.commons.lang3.ArrayUtils.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
public class ArrayUtils extends org.apache.commons.lang3.ArrayUtils {

	@SafeVarargs
	public static <T> Set<T> asSet(T... element) {
		HashSet<T> elements = new HashSet<T>(element.length);
		Collections.addAll(elements, element);
		return elements;
	}
	
}
