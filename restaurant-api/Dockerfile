# Многоступенчатая сборка для оптимизации размера
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Копируем весь проект
COPY . .

# Собираем контрактный модуль
RUN cd restaurant-contract && mvn clean install -DskipTests

# Собираем API модуль
RUN cd restaurant-api && mvn clean package -DskipTests

# Runtime образ
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Копируем собранный JAR из builder
COPY --from=builder /build/restaurant-api/target/restaurant-api-*.jar app.jar

# GraphQL будет доступен на порту 8080
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
