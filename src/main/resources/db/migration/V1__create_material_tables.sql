-- 资料主表：一个资源组（不同版本共享同一个 material_id）
CREATE TABLE osc_material (
                              id                 BIGINT       PRIMARY KEY AUTO_INCREMENT,
                              course_id          BIGINT       NOT NULL COMMENT '所属课程ID (逻辑外键)',
                              chapter_order      INT          NOT NULL,
                              filename           VARCHAR(255) NOT NULL COMMENT '资料组的通用/初始文件名',
                              type               VARCHAR(50)  NOT NULL DEFAULT 'general' COMMENT '资料的业务类型 (pdf, ppt, etc.)',
                              visible_classes    JSON         NULL COMMENT '可见班级ID列表 (JSON数组)',
                              created_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 资料版本表：每次上传/更新插入新版本
CREATE TABLE osc_material_version (
                                      id               BIGINT        PRIMARY KEY AUTO_INCREMENT,
                                      material_id      BIGINT        NOT NULL,
                                      version          INT           NOT NULL,
                                      filename         VARCHAR(255)  NOT NULL,
                                      url              VARCHAR(1024) NOT NULL,
                                      description      TEXT          NULL,
                                      uploaded_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      CONSTRAINT fk_mv_material FOREIGN KEY (material_id)
                                          REFERENCES osc_material(id)
                                          ON DELETE CASCADE,
                                      UNIQUE KEY uq_material_version (material_id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;