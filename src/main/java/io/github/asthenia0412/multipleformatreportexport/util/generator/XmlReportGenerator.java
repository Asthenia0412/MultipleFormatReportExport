package io.github.asthenia0412.multipleformatreportexport.util.generator;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * XML报告生成器
 */
public class XmlReportGenerator {
    
    private static final String REPORT_TITLE = "代码质量检测报告";
    private static final String REPORT_SUBTITLE = "Code Quality Analysis Report";
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 生成XML报告
     * @param dataList 数据列表
     * @return XML字节数组
     */
    public static byte[] generateReport(List<CodeAnalysis> dataList) throws Exception {
        String xml = generateXmlContent(dataList);
        return xml.getBytes("UTF-8");
    }
    
    /**
     * 生成XML内容
     */
    private static String generateXmlContent(List<CodeAnalysis> dataList) {
        StringBuilder xml = new StringBuilder();
        
        // XML声明
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        
        // 根元素
        xml.append("<codeQualityReport xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        xml.append("                   xsi:noNamespaceSchemaLocation=\"code-quality-report.xsd\">\n");
        
        // 报告头部信息
        xml.append("    <reportHeader>\n");
        xml.append("        <title>").append(escapeXml(REPORT_TITLE)).append("</title>\n");
        xml.append("        <subtitle>").append(escapeXml(REPORT_SUBTITLE)).append("</subtitle>\n");
        xml.append("        <generatedAt>").append(java.time.LocalDateTime.now().format(DATE_TIME_FORMATTER)).append("</generatedAt>\n");
        xml.append("        <version>1.0</version>\n");
        xml.append("    </reportHeader>\n");
        
        // 执行摘要
        xml.append("    <executiveSummary>\n");
        int totalFiles = dataList.size();
        int totalIssues = dataList.stream().mapToInt(data -> data.getIssueCount() != null ? data.getIssueCount() : 0).sum();
        int totalCodeLines = dataList.stream().mapToInt(data -> data.getCodeLine() != null ? data.getCodeLine() : 0).sum();
        double issueDensity = totalCodeLines > 0 ? (double) totalIssues / totalCodeLines * 1000 : 0;
        
        xml.append("        <totalFiles>").append(totalFiles).append("</totalFiles>\n");
        xml.append("        <totalIssues>").append(totalIssues).append("</totalIssues>\n");
        xml.append("        <totalCodeLines>").append(totalCodeLines).append("</totalCodeLines>\n");
        xml.append("        <averageIssueDensity>").append(String.format("%.2f", issueDensity)).append("</averageIssueDensity>\n");
        xml.append("        <issueDensityUnit>问题/千行</issueDensityUnit>\n");
        xml.append("    </executiveSummary>\n");
        
        // 问题类型统计
        xml.append("    <issueTypeStatistics>\n");
        Map<String, Long> issueTypeStats = dataList.stream()
            .filter(data -> data.getIssueType() != null && !data.getIssueType().isEmpty())
            .collect(Collectors.groupingBy(
                CodeAnalysis::getIssueType,
                Collectors.counting()
            ));
        
        for (Map.Entry<String, Long> entry : issueTypeStats.entrySet()) {
            xml.append("        <issueType>\n");
            xml.append("            <type>").append(escapeXml(entry.getKey())).append("</type>\n");
            xml.append("            <fileCount>").append(entry.getValue()).append("</fileCount>\n");
            xml.append("        </issueType>\n");
        }
        xml.append("    </issueTypeStatistics>\n");
        
        // 详细检测结果
        xml.append("    <detailedResults>\n");
        for (CodeAnalysis data : dataList) {
            if (data != null) {
                xml.append("        <file>\n");
                xml.append("            <id>").append(data.getId() != null ? data.getId() : "N/A").append("</id>\n");
                xml.append("            <fileName>").append(escapeXml(data.getFileName() != null ? data.getFileName() : "N/A")).append("</fileName>\n");
                xml.append("            <filePath>").append(escapeXml(data.getFilePath() != null ? data.getFilePath() : "N/A")).append("</filePath>\n");
                xml.append("            <codeLine>").append(data.getCodeLine() != null ? data.getCodeLine() : 0).append("</codeLine>\n");
                xml.append("            <issueCount>").append(data.getIssueCount() != null ? data.getIssueCount() : 0).append("</issueCount>\n");
                xml.append("            <issueType>").append(escapeXml(data.getIssueType() != null ? data.getIssueType() : "N/A")).append("</issueType>\n");
                xml.append("            <createdAt>").append(data.getCreatedAt() != null ? DATE_TIME_FORMATTER.format(data.getCreatedAt()) : "N/A").append("</createdAt>\n");
                xml.append("            <updatedAt>").append(data.getUpdatedAt() != null ? DATE_TIME_FORMATTER.format(data.getUpdatedAt()) : "N/A").append("</updatedAt>\n");
                xml.append("        </file>\n");
            }
        }
        xml.append("    </detailedResults>\n");
        
        // 建议和改进措施
        xml.append("    <recommendations>\n");
        String[] recommendations = {
            "定期进行代码质量检测，建立代码质量门禁机制",
            "对发现的问题进行分类处理，优先解决高严重性问题",
            "建立代码审查流程，提高代码质量意识",
            "使用自动化工具进行持续集成和持续部署",
            "定期进行代码重构，减少技术债务"
        };
        
        for (String rec : recommendations) {
            xml.append("        <recommendation>").append(escapeXml(rec)).append("</recommendation>\n");
        }
        xml.append("    </recommendations>\n");
        
        // 报告元数据
        xml.append("    <metadata>\n");
        xml.append("        <generator>MultipleFormatReportExport</generator>\n");
        xml.append("        <format>XML</format>\n");
        xml.append("        <encoding>UTF-8</encoding>\n");
        xml.append("        <schemaVersion>1.0</schemaVersion>\n");
        xml.append("    </metadata>\n");
        
        // 根元素结束
        xml.append("</codeQualityReport>");
        
        return xml.toString();
    }
    
    /**
     * 转义XML特殊字符
     */
    private static String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&apos;");
    }
}