package io.github.asthenia0412.multipleformatreportexport.util;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HTML报告生成器
 */
public class HtmlReportGenerator {
    
    private static final String REPORT_TITLE = "代码质量检测报告";
    private static final String REPORT_SUBTITLE = "Code Quality Analysis Report";
    private static final String[] HEADERS = {
            "ID", "文件名", "文件路径", "代码行数",
            "问题数量", "问题类型", "创建时间", "更新时间"
    };
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 生成HTML报告
     * @param dataList 数据列表
     * @return HTML字节数组
     */
    public static byte[] generateReport(List<CodeAnalysis> dataList) throws Exception {
        String html = generateHtmlContent(dataList);
        return html.getBytes("UTF-8");
    }
    
    /**
     * 生成HTML内容
     */
    private static String generateHtmlContent(List<CodeAnalysis> dataList) {
        StringBuilder html = new StringBuilder();
        
        // HTML头部
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"zh-CN\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>").append(REPORT_TITLE).append("</title>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: 'Microsoft YaHei', Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n");
        html.append("        .container { max-width: 1200px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 20px rgba(0,0,0,0.1); }\n");
        html.append("        .header { text-align: center; margin-bottom: 40px; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-radius: 10px; }\n");
        html.append("        .title { font-size: 28px; font-weight: bold; margin-bottom: 10px; }\n");
        html.append("        .subtitle { font-size: 20px; margin-bottom: 15px; opacity: 0.9; }\n");
        html.append("        .date { font-size: 16px; opacity: 0.8; }\n");
        html.append("        .section { margin: 40px 0; padding: 20px; border-left: 4px solid #667eea; background-color: #fafafa; border-radius: 5px; }\n");
        html.append("        .section-title { font-size: 22px; font-weight: bold; color: #333; margin-bottom: 20px; display: flex; align-items: center; }\n");
        html.append("        .section-title::before { content: '📊'; margin-right: 10px; }\n");
        html.append("        table { width: 100%; border-collapse: collapse; margin: 20px 0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }\n");
        html.append("        th, td { border: 1px solid #e0e0e0; padding: 12px; text-align: left; }\n");
        html.append("        th { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; font-weight: bold; }\n");
        html.append("        tr:nth-child(even) { background-color: #f8f9fa; }\n");
        html.append("        tr:hover { background-color: #e3f2fd; transition: background-color 0.3s; }\n");
        html.append("        .summary-table { width: 60%; margin: 20px auto; }\n");
        html.append("        .stats-table { width: 50%; margin: 20px auto; }\n");
        html.append("        .recommendations { background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%); padding: 25px; border-radius: 10px; margin: 20px 0; }\n");
        html.append("        .recommendations ul { margin: 15px 0; }\n");
        html.append("        .recommendations li { margin: 10px 0; padding: 8px 0; border-bottom: 1px solid rgba(0,0,0,0.1); }\n");
        html.append("        .metric-card { display: inline-block; margin: 10px; padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); text-align: center; min-width: 150px; }\n");
        html.append("        .metric-value { font-size: 24px; font-weight: bold; color: #667eea; }\n");
        html.append("        .metric-label { font-size: 14px; color: #666; margin-top: 5px; }\n");
        html.append("        .toc { background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; }\n");
        html.append("        .toc ul { list-style: none; padding: 0; }\n");
        html.append("        .toc li { margin: 8px 0; }\n");
        html.append("        .toc a { color: #667eea; text-decoration: none; font-weight: 500; }\n");
        html.append("        .toc a:hover { text-decoration: underline; }\n");
        html.append("        @media print { body { background-color: white; } .container { box-shadow: none; } }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        
        // 报告标题
        html.append("        <div class=\"header\">\n");
        html.append("            <div class=\"title\">").append(REPORT_TITLE).append("</div>\n");
        html.append("            <div class=\"subtitle\">").append(REPORT_SUBTITLE).append("</div>\n");
        html.append("            <div class=\"date\">生成时间：").append(java.time.LocalDateTime.now().format(DATE_TIME_FORMATTER)).append("</div>\n");
        html.append("        </div>\n");
        
        // 目录
        html.append("        <div class=\"toc\">\n");
        html.append("            <h3>📋 目录</h3>\n");
        html.append("            <ul>\n");
        html.append("                <li><a href=\"#summary\">1. 执行摘要</a></li>\n");
        html.append("                <li><a href=\"#details\">2. 详细检测结果</a></li>\n");
        html.append("                <li><a href=\"#statistics\">3. 问题统计分析</a></li>\n");
        html.append("                <li><a href=\"#recommendations\">4. 建议和改进措施</a></li>\n");
        html.append("            </ul>\n");
        html.append("        </div>\n");
        
        // 执行摘要
        html.append("        <div id=\"summary\" class=\"section\">\n");
        html.append("            <div class=\"section-title\">执行摘要</div>\n");
        
        int totalFiles = dataList.size();
        int totalIssues = dataList.stream().mapToInt(data -> data.getIssueCount() != null ? data.getIssueCount() : 0).sum();
        int totalCodeLines = dataList.stream().mapToInt(data -> data.getCodeLine() != null ? data.getCodeLine() : 0).sum();
        double issueDensity = totalCodeLines > 0 ? (double) totalIssues / totalCodeLines * 1000 : 0;
        
        // 指标卡片
        html.append("            <div style=\"text-align: center; margin: 20px 0;\">\n");
        html.append("                <div class=\"metric-card\">\n");
        html.append("                    <div class=\"metric-value\">").append(totalFiles).append("</div>\n");
        html.append("                    <div class=\"metric-label\">检测文件总数</div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"metric-card\">\n");
        html.append("                    <div class=\"metric-value\">").append(totalIssues).append("</div>\n");
        html.append("                    <div class=\"metric-label\">发现问题总数</div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"metric-card\">\n");
        html.append("                    <div class=\"metric-value\">").append(totalCodeLines).append("</div>\n");
        html.append("                    <div class=\"metric-label\">代码总行数</div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"metric-card\">\n");
        html.append("                    <div class=\"metric-value\">").append(String.format("%.2f", issueDensity)).append("</div>\n");
        html.append("                    <div class=\"metric-label\">问题/千行</div>\n");
        html.append("                </div>\n");
        html.append("            </div>\n");
        
        html.append("            <table class=\"summary-table\">\n");
        html.append("                <tr><th>指标</th><th>数值</th></tr>\n");
        html.append("                <tr><td>检测文件总数</td><td>").append(totalFiles).append("</td></tr>\n");
        html.append("                <tr><td>发现问题总数</td><td>").append(totalIssues).append("</td></tr>\n");
        html.append("                <tr><td>代码总行数</td><td>").append(totalCodeLines).append("</td></tr>\n");
        html.append("                <tr><td>平均问题密度</td><td>").append(String.format("%.2f", issueDensity)).append(" 问题/千行</td></tr>\n");
        html.append("            </table>\n");
        html.append("        </div>\n");
        
        // 详细检测结果
        html.append("        <div id=\"details\" class=\"section\">\n");
        html.append("            <div class=\"section-title\">详细检测结果</div>\n");
        html.append("            <table>\n");
        html.append("                <tr>");
        for (String header : HEADERS) {
            html.append("<th>").append(header).append("</th>");
        }
        html.append("</tr>\n");
        
        for (CodeAnalysis data : dataList) {
            if (data != null) {
                html.append("                <tr>");
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
        html.append("            </table>\n");
        html.append("        </div>\n");
        
        // 问题统计分析
        html.append("        <div id=\"statistics\" class=\"section\">\n");
        html.append("            <div class=\"section-title\">问题统计分析</div>\n");
        
        Map<String, Long> issueTypeStats = dataList.stream()
            .filter(data -> data.getIssueType() != null && !data.getIssueType().isEmpty())
            .collect(Collectors.groupingBy(
                CodeAnalysis::getIssueType,
                Collectors.counting()
            ));
        
        html.append("            <table class=\"stats-table\">\n");
        html.append("                <tr><th>问题类型</th><th>文件数量</th></tr>\n");
        for (Map.Entry<String, Long> entry : issueTypeStats.entrySet()) {
            html.append("                <tr><td>").append(entry.getKey()).append("</td><td>").append(entry.getValue()).append("</td></tr>\n");
        }
        html.append("            </table>\n");
        html.append("        </div>\n");
        
        // 建议和改进措施
        html.append("        <div id=\"recommendations\" class=\"section\">\n");
        html.append("            <div class=\"section-title\">建议和改进措施</div>\n");
        html.append("            <div class=\"recommendations\">\n");
        html.append("                <ul>\n");
        html.append("                    <li>定期进行代码质量检测，建立代码质量门禁机制</li>\n");
        html.append("                    <li>对发现的问题进行分类处理，优先解决高严重性问题</li>\n");
        html.append("                    <li>建立代码审查流程，提高代码质量意识</li>\n");
        html.append("                    <li>使用自动化工具进行持续集成和持续部署</li>\n");
        html.append("                    <li>定期进行代码重构，减少技术债务</li>\n");
        html.append("                </ul>\n");
        html.append("            </div>\n");
        html.append("        </div>\n");
        
        // 容器和HTML尾部
        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>");
        
        return html.toString();
    }
}