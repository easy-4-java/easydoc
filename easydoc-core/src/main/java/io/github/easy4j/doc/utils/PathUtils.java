/**
 * Copyright 2013-2016 Guoqiang Chen, Shanghai, China. All rights reserved.
 *
 *   Author: Guoqiang Chen
 *    Email: subchen@gmail.com
 *   WebURL: https://github.com/subchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.easy4j.doc.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

public final class PathUtils {

    public static URL fileAsUrl(String file) {
        return fileAsUrl(new File(file));
    }

    public static URL fileAsUrl(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    /*
     * Returns normalized <code>path</code> (or simply the <code>path</code> if
     * it is already in normalized form). Normalized path does not contain any
     * empty or "." segments or ".." segments preceded by other segment than
     * "..".
     *
     * @param originPath path to normalize
     * @return normalize path
     */
    public static String normalize(final String originPath) {
        String path = originPath;

        if (path == null) {
            return null;
        }

        if (path.indexOf('\\') != -1) {
            path = path.replace('\\', '/');
        }

        while (true) {
            int pos = path.indexOf("./");
            if (pos == 0) {
                path = path.substring(2);
                continue;
            }
            if (pos == -1 && !path.contains("//")) {
                return path;
            }
            break;
        }

        boolean absolute = path.startsWith("/");
        boolean dir = path.endsWith("/");
        String[] elements = path.split("/");
        LinkedList<String> list = new LinkedList<String>();
        for (String e : elements) {
            if ("..".equals(e)) {
                if (list.isEmpty() || "..".equals(list.getLast())) {
                    list.add(e);
                } else {
                    list.removeLast();
                }
            } else if (!".".equals(e) && !e.isEmpty()) {
                list.add(e);
            }
        }

        StringBuilder sb = new StringBuilder(path.length());
        if (absolute) {
            sb.append('/');
        }
        int count = 0, last = list.size() - 1;
        for (String e : list) {
            sb.append(e);
            if (count++ < last) {
                sb.append('/');
            }
        }
        if (dir && list.size() > 0) {
            sb.append('/');
        }

        path = sb.toString();
        if (path.startsWith("/..")) {
            throw new IllegalStateException("invalid path: " + originPath);
        }

        return path;
    }

    /*
     * 组合路径.
     */
    public static String concat(final String parent, final String child) {
        if (parent == null) {
            return normalize(child);
        }
        if (child == null) {
            return normalize(parent);
        }
        return normalize(parent + '/' + child);
    }

    /*
     * 计算相对路径.
     */
    public static String getRelativePath(final String baseFile, final String file) {
        if (file.startsWith("/")) {
            return normalize(file);
        }
        int separatorIndex = baseFile.lastIndexOf('/');
        if (separatorIndex != -1) {
            String newPath = baseFile.substring(0, separatorIndex + 1);
            return normalize(newPath + file);
        } else {
            return normalize(file);
        }
    }

    /*
     * 转为 Unix 样式的路径.
     */
    public static String separatorsToUnix(String path) {
        if (path == null || path.indexOf('\\') == -1) {
            return path;
        }
        return path.replace('\\', '/');
    }

    /*
     * 转为 Windows 样式的路径.
     */
    public static String separatorsToWindows(String path) {
        if (path == null || path.indexOf('/') == -1) {
            return path;
        }
        return path.replace('/', '\\');
    }

    /*
     * 转为系统默认样式的路径.
     */
    public static String separatorsToSystem(String path) {
        if (path == null) {
            return null;
        }
        if (File.separatorChar == '\\') {
            return separatorsToWindows(path);
        }
        return separatorsToUnix(path);
    }
}
