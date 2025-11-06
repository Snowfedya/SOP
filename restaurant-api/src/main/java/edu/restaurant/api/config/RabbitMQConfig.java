package edu.restaurant.api.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RESTAURANT_EXCHANGE = "restaurant-exchange";
    public static final String RESTAURANT_DLX = "restaurant-dlx";
    public static final String RESTAURANT_DLQ = "restaurant-dlq";

    public static final String BOOKING_CREATED_KEY = "booking.created";
    public static final String BOOKING_CONFIRMED_KEY = "booking.confirmed";
    public static final String BOOKING_CANCELLED_KEY = "booking.cancelled";
    public static final String TABLE_STATUS_CHANGED_KEY = "table.status.changed";

    // Topic Exchange for regular messages
    @Bean
    public TopicExchange restaurantExchange() {
        return new TopicExchange(RESTAURANT_EXCHANGE, true, false);
    }

    // Topic Exchange for dead letters
    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(RESTAURANT_DLX, true, false);
    }

    // Dead Letter Queue
    @Bean
    public Queue deadLetterQueue() {
        return new Queue(RESTAURANT_DLQ, true);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
            .to(deadLetterExchange())
            .with("#");
    }
}
