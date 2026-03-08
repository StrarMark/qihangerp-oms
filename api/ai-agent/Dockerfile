FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./target/ai-agent-0.1.0.jar ai-agent.jar

CMD ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "ai-agent.jar"]