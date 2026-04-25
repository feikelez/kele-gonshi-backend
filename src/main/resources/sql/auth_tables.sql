-- ====================================
-- 登录认证功能相关数据库变更脚本
-- ====================================

-- 1. 确保 password 字段长度足够（BCrypt 哈希为 60 字符）
ALTER TABLE sys_user MODIFY COLUMN password VARCHAR(255) NOT NULL COMMENT '密码';

-- 2. 新增 Token 黑名单表
CREATE TABLE IF NOT EXISTS sys_token_blacklist
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    token       VARCHAR(500) NOT NULL UNIQUE COMMENT 'Token字符串',
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Token黑名单表';

-- 3. 初始化默认角色数据（如果不存在）
INSERT INTO sys_role (role_code, role_name, description) 
SELECT 'ADMIN', '系统管理员', '拥有所有权限'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'ADMIN');

INSERT INTO sys_role (role_code, role_name, description) 
SELECT 'MANAGER', '项目经理', '项目管理权限'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'MANAGER');

INSERT INTO sys_role (role_code, role_name, description) 
SELECT 'EMPLOYEE', '普通员工', '基础操作权限'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'EMPLOYEE');

-- 4. 查看当前用户表结构（可选，用于调试）
-- DESCRIBE sys_user;
-- SELECT * FROM sys_role;
