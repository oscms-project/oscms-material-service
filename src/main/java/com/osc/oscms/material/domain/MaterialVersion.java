package com.osc.oscms.material.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资料版本表实体类
 */
@Data
@TableName("osc_material_version")
public class MaterialVersion {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资料主表ID
     */
    @TableField("material_id")
    private Long materialId;

    /**
     * 版本号
     */
    @TableField("version")
    private Integer version;

    /**
     * 文件名
     */
    @TableField("filename")
    private String filename;

    /**
     * 文件存储URL
     */
    @TableField("url")
    private String url;

    /**
     * 版本描述
     */
    @TableField("description")
    private String description;

    /**
     * 上传时间
     */
    @TableField(value = "uploaded_at", fill = FieldFill.INSERT)
    private LocalDateTime uploadedAt;
}

