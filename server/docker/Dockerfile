# 基础镜像：OpenJDK 21
FROM openjdk:21

# 工作目录
WORKDIR /app

# 接收 deploy-*.bat 通过 --build-arg 传入的构建参数
# 默认值与 deploy-config.bat 中的 PROJECT_ARTIFACT_ID / PROJECT_VERSION 保持一致
ARG BUILD_ARTIFACT_ID=website_server
ARG BUILD_VERSION=1.0.1

# 时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 应用日志目录（容器内路径与 deploy-config.bat 的 REMOTE_APP_LOG_DIR 保持一致）
RUN mkdir -p /home/${BUILD_ARTIFACT_ID}/logs

# 复制 jar 包（源文件名由 deploy 脚本通过 BUILD_ARTIFACT_ID / BUILD_VERSION 决定）
COPY ${BUILD_ARTIFACT_ID}-${BUILD_VERSION}.jar app.jar

# 暴露端口（与 deploy-config.bat 的 APP_PORT 保持一致）
EXPOSE 8660

# JVM 参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]






