# Restaurant Booking System - Contract-First API

Система бронирования столиков в ресторане, реализованная по подходу Contract-First с использованием Spring Boot, HATEOAS и GraphQL.

## Архитектура

Проект состоит из двух модулей:

### 1. restaurant-contract
Контрактный модуль, содержащий:
- DTO (Data Transfer Objects)
- API интерфейсы (Endpoints)
- Исключения
- GraphQL схема

### 2. restaurant-api  
Реализация API, включающая:
- Controllers (REST API)
- Services (бизнес-логика)
- Assemblers (HATEOAS)
- GraphQL Data Fetchers
- In-Memory Storage

## Технологии

- **Java 17**
- **Spring Boot 3.3.0**
- **Spring HATEOAS** - для RESTful Hypermedia
- **Netflix DGS** - GraphQL framework
- **SpringDoc OpenAPI** - документация API
- **Maven** - сборка проекта

## Запуск

### 1. Собрать контракт
```bash
cd restaurant-contract
mvn clean install
```

### 2. Запустить API
```bash
cd ../restaurant-api
mvn spring-boot:run
```

Приложение запустится на порту 8080.

## API Endpoints

### REST API
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

### GraphQL
- GraphQL endpoint: http://localhost:8080/graphql
- GraphiQL UI: http://localhost:8080/graphiql

## Основные функции

### Столики (Tables)
- Создание, чтение, обновление, удаление столиков
- Фильтрация по статусу, вместимости, расположению
- Поиск доступных столиков на определенное время

### Бронирования (Bookings)
- Создание, чтение, обновление, удаление бронирований
- Подтверждение/отмена бронирований
- Фильтрация по гостю, телефону, столику, статусу
- Проверка конфликтов бронирований

## Примеры запросов

### REST API

#### Создать столик
```bash
curl -X POST http://localhost:8080/api/v1/tables \
  -H "Content-Type: application/json" \
  -d '{
    "tableNumber": "T005",
    "capacity": 4,
    "location": "У окна",
    "status": "AVAILABLE"
  }'
```

#### Получить все столики
```bash
curl http://localhost:8080/api/v1/tables?page=0&size=10
```

#### Создать бронирование
```bash
curl -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "guestName": "Иван Иванов",
    "phoneNumber": "+79991234567",
    "email": "ivan@example.com",
    "tableId": 1,
    "bookingDateTime": "2025-11-15T19:00:00",
    "numberOfGuests": 4,
    "specialRequests": "У окна, пожалуйста"
  }'
```

### GraphQL

#### Получить столики
```graphql
query {
  tables(page: 0, size: 10) {
    content {
      id
      tableNumber
      capacity
      location
      status
    }
    totalElements
    totalPages
  }
}
```

#### Создать бронирование
```graphql
mutation {
  createBooking(input: {
    guestName: "Мария Петрова"
    phoneNumber: "+79991234567"
    email: "maria@example.com"
    tableId: "1"
    bookingDateTime: "2025-11-15T19:00:00"
    numberOfGuests: 2
  }) {
    id
    guestName
    status
    bookingDateTime
  }
}
```

## Структура проекта

```
restaurant-booking-system/
├── restaurant-contract/       # Контрактный модуль
│   ├── src/main/java/
│   │   └── edu/restaurant/contract/
│   │       ├── dto/          # DTO классы
│   │       ├── endpoints/     # API интерфейсы
│   │       └── exception/     # Исключения
│   └── src/main/resources/
│       └── graphql-client/    # GraphQL схема
│
└── restaurant-api/            # Реализация API
    ├── lib/                   # Локальный JAR контракта
    ├── src/main/java/
    │   └── edu/restaurant/api/
    │       ├── controllers/   # REST контроллеры
    │       ├── services/      # Бизнес-логика
    │       ├── assemblers/    # HATEOAS assemblers
    │       ├── graphql/       # GraphQL fetchers
    │       ├── storage/       # In-memory хранилище
    │       └── exception/     # Обработка исключений
    └── src/main/resources/
        ├── application.yml    # Конфигурация
        └── graphql/           # GraphQL схема
```

## Лицензия

MIT License
