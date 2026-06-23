# XiaoMaYi-EleVue 资源分析（阶段1）

> 勘察对象：`XiaoMaYi-EleVue-v1.1.0/`
> 重要结论：**该目录仅含 Java 后端模块，不含 Vue 前端源码**（无 `package.json` / `.vue` / `vite.config`）。
> 因此「复用现成前端组件」不可行；下文先说明现存可参考资源，再给出**按 XiaoMaYi 架构新建前端**的组件规划（已与用户确认采用此方案）。

---

## 一、目录实际构成

```
XiaoMaYi-EleVue-v1.1.0/
├── pom.xml                       # Maven 聚合工程（纯后端）
├── README.md                     # 框架介绍：Vue3+TS+Vite+ElementPlus 前后端分离
├── xiaomayi-common/              # 公共模块
├── xiaomayi-modules/
│   ├── xiaomayi-admin/           # 管理端后端（Java，大量 Controller 可作 RBAC 参考）
│   ├── xiaomayi-web/             # Web 端后端（Java；static 仅零散 css/js/image）
│   └── xiaomayi-uniapp/          # uniapp 端后端（Java）
├── xiaomayi-services/            # cms / generator / quartz / system
└── xiaomayi-visual/xiaomayi-monitor/  # 监控
```

> `xiaomayi-web/src/main/resources/static/` 下只有 `css/ images/ js/` 中的零散静态资源，**不是可运行的 Vue 工程**，无法作为前端基座复用。

---

## 二、可参考的后端资源（用于对齐 RBAC 与接口风格）

`xiaomayi-admin` 的 Controller 清单体现了一套成熟的后台模块划分，可作为本项目 `/api/admin` 设计的**命名与分层参考**（不直接引入，因包名/技术细节不同）：

| 参考点 | XiaoMaYi 对应 | 本项目采用方式 |
|--------|---------------|----------------|
| 登录鉴权 | `LoginController` + JWT/redis | 用 **Sa-Token admin 域**（与本仓库现有 Sa-Token 一致），新建 `admin/auth/AdminAuthController` |
| 菜单/权限 | `MenuController` / RBAC | 新建 `admin_permission`(菜单/按钮/接口) + `@SaCheckPermission` |
| 角色 | `RoleController` | 新建 `AdminRoleController` |
| 字典/配置 | `DictController`/`ConfigController` | 新建 `SysConfigController` |
| 日志 | `LoginLogController`/`RequestLogAspect` | 新建 `AdminLoginLog` + `OperationLogAspect`（AOP） |
| 行政区划/部门等 | `CityController`/`DeptController` | 本期不引入（非命理业务必需） |

> 关键：本项目后端**沿用当前 `com.ydzz` 工程规范**（`Result<T>`、`/api/**`、Sa-Token、MyBatis-Plus），不照搬 XiaoMaYi 的代码，只借鉴其模块划分思想。

---

## 三、前端组件规划（按 XiaoMaYi 架构新建 admin-web）

由于无现成 Vue 源码，下表为**将要新建**的前端工程组件清单，遵循 XiaoMaYi 的技术选型与目录约定。每项含：用途、技术、复用位置。

### 3.1 技术选型（对齐 README 所述 XiaoMaYi 栈）
`Vue 3` + `TypeScript` + `Vite` + `Element-Plus` + `Vue-Router 4` + `Pinia` + `Axios` + `ECharts` + `Sass`。

### 3.2 工程目录（新建 `admin-web/`）
```
admin-web/
├── package.json / vite.config.ts / tsconfig.json
├── index.html
└── src/
    ├── main.ts                    # 入口：挂载 ElementPlus、router、pinia、权限指令
    ├── App.vue
    ├── api/                       # 接口封装（按模块拆分）
    │   ├── request.ts             # axios 实例：baseURL=/api、注入 Authorization、统一 Result 解包、401 跳登录
    │   ├── auth.ts user.ts order.ts dashboard.ts admin.ts role.ts permission.ts log.ts config.ts
    ├── router/                    # 路由：静态路由 + 基于权限的动态菜单
    │   └── index.ts
    ├── store/                     # Pinia：user(token/info/permissions)、app(菜单/折叠)、permission(动态路由)
    ├── layout/                    # 布局：侧边菜单 + 顶栏 + 标签页 + 内容区
    │   ├── index.vue Sidebar.vue Navbar.vue TagsView.vue
    ├── directives/
    │   └── permission.ts          # v-permission 按钮级权限指令
    ├── components/                # 通用组件（见 3.3）
    ├── views/                     # 页面（见功能分析 二）
    │   ├── login/  dashboard/  user/  order/  goods/  funcConfig/
    │   ├── content/(banner,notice) activity/ resource/ analytics/
    │   └── system/(admin,role,permission,config,loginLog,operationLog)
    └── utils/  styles/
```

### 3.3 通用组件清单（新建，对应需求「复用：Table/Form/Dialog/Chart...」）

| 组件 | 文件 | 用途 | 复用位置 |
|------|------|------|----------|
| 数据表格 | `components/ProTable.vue` | 封装 el-table：分页、加载、排序、列配置、选择 | 用户/订单/商品/日志等所有列表页 |
| 查询表单 | `components/SearchForm.vue` | 条件查询区（输入/下拉/日期范围/重置） | 所有列表页顶部 |
| 表单弹窗 | `components/FormDialog.vue` | el-dialog + el-form，新增/编辑通用 | 管理员/角色/Banner/公告等 |
| 分页 | `components/Pagination.vue` | el-pagination 封装（page/size 双向绑定） | 所有列表页 |
| 上传 | `components/Upload.vue` | el-upload 封装（图片/文件，回传 url） | Banner/资源/头像 |
| 图表 | `components/Chart.vue` | ECharts 封装（line/bar/pie，自适应） | 驾驶舱、各分析页 |
| 卡片指标 | `components/StatCard.vue` | 驾驶舱核心数据卡片 | 驾驶舱 |
| 权限指令 | `directives/permission.ts` | `v-permission="'user:disable'"` 控制按钮显隐 | 全局 |
| 树选择 | `components/PermTree.vue` | el-tree 权限/菜单分配 | 角色分配权限 |

### 3.4 鉴权与请求流程（新建，对齐现有后端）
1. 登录页提交 → `POST /api/admin/auth/login` → 拿 token 存 Pinia + localStorage。
2. `request.ts` 请求拦截器注入 `Authorization: token`；响应拦截器解包 `Result`（`code!==0` 报错，`401` 清登录态跳登录）。
3. 登录后 `GET /api/admin/auth/info` 拿权限码 + 菜单树 → 生成动态路由 + 渲染侧边菜单。
4. 按钮级权限由 `v-permission` 指令 + Pinia 权限码集合判断。

---

## 四、结论

- **可复用**：XiaoMaYi 的**架构思想与模块划分**（RBAC、菜单、日志切面、配置）作为设计参考。
- **不可复用**：无任何现成 Vue 组件/页面源码可直接拿来。
- **执行方案**：按上表新建 `admin-web` 前端工程，组件库自建但遵循 XiaoMaYi/Element-Plus 通用范式；后端沿用当前 `com.ydzz` 规范，不照搬 XiaoMaYi 代码。
