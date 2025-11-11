package edu.restaurant.auditservice.listener;

import com.rabbitmq.client.Channel;
import edu.restaurant.events.booking.BookingCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class BookingEventListener {
    private static final Logger log = LoggerFactory.getLogger(BookingEventListener.class);
    private static final String EXCHANGE_NAME = "restaurant-exchange";
    private static final String QUEUE_NAME = "audit-queue";
    private final Set<Long> processedBookingCreations = Collections.synchronizedSet(new HashSet<>());

    @RabbitListener(queues = QUEUE_NAME)
    public void handleBookingCreated(
            @Payload BookingCreatedEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {
        try {
            log.info("Received BookingCreatedEvent: {}", event);

            // Idempotency check
            if (!processedBookingCreations.add(event.getBookingId())) {
                log.warn("Duplicate event received for bookingId: {}", event.getBookingId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            // Simulate crash for testing DLQ
            if (event.getGuestName() != null && event.getGuestName().equalsIgnoreCase("CRASH")) {
                throw new RuntimeException("Simulating error for DLQ test");
            }

            // Business logic: Log to audit trail
            log.info("Audit logged for booking {} by {}", event.getBookingId(), event.getGuestName());

            // Manual ACK
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false); // requeue=false
        }
    }

    @RabbitListener(queues = "audit-queue.dlq")
    public void handleDlqMessages(Object failedMessage) {
        log.error("!!! Message in DLQ: {}", failedMessage);
        // Alert ops team, store for manual retry
    }
}
