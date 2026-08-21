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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection.KeyVal;
import org.junit.jupiter.api.Test;

/**
 * Pure state tests for {@link DataMap}. No docx4j / JAXB involvement, so every
 * test in here should run regardless of MOXy status.
 */
class DataMapTest {

	@Test
	void defaultConstructorInitializesEmptyMaps() {
		DataMap dm = new DataMap();
		assertNotNull(dm.getData1(), "data1 must be initialised to a non-null collection");
		assertNotNull(dm.getData2(), "data2 must be initialised to a non-null map");
		assertNotNull(dm.getCookies(), "cookies must be initialised to a non-null map");
		assertTrue(dm.getData1().isEmpty(), "data1 must start empty");
		assertTrue(dm.getData2().isEmpty(), "data2 must start empty");
		assertTrue(dm.getCookies().isEmpty(), "cookies must start empty");
	}

	@Test
	void setData1ReplacesTheMap() {
		DataMap dm = new DataMap();
		List<KeyVal> list = new ArrayList<KeyVal>();
		// KeyVal has a public no-arg ctor and is final-field, but we don't need real
		// values — we're proving the field is replaced, not exercised.
		dm.setData1(list);
		assertSame(list, dm.getData1(), "setData1 must store the supplied collection by reference");
	}

	@Test
	void setData2ReplacesTheMap() {
		DataMap dm = new DataMap();
		Map<String, String> input = new HashMap<String, String>();
		input.put("k", "v");
		dm.setData2(input);
		assertSame(input, dm.getData2(), "setData2 must store the supplied map by reference");
		assertEquals("v", dm.getData2().get("k"), "stored map must retain its entries");
	}

	@Test
	void setCookiesReplacesTheMap() {
		DataMap dm = new DataMap();
		Map<String, String> input = new HashMap<String, String>();
		input.put("session", "abc123");
		dm.setCookies(input);
		assertSame(input, dm.getCookies(), "setCookies must store the supplied map by reference");
		assertEquals("abc123", dm.getCookies().get("session"),
				"stored cookies map must retain its entries");
	}
}
