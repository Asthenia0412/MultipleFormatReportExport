package io.github.asthenia0412.multipleformatreportexport.entity;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CodeAnalysis {
    private Long id; // 记录ID，主键自增

    @NotBlank(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    private String fileName; // 文件名

    @NotBlank(message = "文件路径不能为空")
    @Size(max = 512, message = "文件路径长度不能超过512个字符")
    private String filePath; // 文件路径

    @NotNull(message = "代码行数不能为空")
    private Integer codeLine; // 代码行数

    @NotNull(message = "问题数量不能为空")
    private Integer issueCount; // 问题数量

    @Size(max = 100, message = "问题类型长度不能超过100个字符")
    private String issueType; // 问题类型

    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}