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
package io.github.easy4j.doc.jetbrick.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.net.URL;

import org.junit.jupiter.api.Test;

class PathUtilsTest {

	// ----- fileAsUrl / urlAsFile / urlAsPath -----

	@Test
	void fileAsUrlFromStringRoundTripsThroughUrlAsFile() throws Exception {
		URL url = PathUtils.fileAsUrl("src/test/resources");
		assertNotNull(url);
		assertEquals("file", url.getProtocol());

		File file = PathUtils.urlAsFile(url);
		assertNotNull(file);
		assertEquals(url.toURI().getPath(), file.toURI().getPath());
	}

	@Test
	void fileAsUrlFromFileProducesFileProtocolUrl() throws Exception {
		File f = new File("src/test/resources");
		URL url = PathUtils.fileAsUrl(f);
		assertNotNull(url);
		assertEquals("file", url.getProtocol());
		assertEquals(f.toURI().toURL(), url);
	}

	@Test
	void urlAsFileWithNullReturnsNull() {
		assertNull(PathUtils.urlAsFile(null));
	}

	@Test
	void urlAsPathWithNullReturnsNull() {
		assertNull(PathUtils.urlAsPath(null));
	}

	@Test
	void urlAsPathForFileUrlReturnsDecodedPath() throws Exception {
		URL url = new File("src/test/resources").toURI().toURL();
		String path = PathUtils.urlAsPath(url);
		assertNotNull(path);
		assertEquals(url.getPath(), path);
	}

	// ----- normalize -----

	@Test
	void normalizeNullReturnsNull() {
		assertNull(PathUtils.normalize(null));
	}

	@Test
	void normalizeEmptyReturnsEmpty() {
		assertEquals("", PathUtils.normalize(""));
	}

	@Test
	void normalizePlainPathUnchanged() {
		assertEquals("a/b/c", PathUtils.normalize("a/b/c"));
	}

	@Test
	void normalizeRemovesDotSegmentInMiddle() {
		assertEquals("a/b", PathUtils.normalize("a/./b"));
	}

	@Test
	void normalizeRemovesLeadingDotSegment() {
		assertEquals("a/b", PathUtils.normalize("./a/b"));
	}

	@Test
	void normalizeCollapsesDoubleSlashes() {
		assertEquals("a/b", PathUtils.normalize("a//b"));
	}

	@Test
	void normalizeBackslashesToForwardSlashes() {
		assertEquals("a/b/c", PathUtils.normalize("a\\b\\c"));
	}

	@Test
	void normalizeResolvesParentReference() {
		// PathUtils only descends into the manual splitter when the input has
		// either a "./" segment (not at offset 0) or a "//" sequence, since
		// the pre-loop short-circuits when both are absent. We use "//" here
		// to force the splitter to run and resolve the "..".
		assertEquals("a/c", PathUtils.normalize("a//b/../c"));
	}

	@Test
	void normalizeAbsolutePathKeepsLeadingSlash() {
		assertEquals("/a/b", PathUtils.normalize("/a/b"));
	}

	@Test
	void normalizeTrailingSlashKeptForDirectory() {
		assertEquals("a/b/", PathUtils.normalize("a/b/"));
	}

	@Test
	void normalizeEscapeAboveRootThrows() {
		// The implementation only throws when the *result* starts with "/.."
		// (i.e. the resolution escapes above an absolute root). A relative
		// input like "../escape" is left alone.
		assertThrows(IllegalStateException.class,
				() -> PathUtils.normalize("/../escape"));
	}

	// ----- concat / getRelativePath -----

	@Test
	void concatJoinsParentAndChild() {
		assertEquals("a/b/c", PathUtils.concat("a/b", "c"));
	}

	@Test
	void concatNullParentNormalizesChild() {
		assertEquals("a/b", PathUtils.concat(null, "a/b"));
	}

	@Test
	void concatNullChildNormalizesParent() {
		assertEquals("a/b", PathUtils.concat("a/b", null));
	}

	@Test
	void concatBothNullReturnsNull() {
		assertNull(PathUtils.concat(null, null));
	}

	@Test
	void getRelativePathAbsoluteInputReturnsNormalized() {
		assertEquals("/a/b", PathUtils.getRelativePath("/base/x", "/a/b"));
	}

	@Test
	void getRelativePathRelativeInputResolvesAgainstBaseDir() {
		// getRelativePath(baseFile, file) for a relative `file` returns
		// normalize(baseDir + file) — i.e. it concatenates with the directory
		// of baseFile, not with baseFile itself. So baseFile="base/sub/x.txt"
		// yields baseDir="base/sub/" and the result is "base/sub/foo.txt".
		assertEquals("base/sub/foo.txt",
				PathUtils.getRelativePath("base/sub/x.txt", "foo.txt"));
	}

	// ----- separatorsToUnix / Windows / System -----

	@Test
	void separatorsToUnixLeavesForwardSlashesAlone() {
		assertEquals("a/b/c", PathUtils.separatorsToUnix("a/b/c"));
	}

	@Test
	void separatorsToUnixConvertsBackslashes() {
		assertEquals("a/b/c", PathUtils.separatorsToUnix("a\\b\\c"));
	}

	@Test
	void separatorsToUnixNullReturnsNull() {
		assertNull(PathUtils.separatorsToUnix(null));
	}

	@Test
	void separatorsToWindowsLeavesBackslashesAlone() {
		assertEquals("a\\b\\c", PathUtils.separatorsToWindows("a\\b\\c"));
	}

	@Test
	void separatorsToWindowsConvertsForwardSlashes() {
		assertEquals("a\\b\\c", PathUtils.separatorsToWindows("a/b/c"));
	}

	@Test
	void separatorsToWindowsNullReturnsNull() {
		assertNull(PathUtils.separatorsToWindows(null));
	}

	@Test
	void separatorsToSystemDelegatesByOs() {
		String input = "a/b\\c";
		String expected = (File.separatorChar == '\\')
				? "a\\b\\c"
				: "a/b/c";
		assertEquals(expected, PathUtils.separatorsToSystem(input));
	}

	@Test
	void separatorsToSystemNullReturnsNull() {
		assertNull(PathUtils.separatorsToSystem(null));
	}
}
