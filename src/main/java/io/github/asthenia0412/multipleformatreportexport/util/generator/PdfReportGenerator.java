package io.github.asthenia0412.multipleformatreportexport.util.generator;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支持中文的PDF报告生成器
 */
@Slf4j
public class PdfReportGenerator {

    // 报告标题和表头
    private static final String REPORT_TITLE = "代码质量检测报告";
    private static final String REPORT_SUBTITLE = "Code Quality Analysis Report";
    private static final String[] HEADERS = {
            "ID", "文件名", "文件路径", "代码行数",
            "问题数量", "问题类型", "创建时间", "更新时间"
    };

    // 日期格式
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 字体定义
    private static Font TITLE_FONT;
    private static Font SUBTITLE_FONT;
    private static Font DATE_FONT;
    private static Font SECTION_TITLE_FONT;
    private static Font TABLE_HEADER_FONT;
    private static Font TABLE_CONTENT_FONT;
    private static Font RECOMMENDATION_FONT;

    // 颜色定义
    private static final BaseColor HEADER_BG_COLOR = new BaseColor(51, 102, 153);
    private static final BaseColor RECOMMENDATION_BG_COLOR = new BaseColor(249, 249, 249);
    private static final BaseColor TABLE_ROW_BG_COLOR = new BaseColor(255, 255, 255);
    private static final BaseColor TABLE_ALT_ROW_BG_COLOR = new BaseColor(245, 245, 245);

    // 静态初始化字体
    static {
        initChineseFonts();
    }

    /**
     * 初始化中文字体
     */
    private static void initChineseFonts() {
        try {
            // 使用iText自带的中文字体（确保中文显示）
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);

            // 初始化各种字体样式
            TITLE_FONT = new Font(baseFont, 24, Font.BOLD, BaseColor.DARK_GRAY);
            SUBTITLE_FONT = new Font(baseFont, 18, Font.NORMAL, BaseColor.GRAY);
            DATE_FONT = new Font(baseFont, 12, Font.NORMAL, BaseColor.LIGHT_GRAY);
            SECTION_TITLE_FONT = new Font(baseFont, 16, Font.BOLD, BaseColor.BLACK);
            TABLE_HEADER_FONT = new Font(baseFont, 10, Font.BOLD, BaseColor.WHITE);
            TABLE_CONTENT_FONT = new Font(baseFont, 9, Font.NORMAL, BaseColor.BLACK);
            RECOMMENDATION_FONT = new Font(baseFont, 10, Font.NORMAL, BaseColor.BLACK);

        } catch (Exception e) {
            log.error("初始化中文字体失败，将使用默认字体", e);
            initFallbackFonts();
        }
    }

    /**
     * 初始化回退字体（当中文不可用时）
     */
    private static void initFallbackFonts() {
        TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.DARK_GRAY);
        SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.NORMAL, BaseColor.GRAY);
        DATE_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.LIGHT_GRAY);
        SECTION_TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK);
        TABLE_HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        TABLE_CONTENT_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
        RECOMMENDATION_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * 生成PDF报告
     * @param dataList 数据列表
     * @return PDF字节数组
     */
    public static byte[] generateReport(List<CodeAnalysis> dataList) throws Exception {
        // 创建文档对象（A4大小，边距36pt）
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, ops);

        // 打开文档
        document.open();

        // 设置文档属性
        setDocumentProperties(document);

        // 添加报告标题部分
        addTitleSection(document);

        // 添加执行摘要部分
        addSummarySection(document, dataList);

        // 添加详细检测结果部分
        addDetailedResultsSection(document, dataList);

        // 添加问题统计分析部分
        addIssueStatisticsSection(document, dataList);

        // 添加建议和改进措施部分
        addRecommendationsSection(document);

        // 关闭文档
        document.close();

        log.info("PDF生成成功");
        return ops.toByteArray();
    }

    /**
     * 设置文档属性
     */
    private static void setDocumentProperties(Document document) {
        document.addTitle(REPORT_TITLE);
        document.addSubject(REPORT_SUBTITLE);
        document.addCreator("代码质量分析工具");
        document.addAuthor("Asthenia0412");
        document.addKeywords("代码质量,静态分析,PDF报告");
    }

    /**
     * 添加报告标题部分
     */
    private static void addTitleSection(Document document) throws DocumentException {
        // 主标题
        Paragraph title = new Paragraph(REPORT_TITLE, TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10f);
        document.add(title);

        // 副标题
        Paragraph subtitle = new Paragraph(REPORT_SUBTITLE, SUBTITLE_FONT);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(10f);
        document.add(subtitle);

        // 生成时间
        Paragraph date = new Paragraph(
                "生成时间: " + java.time.LocalDateTime.now().format(DATE_TIME_FORMATTER),
                DATE_FONT
        );
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(20f);
        document.add(date);
    }

    /**
     * 添加执行摘要部分
     */
    private static void addSummarySection(Document document, List<CodeAnalysis> dataList) throws DocumentException {
        // 部分标题
        Paragraph sectionTitle = new Paragraph("1. 执行摘要", SECTION_TITLE_FONT);
        sectionTitle.setSpacingAfter(10f);
        document.add(sectionTitle);

        // 计算统计数据
        int totalFiles = dataList.size();
        int totalIssues = dataList.stream().mapToInt(data -> data.getIssueCount() != null ? data.getIssueCount() : 0).sum();
        int totalCodeLines = dataList.stream().mapToInt(data -> data.getCodeLine() != null ? data.getCodeLine() : 0).sum();
        double issueDensity = totalCodeLines > 0 ? (double) totalIssues / totalCodeLines * 1000 : 0;

        // 创建表格
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(50);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(20f);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);

        // 添加表头
        addSummaryTableHeader(table);

        // 添加数据行
        addSummaryTableRow(table, "检测文件总数", String.valueOf(totalFiles));
        addSummaryTableRow(table, "发现问题总数", String.valueOf(totalIssues));
        addSummaryTableRow(table, "代码总行数", String.valueOf(totalCodeLines));
        addSummaryTableRow(table, "平均问题密度", String.format("%.2f 问题/千行", issueDensity));

        document.add(table);
    }

    /**
     * 添加摘要表格表头
     */
    private static void addSummaryTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell(new Phrase("指标", TABLE_HEADER_FONT));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setPadding(5);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("数值", TABLE_HEADER_FONT));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setPadding(5);
        table.addCell(cell);
    }

    /**
     * 添加摘要表格行
     */
    private static void addSummaryTableRow(PdfPTable table, String label, String value) {
        table.addCell(createContentCell(label));
        table.addCell(createContentCell(value));
    }

    /**
     * 添加详细检测结果部分
     */
    private static void addDetailedResultsSection(Document document, List<CodeAnalysis> dataList) throws DocumentException {
        // 部分标题
        Paragraph sectionTitle = new Paragraph("2. 详细检测结果", SECTION_TITLE_FONT);
        sectionTitle.setSpacingAfter(10f);
        document.add(sectionTitle);

        // 创建表格
        PdfPTable table = new PdfPTable(HEADERS.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(20f);
        table.setWidths(new float[]{1, 2, 3, 1, 1, 2, 2, 2}); // 设置列宽比例

        // 添加表头
        for (String header : HEADERS) {
            PdfPCell cell = new PdfPCell(new Phrase(header, TABLE_HEADER_FONT));
            cell.setBackgroundColor(HEADER_BG_COLOR);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // 添加数据行（交替行颜色）
        boolean alternate = false;
        for (CodeAnalysis data : dataList) {
            if (data != null) {
                BaseColor bgColor = alternate ? TABLE_ALT_ROW_BG_COLOR : TABLE_ROW_BG_COLOR;

                table.addCell(createContentCell(data.getId() != null ? data.getId().toString() : "N/A", bgColor));
                table.addCell(createContentCell(data.getFileName() != null ? data.getFileName() : "N/A", bgColor));
                table.addCell(createContentCell(data.getFilePath() != null ? data.getFilePath() : "N/A", bgColor));
                table.addCell(createContentCell(data.getCodeLine() != null ? data.getCodeLine().toString() : "0", bgColor));
                table.addCell(createContentCell(data.getIssueCount() != null ? data.getIssueCount().toString() : "0", bgColor));
                table.addCell(createContentCell(data.getIssueType() != null ? data.getIssueType() : "N/A", bgColor));
                table.addCell(createContentCell(data.getCreatedAt() != null ? DATE_TIME_FORMATTER.format(data.getCreatedAt()) : "N/A", bgColor));
                table.addCell(createContentCell(data.getUpdatedAt() != null ? DATE_TIME_FORMATTER.format(data.getUpdatedAt()) : "N/A", bgColor));

                alternate = !alternate;
            }
        }

        document.add(table);
    }

    /**
     * 添加问题统计分析部分
     */
    private static void addIssueStatisticsSection(Document document, List<CodeAnalysis> dataList) throws DocumentException {
        // 部分标题
        Paragraph sectionTitle = new Paragraph("3. 问题统计分析", SECTION_TITLE_FONT);
        sectionTitle.setSpacingAfter(10f);
        document.add(sectionTitle);

        // 统计问题类型分布
        Map<String, Long> issueTypeStats = dataList.stream()
                .filter(data -> data.getIssueType() != null && !data.getIssueType().isEmpty())
                .collect(Collectors.groupingBy(
                        CodeAnalysis::getIssueType,
                        Collectors.counting()
                ));

        // 创建表格
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(50);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(20f);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);

        // 添加表头
        PdfPCell cell = new PdfPCell(new Phrase("问题类型", TABLE_HEADER_FONT));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setPadding(5);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("文件数量", TABLE_HEADER_FONT));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setPadding(5);
        table.addCell(cell);

        // 添加数据行（按数量降序排序）
        issueTypeStats.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    table.addCell(createContentCell(entry.getKey()));
                    table.addCell(createContentCell(entry.getValue().toString()));
                });

        document.add(table);
    }

    /**
     * 添加建议和改进措施部分
     */
    private static void addRecommendationsSection(Document document) throws DocumentException {
        // 部分标题
        Paragraph sectionTitle = new Paragraph("4. 建议和改进措施", SECTION_TITLE_FONT);
        sectionTitle.setSpacingAfter(10f);
        document.add(sectionTitle);

        // 创建表格容器
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(20f);

        // 创建内容单元格
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(RECOMMENDATION_BG_COLOR);
        cell.setPadding(10);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderWidth(1);
        cell.setBorderColor(BaseColor.GRAY);

        // 创建无序列表
        com.itextpdf.text.List pdfList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
        pdfList.setListSymbol("\u2022"); // 使用圆点作为项目符号
        pdfList.setIndentationLeft(10);

        // 添加列表项
        pdfList.add(createListItem("定期进行代码质量检测，建立代码质量门禁机制"));
        pdfList.add(createListItem("对发现的问题进行分类处理，优先解决高严重性问题"));
        pdfList.add(createListItem("建立代码审查流程，提高代码质量意识"));
        pdfList.add(createListItem("使用自动化工具进行持续集成和持续部署"));
        pdfList.add(createListItem("定期进行代码重构，减少技术债务"));

        cell.addElement(pdfList);
        table.addCell(cell);

        document.add(table);
    }

    /**
     * 创建内容单元格
     */
    private static PdfPCell createContentCell(String content) {
        return createContentCell(content, TABLE_ROW_BG_COLOR);
    }

    /**
     * 创建带背景色的内容单元格
     */
    private static PdfPCell createContentCell(String content, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(content, TABLE_CONTENT_FONT));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(5);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    /**
     * 创建列表项
     */
    private static ListItem createListItem(String text) {
        ListItem item = new ListItem(text, RECOMMENDATION_FONT);
        item.setIndentationLeft(5);
        return item;
    }
}