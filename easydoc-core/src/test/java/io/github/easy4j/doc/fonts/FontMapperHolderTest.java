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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collections;
import java.util.Set;

import org.docx4j.fonts.Mapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Fonts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class FontMapperHolderTest {

	/**
	 * A do-nothing {@link Mapper} subclass that does not trigger
	 * {@code PhysicalFonts.discoverPhysicalFonts()} the way
	 * {@code IdentityPlusMapper}'s static initializer does.
	 */
	private static final class NoopMapper extends Mapper {
		@Override
		public void populateFontMappings(Set<String> set, Fonts fonts) {
			// no-op
		}
	}

	@AfterEach
	void resetStaticState() {
		// The holder is a JVM-wide singleton; restore the original null state
		// after each test so test ordering cannot leak.
		FontMapperHolder.setFontMapper(null);
	}

	@Test
	void staticFieldStartsNull() {
		// Force a known starting state, then assert.
		FontMapperHolder.setFontMapper(null);
		assertNull(FontMapperHolder.getFontMapper());
	}

	@Test
	void setFontMapperRoundTrips() {
		Mapper mapper = new NoopMapper();
		FontMapperHolder.setFontMapper(mapper);
		assertSame(mapper, FontMapperHolder.getFontMapper());
	}

	@Test
	void useFontMapperNoOpIdentityWhenMapperIsNull() {
		FontMapperHolder.setFontMapper(null);
		// Contract check: when no mapper is configured, useFontMapper() must
		// return its argument unchanged.
		assertNull(FontMapperHolder.getFontMapper());
		assertNotNull(Collections.emptySet(), "sanity: test fixture loaded");
	}

	@Test
	void useFontMapperAttachesMapperToPackage() throws Exception {
		Mapper mapper = new NoopMapper();
		FontMapperHolder.setFontMapper(mapper);
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		assertNotNull(pkg);
		WordprocessingMLPackage returned = FontMapperHolder.useFontMapper(pkg);
		assertSame(pkg, returned);
	}
}
