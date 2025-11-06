package edu.restaurant.notificationservice.listener;

import edu.restaurant.events.booking.BookingCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@ActiveProfiles("test")
class BookingEventListenerTest {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BookingEventListener listener;

    @Test
    void shouldProcessBookingCreatedEvent() {
        BookingCreatedEvent event = new BookingCreatedEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                1L, "John Doe", "+79991234567", "john@example.com",
                1L, LocalDateTime.now().plusHours(2), 2
        );

        doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

        listener.handleBookingEvents(event, java.util.Collections.emptyMap());
    }
}
