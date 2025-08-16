package io.github.asthenia0412.multipleformatreportexport.service;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ReportService {
    CompletableFuture<byte[]> exportReport(String format, String issueType, int page, int pageSize);
    List<CodeAnalysis> getPagedData(String issueType, int page, int pageSize);
    int getTotalCount();
}