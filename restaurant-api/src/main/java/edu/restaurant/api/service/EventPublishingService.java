package edu.restaurant.api.service;

import edu.restaurant.contract.dto.BookingResponse;
import edu.restaurant.events.booking.BookingCancelledEvent;
import edu.restaurant.events.booking.BookingConfirmedEvent;
import edu.restaurant.events.booking.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.MDC;
import org.springframework.amqp.core.MessagePostProcessor;


@Service
@RequiredArgsConstructor
public class EventPublishingService {

    private final RabbitTemplate rabbitTemplate;

    public void publishBookingCreated(BookingResponse booking) {
        BookingCreatedEvent event = new BookingCreatedEvent(
                java.util.UUID.randomUUID().toString(),
                java.time.LocalDateTime.now(),
                booking.getId(),
                booking.getGuestName(),
                booking.getPhoneNumber(),
                booking.getEmail(),
                booking.getTableId(),
                booking.getBookingDateTime(),
                booking.getNumberOfGuests()
        );
        rabbitTemplate.convertAndSend("restaurant-exchange", "booking.created", event, getCorrelationIdPostProcessor());
    }

    public void publishBookingConfirmed(Long bookingId) {
        BookingConfirmedEvent event = new BookingConfirmedEvent(
                java.util.UUID.randomUUID().toString(),
                java.time.LocalDateTime.now(),
                bookingId,
                java.time.LocalDateTime.now()
        );
        rabbitTemplate.convertAndSend("restaurant-exchange", "booking.confirmed", event, getCorrelationIdPostProcessor());
    }

    public void publishBookingCancelled(Long bookingId, String reason) {
        BookingCancelledEvent event = new BookingCancelledEvent(
                java.util.UUID.randomUUID().toString(),
                java.time.LocalDateTime.now(),
                bookingId,
                reason,
                java.time.LocalDateTime.now()
        );
        rabbitTemplate.convertAndSend("restaurant-exchange", "booking.cancelled", event, getCorrelationIdPostProcessor());
    }

    private MessagePostProcessor getCorrelationIdPostProcessor() {
        return message -> {
            String correlationId = MDC.get("correlationId");
            if (correlationId != null) {
                message.getMessageProperties().setHeader("X-Correlation-ID", correlationId);
            }
            return message;
        };
    }
}
