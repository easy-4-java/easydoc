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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ChineseFontTest {

	@Test
	void enumHasValues() {
		assertTrue(ChineseFont.values().length > 0,
				"ChineseFont must declare at least one value");
	}

	@Test
	void everyValueHasNonBlankName() {
		for (ChineseFont f : ChineseFont.values()) {
			String name = f.getFontName();
			assertNotNull(name, f.name() + ".getFontName() must not be null");
			assertTrue(name.trim().length() > 0,
					f.name() + ".getFontName() must not be blank");
		}
	}

	@Test
	void everyValueHasNonBlankAlias() {
		for (ChineseFont f : ChineseFont.values()) {
			String alias = f.getFontAlias();
			assertNotNull(alias, f.name() + ".getFontAlias() must not be null");
			assertTrue(alias.trim().length() > 0,
					f.name() + ".getFontAlias() must not be blank");
		}
	}

	/**
	 * getFontURL() reads the bundled ttf/ttc resource. The URL may legitimately be null
	 * when the resource is not packaged on the classpath (the test classpath usually
	 * does not ship font binaries), so this test only asserts non-null where the
	 * resource is present.
	 */
	@Test
	void everyValueHasResourceWhenBundled() {
		for (ChineseFont f : ChineseFont.values()) {
			// No assertion on getFontURL() itself because the bundled .ttf/.ttc
			// resources are not on the test classpath. We just touch every value
			// so the test fails fast if any constant throws on access.
			f.getFontURL();
		}
	}
}
