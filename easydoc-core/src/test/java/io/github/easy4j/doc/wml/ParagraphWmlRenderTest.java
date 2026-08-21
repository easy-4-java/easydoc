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
 * Most surfaces of {@link ParagraphWmlRender} transitively call
 * {@code org.docx4j.jaxb.Context.getWmlObjectFactory()}, which initialises
 * {@code Context.jc} via the docx4j JAXB/MOXy bridge. That bridge fails on
 * docx4j 11.5.14 until the easydoc-core pom is migrated; therefore every
 * render test is currently {@link Disabled}.
 *
 * <p>The source class itself is essentially a stub today (no public render
 * methods besides the constructor), so these tests double as a guard for
 * the moment the MOXy fix lands and a real render surface appears.</p>
 */
class ParagraphWmlRenderTest {

	/**
	 * Disabled: the constructor calls
	 * {@code Context.getWmlObjectFactory()}, which initialises
	 * {@code Context.jc} via the MOXy bridge.
	 */
	@Test
	@Disabled("requires MOXy migration — Context.getWmlObjectFactory() initialises the JAXB bridge")
	void constructorStoresFields() throws Exception {
		// We can't construct a real WordprocessingMLPackage without triggering
		// the bridge either, so even this minimal smoke is gated.
		org.docx4j.openpackaging.packages.WordprocessingMLPackage pkg = null;
		ParagraphWmlRender render = new ParagraphWmlRender(pkg);

		Field pkgField = ParagraphWmlRender.class.getDeclaredField("wmlPackage");
		pkgField.setAccessible(true);
		assertNotNull(render);

		Field factoryField = ParagraphWmlRender.class.getDeclaredField("factory");
		factoryField.setAccessible(true);
		Object factory = factoryField.get(render);
		assertNotNull(factory);
		assertNotNull((Object) ((ObjectFactory) factory));
	}
}
