package io.github.asthenia0412.multipleformatreportexport.service;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import io.github.asthenia0412.multipleformatreportexport.mapper.CodeAnalysisMapper;
import io.github.asthenia0412.multipleformatreportexport.util.ReportExportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private CodeAnalysisMapper codeAnalysisMapper;

    @Override
    public CompletableFuture<byte[]> exportReport(String format, String issueType, int page, int pageSize) {
        if (page < 1 || pageSize < 1) {
            throw new IllegalArgumentException("Page and pageSize must be positive");
        }
        List<CodeAnalysis> dataList = getPagedData(issueType, page, pageSize);
        if (dataList.isEmpty()) {
            throw new IllegalStateException("No data available for export");
        }

        switch (format.toLowerCase()) {
            case "xls":
                return ReportExportUtil.exportxls(dataList);
            case "word":
                return ReportExportUtil.exportWord(dataList);
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
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
}