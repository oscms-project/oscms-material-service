package com.osc.oscms.materialservice.service;

import com.osc.oscms.common.dto.material.MaterialDto;
import com.osc.oscms.common.dto.material.MaterialVersionDto;
import com.osc.oscms.common.dto.material.MaterialUploadDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 教学资料服务接口
 */
public interface MaterialService {

    /**
     * 上传新资料
     */
    MaterialDto uploadMaterial(Long courseId, MaterialUploadDto uploadDto);

    /**
     * 更新资料信息（不包括文件）
     */
    MaterialDto updateMaterial(Long materialId, MaterialDto materialDto);

    /**
     * 上传资料新版本
     */
    MaterialVersionDto uploadNewVersion(Long materialId, String description, MultipartFile file);

    /**
     * 删除资料
     */
    void deleteMaterial(Long materialId);

    /**
     * 删除资料版本
     */
    void deleteMaterialVersion(Long versionId);

    /**
     * 根据ID获取资料详情
     */
    MaterialDto getMaterialById(Long materialId);

    /**
     * 根据课程ID获取资料列表
     */
    List<MaterialDto> getMaterialsByCourseId(Long courseId);

    /**
     * 根据课程ID和班级ID获取可见资料
     */
    List<MaterialDto> getVisibleMaterialsByCourseAndClass(Long courseId, Long classId);

    /**
     * 根据章节获取资料
     */
    List<MaterialDto> getMaterialsByChapter(Long courseId, Integer chapterOrder);

    /**
     * 获取资料版本历史
     */
    List<MaterialVersionDto> getMaterialVersions(Long materialId);

    /**
     * 获取指定版本的资料
     */
    MaterialVersionDto getMaterialVersion(Long materialId, Integer version);

    /**
     * 设置资料可见班级
     */
    void setMaterialVisibility(Long materialId, List<String> visibleClassIds);

    /**
     * 下载资料文件
     */
    byte[] downloadMaterial(Long materialId, Integer version);
}
