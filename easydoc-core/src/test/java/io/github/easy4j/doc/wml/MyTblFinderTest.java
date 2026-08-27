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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MyTblFinder}.
 *
 * <p>apply() 返回子节点列表的契约（#26）为 3.0.x d5a37dd 的移植内容。</p>
 */
@DisplayName("MyTblFinder Tests")
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
		// 修复后（#26）：apply 必须返回当前节点的子节点列表而非 null
		assertNotNull(result, "apply() must return the node's children list, never null");
		assertSame(((org.docx4j.wml.ContentAccessor) tbl).getContent(), result,
				"apply() must delegate to getChildren(), i.e. the Tbl content list");
	}

	@Test
	void applyOnSampleTableReturnsNonNullChildrenList() {
		// 构造样例表格：tbl > tr > tc > p > r > text
		Tbl tbl = new Tbl();
		Tr row = new Tr();
		Tc cell = new Tc();
		P paragraph = new P();
		R run = new R();
		Text cellText = new Text();
		cellText.setValue("cell value");
		run.getContent().add(cellText);
		paragraph.getContent().add(run);
		cell.getContent().add(paragraph);
		row.getContent().add(cell);
		tbl.getContent().add(row);

		MyTblFinder finder = new MyTblFinder();
		List<Object> children = finder.apply(tbl);

		assertNotNull(children, "sample table apply() must yield a non-null children list");
		assertEquals(1, children.size());
		assertSame(row, children.get(0));
		assertEquals(1, finder.getTbls().size());
		assertTrue(children.contains(row), "children of the table should contain the row");

		// 表格的下一层子节点也可继续访问（与 CallbackImpl 遍历约定一致）
		List<Object> rowChildren = finder.apply(row);
		assertNotNull(rowChildren);
		assertTrue(rowChildren.contains(cell));
		assertFalse(finder.shouldTraverse(tbl),
				"traversal must not recurse into a matched Tbl");
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
