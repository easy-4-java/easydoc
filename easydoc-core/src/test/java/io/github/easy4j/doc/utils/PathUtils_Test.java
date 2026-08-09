/*
 * Copyright (c) 2024, hiwepy (https://github.com/easy-4-java).
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

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.net.URL;

/**
 * Unit tests for {@link PathUtils}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class PathUtils_Test {

    @Test
    void test_fileAsUrl_withString() {
        URL url = PathUtils.fileAsUrl("/tmp/test.txt");
        assertThat(url).isNotNull();
        assertThat(url.getProtocol()).isEqualTo("file");
    }

    @Test
    void test_fileAsUrl_withFile() {
        URL url = PathUtils.fileAsUrl(new File("/tmp/test.txt"));
        assertThat(url).isNotNull();
    }

    @Test
    void test_normalize_withNull() {
        assertThat(PathUtils.normalize(null)).isNull();
    }

    @Test
    void test_normalize_withSimplePath() {
        assertThat(PathUtils.normalize("a/b/c")).isEqualTo("a/b/c");
    }

    @Test
    void test_normalize_withBackslashes() {
        assertThat(PathUtils.normalize("a\\b\\c")).isEqualTo("a/b/c");
    }

    @Test
    void test_normalize_withDotSlash() {
        assertThat(PathUtils.normalize("./a/b")).isEqualTo("a/b");
    }

    @Test
    void test_normalize_withDoubleDots() {
        assertThat(PathUtils.normalize("a/b/../c")).isEqualTo("a/c");
    }

    @Test
    void test_normalize_withDoubleSlashes() {
        assertThat(PathUtils.normalize("a//b")).isEqualTo("a/b");
    }

    @Test
    void test_normalize_withAbsolutePath() {
        assertThat(PathUtils.normalize("/a/b/c")).isEqualTo("/a/b/c");
    }

    @Test
    void test_normalize_withTrailingSlash() {
        assertThat(PathUtils.normalize("a/b/")).isEqualTo("a/b/");
    }

    @Test
    void test_normalize_withDotSegments() {
        assertThat(PathUtils.normalize("a/./b")).isEqualTo("a/b");
    }

    @Test
    void test_concat_withBothNull() {
        assertThat(PathUtils.concat(null, null)).isNull();
    }

    @Test
    void test_concat_withParentNull() {
        assertThat(PathUtils.concat(null, "child")).isEqualTo("child");
    }

    @Test
    void test_concat_withChildNull() {
        assertThat(PathUtils.concat("parent", null)).isEqualTo("parent");
    }

    @Test
    void test_concat_withBoth() {
        assertThat(PathUtils.concat("parent", "child")).isEqualTo("parent/child");
    }

    @Test
    void test_getRelativePath_withAbsolutePath() {
        assertThat(PathUtils.getRelativePath("base/file", "/absolute/path")).isEqualTo("/absolute/path");
    }

    @Test
    void test_getRelativePath_withRelativePath() {
        assertThat(PathUtils.getRelativePath("base/file.txt", "other.txt")).isEqualTo("base/other.txt");
    }

    @Test
    void test_separatorsToUnix_withNull() {
        assertThat(PathUtils.separatorsToUnix(null)).isNull();
    }

    @Test
    void test_separatorsToUnix_withBackslashes() {
        assertThat(PathUtils.separatorsToUnix("a\\b\\c")).isEqualTo("a/b/c");
    }

    @Test
    void test_separatorsToUnix_withNoBackslashes() {
        assertThat(PathUtils.separatorsToUnix("a/b/c")).isEqualTo("a/b/c");
    }

    @Test
    void test_separatorsToWindows_withNull() {
        assertThat(PathUtils.separatorsToWindows(null)).isNull();
    }

    @Test
    void test_separatorsToWindows_withSlashes() {
        assertThat(PathUtils.separatorsToWindows("a/b/c")).isEqualTo("a\\b\\c");
    }

    @Test
    void test_separatorsToSystem_withNull() {
        assertThat(PathUtils.separatorsToSystem(null)).isNull();
    }

    @Test
    void test_separatorsToSystem_withPath() {
        String result = PathUtils.separatorsToSystem("a/b/c");
        assertThat(result).isNotNull();
    }
}
