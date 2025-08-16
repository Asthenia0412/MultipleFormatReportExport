package io.github.asthenia0412.multipleformatreportexport.util.strategy;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import io.github.asthenia0412.multipleformatreportexport.util.ExportStrategy;
import io.github.asthenia0412.multipleformatreportexport.util.ReportExportUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * XLS导出策略实现
 */
public class XlsExportStrategy implements ExportStrategy {
    
    @Override
    public CompletableFuture<byte[]> export(List<CodeAnalysis> dataList) {
        return ReportExportUtil.exportXls(dataList);
    }
    
    @Override
    public String getFormatName() {
        return "Excel";
    }
    
    @Override
    public String getMimeType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }
    
    @Override
    public String getFileExtension() {
        return "xlsx";
    }
}