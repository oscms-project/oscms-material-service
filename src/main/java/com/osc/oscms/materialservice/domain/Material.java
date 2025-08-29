package com.osc.oscms.materialservice.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 教学资料实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("osc_material")
public class Material {

    /**
     * 主键ID
     */
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
     * 可见班级ID列表，JSON格式存储
     * 例：[1,2,3] 表示班级1、2、3可见
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
}
