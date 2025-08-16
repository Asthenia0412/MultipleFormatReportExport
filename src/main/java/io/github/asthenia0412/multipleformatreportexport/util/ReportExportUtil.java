package io.github.asthenia0412.multipleformatreportexport.util;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import io.github.asthenia0412.multipleformatreportexport.util.generator.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 报告导出工具类 - 使用策略模式支持多种格式
 */
public class ReportExportUtil {


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
                    return XLSReportGenerator.generateXLSReport(dataList);
                }
        );
    }

    /**
     * 导出Word格式报告
     */
    public static CompletableFuture<byte[]> exportWord(List<CodeAnalysis> dataList) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return DocReportGenerator.generatorDocReport(dataList);
            } catch (Exception e) {
                throw new RuntimeException("", e);
            }

        });
    }

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


}