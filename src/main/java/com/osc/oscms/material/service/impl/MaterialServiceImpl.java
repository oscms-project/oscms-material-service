package com.osc.oscms.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osc.oscms.common.exception.MaterialException.MaterialNotFoundException;
import com.osc.oscms.common.exception.MaterialException.MaterialVersionNotFoundException;
import com.osc.oscms.material.domain.Material;
import com.osc.oscms.material.domain.MaterialVersion;
import com.osc.oscms.material.dto.*;
import com.osc.oscms.material.mapper.MaterialMapper;
import com.osc.oscms.material.mapper.MaterialVersionMapper;
import com.osc.oscms.material.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资料服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialMapper materialMapper;
    private final MaterialVersionMapper materialVersionMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public MaterialResponse createMaterial(MaterialCreateRequest request) {
        log.info("Creating material: {}", request.getFilename());

        // 创建资料主记录
        Material material = new Material();
        BeanUtils.copyProperties(request, material);

        // 处理可见班级列表
        if (request.getVisibleClasses() != null && !request.getVisibleClasses().isEmpty()) {
            try {
                material.setVisibleClasses(objectMapper.writeValueAsString(request.getVisibleClasses()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize visible classes", e);
                material.setVisibleClasses(null);
            }
        }

        materialMapper.insert(material);

        // 创建第一个版本
        MaterialVersion version = new MaterialVersion();
        version.setMaterialId(material.getId());
        version.setVersion(1);
        version.setFilename(request.getFilename());
        version.setUrl(request.getUrl());
        version.setDescription(request.getDescription());

        materialVersionMapper.insert(version);

        return getMaterialById(material.getId());
    }

    @Override
    public MaterialResponse getMaterialById(Long id) {
        Material material = materialMapper.selectById(id);
        if (material == null) {
            throw new MaterialNotFoundException("资料不存在，ID: " + id);
        }

        return convertToResponse(material);
    }

    @Override
    public IPage<MaterialResponse> getMaterials(Page<MaterialResponse> page, Long courseId, Long classId, String type) {
        LambdaQueryWrapper<Material> queryWrapper = new LambdaQueryWrapper<>();

        if (courseId != null) {
            queryWrapper.eq(Material::getCourseId, courseId);
        }
        if (StringUtils.hasText(type)) {
            queryWrapper.eq(Material::getType, type);
        }

        queryWrapper.orderByAsc(Material::getChapterOrder)
                .orderByDesc(Material::getCreatedAt);

        IPage<Material> materialPage = materialMapper.selectPage(
                new Page<>(page.getCurrent(), page.getSize()), queryWrapper);

        return materialPage.convert(this::convertToResponse);
    }

    @Override
    public List<MaterialResponse> getMaterialsByCourse(Long courseId) {
        List<Material> materials = materialMapper.selectByCourseId(courseId);
        return materials.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaterialResponse> getMaterialsByCourseAndClass(Long courseId, Long classId) {
        List<Material> materials = materialMapper.selectByCourseIdAndClassId(courseId, classId);
        return materials.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MaterialResponse updateMaterial(Long id, MaterialUpdateRequest request) {
        log.info("Updating material with ID: {}", id);

        Material existingMaterial = materialMapper.selectById(id);
        if (existingMaterial == null) {
            throw new MaterialNotFoundException("资料不存在，ID: " + id);
        }

        if (request.getChapterOrder() != null) {
            existingMaterial.setChapterOrder(request.getChapterOrder());
        }
        if (StringUtils.hasText(request.getFilename())) {
            existingMaterial.setFilename(request.getFilename());
        }
        if (StringUtils.hasText(request.getType())) {
            existingMaterial.setType(request.getType());
        }
        if (request.getVisibleClasses() != null) {
            try {
                existingMaterial.setVisibleClasses(objectMapper.writeValueAsString(request.getVisibleClasses()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize visible classes", e);
            }
        }

        materialMapper.updateById(existingMaterial);
        return convertToResponse(existingMaterial);
    }

    @Override
    @Transactional
    public void deleteMaterial(Long id) {
        log.info("Deleting material with ID: {}", id);

        Material material = materialMapper.selectById(id);
        if (material == null) {
            throw new MaterialNotFoundException("资料不存在，ID: " + id);
        }

        // 删除所有版本
        LambdaQueryWrapper<MaterialVersion> versionWrapper = new LambdaQueryWrapper<>();
        versionWrapper.eq(MaterialVersion::getMaterialId, id);
        materialVersionMapper.delete(versionWrapper);

        // 删除主记录
        materialMapper.deleteById(id);
    }

    @Override
    public MaterialVersionResponse addMaterialVersion(Long materialId, MaterialVersionCreateRequest request) {
        log.info("Adding new version to material: {}", materialId);

        // 检查资料是否存在
        Material material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new MaterialNotFoundException("资料不存在，ID: " + materialId);
        }

        // 获取下一个版本号
        Integer nextVersion = materialVersionMapper.getNextVersion(materialId);

        // 创建新版本
        MaterialVersion version = new MaterialVersion();
        version.setMaterialId(materialId);
        version.setVersion(nextVersion);
        version.setFilename(request.getFilename());
        version.setUrl(request.getUrl());
        version.setDescription(request.getDescription());

        materialVersionMapper.insert(version);

        return convertVersionToResponse(version);
    }

    @Override
    public List<MaterialVersionResponse> getMaterialVersions(Long materialId) {
        List<MaterialVersion> versions = materialVersionMapper.selectByMaterialId(materialId);
        return versions.stream()
                .map(this::convertVersionToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MaterialVersionResponse getLatestMaterialVersion(Long materialId) {
        MaterialVersion version = materialVersionMapper.selectLatestByMaterialId(materialId);
        if (version == null) {
            throw new MaterialVersionNotFoundException("该资料没有版本记录，ID: " + materialId);
        }
        return convertVersionToResponse(version);
    }

    @Override
    public void deleteMaterialVersion(Long versionId) {
        log.info("Deleting material version with ID: {}", versionId);

        MaterialVersion version = materialVersionMapper.selectById(versionId);
        if (version == null) {
            throw new MaterialVersionNotFoundException("资料版本不存在，ID: " + versionId);
        }

        materialVersionMapper.deleteById(versionId);
    }

    /**
     * 转换为响应DTO
     */
    private MaterialResponse convertToResponse(Material material) {
        MaterialResponse response = new MaterialResponse();
        BeanUtils.copyProperties(material, response);

        // 解析可见班级列表
        if (StringUtils.hasText(material.getVisibleClasses())) {
            try {
                List<Long> visibleClasses = objectMapper.readValue(
                        material.getVisibleClasses(), new TypeReference<List<Long>>() {
                        });
                response.setVisibleClasses(visibleClasses);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize visible classes for material {}", material.getId(), e);
                response.setVisibleClasses(Collections.emptyList());
            }
        }

        // 获取版本信息
        List<MaterialVersion> versions = materialVersionMapper.selectByMaterialId(material.getId());
        response.setVersions(versions.stream()
                .map(this::convertVersionToResponse)
                .collect(Collectors.toList()));

        // 设置最新版本
        if (!versions.isEmpty()) {
            response.setLatestVersion(convertVersionToResponse(versions.get(0)));
        }

        return response;
    }

    /**
     * 转换版本为响应DTO
     */
    private MaterialVersionResponse convertVersionToResponse(MaterialVersion version) {
        MaterialVersionResponse response = new MaterialVersionResponse();
        BeanUtils.copyProperties(version, response);
        return response;
    }
}

