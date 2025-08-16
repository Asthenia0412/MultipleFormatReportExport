package io.github.asthenia0412.multipleformatreportexport.util.strategy;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import io.github.asthenia0412.multipleformatreportexport.util.ExportStrategy;
import io.github.asthenia0412.multipleformatreportexport.util.ReportExportUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * XML导出策略实现
 */
public class XmlExportStrategy implements ExportStrategy {
    
    @Override
    public CompletableFuture<byte[]> export(List<CodeAnalysis> dataList) {
        return ReportExportUtil.exportXml(dataList);
    }
    
    @Override
    public String getFormatName() {
        return "XML";
    }
    
    @Override
    public String getMimeType() {
        return "application/xml";
    }
    
    @Override
    public String getFileExtension() {
        return "xml";
    }
}