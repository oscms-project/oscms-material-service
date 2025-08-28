package com.osc.oscms.material.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资料主表实体类
 */
@Data
@TableName("osc_material")
public class Material {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属课程ID
     */
    @TableField("course_id")
    private Long courseId;

    /**
     * 章节顺序
     */
    @TableField("chapter_order")
    private Integer chapterOrder;

    /**
     * 资料组的通用/初始文件名
     */
    @TableField("filename")
    private String filename;

    /**
     * 资料的业务类型 (pdf, ppt, etc.)
     */
    @TableField("type")
    private String type;

    /**
     * 可见班级ID列表 (JSON数组)
     */
    @TableField("visible_classes")
    private String visibleClasses;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 资料版本列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<MaterialVersion> versions;
}

