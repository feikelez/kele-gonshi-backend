# 工时管理系统后端

基于 Spring Boot 3 + MyBatis Plus 的企业工时统计与管理平台，提供用户管理、项目管理、任务分配、工时上报审批、数据统计及 Excel 导出等功能。

## 功能特性

### 用户与权限
- 用户注册、登录、JWT 认证
- 三种角色：管理员 (ADMIN)、项目经理 (MANAGER)、普通员工 (EMPLOYEE)
- RBAC 权限控制，角色路由拦截

### 项目管理
- 项目 CRUD、成员管理
- 项目状态跟踪（进行中/已完成/已暂停）

### 任务管理
- 任务创建、分配、状态跟踪
- 关联项目、指派人员

### 工时管理
- 日报/周报填报
- 审批流程（待审批/已通过/已驳回）
- 按项目/按周统计数据
- **Excel 导出**工时记录

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2.6 | 后端框架 |
| Java | 21 | 编程语言 |
| MyBatis Plus | 3.5.7 | ORM 框架 |
| MySQL | 8.0 | 数据库 |
| JWT | 0.11.5 | 身份认证 |
| EasyExcel | 3.3.4 | Excel 导出 |
| Knife4j | 4.4.0 | API 文档 |

## 项目结构

```
src/main/java/com/kele/gongshibackend/
├── annotation/          # 自定义注解 (@RequireRole, @IgnoreAuth)
├── common/              # 通用响应 Result 类
├── config/              # 配置类 (JWT, MyBatis Plus, CORS 等)
├── controller/          # REST API 控制器
├── dto/                 # 数据传输对象
├── entity/              # 数据库实体类
├── exception/           # 异常处理
├── interceptor/         # 拦截器 (认证、权限)
├── mapper/              # MyBatis Mapper 接口
├── service/             # 业务逻辑接口
├── util/                # 工具类
└── vo/                  # 视图对象
```

## 快速开始

### 环境要求

- JDK 21+
- MySQL 8.0+
- Maven 3.8+

### 1. 克隆项目

```bash
git clone https://github.com/feikelez/kele-gonshi-backend.git
cd kele-gonshi-backend
```

### 2. 创建数据库

```sql
CREATE DATABASE my-gongshi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 修改配置

编辑 `src/main/resources/application.yaml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/my-gongshi?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 4. 初始化表结构

执行数据库脚本：

```bash
mysql -u your_username -p my-gongshi < table_init.sql
mysql -u your_username -p my-gongshi < src/main/resources/sql/auth_tables.sql
```

### 5. 启动项目

```bash
mvn spring-boot:run
```

服务启动后访问：`http://localhost:3102`

## API 文档

启动项目后访问 Knife4j 文档页面：

- **Swagger UI**: http://localhost:3102/doc.html
- ** Knife4j**: http://localhost:3102/doc.html

### 认证接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/register` | POST | 用户注册 |
| `/api/auth/logout` | POST | 退出登录 |

### 主要业务接口

| 模块 | 接口路径 | 说明 |
|------|---------|------|
| 用户 | `/api/user` | 用户管理 |
| 角色 | `/api/role` | 角色管理 |
| 用户角色 | `/api/userRole` | 角色分配 |
| 项目 | `/api/project` | 项目管理 |
| 项目成员 | `/api/projectMember` | 成员管理 |
| 任务 | `/api/task` | 任务管理 |
| 工时记录 | `/api/work-record` | 工时管理 |
| 审批配置 | `/api/approval-config` | 审批人配置 |

## 数据库表结构

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户表 |
| `sys_role` | 角色表 |
| `sys_user_role` | 用户角色关联表 |
| `sys_token_blacklist` | Token 黑名单表 |
| `project` | 项目表 |
| `project_member` | 项目成员表 |
| `task` | 任务表 |
| `work_record` | 工时记录表 |
| `approval_config` | 审批人配置表 |
| `sys_config` | 系统配置表 |

详细表结构请参考 `table_init.sql` 文件。

## License

本项目采用 MIT License。
