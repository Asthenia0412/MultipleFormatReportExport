package io.github.asthenia0412.multipleformatreportexport.controller;


import io.github.asthenia0412.multipleformatreportexport.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/export")
    public CompletableFuture<ResponseEntity<byte[]>> export(
            @RequestParam(defaultValue = "xls") String format,
            @RequestParam(defaultValue = "bug") String issueType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1000") int pageSize) {
        return reportService.exportReport(format, issueType, page, pageSize)
                .thenApply(bytes -> {

                    String fileName = "report." + format.toLowerCase();
                    String contentType = format.equalsIgnoreCase("xls") ?
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                            .contentType(MediaType.parseMediaType(contentType))
                            .body(bytes);
                });
    }
}