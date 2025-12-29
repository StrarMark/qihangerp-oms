FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./target/gateway-2.12.0.jar gateway.jar

CMD ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "gateway.jar"]