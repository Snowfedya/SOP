package edu.restaurant.notificationservice.listener;

import edu.restaurant.events.booking.BookingConfirmedEvent;
import edu.restaurant.events.booking.BookingCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BookingEventListenerTest {

    @MockBean
    private RabbitTemplate rabbitTemplate;  // Mock RabbitTemplate

    @Autowired
    private BookingEventListener bookingEventListener;

    @Test
    void shouldProcessBookingCreatedEvent() {
        // Given
        BookingCreatedEvent event = new BookingCreatedEvent(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            1L, "John Doe", "+79991234567", "john@example.com",
            1L, LocalDateTime.now().plusHours(2), 2
        );

        Map<String, Object> headers = Map.of(
            "X-Correlation-ID", "test-correlation-123"
        );

        // When & Then - прямой вызов метода обработчика
        assertDoesNotThrow(() -> {
            bookingEventListener.handleBookingEvents(event, headers);
        });

        // Verify логирование
        // (можно добавить Logback test appender для проверки логов)
    }

    @Test
    void shouldProcessBookingConfirmedEvent() {
        // Given
        BookingConfirmedEvent event = new BookingConfirmedEvent(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            2L, LocalDateTime.now()
        );

        Map<String, Object> headers = Map.of("X-Correlation-ID", "test-correlation-456");

        // When & Then
        assertDoesNotThrow(() -> {
            bookingEventListener.handleBookingEvents(event, headers);
        });
    }

    @Test
    void shouldHandleProcessingException() {
        // Given - создаем событие, которое может вызвать исключение
        BookingCreatedEvent event = new BookingCreatedEvent(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            null, // null ID вызовет NPE
            "Error Test", "+79991234569", "error@example.com",
            1L, LocalDateTime.now().plusHours(1), 1
        );

        Map<String, Object> headers = Map.of();

        // When & Then - проверяем, что исключение правильно обрабатывается
        assertThrows(IllegalArgumentException.class, () -> {
            bookingEventListener.handleBookingEvents(event, headers);
        });
    }

    @Test
    void shouldHandleCorrelationIdCorrectly() {
        // Given
        String correlationId = "test-correlation-789";
        BookingCreatedEvent event = new BookingCreatedEvent(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            3L, "Jane Doe", "+79991234568", "jane@example.com",
            2L, LocalDateTime.now().plusHours(1), 3
        );

        Map<String, Object> headers = Map.of("X-Correlation-ID", correlationId);

        // When & Then
        assertDoesNotThrow(() -> {
            bookingEventListener.handleBookingEvents(event, headers);
        });

        // Можно добавить проверку MDC через custom matcher или spy
    }
}
