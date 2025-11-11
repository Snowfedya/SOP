package edu.restaurant.auditservice.config;

import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "restaurant-exchange";
    public static final String QUEUE_NAME = "audit-queue";
    public static final String DLX_EXCHANGE_NAME = "dlx-exchange";
    public static final String DLQ_NAME = "audit-queue.dlq";
    public static final String DLQ_ROUTING_KEY = "dlq.audit";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("#");
    }

    @Bean
    public TopicExchange dlxExchange() {
        return new TopicExchange(DLX_EXCHANGE_NAME);
    }

    @Bean
    public Queue dlq() {
        return new Queue(DLQ_NAME);
    }

    @Bean
    public Binding dlqBinding(Queue dlq, TopicExchange dlxExchange) {
        return BindingBuilder.bind(dlq).to(dlxExchange).with(DLQ_ROUTING_KEY);
    }
}
