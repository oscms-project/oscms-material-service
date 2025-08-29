package com.osc.oscms.materialservice.controller;

import com.osc.oscms.common.response.ApiResponse;
import com.osc.oscms.common.dto.material.MaterialDto;
import com.osc.oscms.common.dto.material.MaterialVersionDto;
import com.osc.oscms.common.dto.material.MaterialUploadDto;
import com.osc.oscms.materialservice.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 教学资料管理控制器
 */
@RestController
@RequestMapping("/materials")
@Tag(name = "Material Management", description = "教学资料管理接口")
@RequiredArgsConstructor
@Validated
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping("/upload")
    @Operation(summary = "上传教学资料", description = "上传新的教学资料文件")
    public ApiResponse<MaterialDto> uploadMaterial(
            @RequestParam Long courseId,
            @RequestParam(required = false) Integer chapterOrder,
            @RequestParam(required = false) List<String> visibleClassIds,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String type,
            @RequestParam("file") MultipartFile file) {

        MaterialUploadDto uploadDto = new MaterialUploadDto();
        uploadDto.setFile(file);
        uploadDto.setChapterOrder(chapterOrder);
        uploadDto.setVisibleForClasses(visibleClassIds);
        uploadDto.setDescription(description);
        uploadDto.setType(type != null ? type : "DOCUMENT");

        MaterialDto result = materialService.uploadMaterial(courseId, uploadDto);
        return ApiResponse.ok(result);
    }

    @PutMapping("/{materialId}")
    @Operation(summary = "更新资料信息", description = "更新教学资料的基本信息")
    public ApiResponse<MaterialDto> updateMaterial(
            @PathVariable Long materialId,
            @Valid @RequestBody MaterialDto materialDto) {
        MaterialDto result = materialService.updateMaterial(materialId, materialDto);
        return ApiResponse.ok(result);
    }

    @PostMapping("/{materialId}/versions")
    @Operation(summary = "上传新版本", description = "为现有资料上传新版本")
    public ApiResponse<MaterialVersionDto> uploadNewVersion(
            @PathVariable Long materialId,
            @RequestParam(required = false) String description,
            @RequestParam("file") MultipartFile file) {
        MaterialVersionDto result = materialService.uploadNewVersion(materialId, description, file);
        return ApiResponse.ok(result);
    }

    @DeleteMapping("/{materialId}")
    @Operation(summary = "删除资料", description = "删除指定的教学资料")
    public ApiResponse<Void> deleteMaterial(@PathVariable Long materialId) {
        materialService.deleteMaterial(materialId);
        return ApiResponse.ok();
    }

    @DeleteMapping("/versions/{versionId}")
    @Operation(summary = "删除资料版本", description = "删除指定的资料版本")
    public ApiResponse<Void> deleteMaterialVersion(@PathVariable Long versionId) {
        materialService.deleteMaterialVersion(versionId);
        return ApiResponse.ok();
    }

    @GetMapping("/{materialId}")
    @Operation(summary = "获取资料详情", description = "根据资料ID获取资料的详细信息和版本历史")
    public ApiResponse<MaterialDto> getMaterialById(@PathVariable Long materialId) {
        MaterialDto material = materialService.getMaterialById(materialId);
        return ApiResponse.ok(material);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取课程资料列表", description = "根据课程ID获取该课程的所有教学资料")
    public ApiResponse<List<MaterialDto>> getMaterialsByCourse(@PathVariable Long courseId) {
        List<MaterialDto> materials = materialService.getMaterialsByCourseId(courseId);
        return ApiResponse.ok(materials);
    }

    @GetMapping("/course/{courseId}/class/{classId}")
    @Operation(summary = "获取班级可见资料", description = "获取指定班级可见的教学资料")
    public ApiResponse<List<MaterialDto>> getVisibleMaterials(
            @PathVariable Long courseId,
            @PathVariable Long classId) {
        List<MaterialDto> materials = materialService.getVisibleMaterialsByCourseAndClass(courseId, classId);
        return ApiResponse.ok(materials);
    }

    @GetMapping("/course/{courseId}/chapter/{chapterOrder}")
    @Operation(summary = "获取章节资料", description = "获取指定章节的教学资料")
    public ApiResponse<List<MaterialDto>> getMaterialsByChapter(
            @PathVariable Long courseId,
            @PathVariable Integer chapterOrder) {
        List<MaterialDto> materials = materialService.getMaterialsByChapter(courseId, chapterOrder);
        return ApiResponse.ok(materials);
    }

    @GetMapping("/{materialId}/versions")
    @Operation(summary = "获取资料版本历史", description = "获取指定资料的所有版本")
    public ApiResponse<List<MaterialVersionDto>> getMaterialVersions(@PathVariable Long materialId) {
        List<MaterialVersionDto> versions = materialService.getMaterialVersions(materialId);
        return ApiResponse.ok(versions);
    }

    @GetMapping("/{materialId}/versions/{version}")
    @Operation(summary = "获取指定版本", description = "获取资料的指定版本信息")
    public ApiResponse<MaterialVersionDto> getMaterialVersion(
            @PathVariable Long materialId,
            @PathVariable Integer version) {
        MaterialVersionDto materialVersion = materialService.getMaterialVersion(materialId, version);
        return ApiResponse.ok(materialVersion);
    }

    @PutMapping("/{materialId}/visibility")
    @Operation(summary = "设置资料可见性", description = "设置资料对哪些班级可见")
    public ApiResponse<Void> setMaterialVisibility(
            @PathVariable Long materialId,
            @RequestBody List<String> visibleClassIds) {
        materialService.setMaterialVisibility(materialId, visibleClassIds);
        return ApiResponse.ok();
    }

    @GetMapping("/{materialId}/download")
    @Operation(summary = "下载资料文件", description = "下载指定资料的最新版本或指定版本")
    public ResponseEntity<byte[]> downloadMaterial(
            @PathVariable Long materialId,
            @RequestParam(required = false) Integer version) {

        byte[] fileContent = materialService.downloadMaterial(materialId, version);

        // 获取文件信息用于设置响应头
        MaterialVersionDto materialVersion = version != null
                ? materialService.getMaterialVersion(materialId, version)
                : materialService.getMaterialVersions(materialId).get(0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", materialVersion.getFilename());
        headers.setContentLength(fileContent.length);

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
}
