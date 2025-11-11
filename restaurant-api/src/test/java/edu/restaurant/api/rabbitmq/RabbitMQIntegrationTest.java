package edu.restaurant.api.rabbitmq;

import edu.restaurant.api.config.RabbitMQConfig;
import edu.restaurant.events.booking.BookingCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
@Testcontainers
class RabbitMQIntegrationTest {

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3-management");

    @DynamicPropertySource
    static void configureRabbitMQ(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Queue testQueue() {
            return new Queue("test-queue", false);
        }

        @Bean
        public TopicExchange testExchange() {
            return new TopicExchange(RabbitMQConfig.EXCHANGE_NAME);
        }

        @Bean
        public Binding testBinding(Queue testQueue, TopicExchange testExchange) {
            return BindingBuilder.bind(testQueue).to(testExchange).with(RabbitMQConfig.ROUTING_KEY_BOOKING_CREATED);
        }
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TestRabbitMQListener listener;

    @Test
    void shouldPublishBookingCreatedEvent() throws InterruptedException {
        BookingCreatedEvent event = new BookingCreatedEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                1L,
                "Test Guest",
                "+1234567890",
                "test@guest.com",
                1L,
                LocalDateTime.now().plusDays(1),
                2
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_BOOKING_CREATED,
                event
        );

        assertTrue(listener.getLatch().await(5, TimeUnit.SECONDS));
    }
}
