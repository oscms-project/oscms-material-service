package com.osc.oscms.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osc.oscms.material.domain.Material;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 资料Mapper接口
 */
@Mapper
public interface MaterialMapper extends BaseMapper<Material> {

    /**
     * 根据课程ID查询资料列表
     */
    @Select("SELECT * FROM osc_material WHERE course_id = #{courseId} ORDER BY chapter_order")
    List<Material> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据课程ID和班级ID查询可见的资料列表
     */
    @Select("SELECT * FROM osc_material WHERE course_id = #{courseId} " +
            "AND (visible_classes IS NULL OR JSON_CONTAINS(visible_classes, CAST(#{classId} AS JSON))) " +
            "ORDER BY chapter_order")
    List<Material> selectByCourseIdAndClassId(@Param("courseId") Long courseId, @Param("classId") Long classId);

    /**
     * 根据类型查询资料列表
     */
    @Select("SELECT * FROM osc_material WHERE type = #{type} ORDER BY created_at DESC")
    List<Material> selectByType(@Param("type") String type);
}

