package com.osc.oscms.material.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建资料版本请求DTO
 */
@Data
@Schema(description = "创建资料版本请求")
public class MaterialVersionCreateRequest {

    @NotBlank(message = "文件名不能为空")
    @Schema(description = "文件名", example = "第一章-v2.pdf")
    private String filename;

    @NotBlank(message = "文件URL不能为空")
    @Schema(description = "文件存储URL", example = "https://example.com/files/chapter1-v2.pdf")
    private String url;

    @Schema(description = "版本描述", example = "修正了部分内容错误")
    private String description;
}

