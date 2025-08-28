package com.osc.oscms.materialservice.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osc.oscms.materialservice.domain.MaterialVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 教学资料版本数据访问层
 */
@Mapper
public interface MaterialVersionRepository extends BaseMapper<MaterialVersion> {
    
    /**
     * 根据资料ID查询所有版本
     */
    @Select("SELECT * FROM osc_material_version WHERE material_id = #{materialId} ORDER BY version DESC")
    List<MaterialVersion> findByMaterialId(@Param("materialId") Long materialId);
    
    /**
     * 根据资料ID获取最新版本
     */
    @Select("SELECT * FROM osc_material_version WHERE material_id = #{materialId} ORDER BY version DESC LIMIT 1")
    Optional<MaterialVersion> findLatestVersionByMaterialId(@Param("materialId") Long materialId);
    
    /**
     * 根据资料ID和版本号查询
     */
    @Select("SELECT * FROM osc_material_version WHERE material_id = #{materialId} AND version = #{version}")
    Optional<MaterialVersion> findByMaterialIdAndVersion(@Param("materialId") Long materialId, @Param("version") Integer version);
    
    /**
     * 获取资料的最大版本号
     */
    @Select("SELECT COALESCE(MAX(version), 0) FROM osc_material_version WHERE material_id = #{materialId}")
    Integer getMaxVersionByMaterialId(@Param("materialId") Long materialId);
    
    /**
     * 删除资料的所有版本
     */
    @Select("DELETE FROM osc_material_version WHERE material_id = #{materialId}")
    int deleteByMaterialId(@Param("materialId") Long materialId);
}
