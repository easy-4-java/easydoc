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
 * Implementation of wml zip utils functionality.
 *
 * [@Loong Wan](https://github.com/loong10k)
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
			helper.setIncludeInitialFolder(includeInitialFolder);
			helper.process(dirToZip, output);
		}
	}
	
	public static void zipDir(File dirToZip, OutputStream output,boolean includeInitialFolder) throws Exception {
		helper.setIncludeInitialFolder(includeInitialFolder);
		helper.process(dirToZip, output);
	}

	public static void unzip(String sourceFile, String outputDir) throws IOException {
		unzip(new File(sourceFile), new File(outputDir));
	}

	@SuppressWarnings("resource")
	public static void unzip(File sourceFile, File outputDir) throws ZipException, IOException {
		FileUtils.deleteDirectory(outputDir);
		ZipFile zipFile = new ZipFile(sourceFile);
		Enumeration<?> files = zipFile.entries();
		File f = null;
		FileOutputStream fos = null;
		while (files.hasMoreElements()) {
			try {
				ZipEntry entry = (ZipEntry) files.nextElement();
				InputStream eis = zipFile.getInputStream(entry);
				byte[] buffer = new byte[BUFFER];
				int bytesRead = 0;
				f = new File(outputDir.getAbsolutePath() + File.separator + entry.getName());
				if (entry.isDirectory()) {
					f.mkdirs();
					continue;
				} else {
					f.getParentFile().mkdirs();
					f.createNewFile();
				}
				fos = new FileOutputStream(f);
				while ((bytesRead = eis.read(buffer)) != -1) {
					fos.write(buffer, 0, bytesRead);
				}
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
	}
	
}
