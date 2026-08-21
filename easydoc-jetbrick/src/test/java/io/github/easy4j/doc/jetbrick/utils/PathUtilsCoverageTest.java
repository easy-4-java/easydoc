package io.github.easy4j.doc.jetbrick.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.junit.jupiter.api.Test;

/**
 * Additional tests to push JaCoCo line coverage of {@link PathUtils} above 90 %.
 * Covers jar/zip/vfs URL protocol paths in {@code urlAsPath} and the
 * empty-list + ".." branch in {@code normalize}.
 */
class PathUtilsCoverageTest {

    // ---- urlAsPath: jar protocol with "!/" separator ----

    @Test
    void urlAsPathJarProtocolStripsAfterSeparator() throws Exception {
        // URL("jar:file:/path/to/file.jar!/META-INF/resource")
        // getPath() = "file:/path/to/file.jar!/META-INF/resource"
        URL url = new URL("jar:file:/path/to/file.jar!/META-INF/resource");
        String result = PathUtils.urlAsPath(url);
        // After stripping "!/" prefix and "file:" prefix: "/path/to/file.jar"
        assertEquals("/path/to/file.jar", result);
    }

    @Test
    void urlAsPathJarProtocolWithoutFilePrefix() throws Exception {
        URL url = new URL("jar:http://example.com/lib.jar!/resource");
        String result = PathUtils.urlAsPath(url);
        assertEquals("http://example.com/lib.jar", result);
    }

    // ---- urlAsPath: zip protocol (requires custom handler) ----

    @Test
    void urlAsPathZipProtocolStripsAfterSeparator() throws Exception {
        URL url = new URL(null, "zip:file:/path/to/file.zip!/resource",
                new DummyStreamHandler());
        String result = PathUtils.urlAsPath(url);
        assertEquals("/path/to/file.zip", result);
    }

    @Test
    void urlAsPathZipProtocolWithoutSeparator() throws Exception {
        URL url = new URL(null, "zip:file:/path/to/file.zip",
                new DummyStreamHandler());
        String result = PathUtils.urlAsPath(url);
        // No "!/" → ipos == -1; starts with "file:" → strip it
        assertEquals("/path/to/file.zip", result);
    }

    // ---- urlAsPath: vfs protocol (requires custom handler) ----

    @Test
    void urlAsPathVfsProtocolWithSeparator() throws Exception {
        URL url = new URL(null, "vfs:/path/to/file.war!/WEB-INF/web.xml",
                new DummyStreamHandler());
        String result = PathUtils.urlAsPath(url);
        // ipos > 0, file.substring(0, ipos)
        assertEquals("/path/to/file.war", result);
    }

    @Test
    void urlAsPathVfsProtocolWithTrailingSlash() throws Exception {
        URL url = new URL(null, "vfs:/path/to/dir/",
                new DummyStreamHandler());
        String result = PathUtils.urlAsPath(url);
        // ipos == -1, file.endsWith("/") → strip trailing slash
        assertEquals("/path/to/dir", result);
    }

    @Test
    void urlAsPathVfsProtocolPlain() throws Exception {
        URL url = new URL(null, "vfs:/path/to/resource",
                new DummyStreamHandler());
        String result = PathUtils.urlAsPath(url);
        // ipos == -1, no trailing slash → return as-is
        assertEquals("/path/to/resource", result);
    }

    // ---- urlAsPath: unknown protocol falls through ----

    @Test
    void urlAsPathUnknownProtocolReturnsFilePath() throws Exception {
        URL url = new URL(null, "ftp://host/path/to/file",
                new DummyStreamHandler());
        String result = PathUtils.urlAsPath(url);
        // Falls through all if/else → returns file as-is
        assertEquals("/path/to/file", result);
    }

    // ---- normalize: empty list + ".." branch ----

    @Test
    void normalizeEmptyListWithDotDotKeepsParentRef() {
        // "./.." → after stripping leading "./" → ".."
        // The ".." loop processes ".." with empty list → list.isEmpty() == true → add ".."
        assertEquals("..", PathUtils.normalize("./.."));
    }

    @Test
    void normalizeMultipleParentRefsFromRelativeBase() {
        // "a/../.." → elements = ["a", "..", ".."]
        // "a" → list=["a"]; ".." → list.removeLast → list=[]; ".." → list.isEmpty → add → list=[".."]
        assertEquals("..", PathUtils.normalize("a/../.."));
    }

    // ---- getRelativePath: no separator in baseFile ----

    @Test
    void getRelativePathBaseFileWithoutSeparator() {
        // baseFile has no '/' → separatorIndex == -1 → normalize(file)
        assertEquals("foo.txt", PathUtils.getRelativePath("basefile", "foo.txt"));
    }

    // ---- DummyStreamHandler: minimal handler for constructing test URLs ----

    private static class DummyStreamHandler extends URLStreamHandler {
        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            throw new UnsupportedOperationException("test-only handler");
        }
    }
}
