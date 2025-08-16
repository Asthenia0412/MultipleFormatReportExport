package io.github.asthenia0412.multipleformatreportexport.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 导出格式信息实体类
 */
@Data
public class FormatInfo {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 格式名称
     */
    private String formatName;
    
    /**
     * 格式描述
     */
    private String description;
    
    /**
     * MIME类型
     */
    private String mimeType;
    
    /**
     * 文件扩展名
     */
    private String fileExtension;
    
    /**
     * 是否支持
     */
    private boolean supported;
    
    /**
     * 格式特性
     */
    private String[] features;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    public FormatInfo() {}
    
    public FormatInfo(String formatName, String description, String mimeType, 
                     String fileExtension, boolean supported, String[] features) {
        this.formatName = formatName;
        this.description = description;
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
        this.supported = supported;
        this.features = features;
    }
    
    public FormatInfo(Integer id, String formatName, String description, String mimeType, 
                     String fileExtension, boolean supported, String[] features, 
                     LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.id = id;
        this.formatName = formatName;
        this.description = description;
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
        this.supported = supported;
        this.features = features;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }
}