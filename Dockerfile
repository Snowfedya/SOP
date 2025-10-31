# Этап 1: Сборка
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Копируем весь проект
COPY . .

# Собираем контрактный модуль
RUN cd restaurant-contract && mvn clean install -DskipTests

# Собираем API модуль
RUN cd restaurant-api && mvn clean package -DskipTests

# Этап 2: Runtime
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /build/restaurant-api/target/restaurant-api-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]