# 使用OpenJDK 21作为基础镜像
FROM openjdk:21-jdk

# 设置工作目录
WORKDIR /app

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建日志目录
RUN mkdir -p /home/website_server/logs

# 复制jar包
COPY website_server-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8660

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
