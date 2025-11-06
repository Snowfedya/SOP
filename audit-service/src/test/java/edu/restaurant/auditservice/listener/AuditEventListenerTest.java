package edu.restaurant.auditservice.listener;

import edu.restaurant.events.booking.BookingCancelledEvent;
import edu.restaurant.events.booking.BookingConfirmedEvent;
import edu.restaurant.events.booking.BookingCreatedEvent;
import edu.restaurant.events.table.TableStatusChangedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
class AuditEventListenerTest {

    @Autowired
    private AuditEventListener auditEventListener;

    @Test
    void shouldAuditBookingCreatedEvent() {
        // Given
        BookingCreatedEvent event = new BookingCreatedEvent(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            1L, "John", "+79991234567", "john@example.com",
            1L, LocalDateTime.now().plusHours(1), 2
        );

        Map<String, Object> headers = Map.of("X-Correlation-ID", "audit-test-123");

        // When
        auditEventListener.handleAllEvents(event, headers);

        // Then - проверяем, что событие записано
        // Если есть доступ к audit storage, можно проверить запись
    }

    @Test
    void shouldAuditAllEventTypes() {
        // Given
        List<Object> events = Arrays.asList(
            new BookingCreatedEvent(UUID.randomUUID().toString(), LocalDateTime.now(),
                                   1L, "John", "+79991234567", "john@example.com",
                                   1L, LocalDateTime.now().plusHours(1), 2),
            new BookingConfirmedEvent(UUID.randomUUID().toString(), LocalDateTime.now(),
                                    2L, LocalDateTime.now()),
            new BookingCancelledEvent(UUID.randomUUID().toString(), LocalDateTime.now(),
                                    3L, "cancelled", LocalDateTime.now()),
            new TableStatusChangedEvent(UUID.randomUUID().toString(), LocalDateTime.now(),
                                      1L, "AVAILABLE", "RESERVED")
        );

        Map<String, Object> headers = Map.of("X-Correlation-ID", "multi-audit-test");

        // When & Then - проверяем, что все типы событий обрабатываются
        for (Object event : events) {
            assertDoesNotThrow(() -> {
                auditEventListener.handleAllEvents(event, headers);
            });
        }
    }
}
