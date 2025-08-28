package com.osc.oscms.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osc.oscms.material.domain.MaterialVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 资料版本Mapper接口
 */
@Mapper
public interface MaterialVersionMapper extends BaseMapper<MaterialVersion> {

    /**
     * 根据资料ID查询所有版本
     */
    @Select("SELECT * FROM osc_material_version WHERE material_id = #{materialId} ORDER BY version DESC")
    List<MaterialVersion> selectByMaterialId(@Param("materialId") Long materialId);

    /**
     * 根据资料ID查询最新版本
     */
    @Select("SELECT * FROM osc_material_version WHERE material_id = #{materialId} ORDER BY version DESC LIMIT 1")
    MaterialVersion selectLatestByMaterialId(@Param("materialId") Long materialId);

    /**
     * 根据资料ID获取下一个版本号
     */
    @Select("SELECT IFNULL(MAX(version), 0) + 1 FROM osc_material_version WHERE material_id = #{materialId}")
    Integer getNextVersion(@Param("materialId") Long materialId);
}

