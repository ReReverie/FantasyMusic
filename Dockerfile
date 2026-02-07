FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 复制 JAR 文件
COPY build/libs/*.jar fm.jar
# 复制配置文件
COPY application-prod.yml application-prod.yml
ENV TZ=Asia/Shanghai
EXPOSE 8080
# 启动命令，支持 JAVA_OPTS 环境变量
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar fm.jar"]