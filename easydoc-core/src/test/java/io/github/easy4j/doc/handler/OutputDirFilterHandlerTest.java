package io.github.easy4j.doc.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OutputDirFilterHandler Tests")
class OutputDirFilterHandlerTest {

    @Test
    void acceptReturnsTrueForMatchingDirectory(@TempDir Path tempDir) throws IOException {
        String dirName = "images";
        File dir = tempDir.resolve(dirName).toFile();
        dir.mkdir();
        FileFilter filter = new OutputDirFilterHandler(dirName);
        assertTrue(filter.accept(dir));
    }

    @Test
    void acceptReturnsFalseForNonMatchingDirectory(@TempDir Path tempDir) throws IOException {
        File dir = tempDir.resolve("other").toFile();
        dir.mkdir();
        FileFilter filter = new OutputDirFilterHandler("images");
        assertFalse(filter.accept(dir));
    }

    @Test
    void acceptReturnsFalseForFile(@TempDir Path tempDir) throws IOException {
        File file = tempDir.resolve("images").toFile();
        file.createNewFile();
        FileFilter filter = new OutputDirFilterHandler("images");
        assertFalse(filter.accept(file));
    }

    @Test
    void acceptReturnsFalseForMatchingNameButNotDirectory(@TempDir Path tempDir) throws IOException {
        File file = tempDir.resolve("outdir").toFile();
        file.createNewFile();
        FileFilter filter = new OutputDirFilterHandler("outdir");
        assertFalse(filter.accept(file));
    }
}
