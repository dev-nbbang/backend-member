package com.dev.nbbang.member.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQProducerConfig {
    private final String NBBANG_EXCHANGE = "nbbang.exchange";
    private final String MEMBER_LEAVE_ROUTING_KEY = "member.leave.route";
    private final String MEMBER_LEAVE_QUEUE = "member.leave.queue";

    @Bean
    public Queue queue() {
        return new Queue(MEMBER_LEAVE_QUEUE, true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(NBBANG_EXCHANGE);
    }

    @Bean
    public Binding bind(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(MEMBER_LEAVE_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());     // ObjectMapper 사용안하고 사용자 Dto 바로 Json 변환 (application/json)

        return rabbitTemplate;
    }
}
