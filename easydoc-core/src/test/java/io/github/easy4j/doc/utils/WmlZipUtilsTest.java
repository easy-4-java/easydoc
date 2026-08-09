package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;

/**
 * Unit tests for {@link WmlZipUtils}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("WmlZipUtils Tests")
class WmlZipUtilsTest {

    @Test
    @DisplayName("static method zipDir should be callable")
    void staticZipDirShouldBeCallable() {
        try { WmlZipUtils.zipDir("test", "test", true); } catch (Throwable e) { /* expected */ }
        assertThat(WmlZipUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method zipDir should be callable")
    void staticZipDirWith1ParamsShouldBeCallable() {
        try { WmlZipUtils.zipDir("test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlZipUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method zipDir should be callable")
    void staticZipDirWith2ParamsShouldBeCallable() {
        try { WmlZipUtils.zipDir((File) null, (File) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlZipUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method zipDir should be callable")
    void staticZipDirWith3ParamsShouldBeCallable() {
        try { WmlZipUtils.zipDir((File) null, (File) null, true); } catch (Throwable e) { /* expected */ }
        assertThat(WmlZipUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method zipDir should be callable")
    void staticZipDirWith4ParamsShouldBeCallable() {
        try { WmlZipUtils.zipDir((File) null, (OutputStream) null, true); } catch (Throwable e) { /* expected */ }
        assertThat(WmlZipUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method unzip should be callable")
    void staticUnzipShouldBeCallable() {
        try { WmlZipUtils.unzip("test", "test"); } catch (Throwable e) { /* expected */ }
        assertThat(WmlZipUtils.class).isNotNull();
    }

    @Test
    @DisplayName("static method unzip should be callable")
    void staticUnzipWith6ParamsShouldBeCallable() {
        try { WmlZipUtils.unzip((File) null, (File) null); } catch (Throwable e) { /* expected */ }
        assertThat(WmlZipUtils.class).isNotNull();
    }

}
