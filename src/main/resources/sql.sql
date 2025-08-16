-- 创建 test_report 数据库
CREATE DATABASE IF NOT EXISTS test_report
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE test_report;

-- 创建 code_analysis 表
CREATE TABLE code_analysis (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID，主键自增',
                               file_name VARCHAR(255) NOT NULL COMMENT '文件名',
                               file_path VARCHAR(512) NOT NULL COMMENT '文件路径',
                               code_line INT NOT NULL COMMENT '代码行数',
                               issue_count INT NOT NULL COMMENT '问题数量',
                               issue_type VARCHAR(100) DEFAULT '' COMMENT '问题类型（空字符串表示无类型）',
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
                               CONSTRAINT uk_file_path UNIQUE (file_path) -- 确保文件路径唯一
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '代码分析记录表';

-- 创建索引以优化查询
CREATE INDEX idx_created_at ON code_analysis (created_at);
CREATE INDEX idx_issue_type ON code_analysis (issue_type);

-- 插入初始化数据（示例数据，模拟10条记录）
INSERT INTO code_analysis (file_name, file_path, code_line, issue_count, issue_type) VALUES
                                                                                         ('Main.java', '/src/main/java/com/example/Main.java', 150, 5, 'Code Smell'),
                                                                                         ('ServiceImpl.java', '/src/main/java/com/example/ServiceImpl.java', 300, 10, 'Bug'),
                                                                                         ('Controller.java', '/src/main/java/com/example/Controller.java', 200, 3, 'Code Smell'),
                                                                                         ('Util.java', '/src/main/java/com/example/Util.java', 100, 2, 'Vulnerability'),
                                                                                         ('Config.java', '/src/main/java/com/example/Config.java', 80, 1, 'Code Smell'),
                                                                                         ('Mapper.java', '/src/main/java/com/example/Mapper.java', 120, 4, 'Bug'),
                                                                                         ('Entity.java', '/src/main/java/com/example/Entity.java', 90, 0, ''),
                                                                                         ('Test.java', '/src/test/java/com/example/Test.java', 250, 6, 'Test Failure'),
                                                                                         ('Helper.java', '/src/main/java/com/example/Helper.java', 180, 3, 'Code Smell'),
                                                                                         ('Api.java', '/src/main/java/com/example/Api.java', 220, 7, 'Bug');