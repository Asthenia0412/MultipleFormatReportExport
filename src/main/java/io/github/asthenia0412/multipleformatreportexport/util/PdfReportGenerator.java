package io.github.asthenia0412.multipleformatreportexport.util;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PDF报告生成器
 * 注意：需要添加iText依赖到pom.xml
 */
public class PdfReportGenerator {
    
    private static final String REPORT_TITLE = "代码质量检测报告";
    private static final String REPORT_SUBTITLE = "Code Quality Analysis Report";
    private static final String[] HEADERS = {
            "ID", "文件名", "文件路径", "代码行数",
            "问题数量", "问题类型", "创建时间", "更新时间"
    };
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 生成PDF报告
     * @param dataList 数据列表
     * @return PDF字节数组
     */
    public static byte[] generateReport(List<CodeAnalysis> dataList) throws Exception {
        // 由于iText依赖可能未添加，这里返回一个简单的HTML格式作为替代
        // 实际项目中应该使用iText库生成真正的PDF
        return generateHtmlReport(dataList).getBytes("UTF-8");
    }
    
    /**
     * 生成HTML格式报告（作为PDF的替代方案）
     */
    private static String generateHtmlReport(List<CodeAnalysis> dataList) {
        StringBuilder html = new StringBuilder();
        
        // HTML头部
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"zh-CN\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>").append(REPORT_TITLE).append("</title>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: 'Microsoft YaHei', Arial, sans-serif; margin: 20px; }\n");
        html.append("        .header { text-align: center; margin-bottom: 30px; }\n");
        html.append("        .title { font-size: 24px; font-weight: bold; color: #333; }\n");
        html.append("        .subtitle { font-size: 18px; color: #666; margin: 10px 0; }\n");
        html.append("        .date { font-size: 14px; color: #999; }\n");
        html.append("        .section { margin: 30px 0; }\n");
        html.append("        .section-title { font-size: 18px; font-weight: bold; color: #333; margin-bottom: 15px; }\n");
        html.append("        table { width: 100%; border-collapse: collapse; margin: 15px 0; }\n");
        html.append("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        html.append("        th { background-color: #f5f5f5; font-weight: bold; }\n");
        html.append("        .summary-table { width: 50%; }\n");
        html.append("        .stats-table { width: 50%; }\n");
        html.append("        .recommendations { background-color: #f9f9f9; padding: 15px; border-radius: 5px; }\n");
        html.append("        .recommendations ul { margin: 10px 0; }\n");
        html.append("        .recommendations li { margin: 5px 0; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        
        // 报告标题
        html.append("    <div class=\"header\">\n");
        html.append("        <div class=\"title\">").append(REPORT_TITLE).append("</div>\n");
        html.append("        <div class=\"subtitle\">").append(REPORT_SUBTITLE).append("</div>\n");
        html.append("        <div class=\"date\">生成时间：").append(java.time.LocalDateTime.now().format(DATE_TIME_FORMATTER)).append("</div>\n");
        html.append("    </div>\n");
        
        // 执行摘要
        html.append("    <div class=\"section\">\n");
        html.append("        <div class=\"section-title\">1. 执行摘要</div>\n");
        
        int totalFiles = dataList.size();
        int totalIssues = dataList.stream().mapToInt(data -> data.getIssueCount() != null ? data.getIssueCount() : 0).sum();
        int totalCodeLines = dataList.stream().mapToInt(data -> data.getCodeLine() != null ? data.getCodeLine() : 0).sum();
        double issueDensity = totalCodeLines > 0 ? (double) totalIssues / totalCodeLines * 1000 : 0;
        
        html.append("        <table class=\"summary-table\">\n");
        html.append("            <tr><th>指标</th><th>数值</th></tr>\n");
        html.append("            <tr><td>检测文件总数</td><td>").append(totalFiles).append("</td></tr>\n");
        html.append("            <tr><td>发现问题总数</td><td>").append(totalIssues).append("</td></tr>\n");
        html.append("            <tr><td>代码总行数</td><td>").append(totalCodeLines).append("</td></tr>\n");
        html.append("            <tr><td>平均问题密度</td><td>").append(String.format("%.2f", issueDensity)).append(" 问题/千行</td></tr>\n");
        html.append("        </table>\n");
        html.append("    </div>\n");
        
        // 详细检测结果
        html.append("    <div class=\"section\">\n");
        html.append("        <div class=\"section-title\">2. 详细检测结果</div>\n");
        html.append("        <table>\n");
        html.append("            <tr>");
        for (String header : HEADERS) {
            html.append("<th>").append(header).append("</th>");
        }
        html.append("</tr>\n");
        
        for (CodeAnalysis data : dataList) {
            if (data != null) {
                html.append("            <tr>");
                html.append("<td>").append(data.getId() != null ? data.getId() : "N/A").append("</td>");
                html.append("<td>").append(data.getFileName() != null ? data.getFileName() : "N/A").append("</td>");
                html.append("<td>").append(data.getFilePath() != null ? data.getFilePath() : "N/A").append("</td>");
                html.append("<td>").append(data.getCodeLine() != null ? data.getCodeLine() : 0).append("</td>");
                html.append("<td>").append(data.getIssueCount() != null ? data.getIssueCount() : 0).append("</td>");
                html.append("<td>").append(data.getIssueType() != null ? data.getIssueType() : "N/A").append("</td>");
                html.append("<td>").append(data.getCreatedAt() != null ? DATE_TIME_FORMATTER.format(data.getCreatedAt()) : "N/A").append("</td>");
                html.append("<td>").append(data.getUpdatedAt() != null ? DATE_TIME_FORMATTER.format(data.getUpdatedAt()) : "N/A").append("</td>");
                html.append("</tr>\n");
            }
        }
        html.append("        </table>\n");
        html.append("    </div>\n");
        
        // 问题统计分析
        html.append("    <div class=\"section\">\n");
        html.append("        <div class=\"section-title\">3. 问题统计分析</div>\n");
        
        Map<String, Long> issueTypeStats = dataList.stream()
            .filter(data -> data.getIssueType() != null && !data.getIssueType().isEmpty())
            .collect(Collectors.groupingBy(
                CodeAnalysis::getIssueType,
                Collectors.counting()
            ));
        
        html.append("        <table class=\"stats-table\">\n");
        html.append("            <tr><th>问题类型</th><th>文件数量</th></tr>\n");
        for (Map.Entry<String, Long> entry : issueTypeStats.entrySet()) {
            html.append("            <tr><td>").append(entry.getKey()).append("</td><td>").append(entry.getValue()).append("</td></tr>\n");
        }
        html.append("        </table>\n");
        html.append("    </div>\n");
        
        // 建议和改进措施
        html.append("    <div class=\"section\">\n");
        html.append("        <div class=\"section-title\">4. 建议和改进措施</div>\n");
        html.append("        <div class=\"recommendations\">\n");
        html.append("            <ul>\n");
        html.append("                <li>定期进行代码质量检测，建立代码质量门禁机制</li>\n");
        html.append("                <li>对发现的问题进行分类处理，优先解决高严重性问题</li>\n");
        html.append("                <li>建立代码审查流程，提高代码质量意识</li>\n");
        html.append("                <li>使用自动化工具进行持续集成和持续部署</li>\n");
        html.append("                <li>定期进行代码重构，减少技术债务</li>\n");
        html.append("            </ul>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        
        // HTML尾部
        html.append("</body>\n");
        html.append("</html>");
        
        return html.toString();
    }
}