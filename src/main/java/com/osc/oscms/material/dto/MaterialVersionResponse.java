package com.osc.oscms.material.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资料版本响应DTO
 */
@Data
@Schema(description = "资料版本响应")
public class MaterialVersionResponse {

    @Schema(description = "版本ID", example = "1")
    private Long id;

    @Schema(description = "资料主表ID", example = "1")
    private Long materialId;

    @Schema(description = "版本号", example = "1")
    private Integer version;

    @Schema(description = "文件名", example = "第一章.pdf")
    private String filename;

    @Schema(description = "文件存储URL", example = "https://example.com/files/chapter1.pdf")
    private String url;

    @Schema(description = "版本描述", example = "初始版本")
    private String description;

    @Schema(description = "上传时间")
    private LocalDateTime uploadedAt;
}

