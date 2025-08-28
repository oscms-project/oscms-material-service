package com.osc.oscms.material.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 更新资料请求DTO
 */
@Data
@Schema(description = "更新资料请求")
public class MaterialUpdateRequest {

    @Schema(description = "章节顺序", example = "1")
    private Integer chapterOrder;

    @Schema(description = "文件名", example = "第一章-修订版.pdf")
    private String filename;

    @Schema(description = "资料类型", example = "pdf")
    private String type;

    @Schema(description = "可见班级ID列表")
    private List<Long> visibleClasses;
}

