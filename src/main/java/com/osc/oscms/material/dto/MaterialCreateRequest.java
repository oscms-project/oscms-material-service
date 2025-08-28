package com.osc.oscms.material.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建资料请求DTO
 */
@Data
@Schema(description = "创建资料请求")
public class MaterialCreateRequest {

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "所属课程ID", example = "1")
    private Long courseId;

    @NotNull(message = "章节顺序不能为空")
    @Schema(description = "章节顺序", example = "1")
    private Integer chapterOrder;

    @NotBlank(message = "文件名不能为空")
    @Schema(description = "文件名", example = "第一章.pdf")
    private String filename;

    @NotBlank(message = "资料类型不能为空")
    @Schema(description = "资料类型", example = "pdf")
    private String type;

    @Schema(description = "可见班级ID列表")
    private List<Long> visibleClasses;

    @NotBlank(message = "文件URL不能为空")
    @Schema(description = "文件存储URL", example = "https://example.com/files/chapter1.pdf")
    private String url;

    @Schema(description = "版本描述", example = "初始版本")
    private String description;
}

