# Руководство по демонстрации функционала

Проект успешно запущен и готов к использованию. Все сервисы работают в Docker-контейнерах.

## 1. Обзор запущенных сервисов

Вы можете просмотреть все запущенные контейнеры с помощью команды:

```bash
docker-compose ps
```

Вы увидите следующие сервисы:
- `restaurant-api`: Основной API-сервис.
- `notification-service`: Сервис для обработки уведомлений.
- `audit-service`: Сервис для аудита событий.
- `rabbitmq`: Брокер сообщений для асинхронного взаимодействия между сервисами.

## 2. Взаимодействие с API

### Получение всех столиков

```bash
curl http://localhost:8080/api/v1/tables
```

### Создание нового столика

```bash
curl -X POST http://localhost:8080/api/v1/tables \
  -H "Content-Type: application/json" \
  -d 
    "tableNumber": "T006",
    "capacity": 5,
    "location": "На веранде",
    "status": "AVAILABLE"
  
```

### Создание бронирования

```bash
curl -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d 
    "guestName": "Петр Петров",
    "phoneNumber": "+79997654321",
    "email": "petr@example.com",
    "tableId": 1,
    "bookingDateTime": "2025-12-20T20:00:00",
    "numberOfGuests": 2,
    "specialRequests": "Безглютеновые блюда"
  
```

## 3. Проверка асинхронного взаимодействия

После создания нового бронирования, `restaurant-api` отправляет событие в RabbitMQ. Сервисы `notification-service` и `audit-service` получают это событие и обрабатывают его.

Вы можете увидеть это в логах контейнеров.

### Логи `notification-service`

```bash
docker-compose logs notification-service
```
Вы должны увидеть сообщение о получении события создания бронирования.

### Логи `audit-service`

```bash
docker-compose logs audit-service
```
Вы также должны увидеть сообщение о получении и обработке события.

## 4. Доступ к Swagger UI и GraphiQL

- **Swagger UI** (документация REST API): [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **GraphiQL** (интерфейс для GraphQL): [http://localhost:8080/graphiql](http://localhost:8080/graphiql)

С помощью этих инструментов вы можете в интерактивном режиме исследовать все возможности API.
