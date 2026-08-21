package io.github.easy4j.doc.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

/**
 * Additional tests for PathUtils to cover separatorsToSystem,
 * getRelativePath without separator, and normalize edge cases.
 */
class PathUtilsExtendedTest {

    @Test
    void separatorsToSystemConvertsToSystemSeparator() {
        String input = "foo/bar/baz";
        String result = PathUtils.separatorsToSystem(input);
        assertNotNull(result);
        if (File.separatorChar == '\\') {
            assertEquals("foo\\bar\\baz", result);
        } else {
            assertEquals("foo/bar/baz", result);
        }
    }

    @Test
    void separatorsToSystemReturnsNullForNull() {
        assertNull(PathUtils.separatorsToSystem(null));
    }

    @Test
    void separatorsToSystemNoConversionNeeded() {
        if (File.separatorChar == '/') {
            assertEquals("foo/bar", PathUtils.separatorsToSystem("foo/bar"));
        } else {
            assertEquals("foo\\bar", PathUtils.separatorsToSystem("foo\\bar"));
        }
    }

    @Test
    void getRelativePathWithoutTrailingSlashInBase() {
        // When base has no '/', separatorIndex is -1, so just normalize(file)
        String result = PathUtils.getRelativePath("basefile", "childfile");
        assertEquals("childfile", result);
    }

    @Test
    void normalizeHandlesBackslashes() {
        assertEquals("foo/bar/baz", PathUtils.normalize("foo\\bar\\baz"));
    }

    @Test
    void normalizeHandlesLeadingDotSlash() {
        assertEquals("foo/bar", PathUtils.normalize("./foo/bar"));
    }

    @Test
    void normalizeHandlesDoubleSlashes() {
        assertEquals("foo/bar", PathUtils.normalize("foo//bar"));
    }

    @Test
    void normalizeHandlesDotSegment() {
        assertEquals("foo/bar", PathUtils.normalize("foo/./bar"));
    }

    @Test
    void normalizePreservesTrailingSlash() {
        assertEquals("foo/bar/", PathUtils.normalize("foo/bar/"));
    }

    @Test
    void normalizeHandlesAbsoluteParent() {
        assertEquals("/bar", PathUtils.normalize("/foo/../bar"));
    }

    @Test
    void normalizeThrowsForInvalidPath() {
        assertThrows(IllegalStateException.class, () -> {
            PathUtils.normalize("/../invalid");
        });
    }

    @Test
    void normalizeHandlesMultipleParentRefs() {
        assertEquals("a/bar", PathUtils.normalize("a/b/../b/../bar"));
    }

    @Test
    void normalizeHandlesOnlyDoubleDots() {
        assertEquals("..", PathUtils.normalize(".."));
    }

    @Test
    void separatorsToUnixNoConversionNeeded() {
        assertEquals("foo/bar", PathUtils.separatorsToUnix("foo/bar"));
    }

    @Test
    void separatorsToWindowsNoConversionNeeded() {
        assertEquals("foo\\bar", PathUtils.separatorsToWindows("foo\\bar"));
    }
}
