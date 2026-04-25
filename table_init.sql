-- 1. 用户表
CREATE TABLE sys_user
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码',
    real_name   VARCHAR(50) COMMENT '真实姓名',
    email       VARCHAR(100) COMMENT '邮箱',
    phone       VARCHAR(20) COMMENT '手机号',
    status      TINYINT  DEFAULT 1 COMMENT '状态：1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag    TINYINT  DEFAULT 0 COMMENT '删除标志：0未删除 1已删除'
) COMMENT '用户表';

-- 2. 角色表
CREATE TABLE sys_role
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code   VARCHAR(50) NOT NULL UNIQUE COMMENT '角色代码：ADMIN管理员 MANAGER项目经理 EMPLOYEE普通员工',
    role_name   VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '角色表';

-- 3. 用户角色关联表
CREATE TABLE sys_user_role
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID'
) COMMENT '用户角色关联表';

-- 4. 项目表
CREATE TABLE project
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '项目ID',
    project_code VARCHAR(50)  NOT NULL UNIQUE COMMENT '项目编码',
    project_name VARCHAR(100) NOT NULL COMMENT '项目名称',
    description  VARCHAR(500) COMMENT '项目描述',
    start_date   DATE COMMENT '开始日期',
    end_date     DATE COMMENT '结束日期',
    status       TINYINT  DEFAULT 1 COMMENT '项目状态：1进行中 2已完成 3已暂停',
    manager_id   BIGINT COMMENT '项目经理用户ID',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag     TINYINT  DEFAULT 0 COMMENT '删除标志：0未删除 1已删除'
) COMMENT '项目表';

-- 5. 项目成员表
CREATE TABLE project_member
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    project_id  BIGINT NOT NULL COMMENT '项目ID',
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    join_date   DATE COMMENT '加入日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '项目成员表';

-- 6. 任务表
CREATE TABLE task
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    project_id  BIGINT       NOT NULL COMMENT '所属项目ID',
    task_name   VARCHAR(100) NOT NULL COMMENT '任务名称',
    description VARCHAR(500) COMMENT '任务描述',
    assignee_id BIGINT COMMENT '任务指派人用户ID',
    status      TINYINT  DEFAULT 1 COMMENT '任务状态：1待处理 2进行中 3已完成',
    plan_hours  DECIMAL(10, 2) COMMENT '计划工时',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag    TINYINT  DEFAULT 0 COMMENT '删除标志：0未删除 1已删除'
) COMMENT '任务表';

-- 7. 工时记录表
CREATE TABLE work_record
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '工时记录ID',
    user_id          BIGINT         NOT NULL COMMENT '填报人用户ID',
    project_id       BIGINT COMMENT '关联项目ID',
    task_id          BIGINT COMMENT '关联任务ID',
    work_date        DATE           NOT NULL COMMENT '工作日期',
    work_type        TINYINT        NOT NULL COMMENT '工时类型：1日报 2周报',
    hours            DECIMAL(10, 2) NOT NULL COMMENT '填报工时数',
    work_content     TEXT           NOT NULL COMMENT '工作内容',
    status           TINYINT  DEFAULT 1 COMMENT '审批状态：1待审批 2已通过 3已驳回',
    submit_time      DATETIME COMMENT '提交时间',
    approval_time    DATETIME COMMENT '审批时间',
    approver_id      BIGINT COMMENT '审批人用户ID',
    approval_comment VARCHAR(500) COMMENT '审批意见',
    create_time      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag         TINYINT  DEFAULT 0 COMMENT '删除标志：0未删除 1已删除'
) COMMENT '工时记录表';

-- 8. 审批人配置表
CREATE TABLE approval_config
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id     BIGINT NOT NULL COMMENT '用户ID（被审批人）',
    approver_id BIGINT NOT NULL COMMENT '审批人用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '审批人配置表';

-- 9. 系统配置表
CREATE TABLE sys_config
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key   VARCHAR(50) NOT NULL UNIQUE COMMENT '配置键',
    config_value VARCHAR(255) COMMENT '配置值',
    description  VARCHAR(255) COMMENT '配置描述',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '系统配置表';
