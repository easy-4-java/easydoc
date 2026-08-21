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
package io.github.easy4j.doc.wml;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.docx4j.wml.Tbl;
import org.junit.jupiter.api.Test;

class MyTblFinderTest {

	@Test
	void freshFinderHasEmptyTblList() {
		MyTblFinder finder = new MyTblFinder();
		List<Tbl> tables = finder.getTbls();
		assertNotNull(tables);
		assertTrue(tables.isEmpty());
	}

	@Test
	void applyAddsTblInstanceToList() {
		MyTblFinder finder = new MyTblFinder();
		Tbl tbl = new Tbl();
		List<Object> result = finder.apply(tbl);
		assertNotNull(finder.getTbls());
		assertTrue(finder.getTbls().contains(tbl));
		assertSame(tbl, finder.getTbls().get(0));
		assertTrue(result == null || result.isEmpty(),
				"apply() must return null/empty per CallbackImpl contract");
	}

	@Test
	void applyIgnoresNonTblObjects() {
		MyTblFinder finder = new MyTblFinder();
		finder.apply("not a tbl");
		finder.apply(42);
		finder.apply(new Object());
		assertTrue(finder.getTbls().isEmpty());
	}

	@Test
	void shouldTraverseReturnsFalseForTbl() {
		MyTblFinder finder = new MyTblFinder();
		Tbl tbl = new Tbl();
		assertFalse(finder.shouldTraverse(tbl),
				"shouldTraverse must return false for Tbl instances " +
						"(CallbackImpl convention: do not recurse into a matched node)");
	}

	@Test
	void shouldTraverseReturnsTrueForNonTblObjects() {
		MyTblFinder finder = new MyTblFinder();
		assertTrue(finder.shouldTraverse("string"));
		assertTrue(finder.shouldTraverse(new Object()));
	}
}
