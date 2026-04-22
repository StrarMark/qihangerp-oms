FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./target/open-api-1.0.0.jar open-api.jar

CMD ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "open-api.jar"]