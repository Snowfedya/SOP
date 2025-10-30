# Restaurant Booking System - Contract-First Architecture

Система бронирования столиков в ресторане на базе Spring Boot с подходом Contract-First.

## Требования

- **Java 17+** (обязательно!)
- **Maven 3.6+**
- **Git** (опционально)

## Быстрый старт

### 1. Установка Java 17

**Windows:**
```bash
# Скачайте и установите с
https://adoptium.net/temurin/releases/?version=17

# Проверьте установку
java -version
```

**Linux:**
```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install openjdk-17-jdk

# Fedora/RHEL
sudo dnf install java-17-openjdk-devel

# Проверьте установку  
java -version
```

**macOS:**
```bash
# Через Homebrew
brew install openjdk@17

# Проверьте установку
java -version
```

### 2. Сборка и запуск

```bash
# Перейдите в папку проекта
cd restaurant-booking-system

# Соберите контрактный модуль
cd restaurant-contract
mvn clean install

# Запустите API сервер
cd ../restaurant-api
mvn spring-boot:run
```

Сервер запустится на **http://localhost:8080**

## API Endpoints

### Swagger UI (документация API)
http://localhost:8080/swagger-ui.html

### GraphQL
- **GraphiQL UI**: http://localhost:8080/graphiql
- **GraphQL Endpoint**: http://localhost:8080/graphql

## Примеры использования

### REST API - Столики

**Получить все столики:**
```bash
curl http://localhost:8080/api/v1/tables
```

**Создать столик:**
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

**Получить доступные столики:**
```bash
curl "http://localhost:8080/api/v1/tables/available?dateTime=2025-11-15T19:00:00"
```

### REST API - Бронирования

**Создать бронирование:**
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

**Получить все бронирования:**
```bash
curl http://localhost:8080/api/v1/bookings
```

**Подтвердить бронирование:**
```bash
curl -X PATCH http://localhost:8080/api/v1/bookings/1/confirm
```

### GraphQL

Откройте GraphiQL UI: http://localhost:8080/graphiql

**Получить столики:**
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

**Создать бронирование:**
```graphql
mutation {
  createBooking(input: {
    guestName: "Мария Петрова"
    phoneNumber: "+79991234567"
    email: "maria@example.com"
    tableId: "1"
    bookingDateTime: "2025-11-15T19:00:00"
    numberOfGuests: 2
    specialRequests: "Тихое место"
  }) {
    id
    guestName
    status
    bookingDateTime
  }
}
```

**Получить доступные столики:**
```graphql
query {
  availableTables(
    dateTime: "2025-11-15T19:00:00"
    minCapacity: 2
    page: 0
    size: 10
  ) {
    content {
      id
      tableNumber
      capacity
      location
    }
  }
}
```

## Архитектура проекта

### Contract-First подход
Проект следует принципу Contract-First: сначала определяются контракты API, затем их реализация.

### Структура:

```
restaurant-booking-system/
├── restaurant-contract/          # Контрактный модуль (JAR)
│   ├── dto/                       # Data Transfer Objects
│   ├── endpoints/                 # API интерфейсы
│   ├── exception/                 # Исключения
│   └── graphql-client/schema.graphqls  # GraphQL схема
│
└── restaurant-api/                # Реализация API
    ├── controllers/               # REST контроллеры
    ├── services/                  # Бизнес-логика
    ├── assemblers/                # HATEOAS assemblers
    ├── graphql/                   # GraphQL data fetchers
    ├── storage/                   # In-memory хранилище
    └── exception/                 # Обработчики исключений
```

### Технологии

- **Spring Boot 3.3.0** - Основной framework
- **Spring HATEOAS** - RESTful Hypermedia
- **Netflix DGS 8.5.6** - GraphQL framework
- **SpringDoc OpenAPI 2.3.0** - Swagger документация
- **Maven** - Управление зависимостями

## Функциональность

### Управление столиками
- ✅ CRUD операции
- ✅ Фильтрация по статусу, вместимости, расположению
- ✅ Поиск доступных столиков на конкретное время
- ✅ Управление статусами (AVAILABLE, RESERVED, OCCUPIED, MAINTENANCE)

### Управление бронированиями
- ✅ CRUD операции
- ✅ Подтверждение/отмена бронирований
- ✅ Проверка конфликтов по времени
- ✅ Фильтрация по гостю, телефону, столику, статусу
- ✅ Статусы: PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW

### API
- ✅ REST API с HATEOAS
- ✅ GraphQL API
- ✅ Swagger UI документация
- ✅ Пагинация результатов
- ✅ Валидация данных
- ✅ Централизованная обработка ошибок

## Тестовые данные

При запуске автоматически создаются:

**Столики:**
- T001 - 4 места, У окна, зал А
- T002 - 2 места, В углу, зал А
- T003 - 6 мест, Центр, зал B
- VIP001 - 8 мест, VIP зона

**Бронирования:**
- Иван Петров - T001, на завтра 19:00 (CONFIRMED)
- Мария Сидорова - T002, через 2 дня 20:30 (PENDING)

## Разработка

### Изменение контрактов
1. Измените файлы в `restaurant-contract/`
2. Пересоберите: `mvn clean install`
3. Обновите JAR в `restaurant-api/lib/`
4. Перезапустите API

### Тестирование
```bash
# Запуск тестов
mvn test

# Запуск с покрытием
mvn test jacoco:report
```

## Проблемы и решения

**"records are not supported"**
- Убедитесь, что используете Java 17 или выше
- Проверьте: `java -version`

**"release version not supported"**
- Установите JDK 17 (не JRE)
- Установите JAVA_HOME правильно

**Maven компилирует с неправильной версией Java**
```bash
export JAVA_HOME=/path/to/jdk-17
mvn clean install
```

## Лицензия

MIT License

## Автор

Restaurant Booking System - Contract-First Demo Project
