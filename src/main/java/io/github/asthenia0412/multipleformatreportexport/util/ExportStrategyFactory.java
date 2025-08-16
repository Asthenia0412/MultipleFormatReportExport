package io.github.asthenia0412.multipleformatreportexport.util;

import io.github.asthenia0412.multipleformatreportexport.util.strategy.*;

/**
 * 导出策略工厂类
 * 根据格式类型创建对应的导出策略
 */
public class ExportStrategyFactory {
    
    /**
     * 根据格式创建对应的导出策略
     * @param format 导出格式
     * @return 导出策略实例
     */
    public static ExportStrategy createStrategy(String format) {
        if (format == null) {
            throw new IllegalArgumentException("导出格式不能为空");
        }
        
        String formatLower = format.toLowerCase().trim();
        
        switch (formatLower) {
            case "xls":
            case "xlsx":
                return new XlsExportStrategy();
            case "docx":
                return new WordExportStrategy();
            case "pdf":
                return new PdfExportStrategy();
            case "html":
                return new HtmlExportStrategy();
            case "xml":
                return new XmlExportStrategy();
            default:
                throw new IllegalArgumentException("不支持的导出格式: " + format);
        }
    }
    
    /**
     * 检查格式是否支持
     * @param format 导出格式
     * @return 是否支持
     */
    public static boolean isFormatSupported(String format) {
        if (format == null) {
            return false;
        }
        
        String formatLower = format.toLowerCase().trim();
        return formatLower.matches("^(xls|xlsx|docx|pdf|html|xml)$");
    }
}
