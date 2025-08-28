package com.osc.oscms.materialservice.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教学资料版本实体类
 */
@Data
@TableName("osc_material_version")
public class MaterialVersion {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("material_id")
    private Long materialId;

    private Integer version;

    private String filename;

    private String url;

    private String description;

    @TableField("uploaded_at")
    private LocalDateTime uploadedAt;
}
