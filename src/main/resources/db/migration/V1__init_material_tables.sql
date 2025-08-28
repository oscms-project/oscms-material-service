-- 资料服务数据库初始化脚本
-- 包含：材料表、材料版本表

-- 资料主表：一个资源组（不同版本共享同一个 material_id）
CREATE TABLE osc_material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL COMMENT '所属课程ID',
    chapter_order INT NOT NULL COMMENT '章节顺序',
    visible_classes JSON NULL COMMENT '可见班级ID列表，JSON格式',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_course_id (course_id),
    INDEX idx_chapter_order (chapter_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学资料主表';

-- 资料版本表：每次上传/更新插入新版本
CREATE TABLE osc_material_version (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_id BIGINT NOT NULL COMMENT '关联的资料ID',
    version INT NOT NULL COMMENT '版本号',
    filename VARCHAR(255) NOT NULL COMMENT '文件名',
    url VARCHAR(1024) NOT NULL COMMENT '文件URL或路径',
    description TEXT NULL COMMENT '版本描述',
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    
    CONSTRAINT fk_mv_material FOREIGN KEY (material_id) REFERENCES osc_material(id) ON DELETE CASCADE,
    UNIQUE KEY uq_material_version (material_id, version),
    INDEX idx_material_id (material_id),
    INDEX idx_uploaded_at (uploaded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学资料版本表';
