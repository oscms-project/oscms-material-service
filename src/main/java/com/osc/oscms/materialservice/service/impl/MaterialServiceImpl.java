package com.osc.oscms.materialservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osc.oscms.materialservice.client.CourseServiceClient;
import com.osc.oscms.materialservice.domain.Material;
import com.osc.oscms.materialservice.domain.MaterialVersion;
import com.osc.oscms.materialservice.dto.MaterialDto;
import com.osc.oscms.materialservice.dto.MaterialVersionDto;
import com.osc.oscms.materialservice.repository.MaterialRepository;
import com.osc.oscms.materialservice.repository.MaterialVersionRepository;
import com.osc.oscms.materialservice.service.MaterialService;
import com.osc.oscms.common.exception.BusinessException;
import com.osc.oscms.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 教学资料服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {
    
    private final MaterialRepository materialRepository;
    private final MaterialVersionRepository materialVersionRepository;
    private final CourseServiceClient courseServiceClient;
    private final ObjectMapper objectMapper;
    
    // 文件存储路径配置
    private static final String UPLOAD_DIR = "uploads/materials/";
    
    @Override
    @Transactional
    public MaterialDto uploadMaterial(Long courseId, Integer chapterOrder, 
                                    List<Long> visibleClassIds, String description,
                                    MultipartFile file) {
        log.info("Uploading material for course: {}, chapter: {}", courseId, chapterOrder);
        
        // 验证课程是否存在
        validateCourseExists(courseId);
        
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        
        // 保存文件
        String fileName = saveFile(file);
        
        // 创建资料记录
        Material material = new Material();
        material.setCourseId(courseId);
        material.setChapterOrder(chapterOrder != null ? chapterOrder : getNextChapterOrder(courseId));
        material.setVisibleClasses(convertClassIdsToJson(visibleClassIds));
        material.setCreatedAt(LocalDateTime.now());
        material.setUpdatedAt(LocalDateTime.now());
        
        materialRepository.insert(material);
        
        // 创建第一个版本
        MaterialVersion version = new MaterialVersion();
        version.setMaterialId(material.getId());
        version.setVersion(1);
        version.setFilename(file.getOriginalFilename());
        version.setUrl(fileName);
        version.setDescription(description);
        version.setUploadedAt(LocalDateTime.now());
        
        materialVersionRepository.insert(version);
        
        return convertToDto(material);
    }
    
    @Override
    @Transactional
    public MaterialDto updateMaterial(Long materialId, MaterialDto materialDto) {
        log.info("Updating material: {}", materialId);
        
        Material material = getMaterialByIdOrThrow(materialId);
        
        // 更新字段
        if (materialDto.getChapterOrder() != null) {
            material.setChapterOrder(materialDto.getChapterOrder());
        }
        if (materialDto.getVisibleClassIds() != null) {
            material.setVisibleClasses(convertClassIdsToJson(materialDto.getVisibleClassIds()));
        }
        
        material.setUpdatedAt(LocalDateTime.now());
        
        materialRepository.updateById(material);
        
        return convertToDto(material);
    }
    
    @Override
    @Transactional
    public MaterialVersionDto uploadNewVersion(Long materialId, String description, MultipartFile file) {
        log.info("Uploading new version for material: {}", materialId);
        
        Material material = getMaterialByIdOrThrow(materialId);
        
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        
        // 保存文件
        String fileName = saveFile(file);
        
        // 获取下一个版本号
        Integer nextVersion = materialVersionRepository.getMaxVersionByMaterialId(materialId) + 1;
        
        // 创建新版本
        MaterialVersion version = new MaterialVersion();
        version.setMaterialId(materialId);
        version.setVersion(nextVersion);
        version.setFilename(file.getOriginalFilename());
        version.setUrl(fileName);
        version.setDescription(description);
        version.setUploadedAt(LocalDateTime.now());
        
        materialVersionRepository.insert(version);
        
        // 更新资料的更新时间
        material.setUpdatedAt(LocalDateTime.now());
        materialRepository.updateById(material);
        
        return convertVersionToDto(version);
    }
    
    @Override
    @Transactional
    public void deleteMaterial(Long materialId) {
        log.info("Deleting material: {}", materialId);
        
        Material material = getMaterialByIdOrThrow(materialId);
        
        // 删除所有版本的文件
        List<MaterialVersion> versions = materialVersionRepository.findByMaterialId(materialId);
        for (MaterialVersion version : versions) {
            deleteFile(version.getUrl());
        }
        
        // 删除版本记录
        materialVersionRepository.deleteByMaterialId(materialId);
        
        // 删除资料记录
        materialRepository.deleteById(materialId);
    }
    
    @Override
    @Transactional
    public void deleteMaterialVersion(Long versionId) {
        log.info("Deleting material version: {}", versionId);
        
        MaterialVersion version = materialVersionRepository.selectById(versionId);
        if (version == null) {
            throw new BusinessException("版本不存在");
        }
        
        // 检查是否是唯一版本
        List<MaterialVersion> versions = materialVersionRepository.findByMaterialId(version.getMaterialId());
        if (versions.size() <= 1) {
            throw new BusinessException("不能删除最后一个版本");
        }
        
        // 删除文件
        deleteFile(version.getUrl());
        
        // 删除版本记录
        materialVersionRepository.deleteById(versionId);
    }
    
    @Override
    public MaterialDto getMaterialById(Long materialId) {
        Material material = getMaterialByIdOrThrow(materialId);
        MaterialDto dto = convertToDto(material);
        
        // 加载版本信息
        List<MaterialVersion> versions = materialVersionRepository.findByMaterialId(materialId);
        List<MaterialVersionDto> versionDtos = versions.stream()
                .map(this::convertVersionToDto)
                .collect(Collectors.toList());
        dto.setVersions(versionDtos);
        
        // 设置当前版本和最新版本信息
        if (!versions.isEmpty()) {
            MaterialVersion latestVersion = versions.get(0); // 按版本降序排列
            dto.setCurrentVersion(latestVersion.getVersion());
            dto.setFilename(latestVersion.getFilename());
            dto.setUrl(latestVersion.getUrl());
            dto.setDescription(latestVersion.getDescription());
        }
        
        return dto;
    }
    
    @Override
    public List<MaterialDto> getMaterialsByCourseId(Long courseId) {
        log.info("Getting materials for course: {}", courseId);
        
        List<Material> materials = materialRepository.findByCourseId(courseId);
        return materials.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MaterialDto> getVisibleMaterialsByCourseAndClass(Long courseId, Long classId) {
        log.info("Getting visible materials for course: {}, class: {}", courseId, classId);
        
        List<Material> materials = materialRepository.findVisibleMaterialsByCourseAndClass(courseId, classId);
        return materials.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MaterialDto> getMaterialsByChapter(Long courseId, Integer chapterOrder) {
        log.info("Getting materials for course: {}, chapter: {}", courseId, chapterOrder);
        
        List<Material> materials = materialRepository.findByCourseIdAndChapterOrder(courseId, chapterOrder);
        return materials.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MaterialVersionDto> getMaterialVersions(Long materialId) {
        log.info("Getting versions for material: {}", materialId);
        
        getMaterialByIdOrThrow(materialId); // 验证资料存在
        
        List<MaterialVersion> versions = materialVersionRepository.findByMaterialId(materialId);
        return versions.stream()
                .map(this::convertVersionToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public MaterialVersionDto getMaterialVersion(Long materialId, Integer version) {
        log.info("Getting material: {}, version: {}", materialId, version);
        
        Optional<MaterialVersion> materialVersion = materialVersionRepository
                .findByMaterialIdAndVersion(materialId, version);
        
        if (materialVersion.isEmpty()) {
            throw new BusinessException("指定版本不存在");
        }
        
        return convertVersionToDto(materialVersion.get());
    }
    
    @Override
    @Transactional
    public void setMaterialVisibility(Long materialId, List<Long> visibleClassIds) {
        log.info("Setting visibility for material: {}", materialId);
        
        Material material = getMaterialByIdOrThrow(materialId);
        material.setVisibleClasses(convertClassIdsToJson(visibleClassIds));
        material.setUpdatedAt(LocalDateTime.now());
        
        materialRepository.updateById(material);
    }
    
    @Override
    public byte[] downloadMaterial(Long materialId, Integer version) {
        log.info("Downloading material: {}, version: {}", materialId, version);
        
        MaterialVersion materialVersion;
        if (version != null) {
            Optional<MaterialVersion> versionOpt = materialVersionRepository
                    .findByMaterialIdAndVersion(materialId, version);
            if (versionOpt.isEmpty()) {
                throw new BusinessException("指定版本不存在");
            }
            materialVersion = versionOpt.get();
        } else {
            Optional<MaterialVersion> latestOpt = materialVersionRepository
                    .findLatestVersionByMaterialId(materialId);
            if (latestOpt.isEmpty()) {
                throw new BusinessException("资料文件不存在");
            }
            materialVersion = latestOpt.get();
        }
        
        return readFile(materialVersion.getUrl());
    }
    
    private Material getMaterialByIdOrThrow(Long materialId) {
        Material material = materialRepository.selectById(materialId);
        if (material == null) {
            throw new BusinessException("资料不存在");
        }
        return material;
    }
    
    private void validateCourseExists(Long courseId) {
        try {
            ApiResponse<Map<String, Object>> response = courseServiceClient.getCourseById(courseId);
            if (!response.isSuccess() || response.getData() == null) {
                throw new BusinessException("课程不存在");
            }
        } catch (Exception e) {
            log.error("Failed to validate course existence: {}", courseId, e);
            throw new BusinessException("无法验证课程信息");
        }
    }
    
    private Integer getNextChapterOrder(Long courseId) {
        return materialRepository.getMaxChapterOrderByCourseId(courseId) + 1;
    }
    
    private String convertClassIdsToJson(List<Long> classIds) {
        if (classIds == null || classIds.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(classIds);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert class IDs to JSON", e);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<Long> convertJsonToClassIds(String json) {
        if (json == null || json.trim().isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, List.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert JSON to class IDs", e);
            return List.of();
        }
    }
    
    private String saveFile(MultipartFile file) {
        try {
            // 创建上传目录
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            return fileName;
        } catch (IOException e) {
            log.error("Failed to save file", e);
            throw new BusinessException("文件保存失败");
        }
    }
    
    private void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileName, e);
        }
    }
    
    private byte[] readFile(String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to read file: {}", fileName, e);
            throw new BusinessException("文件读取失败");
        }
    }
    
    private MaterialDto convertToDto(Material material) {
        MaterialDto dto = new MaterialDto();
        BeanUtils.copyProperties(material, dto);
        
        // 转换可见班级ID
        dto.setVisibleClassIds(convertJsonToClassIds(material.getVisibleClasses()));
        
        // 尝试获取课程名称
        try {
            ApiResponse<Map<String, Object>> courseResponse = courseServiceClient.getCourseById(material.getCourseId());
            if (courseResponse.isSuccess() && courseResponse.getData() != null) {
                dto.setCourseName((String) courseResponse.getData().get("name"));
            }
        } catch (Exception e) {
            log.warn("Failed to get course info for material: {}", material.getId(), e);
        }
        
        return dto;
    }
    
    private MaterialVersionDto convertVersionToDto(MaterialVersion version) {
        MaterialVersionDto dto = new MaterialVersionDto();
        BeanUtils.copyProperties(version, dto);
        return dto;
    }
}