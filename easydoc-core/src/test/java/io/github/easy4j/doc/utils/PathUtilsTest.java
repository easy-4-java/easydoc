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
package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.net.URL;

import org.junit.jupiter.api.Test;

class PathUtilsTest {

    @Test
    void normalizeReturnsNullForNull() {
        assertNull(PathUtils.normalize(null));
    }

    @Test
    void normalizeStripsLeadingDot() {
        assertEquals("foo/bar", PathUtils.normalize("./foo/bar"));
    }

    @Test
    void normalizeCollapsesDoubleSlashes() {
        assertEquals("foo/bar", PathUtils.normalize("foo//bar"));
    }

    @Test
    void normalizeResolvesParent() {
        assertEquals("bar", PathUtils.normalize("foo/../bar"));
    }

    @Test
    void normalizeKeepsAbsolute() {
        assertEquals("/foo/bar", PathUtils.normalize("/foo/bar"));
    }

    @Test
    void concatJoinsParentAndChild() {
        assertEquals("foo/bar", PathUtils.concat("foo", "bar"));
    }

    @Test
    void concatReturnsNormalizedWhenParentNull() {
        assertEquals("foo/bar", PathUtils.concat(null, "foo/bar"));
    }

    @Test
    void concatReturnsNormalizedWhenChildNull() {
        assertEquals("foo/bar", PathUtils.concat("foo/bar", null));
    }

    @Test
    void separatorsToUnixConvertsBackslashes() {
        assertEquals("foo/bar/baz", PathUtils.separatorsToUnix("foo\\bar\\baz"));
    }

    @Test
    void separatorsToWindowsConvertsForwardSlashes() {
        assertEquals("foo\\bar\\baz", PathUtils.separatorsToWindows("foo/bar/baz"));
    }

    @Test
    void separatorsToUnixReturnsNullForNull() {
        assertNull(PathUtils.separatorsToUnix(null));
    }

    @Test
    void separatorsToWindowsReturnsNullForNull() {
        assertNull(PathUtils.separatorsToWindows(null));
    }

    @Test
    void getRelativePathReturnsAbsoluteAsNormalized() {
        assertEquals("/foo/bar", PathUtils.getRelativePath("/anywhere", "/foo/bar"));
    }

    @Test
    void getRelativePathComputesFromBase() {
        // PathUtils.getRelativePath("/baz/", "foo/bar") returns "/baz/foo/bar"
        // because the base file is "/baz/" (it includes the trailing slash).
        assertEquals("/baz/foo/bar", PathUtils.getRelativePath("/baz/", "foo/bar"));
    }

    @Test
    void fileAsUrlStringReturnsURL() throws Exception {
        URL url = PathUtils.fileAsUrl(new File("foo.txt").getAbsolutePath());
        assertNotNull(url);
    }

    @Test
    void fileAsUrlFileReturnsURL() throws Exception {
        URL url = PathUtils.fileAsUrl(new File("foo.txt"));
        assertNotNull(url);
    }
}
