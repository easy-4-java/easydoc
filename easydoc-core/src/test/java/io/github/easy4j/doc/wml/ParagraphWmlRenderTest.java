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
import org.junit.jupiter.api.Test;

class ParagraphWmlRenderTest {

	@Test
	void constructorStoresFields() throws Exception {
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
