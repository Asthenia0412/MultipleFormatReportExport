package io.github.asthenia0412.multipleformatreportexport.service;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import io.github.asthenia0412.multipleformatreportexport.entity.FormatInfo;
import io.github.asthenia0412.multipleformatreportexport.mapper.CodeAnalysisMapper;
import io.github.asthenia0412.multipleformatreportexport.mapper.FormatInfoMapper;
import io.github.asthenia0412.multipleformatreportexport.util.ExportStrategy;
import io.github.asthenia0412.multipleformatreportexport.util.ExportStrategyFactory;
import io.github.asthenia0412.multipleformatreportexport.util.HtmlReportGenerator;
import io.github.asthenia0412.multipleformatreportexport.util.PdfReportGenerator;
import io.github.asthenia0412.multipleformatreportexport.util.XmlReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    
    @Autowired
    private CodeAnalysisMapper codeAnalysisMapper;
    
    @Autowired
    private FormatInfoMapper formatInfoMapper;

    @Override
    public CompletableFuture<byte[]> exportReport(String format, String issueType, int page, int pageSize) {
        if (page < 1 || pageSize < 1) {
            throw new IllegalArgumentException("页码和页大小必须为正数");
        }
        
        // 检查格式是否支持
        if (!ExportStrategyFactory.isFormatSupported(format)) {
            throw new IllegalArgumentException("不支持的导出格式: " + format);
        }
        
        List<CodeAnalysis> dataList = getPagedData(issueType, page, pageSize);
        if (dataList.isEmpty()) {
            throw new IllegalStateException("没有可导出的数据");
        }

        // 使用策略模式创建导出策略
        ExportStrategy exportStrategy = ExportStrategyFactory.createStrategy(format);
        
        // 直接使用策略的导出方法，它已经是异步的
        return exportStrategy.export(dataList);
    }

    @Override
    public List<CodeAnalysis> getPagedData(String issueType, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        if (StringUtils.hasText(issueType)) {
            return codeAnalysisMapper.findByIssueType(issueType, offset, pageSize);
        } else {
            return codeAnalysisMapper.findAllWithPagination(offset, pageSize);
        }
    }

    @Override
    public int getTotalCount() {
        return codeAnalysisMapper.countAll();
    }

    @Override
    public String[] getSupportedFormats() {
        try {
            List<FormatInfo> supportedFormats = formatInfoMapper.selectSupportedFormats();
            return supportedFormats.stream()
                    .map(FormatInfo::getFormatName)
                    .toArray(String[]::new);
        } catch (Exception e) {
            // 如果数据库查询失败，返回默认支持的格式
            return new String[]{"xls", "xlsx", "docx", "pdf", "html", "xml"};
        }
    }

    @Override
    public FormatInfo getFormatInfo(String format) {
        try {
            return formatInfoMapper.selectByFormatName(format);
        } catch (Exception e) {
            // 如果数据库查询失败，返回默认格式信息
            return createDefaultFormatInfo(format);
        }
    }
    
    /**
     * 创建默认格式信息
     * @param format 格式名称
     * @return 格式信息
     */
    private FormatInfo createDefaultFormatInfo(String format) {
        String formatLower = format.toLowerCase();
        
        switch (formatLower) {
            case "xls":
                return new FormatInfo("xls", "Excel电子表格格式", 
                    "application/vnd.ms-excel", "xls", true, 
                    new String[]{"表格数据", "图表支持", "公式计算"});
            case "xlsx":
                return new FormatInfo("xlsx", "Excel 2007+格式", 
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx", true, 
                    new String[]{"现代Excel格式", "大数据支持", "图表增强"});
            case "docx":
                return new FormatInfo("docx", "Word文档格式", 
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx", true, 
                    new String[]{"文档格式", "样式支持", "图片嵌入"});
            case "pdf":
                return new FormatInfo("pdf", "PDF文档格式", 
                    "application/pdf", "pdf", true, 
                    new String[]{"跨平台", "打印友好", "安全可靠"});
            case "html":
                return new FormatInfo("html", "HTML网页格式", 
                    "text/html", "html", true, 
                    new String[]{"网页浏览", "样式丰富", "交互支持"});
            case "xml":
                return new FormatInfo("xml", "XML数据格式", 
                    "application/xml", "xml", true, 
                    new String[]{"数据交换", "结构化", "标准格式"});
            default:
                return new FormatInfo(format, "未知格式", 
                    "application/octet-stream", format, false, 
                    new String[]{"未知格式"});
        }
    }
}