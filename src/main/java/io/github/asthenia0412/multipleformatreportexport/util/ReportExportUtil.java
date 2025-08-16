package io.github.asthenia0412.multipleformatreportexport.util;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReportExportUtil {
    private static final String[] HEADERS = {
            "ID", "File Name", "File Path", "Code Line",
            "Issue Count", "Issue Type", "Created At", "Updated At"
    };

    public static CompletableFuture<byte[]> exportxls(List<CodeAnalysis> dataList) {
        return CompletableFuture.supplyAsync(() -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(100);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                Sheet sheet = workbook.createSheet("Code Analysis");
                createxlsHeader(sheet);

                for (int i = 0; i < dataList.size(); i++) {
                    CodeAnalysis data = dataList.get(i);
                    if (data != null) {
                        populatexlsRow(sheet.createRow(i + 1), data);
                    }
                }

                workbook.write(out);
                return out.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("xls export failed", e);
            }
        });
    }

    public static CompletableFuture<byte[]> exportWord(List<CodeAnalysis> dataList) {
        return CompletableFuture.supplyAsync(() -> {
            try (XWPFDocument document = new XWPFDocument();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                XWPFTable table = document.createTable();
                createWordHeader(table);

                dataList.forEach(data -> {
                    if (data != null) {
                        populateWordRow(table.createRow(), data);
                    }
                });

                document.write(out);
                return out.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("Word export failed", e);
            }
        });
    }

    private static void createxlsHeader(Sheet sheet) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            header.createCell(i).setCellValue(HEADERS[i]);
        }
    }

    private static void populatexlsRow(Row row, CodeAnalysis data) {
        if (data == null) {
            return; // 或者创建空行
        }

        row.createCell(0).setCellValue(data.getId() != null ? data.getId() : 0L);
        row.createCell(1).setCellValue(data.getFileName() != null ? data.getFileName() : "文件名拉取失败");
        row.createCell(2).setCellValue(data.getFilePath() != null ? data.getFilePath() : "文件路径拉取失败");
        row.createCell(3).setCellValue(data.getCodeLine() != null ? data.getCodeLine() : 0);
        row.createCell(4).setCellValue(data.getIssueCount() != null ? data.getIssueCount() : 0);
        row.createCell(5).setCellValue(data.getIssueType() != null ? data.getIssueType() : "问题类型拉取失败");
        row.createCell(6).setCellValue(data.getCreatedAt() != null ? data.getCreatedAt().toString() : "问题发生时间拉取失败");
        row.createCell(7).setCellValue(data.getUpdatedAt() != null ? data.getUpdatedAt().toString() : "问题更新时间拉取失败");
    }

    private static void createWordHeader(XWPFTable table) {
        XWPFTableRow headerRow = table.getRow(0);
        for (String header : HEADERS) {
            if (headerRow.getCtRow().sizeOfTcArray() <= HEADERS.length) {
                headerRow.addNewTableCell().setText(header);
            } else {
                headerRow.getCell(headerRow.getCtRow().sizeOfTcArray() - 1).setText(header);
            }
        }
    }

    private static void populateWordRow(XWPFTableRow row, CodeAnalysis data) {
        addCell(row, String.valueOf(data.getId()));
        addCell(row, data.getFileName());
        addCell(row, data.getFilePath());
        addCell(row, String.valueOf(data.getCodeLine()));
        addCell(row, String.valueOf(data.getIssueCount()));
        addCell(row, data.getIssueType());
        addCell(row, data.getCreatedAt() != null ? data.getCreatedAt().toString() : "");
        addCell(row, data.getUpdatedAt() != null ? data.getUpdatedAt().toString() : "");
    }

    private static void addCell(XWPFTableRow row, String text) {
        row.addNewTableCell().setText(text != null ? text : "");
    }
}