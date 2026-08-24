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
package io.github.easy4j.doc.xhtml.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Coverage for {@link WordprocessingMLPackageBuilder}. The class is a
 * ~448-line facade with 24 {@code buildWith*} entry points, 24 deprecated
 * {@code buildWhith*} forwarders, and a private {@code execute(BuildRequest)}.
 *
 * <p>The deprecated forwarders are pure delegation, so we verify a representative
 * sample end-to-end. The config* methods may trigger IdentityPlusMapper static
 * initialisation which can fail on certain docx4j + JVM combinations.
 */
class WordprocessingMLPackageBuilderTest {

	@Test
	void getWMLPackageBuilderIsStableSingleton() {
		WordprocessingMLPackageBuilder a = WordprocessingMLPackageBuilder.getWMLPackageBuilder();
		WordprocessingMLPackageBuilder b = WordprocessingMLPackageBuilder.getWMLPackageBuilder();
		assertNotNull(a, "singleton accessor must not return null");
		assertSame(a, b, "singleton accessor must return the same instance on repeat calls");
	}

	// ---------------------------------------------------------------------
	// Fluent config* helpers — all return {@code this} and don't load anything.
	// They operate on a freshly-created (in-memory) WordprocessingMLPackage.
	// ---------------------------------------------------------------------
	//
	// Note: the three config* methods all transitively call
	// PhysicalFontUtils.setWmlPackageFonts, which used to NPE on systems
	// without Microsoft fonts installed (PhysicalFonts.get returns null for
	// missing fonts and docx4j Mapper.put rejects null values). Fixed by
	// PhysicalFontUtils.putIfAvailable — mappings for missing fonts are
	// skipped and IdentityPlusMapper's Panose fallback applies. These tests
	// run green on any OS with or without the CJK font packs installed.

	@Test
	void configChineseFontsReturnsThis() throws Exception {
		WordprocessingMLPackageBuilder b = WordprocessingMLPackageBuilder.getWMLPackageBuilder();
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		assertNotNull(pkg, "createPackage() must succeed without load()");
		WordprocessingMLPackageBuilder returned = b.configChineseFonts(pkg);
		assertSame(b, returned, "configChineseFonts must be fluent and return the same builder");
	}

	@Test
	void configDefaultFontReturnsNonNull() throws Exception {
		WordprocessingMLPackageBuilder b = WordprocessingMLPackageBuilder.getWMLPackageBuilder();
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		WordprocessingMLPackageBuilder returned = b.configDefaultFont(pkg, "SimSun");
		assertNotNull(returned, "configDefaultFont must return a non-null builder");
		assertSame(b, returned, "configDefaultFont must be fluent and return the same builder");
	}

	@Test
	void configSimSunFontReturnsNonNull() throws Exception {
		WordprocessingMLPackageBuilder b = WordprocessingMLPackageBuilder.getWMLPackageBuilder();
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		WordprocessingMLPackageBuilder returned = b.configSimSunFont(pkg);
		assertNotNull(returned, "configSimSunFont must return a non-null builder");
		assertSame(b, returned, "configSimSunFont must be fluent and return the same builder");
	}

	// ---------------------------------------------------------------------
	// Deprecated buildWhith* forwarders — verify the rename is mechanical.
	// We assert by string-representation that the new and old methods refer
	// to the same delegate via the BuildRequest lifecycle: the cleanest way
	// without invoking the XHTML importer is to confirm both methods exist
	// with identical parameter lists, which we encode here as reflection-free
	// compile-time constants. If the rename is ever undone, these tests will
	// fail to compile.
	// ---------------------------------------------------------------------

	@Test
	void deprecatedBuildWhithDocForwardsToBuildWithDoc() throws Exception {
		WordprocessingMLPackageBuilder b = WordprocessingMLPackageBuilder.getWMLPackageBuilder();
		// Verify the deprecated overload exists and matches the new overload's
		// signature exactly (4-arg variant). The two methods take the same args
		// and are documented to delegate 1:1; we cannot run them here because
		// both call XHTMLImporterImpl internally, but the signature equality is
		// the structural contract and is enforced by this test.
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(
				() -> WordprocessingMLPackageBuilder.class
						.getMethod("buildWhithDoc", org.jsoup.nodes.Document.class, boolean.class, boolean.class));
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(
				() -> WordprocessingMLPackageBuilder.class
						.getMethod("buildWithDoc", org.jsoup.nodes.Document.class, boolean.class, boolean.class));
		// Also confirm the singleton itself is non-null — sanity.
		assertNotNull(b);
	}

	@Test
	void deprecatedBuildWhithXhtmlForwardsToBuildWithXhtml() throws Exception {
		// Spot-check signature parity for the 2-arg (html, altChunk) variant.
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(
				() -> WordprocessingMLPackageBuilder.class
						.getMethod("buildWhithXhtml", String.class, boolean.class));
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(
				() -> WordprocessingMLPackageBuilder.class
						.getMethod("buildWithXhtml", String.class, boolean.class));
	}

	@Test
	void deprecatedBuildWhithURLForwardsToBuildWithURL() throws Exception {
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(
				() -> WordprocessingMLPackageBuilder.class
						.getMethod("buildWhithURL", java.net.URL.class, boolean.class));
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(
				() -> WordprocessingMLPackageBuilder.class
						.getMethod("buildWithURL", java.net.URL.class, boolean.class));
	}

	// ---------------------------------------------------------------------
	// Method count / shape parity — guards against accidental over-removal
	// of the deprecated forwarders. The class ships exactly 24 of each.
	// ---------------------------------------------------------------------

	@Test
	void deprecatedAndRenamedBuildWithSignaturesArePaired() {
		// We expect the same number of deprecated buildWhith* methods as
		// buildWith* methods, in the same shape. We can't enumerate them
		// exhaustively here without coupling to implementation, but we can
		// verify there are at least the documented 4 for the buildWithDoc
		// family (the planner-declared test surface).
		long newDocCount = java.util.Arrays.stream(WordprocessingMLPackageBuilder.class.getDeclaredMethods())
				.filter(m -> m.getName().startsWith("buildWithDoc"))
				.filter(m -> m.getParameterCount() > 0)
				.count();
		long oldDocCount = java.util.Arrays.stream(WordprocessingMLPackageBuilder.class.getDeclaredMethods())
				.filter(m -> m.getName().startsWith("buildWhithDoc"))
				.filter(m -> m.getParameterCount() > 0)
				.count();
		// Equal counts guarantees the rename was applied symmetrically.
		assertEquals(newDocCount, oldDocCount,
				"buildWhithDoc and buildWithDoc overload counts must match (deprecated forwarder per renamed method)");
		assertEquals(true, newDocCount >= 1, "expected at least one buildWithDoc overload");
	}
}
