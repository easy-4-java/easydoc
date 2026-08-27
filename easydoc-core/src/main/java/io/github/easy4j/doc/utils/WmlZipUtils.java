/**
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

/**
 * Implementation of wml zip utils functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class WmlZipUtils {

	private static final ZipFolderHelper helper = new ZipFolderHelper();
	private static final int BUFFER = 1024;

	public static void zipDir(String dirToZip, String destFile, boolean includeInitialFolder) throws Exception {
		zipDir(new File(dirToZip), new File(destFile), includeInitialFolder);
	}

	public static void zipDir(String dirToZip, String destFile) throws Exception {
		zipDir(new File(dirToZip), new File(destFile));
	}

	public static void zipDir(File dirToZip, File destFile) throws Exception {
		zipDir(dirToZip, destFile, true);
	}

	public static void zipDir(File dirToZip, File destFile, boolean includeInitialFolder) throws Exception {
		try (OutputStream output = new FileOutputStream(destFile);){
			// helper 是共享静态实例，其 includeInitialFolder 为运行期可变状态；
			// 并发压缩时存在标志位互相覆盖的竞态（A 线程设置的标志可能被 B 线程
			// 在 process 中途改写）。以 helper 为锁把“设置标志 + 处理”合并为
			// 临界区串行化，保证单次压缩过程中标志不会被并发修改。
			// 注：若未来需要消除这段串行化，可将 includeInitialFolder 作为参数
			// 传入 ZipFolderHelper 的调用链（使其无状态），该类不在本修复的改动范围。
			synchronized (helper) {
				helper.setIncludeInitialFolder(includeInitialFolder);
				helper.process(dirToZip, output);
			}
		}
	}

	public static void zipDir(File dirToZip, OutputStream output,boolean includeInitialFolder) throws Exception {
		// 同上：临界区化，防止并发场景下标志位互相覆盖
		synchronized (helper) {
			helper.setIncludeInitialFolder(includeInitialFolder);
			helper.process(dirToZip, output);
		}
	}

	public static void unzip(String sourceFile, String outputDir) throws IOException {
		unzip(new File(sourceFile), new File(outputDir));
	}

	public static void unzip(File sourceFile, File outputDir) throws ZipException, IOException {
		FileUtils.deleteDirectory(outputDir);
		// P1 资源修复（#14）：ZipFile 持有底层文件句柄，原实现靠 @SuppressWarnings("resource")
		// 压制告警且从不关闭；try-with-resources 保证任何路径（含异常）都释放句柄。
		try (ZipFile zipFile = new ZipFile(sourceFile)) {
			List<? extends ZipEntry> entries = Collections.list(zipFile.entries());
			byte[] buffer = new byte[BUFFER];
			for (ZipEntry entry : entries) {
				File f = new File(outputDir, entry.getName());
				// Zip Slip 防护：规范化后必须仍在目标目录内，拒绝路径穿越条目
				String canonicalBase = outputDir.getCanonicalPath();
				String canonicalTarget = f.getCanonicalPath();
				if (!canonicalTarget.startsWith(canonicalBase + File.separator)
						&& !canonicalTarget.equals(canonicalBase)) {
					throw new IOException("Zip entry escapes target directory: " + entry.getName());
				}
				if (entry.isDirectory()) {
					f.mkdirs();
					continue;
				}
				f.getParentFile().mkdirs();
				f.createNewFile();
				// 条目输入流与文件输出流均为一次性局部资源，各自用
				// try-with-resources 确保逐条释放（防止句柄在长压缩包场景堆积）
				try (InputStream eis = zipFile.getInputStream(entry);
						OutputStream fos = new FileOutputStream(f)) {
					int bytesRead;
					while ((bytesRead = eis.read(buffer)) != -1) {
						fos.write(buffer, 0, bytesRead);
					}
				}
			}
		}
	}
	
}
