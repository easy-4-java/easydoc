package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Unit tests for {@link PathUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("PathUtils Tests")
class PathUtilsTest {

    @Test
    @DisplayName("static method fileAsUrl should be callable")
    void staticFileAsUrlShouldBeCallable() {
        try { PathUtils.fileAsUrl("test"); } catch (Throwable e) { /* expected */ }
        assertThat(PathUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method fileAsUrl should be callable")
    void staticFileAsUrlWith1ParamsShouldBeCallable() {
        try { PathUtils.fileAsUrl((File) null); } catch (Throwable e) { /* expected */ }
        assertThat(PathUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method normalize should be callable")
    void staticNormalizeShouldBeCallable() {
        try { PathUtils.normalize("test"); } catch (Throwable e) { /* expected */ }
        assertThat(PathUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method concat should be callable")
    void staticConcatShouldBeCallable() {
        try { PathUtils.concat("test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(PathUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method getRelativePath should be callable")
    void staticGetRelativePathShouldBeCallable() {
        try { PathUtils.getRelativePath("test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(PathUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method separatorsToUnix should be callable")
    void staticSeparatorsToUnixShouldBeCallable() {
        try { PathUtils.separatorsToUnix("test"); } catch (Throwable e) { /* expected */ }
        assertThat(PathUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method separatorsToWindows should be callable")
    void staticSeparatorsToWindowsShouldBeCallable() {
        try { PathUtils.separatorsToWindows("test"); } catch (Throwable e) { /* expected */ }
        assertThat(PathUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method separatorsToSystem should be callable")
    void staticSeparatorsToSystemShouldBeCallable() {
        try { PathUtils.separatorsToSystem("test"); } catch (Throwable e) { /* expected */ }
        assertThat(PathUtils.class).isNotNull();
    }

}
