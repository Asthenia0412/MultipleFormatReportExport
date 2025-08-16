package io.github.asthenia0412.multipleformatreportexport.util.strategy;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import io.github.asthenia0412.multipleformatreportexport.util.ExportStrategy;
import io.github.asthenia0412.multipleformatreportexport.util.ReportExportUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * HTML导出策略实现
 */
public class HtmlExportStrategy implements ExportStrategy {
    
    @Override
    public CompletableFuture<byte[]> export(List<CodeAnalysis> dataList) {
        return ReportExportUtil.exportHtml(dataList);
    }
    
    @Override
    public String getFormatName() {
        return "HTML";
    }
    
    @Override
    public String getMimeType() {
        return "text/html";
    }
    
    @Override
    public String getFileExtension() {
        return "html";
    }
}