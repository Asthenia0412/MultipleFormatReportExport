package io.github.asthenia0412.multipleformatreportexport.controller;

import io.github.asthenia0412.multipleformatreportexport.entity.FormatInfo;
import io.github.asthenia0412.multipleformatreportexport.service.ReportService;
import io.github.asthenia0412.multipleformatreportexport.util.ExportStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "*")
public class ReportController {
    
    @Autowired
    private ReportService reportService;

    /**
     * 导出报告
     */
    @GetMapping("/export")
    public CompletableFuture<ResponseEntity<byte[]>> export(
            @RequestParam(defaultValue = "xls") String format,
            @RequestParam(defaultValue = "bug") String issueType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1000") int pageSize) {
        
        // 验证格式是否支持
        if (!ExportStrategyFactory.isFormatSupported(format)) {
            // CompletedFuture的会用NIL解决空指针问题-内部存储一个CompletableFuture实例
            return CompletableFuture.completedFuture(
                ResponseEntity.badRequest().body("不支持的导出格式: ".getBytes())
            );
        }
        
        return reportService.exportReport(format, issueType, page, pageSize)
                .thenApply(bytes -> {
                    // 获取格式信息
                    FormatInfo formatInfo = reportService.getFormatInfo(format);
                    String fileName = "report." + formatInfo.getFileExtension();
                    String contentType = formatInfo.getMimeType();
                    
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                            .contentType(MediaType.parseMediaType(contentType))
                            .body(bytes);
                })
                .exceptionally(throwable -> {
                    return ResponseEntity.internalServerError()
                            .body(("导出失败: " + throwable.getMessage()).getBytes());
                });
    }
    
    /**
     * 获取支持的导出格式列表
     */
    @GetMapping("/formats")
    public ResponseEntity<String[]> getSupportedFormats() {
        String[] formats = reportService.getSupportedFormats();
        return ResponseEntity.ok(formats);
    }
    
    /**
     * 获取特定格式的详细信息
     */
    @GetMapping("/formats/{format}")
    public ResponseEntity<FormatInfo> getFormatInfo(@PathVariable String format) {
        FormatInfo formatInfo = reportService.getFormatInfo(format);
        if (formatInfo != null) {
            return ResponseEntity.ok(formatInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 获取总记录数
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getTotalCount() {
        int count = reportService.getTotalCount();
        return ResponseEntity.ok(count);
    }
    
    /**
     * 获取分页数据（用于预览）
     */
    @GetMapping("/data")
    public ResponseEntity<?> getPagedData(
            @RequestParam(defaultValue = "bug") String issueType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            return ResponseEntity.badRequest().body("页码和页大小必须为正数，页大小不能超过100");
        }
        
        try {
            Object data = reportService.getPagedData(issueType, page, pageSize);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("获取数据失败: " + e.getMessage());
        }
    }
}