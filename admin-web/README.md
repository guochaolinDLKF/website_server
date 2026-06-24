# admin-web · 运营后台前端

基于 **Vue3 + Vite + Element Plus + Pinia + Vue Router + ECharts**，对接 `server`（Spring Boot 3）后端 `/api/admin/**` 接口，构成运营后台系统的前端工程。架构约定沿用 XiaoMaYi-EleVue（Vue3+TS+Vite+ElementPlus 体系，本工程采用 JS 实现以降低启动门槛）。

## 技术栈

| 类别 | 选型 |
|------|------|
| 框架 | Vue 3（`<script setup>`） |
| 构建 | Vite 5 |
| UI | Element Plus 2 + @element-plus/icons-vue |
| 状态 | Pinia |
| 路由 | Vue Router 4（history 模式） |
| 请求 | Axios（统一封装、Token 注入、Result 解包） |
| 图表 | ECharts 5 |

## 目录结构

```
admin-web/
├── index.html
├── vite.config.js          # 开发代理 /api -> http://localhost:8660
├── package.json
└── src/
    ├── main.js             # 入口：ElementPlus / Pinia / Router / 图标 / v-permission
    ├── api/                # 各业务接口模块（auth/dashboard/user/adminUser/role/permission/log/sysConfig）
    │   └── request.js      # axios 封装（注入 Token、解包 Result、401/403 处理）
    ├── router/index.js     # 路由表 + 登录守卫
    ├── store/              # user（token/信息/权限/菜单）、app（侧边栏）
    ├── directives/         # v-permission 按钮级权限指令
    ├── layout/             # 布局：Sidebar / Navbar / Breadcrumb（侧边栏由后端菜单树驱动）
    ├── components/         # ComingSoon 占位组件
    ├── utils/auth.js       # Token 本地存储
    ├── styles/index.css    # 全局样式
    └── views/              # 页面
        ├── login/ dashboard/ profile/ error/
        └── operation/      # user / order / content / analytics / system
```

## 开发运行

```bash
npm install
npm run dev      # 默认 http://localhost:9660 ，/api 代理到后端 8660
```

> 前置条件：后端 `server` 已在 `8660` 启动；website(operations) 库已执行 `server/src/main/resources/sql/admin_init.sql`。

## 生产构建与部署

本项目采用 **「开发分离、部署合一」**：

### 方式一（推荐）：单 jar 一体化部署 —— 前端打进后端
在 `server` 目录执行：

```bash
mvn clean package -Pwith-frontend -DskipTests
java -jar target/website_server-1.0.0.jar
```

`-Pwith-frontend` 会自动构建本前端（base 自动设为 `/admin/`）并打包进 jar 的 `static/admin/`，
由 Spring Boot 直接托管。**一个端口 8660 同时提供页面与接口**：

- 后台页面：`http://localhost:8660/admin/`
- 接口：`http://localhost:8660/api/admin/**`

> 不带 `-Pwith-frontend` 的 `mvn package` 仍只打后端，保持原有构建/部署不变。

### 方式二：独立部署（Nginx）
```bash
npm run build     # 默认仍输出 base=/admin/，如需根路径部署可自行调整 vite.config.js
npm run preview
```
将 `dist/` 交由 Nginx 托管，并把 `/api` 反向代理到后端；history 模式配置 `try_files $uri $uri/ /index.html`。

### 开发态（带 HMR，前后端分开跑）
- 根目录双击 `start-dev.bat` 一键拉起后端(8660)+前端(9660)；或手动 `npm run dev`。
- 开发访问：`http://localhost:9660/`（base 为 `/`，`/api` 已代理到 8660）。

## 登录与权限

- 默认超级管理员：**admin / Admin@123**（首次登录后请尽快修改）。
- 登录态：后端返回 `tokenValue` + `tokenName`，前端以该请求头名（默认 `Authorization`）携带 Token。
- 菜单：由后端 `/api/admin/auth/info` 返回的菜单树动态渲染，无需前端维护菜单。
- 按钮级权限：`v-permission="'user:export'"`，与后端 `@SaCheckPermission` 双重校验。

## 与后端的对接约定

- 统一响应：`{ code, message, data, timestamp }`，`code === 0` 为成功。
- 分页：`{ total, current, size, records }`。
- 鉴权失败：HTTP 401（跳转重新登录）/ 403（权限不足提示）。
