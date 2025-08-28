package com.osc.oscms.materialservice.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osc.oscms.materialservice.domain.Material;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 教学资料数据访问层
 */
@Mapper
public interface MaterialRepository extends BaseMapper<Material> {
    
    /**
     * 根据课程ID查询资料列表
     */
    @Select("SELECT * FROM osc_material WHERE course_id = #{courseId} ORDER BY chapter_order ASC, created_at DESC")
    List<Material> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据课程ID和章节顺序查询资料
     */
    @Select("SELECT * FROM osc_material WHERE course_id = #{courseId} AND chapter_order = #{chapterOrder} ORDER BY created_at DESC")
    List<Material> findByCourseIdAndChapterOrder(@Param("courseId") Long courseId, @Param("chapterOrder") Integer chapterOrder);
    
    /**
     * 查询某个课程某个班级可见的资料
     */
    @Select("SELECT * FROM osc_material WHERE course_id = #{courseId} " +
            "AND (visible_classes IS NULL OR JSON_CONTAINS(visible_classes, CAST(#{classId} AS JSON))) " +
            "ORDER BY chapter_order ASC, created_at DESC")
    List<Material> findVisibleMaterialsByCourseAndClass(@Param("courseId") Long courseId, @Param("classId") Long classId);
    
    /**
     * 获取课程的最大章节顺序号
     */
    @Select("SELECT COALESCE(MAX(chapter_order), 0) FROM osc_material WHERE course_id = #{courseId}")
    Integer getMaxChapterOrderByCourseId(@Param("courseId") Long courseId);
}
