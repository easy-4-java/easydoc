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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;

import org.docx4j.wml.ObjectFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * {@link DocxElementWmlRender} touches the docx4j JAXB/MOXy bridge in two
 * places:
 *
 * <ul>
 *   <li>The constructor calls {@code Context.getWmlObjectFactory()}, which
 *       initialises {@code Context.jc} via the MOXy SPI lookup that fails on
 *       docx4j 11.5.14.</li>
 *   <li>Render methods (e.g. {@code newTable}, {@code newRow}) operate on a
 *       live {@code WordprocessingMLPackage}, which itself triggers the same
 *       bridge at construction time.</li>
 * </ul>
 *
 * <p>Both surfaces are therefore {@link Disabled} until the easydoc-core pom
 * is migrated. Tests are kept here as guards for the moment the fix lands.</p>
 */
class DocxElementWmlRenderTest {

	/**
	 * Disabled: the constructor calls
	 * {@code Context.getWmlObjectFactory()}, which initialises
	 * {@code Context.jc} via the MOXy bridge.
	 */
	@Test
	@Disabled("requires MOXY migration — DocxElementWmlRender constructor calls Context.getWmlObjectFactory()")
	void constructorStoresFields() throws Exception {
		org.docx4j.openpackaging.packages.WordprocessingMLPackage pkg = null;
		DocxElementWmlRender render = new DocxElementWmlRender(pkg);
		assertNotNull(render);

		Field pkgField = DocxElementWmlRender.class.getDeclaredField("wmlPackage");
		pkgField.setAccessible(true);
		assertNotNull(pkgField.get(render));

		Field factoryField = DocxElementWmlRender.class.getDeclaredField("factory");
		factoryField.setAccessible(true);
		ObjectFactory factory = (ObjectFactory) factoryField.get(render);
		assertNotNull(factory);
	}

	/**
	 * Disabled: {@code newTable(int, int)} ultimately reaches into a live
	 * {@code WordprocessingMLPackage.getMainDocumentPart()}, which triggers the
	 * JAXB bridge. The factory call itself ({@code createTbl}) is fine, but
	 * the test cannot exercise it without a real package.
	 */
	@Test
	@Disabled("requires MOXY migration — newTable(int,int) uses WordprocessingMLPackage.getMainDocumentPart()")
	void newTableCreatesGridOfCorrectDimensions() {
		DocxElementWmlRender render = new DocxElementWmlRender(null);
		org.docx4j.wml.Tbl table = render.newTable(2, 3);
		assertNotNull(table);
		assertNotNull(table.getContent());
	}
}
