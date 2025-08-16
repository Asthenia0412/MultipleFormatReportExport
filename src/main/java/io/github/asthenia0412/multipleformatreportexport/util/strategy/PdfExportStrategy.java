package io.github.asthenia0412.multipleformatreportexport.util.strategy;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import io.github.asthenia0412.multipleformatreportexport.util.ExportStrategy;
import io.github.asthenia0412.multipleformatreportexport.util.ReportExportUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * PDF导出策略实现
 */
public class PdfExportStrategy implements ExportStrategy {
    
    @Override
    public CompletableFuture<byte[]> export(List<CodeAnalysis> dataList) {
        return ReportExportUtil.exportPdf(dataList);
    }
    
    @Override
    public String getFormatName() {
        return "PDF";
    }
    
    @Override
    public String getMimeType() {
        return "application/pdf";
    }
    
    @Override
    public String getFileExtension() {
        return "pdf";
    }
}