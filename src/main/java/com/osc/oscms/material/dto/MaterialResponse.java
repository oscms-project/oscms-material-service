package com.osc.oscms.material.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资料响应DTO
 */
@Data
@Schema(description = "资料响应")
public class MaterialResponse {

    @Schema(description = "资料ID", example = "1")
    private Long id;

    @Schema(description = "所属课程ID", example = "1")
    private Long courseId;

    @Schema(description = "章节顺序", example = "1")
    private Integer chapterOrder;

    @Schema(description = "文件名", example = "第一章.pdf")
    private String filename;

    @Schema(description = "资料类型", example = "pdf")
    private String type;

    @Schema(description = "可见班级ID列表")
    private List<Long> visibleClasses;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "资料版本列表")
    private List<MaterialVersionResponse> versions;

    @Schema(description = "最新版本")
    private MaterialVersionResponse latestVersion;
}

