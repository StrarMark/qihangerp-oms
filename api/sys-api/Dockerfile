FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./target/sys-api-2.12.0.jar sys-api.jar

CMD ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "sys-api.jar"]