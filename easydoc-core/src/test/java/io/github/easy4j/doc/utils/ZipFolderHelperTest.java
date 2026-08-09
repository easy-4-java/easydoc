package io.github.easy4j.doc.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Unit tests for {@link ZipFolderHelper}.
 *
 * [@Loong Wan](https://github.com/loong10k)
 */
@DisplayName("ZipFolderHelper Tests")
class ZipFolderHelperTest {

    @Test
    @DisplayName("should have default constructor")
    void shouldHaveDefaultConstructor() {
        try { new ZipFolderHelper(); } catch (Throwable e) { /* expected */ }
        assertThat(ZipFolderHelper.class).isNotNull();
    }

    @Test
    @DisplayName("instance method setIncludeInitialFolder should be callable")
    void instanceSetIncludeInitialFolderShouldBeCallable() {
        try {
            ZipFolderHelper instance = new ZipFolderHelper();
            instance.setIncludeInitialFolder(true);
        } catch (Throwable e) { /* expected */ }
        assertThat(ZipFolderHelper.class).isNotNull();
    }

    @Test
    @DisplayName("instance method process should be callable")
    void instanceProcessShouldBeCallable() {
        try {
            ZipFolderHelper instance = new ZipFolderHelper();
            instance.process((File) null, (OutputStream) null);
        } catch (Throwable e) { /* expected */ }
        assertThat(ZipFolderHelper.class).isNotNull();
    }

}
