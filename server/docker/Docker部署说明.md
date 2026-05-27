# Docker 部署说明

## 部署前准备

### 1. 服务器要求
- 操作系统：Linux（推荐 Ubuntu 20.04+ 或 CentOS 7+）
- 已安装 Docker
- 内存：至少 2GB
- 磁盘：至少 10GB 可用空间

### 2. 配置修改

部署前需修改对应脚本中的服务器连接信息：

```batch
set SERVER_IP=YOUR_SERVER_IP        # 替换为您的服务器IP
set SERVER_PORT=22                   # SSH端口，默认22
set SERVER_USER=root                 # 替换为您的用户名
set SERVER_PASSWORD=YOUR_PASSWORD    # 替换为您的密码
```

## 部署步骤

### 方式一：简单部署（推荐）

直接双击对应的 bat 脚本即可完成构建 + 上传 + 部署：

| 脚本 | 环境 |
|---|---|
| `deploy-simple-dev.bat` | 开发环境 |
| `deploy-simple-prod.bat` | 生产环境 |
| `deploy-simple-arraignment.bat` | 提审环境 |
| `deploy-simple.bat` | 自定义环境（修改 SPRING_PROFILE 变量） |

脚本会自动完成：
1. 本地 `mvn clean package` 构建 jar 包
2. 上传 jar 和 Dockerfile 到服务器
3. 在服务器上构建 Docker 镜像
4. 停止旧容器 → 删除旧容器/镜像 → 启动新容器
5. 查看启动日志确认运行状态

### 方式二：expect 方式（需要 expect 工具）

如果 SSH 不支持密钥登录，密码中含特殊字符导致脚本无法交互输入，可用 expect 方式：

```bash
deploy-with-expect.bat
```

### 方式三：手动部署

```bash
# 1. 本地构建
cd ..
mvn clean package -DskipTests

# 2. 复制 jar 和 Dockerfile 到 docker 目录
copy target\website_server-1.0.0.jar docker\jar\
cd docker

# 3. 上传到服务器
scp jar\website_server-1.0.0.jar root@YOUR_SERVER:/home/docker/server/
scp Dockerfile root@YOUR_SERVER:/home/docker/server/

# 4. SSH 到服务器
ssh root@YOUR_SERVER

# 5. 在服务器上构建和运行
cd /home/docker/server
docker build -t server:latest .
docker run -d --name server -p 8660:8660 \
    -v /home/fortune-telling/logs:/home/fortune-telling/logs \
    --restart unless-stopped \
    -e SPRING_PROFILES_ACTIVE=prod \
    server:latest
```

## 容器配置

| 配置项 | 值 | 说明 |
|---|---|---|
| 容器名称 | `server-{env}` | 如 `server-prod`, `server-dev` |
| 镜像名称 | `server:{env}` | 如 `server:prod` |
| 端口映射 | `8660:8660` | 主机端口:容器端口 |
| 重启策略 | `unless-stopped` | 容器异常退出自动重启 |
| 环境变量 | `SPRING_PROFILES_ACTIVE` | 激活的 Spring profile |

## 监控和管理

### 查看容器状态
```bash
docker ps | grep server
```

### 查看容器日志
```bash
docker logs -f server-prod          # 实时日志
docker logs --tail 100 server-prod  # 最近100行
```

### 容器管理
```bash
docker stop server-prod       # 停止
docker start server-prod      # 启动
docker restart server-prod    # 重启
docker rm server-prod         # 删除容器
```

### 镜像管理
```bash
docker images | grep server
docker rmi server:prod        # 删除镜像
```

## 故障排查

### 1. 容器启动失败
```bash
# 查看启动日志
docker logs server-prod

# 检查端口是否被占用
netstat -tlnp | grep 8660
```

### 2. 数据库连接失败
- 检查 `application-{env}.yml` 中数据库地址是否正确
- 确认服务器能访问数据库（网络、防火墙）

### 3. Redis 连接失败
- 检查 `application-{env}.yml` 中 Redis 地址和密码是否正确
- 确认服务器能访问 Redis

### 4. 应用无法访问
- 检查防火墙是否开放了 8660 端口
- 确认端口映射是否正确：`docker port server-prod`
- 确认容器是否在运行：`docker ps`

## 注意事项

1. **数据持久化**：当前配置已将日志目录挂载（`-v /home/fortune-telling/logs`），如需持久化其他数据请添加对应挂载
2. **安全配置**：生产环境请修改配置文件中的数据库密码、Redis 密码
3. **备份策略**：建议定期备份数据库和配置文件
4. **多环境区分**：通过 `SPRING_PROFILES_ACTIVE` 环境变量切换（dev/prod/arraignment/local-dev）

## 访问地址

部署成功后：
- **应用**: http://YOUR_SERVER_IP:8660
- **Swagger 文档**: http://YOUR_SERVER_IP:8660/swagger-ui.html
- **API 文档 JSON**: http://YOUR_SERVER_IP:8660/v3/api-docs
