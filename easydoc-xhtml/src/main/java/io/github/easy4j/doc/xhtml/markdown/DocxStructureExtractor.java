package io.github.easy4j.doc.xhtml.markdown;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.docx4j.dml.CTBlip;
import org.docx4j.dml.CTBlipFillProperties;
import org.docx4j.dml.Graphic;
import org.docx4j.dml.picture.Pic;
import org.docx4j.dml.wordprocessingDrawing.Anchor;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DocPropsCorePart;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.wml.Body;
import org.docx4j.wml.Br;
import org.docx4j.wml.Document;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.Lvl;
import org.docx4j.wml.NumberFormat;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.SdtElement;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DOCX 结构抽取器（OOXML 直接遍历核心）：把 {@link WordprocessingMLPackage} 的
 * body 按 标题/段落/列表/表格/图片 拆成结构化 POJO 树（{@link DocxDocument}）。
 *
 * <p>解析与降级规则：</p>
 * <ul>
 * <li>标题：pStyle 值不区分大小写匹配 Heading1-9 → level 1-9，Title → level 0，
 * 其余样式一律按普通段落处理；标题文本取拼接后的纯文本，若含超链接则带首个链接地址。</li>
 * <li>列表（扁平合并规则）：numPr.numId 连续相同的段落归为一次列表 run；同一 run 内
 * 按 ilvl 分桶，run 结束时按 ilvl 升序各输出一个 {@link DocxList}
 * （ilvl 0 的在前、indent=ilvl，实现"嵌套通过缩进表达"的扁平模型）；
 * 深浅层级交替时同一 ilvl 桶内的项仍合并为一个 {@link DocxList}；
 * 有序/无序由 numbering part 中对应 lvl 的 numFmt 决定：凡可解析且非 BULLET
 * （DECIMAL、LOWER_ROMAN、lowerLetter 等全部枚举值）一律视为有序 —— Markdown 只有一种
 * 有序列表语法，把 i/ii/iii 等样式降级为无序会损失更多保真度；仅当编号定义不可解析时
 * （numbering part 整体缺失、numId 未映射（悬空）、lvl 或 numFmt 缺失）才降级为无序并
 * LOG.debug。夹在两次列表 run 之间的普通段落按文档顺序落地：关闭当前 run 并输出自身，
 * 下一次列表 run 另起新段。</li>
 * <li>表格：OOXML 无真实表头语义，采用"首行作表头"约定，其余行为数据行；
 * 单元格取全部后代文本（嵌套表格内文字一并扁平化，单元格内换行与 Tab 折叠为空格）。</li>
 * <li>图片：w:drawing → a:blip r:embed → BinaryPart 字节内联为 data URI；
 * alt 取 docPr descr（其次 name）；blip/关系/字节任一环失败仅 LOG.debug 并跳过该图片；
 * 列表段落中的图片延后到其所属列表输出之后，避免图片插进列表 Markdown 片段中间。</li>
 * <li>失败降级：单个顶层元素解析异常时 LOG.warn 并继续（跳过坏元素不中断全文）；
 * 空段落（无可见文本且无图片）整体跳过。</li>
 * </ul>
 */
public final class DocxStructureExtractor {

	private static final Logger LOG = LoggerFactory.getLogger(DocxStructureExtractor.class);

	/** 标题样式正则：Heading1 / heading 1 等，捕获层级数字（样式名不区分大小写）。 */
	private static final java.util.regex.Pattern HEADING_STYLE =
			java.util.regex.Pattern.compile("^heading\\s*([1-9])$",
					java.util.regex.Pattern.CASE_INSENSITIVE);

	/** 文档标题样式名（level 0）。 */
	private static final String TITLE_STYLE = "title";

	private DocxStructureExtractor() {
	}

	/** DOCX 文件 → 结构化文档。null 输入抛 NPE；文件不存在抛 IOException；损坏包包装为 IOException。 */
	public static DocxDocument extract(File docx) throws IOException {
		Objects.requireNonNull(docx, "docx must not be null");
		if (!docx.isFile()) {
			throw new IOException("DOCX not found: " + docx.getAbsolutePath());
		}
		try (InputStream in = new FileInputStream(docx)) {
			return extract(in);
		}
	}

	/** DOCX 输入流 → 结构化文档（流由本方法负责关闭）。null 输入抛 NPE；损坏包包装为 IOException。 */
	public static DocxDocument extract(InputStream in) throws IOException {
		Objects.requireNonNull(in, "in must not be null");
		try (InputStream closeable = in) {
			try {
				return extract(WordprocessingMLPackage.load(closeable));
			} catch (Docx4JException | RuntimeException ex) {
				throw new IOException("Failed to load DOCX: " + ex.getMessage(), ex);
			}
		}
	}

	/**
	 * 已加载包 → 结构化文档（包私有入口，供测试直接构造对象树复用）。
	 * 元数据填 title/author/modified；核心属性缺失或 modified 非 ISO-8601 时保持 null。
	 */
	static DocxDocument extract(WordprocessingMLPackage pkg) {
		Objects.requireNonNull(pkg, "pkg must not be null");
		ParseContext ctx = new ParseContext();
		MainDocumentPart main = pkg.getMainDocumentPart();
		ctx.attach(main);

		for (Object child : bodyChildren(pkg)) {
			try {
				processBlockChild(child, ctx);
			} catch (Exception ex) {
				LOG.warn("Skipping malformed element: {}", ex.getMessage());
			}
		}
		ctx.flushPendingList();

		org.docx4j.docProps.core.CoreProperties props = corePropertiesOf(pkg);
		return new DocxDocument(titleOf(pkg), props == null ? null : literalOf(props.getCreator()),
				modifiedOf(props), ctx.elements);
	}

	// ==================== 包级骨架 ====================

	private static List<Object> bodyChildren(WordprocessingMLPackage pkg) {
		try {
			if (pkg.getMainDocumentPart() == null) {
				return Collections.emptyList();
			}
			Document wml = pkg.getMainDocumentPart().getContents();
			if (wml == null || wml.getBody() == null || wml.getBody().getContent() == null) {
				return Collections.emptyList();
			}
			return wml.getBody().getContent();
		} catch (Docx4JException ex) {
			LOG.debug("Unable to read document body: {}", ex.getMessage());
			return Collections.emptyList();
		}
	}

	/** 顶层内容分发：P/Tbl/SdtBlock 走解析，其余静默忽略。 */
	private static void processBlockChild(Object child, ParseContext ctx) {
		Object childValue = unwrap(child);
		if (childValue instanceof P) {
			handleParagraph((P) childValue, ctx);
		} else if (childValue instanceof Tbl) {
			ctx.flushPendingList();
			HeadersAndRows table = parseTable((Tbl) childValue);
			if (table != null) {
				ctx.elements.add(new DocxTable(table.headers, table.rows));
			}
		} else if (childValue instanceof SdtElement) {
			handleSdt((SdtElement) childValue, ctx);
		}
	}

	/** w:t/w:br 等全局元素经 load 后可能带 JAXBElement 包装，统一解包。 */
	private static Object unwrap(Object item) {
		return item instanceof javax.xml.bind.JAXBElement
				? ((javax.xml.bind.JAXBElement<?>) item).getValue() : item;
	}

	/** 内容控件：递归其 sdtContent 内的块级子元素（嵌套 sdt 同样支持，沿用降级规则）。 */
	private static void handleSdt(SdtElement sdt, ParseContext ctx) {
		if (sdt.getSdtContent() == null || sdt.getSdtContent().getContent() == null) {
			return;
		}
		for (Object inner : sdt.getSdtContent().getContent()) {
			try {
				processBlockChild(unwrap(inner), ctx);
			} catch (Exception ex) {
				LOG.warn("Skipping malformed element inside SDT: {}", ex.getMessage());
			}
		}
	}

	// ==================== 段落 ====================

	private static void handleParagraph(P p, ParseContext ctx) {
		int headingLevel = headingLevelOf(p);
		ParaContent pc = new ParaContent();
		parseInline(p.getContent(), false, false, false, null, pc, ctx);

		if (headingLevel >= 0) {
			ctx.flushPendingList();
			// 标题必须单行：正文中的换行/制表（含首个子元素即换行的场景）折叠为单空格并 trim
			String headingText = MarkdownEscaper.collapseLineBreaks(plainText(pc.spans));
			ctx.elements.add(new DocxHeading(headingLevel, headingText, firstLink(pc.spans)));
			appendImages(pc.images, ctx);
			return;
		}

		BigInteger numId = numIdOf(p.getPPr());
		if (numId != null) {
			int ilvl = ilvlOf(p.getPPr());
			ctx.openOrReuseList(numId, ilvl);
			ctx.appendListItem(pc.spans);
			ctx.deferImages(pc.images);
			return;
		}

		boolean visible = hasVisibleText(pc.spans);
		if (!visible && pc.images.isEmpty()) {
			return; // 空段落（含纯 Tab/换行）整体跳过
		}
		ctx.flushPendingList();
		if (visible) {
			ctx.elements.add(new DocxParagraph(pc.spans));
		}
		appendImages(pc.images, ctx);
	}

	private static void appendImages(List<DocxImage> images, ParseContext ctx) {
		for (DocxImage image : images) {
			ctx.elements.add(image);
		}
	}

	/** HeadingX → X、Title → 0；非标题返回 -1。pPr/pStyle 可能为 null。 */
	private static int headingLevelOf(P p) {
		if (p.getPPr() == null || p.getPPr().getPStyle() == null) {
			return -1;
		}
		String val = p.getPPr().getPStyle().getVal();
		if (val == null) {
			return -1;
		}
		String style = val.trim();
		if (TITLE_STYLE.equalsIgnoreCase(style)) {
			return 0;
		}
		java.util.regex.Matcher matcher = HEADING_STYLE.matcher(style);
		if (matcher.matches()) {
			return Integer.parseInt(matcher.group(1));
		}
		return -1;
	}

	private static BigInteger numIdOf(org.docx4j.wml.PPrBase pPr) {
		if (pPr == null || pPr.getNumPr() == null || pPr.getNumPr().getNumId() == null) {
			return null;
		}
		return pPr.getNumPr().getNumId().getVal();
	}

	private static int ilvlOf(org.docx4j.wml.PPrBase pPr) {
		if (pPr == null || pPr.getNumPr() == null
				|| pPr.getNumPr().getIlvl() == null || pPr.getNumPr().getIlvl().getVal() == null) {
			return 0;
		}
		return pPr.getNumPr().getIlvl().getVal().intValue();
	}

	// ==================== 行内内容 ====================

	/**
	 * 深度遍历行内内容收集片段与图片。
	 * 格式只取 Run 局部 b/i/u（不做样式继承）；超链接文本以其 rel id 对应目标地址包裹。
	 */
	private static void parseInline(List<Object> content, boolean bold, boolean italic,
			boolean underline, String linkUrl, ParaContent out, ParseContext ctx) {
		if (content == null) {
			return;
		}
		for (Object rawItem : content) {
			Object item = unwrap(rawItem);
			if (item instanceof R) {
				R r = (R) item;
				org.docx4j.wml.RPr rPr = r.getRPr();
				parseInline(r.getContent(),
						bold || boolFlag(rPr == null ? null : rPr.getB()),
						italic || boolFlag(rPr == null ? null : rPr.getI()),
						underline || underlineFlag(rPr == null ? null : rPr.getU()),
						linkUrl, out, ctx);
			} else if (item instanceof P.Hyperlink) {
				P.Hyperlink link = (P.Hyperlink) item;
				String target = resolveLink(link.getId(), ctx);
				parseInline(link.getContent(), bold, italic, underline,
						target == null || target.isEmpty() ? null : target, out, ctx);
			} else if (item instanceof Text) {
				Text t = (Text) item;
				out.spans.add(new InlineSpan(t.getValue() == null ? "" : t.getValue(),
						bold, italic, underline, linkUrl));
			} else if (item instanceof R.Tab) {
				out.spans.add(new InlineSpan("\t", bold, italic, underline, linkUrl));
			} else if (item instanceof Br) {
				out.spans.add(new InlineSpan("\n", bold, italic, underline, linkUrl)); // 含 page break
			} else if (item instanceof Drawing) {
				DocxImage image = parseImage((Drawing) item, ctx);
				if (image != null) {
					out.images.add(image);
				}
			} else if (item instanceof SdtElement && ((SdtElement) item).getSdtContent() != null) {
				parseInline(((SdtElement) item).getSdtContent().getContent(),
						bold, italic, underline, linkUrl, out, ctx);
			} else if (item instanceof org.docx4j.wml.ContentAccessor) {
				// 兜底递归（RunTrackChange/SmartTag 等容器），需置于具体类型之后
				parseInline(((org.docx4j.wml.ContentAccessor) item).getContent(),
						bold, italic, underline, linkUrl, out, ctx);
			}
		}
	}

	private static boolean boolFlag(org.docx4j.wml.BooleanDefaultTrue flag) {
		return flag != null && flag.isVal();
	}

	/** w:u 值为枚举：非空且非 NONE 视为有下划线。 */
	private static boolean underlineFlag(org.docx4j.wml.U u) {
		return u != null && u.getVal() != null
				&& u.getVal() != org.docx4j.wml.UnderlineEnumeration.NONE;
	}

	private static String resolveLink(String relId, ParseContext ctx) {
		if (relId == null) {
			return null;
		}
		return ctx.relTargets.get(relId);
	}

	// ==================== 图片 ====================

	/** drawing → data URI 图片元素；blip/关系/字节任一环解析失败返回 null（debug 记录）。 */
	private static DocxImage parseImage(Drawing drawing, ParseContext ctx) {
		if (drawing.getAnchorOrInline() == null) {
			return null;
		}
		for (Object rawWrapper : drawing.getAnchorOrInline()) {
			Canvas canvas = canvasOf(unwrap(rawWrapper));
			if (canvas == null) {
				continue;
			}
			CTBlip blip = findBlip(canvas.graphic);
			if (blip == null || blip.getEmbed() == null) {
				LOG.debug("Drawing without resolvable a:blip r:embed skipped");
				continue;
			}
			byte[] bytes = imageBytes(ctx, blip.getEmbed());
			if (bytes == null) {
				continue;
			}
			String mime = imageMime(ctx, blip.getEmbed());
			String src = "data:" + (mime == null ? "application/octet-stream" : mime)
					+ ";base64," + Base64.getEncoder().encodeToString(bytes);
			return new DocxImage(src, altOf(canvas.docPr), mime);
		}
		return null;
	}

	/** inline/anchor 统一视图（docPr + graphic）。 */
	private static Canvas canvasOf(Object wrapper) {
		if (wrapper instanceof Inline) {
			Inline inline = (Inline) wrapper;
			return new Canvas(inline.getDocPr(), inline.getGraphic());
		}
		if (wrapper instanceof Anchor) {
			Anchor anchor = (Anchor) wrapper;
			return new Canvas(anchor.getDocPr(), anchor.getGraphic());
		}
		return null;
	}

	/** graphicData 内定位 CTBlip：直挂 / JAXBElement 包装 / Pic.blipFill 三种形态均兼容。 */
	private static CTBlip findBlip(Graphic graphic) {
		if (graphic == null || graphic.getGraphicData() == null
				|| graphic.getGraphicData().getAny() == null) {
			return null;
		}
		for (Object any : graphic.getGraphicData().getAny()) {
			Object value = unwrap(any);
			if (value instanceof CTBlip) {
				return (CTBlip) value;
			}
			if (value instanceof Pic) {
				Pic pic = (Pic) value;
				return pic.getBlipFill() == null ? null : pic.getBlipFill().getBlip();
			}
			if (value instanceof CTBlipFillProperties) {
				return ((CTBlipFillProperties) value).getBlip();
			}
		}
		return null;
	}

	private static byte[] imageBytes(ParseContext ctx, String relId) {
		try {
			if (ctx.rels == null) {
				LOG.debug("No relationships part; image {} skipped", relId);
				return null;
			}
			org.docx4j.relationships.Relationship rel = ctx.rels.getRelationshipByID(relId);
			if (!(ctx.rels.getPart(rel) instanceof BinaryPart)) {
				LOG.debug("Relationship {} does not point to a binary part", relId);
				return null;
			}
			BinaryPart binary = (BinaryPart) ctx.rels.getPart(rel);
			java.nio.ByteBuffer buffer = binary.getBuffer();
			if (buffer == null) {
				return null;
			}
			byte[] bytes = new byte[buffer.remaining()];
			buffer.get(bytes);
			return bytes;
		} catch (RuntimeException ex) {
			LOG.debug("Cannot load image bytes for {}: {}", relId, ex.getMessage());
			return null;
		}
	}

	private static String imageMime(ParseContext ctx, String relId) {
		try {
			if (ctx.rels == null) {
				return null;
			}
			org.docx4j.relationships.Relationship rel = ctx.rels.getRelationshipByID(relId);
			Part part = ctx.rels.getPart(rel);
			if (part == null || part.getPartName() == null) {
				return null;
			}
			return ctx.contentTypeManager == null ? null
					: ctx.contentTypeManager.getContentType(part.getPartName());
		} catch (RuntimeException ex) {
			return null;
		}
	}

	private static String altOf(org.docx4j.dml.CTNonVisualDrawingProps docPr) {
		if (docPr == null) {
			return null;
		}
		if (docPr.getDescr() != null && !docPr.getDescr().trim().isEmpty()) {
			return docPr.getDescr();
		}
		return docPr.getName();
	}

	// ==================== 表格 ====================

	/** 首行作表头，其余为数据行；无 Tr 返回 null。 */
	static HeadersAndRows parseTable(Tbl tbl) {
		if (tbl.getContent() == null) {
			return null;
		}
		List<Tr> rows = new ArrayList<Tr>();
		for (Object child : tbl.getContent()) {
			if (unwrap(child) instanceof Tr) {
				rows.add((Tr) unwrap(child));
			}
		}
		if (rows.isEmpty()) {
			return null;
		}
		List<String> headers = rowCells(rows.get(0));
		List<List<String>> dataRows = new ArrayList<List<String>>();
		for (int i = 1; i < rows.size(); i++) {
			dataRows.add(rowCells(rows.get(i)));
		}
		return new HeadersAndRows(headers, dataRows);
	}

	/** 解析结果载体：表头 + 数据行。 */
	static final class HeadersAndRows {

		final List<String> headers;
		final List<List<String>> rows;

		HeadersAndRows(List<String> headers, List<List<String>> rows) {
			this.headers = headers;
			this.rows = rows;
		}
	}

	private static List<String> rowCells(Tr tr) {
		List<String> cells = new ArrayList<String>();
		if (tr.getContent() == null) {
			return cells;
		}
		for (Object child : tr.getContent()) {
			if (unwrap(child) instanceof org.docx4j.wml.Tc) {
				StringBuilder sb = new StringBuilder();
				flatText(((org.docx4j.wml.Tc) unwrap(child)).getContent(), sb);
				cells.add(sb.toString().trim());
			}
		}
		return cells;
	}

	/** 单元格扁平化：递归收集全部后代 Text（含嵌套表格/Sdt），Br 与 Tab 折叠为空格。 */
	private static void flatText(List<Object> content, StringBuilder sb) {
		if (content == null) {
			return;
		}
		for (Object rawItem : content) {
			Object item = unwrap(rawItem);
			if (item instanceof Text) {
				sb.append(((Text) item).getValue() == null ? "" : ((Text) item).getValue());
			} else if (item instanceof Br || item instanceof R.Tab) {
				sb.append(' ');
			} else if (item instanceof Tbl || item instanceof P) {
				// 块级容器扁平化后补一个分隔空格，避免相邻块文字粘连
				flatText(((org.docx4j.wml.ContentAccessor) item).getContent(), sb);
				sb.append(' ');
			} else if (item instanceof SdtElement && ((SdtElement) item).getSdtContent() != null) {
				flatText(((SdtElement) item).getSdtContent().getContent(), sb);
			} else if (item instanceof org.docx4j.wml.ContentAccessor) {
				flatText(((org.docx4j.wml.ContentAccessor) item).getContent(), sb);
			}
		}
	}

	// ==================== 元数据 ====================

	private static Map<String, String> relIdToTarget(MainDocumentPart main) {
		Map<String, String> targets = new LinkedHashMap<String, String>();
		if (main == null) {
			return targets;
		}
		try {
			RelationshipsPart rels = main.getRelationshipsPart();
			if (rels == null || rels.getRelationships() == null) {
				return targets;
			}
			for (org.docx4j.relationships.Relationship rel : rels.getRelationships().getRelationship()) {
				if (rel != null && rel.getId() != null) {
					targets.put(rel.getId(), rel.getTarget());
				}
			}
		} catch (RuntimeException ex) {
			LOG.debug("Cannot index relationships: {}", ex.getMessage());
		}
		return targets;
	}

	private static org.docx4j.docProps.core.CoreProperties corePropertiesOf(WordprocessingMLPackage pkg) {
		try {
			DocPropsCorePart core = pkg.getDocPropsCorePart();
			return core == null ? null : core.getJaxbElement();
		} catch (RuntimeException ex) {
			LOG.debug("Core properties unavailable: {}", ex.getMessage());
			return null;
		}
	}

	private static String titleOf(WordprocessingMLPackage pkg) {
		try {
			String title = pkg.getTitle();
			return title == null || title.trim().isEmpty() ? null : title.trim();
		} catch (RuntimeException ex) {
			LOG.debug("Title unavailable: {}", ex.getMessage());
			return null;
		}
	}

	private static Instant modifiedOf(org.docx4j.docProps.core.CoreProperties props) {
		String raw = literalOf(props == null ? null : props.getModified());
		if (raw == null) {
			return null;
		}
		try {
			return Instant.parse(raw.trim());
		} catch (RuntimeException ex) {
			LOG.debug("Modified timestamp '{}' is not ISO-8601; left as null", raw);
			return null;
		}
	}

	/** SimpleLiteral 多段值拼接 trim；空白返回 null。 */
	private static String literalOf(org.docx4j.docProps.core.dc.elements.SimpleLiteral lit) {
		if (lit == null || lit.getContent() == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (String part : lit.getContent()) {
			if (part == null) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append(' ');
			}
			sb.append(part.trim());
		}
		String joined = sb.toString().trim();
		return joined.isEmpty() ? null : joined;
	}

	// ==================== 文本工具 ====================

	private static String plainText(List<InlineSpan> spans) {
		if (spans == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (InlineSpan span : spans) {
			if (span != null && span.getText() != null) {
				sb.append(span.getText());
			}
		}
		return sb.toString();
	}

	private static String firstLink(List<InlineSpan> spans) {
		if (spans == null) {
			return null;
		}
		for (InlineSpan span : spans) {
			if (span != null && span.getHyperlinkUrl() != null && !span.getHyperlinkUrl().isEmpty()) {
				return span.getHyperlinkUrl();
			}
		}
		return null;
	}

	private static boolean hasVisibleText(List<InlineSpan> spans) {
		if (spans == null) {
			return false;
		}
		for (InlineSpan span : spans) {
			if (span != null && span.getText() != null && !span.getText().trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	// ==================== 抽取会话状态 ====================

	/**
	 * 单次抽取的上下文：关系索引、numbering 解析器、待渲染元素以及当前打开的列表 run
	 * （扁平合并规则见类注释）。同时承载延迟输出的列表内图片。
	 */
	private static final class ParseContext {

		final List<DocxElement> elements = new ArrayList<DocxElement>();
		final Map<String, String> relTargets = new LinkedHashMap<String, String>();
		NumberingResolver numbering = NumberingResolver.NONE;
		RelationshipsPart rels;
		org.docx4j.openpackaging.contenttype.ContentTypeManager contentTypeManager;

		/** 当前打开列表 run 的 numId；null 表示无未决列表。 */
		private BigInteger openNumId;

		/** 当前段落所属的 ilvl（append 时定位对应桶）。 */
		private int currentIlvl;

		/** run 内按 ilvl 分桶的构建器，key=ilvl 升序输出。 */
		private final TreeMap<Integer, ListBuilder> buckets = new TreeMap<Integer, ListBuilder>();

		/** 当前 run 中出现的最大图片集合（跨层级的顺序保持）。 */
		private final List<DocxImage> deferredImages = new ArrayList<DocxImage>();

		void attach(MainDocumentPart main) {
			if (main == null) {
				return;
			}
			relTargets.putAll(relIdToTarget(main));
			try {
				this.rels = main.getRelationshipsPart();
			} catch (RuntimeException ex) {
				LOG.debug("Relationships part unavailable: {}", ex.getMessage());
			}
			try {
				contentTypeManager = main.getPackage() == null
						? null : main.getPackage().getContentTypeManager();
			} catch (RuntimeException ex) {
				LOG.debug("Content types unavailable: {}", ex.getMessage());
			}
			numbering = NumberingResolver.of(main.getNumberingDefinitionsPart());
		}

		/**
		 * 关闭当前列表 run 并按 ilvl 升序落地各分桶，随后追加 run 内延迟图片。
		 *
		 * <p>缩进几何（CommonMark）：子块缩进列数必须 ≥ 父项内容起始列。落地第 k 级前，
		 * 累计其所有祖先级（j&lt;k）的“最宽标记宽度”：无序 {@code "- "} 固定 2 列；
		 * 有序取最大编号位宽 + 2（如 10+ 项的 {@code "10. "} 占 4 列）。</p>
		 */
		void flushPendingList() {
			if (openNumId == null && buckets.isEmpty()) {
				appendDeferredImages();
				return;
			}
			openNumId = null;
			int cumulativeColumns = 0;
			for (Map.Entry<Integer, ListBuilder> entry : buckets.entrySet()) {
				ListBuilder builder = entry.getValue();
				if (!builder.items.isEmpty()) {
					elements.add(builder.build(cumulativeColumns));
					cumulativeColumns += builder.widestMarkerWidth();
				}
			}
			buckets.clear();
			appendDeferredImages();
		}

		private void appendDeferredImages() {
			for (DocxImage image : deferredImages) {
				elements.add(image);
			}
			deferredImages.clear();
		}

		void openOrReuseList(BigInteger numId, int ilvl) {
			if (openNumId == null || !openNumId.equals(numId)) {
				flushPendingList();
				openNumId = numId;
			}
			currentIlvl = ilvl;
			Integer key = Integer.valueOf(ilvl);
			if (!buckets.containsKey(key)) {
				buckets.put(key, new ListBuilder(numbering.isOrdered(numId, ilvl), ilvl));
			}
		}

		void appendListItem(List<InlineSpan> spans) {
			buckets.get(Integer.valueOf(currentIlvl)).append(spans);
		}

		void deferImages(List<DocxImage> images) {
			deferredImages.addAll(images);
		}

		/** 单个 ilvl 桶 → 一个 DocxList（items 与 richItems 等长平行）。 */
		private static final class ListBuilder {

			private final boolean ordered;
			private final int indent;
			private final List<String> items = new ArrayList<String>();
			private final List<List<InlineSpan>> richItems = new ArrayList<List<InlineSpan>>();

			ListBuilder(boolean ordered, int indent) {
				this.ordered = ordered;
				this.indent = indent;
			}

			void append(List<InlineSpan> spans) {
				items.add(plainSpansText(spans));
				richItems.add(spans == null ? Collections.<InlineSpan>emptyList()
						: Collections.unmodifiableList(spans));
			}

			DocxList build(int indentColumns) {
				return new DocxList(ordered, indent, items, richItems, Integer.valueOf(indentColumns));
			}

			/**
			 * 本级“最宽标记”所占列数（含标记后的一个空格）：无序 "-" 固定 2；
			 * 有序为最大编号的位宽 + 2（"9. " 占 3 列、"10. " 起占 4 列）。
			 * 供下级层级累计缩进使用；仅对非空桶调用。
			 */
			int widestMarkerWidth() {
				int count = Math.max(items.size(), richItems.size());
				return ordered ? Integer.toString(count).length() + 2 : 2;
			}

			private static String plainSpansText(List<InlineSpan> spans) {
				StringBuilder sb = new StringBuilder();
				if (spans != null) {
					for (InlineSpan span : spans) {
						if (span != null && span.getText() != null) {
							sb.append(span.getText());
						}
					}
				}
				return sb.toString();
			}
		}
	}

	/** 单个段落收集产物：行内片段 + 提升为块级的图片。 */
	private static final class ParaContent {

		final List<InlineSpan> spans = new ArrayList<InlineSpan>();
		final List<DocxImage> images = new ArrayList<DocxImage>();
	}

	/** inline/anchor 抽象出的绘图画布（docPr 元数据 + graphic 内容）。 */
	private static final class Canvas {

		private final org.docx4j.dml.CTNonVisualDrawingProps docPr;
		private final Graphic graphic;

		Canvas(org.docx4j.dml.CTNonVisualDrawingProps docPr, Graphic graphic) {
			this.docPr = docPr;
			this.graphic = graphic;
		}
	}

	// ==================== 编号格式解析 ====================

	/**
	 * numbering 定义索引：numId → abstractNumId → lvl(numFmt)。
	 * part 缺失或定义断链时返回 {@link #NONE}，查询一律判为无序（降级策略见类注释）。
	 */
	private static final class NumberingResolver {

		/** 永远无定义的兜底实例（无 numbering part 或解析失败）。 */
		static final NumberingResolver NONE = new NumberingResolver(null, null, true);

		private final Map<BigInteger, BigInteger> numToAbstract;
		private final Map<BigInteger, List<Lvl>> abstractLevels;
		private final boolean degradedByAbsence;

		NumberingResolver(Map<BigInteger, BigInteger> numToAbstract,
				Map<BigInteger, List<Lvl>> abstractLevels, boolean degradedByAbsence) {
			this.numToAbstract = numToAbstract;
			this.abstractLevels = abstractLevels;
			this.degradedByAbsence = degradedByAbsence;
		}

		static NumberingResolver of(NumberingDefinitionsPart ndp) {
			if (ndp == null) {
				return NONE;
			}
			try {
				Object contents = ndp.getContents();
				if (!(contents instanceof org.docx4j.wml.Numbering)) {
					return NONE;
				}
				org.docx4j.wml.Numbering numbering = (org.docx4j.wml.Numbering) contents;
				Map<BigInteger, BigInteger> numMap = new LinkedHashMap<BigInteger, BigInteger>();
				if (numbering.getNum() != null) {
					for (org.docx4j.wml.Numbering.Num num : numbering.getNum()) {
						if (num != null && num.getNumId() != null && num.getAbstractNumId() != null
								&& num.getAbstractNumId().getVal() != null) {
							numMap.put(num.getNumId(), num.getAbstractNumId().getVal());
						}
					}
				}
				Map<BigInteger, List<Lvl>> abstractMap = new LinkedHashMap<BigInteger, List<Lvl>>();
				if (numbering.getAbstractNum() != null) {
					for (org.docx4j.wml.Numbering.AbstractNum abs : numbering.getAbstractNum()) {
						if (abs != null && abs.getAbstractNumId() != null) {
							abstractMap.put(abs.getAbstractNumId(),
									abs.getLvl() == null
											? Collections.<Lvl>emptyList() : abs.getLvl());
						}
					}
				}
				return new NumberingResolver(numMap, abstractMap, false);
			} catch (RuntimeException | Docx4JException ex) {
				LOG.debug("Numbering definitions unreadable; lists degrade to bullets: {}",
						ex.getMessage());
				return NONE;
			}
		}

		/**
		 * 该 numId/ilvl 是否有序：凡可解析的非 BULLET numFmt（DECIMAL、LOWER_ROMAN、
		 * lowerLetter 等）一律有序；断链（映射缺失、lvl 缺失、numFmt 缺失）降级返回 false。
		 */
		boolean isOrdered(BigInteger numId, int ilvl) {
			if (numToAbstract == null || abstractLevels == null || numToAbstract.isEmpty()) {
				logDegradedOnce(numId);
				return false;
			}
			BigInteger abstractId = numToAbstract.get(numId);
			List<Lvl> levels = abstractId == null ? null : abstractLevels.get(abstractId);
			Lvl match = null;
			if (levels != null) {
				for (Lvl lvl : levels) {
					if (lvl.getIlvl() != null && lvl.getIlvl().intValue() == ilvl) {
						match = lvl;
						break;
					}
				}
			}
			if (match == null || match.getNumFmt() == null || match.getNumFmt().getVal() == null) {
				LOG.debug("numFmt unresolvable for numId={} ilvl={}; degrade to bullet", numId, ilvl);
				return false;
			}
			return match.getNumFmt().getVal() != NumberFormat.BULLET;
		}

		private void logDegradedOnce(BigInteger numId) {
			if (degradedByAbsence) {
				LOG.debug("No usable numbering definitions; numId={} degrades to bullet", numId);
			}
		}
	}
}
