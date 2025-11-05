# Restaurant Booking System - AI Agent Guide

## ОБЩИЕ ПРИНЦИПЫ
- Используй Contract-First подход: сначала контракты, потом реализация
- Следуй Spring Boot best practices
- Все изменения должны быть обратно совместимы
- Код должен быть протестирован на >85% покрытие

## МОДУЛЬНАЯ СТРУКТУРА
restaurant-booking-system/
├── restaurant-contract/     # Контрактный модуль (JAR)
├── restaurant-api/         # Основная реализация API
├── restaurant-events/      # Event контракты для RabbitMQ
├── notification-service/   # Микросервис уведомлений
└── audit-service/         # Микросервис аудита

## ТЕХНИЧЕСКИЙ СТЕК
- Java 17+
- Spring Boot 3.3.0
- Spring HATEOAS
- Netflix DGS (GraphQL)
- RabbitMQ
- SpringDoc OpenAPI
- Maven Multi-module

## ПРОВЕРКА КАЧЕСТВА
- Тесты: mvn test
- Покрытие: mvn jacoco:report
- Сборка: mvn clean install
- Запуск: mvn spring-boot:run

## ВАЖНЫЕ ФАЙЛЫ ДЛЯ ПОНИМАНИЯ
- README.md - основная документация
- restaurant-contract/src/main/java/dto/ - DTO модели
- restaurant-api/src/main/java/controllers/ - REST контроллеры
- restaurant-api/src/main/resources/graphql/ - GraphQL схемы
