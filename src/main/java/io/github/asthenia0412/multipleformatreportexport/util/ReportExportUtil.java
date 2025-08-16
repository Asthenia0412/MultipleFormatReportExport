package io.github.asthenia0412.multipleformatreportexport.util;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import io.github.asthenia0412.multipleformatreportexport.util.generator.HtmlReportGenerator;
import io.github.asthenia0412.multipleformatreportexport.util.generator.PdfReportGenerator;
import io.github.asthenia0412.multipleformatreportexport.util.generator.XmlReportGenerator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 报告导出工具类 - 使用策略模式支持多种格式
 */
public class ReportExportUtil {

    // 报告配置常量
    private static final String REPORT_TITLE = "代码质量检测报告";
    private static final String REPORT_SUBTITLE = "Code Quality Analysis Report";
    private static final String[] HEADERS = {
            "ID", "文件名", "文件路径", "代码行数",
            "问题数量", "问题类型", "创建时间", "更新时间"
    };

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 字体和样式配置
    private static final String DEFAULT_FONT_FAMILY = "微软雅黑";
    private static final int DEFAULT_FONT_SIZE = 10;
    private static final int HEADER_FONT_SIZE = 11;
    private static final int TITLE_FONT_SIZE = 16;
    private static final String HEADER_BACKGROUND_COLOR = "D3D3D3";

    /**
     * 导出报告 - 根据格式选择对应的导出策略
     */
    public static CompletableFuture<byte[]> exportReport(List<CodeAnalysis> dataList, String format) {
        ExportStrategy strategy = ExportStrategyFactory.createStrategy(format);
        return strategy.export(dataList);
    }

    /**
     * 导出XLS格式报告
     */
    public static CompletableFuture<byte[]> exportXls(List<CodeAnalysis> dataList) {
        return CompletableFuture.supplyAsync(() -> {
            SXSSFWorkbook workbook = null;
            ByteArrayOutputStream out = null;
            try {
                workbook = new SXSSFWorkbook(100);
                out = new ByteArrayOutputStream();

                Sheet sheet = workbook.createSheet("代码质量检测");

                // 创建报告标题
                createXlsTitle(sheet);
                createXlsHeader(sheet, 2); // 从第3行开始创建表头

                // 填充数据行
                for (int i = 0; i < dataList.size(); i++) {
                    CodeAnalysis data = dataList.get(i);
                    if (data != null) {
                        populateXlsRow(sheet.createRow(i + 3), data); // 从第4行开始填充数据
                    }
                }

                // 手动设置列宽
                setXlsColumnWidths(sheet);

                workbook.write(out);
                return out.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("Excel导出失败", e);
            } finally {
                closeResources(workbook, out);
            }
        });
    }

    /**
     * 导出Word格式报告
     */
    public static CompletableFuture<byte[]> exportWord(List<CodeAnalysis> dataList) {
        return CompletableFuture.supplyAsync(() -> {
            try (XWPFDocument document = new XWPFDocument();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                // 创建报告标题页
                createWordTitlePage(document);

                // 创建目录页
                createWordTableOfContents(document);

                // 创建执行摘要
                createWordExecutiveSummary(document, dataList);

                // 创建详细报告表格
                createWordDetailedReport(document, dataList);

                // 创建问题统计图表
                createWordIssueStatistics(document, dataList);

                // 创建建议和改进措施
                createWordRecommendations(document);

                document.write(out);
                return out.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("Word导出失败", e);
            }
        });
    }

    /**
     * 导出PDF格式报告
     */
    public static CompletableFuture<byte[]> exportPdf(List<CodeAnalysis> dataList) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return PdfReportGenerator.generateReport(dataList);
            } catch (Exception e) {
                throw new RuntimeException("", e);
            }
        });
    }

    /**
     * 导出HTML格式报告
     */
    public static CompletableFuture<byte[]> exportHtml(List<CodeAnalysis> dataList) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return HtmlReportGenerator.generateReport(dataList);
            } catch (Exception e) {
                throw new RuntimeException("HTML导出失败", e);
            }
        });
    }

    /**
     * 导出XML格式报告
     */
    public static CompletableFuture<byte[]> exportXml(List<CodeAnalysis> dataList) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return XmlReportGenerator.generateReport(dataList);
            } catch (Exception e) {
                throw new RuntimeException("XML导出失败", e);
            }
        });
    }

    // ==================== XLS相关方法 ====================

    private static void createXlsTitle(Sheet sheet) {
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue(REPORT_TITLE);
        titleRow.createCell(1).setCellValue(REPORT_SUBTITLE);

        Row dateRow = sheet.createRow(1);
        dateRow.createCell(0).setCellValue("生成时间：" + java.time.LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    private static void createXlsHeader(Sheet sheet, int startRow) {
        Row header = sheet.createRow(startRow);
        for (int i = 0; i < HEADERS.length; i++) {
            header.createCell(i).setCellValue(HEADERS[i]);
        }
    }

    private static void populateXlsRow(Row row, CodeAnalysis data) {
        if (data == null) return;

        row.createCell(0).setCellValue(data.getId() != null ? data.getId() : 0L);
        row.createCell(1).setCellValue(data.getFileName() != null ? data.getFileName() : "N/A");
        row.createCell(2).setCellValue(data.getFilePath() != null ? data.getFilePath() : "N/A");
        row.createCell(3).setCellValue(data.getCodeLine() != null ? data.getCodeLine() : 0);
        row.createCell(4).setCellValue(data.getIssueCount() != null ? data.getIssueCount() : 0);
        row.createCell(5).setCellValue(data.getIssueType() != null ? data.getIssueType() : "N/A");
        row.createCell(6).setCellValue(data.getCreatedAt() != null ?
                DATE_TIME_FORMATTER.format(data.getCreatedAt()) : "N/A");
        row.createCell(7).setCellValue(data.getUpdatedAt() != null ?
                DATE_TIME_FORMATTER.format(data.getUpdatedAt()) : "N/A");
    }

    private static void setXlsColumnWidths(Sheet sheet) {
        int[] widths = {15, 30, 50, 15, 15, 20, 20, 20}; // 各列宽度
        for (int i = 0; i < widths.length; i++) {
            sheet.setColumnWidth(i, widths[i] * 256);
        }
    }

    // ==================== Word相关方法 ====================

    private static void createWordTitlePage(XWPFDocument document) {
        // 创建标题段落
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(REPORT_TITLE);
        titleRun.setBold(true);
        titleRun.setFontSize(TITLE_FONT_SIZE);
        titleRun.setFontFamily(DEFAULT_FONT_FAMILY);

        // 创建副标题
        XWPFParagraph subtitlePara = document.createParagraph();
        subtitlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun subtitleRun = subtitlePara.createRun();
        subtitleRun.setText(REPORT_SUBTITLE);
        subtitleRun.setFontSize(14);
        subtitleRun.setFontFamily(DEFAULT_FONT_FAMILY);

        // 添加生成时间
        XWPFParagraph datePara = document.createParagraph();
        datePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun dateRun = datePara.createRun();
        dateRun.setText("生成时间：" + java.time.LocalDateTime.now().format(DATE_TIME_FORMATTER));
        dateRun.setFontSize(12);
        dateRun.setFontFamily(DEFAULT_FONT_FAMILY);

        // 添加分页符
        document.createParagraph().createRun().addBreak(BreakType.PAGE);
    }

    private static void createWordTableOfContents(XWPFDocument document) {
        XWPFParagraph tocTitle = document.createParagraph();
        tocTitle.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun tocTitleRun = tocTitle.createRun();
        tocTitleRun.setText("目录");
        tocTitleRun.setBold(true);
        tocTitleRun.setFontSize(16);
        tocTitleRun.setFontFamily(DEFAULT_FONT_FAMILY);

        // 添加目录项
        String[] tocItems = {
                "1. 执行摘要",
                "2. 详细检测结果",
                "3. 问题统计分析",
                "4. 建议和改进措施"
        };

        for (String item : tocItems) {
            XWPFParagraph tocItem = document.createParagraph();
            XWPFRun tocItemRun = tocItem.createRun();
            tocItemRun.setText(item);
            tocItemRun.setFontSize(12);
            tocItemRun.setFontFamily(DEFAULT_FONT_FAMILY);
        }

        document.createParagraph().createRun().addBreak(BreakType.PAGE);
    }

    private static void createWordExecutiveSummary(XWPFDocument document, List<CodeAnalysis> dataList) {
        XWPFParagraph summaryTitle = document.createParagraph();
        summaryTitle.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun summaryTitleRun = summaryTitle.createRun();
        summaryTitleRun.setText("1. 执行摘要");
        summaryTitleRun.setBold(true);
        summaryTitleRun.setFontSize(14);
        summaryTitleRun.setFontFamily(DEFAULT_FONT_FAMILY);

        // 计算统计信息
        int totalFiles = dataList.size();
        int totalIssues = dataList.stream().mapToInt(data -> data.getIssueCount() != null ? data.getIssueCount() : 0).sum();
        int totalCodeLines = dataList.stream().mapToInt(data -> data.getCodeLine() != null ? data.getCodeLine() : 0).sum();

        // 创建摘要表格
        XWPFTable summaryTable = document.createTable(4, 2);
        summaryTable.setWidth("100%");

        String[][] summaryData = {
                {"检测文件总数", String.valueOf(totalFiles)},
                {"发现问题总数", String.valueOf(totalIssues)},
                {"代码总行数", String.valueOf(totalCodeLines)},
                {"平均问题密度", String.format("%.2f", totalCodeLines > 0 ? (double) totalIssues / totalCodeLines * 1000 : 0) + " 问题/千行"}
        };

        for (int i = 0; i < summaryData.length; i++) {
            XWPFTableRow row = summaryTable.getRow(i);
            row.getCell(0).setText(summaryData[i][0]);
            row.getCell(1).setText(summaryData[i][1]);
        }

        document.createParagraph().createRun().addBreak(BreakType.PAGE);
    }

    private static void createWordDetailedReport(XWPFDocument document, List<CodeAnalysis> dataList) {
        XWPFParagraph detailTitle = document.createParagraph();
        detailTitle.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun detailTitleRun = detailTitle.createRun();
        detailTitleRun.setText("2. 详细检测结果");
        detailTitleRun.setBold(true);
        detailTitleRun.setFontSize(14);
        detailTitleRun.setFontFamily(DEFAULT_FONT_FAMILY);

        // 创建详细报告表格
        XWPFTable detailTable = document.createTable(dataList.size() + 1, HEADERS.length);
        detailTable.setWidth("100%");

        // 设置表头
        createWordHeader(detailTable);

        // 填充数据行
        for (int i = 0; i < dataList.size(); i++) {
            CodeAnalysis data = dataList.get(i);
            if (data != null) {
                populateWordRow(detailTable.getRow(i + 1), data);
            }
        }

        document.createParagraph().createRun().addBreak(BreakType.PAGE);
    }

    private static void createWordIssueStatistics(XWPFDocument document, List<CodeAnalysis> dataList) {
        XWPFParagraph statsTitle = document.createParagraph();
        statsTitle.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun statsTitleRun = statsTitle.createRun();
        statsTitleRun.setText("3. 问题统计分析");
        statsTitleRun.setBold(true);
        statsTitleRun.setFontSize(14);
        statsTitleRun.setFontFamily(DEFAULT_FONT_FAMILY);

        // 按问题类型统计
        java.util.Map<String, Long> issueTypeStats = dataList.stream()
                .filter(data -> data.getIssueType() != null && !data.getIssueType().isEmpty())
                .collect(java.util.stream.Collectors.groupingBy(
                        CodeAnalysis::getIssueType,
                        java.util.stream.Collectors.counting()
                ));

        // 创建统计表格
        XWPFTable statsTable = document.createTable(issueTypeStats.size() + 1, 2);
        statsTable.setWidth("50%");

        // 表头
        statsTable.getRow(0).getCell(0).setText("问题类型");
        statsTable.getRow(0).getCell(1).setText("文件数量");

        // 统计数据
        int rowIndex = 1;
        for (java.util.Map.Entry<String, Long> entry : issueTypeStats.entrySet()) {
            XWPFTableRow row = statsTable.getRow(rowIndex++);
            row.getCell(0).setText(entry.getKey());
            row.getCell(1).setText(String.valueOf(entry.getValue()));
        }
    }

    private static void createWordRecommendations(XWPFDocument document) {
        XWPFParagraph recTitle = document.createParagraph();
        recTitle.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun recTitleRun = recTitle.createRun();
        recTitleRun.setText("4. 建议和改进措施");
        recTitleRun.setBold(true);
        recTitleRun.setFontSize(14);
        recTitleRun.setFontFamily(DEFAULT_FONT_FAMILY);

        String[] recommendations = {
                "• 定期进行代码质量检测，建立代码质量门禁机制",
                "• 对发现的问题进行分类处理，优先解决高严重性问题",
                "• 建立代码审查流程，提高代码质量意识",
                "• 使用自动化工具进行持续集成和持续部署",
                "• 定期进行代码重构，减少技术债务"
        };

        for (String rec : recommendations) {
            XWPFParagraph recPara = document.createParagraph();
            XWPFRun recRun = recPara.createRun();
            recRun.setText(rec);
            recRun.setFontSize(12);
            recRun.setFontFamily(DEFAULT_FONT_FAMILY);
        }
    }

    private static void createWordHeader(XWPFTable table) {
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            if (cell == null) {
                cell = headerRow.addNewTableCell();
            }
            cell.removeParagraph(0);
            XWPFParagraph paragraph = cell.addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = paragraph.createRun();
            run.setText(HEADERS[i]);
            run.setBold(true);
            run.setFontSize(HEADER_FONT_SIZE);
            run.setFontFamily(DEFAULT_FONT_FAMILY);
            cell.setColor(HEADER_BACKGROUND_COLOR);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        }
    }

    private static void populateWordRow(XWPFTableRow row, CodeAnalysis data) {
        if (data == null) return;

        addCell(row, 0, String.valueOf(data.getId() != null ? data.getId() : "N/A"));
        addCell(row, 1, data.getFileName() != null ? data.getFileName() : "N/A");
        addCell(row, 2, data.getFilePath() != null ? data.getFilePath() : "N/A");
        addCell(row, 3, String.valueOf(data.getCodeLine() != null ? data.getCodeLine() : 0));
        addCell(row, 4, String.valueOf(data.getIssueCount() != null ? data.getIssueCount() : 0));
        addCell(row, 5, data.getIssueType() != null ? data.getIssueType() : "N/A");
        addCell(row, 6, data.getCreatedAt() != null ?
                DATE_TIME_FORMATTER.format(data.getCreatedAt()) : "N/A");
        addCell(row, 7, data.getUpdatedAt() != null ?
                DATE_TIME_FORMATTER.format(data.getUpdatedAt()) : "N/A");
    }

    private static void addCell(XWPFTableRow row, int colIndex, String text) {
        XWPFTableCell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.addNewTableCell();
        }
        cell.removeParagraph(0);
        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = paragraph.createRun();
        run.setText(text != null ? text : "");
        run.setFontSize(DEFAULT_FONT_SIZE);
        run.setFontFamily(DEFAULT_FONT_FAMILY);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
    }

    private static void closeResources(SXSSFWorkbook workbook, ByteArrayOutputStream out) {
        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                System.err.println("关闭工作簿时出错: " + e.getMessage());
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                System.err.println("关闭输出流时出错: " + e.getMessage());
            }
        }
    }
}