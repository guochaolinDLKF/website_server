# Swagger 接口文档使用说明

## 一、什么是 Swagger

Swagger 是一个**自动生成接口文档**的工具。你写完 Controller 代码后，不用再手写接口文档，Swagger 会自动扫描你的接口，生成一个网页版的文档。你可以在网页上直接看到所有接口、参数说明，甚至可以直接在网页上**在线调试**接口。

简单说：**写完代码 = 有了文档**。

## 二、怎么打开

启动项目后，浏览器访问：

```
http://localhost:8000/swagger-ui.html
```

你会看到一个网页，左边是接口列表，右边是接口详情。

---

## 三、在项目中的体现

Swagger 相关的代码有**两个地方**：

### 1. 配置文件（application-*.yml）

在 [application-local-dev.yml](file:///D:/work_space/website_server/server/src/main/resources/application-local-dev.yml#L149-L165) 中：

```yaml
springdoc:
  enabled: true                     # 是否开启 Swagger
  api-docs:
    enabled: true
    path: /v3/api-docs              # API 文档的 JSON 地址
  swagger-ui:
    enabled: true
    path: /swagger-ui.html          # Swagger 网页的访问路径
    tags-sorter: alpha              # 接口分组按字母排序
    operations-sorter: alpha        # 接口方法按字母排序
    disable-swagger-default-url: true
    defaultModelsExpandDepth: -1    # 不展开数据模型
    defaultModelExpandDepth: -1
    doc-expansion: none             # 默认折叠所有接口（点开才展开）
  packages-to-scan: com.ydzz.controller  # 扫描哪个包下的 Controller
  paths-to-match: /**                     # 扫描哪些路径
```

**核心参数解释**：

| 参数 | 作用 | 常用值 |
|---|---|---|
| `enabled` | 打开/关闭 Swagger | 生产环境设为 `false` |
| `packages-to-scan` | 扫哪个包里的 Controller | 换成你的包名即可 |
| `doc-expansion` | 接口默认展开还是折叠 | `none`(折叠) / `list`(展开) |
| `defaultModelsExpandDepth` | 数据模型展示深度 | `-1`(不展示) / `1`(展示一层) |

### 2. Java 配置类（SwaggerSecurityConfig）

在 [SwaggerSecurityConfig.java](file:///D:/work_space/website_server/server/src/main/java/com/ydzz/config/SwaggerSecurityConfig.java) 中，配置了：

**(1) 文档标题和说明**：

```java
.info(new Info()
    .title("你的项目名称 API 文档")
    .description("项目描述，支持 Markdown 格式")
    .version("1.0.0"))
```

**(2) Token 认证配置**：
配置了一个全局的 `Authorization` 请求头，这样你在 Swagger 网页上填一次 Token，所有需要登录的接口都会自动带上这个 Token。

```java
.addSecuritySchemes("Authorization", new SecurityScheme()
    .type(SecurityScheme.Type.HTTP)       // HTTP 类型的认证
    .scheme("bearer")                      // Bearer Token 格式
    .description("请输入 Token"))
```

---

## 四、怎么用 Swagger 测试接口

### 场景：测试"获取用户信息"接口

这是一个需要登录的接口，直接调用会返回 401 未授权。

**第 1 步：先登录拿 Token**

在 Swagger 网页上：
1. 找到 **"用户接口"** 分组
2. 点开 `POST /api/user/login`
3. 点击 **"Try it out"** 按钮
4. 输入手机号和验证码
5. 点击 **"Execute"** 执行

返回结果里会有一个 `token` 字段，复制它。

**第 2 步：配置 Token**

1. 点击页面右上角的 **"Authorize"** 🔒 按钮
2. 在弹出的框里粘贴 Token
3. 点击 **"Authorize"** → **"Close"**

**第 3 步：调用需要登录的接口**

现在点开 `GET /api/user/info`，点击 **"Execute"**，请求会自动带上 Token，返回用户信息。

### 场景：快速测试一个不需要登录的接口

比如测试登录接口（白名单接口）：
1. 找到 `POST /api/user/login`
2. 点击 **"Try it out"**
3. 填入参数
4. 点击 **"Execute"**

直接就能看到返回结果，因为登录接口不需要 Token。

---

## 五、给你的接口加上 Swagger 注解

Swagger 的文档不是凭空生成的——它读的是你代码里的**注解**。

### 基本的注解（必须加的）

```java
@RestController
@RequestMapping("/api/article")
@Tag(name = "文章管理", description = "文章的增删改查")   // ← 接口分组
public class ArticleController {

    @Operation(summary = "创建文章", description = "用户可以创建一篇新文章")  // ← 接口说明
    @PostMapping
    public Result<ArticleResponse> create(@RequestBody ArticleRequest request) {
        // ...
    }
}
```

加了这两个注解后，Swagger 网页上就会显示：
- 分组名：**文章管理**
- 接口名：**创建文章**
- 说明文字：用户可以创建一篇新文章

### 参数注解（让参数含义更清楚）

```java
// 请求体参数（POST/PUT）
@PostMapping
public Result<String> create(
    @RequestBody @Valid ArticleRequest request   // 自动显示 DTO 里的字段
) { ... }

// URL 路径参数
@GetMapping("/{id}")
public Result<Article> getById(
    @Parameter(description = "文章ID", example = "1")  // ← 参数说明
    @PathVariable Long id
) { ... }

// URL 查询参数
@GetMapping("/list")
public Result<List<Article>> list(
    @Parameter(description = "页码", example = "1")
    @RequestParam(defaultValue = "1") int page,

    @Parameter(description = "每页数量", example = "10")
    @RequestParam(defaultValue = "10") int size
) { ... }
```

### 返回值注解（让返回数据一目了然）

```java
@Operation(summary = "获取用户信息")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "获取成功"),
    @ApiResponse(responseCode = "401", description = "未登录")
})
@GetMapping("/info")
public Result<User> getInfo() { ... }
```

### 在 DTO 类上标注字段说明

```java
@Data
@Schema(description = "创建文章请求")
public class ArticleRequest {
    @Schema(description = "文章标题", example = "我的第一篇文章")
    @NotBlank
    private String title;

    @Schema(description = "文章内容", example = "这是正文...")
    private String content;
}
```

加了这些后，Swagger 网页上会显示每个字段叫什么、是什么意思、示例值是什么。

---

## 六、常用配置场景

### 场景 1：生产环境关闭 Swagger

改 `application-prod.yml`：

```yaml
springdoc:
  enabled: false
```

**原因**：生产环境暴露接口文档有安全风险，外部人员可以看到你有哪些接口。

### 场景 2：扫不到新的 Controller

检查 `packages-to-scan` 是否包含了你的 Controller 包：

```yaml
springdoc:
  packages-to-scan: com.ydzz.controller   # 改成你的包名
```

### 场景 3：修改 Swagger 标题

改 [SwaggerSecurityConfig.java](file:///D:/work_space/website_server/server/src/main/java/com/ydzz/config/SwaggerSecurityConfig.java) 第 25 行：

```java
.info(new Info()
    .title("你的项目名称 API 文档")       // ← 改这里
    .description("项目说明")             // ← 改这里
    .version("1.0.0"))
```

### 场景 4：不想要 Swagger 了

1. 把 `application.yml` 中所有环境的 `springdoc.enabled` 设为 `false`
2. 或者直接删除 `pom.xml` 中的 `springdoc-openapi-starter-webmvc-ui` 依赖

### 场景 5：指定哪些接口显示、哪些隐藏

```yaml
springdoc:
  paths-to-match: /api/**           # 只显示 /api/ 下的接口
  paths-to-exclude: /api/internal/** # 隐藏 /api/internal/ 下的接口
```

---

## 七、文件清单（和 Swagger 相关的所有文件）

| 文件 | 作用 |
|---|---|
| `pom.xml` 第 177-181 行 | 引入 springdoc 依赖 |
| `application-local-dev.yml` 第 149-165 行 | Swagger 开关、访问路径、扫描包 |
| `application-dev.yml` | 开发环境也开启 |
| `application-prod.yml` | 生产环境关闭 |
| [SwaggerSecurityConfig.java](file:///D:/work_space/website_server/server/src/main/java/com/ydzz/config/SwaggerSecurityConfig.java) | 文档标题、Token 认证配置 |
| [WebConfig.java](file:///D:/work_space/website_server/server/src/main/java/com/ydzz/config/WebConfig.java) 白名单 | `/swagger-ui/**` 等路径不需要登录 |

---

## 八、快速检查清单

Swagger 不显示时，按这个顺序排查：

- [ ] 项目启动成功了吗？看控制台有没有报错
- [ ] `springdoc.enabled` 是 `true` 吗？
- [ ] 访问的地址对吗？`http://localhost:8000/swagger-ui.html`
- [ ] `packages-to-scan` 包含你的 Controller 包名吗？
- [ ] 你的 Controller 上有 `@Tag` 和 `@Operation` 注解吗？
- [ ] WebConfig 白名单里有 `/swagger-ui/**` 吗？（否则访问会被拦截器拦掉）
