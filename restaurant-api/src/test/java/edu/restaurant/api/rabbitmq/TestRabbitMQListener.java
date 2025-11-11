package edu.restaurant.api.rabbitmq;

import java.util.concurrent.CountDownLatch;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import edu.restaurant.events.booking.BookingCreatedEvent;

@Component
public class TestRabbitMQListener {

    private CountDownLatch latch = new CountDownLatch(1);

    @RabbitListener(queues = "test-queue")
    public void receiveMessage(BookingCreatedEvent event) {
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
