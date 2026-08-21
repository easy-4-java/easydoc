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
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 处理文件压缩
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class ZipFolderHelper {   
	
    private boolean includeInitialFolder;   
    public ZipFolderHelper() {   
    }   
    
    public void setIncludeInitialFolder(boolean includeInitialFolder) {   
        this.includeInitialFolder = includeInitialFolder;   
    }   
    
    public void process(File folderToZip, OutputStream output) throws Exception {   
    	ZipOutputStream zip = new ZipOutputStream(output);   
		addFolderToZip("", folderToZip.getPath(), zip);   
		zip.flush();
    }
    
    private void addFileToZip(String path, String srcFile, ZipOutputStream zip)  throws Exception {   
        File folder = new File(srcFile);   
        if (folder.isDirectory()) {   
            addFolderToZip(path, srcFile, zip);   
        } else {   
            FileInputStream in = null;   
            try {   
                byte[] buf = new byte[1024];   
                int len;   
                in = new FileInputStream(srcFile);   
                String zeName = path + File.separator + folder.getName();   
                if (!includeInitialFolder) {   
                    int idx = zeName.indexOf(File.separator);   
                    zeName = zeName.substring(idx + 1);   
                }   
                zip.putNextEntry(new ZipEntry(zeName));   
                while ((len = in.read(buf)) > 0) {   
                    zip.write(buf, 0, len);   
                }   
            } finally {   
                if (in != null) {   
                    in.close();   
                }   
            }   
        }   
    }   
    
    private void addFolderToZip(String path, String srcFolder,ZipOutputStream zip) throws Exception {   
        File folder = new File(srcFolder);   
        for (String fileName : folder.list()) {   
            if (path.equals("")) {   
                addFileToZip(folder.getName(), srcFolder + File.separator + fileName, zip);   
            } else {   
                addFileToZip(path + File.separator + folder.getName(), srcFolder + File.separator + fileName, zip);   
            }   
        }   
    }   
}  