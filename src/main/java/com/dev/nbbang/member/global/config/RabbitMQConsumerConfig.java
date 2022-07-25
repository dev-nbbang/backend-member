package com.dev.nbbang.member.global.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConsumerConfig {
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory listenerFactory(ConnectionFactory connectionFactory) {
        final SimpleRabbitListenerContainerFactory listenerFactory = new SimpleRabbitListenerContainerFactory();
        listenerFactory.setConnectionFactory(connectionFactory);
        listenerFactory.setMessageConverter(new Jackson2JsonMessageConverter());

        return listenerFactory;
    }
}
