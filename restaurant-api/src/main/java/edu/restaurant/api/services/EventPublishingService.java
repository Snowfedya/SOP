package edu.restaurant.api.services;

import edu.restaurant.api.config.RabbitMQConfig;
import edu.restaurant.contract.dto.BookingResponse;
import edu.restaurant.events.DomainEvent;
import edu.restaurant.events.booking.BookingCancelledEvent;
import edu.restaurant.events.booking.BookingConfirmedEvent;
import edu.restaurant.events.booking.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventPublishingService {

    private final RabbitTemplate rabbitTemplate;

    public void publishBookingCreated(BookingResponse booking) {
        BookingCreatedEvent event = new BookingCreatedEvent(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            booking.id(),
            booking.guestName(),
            booking.phoneNumber(),
            booking.email(),
            booking.tableId(),
            booking.bookingDateTime(),
            booking.numberOfGuests()
        );

        publishEvent(RabbitMQConfig.BOOKING_CREATED_KEY, event);
        log.info("Published BookingCreatedEvent: id={}", event.getEventId());
    }

    public void publishBookingConfirmed(Long bookingId) {
        BookingConfirmedEvent event = new BookingConfirmedEvent(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            bookingId,
            LocalDateTime.now()
        );

        publishEvent(RabbitMQConfig.BOOKING_CONFIRMED_KEY, event);
        log.info("Published BookingConfirmedEvent: id={}", event.getEventId());
    }

    public void publishBookingCancelled(Long bookingId, String reason) {
        BookingCancelledEvent event = new BookingCancelledEvent(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            bookingId,
            reason,
            LocalDateTime.now()
        );

        publishEvent(RabbitMQConfig.BOOKING_CANCELLED_KEY, event);
        log.info("Published BookingCancelledEvent: id={}", event.getEventId());
    }

    private void publishEvent(String routingKey, DomainEvent event) {
        String correlationId = MDC.get("correlationId");

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.RESTAURANT_EXCHANGE,
            routingKey,
            event,
            message -> {
                if (correlationId != null) {
                    message.getMessageProperties().setHeader("X-Correlation-ID", correlationId);
                }
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            }
        );
    }
}
