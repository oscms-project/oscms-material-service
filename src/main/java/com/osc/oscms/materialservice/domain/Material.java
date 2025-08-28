package com.osc.oscms.materialservice.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教学资料实体类
 */
@Data
@TableName("osc_material")
public class Material {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("course_id")
    private Long courseId;

    @TableField("chapter_order")
    private Integer chapterOrder;

    /**
     * 可见班级ID列表，JSON格式存储
     * 例：[1,2,3] 表示班级1、2、3可见
     */
    @TableField("visible_classes")
    private String visibleClasses;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
