package io.github.asthenia0412.multipleformatreportexport.util.generator;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class XLSReportGenerator {
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

    public static byte[] generateXLSReport(List<CodeAnalysis> dataList) {
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


