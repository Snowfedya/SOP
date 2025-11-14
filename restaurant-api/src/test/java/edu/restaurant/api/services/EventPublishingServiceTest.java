package edu.restaurant.api.services;

import edu.restaurant.api.config.RabbitMQConfig;
import edu.restaurant.contract.dto.BookingResponse;
import edu.restaurant.contract.dto.BookingStatus;
import edu.restaurant.events.booking.BookingConfirmedEvent;
import edu.restaurant.events.booking.BookingCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;

import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventPublishingServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EventPublishingService eventPublishingService;

    @Test
    void shouldPublishBookingCreatedEvent() {
        // Given
        BookingResponse booking = new BookingResponse(
            1L, "John Doe", "+79991234567", "john@example.com",
            1L, LocalDateTime.now().plusHours(2), 2, null,
            BookingStatus.PENDING, null
        );

        // When
        eventPublishingService.publishBookingCreated(booking);

        // Then - проверяем, что RabbitTemplate вызван с правильными параметрами
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConfig.RESTAURANT_EXCHANGE),
            eq(RabbitMQConfig.BOOKING_CREATED_KEY),
            argThat(event -> {
                if (event instanceof BookingCreatedEvent created) {
                    return created.getBookingId().equals(1L) &&
                           created.getGuestName().equals("John Doe");
                }
                return false;
            }),
            any(MessagePostProcessor.class)
        );
    }

    @Test
    void shouldPublishBookingConfirmedEvent() {
        // Given
        Long bookingId = 2L;

        // When
        eventPublishingService.publishBookingConfirmed(bookingId);

        // Then
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConfig.RESTAURANT_EXCHANGE),
            eq(RabbitMQConfig.BOOKING_CONFIRMED_KEY),
            argThat(event -> {
                if (event instanceof BookingConfirmedEvent confirmed) {
                    return confirmed.getBookingId().equals(2L);
                }
                return false;
            }),
            any(MessagePostProcessor.class)
        );
    }

    @Test
    void shouldAddCorrelationIdToMessage() {
        // Given
        MDC.put("correlationId", "test-correlation-999");
        BookingResponse booking = new BookingResponse(
            3L, "Test User", "+79991234567", "test@example.com",
            1L, LocalDateTime.now().plusHours(2), 2, null,
            BookingStatus.PENDING, null
        );

        try {
            // When
            eventPublishingService.publishBookingCreated(booking);

            // Then - Capture the MessagePostProcessor and verify it
            ArgumentCaptor<MessagePostProcessor> captor = ArgumentCaptor.forClass(MessagePostProcessor.class);
            verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.RESTAURANT_EXCHANGE),
                eq(RabbitMQConfig.BOOKING_CREATED_KEY),
                any(BookingCreatedEvent.class),
                captor.capture()
            );

            MessagePostProcessor processor = captor.getValue();
            MessageProperties props = new MessageProperties();
            Message message = new Message("".getBytes(), props);
            processor.postProcessMessage(message);

            assertEquals("test-correlation-999", props.getHeaders().get("X-Correlation-ID"));
        } finally {
            MDC.clear();
        }
    }
}
