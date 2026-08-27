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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.contenttype.ContentTypes;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of docx4j utils functionality.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class Docx4jUtils {

	private static final Logger LOG = LoggerFactory.getLogger(Docx4jUtils.class);

	/*
	 * 生成临时文件位置
	 *
	 * <p>P1 缺陷修复（port from 3.0.x）：旧实现基于 {@code System.currentTimeMillis()}，同一毫秒内
	 * 的两次调用会生成完全相同的路径前缀，导致并发写出时互相覆盖/损坏。
	 * 现改为 {@link Files#createTempFile} 原子占用一个唯一文件名后立即释放，
	 * 既保证唯一性又保持“返回无后缀前缀、由调用方自行追加扩展名”的历史契约。</p>
	 *
	 * @return 系统临时目录下的唯一路径前缀（不含扩展名）
	 */
	public static String getTempPath() {
		try {
			java.nio.file.Path unique = Files.createTempFile("easydoc-", null);
			Files.delete(unique);
			return unique.toString();
		} catch (IOException e) {
			// 极端文件系统异常下的兜底：随机 UUID 命名，仍可避免毫秒级碰撞
			LOG.warn("createTempFile failed, falling back to random temp path", e);
			return System.getProperty("java.io.tmpdir") + File.separator
					+ "easydoc-" + java.util.UUID.randomUUID();
		}
	}

	/**
	 * 创建全局唯一的临时输出文件（原子命名），用于无显式路径的导出重载。
	 *
	 * <p>与 {@link #getTempPath()} 不同，本方法创建的就是最终目标文件本身
	 * （含正确扩展名），因此不存在“先造前缀再拼后缀”的二次碰撞窗口。</p>
	 *
	 * <p>生命周期说明：该文件是无路径导出（如
	 * {@code writeToDocx(wmlPackage)}）交付给调用方的产物，工具层无法在其被
	 * 消费前删除；故仅做尽力而为的清理——注册 {@code deleteOnExit}，
	 * 避免调用方丢弃引用后的永久残留。需要严格控制生命周期的调用方应使用
	 * 显式路径重载并自行管理文件。</p>
	 *
	 * @param suffix 文件后缀，需以 "." 开头（如 ".docx"、".html"、".pdf"）
	 * @return 已创建（空内容）的唯一临时文件
	 * @throws IOException ：无法在系统临时目录创建文件时抛出
	 */
	public static File newTempOutputFile(String suffix) throws IOException {
		File file = Files.createTempFile("easydoc-", suffix).toFile();
		file.deleteOnExit();
		return file;
	}

    public InputStream mergeDocx(final List<InputStream> streams)  throws Docx4JException, IOException {

        WordprocessingMLPackage target = null;
        final File generated = File.createTempFile("generated", ".docx");

        int chunkId = 0;
        Iterator<InputStream> it = streams.iterator();
        while (it.hasNext()) {
            InputStream is = it.next();
            if (is != null) {
                if (target == null) {
                    // Copy first (master) document — stream rather than buffering twice on the heap.
                    try (OutputStream os = new FileOutputStream(generated)) {
                        IOUtils.copy(is, os);
                    }

                    target = WordprocessingMLPackage.load(generated);
                } else {
                    // Attach the others (Alternative input parts)
                    insertDocx(target.getMainDocumentPart(),
                            IOUtils.toByteArray(is), chunkId++);
                }
            }
        }

        if (target != null) {
            target.save(generated);
            // close() 时删除临时文件：调用方读完关闭流即可回收，不再依赖
            // deleteOnExit（长生命周期服务/虚拟线程场景下会无限累积临时文件）；
            // 即使调用方忘记 close，也只在本次 JVM 留一个孤儿文件而非全部依赖 JVM 退出
            return new DeleteOnCloseFileInputStream(generated);
        } else {
            generated.delete();
            return null;
        }
    }

    /**
     * close() 时删除底层临时文件的 FileInputStream。用于 mergeDocx 的返回值，
     * 使临时文件生命周期与流一致：调用方关闭流即回收磁盘文件。
     */
    private static final class DeleteOnCloseFileInputStream extends FileInputStream {

        private final File file;
        private boolean closed = false;

        DeleteOnCloseFileInputStream(File file) throws IOException {
            super(file);
            this.file = file;
        }

        @Override
        public void close() throws IOException {
            try {
                super.close();
            } finally {
                if (!closed) {
                    closed = true;
                    if (!file.delete() && file.exists()) {
                        LOG.warn("Failed to delete temp file {} after stream close", file.getAbsolutePath());
                    }
                }
            }
        }
    }

    // 插入文档
    private void insertDocx(MainDocumentPart main, byte[] bytes, int chunkId) {
        try {
            AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/part" + chunkId + ".docx"));
            afiPart.setContentType(new ContentType(ContentTypes.APPLICATION_XML));
            afiPart.setBinaryData(bytes);
            Relationship altChunkRel = main.addTargetPart(afiPart);

            CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
            chunk.setId(altChunkRel.getId());

            main.addObject(chunk);
        } catch (Exception e) {
            LOG.error("Failed to insert docx chunk {}", chunkId, e);
        }
    }

    public static void toP(WordprocessingMLPackage wordMLPackage,String outPath) throws Exception{
        try (OutputStream os = new FileOutputStream(outPath)) {
            FOSettings foSettings = Docx4J.createFOSettings();
            foSettings.setWmlPackage(wordMLPackage);
            Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);
        }
    }

}
