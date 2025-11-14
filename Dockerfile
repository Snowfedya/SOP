# Этап 1: Сборка
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Копируем весь проект
COPY . .

# Собираем все модули
RUN mvn clean package -DskipTests

# Этап 2: Runtime
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Аргумент для указания, какой сервис запускать
ARG SERVICE_NAME

# Копируем собранный JAR
COPY --from=builder /build/${SERVICE_NAME}/target/${SERVICE_NAME}-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
