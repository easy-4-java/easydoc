package io.github.easy4j.doc.wml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.P;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link DocxElementWmlRender#newCell(Tr, String)} 缺陷回归测试（P0，port from 3.0.x）。
 *
 * <p>修复背景：原实现误写为 {@code tbCell.getContent().add(tbCell)}，
 * 新单元格被挂到自己的内容列表上，行内容始终为空；同时单元格自引用
 * 在文档编组/保存时存在无限递归风险。修复后与
 * {@link DocxElementWmlRender#newCell(Tbl, int, String)} 语义一致：
 * 单元格追加到目标行。</p>
 */
@DisplayName("DocxElementWmlRender.newCell(Tr, String) regression tests")
class DocxElementWmlRenderNewCellRegressionTest {

    @Test
    @DisplayName("newCell(Tr, String) appends the cell to the target row")
    void newCellAddsCellToRow() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        DocxElementWmlRender render = new DocxElementWmlRender(pkg);
        Tbl table = render.newTable(1, 0);
        Tr row = render.getRow(table, 0);

        assertEquals(0, row.getContent().size(), "初始行为空");

        Tc cell = render.newCell(row, "content");
        assertEquals(1, row.getContent().size(), "newCell 后行内容应恰好新增一个单元格");
        assertSame(cell, row.getContent().get(0), "行中的元素应为刚创建的单元格对象");
    }

    @Test
    @DisplayName("newCell(Tr, String) cell must not contain itself (no self-reference)")
    void newCellDoesNotSelfReference() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        DocxElementWmlRender render = new DocxElementWmlRender(pkg);
        Tr row = new Tr();

        Tc cell = render.newCell(row, "content");

        assertEquals(1, cell.getContent().size(), "单元格内容应只包含新建段落");
        assertTrue(cell.getContent().get(0) instanceof P, "唯一内容应为段落 P");
    }

    @Test
    @DisplayName("newTable(row, cell) produces rows x cells and mirrors (Tbl,int,String) overload")
    void newTableProducesExpectedCells() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        DocxElementWmlRender render = new DocxElementWmlRender(pkg);

        Tbl table = render.newTable(3, 4);
        assertEquals(3, table.getContent().size(), "表格应有 3 行");
        int totalCells = 0;
        for (Object obj : table.getContent()) {
            assertTrue(obj instanceof Tr, "每行都应是 Tr");
            Tr tr = (Tr) obj;
            assertEquals(4, tr.getContent().size(), "每行应有 4 个单元格");
            for (Object c : tr.getContent()) {
                assertTrue(c instanceof Tc, "单元格类型应为 Tc");
                // 自引用防护：任何单元格的内容里都不应包含其自身
                for (Object inner : ((Tc) c).getContent()) {
                    assertTrue(inner instanceof P, "单元格内容只应包含段落");
                }
                totalCells++;
            }
        }
        assertEquals(12, totalCells, "总单元格数应为 3 x 4");

        // 与正确的 (Tbl,int,String) 重载对齐：在同一行上新旧两种调用互不干扰
        render.newCell(table, 0, "appended");
        assertEquals(5, ((Tr) table.getContent().get(0)).getContent().size(),
                "(Tbl,int,String) 重载也应继续追加到第 0 行");
    }

    @Test
    @DisplayName("document containing a newCell-built table still marshals (no infinite recursion)")
    void documentWithBuiltTableMarshals() throws Exception {
        WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
        DocxElementWmlRender render = new DocxElementWmlRender(pkg);
        Tbl table = render.newTable(2, 2);
        pkg.getMainDocumentPart().getContent().add(table);
        // 修复前：单元格自引用会在编组时引发无限递归；修复后必须可正常序列化
        String xml = pkg.getMainDocumentPart().getXML();
        assertTrue(xml.length() > 0, "文档部件应可序列化");
        assertTrue(xml.contains("tbl"), "序列化结果应包含表格元素");
    }
}
