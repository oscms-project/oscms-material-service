package com.osc.oscms.material.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.osc.oscms.common.response.ApiResponse;
import com.osc.oscms.material.dto.*;
import com.osc.oscms.material.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资料管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@Tag(name = "资料管理", description = "资料的增删改查操作")
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping
    @Operation(summary = "创建资料", description = "创建新的资料")
    public ResponseEntity<ApiResponse<MaterialResponse>> createMaterial(
            @Valid @RequestBody MaterialCreateRequest request) {
        log.info("Creating material: {}", request.getFilename());
        MaterialResponse response = materialService.createMaterial(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取资料详情", description = "根据ID获取资料详细信息")
    public ResponseEntity<ApiResponse<MaterialResponse>> getMaterialById(
            @Parameter(description = "资料ID") @PathVariable Long id) {
        MaterialResponse response = materialService.getMaterialById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    @Operation(summary = "分页查询资料", description = "分页获取资料列表")
    public ResponseEntity<ApiResponse<IPage<MaterialResponse>>> getMaterials(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "资料类型") @RequestParam(required = false) String type) {

        Page<MaterialResponse> page = new Page<>(current, size);
        IPage<MaterialResponse> result = materialService.getMaterials(page, courseId, classId, type);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取课程资料列表", description = "根据课程ID获取资料列表")
    public ResponseEntity<ApiResponse<List<MaterialResponse>>> getMaterialsByCourse(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        List<MaterialResponse> result = materialService.getMaterialsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/course/{courseId}/class/{classId}")
    @Operation(summary = "获取课程班级可见资料", description = "根据课程ID和班级ID获取可见的资料列表")
    public ResponseEntity<ApiResponse<List<MaterialResponse>>> getMaterialsByCourseAndClass(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Parameter(description = "班级ID") @PathVariable Long classId) {
        List<MaterialResponse> result = materialService.getMaterialsByCourseAndClass(courseId, classId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新资料", description = "更新资料信息")
    public ResponseEntity<ApiResponse<MaterialResponse>> updateMaterial(
            @Parameter(description = "资料ID") @PathVariable Long id,
            @Valid @RequestBody MaterialUpdateRequest request) {
        log.info("Updating material: {}", id);
        MaterialResponse response = materialService.updateMaterial(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除资料", description = "删除资料")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(
            @Parameter(description = "资料ID") @PathVariable Long id) {
        log.info("Deleting material: {}", id);
        materialService.deleteMaterial(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // === 资料版本管理 ===

    @PostMapping("/{materialId}/versions")
    @Operation(summary = "添加资料版本", description = "为资料添加新版本")
    public ResponseEntity<ApiResponse<MaterialVersionResponse>> addMaterialVersion(
            @Parameter(description = "资料ID") @PathVariable Long materialId,
            @Valid @RequestBody MaterialVersionCreateRequest request) {
        log.info("Adding version to material: {}", materialId);
        MaterialVersionResponse response = materialService.addMaterialVersion(materialId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{materialId}/versions")
    @Operation(summary = "获取资料版本列表", description = "获取资料的所有版本")
    public ResponseEntity<ApiResponse<List<MaterialVersionResponse>>> getMaterialVersions(
            @Parameter(description = "资料ID") @PathVariable Long materialId) {
        List<MaterialVersionResponse> result = materialService.getMaterialVersions(materialId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{materialId}/versions/latest")
    @Operation(summary = "获取最新版本", description = "获取资料的最新版本")
    public ResponseEntity<ApiResponse<MaterialVersionResponse>> getLatestMaterialVersion(
            @Parameter(description = "资料ID") @PathVariable Long materialId) {
        MaterialVersionResponse response = materialService.getLatestMaterialVersion(materialId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/versions/{versionId}")
    @Operation(summary = "删除资料版本", description = "删除指定的资料版本")
    public ResponseEntity<ApiResponse<Void>> deleteMaterialVersion(
            @Parameter(description = "版本ID") @PathVariable Long versionId) {
        log.info("Deleting material version: {}", versionId);
        materialService.deleteMaterialVersion(versionId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}

