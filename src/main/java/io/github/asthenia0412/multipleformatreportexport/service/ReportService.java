package io.github.asthenia0412.multipleformatreportexport.service;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import io.github.asthenia0412.multipleformatreportexport.entity.FormatInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ReportService {
    /**
     * 导出报告
     * @param format 导出格式
     * @param issueType 问题类型
     * @param page 页码
     * @param pageSize 页大小
     * @return 报告字节数组
     */
    CompletableFuture<byte[]> exportReport(String format, String issueType, int page, int pageSize);
    
    /**
     * 获取分页数据
     * @param issueType 问题类型
     * @param page 页码
     * @param pageSize 页大小
     * @return 数据列表
     */
    List<CodeAnalysis> getPagedData(String issueType, int page, int pageSize);
    
    /**
     * 获取总记录数
     * @return 总记录数
     */
    int getTotalCount();
    
    /**
     * 获取支持的导出格式
     * @return 支持的格式列表
     */
    String[] getSupportedFormats();
    
    /**
     * 获取格式信息
     * @param format 格式名称
     * @return 格式信息
     */
    FormatInfo getFormatInfo(String format);
}