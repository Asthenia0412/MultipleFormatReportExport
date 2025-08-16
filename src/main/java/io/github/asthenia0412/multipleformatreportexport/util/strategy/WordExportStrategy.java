package io.github.asthenia0412.multipleformatreportexport.util.strategy;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import io.github.asthenia0412.multipleformatreportexport.util.ExportStrategy;
import io.github.asthenia0412.multipleformatreportexport.util.ReportExportUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Word导出策略实现
 */
public class WordExportStrategy implements ExportStrategy {
    
    @Override
    public CompletableFuture<byte[]> export(List<CodeAnalysis> dataList) {
        return ReportExportUtil.exportWord(dataList);
    }
    
    @Override
    public String getFormatName() {
        return "Word";
    }
    
    @Override
    public String getMimeType() {
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    }
    
    @Override
    public String getFileExtension() {
        return "docx";
    }
}