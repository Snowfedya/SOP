package edu.restaurant.notificationservice.listener;

import edu.restaurant.events.booking.BookingCancelledEvent;
import edu.restaurant.events.booking.BookingConfirmedEvent;
import edu.restaurant.events.booking.BookingCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class BookingEventListener {

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = "notification-queue", durable = "true",
                      arguments = {
                          @Argument(name = "x-dead-letter-exchange", value = "restaurant-dlx"),
                          @Argument(name = "x-dead-letter-routing-key", value = "booking.failed")
                      }),
        exchange = @Exchange(name = "restaurant-exchange", type = "topic"),
        key = "booking.*"
    ))
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void handleBookingEvents(Object event, @Header Map<String, ?> headers) {
        Object correlationIdObj = headers.get("X-Correlation-ID");
        String correlationId = correlationIdObj != null ? correlationIdObj.toString() : "unknown";
        MDC.put("correlationId", correlationId);

        try {
            if (event instanceof BookingCreatedEvent created) {
                if (created.getBookingId() == null) {
                    throw new IllegalArgumentException("Booking ID cannot be null for BookingCreatedEvent");
                }
                sendBookingConfirmationEmail(created);
            } else if (event instanceof BookingConfirmedEvent confirmed) {
                sendBookingConfirmedSMS(confirmed);
            } else if (event instanceof BookingCancelledEvent cancelled) {
                sendCancellationEmail(cancelled);
            } else {
                log.warn("Unknown event type: {}", event.getClass().getName());
            }
            log.info("Successfully processed event: {}", event.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Error processing event: {}, will retry", event.getClass().getSimpleName(), e);
            throw e; // Retry or send to DLQ
        } finally {
            MDC.clear();
        }
    }

    private void sendBookingConfirmationEmail(BookingCreatedEvent event) {
        log.info("[EMAIL] Sending confirmation to: {}, Booking ID: {}", event.getEmail(), event.getBookingId());
        // TODO: Integrate with email service
    }

    private void sendBookingConfirmedSMS(BookingConfirmedEvent event) {
        log.info("[SMS] Sending confirmation SMS for Booking ID: {}", event.getBookingId());
        // TODO: Integrate with SMS service
    }

    private void sendCancellationEmail(BookingCancelledEvent event) {
        log.info("[EMAIL] Sending cancellation for Booking ID: {}", event.getBookingId());
        // TODO: Integrate with email service
    }
}
