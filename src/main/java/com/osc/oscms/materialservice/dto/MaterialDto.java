package com.osc.oscms.materialservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教学资料DTO
 */
public class MaterialDto {
    private Long id;
    
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    private String courseName;
    
    @Positive(message = "章节顺序必须大于0")
    private Integer chapterOrder;
    
    private List<Long> visibleClassIds;
    private String filename;
    private String url;
    
    @Size(max = 500, message = "资料描述长度不能超过500个字符")
    private String description;
    
    private Integer currentVersion;
    private List<MaterialVersionDto> versions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getChapterOrder() {
        return chapterOrder;
    }

    public void setChapterOrder(Integer chapterOrder) {
        this.chapterOrder = chapterOrder;
    }

    public List<Long> getVisibleClassIds() {
        return visibleClassIds;
    }

    public void setVisibleClassIds(List<Long> visibleClassIds) {
        this.visibleClassIds = visibleClassIds;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Integer currentVersion) {
        this.currentVersion = currentVersion;
    }

    public List<MaterialVersionDto> getVersions() {
        return versions;
    }

    public void setVersions(List<MaterialVersionDto> versions) {
        this.versions = versions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
