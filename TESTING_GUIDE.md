# Testing Guide for Restaurant Booking System

## 1. Test Correlation ID
```bash
curl -H "X-Request-ID: test-123" http://localhost:8080/api/tables
# Expected: Response header contains "X-Request-ID: test-123"
# Expected: Logs show [test-123] pattern
```

## 2. Test GraphQL Genre Filter
```graphql
# In GraphiQL (http://localhost:8080/graphiql)
query {
tables(genre: "VIP", page: 0, size: 10) {
content {
id
genre
}
}
}
```

## 3. Test RabbitMQ Resilience
```bash
# Terminal 1: Start RabbitMQ
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# Terminal 2: Start audit-service
cd audit-service && mvn spring-boot:run

# Terminal 3: Start restaurant-api
cd restaurant-api && mvn spring-boot:run

# Terminal 4: Create booking with normal name (should succeed)
curl -X POST http://localhost:8080/api/v1/bookings \
-H "Content-Type: application/json" \
-d '{
"guestName": "Normal Guest",
"phoneNumber": "+79991234567",
"tableId": 1,
"bookingDateTime": "2025-11-15T19:00:00",
"numberOfGuests": 2
}'

# Terminal 5: Create booking with "CRASH" name (should trigger DLQ)
curl -X POST http://localhost:8080/api/v1/bookings \
-H "Content-Type: application/json" \
-d '{
"guestName": "CRASH",
"phoneNumber": "+79991234567",
"tableId": 1,
"bookingDateTime": "2025-11-15T20:00:00",
"numberOfGuests": 2
}'

# Check audit-service logs:
# - Normal message: "Audit logged for booking..."
# - CRASH message: "!!! Message in DLQ: ..."

# Check RabbitMQ UI (http://localhost:15672, guest/guest):
# - Navigate to Queues tab
# - Verify `audit-queue.dlq` has 1 message
```

## 4. Test Idempotency
```bash
# Stop audit-service mid-processing (Ctrl+C)
# Restart audit-service
# Check logs: Should show "Duplicate event received" for re-delivered messages
```
