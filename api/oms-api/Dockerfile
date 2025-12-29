FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./target/oms-api-2.12.0.jar oms-api.jar

CMD ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "oms-api.jar"]