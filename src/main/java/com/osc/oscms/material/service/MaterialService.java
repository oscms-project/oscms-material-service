package com.osc.oscms.material.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.osc.oscms.material.dto.*;

import java.util.List;

/**
 * 资料服务接口
 */
public interface MaterialService {

    /**
     * 创建资料
     */
    MaterialResponse createMaterial(MaterialCreateRequest request);

    /**
     * 根据ID获取资料详情
     */
    MaterialResponse getMaterialById(Long id);

    /**
     * 分页查询资料
     */
    IPage<MaterialResponse> getMaterials(Page<MaterialResponse> page, Long courseId, Long classId, String type);

    /**
     * 根据课程ID查询资料列表
     */
    List<MaterialResponse> getMaterialsByCourse(Long courseId);

    /**
     * 根据课程ID和班级ID查询可见的资料列表
     */
    List<MaterialResponse> getMaterialsByCourseAndClass(Long courseId, Long classId);

    /**
     * 更新资料
     */
    MaterialResponse updateMaterial(Long id, MaterialUpdateRequest request);

    /**
     * 删除资料
     */
    void deleteMaterial(Long id);

    /**
     * 为资料添加新版本
     */
    MaterialVersionResponse addMaterialVersion(Long materialId, MaterialVersionCreateRequest request);

    /**
     * 获取资料的所有版本
     */
    List<MaterialVersionResponse> getMaterialVersions(Long materialId);

    /**
     * 获取资料的最新版本
     */
    MaterialVersionResponse getLatestMaterialVersion(Long materialId);

    /**
     * 删除资料版本
     */
    void deleteMaterialVersion(Long versionId);
}

